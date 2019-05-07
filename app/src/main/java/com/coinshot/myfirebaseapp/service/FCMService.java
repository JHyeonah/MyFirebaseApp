package com.coinshot.myfirebaseapp.service;

import com.coinshot.myfirebaseapp.model.Push;
import com.coinshot.myfirebaseapp.model.Response;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMService {
    @Headers({
            "Content-Type: application/json",
            "Authorization: key=**"
    })
    @POST("/fcm/send")
    Call<Response> postFCMBody(@Body Push push);

}
