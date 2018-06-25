package pe.edu.sda.sdaasistencia.interfaces;

import pe.edu.sda.sdaasistencia.model.AttendanceCloseResponse;
import pe.edu.sda.sdaasistencia.model.AttendanceResponse;
import pe.edu.sda.sdaasistencia.model.ClassesResponse;
import pe.edu.sda.sdaasistencia.model.EnrollmentResponse;
import pe.edu.sda.sdaasistencia.model.QrSyncResponse;
import pe.edu.sda.sdaasistencia.model.StudentsResponse;
import pe.edu.sda.sdaasistencia.model.UserResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {
    @GET("alumnos/alumno.php")
    Call<StudentsResponse> getStudents(@Query(value = "nombre") String partialName);

    @GET("alumnos/secciones.php")
    Call<ClassesResponse> getClasses(@Query(value = "employeeId") String employeeId);

    @FormUrlEncoded
    @POST("qr/sync.php")
    Call<QrSyncResponse> syncQr(@Field("idalumno") String studentId, @Field("codigoqr") String qrCode);

    @GET("login/authentication.php")
    Call<UserResponse> validateUser(@Query(value = "username") String username, @Query(value="password") String password);

    @GET("alumnos/matricula.php")
    Call<EnrollmentResponse> getEnrollment(@Query(value = "mtri_qr") String enrollmentQrCode);

    @GET("asistencia/cerrar.php")
    Call<AttendanceCloseResponse> closeAttendance(@Query(value = "classCode") String classCode, @Query(value = "auxId") String auxId, @Query(value = "closeTime") String closeTime);

    @FormUrlEncoded
    @POST("asistencia/registrar.php")
    Call<AttendanceResponse> addAttendance(@Field("id_mtri") String enrollmentId, @Field("asis_fecha") String attendanceDate, @Field("asis_hentrada") String attendanceInHour,
                                           @Field("asis_hsalida") String attendanceOutHour, @Field("asis_ctlgasis") String attendanceCatalog, @Field("audt_usuario") String asignedUser, @Field("id_area") String areaId);
}
