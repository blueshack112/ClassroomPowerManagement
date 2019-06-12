package com.hassan.android.fyp_app_final;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
    protected ScheduleListRecyclerAdapter adapter;
    protected ArrayList<CourseModel> courses;
    private ListUpdater updateList;

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
        //Thread to keep updating the list
        updateList = new ListUpdater();
        updateList.execute("");


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
                    JSONObject schedResponse = new JSONObject(response.toString());

                    if (schedResponse.getBoolean("scheduleFound")) {
                        JSONArray scheduleItems = schedResponse.getJSONArray("scheduleItems");
                        for (int i = 0; i < scheduleItems.length(); i++) {
                            JSONObject tempObject = scheduleItems.getJSONObject(i);
                            String courseName = tempObject.getString("courseName");
                            courses.add(new CourseModel(courseName, tempObject.getString("dayOfWeek"), tempObject.getString("slot"), tempObject.getString("classLength")));
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        courses.add(new CourseModel("NA", "NA", "NA", "NA"));
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

        //Initialize request string with POST method
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

    private class ListUpdater extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            boolean dummy = true;
            boolean firstTime = true;
            while (dummy) {
                if (firstTime) {
                    try {
                        firstTime = false;
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < courses.size(); i++) {
                    if (courses.get(i).isTime()) {
                        courses.get(i).setIsActive(true);
                    } else {
                        courses.get(i).setIsActive(false);
                    }
                    publishProgress();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            adapter.updateDataSet(courses);
            adapter.notifyDataSetChanged();
        }
    }
}