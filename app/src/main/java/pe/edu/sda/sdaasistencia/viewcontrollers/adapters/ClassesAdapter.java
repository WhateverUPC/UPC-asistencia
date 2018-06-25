package pe.edu.sda.sdaasistencia.viewcontrollers.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static pe.edu.sda.sdaasistencia.util.SharedPreferencesManager.GetStringSharedPreference;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pe.edu.sda.sdaasistencia.R;
import pe.edu.sda.sdaasistencia.interfaces.APIService;
import pe.edu.sda.sdaasistencia.model.AttendanceCloseResponse;
import pe.edu.sda.sdaasistencia.model.Class;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder> {
    private List<Class> classes;

    public List<Class> getClasses() {
        return classes;
    }

    public ClassesAdapter setClasses(List<Class> classes) {
        this.classes = classes;
        return this;
    }

    public ClassesAdapter() {

    }

    public ClassesAdapter(List<Class> classes) {
        this.classes = classes;
    }

    @NonNull
    @Override
    public ClassesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_class, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClassesAdapter.ViewHolder holder, int position) {
        holder.updateViews(classes.get(position));
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView classTitleTextView;
        private Button closeAttendanceBtn;
        private Retrofit retrofit;
        private APIService apiService;

        public ViewHolder(View view) {
            super(view);
            classTitleTextView = (TextView) view.findViewById(R.id.txt_class_title);
            closeAttendanceBtn = (Button) view.findViewById(R.id.btn_close_attendance);

            retrofit = new Retrofit.Builder().baseUrl("http://45.55.189.9/webservices/sda/asistenciasda/").addConverterFactory(GsonConverterFactory.create()).build();
            apiService = retrofit.create(APIService.class);
        }

        public void updateViews(final Class pclass) {
            classTitleTextView.setText(pclass.getClassName());

            closeAttendanceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View view = v;
                    Date now = new Date();
                    SimpleDateFormat sDF = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                    final String fechaAsistencia = sDF.format(now);

                    new AlertDialog.Builder(v.getContext())
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle(pclass.getClassName())
                            .setMessage("¿Cerrar la asistencia del día?")
                            .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Call<AttendanceCloseResponse> cerrarAsistencia = apiService.closeAttendance(pclass.getClassCode(),
                                                                                                                GetStringSharedPreference(view.getContext(), "userId"), fechaAsistencia);
                                    cerrarAsistencia.enqueue(new Callback<AttendanceCloseResponse>() {
                                        @Override
                                        public void onResponse(Call<AttendanceCloseResponse> call, Response<AttendanceCloseResponse> response) {
                                            if(response.isSuccessful()){
                                                AttendanceCloseResponse attendanceCloseResponse = response.body();
                                                Snackbar.make(view, attendanceCloseResponse.getMessage(), Snackbar.LENGTH_LONG).show();
                                            } else{
                                                Snackbar.make(view, "Hubo un problema, por favor intente nuevamente", Snackbar.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<AttendanceCloseResponse> call, Throwable t) {
                                            Snackbar.make(view, "Hubo un problema, por favor intente nuevamente", Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }
            });

        }
    }
}
