package io.brickhack.mobile;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";
    AuthorizationService authorizationService;

    List<backendTag> tagList = new ArrayList<>();
    backendTag currentTag;

    AuthState authState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wristband);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        authorizationService = new AuthorizationService(this);
        Button logout = findViewById(R.id.button_logout);

        authState = restoreAuthState();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        backendTag noneTag = new backendTag();
        noneTag.id = 0;
        noneTag.name = "None";
        tagList.add(noneTag);

        getAvailableTags();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAuthState();
                Intent moveToAuthentication = new Intent(Wristband.this, Authentication.class);
                startActivity(moveToAuthentication);
            }
        });

    }

    private void clearAuthState() {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(AUTH_STATE)
                .apply();
    }

    @Override
    protected void onNewIntent(Intent intent){

        vibrate(VIB_SCAN_SUCCESS);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        }
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
            Toast.makeText(Wristband.this, "Listening for bands, hold phone near band!",
                    Toast.LENGTH_LONG).show();
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
                        .baseUrl("https://brickhack.io")
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
                                    backendTag newTag = new backendTag();
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
        currentTag = tagList.get(i);
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author Ralf Wondratschek
     *
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        System.out.println("Unsupported Encoding");
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            /*
             * See NFC forum specification for "Text Record Type Definition" at 3.2.1
             *
             * http://www.nfc-forum.org/specs/
             *
             * bit_7 defines encoding
             * bit_6 reserved for future use, must be 0
             * bit_5..0 length of IANA language code
             */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(final String result) {
            if (result != null) {
                authState.performActionWithFreshTokens(authorizationService, new AuthState.AuthStateAction() {
                    @Override
                    public void execute(@Nullable final String accessToken, @Nullable String idToken, @Nullable AuthorizationException ex) {

                        GsonBuilder gsonBuilder = new GsonBuilder();
                        gsonBuilder.setLenient();
                        Gson gson = gsonBuilder.create();

                        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

                        clientBuilder.addInterceptor(new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request newRequest  = chain.request().newBuilder()
                                        .addHeader("Authorization", "Bearer " + accessToken)
                                        .build();
                                return chain.proceed(newRequest);
                            }
                        });

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://brickhack.io")
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .client(clientBuilder.build())
                                .build();

                        BrickHackAPI brickHackAPI = retrofit.create(BrickHackAPI.class);

                        JSONObject scan = new JSONObject();
                        try {
                            scan.put("band_id", result);
                            scan.put("trackable_tag_id", currentTag.id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        postTagFormat postTagFormat = new postTagFormat(result, currentTag.id);


                        Call<JsonElement> call = brickHackAPI.submitScan(postTagFormat);

                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                                if(response.isSuccessful()){
                                    System.out.println("Success:" + response);
                                }
                                try {
                                    if (response.body().getAsJsonObject().has("errors")) {
                                        System.out.println("true");
                                        JsonElement errors = response.body().getAsJsonObject().get("errors");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Wristband.this);
                                        String error_message = errors.toString();
                                        System.out.println(errors.toString());
                                        builder.setMessage(errors.toString())
                                                .setTitle(R.string.error_dialog_title)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //clear dialog
                                            }
                                        });
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }else{
                                        Toast.makeText(Wristband.this, "Scan successful",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }catch (NullPointerException e){
                                    e.printStackTrace();
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
        }
    }
}

