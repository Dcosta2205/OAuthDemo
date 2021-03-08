package com.xyz.oauthdemo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("/oauth/access_token")
    Call<AccessToken> getAccessToken(
            @Field("grant_type") String type,
            @Query("client_id") String client_id,
            @Query("redirect_uri") String redirect_url,
            @Query("client_secret") String client_secret,
            @Query("code") String code
    );

}
