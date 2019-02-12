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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Wristband extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    NfcAdapter nfcAdapter;
    Button historyButton;
    Boolean readyToScan = false;
    private static final String SHARED_PREFERENCES_NAME = "BrickHackPreference";
    private static final String AUTH_STATE = "AUTH_STATE";
    int VIB_SCAN_SUCCESS = 500;

    class Tag {
        Integer id;
        String name;

        @NonNull
        @Override
        public String toString() {
            return name;
        }
    }
    List<Tag> tagList = new ArrayList<Tag>();

    AuthState authState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wristband);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        authState = restoreAuthState();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        historyButton = findViewById(R.id.button_history);

        Tag noneTag = new Tag();
        noneTag.id = 0;
        noneTag.name = "None";
        tagList.add(noneTag);

        getAvailableTags();

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

    private void getAvailableTags(){
        AuthorizationService authorizationService = new AuthorizationService(this);
        authState.performActionWithFreshTokens(authorizationService, new AuthState.AuthStateAction() {
            @Override
            public void execute(@Nullable final String accessToken, @Nullable String idToken, @Nullable AuthorizationException ex) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setLenient();
                Gson gson = gsonBuilder.create();

                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request newRequest  = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + accessToken)
                                .build();
                        return chain.proceed(newRequest);
                    }
                }).build();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://staging.brickhack.io")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();

                BrickHackAPI brickHackAPI = retrofit.create(BrickHackAPI.class);
                Call<JsonElement> call = brickHackAPI.getTags();

                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        if(response.isSuccessful()){
                            try{
                                JsonArray array = response.body().getAsJsonArray();
                                for(int i = 0; i < array.size(); i++){
                                    JsonElement jsonObject = array.get(i);
                                    Tag newTag = new Tag();
                                    newTag.id = jsonObject.getAsJsonObject().get("id").getAsInt();
                                    newTag.name = jsonObject.getAsJsonObject().get("name").getAsString();
                                    tagList.add(newTag);
                                }
                                populateSpinner();
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        System.out.println("ERROR: " + t.getMessage());
                    }
                });
            }
        });
    }

    public void populateSpinner(){
        Spinner tagSpinner = findViewById(R.id.id_tagSpinner);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, tagList);
        tagSpinner.setAdapter(arrayAdapter);
        tagSpinner.setOnItemSelectedListener(this);
        tagSpinner.setClickable(true);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        TextView currentTagLabel = findViewById(R.id.id_currentTagLabel);
        currentTagLabel.setText(adapterView.getItemAtPosition(i).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
