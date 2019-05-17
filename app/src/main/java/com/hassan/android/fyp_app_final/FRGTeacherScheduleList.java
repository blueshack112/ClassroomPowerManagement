package com.hassan.android.fyp_app_final;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class FRGTeacherScheduleList extends Fragment {

    protected String userID = "";
    private RecyclerView recycler;
    private ScheduleListRecyclerAdapter adapter;
    private ArrayList<CourseModel> courses;

    public FRGTeacherScheduleList() {
        // Required empty public constructor
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_schedule_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        courses = new ArrayList<CourseModel>();
        recycler = view.findViewById(R.id.teacher_schedule_recycler_list);
        adapter = new ScheduleListRecyclerAdapter(getActivity(), courses);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(layoutManager);

        final String temp = userID;
        //Start calling schedule data
        final ProgressDialog dialog = ProgressDialog.show(getContext(), "Fetching Schedule",
                "Fetching your Schedule, Please wait...", true);
        String url = "http://" + MainActivity.URL + "/AreebaFYP/teacherSchedule.php";

        //Setting up response handler
        Response.Listener listener = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                try {
//                    Log.d("YYYYYYYYYYYYYYYYY", response.toString());
                    JSONObject schedResponse = new JSONObject(response.toString());

                    if (schedResponse.getBoolean("scheduleFound")) {
                        JSONArray scheduleItems = schedResponse.getJSONArray("scheduleItems");
                        Calendar cal = Calendar.getInstance();
                        for (int i = 0; i < scheduleItems.length(); i++) {
                            JSONObject tempObject = scheduleItems.getJSONObject(i);
                            String courseName = tempObject.getString("courseName");
                            String daySlotLength = "";
                            cal.set(Calendar.DAY_OF_WEEK,Integer.parseInt(tempObject.getString("dayOfWeek"))+1);
                            daySlotLength += cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
                            daySlotLength += " | " + tempObject.getString("slot");

                            if (Integer.parseInt(tempObject.getString("slot")) == 1)
                                daySlotLength += "st slot";
                            else if (Integer.parseInt(tempObject.getString("slot")) == 2)
                                daySlotLength += "nd slot";
                            else if (Integer.parseInt(tempObject.getString("slot")) == 3)
                                daySlotLength += "rd slot";
                            else
                                daySlotLength += "th slot";

                            daySlotLength += " | " + tempObject.getString("classLength") + " sessions";
                            courses.add(new CourseModel(courseName, daySlotLength));
                            adapter.notifyDataSetChanged();
                            Log.d("XXXXXXXXXXXXXXXXXXXXX", "added");
                        }
                    } else {
                        courses.add(new CourseModel("NA", "NA"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("XXXXXXXXXXXXXXXXXX", error.getLocalizedMessage());
            }
        };

        //Initialize request string with GET method
        StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                //Put user ID in data set
                param.put("userID", temp);
                return param;
            }
        };
        //Execute request
        Volleyton.getInstance(getContext()).addToRequestQueue(request);
        dialog.cancel();
    }
}