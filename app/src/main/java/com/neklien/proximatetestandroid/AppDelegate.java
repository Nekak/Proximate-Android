package com.neklien.proximatetestandroid;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.neklien.proximatetestandroid.helpers.database.DBManager;
import com.neklien.proximatetestandroid.helpers.retrofit.RestApi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nekak on 07/04/18.
 */

public class AppDelegate extends Application {
    private RestApi restApi;

    private static AppDelegate appDelegateInstance;

    public static AppDelegate getInstance(){
        return appDelegateInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appDelegateInstance=this;

        new Thread() {
            public void run() {
                new DBManager(AppDelegate.this);
            };
        }.start();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        Gson gson=gsonBuilder.create();

        final OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30,TimeUnit.SECONDS)
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://serveless.proximateapps-services.com.mx/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        restApi = retrofit.create(RestApi.class);
    }

    /**
     * Provide pet rest api instance.
     *
     * @return petRestApi rest api
     */
    public RestApi getRestApi() {
        return restApi;
    }
}
