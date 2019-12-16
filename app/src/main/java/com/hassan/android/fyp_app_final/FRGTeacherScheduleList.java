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

    // Variables used majorly
    protected String userID = "";
    private RecyclerView recycler;
    protected ScheduleListRecyclerAdapter adapter;
    protected ArrayList<CourseModel> courses;
    private ListUpdater updateList;


    public FRGTeacherScheduleList() {
        // Required empty public constructor
    }

    /**
     * Funciton that sets the user ID of the main class
     *
     * @param userID: String: user ID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Cuntion used to refer to the xml of list item for the recycler to show
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_schedule_list, container, false);
    }

    /**
     * Function called to setup the whole layout, get nencessary data and bind OnClickListeners
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        courses = new ArrayList<CourseModel>();
        recycler = view.findViewById(R.id.teacher_schedule_recycler_list);
        adapter = new ScheduleListRecyclerAdapter(getActivity(), courses);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(layoutManager);

        // Thread to keep updating the list
        updateList = new ListUpdater();
        updateList.execute("");


        final String temp = userID;

        //Start calling schedule data and run the dialog so that user wont do anything
        final ProgressDialog dialog = ProgressDialog.show(getContext(), "Fetching Schedule",
                "Fetching your Schedule, Please wait...", true);

        // URL of the API
        String url = "http://" + MainActivity.URL + "/AreebaFYP/teacherSchedule.php";

        //Setting up response handler
        Response.Listener listener = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                try {
                    // Converting the response into JSON object
                    JSONObject schedResponse = new JSONObject(response.toString());

                    // If even a single course item arrived in the response
                    if (schedResponse.getBoolean("scheduleFound")) {
                        // Get the JSON array that has all the course items (in the response)
                        JSONArray scheduleItems = schedResponse.getJSONArray("scheduleItems");

                        // Create CourseModels and assign them to the dataset
                        for (int i = 0; i < scheduleItems.length(); i++) {
                            // Get one array of course information from the whole list
                            JSONObject tempObject = scheduleItems.getJSONObject(i);

                            // Add the course model to the dataset
                            courses.add(new CourseModel(tempObject.getString("courseName"), tempObject.getString("dayOfWeek"), tempObject.getString("slot"), tempObject.getString("classLength"), tempObject.getString("roomID"), tempObject.getString("courseID"), "-1"));
                            checkIfAttendanceUpdated(courses.size()-1, tempObject.getString("roomID"), tempObject.getString("courseID"));
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        courses.add(new CourseModel("NA", "NA", "NA", "NA", "NA", "NA", "NA"));
                        adapter.notifyDataSetChanged();
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

    /**
     * Funcitno to check if the attendance of the current course has been udpated in the database
     * @param roomID: room id of the course
     * @param courseID: course id of the course
     * @return int: -1 if the attendance is not updated and some number if the attendance is updated
     */
    private void checkIfAttendanceUpdated(final int coursePosition, String roomID, String courseID) {
        final String finalRoomID = roomID;
        final String finalCourseID = courseID;
        //This is where the attendance will be retrieved from the database
        // URl of the API request
        String url = "http://" + MainActivity.URL + "/AreebaFYP/getAttendance.php";


        //Setting up response handler
        Response.Listener listener = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                try {
                    // Get the json array that came in response
                    JSONObject getAttendanceResponse = new JSONObject(response.toString());

                    // get attendance variable inside the JSON array
                    int attendance = getAttendanceResponse.getInt("attendance");

                    // Add the attendance to the course item and notify change to the adapter
                    courses.get(coursePosition).setAttendance(attendance);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String localizedMessage = error.getLocalizedMessage();
                if (localizedMessage != null)
                    Log.v("XXXXXXXXXXXXXXXXXX", error.getLocalizedMessage());
                else
                    Toast.makeText(getContext(), "Check your internet connection and try again.", Toast.LENGTH_LONG).show();
            }
        };

        //Initialize request string with POST method
        StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                //Put user ID in data set
                param.put("roomID", finalRoomID);
                param.put("courseID", finalCourseID);
                return param;
            }
        };
        //Execute request
        Volleyton.getInstance(getContext()).addToRequestQueue(request);
    }

    // This class will contain the functinality use to update the list
    private class ListUpdater extends AsyncTask<String, String, String> {

        /**
         * Fucntion that will run in the background simultaneously
         */
        @Override
        protected String doInBackground(String... strings) {
            boolean infinite = true;
            boolean firstTime = true;

            // The infinite loop that will keep running and check for updates
            while (infinite) {
                // If the funciton is running for the first time (means that the activity was just
                // loaded, then wait for a small time
                if (firstTime) {
                    try {
                        firstTime = false;
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // For every course item present inside the courses list...
                for (int i = 0; i < courses.size(); i++) {
                    Log.d("CourseCondition...", courses.get(i).getCourseName() + "\t" + Integer.toString(courses.get(i).getAttendance()) + "\t" + Boolean.toString(courses.get(i).isAttendanceAdded()));
                    // Check if the course's time to get active has arrived and set its active
                    // status variable to true
                    if (courses.get(i).isTime()) {
                        courses.get(i).setIsActive(true);
                    } else {
                        courses.get(i).setIsActive(false);
                    }

                    // If the course is active and its attendance has been set but it is not updated
                    // In the database, call the function that tries to update the database
                    if (courses.get(i).isActive() && courses.get(i).getAttendance() < 0 && !courses.get(i).isAttendanceAdded())
                        courses.set(i, retryAttendanceUpdate(courses.get(i)));

                }

                // After every course has been checkked, publish the progress
                publishProgress();

                // Sleep for 1 second (so that the loop doesn't run repeatedly and use CPU resources
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        /**
         * Function that will be called after every cycle to update the dataset
         */
        @Override
        protected void onProgressUpdate(String... values) {
            // Set the courses variable of the recycler to the updated courses
            adapter.updateDataSet(courses);

            // notify recycler that the dataset has been changed
            adapter.notifyDataSetChanged();
        }

        /**
         * Function that will try to udpate the database again and put the attendance.
         *
         * @param course: CourseModel: the course for which the attendance needs to be updated
         * @return course: CourseModel after the attempt to update was made
         */
        private CourseModel retryAttendanceUpdate(final CourseModel course) {
            //This is where the attendance will be sent to the database based on the room and course
            // URl of the API request
            String url = "http://" + MainActivity.URL + "/AreebaFYP/addAttendance.php";

            //Setting up response handler
            Response.Listener listener = new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    try {
                        boolean addAttendanceSuccess = false;

                        // Get the json array that came in response
                        JSONObject addAttendanceResponse = new JSONObject(response.toString());

                        // Get the successful boolean from the response to see if the attendance was made or not
                        addAttendanceSuccess = addAttendanceResponse.getBoolean("successful");

                        // set the attendance added status of the courses to the success variable so that
                        // if it was false, attendance can be added again in the belo update funciton
                        course.setAttendanceAdded(addAttendanceSuccess);

                        // Log the reaction of the request
                        Log.d("AttendanceStatus: ", Boolean.toString(addAttendanceSuccess));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String localizedMessage = error.getLocalizedMessage();
                    if (localizedMessage != null)
                        Log.v("XXXXXXXXXXXXXXXXXX", error.getLocalizedMessage());
                    else
                        Toast.makeText(getContext(), "Check your internet connection and try again.", Toast.LENGTH_LONG).show();
                }
            };

            //Initialize request string with POST method
            StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<>();
                    //Put user ID in data set
                    param.put("roomID", course.getRoomID());
                    param.put("courseID", course.getCourseID());
                    param.put("attendance", Integer.toString(course.getAttendance()));
                    return param;
                }
            };
            //Execute request
            Volleyton.getInstance(getContext()).addToRequestQueue(request);

            return course;
        }
    }
}