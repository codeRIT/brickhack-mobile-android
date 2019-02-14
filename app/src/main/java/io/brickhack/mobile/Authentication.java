package io.brickhack.mobile;

import android.animation.ObjectAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenResponse;

import static io.brickhack.mobile.BrickHackSettings.LOG_TAG;

public class Authentication extends AppCompatActivity {

    private static final String SHARED_PREFERENCES_NAME = "BrickHackPreference";
    private LinearLayout layout;
    private BrickHackSettings brickHackSettings = new BrickHackSettings();
    private static final int RC_AUTH = 100;
    private static final String AUTH_STATE = "AUTH_STATE";
    private static final String SERVICE_CONFIGURATION = "SERVICE_CONFIGURATION";
    private static final String USED_INTENT = "USED_INTENT";
    AuthorizationServiceConfiguration serviceConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button login = (Button) findViewById(R.id.id_logo_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceConfig =
                        new AuthorizationServiceConfiguration(
                                Uri.parse("https://staging.brickhack.io/oauth/authorize"), // authorization endpoint
                                Uri.parse("https://staging.brickhack.io/oauth/token")); // token endpoint

                getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                        .putString(SERVICE_CONFIGURATION, serviceConfig.toJsonString())
                        .commit();

                Uri redirectUri = Uri.parse("brickhack://oauth/callback");
                AuthorizationRequest.Builder authRequestBuilder =
                        new AuthorizationRequest.Builder(
                                serviceConfig, // the authorization service configuration
                                "a46ad487beade18ee2868fb9b6a6de69950f3a5bd7b2d5eb3fb62e35f53c120e", // the client ID, typically pre-registered and static
                                ResponseTypeValues.CODE, // the response_type value: we want a code
                                redirectUri); // the redirect URI to which the auth response is sent
                authRequestBuilder.setScopes("Access-your-bricks");
                AuthorizationRequest request = authRequestBuilder.build();

                AuthorizationService authorizationService = new AuthorizationService(v.getContext());
                String action = "io.brickhack.mobile.appauth.HANDLE_AUTHORIZATION_RESPONSE";
                Intent postAuthorizationIntent = new Intent(action);
                PendingIntent pendingIntent = PendingIntent.getActivity(v.getContext(), request.hashCode(), postAuthorizationIntent, 0);
                authorizationService.performAuthorizationRequest(request, pendingIntent);
            }
        });
    }

    private void handleAuthorizationResponse(@NonNull Intent intent) {

        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);
        final AuthState authState = new AuthState(response, error);

        if (response != null) {
            Log.i(LOG_TAG, String.format("Handled Authorization Response %s ", authState.toString()));
            AuthorizationService service = new AuthorizationService(this);
            service.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
                @Override
                public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                    if (exception != null) {
                        Log.w(LOG_TAG, "Token Exchange failed", exception);
                    } else {
                        if (tokenResponse != null) {
                            authState.update(tokenResponse, exception);
                            persistAuthState(authState);
                            // Let the user continue
                            Intent moveToDashboard = new Intent(Authentication.this, DashBoard.class);
                            startActivity(moveToDashboard);
                        }
                    }
                }
            });
        }
    }

    public void loginAnimation(View v){
        ObjectAnimator lg = ObjectAnimator.ofFloat(layout, "y", -0.001f);
        lg.setDuration(1000);
        lg.setInterpolator(new AccelerateDecelerateInterpolator());
        lg.start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        checkIntent(intent);
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

    private void persistAuthState(@NonNull AuthState authState) {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                .putString(AUTH_STATE, authState.jsonSerializeString())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIntent(getIntent());
    }

    // Prevents users from re-entering the app via back button
    @Override
    public void onBackPressed() {
        this.finishAffinity();
        finishAndRemoveTask();
    }
}
