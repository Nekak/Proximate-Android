package com.neklien.proximatetestandroid.helpers.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.neklien.proximatetestandroid.helpers.database.User;

import java.util.ArrayList;

/**
 * Created by nekak on 07/04/18.
 */

public class ServerResponse {
    @SerializedName("success")
    @Expose
    private Boolean isSuccess;

    @SerializedName("error")
    @Expose
    private Boolean isError;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("data")
    @Expose
    private ArrayList<User> arrUsers;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public Boolean getError() {
        return isError;
    }

    public void setError(Boolean error) {
        isError = error;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ArrayList<User> getArrUsers() {
        return arrUsers;
    }

    public void setArrUsers(ArrayList<User> arrUsers) {
        this.arrUsers = arrUsers;
    }
}
