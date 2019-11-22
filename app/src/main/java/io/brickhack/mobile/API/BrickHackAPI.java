package io.brickhack.mobile.API;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BrickHackAPI {

    @GET("/oauth/token/info")
    Call<JsonElement> getInfo();

    @GET("/manage/questionnaires/{id}.json")
    Call<JsonElement> getUser(@Path("id") String userid);


}
