package io.brickhack.mobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class resourcesPage extends AppCompatActivity {
    private Button SlackButton;
    private Button DevPostButton;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);
        SlackButton = findViewById(R.id.button1);
        DevPostButton = findViewById(R.id.button2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitleToolbar(toolbar, "  Resources");
        toolbar.setLogo(R.drawable.hack_logo_v6_icn);

        SlackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("slack://open?team=T0MRBMTHA"));
                startActivity(viewIntent);
            }
        });

        DevPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("https://brickhack.devpost.com/"));
                startActivity(viewIntent);
            }
        });
    }

    private void setTitleToolbar(Toolbar toolbar, String title){
        toolbar.setTitle(title);
    }

}
