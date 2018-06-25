package pe.edu.sda.sdaasistencia.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Class implements Serializable{
    @SerializedName("CODIGO")
    String classCode;

    @SerializedName("DESCRIPCION")
    String className;

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
