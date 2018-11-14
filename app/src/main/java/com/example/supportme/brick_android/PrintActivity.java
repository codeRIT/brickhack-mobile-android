package com.example.supportme.brick_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PrintActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        String email = getIntent().getStringExtra("email");
        String pw = getIntent().getStringExtra("password");

        TextView t = (TextView) findViewById(R.id.textView);
        TextView t2 = (TextView) findViewById(R.id.textView2);

        t.setText(email);
        t2.setText(pw);
    }
}
