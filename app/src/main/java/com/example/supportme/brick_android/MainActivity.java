package com.example.supportme.brick_android;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    private LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final EditText email= (EditText) findViewById(R.id.id_email);
        final EditText password = (EditText)  findViewById(R.id.id_password);
        layout = (LinearLayout) findViewById(R.id.id_layout);
        Button login = (Button) findViewById(R.id.id_logo_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent theIntent = new Intent(MainActivity.this, DashBoard.class);
                startActivity(theIntent);
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
