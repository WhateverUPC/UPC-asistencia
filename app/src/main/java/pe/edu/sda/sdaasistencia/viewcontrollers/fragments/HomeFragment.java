package pe.edu.sda.sdaasistencia.viewcontrollers.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pe.edu.sda.sdaasistencia.R;
import pe.edu.sda.sdaasistencia.interfaces.APIService;
import pe.edu.sda.sdaasistencia.model.Class;
import pe.edu.sda.sdaasistencia.model.ClassesResponse;
import pe.edu.sda.sdaasistencia.viewcontrollers.activities.LoginActivity;
import pe.edu.sda.sdaasistencia.viewcontrollers.adapters.ClassesAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import static pe.edu.sda.sdaasistencia.util.SharedPreferencesManager.CleanSharedPreferences;
import static pe.edu.sda.sdaasistencia.util.SharedPreferencesManager.GetStringSharedPreference;
import static pe.edu.sda.sdaasistencia.util.SharedPreferencesManager.AddStringSharedPreference;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private TextView nombreCortoTextView;
    private RecyclerView classesRecyclerView;
    private RecyclerView.LayoutManager classesLayoutManager;
    private ClassesAdapter classesAdapter;
    private Button logoutButton;
    private Retrofit retrofit;
    private APIService apiService;
    private List<Class> classes;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        nombreCortoTextView = rootView.findViewById(R.id.nombreCortoTextView);
        logoutButton = rootView.findViewById(R.id.logoutButton);

        classes = new ArrayList<>();
        classesRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_classes);
        classesAdapter = new ClassesAdapter(classes);
        classesLayoutManager = new GridLayoutManager(rootView.getContext(), 1);
        classesRecyclerView.setAdapter(classesAdapter);
        classesRecyclerView.setLayoutManager(classesLayoutManager);


        retrofit = new Retrofit.Builder().baseUrl("http://45.55.189.9/webservices/sda/asistenciasda/").addConverterFactory(GsonConverterFactory.create()).build();
        apiService = retrofit.create(APIService.class);

        nombreCortoTextView.setText(GetStringSharedPreference(getActivity().getApplicationContext(), "userShortName"));

        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("SALIR")
                        .setMessage("¿Estás seguro de salir?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                                CleanSharedPreferences(getActivity().getApplicationContext());
                                startActivity(loginIntent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });

        updateData();
        return rootView;
    }

    private void updateData(){
        Call<ClassesResponse> classesCall = apiService.getClasses(GetStringSharedPreference(getActivity().getApplicationContext(), "userIdEmpl"));
        classesCall.enqueue(new Callback<ClassesResponse>() {
            @Override
            public void onResponse(Call<ClassesResponse> call, Response<ClassesResponse> response) {
                if(response.isSuccessful()){
                    ClassesResponse classesResponse = response.body();
                    List<Class> classes = classesResponse.getClasses();
                    classes.remove(0);
                    classesAdapter.setClasses(classes);
                    classesAdapter.notifyDataSetChanged();
                } else{
                    Toast.makeText(getActivity(),"Hubo un error al cargar las secciones, por favor vuelva en unos minutos", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ClassesResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
