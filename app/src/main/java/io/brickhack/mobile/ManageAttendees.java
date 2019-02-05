package io.brickhack.mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import io.brickhack.mobile.R;

public class ManageAttendees extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_attendees);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
    }
}
