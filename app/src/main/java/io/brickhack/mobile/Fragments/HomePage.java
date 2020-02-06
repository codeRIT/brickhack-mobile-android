package io.brickhack.mobile.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.brickhack.mobile.Adapters.RecyclerViewAdapter;
import io.brickhack.mobile.Model.Event;
import io.brickhack.mobile.R;

import static io.brickhack.mobile.Commons.Constants.GOOSHEETS;

public class HomePage extends Fragment {


    private static final String TAG = "HomePage";
    private final List<Event> eventList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        recyclerView = view.findViewById(R.id.fragment_recycler);
        return view;
    }

    private void scheduleRetrieve() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, GOOSHEETS, null, response -> {
            try {
                JSONArray sheet1 = response.getJSONArray("sheets");
                JSONObject data = sheet1.getJSONObject(0);
                JSONArray dataArray = data.getJSONArray("data");
                JSONObject rowData = dataArray.getJSONObject(0);
                JSONArray rowDataArray = rowData.getJSONArray("rowData");

                for (int i = 0; i < rowDataArray.length(); i++) {
                    JSONObject index = rowDataArray.getJSONObject(i);
                    JSONArray cvalues = index.getJSONArray("values");

//                    Log.e(TAG, "Values: " + cvalues);
                    Event regulatevent = new Event();
                    for (int j = 1; j < cvalues.length(); j++) {
                        if (j > 2) {
                            continue;
                        }

                        JSONObject ind = cvalues.getJSONObject(j);
                        // This is supposed to store the Days
                        if (i == 0 || i == 18) {
                            System.out.println("I have one value");
                            JSONObject userEnter = ind.getJSONObject("userEnteredValue");
                            String userString = userEnter.getString("stringValue");
//                            Log.e(TAG, "USER_STRING_ONE: " + userString);
//                            final Event event = new Event(userString, null, null, false);
//                            eventList.add(event);
                        } else {
                            if (ind.has("userEnteredValue")) {
//                                System.out.println("Count: " + j);
                                if (j == 1) {
                                    JSONObject userEnter = ind.getJSONObject("userEnteredValue");
                                    String userString = userEnter.getString("stringValue");
                                    regulatevent.setTime(userString);
//                                    Log.e(TAG, "USER_TIME: " + userString);
                                } else {
                                    JSONObject userEnter = ind.getJSONObject("userEnteredValue");
                                    String userString = userEnter.getString("stringValue");
                                    regulatevent.setDesc(userString);
//                                    Log.e(TAG, "USER_DESC: " + userString);
                                }
                            }

//                            Log.e(TAG, "Event time: " + regulatevent.getTime() + " Event Descr: " + regulatevent.getDesc());
                        }
                    }
//                    Log.e(TAG, "In the BIg loop Event time: " + regulatevent.getTime() + " Event Descr: " + regulatevent.getDesc());
                    eventList.add(regulatevent);

//                    System.out.println("===========================================");
//                    Log.e(TAG, "Results: " + eventList);
//
//                    for (Event one: eventList) {
//                        Log.e(TAG, "Event info: " + one.toString());
//                    }

                    adapter = new RecyclerViewAdapter(getContext(), eventList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(adapter);

                }

            } catch (JSONException e) {
//                Log.e(TAG, "OUt of bound we know");
                e.printStackTrace();
            }

        }, error -> Log.e(TAG, "onErrorResponse: " + error.getMessage()));
        requestQueue.add(request);

    }

    @Override
    public void onStart() {
        super.onStart();
        scheduleRetrieve();
//        Log.e(TAG, "onStart: " + eventList);
    }
}
