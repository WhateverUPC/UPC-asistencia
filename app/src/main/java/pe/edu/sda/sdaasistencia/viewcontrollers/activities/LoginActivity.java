package pe.edu.sda.sdaasistencia.viewcontrollers.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import pe.edu.sda.sdaasistencia.R;
import pe.edu.sda.sdaasistencia.interfaces.APIService;
import pe.edu.sda.sdaasistencia.model.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static pe.edu.sda.sdaasistencia.util.SharedPreferencesManager.GetStringSharedPreference;
import static pe.edu.sda.sdaasistencia.util.SharedPreferencesManager.AddStringSharedPreference;

public class LoginActivity extends AppCompatActivity {

    private ConstraintLayout loginConstraintLayout;
    private EditText userEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ImageView logoImageView;
    private Retrofit retrofit;
    private APIService apiService;
    private String username;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!GetStringSharedPreference(getApplicationContext(), "userId").equals("")) {
            Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainActivityIntent);
        }
        setContentView(R.layout.activity_login);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://45.55.189.9/webservices/sda/asistenciasda/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIService.class);

        //UI ELEMENTS
        logoImageView = findViewById(R.id.logoImageView);
        loginConstraintLayout = findViewById(R.id.loginConstraintLayout);
        userEditText = findViewById(R.id.userEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private boolean isPasswordValid() {
        return passwordEditText.getText().length() > 0;
    }

    private boolean isUserValid() {
        return userEditText.getText().length() > 0;
    }

    private boolean areCredentialsValid() {
        if (isPasswordValid() && isUserValid()) return true;
        return false;
    }

    private void attemptLogin() {
        if (isOnline()) {
            if (areCredentialsValid()) {
                username = userEditText.getText().toString();
                password = passwordEditText.getText().toString();

                Call<UserResponse> authCall = apiService.validateUser(username, password);
                authCall.enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        UserResponse userAuth = response.body();
                        if (!userAuth.getProfile().equals("")) {
                            Context ctx = getApplicationContext();

                            AddStringSharedPreference(ctx, "userShortName", userAuth.getShortName());
                            AddStringSharedPreference(ctx, "userFullName", userAuth.getFullName());
                            AddStringSharedPreference(ctx, "userEmail", userAuth.getEmail());
                            AddStringSharedPreference(ctx, "userId", userAuth.getUserId());
                            AddStringSharedPreference(ctx, "userIdEmpl", userAuth.getEmployeeId());

                            Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(mainActivityIntent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Solo los auxiliares tienen acceso", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {

                    }
                });
            } else {
                Toast.makeText(this, "Por favor, completa ambos campos", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "No tienes conexión a Internet", Toast.LENGTH_SHORT).show();
        }
    }
}
