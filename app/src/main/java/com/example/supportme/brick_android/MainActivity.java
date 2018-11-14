package com.example.supportme.brick_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final EditText email= (EditText) findViewById(R.id.id_email);
        final EditText password = (EditText)  findViewById(R.id.id_password);

        Button login = (Button) findViewById(R.id.id_logo_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent theIntent = new Intent(MainActivity.this, PrintActivity.class);
                theIntent.putExtra("email",  email.getText().toString());
                theIntent.putExtra("password", password.getText().toString());
                startActivity(theIntent);
            }
        });
    }
}
