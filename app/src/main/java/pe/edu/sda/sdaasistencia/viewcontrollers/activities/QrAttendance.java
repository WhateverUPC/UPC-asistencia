package pe.edu.sda.sdaasistencia.viewcontrollers.activities;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pe.edu.sda.sdaasistencia.R;
import pe.edu.sda.sdaasistencia.interfaces.APIService;
import pe.edu.sda.sdaasistencia.model.AttendanceResponse;
import pe.edu.sda.sdaasistencia.model.EnrollmentResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static pe.edu.sda.sdaasistencia.util.SharedPreferencesManager.GetStringSharedPreference;
import static pe.edu.sda.sdaasistencia.util.SharedPreferencesManager.AddStringSharedPreference;

public class QrAttendance extends AppCompatActivity {

    private DecoratedBarcodeView qrView;
    private BeepManager beepManager;
    private TextView txtMessage;
    private Retrofit retrofit;
    private APIService apiService;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null) {
                txtMessage.setText(R.string.error_qr_not_valid);
                return;
            }
            String stringResult = result.getText();

            qrView.setStatusText(result.getText());

            txtMessage.setText("");

            Call<EnrollmentResponse> mtriCall = apiService.getEnrollment(stringResult);

            mtriCall.enqueue(new Callback<EnrollmentResponse>() {

                @Override
                public void onResponse(Call<EnrollmentResponse> call, Response<EnrollmentResponse> response) {
                    if (response.isSuccessful()) {

                        EnrollmentResponse mtriResponse = response.body();

                        final String idMatri = mtriResponse.getEnrollmentId();
                        SimpleDateFormat sDF = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                        SimpleDateFormat sDF2 = new SimpleDateFormat("HH:mm");
                        Date now = new Date();

                        final String horaAsistencia = sDF2.format(now);

                        final String fechaAsistencia = sDF.format(now);

                        String horaSalida = "";
                        String ctlgAsis = "";

                        final String audtUsuario = GetStringSharedPreference(getApplicationContext(), "userId");
                        String idArea = "";

                        Call<AttendanceResponse> registrarAsistencia = apiService.addAttendance(idMatri, fechaAsistencia, horaAsistencia, horaSalida, ctlgAsis, audtUsuario, idArea);
                        registrarAsistencia.enqueue(new Callback<AttendanceResponse>() {
                            @Override
                            public void onResponse(Call<AttendanceResponse> call, Response<AttendanceResponse> response) {
                                AttendanceResponse attendanceResponse = response.body();
                                beepManager.setVibrateEnabled(true);
                                beepManager.setBeepEnabled(false);
                                beepManager.playBeepSoundAndVibrate();

                                txtMessage.setText(attendanceResponse.getMessage());
                                qrView.getStatusView().setText("");
                            }

                            @Override
                            public void onFailure(Call<AttendanceResponse> call, Throwable t) {
                                txtMessage.setText(t.getMessage());
                                qrView.getStatusView().setText("");
                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.error_try_again, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<EnrollmentResponse> call, Throwable t) {
                    txtMessage.setText(t.getMessage());
                }
            });
        }


        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        qrView = (DecoratedBarcodeView) findViewById(R.id.qr_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE);
        qrView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        qrView.decodeContinuous(callback);
        qrView.setStatusText("Coloque el QR de un alumno dentro del rectángulo para escanear");
        beepManager = new BeepManager(this);
        txtMessage = (TextView) findViewById(R.id.txt_message_result);

        retrofit = new Retrofit.Builder().baseUrl("http://45.55.189.9/webservices/sda/asistenciasda/").addConverterFactory(GsonConverterFactory.create()).build();
        apiService = retrofit.create(APIService.class);

    }

    @Override
    protected void onResume() {
        super.onResume();

        qrView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        qrView.pause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.startActivity(new Intent(this, MainActivity.class));
    }
}
