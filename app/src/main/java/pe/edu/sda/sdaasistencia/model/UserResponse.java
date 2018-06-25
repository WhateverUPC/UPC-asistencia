package pe.edu.sda.sdaasistencia.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserResponse implements Serializable{
    @SerializedName("ID_USER")
    private String userId;

    @SerializedName("USER_NOMBRECOMPLETO")
    private String fullName;

    @SerializedName("ID_EMPL")
    private String employeeId;

    @SerializedName("PERFIL")
    private String profile;

    @SerializedName("EMAIL")
    private String email;

    @SerializedName("ID_GRUPO")
    private String groupId;

    @SerializedName("NOMBRE_CORTO")
    private String shortName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
