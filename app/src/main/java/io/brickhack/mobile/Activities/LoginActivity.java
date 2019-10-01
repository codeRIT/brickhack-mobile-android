package io.brickhack.mobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.openid.appauth.AuthorizationServiceConfiguration;

import io.brickhack.mobile.R;

public class LoginActivity extends AppCompatActivity {

    private TextView username, password;
    private Button login;
    AuthorizationServiceConfiguration serviceConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.brickhack_username);
        password = findViewById(R.id.brickhack_password);
        login = findViewById(R.id.id_logo_button);

        login.setOnClickListener(view -> {
            Toast.makeText(this, "Username: " + username.getText(), Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
//            serviceConfiguration =
//                    new AuthorizationServiceConfiguration(
//                            Uri.parse("https://brickhack.io/oauth/authorize"), // authorization endpoint
//                            Uri.parse("https://brickhack.io/oauth/token")); // token endpoint
//
//            Uri redirectUri = Uri.parse("brickhack://oauth/callback");
//            AuthorizationRequest.Builder authRequestBuilder =
//                    new AuthorizationRequest.Builder(
//                            serviceConfiguration, // the authorization service configuration
//                            "",
//                            ResponseTypeValues.CODE,
//                            redirectUri); // The redirect URI to which the auth response is sent
//
//            authRequestBuilder.setScope("Access-your-bricks");
//            AuthorizationRequest request = authRequestBuilder.build();
//
//            AuthorizationService authorizationService = new AuthorizationService(this);
//            String action = "";
//            Intent postAuthorizationIntent = new Intent(action);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, request.hashCode(), postAuthorizationIntent, 0);
//            authorizationService.performAuthorizationRequest(request, pendingIntent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
        finishAndRemoveTask();
    }
}
