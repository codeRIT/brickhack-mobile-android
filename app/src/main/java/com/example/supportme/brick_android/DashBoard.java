package com.example.supportme.brick_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DashBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button wristband = (Button) findViewById(R.id.id_wristband);
        Button history = (Button) findViewById(R.id.id_history);
        Button attendees = (Button) findViewById(R.id.id_attendees);


        wristband.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent theIntent = new Intent(DashBoard.this, Wristband.class);
                startActivity(theIntent);
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent theIntent = new Intent(DashBoard.this, History.class);
                startActivity(theIntent);
            }
        });
        attendees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent theIntent = new Intent(DashBoard.this, ManageAttendees.class);
                startActivity(theIntent);
            }
        });
    }
}
