package pe.edu.sda.sdaasistencia.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Student implements Serializable{
    @SerializedName("NOMBRES")
    private String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
