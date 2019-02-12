package io.brickhack.mobile;

import com.google.gson.JsonElement;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface BrickHackAPI {

    @GET("/manage/dashboard/todays_stats_data")
    Call<JsonElement> getStats();

    @GET("/manage/trackable_tags.json")
    Call<JsonElement> getTags();

    @POST("/manage/trackable_events.json")
    Call<JsonElement> submitScan(@Field("trackable-event") JSONObject trackableEvent);
}
