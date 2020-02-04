package io.brickhack.mobile.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import io.brickhack.mobile.R;

public class HomePage extends AppCompatActivity {

    boolean button1 = false;
    boolean button2 = false;
    boolean button3 = false;
    boolean button4 = false;
    boolean button5 = false;
    boolean button6 = false;
    boolean button7 = false;
    boolean button8 = false;
    boolean button9 = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        ImageButton button_event1 = (ImageButton) findViewById(R.id.event1_button);
        ImageButton button_event2 = (ImageButton) findViewById(R.id.event2_button);
        ImageButton button_event3 = (ImageButton) findViewById(R.id.event3_button);
        ImageButton button_event4 = (ImageButton) findViewById(R.id.event4_button);
        ImageButton button_event5 = (ImageButton) findViewById(R.id.event5_button);
        ImageButton button_event6 = (ImageButton) findViewById(R.id.event6_button);
        ImageButton button_event7 = (ImageButton) findViewById(R.id.event7_button);
        ImageButton button_event8 = (ImageButton) findViewById(R.id.event8_button);
        ImageButton button_event9 = (ImageButton) findViewById(R.id.event9_button);

        button_event1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if(!button1){
                    button1 = true;
                    button_event1.setImageResource(R.drawable.button_pressed);
                }
                else{
                    button1 = false;
                    button_event1.setImageResource(R.drawable.btn_star);
                }
            }
        });

        button_event2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if(!button2){
                    button2 = true;
                    button_event2.setImageResource(R.drawable.button_pressed);
                }
                else{
                    button2 = false;
                    button_event2.setImageResource(R.drawable.btn_star);
                }
            }
        });

        button_event3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if(!button3){
                    button3 = true;
                    button_event3.setImageResource(R.drawable.button_pressed);
                }
                else{
                    button3 = false;
                    button_event3.setImageResource(R.drawable.btn_star);
                }
            }
        });

        button_event4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if(!button4){
                    button4 = true;
                    button_event4.setImageResource(R.drawable.button_pressed);
                }
                else{
                    button4 = false;
                    button_event4.setImageResource(R.drawable.btn_star);
                }
            }
        });

        button_event5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if(!button5){
                    button5 = true;
                    button_event5.setImageResource(R.drawable.button_pressed);
                }
                else{
                    button5 = false;
                    button_event5.setImageResource(R.drawable.btn_star);
                }
            }
        });

        button_event6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if(!button6){
                    button6 = true;
                    button_event6.setImageResource(R.drawable.button_pressed);
                }
                else{
                    button6 = false;
                    button_event6.setImageResource(R.drawable.btn_star);
                }
            }
        });

        button_event7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if(!button7){
                    button7 = true;
                    button_event7.setImageResource(R.drawable.button_pressed);
                }
                else{
                    button7 = false;
                    button_event7.setImageResource(R.drawable.btn_star);
                }
            }
        });

        button_event8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if(!button8){
                    button8 = true;
                    button_event8.setImageResource(R.drawable.button_pressed);
                }
                else{
                    button8 = false;
                    button_event8.setImageResource(R.drawable.btn_star);
                }
            }
        });

        button_event9.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if(!button9){
                    button9 = true;
                    button_event9.setImageResource(R.drawable.button_pressed);
                }
                else{
                    button9 = false;
                    button_event9.setImageResource(R.drawable.btn_star);
                }
            }
        });

    }
}
