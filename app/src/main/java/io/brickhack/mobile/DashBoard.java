package io.brickhack.mobile;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import net.openid.appauth.AuthState;

import org.json.JSONException;

import io.brickhack.mobile.R;


public class DashBoard extends AppCompatActivity {

    private static final String SHARED_PREFERENCES_NAME = "BrickHackPreference";
    private static final String AUTH_STATE = "AUTH_STATE";

    AuthState authState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        authState = restoreAuthState();

        Button wristband = findViewById(R.id.id_wristband);
        Button history = findViewById(R.id.id_history);
        Button attendees = findViewById(R.id.id_attendees);
        Button logout = findViewById(R.id.button_logout);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

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

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAuthState();
                Intent moveToAuthentication = new Intent(DashBoard.this, Authentication.class);
                startActivity(moveToAuthentication);
            }
        });
    }

    // Prevents user from returning to login screen without pressing the logout button
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Nullable
    private AuthState restoreAuthState() {
        String jsonString = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(AUTH_STATE, null);
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                return AuthState.jsonDeserialize(jsonString);
            } catch (JSONException jsonException) {
                // should never happen
            }
        }
        return null;
    }

    private void clearAuthState() {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(AUTH_STATE)
                .apply();
    }
}
