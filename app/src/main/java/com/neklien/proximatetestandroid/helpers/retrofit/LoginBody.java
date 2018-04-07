package com.neklien.proximatetestandroid.helpers.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nekak on 07/04/18.
 */

public class LoginBody {
    @SerializedName("correo")
    @Expose
    private String email;

    @SerializedName("contrasenia")
    @Expose
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
