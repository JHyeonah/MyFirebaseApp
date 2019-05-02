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
            "Authorization: key=AAAA0qxo8nQ:APA91bEdUQrgLU-UiYUi7veXJFXPvKuj7VukwjjVUyEEPXnwJi4K2vC3-X92mlKrLAfZgo9a829waphnWFf16E081kI2GyMpt3-ksvPCPTGWTvfNDNYLXsE0JGQllmUITqafxGoQJ8iF"
    })
    @POST("/fcm/send")
    Call<Response> postFCMBody(@Body Push push);

}
