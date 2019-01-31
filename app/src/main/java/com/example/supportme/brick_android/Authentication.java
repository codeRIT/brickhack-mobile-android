package com.example.supportme.brick_android;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

public class Authentication extends AppCompatActivity {

    private LinearLayout layout;
    private BrickHackSettings brickHackSettings = new BrickHackSettings();
    private static final int RC_AUTH = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AuthorizationRequest.Builder authBuilder = generateAuthorizationRequestBuilder(generateServiceConfig());


        Button login = (Button) findViewById(R.id.id_logo_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent theIntent = new Intent(Authentication.this, DashBoard.class);
                System.out.println(brickHackSettings.getRedirectURI());
                doAuthorization(authBuilder.build());
            }
        });
    }

    private AuthorizationServiceConfiguration generateServiceConfig(){
        return new AuthorizationServiceConfiguration(
                        Uri.parse(this.brickHackSettings.getAuthorizationRoute()),
                        Uri.parse(this.brickHackSettings.getTokenRoute())
                );

    }

    private AuthorizationRequest.Builder generateAuthorizationRequestBuilder(
            AuthorizationServiceConfiguration authorizationServiceConfiguration){
        return new AuthorizationRequest.Builder(
                authorizationServiceConfiguration,
                this.brickHackSettings.getClientID(),
                ResponseTypeValues.CODE,
                this.brickHackSettings.getRedirectURI());
    }

    private void doAuthorization(AuthorizationRequest authRequest) {
        AuthorizationService authService = new AuthorizationService(this);
        Intent authIntent = authService.getAuthorizationRequestIntent(authRequest);
        startActivityForResult(authIntent, RC_AUTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_AUTH) {
            AuthorizationResponse response = AuthorizationResponse.fromIntent(data);
            AuthorizationException exception = AuthorizationException.fromIntent(data);
            // ... process the response or exception ...
        } else {
            // ...
        }
    }

    public void loginAnimation(View v){
        ObjectAnimator lg = ObjectAnimator.ofFloat(layout, "y", -0.001f);
        lg.setDuration(1000);
        lg.setInterpolator(new AccelerateDecelerateInterpolator());
        lg.start();
    }
}
