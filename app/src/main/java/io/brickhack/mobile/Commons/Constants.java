package io.brickhack.mobile.Commons;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.util.ArrayList;

import io.brickhack.mobile.API.BrickHackAPI;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Constants {

    public static final String SHARED_PREFERENCE = "BrickHack";
    public static final String AUTH_STATE = "AUTH_STATE";
    public static final String SERVICE_CONFIGURATION = "SERVICE_CONFIGURATION";
    public static final String TEST_URL = "https://hm.baudouin.io";
    public static final String URL = "https://apply.brickhack.io";

    public static final String END_POINT = URL + "/oauth/authorize";
    public static final String TOKEN = URL + "/oauth/token";

    public static final String CLIENT_ID = "b0a484dfaf474fdfd43ad7867d3c70fe8d76195ee565f36a29677fdbd8a168d3";
    public static final String USED_INTENT = "USED_INTENT";

    public static final String GOOSHEETS = "https://sheets.googleapis.com/v4/spreadsheets/1eCEF8d4jkSMcY_nZue93roCCdkbyfiBG0G0XZ5KV9xI?fields=sheets" +
            "(data.rowData.values.userEnteredValue)&key=AIzaSyCt1OkeQmc0ygJLwTIb5ZMrWHoACf1v2yo";

    public static final String GOO = "https://sheets.googleapis.com/v4/spreadsheets/";


    public static final String TAG = "Constants";


    public static Retrofit RetrofitBuilder(String accessToken) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLenient();
        Gson gson = gsonBuilder.create();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        clientBuilder.addInterceptor(chain -> {
            Request newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();
            return chain.proceed(newRequest);
        });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(clientBuilder.build())
                .build();

        return retrofit;
    }


    public static ArrayList<String> RetroResult(Retrofit retrofit, String jsonid, String... jsonkeys) {
        ArrayList<String> results = new ArrayList<>();

        BrickHackAPI brickHackAPI = retrofit.create(BrickHackAPI.class);
        Call<JsonElement> call = brickHackAPI.getUser();
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.body());

                    assert response.body() != null;
                    for (int i = 0; i < jsonkeys.length; i++) {
                        JsonElement element = response.body().getAsJsonObject().get(jsonkeys[i]);
                        results.add(element.getAsString());

                    }

                    Log.i(TAG, "onResponse: " + results);

                } else {
//                    Toast.makeText(ProfileFragment.this, "Not success", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                System.out.println("ERROR: " + t.getMessage());
            }

        });

        return results;
    }

}
