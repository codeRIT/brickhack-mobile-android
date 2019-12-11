package io.brickhack.mobile.Activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.brickhack.mobile.R;
import io.brickhack.mobile.Settings;
import io.brickhack.mobile.resourcesPage;
import net.openid.appauth.*;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    AuthorizationServiceConfiguration serviceConfiguration;
    public static final String LOG_TAG = "MainActivity";
    private static final String USED_INTENT = "USED_INTENT";
    private static final String SHARED_PREFERENCE = "BrickHack";
    private static final int RC_AUTH = 100;
    private static final String AUTH_STATE = "AUTH_STATE";
    private static final String SERVICE_CONFIGURATION = "SERVICE_CONFIGURATION";
    Settings settings = new Settings();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.id_logo_button);

        login.setOnClickListener(view -> {


            Toast.makeText(this, "Login clicked", Toast.LENGTH_SHORT).show();
            serviceConfiguration =
                    new AuthorizationServiceConfiguration(
                            Uri.parse("https://apply.brickhack.io/oauth/authorize"), // authorization endpoint
                            Uri.parse("https://apply.brickhack.io/oauth/token")); // token endpoint

            getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()
                    .putString(SERVICE_CONFIGURATION, serviceConfiguration.toJsonString())
                    .apply();


            Uri redirectUri = Uri.parse("brickhack://oauth/callback");
            AuthorizationRequest.Builder authRequestBuilder =
                    new AuthorizationRequest.Builder(
                            serviceConfiguration, // the authorization service configuration
                            "b0a484dfaf474fdfd43ad7867d3c70fe8d76195ee565f36a29677fdbd8a168d3",
                            ResponseTypeValues.CODE,
                            redirectUri); // The redirect URI to which the auth response is sent

            authRequestBuilder.setScope("Access-your-bricks");
            AuthorizationRequest request = authRequestBuilder.build();

            AuthorizationService authorizationService = new AuthorizationService(this);
            String action = "io.brickhack.mobile.appauth.HANDLE_AUTHORIZATION_RESPONSE";
            Intent postAuthorizationIntent = new Intent(action);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, request.hashCode(), postAuthorizationIntent, 0);
            authorizationService.performAuthorizationRequest(request, pendingIntent);

        });
    }

    private void handleAuthorizationResponse(@NonNull Intent intent) {

        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);
        final AuthState authState = new AuthState(response, error);

        if (response != null) {
            Log.i(LOG_TAG, String.format("Handled Authorization Response %s ", authState.toString()));
            AuthorizationService service = new AuthorizationService(this);
            service.performTokenRequest(response.createTokenExchangeRequest(), (tokenResponse, exception) -> {
                if (exception != null) {
                    Log.w(LOG_TAG, "Token Exchange failed", exception);
                } else {
                    if (tokenResponse != null) {
                        authState.update(tokenResponse, exception);
                        // TODO: 2019-10-27
                            persistAuthState(authState);
//                            // Let the user continue
                        Intent moveToDashboard = new Intent(LoginActivity.this, resourcesPage.class);
                        startActivity(moveToDashboard);
                    }
                }
            });
        }
    }

    private void persistAuthState(@NonNull AuthState authState) {
        getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()
                .putString(AUTH_STATE, authState.jsonSerializeString())
                .apply();
    }



    @Override
    protected void onResume() {
        super.onResume();
        checkIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIntent(getIntent());
    }

    private void checkIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null){
                switch (action) {
                    case "io.brickhack.mobile.appauth.HANDLE_AUTHORIZATION_RESPONSE":
                        if (!intent.hasExtra(USED_INTENT)) {
                            handleAuthorizationResponse(intent);
                            intent.putExtra(USED_INTENT, true);
                        }
                        break;
                    default:
                        // do nothing
                }
            }

        }

    }


    // TODO: 2019-10-27 i'm not sure if this works 
    @Override
    public void onBackPressed() {
        this.finishAffinity();
        finishAndRemoveTask();
    }
}
