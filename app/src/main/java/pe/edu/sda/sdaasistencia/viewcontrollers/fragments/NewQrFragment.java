package pe.edu.sda.sdaasistencia.viewcontrollers.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pe.edu.sda.sdaasistencia.R;
import pe.edu.sda.sdaasistencia.interfaces.APIService;
import pe.edu.sda.sdaasistencia.model.Student;
import pe.edu.sda.sdaasistencia.model.StudentsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewQrFragment extends Fragment {

    private EditText syncNewQrTextView;
    private ArrayAdapter arrayAdapter;
    private Retrofit retrofit;
    private APIService apiService;
    private ListView studentsList;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<String> arrayList;

    public NewQrFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_qr, container, false);

        syncNewQrTextView = rootView.findViewById(R.id.filterStudentsEditText);
        retrofit = new Retrofit.Builder().baseUrl("http://45.55.189.9/webservices/sda/asistenciasda/").addConverterFactory(GsonConverterFactory.create()).build();
        apiService = retrofit.create(APIService.class);

        studentsList = rootView.findViewById(R.id.studentsFilterListView);
        arrayList = new ArrayList<String>();

        listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayList);
        studentsList.setAdapter(listAdapter);

        syncNewQrTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (syncNewQrTextView.getText().length() > 1) {
                    Call<StudentsResponse> alumnoCall = apiService.getStudents(syncNewQrTextView.getText().toString());


                    alumnoCall.enqueue(new Callback<StudentsResponse>() {
                        @Override
                        public void onResponse(Call<StudentsResponse> call, Response<StudentsResponse> response) {
                            if (response.isSuccessful()) {
                                arrayList.clear();
                                listAdapter.notifyDataSetChanged();

                                StudentsResponse alumnoResponse = response.body();
                                List<Student> alumnos = alumnoResponse.getStudents();
                                Iterator<Student> iterator = alumnos.iterator();
                                arrayList.clear();
                                while (iterator.hasNext()) {
                                    arrayList.add(iterator.next().getFullName());
                                    listAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Hubo un error, intente nuevamente", Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<StudentsResponse> call, Throwable t) {

                        }
                    });
                } else {
                    arrayList.clear();
                    listAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        studentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String alumno_id = arrayList.get(position).substring(1, 8);
                String alumno_nombre = arrayList.get(position).substring(9);
                NewQrCamFragment newQrCamFragment = new NewQrCamFragment();
                Bundle args = new Bundle();
                args.putString("idalumno", alumno_id);
                args.putString("nombrealumno", alumno_nombre);
                newQrCamFragment.setArguments(args);

                hideSoftKeyboard(getActivity());

                final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrameLayout, newQrCamFragment, "newQrCamFragment");
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

}
