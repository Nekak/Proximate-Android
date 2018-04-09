package com.neklien.proximatetestandroid.helpers.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by nekak on 07/04/18.
 */

public interface RestApi {
    @POST("catalog/dev/webadmin/authentication/login")
    Call<ServerResponse> sendLogin(@Body LoginBody loginBody);

    @POST("catalog/dev/webadmin/users/getdatausersession")
    Call<ServerResponse> getProfile(@Header("Authorization") String token);
}
