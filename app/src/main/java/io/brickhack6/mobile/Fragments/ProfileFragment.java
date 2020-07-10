package io.brickhack6.mobile.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;

import org.json.JSONException;

import java.io.IOException;

import io.brickhack6.mobile.API.BrickHackAPI;
import io.brickhack6.mobile.Commons.Constants;
import io.brickhack6.mobile.R;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static io.brickhack6.mobile.Commons.Constants.AUTH_STATE;
import static io.brickhack6.mobile.Commons.Constants.SERVICE_CONFIGURATION;
import static io.brickhack6.mobile.Commons.Constants.SHARED_PREFERENCE;
import static io.brickhack6.mobile.Commons.Constants.URL;

public class ProfileFragment extends Fragment {

    private static final String TAG = "Profile";
    private AuthState authState;
    private AuthorizationServiceConfiguration serviceConfig;
    //UI
    private TextView user_first_name;
    private TextView user_last_name;
    private TextView user_major;
//    private TextView user_email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.e(TAG, "onCreateView: PROFILE");

        user_first_name = view.findViewById(R.id.user_first_name);
        user_last_name = view.findViewById(R.id.user_last_name);
        user_major = view.findViewById(R.id.user_major);
//        user_email = view.findViewById(R.id.user_email);

        authState = restoreAuthState();
        serviceConfig = restoreServiceConfig();

        networkstuff();
        return view;
    }

    @Nullable
    private AuthState restoreAuthState() {
        String jsonString = getContext().getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)
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

    //
    @Nullable
    private AuthorizationServiceConfiguration restoreServiceConfig() {
        String jsonString = getContext().getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)
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
        AuthorizationService service = new AuthorizationService(getContext());
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
                        Log.e(TAG, "onResponse: " + response);
                        if (response.isSuccessful()) {
                            JsonElement jsonuid = response.body().getAsJsonObject().get("resource_owner_id");
                            Log.e(TAG, "onResponse: " + jsonuid);
                            populateView(accessToken);

                        } else {
//                            Toast.makeText(ProfileFragment.this, "Not success", Toast.LENGTH_LONG).show();
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

    private void populateView(String accessToken) {
        Retrofit retrofit = Constants.RetrofitBuilder(accessToken);

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
                    JsonElement major = response.body().getAsJsonObject().get("major");

                    user_first_name.setText(first.getAsString());
                    user_last_name.setText(last.getAsString());
                    user_major.setText(major.getAsString());

                } else {
                    Log.i(TAG, "Error requesting data");
//                    Toast.makeText(ProfileFragment.this, "Not success", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                System.out.println("ERROR: " + t.getMessage());
            }

        });

    }
}
