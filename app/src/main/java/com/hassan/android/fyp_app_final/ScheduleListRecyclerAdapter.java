package com.hassan.android.fyp_app_final;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScheduleListRecyclerAdapter extends RecyclerView.Adapter<ScheduleListRecyclerAdapter.Holder> {

    private ArrayList<CourseModel> courses;
    private Context context;

    public ScheduleListRecyclerAdapter(Context context, ArrayList<CourseModel> courses) {
        this.courses = courses;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_schedule_list_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        holder.courseNameText.setText(courses.get(position).getCourseName());
        holder.courseDaySlotText.setText(courses.get(position).getCourseDaySlot());
        if (courses.get(position).isActive()) {
            holder.attendanceButton.setVisibility(View.VISIBLE);
            holder.courseStatusImage.setImageResource(R.drawable.icon_class_available);
        } else {
            holder.attendanceButton.setVisibility(View.GONE);
            holder.courseStatusImage.setImageResource(R.drawable.icon_class_offline);
        }

        //Create an alert dialog to enter the attendance of the ongoing class.
        holder.attendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder attendanceBuilder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_attendance, null);
                final TextView tvAttendance = view.findViewById(R.id.tv_attendance);
                attendanceBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                attendanceBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    boolean addAttendanceSuccess = false;
                    //This is where the attendance will be sent to the databse based on the room and course
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = "http://" + MainActivity.URL + "/AreebaFYP/addAttendance.php";

                        //Setting up response handler
                        Response.Listener listener = new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                try {
                                    JSONObject addAttendanceResponse = new JSONObject(response.toString());
                                    addAttendanceSuccess = addAttendanceResponse.getBoolean("successful");
                                    Log.d("AAAAAAAAAAAAAAAAAAAAAAA", Boolean.toString(addAttendanceSuccess));
                                    holder.changeAttendanceEntered(addAttendanceSuccess);
                                    Toast.makeText(context, "Attendance is " + tvAttendance.getText().toString() + "\t" + Boolean.toString(holder.attendanceEntered), Toast.LENGTH_SHORT).show();
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
                                param.put("roomID", courses.get(position).getRoomID());
                                param.put("courseID", courses.get(position).getCourseID());
                                param.put("attendance", tvAttendance.getText().toString());
                                return param;
                            }
                        };
                        //Execute request
                        Volleyton.getInstance(context).addToRequestQueue(request);
                        dialog.dismiss();
                    }
                });
                attendanceBuilder.setView(view);

                //if attendance is already entered, don't enter again.
                if (holder.attendanceEntered) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Attendance Already Entered!");
                    builder.setMessage("You have already entered attendance for this class.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    return;
                }
                attendanceBuilder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        boolean attendanceEntered;
        TextView courseNameText;
        TextView courseDaySlotText;
        ImageView courseStatusImage;
        Button attendanceButton;

        public Holder(View itemView) {
            super(itemView);
            courseNameText = itemView.findViewById(R.id.tv_course_name);
            courseDaySlotText = itemView.findViewById(R.id.tv_day_slot);
            courseStatusImage = itemView.findViewById(R.id.img_status);
            attendanceButton = itemView.findViewById(R.id.button_add_attendance);
            attendanceEntered = false;
        }

        public void changeAttendanceEntered(boolean x) {
            attendanceEntered = x;
        }
    }


    public void updateDataSet(ArrayList<CourseModel> courses) {
        this.courses = courses;
    }
}
