package io.brickhack.mobile;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Wristband extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    Button scanButton, historyButton;
    Boolean readyToScan = false;

    int VIB_SCAN_SUCCESS = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wristband);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        historyButton = findViewById(R.id.button_history);

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent theIntent = new Intent(Wristband.this, History.class);
                startActivity(theIntent);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent Intent){

        // Give the user haptic feedback after each success/failed scan
        if(readyToScan){
            Toast.makeText(this, "Scanned tag", Toast.LENGTH_LONG).show();
            vibrate(VIB_SCAN_SUCCESS);
        }else{
            Toast.makeText(this, "No tag selected", Toast.LENGTH_LONG).show();
        }

        // TODO Connect to backend here

        super.onNewIntent(Intent);
    }

    protected void vibrate(int milliseconds){
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { // Need to support different versions of Android here
                vibrator.vibrate(milliseconds);
            } else {
                vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }
    }

    @Override
    protected void onResume(){
        // This will prevent other apps from opening when reading a tag
        Intent intent = new Intent(this, Wristband.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};

        if(nfcAdapter != null && nfcAdapter.isEnabled()){
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }else{
            Toast.makeText(this, "NFC not enabled on this device", Toast.LENGTH_LONG).show();
        }

        super.onResume();
    }

    @Override
    protected void onPause(){
        // Give control back to OS
        if(nfcAdapter != null && nfcAdapter.isEnabled()) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }
}
