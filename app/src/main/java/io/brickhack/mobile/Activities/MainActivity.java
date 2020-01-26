package io.brickhack.mobile.Activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationServiceConfiguration;

import org.json.JSONException;

import static io.brickhack.mobile.Commons.Constants.AUTH_STATE;
import static io.brickhack.mobile.Commons.Constants.SERVICE_CONFIGURATION;
import static io.brickhack.mobile.Commons.Constants.SHARED_PREFERENCE;

public class MainActivity extends AppCompatActivity {

    AuthState authState;
    AuthorizationServiceConfiguration serviceConfig;
    public static final String TAG = "MAIN";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        authState = restoreAuthState();
        serviceConfig = restoreServiceConfig();

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



}
