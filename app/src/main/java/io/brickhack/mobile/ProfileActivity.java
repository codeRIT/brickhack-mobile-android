package io.brickhack.mobile;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import io.brickhack.mobile.Commons.Constants;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static io.brickhack.mobile.Commons.Constants.AUTH_STATE;
import static io.brickhack.mobile.Commons.Constants.SERVICE_CONFIGURATION;
import static io.brickhack.mobile.Commons.Constants.SHARED_PREFERENCE;
import static io.brickhack.mobile.Commons.Constants.URL;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "Profile";
    AuthState authState;
    AuthorizationServiceConfiguration serviceConfig;
    //UI
    private TextView user_first_name;
    private TextView user_last_name;
    private TextView user_school;
    private TextView user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.username);
        user_first_name = findViewById(R.id.user_first_name);
        user_last_name = findViewById(R.id.user_last_name);
        user_school = findViewById(R.id.user_school);
        user_email = findViewById(R.id.user_email);

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
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + accessToken)
                                .build();
                        return chain.proceed(newRequest);
                    }
                });


                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(clientBuilder.build())
                        .build();

                BrickHackAPI brickHackAPI = retrofit.create(BrickHackAPI.class);
                Call<JsonElement> call = brickHackAPI.getInfo();
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        if (response.isSuccessful()) {
                            JsonElement jsonuid = response.body().getAsJsonObject().get("resource_owner_id");
                            populateView(jsonuid.toString(), accessToken);

                        } else {
                            Toast.makeText(ProfileActivity.this, "Not success", Toast.LENGTH_LONG).show();
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

    private void populateView(String userid, String accessToken) {
        Retrofit retrofit = Constants.RetrofitBuilder(accessToken);

        //Get user's name
//        ArrayList<String> result = Constants.RetroResult(retrofit, userid, "first_name", "last_name");

        BrickHackAPI brickHackAPI = retrofit.create(BrickHackAPI.class);
        Call<JsonElement> call = brickHackAPI.getUser(userid);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.body());

                    assert response.body() != null;
                    JsonElement first = response.body().getAsJsonObject().get("first_name");
                    JsonElement last = response.body().getAsJsonObject().get("last_name");
                    JsonElement sid = response.body().getAsJsonObject().get("school_id");

                    user_first_name.setText(first.getAsString());
                    user_last_name.setText(last.getAsString());

                    populateVieww(retrofit, sid.getAsString());

                } else {
                    Log.i(TAG, "Error requesting data");
                    Toast.makeText(ProfileActivity.this, "Not success", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                System.out.println("ERROR: " + t.getMessage());
            }

        });

    }

    private void populateVieww(Retrofit retrofit, String schoolid) {
        BrickHackAPI brickHackAPI = retrofit.create(BrickHackAPI.class);
        Call<JsonElement> call = brickHackAPI.getSchool(schoolid);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    JsonElement school = response.body().getAsJsonObject().get("name");
                    user_school.setText(school.getAsString());
                } else {
                    Log.i(TAG, "Error requesting data");
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

            }
        });
    }
}
