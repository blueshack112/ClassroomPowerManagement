package com.hassan.android.fyp_app_final;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
// TODO: Documentation
public class FRGRoomStatusTab extends Fragment {
    private Context context;
    private Spinner roomSpinner;
    private Button goButton;
    private TextView dateTV;
    private TextView roomNameTV;
    private TextView courseNameTV;
    private TextView teacherNameTV;
    private TextView sessionTV;
    private TextView classTypeTV;
    private TextView attendanceTV;


    public FRGRoomStatusTab() {
        super();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frgroom_status_tab, container, false);
        context = container.getContext();
        SelectionSpinnerAdapter spinnerAdapter = new SelectionSpinnerAdapter(context, android.R.layout.simple_spinner_dropdown_item);
        roomSpinner = view.findViewById(R.id.spinner_select_room);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSpinner.setAdapter(spinnerAdapter);
        goButton = view.findViewById(R.id.button_room_selected);
        dateTV = view.findViewById(R.id.tv_room_status_date);
        roomNameTV = view.findViewById(R.id.tv_room_status_room_name);
        courseNameTV = view.findViewById(R.id.tv_room_status_course_name);
        teacherNameTV = view.findViewById(R.id.tv_room_status_teacher_name);
        sessionTV = view.findViewById(R.id.tv_room_status_session);
        classTypeTV = view.findViewById(R.id.tv_room_status_class_type);
        attendanceTV = view.findViewById(R.id.tv_room_status_attendance);


        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String selection = roomSpinner.getSelectedItem().toString();
                char room = selection.charAt(selection.length()-1);
                final int finalRoom = room - 64;

                Toast.makeText(context, selection, Toast.LENGTH_SHORT).show();

                String url = "http://" + MainActivity.URL + "/AreebaFYP/roomStatusResponse.php";

                //Setting up response handler
                Response.Listener listener = new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        //Placeholders for text views
                        String dateText = "";
                        String roomNameText = "";
                        String courseNameText = "";
                        String teacherNameText = "";
                        String sessionText = "";
                        String classTypeText = "";
                        String attendanceText = "";
                        try {
                            JSONObject schedResponse = new JSONObject(response.toString());

                            if (schedResponse.getBoolean("roomIsActive")) {
                                JSONObject roomStatusResponse = schedResponse.getJSONObject("roomResponse");
                                dateText = roomStatusResponse.getString("classDate");
                                roomNameText = roomStatusResponse.getString("roomID");
                                courseNameText = roomStatusResponse.getString("courseName");
                                teacherNameText = roomStatusResponse.getString("teacherFirstName") + " " + roomStatusResponse.getString("teacherLastName");
                                sessionText = roomStatusResponse.getString("classLength");
                                classTypeText = "Regular";
                                attendanceText = roomStatusResponse.getString("attendance");
                                if (attendanceText.equals("-1")) {
                                    attendanceText = "Not Yet Added";
                                }
                            } else {
                                String date = Calendar.getInstance().getTime().toString();
                                dateText = date.substring(0, date.indexOf(':')-2);
                                roomNameText = selection;
                                courseNameText = "None";
                                teacherNameText = "None";
                                sessionText = "None";
                                classTypeText = "Room Inactive";
                                attendanceText = "None";
                            }
                            dateTV.setText(dateText);
                            roomNameTV.setText(roomNameText);
                            courseNameTV.setText(courseNameText);
                            teacherNameTV.setText(teacherNameText);
                            sessionTV.setText(sessionText);
                            classTypeTV.setText(classTypeText);
                            attendanceTV.setText(attendanceText);
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
                        param.put("roomID", Integer.toString(1000+finalRoom));
                        return param;
                    }
                };
                //Execute request
                Volleyton.getInstance(getContext()).addToRequestQueue(request);
            }
        });
        return view;
    }
}
