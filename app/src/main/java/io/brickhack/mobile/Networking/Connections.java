package io.brickhack.mobile.Networking;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationService;

import io.brickhack.mobile.API.BrickHackAPI;
import io.brickhack.mobile.Model.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Connections {
    private static final String TAG = "Connections";

    private AuthorizationService service;
    private AuthState authState;
    private Retrofit retrofit;
    private String accessToken;
    private Context context;
    private User user;

    public Connections(Context context,
                       AuthState authState, User user) {
        Log.e(TAG, "Connections: IN the connections class ");
        this.service = new AuthorizationService(context);
        this.authState = authState;
        this.context = context;
        this.user = user;
    }

    // TODO: 2019-10-30 Find a better way to
    // TODO: Observer, Singleton, Guice
    public void get_userinfo() {
        authState.performActionWithFreshTokens(service, (accessToken, idToken, ex) -> {
            gsonBuilder(accessToken);
            API_Calls();

        });
    }


    private void gsonBuilder(@Nullable String accessToken) {
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
                .baseUrl("https://apply.brickhack.io")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(clientBuilder.build())
                .build();

        this.retrofit = retrofit;
    }

    private void API_Calls() {
        BrickHackAPI brickHackAPI = retrofit.create(BrickHackAPI.class);
        Call<JsonElement> call = brickHackAPI.getInfo();
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {

                    assert response.body() != null;
                    JsonElement jsonuid = response.body().getAsJsonObject().get("resource_owner_id");
                    useruser(jsonuid.toString());

                } else {
                    System.err.println("ERROR communicating with the API");
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

            }
        });
    }


    // TODO: 2019-10-28  I have all the info, all I need is to pass them into user class
    private void useruser(String uid) {
        BrickHackAPI brickHackAPI = retrofit.create(BrickHackAPI.class);
        Call<JsonElement> call = brickHackAPI.getUser();
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.body());

                    assert response.body() != null;
                    JsonElement first = response.body().getAsJsonObject().get("first_name");
                    JsonElement last = response.body().getAsJsonObject().get("last_name");

                    //Sets user details
//                    user = new User(uid, first.toString(), last.toString());
                    user.setUid(uid);
                    user.setFirst_name(first.toString());
                    user.setLast_name(last.toString());

                } else {
                    Toast.makeText(context, "Not success", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                System.out.println("ERROR: " + t.getMessage());
            }
        });
    }

    public User getUser() {
        return user;
    }
}
