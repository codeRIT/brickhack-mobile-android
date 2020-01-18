package io.brickhack.mobile.Activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;

import org.json.JSONException;

import java.io.IOException;

import io.brickhack.mobile.API.BrickHackAPI;
import io.brickhack.mobile.R;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    AuthState authState;
    AuthorizationServiceConfiguration serviceConfig;
    private TextView username;
    public static final String TAG = "MAINNN";

    private static final String SHARED_PREFERENCE = "BrickHack";
    private static final String AUTH_STATE = "AUTH_STATE";
    private static final String SERVICE_CONFIGURATION = "SERVICE_CONFIGURATION";

    // TODO: 2019-11-08 Create a test user 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //username = findViewById(R.id.username);
        authState = restoreAuthState();
        serviceConfig = restoreServiceConfig();

        networkstuff();
    }

    @Nullable
    private AuthState restoreAuthState() {
        String jsonString = getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)
                .getString(AUTH_STATE, null);
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                return AuthState.jsonDeserialize(jsonString);
            } catch (JSONException jsonException) {
                // should never happen
            }
        }
        return null;
    }

    @Nullable
    private AuthorizationServiceConfiguration restoreServiceConfig() {
        String jsonString = getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)
                .getString(SERVICE_CONFIGURATION, null);
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                return AuthorizationServiceConfiguration.fromJson(jsonString);
            } catch (JSONException jsonException) {
                // should never happen
            }
        }
        return null;
    }


    private void networkstuff() {
        Toast.makeText(this, "In the network", Toast.LENGTH_SHORT).show();
        AuthorizationService service = new AuthorizationService(this);
        authState.performActionWithFreshTokens(service, new AuthState.AuthStateAction() {
            @Override
            public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException ex) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setLenient();
                Gson gson = gsonBuilder.create();

                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

                clientBuilder.addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request newRequest  = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + accessToken)
                                .build();
                        return chain.proceed(newRequest);
                    }
                });


                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://apply.brickhack.io")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(clientBuilder.build())
                        .build();

                BrickHackAPI brickHackAPI = retrofit.create(BrickHackAPI.class);
                Call<JsonElement> call = brickHackAPI.getInfo();
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: " + response.body());
                            JsonElement jsonuid = response.body().getAsJsonObject().get("resource_owner_id");
                            user_info(jsonuid.toString(), accessToken);

                        }else{
                            Toast.makeText(MainActivity.this, "Not success", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        System.out.println("ERROR: " + t.getMessage());
                    }
                });

            }
        });
    }


    // I mean this could work but it's bad. And i don't like it
    //Really bad code. I'm gonna fix it later
    private void user_info(String toString, String accessToken) {
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


        BrickHackAPI brickHackAPI = retrofit.create(BrickHackAPI.class);

        Call<JsonElement> call = brickHackAPI.getUser(toString);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onResponse: " + response.body());

                    assert response.body() != null;
                    JsonElement first = response.body().getAsJsonObject().get("first_name");
                    // JsonElement last = response.body().getAsJsonObject().get("last_name");
                    username.setText(first.toString());

                } else {
                    Toast.makeText(MainActivity.this, "Not success", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                System.out.println("ERROR: " + t.getMessage());
            }

        });

    }

}
