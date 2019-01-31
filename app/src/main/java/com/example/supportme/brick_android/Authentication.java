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

public class Authentication extends AppCompatActivity {

    private LinearLayout layout;
    private BrickHackSettings brickHackSettings = new BrickHackSettings();
    private static final int RC_AUTH = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login = (Button) findViewById(R.id.id_logo_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent theIntent = new Intent(Authentication.this, DashBoard.class);
                System.out.println(brickHackSettings.getRedirectURI());
            }
        });
    }

    public void loginAnimation(View v){
        ObjectAnimator lg = ObjectAnimator.ofFloat(layout, "y", -0.001f);
        lg.setDuration(1000);
        lg.setInterpolator(new AccelerateDecelerateInterpolator());
        lg.start();
    }
}
