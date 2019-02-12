package io.brickhack.mobile;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BrickHackAPI {

    @GET("/manage/dashboard/todays_stats_data")
    Call<JsonElement> getStats();

    @GET("/manage/trackable_tags.json")
    Call<JsonElement> getTags();
}
