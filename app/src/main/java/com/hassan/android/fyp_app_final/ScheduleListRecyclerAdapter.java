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
    // Will contian all the instances of courses
    private Context                context;
    // Required for stuff like Toast

    /**
     * Necessary contructor for adapter to initialize properly
     */
    public ScheduleListRecyclerAdapter(Context context, ArrayList<CourseModel> courses) {
        this.courses = courses;
        this.context = context;
    }

    /**
     * Overridden function used to render the required
     */
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.teacher_schedule_list_item, parent, false);
        return new Holder(view);
    }

    /**
     * Overridden Function where all the bounds and listeners are initiated.
     * View variables used in this function are created in the constructor of the
     * Holder class created belo
     */
    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        // Set the course name text to the name of the course
        holder.courseNameText.setText(courses.get(position).getCourseName());

        // Set the slot text to the slot of the course
        holder.courseDaySlotText
                .setText(courses.get(position).getCourseDaySlot() + " | " + courses.get(position).getRoomName());

        // if the courses is active, show green circular light or else show red
        if (courses.get(position).isActive()) {
            holder.attendanceButton.setVisibility(View.VISIBLE);
            holder.courseStatusImage.setImageResource(R.drawable.icon_class_available);
        } else {
            holder.attendanceButton.setVisibility(View.GONE);
            holder.courseStatusImage.setImageResource(R.drawable.icon_class_offline);
        }

        // Set the holder's attendance entered status to the course's attendance entered status
        // so that if the application is restarted and the attendance is entered in the database
        // then the app wont allow the user the enter the attendance
        holder.attendanceEntered = courses.get(position).isAttendanceAdded();

        // on click listener for for the add attendance button
        holder.attendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create an alert dialog to enter the attendance of the ongoing class.
                final AlertDialog.Builder attendanceBuilder = new AlertDialog.Builder(context);

                // Render the view
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_attendance, null);
                final TextView tvAttendance = view.findViewById(R.id.tv_attendance);

                // Set cancel button
                attendanceBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // Set submit button and its functionality
                attendanceBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    boolean addAttendanceSuccess = false;

                    //This is where the attendance will be sent to the database based on the room and course
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final Context dialogContext = context;
                        // URl of the API request
                        String url = "http://" + MainActivity.URL + "/AreebaFYP/addAttendance.php";

                        // Get the attendance and convert it to int for proper usage
                        final int attendance = Integer.parseInt(tvAttendance.getText().toString());

                        // Set the attendance of the course item to the one entered by user
                        courses.get(position).setAttendance(attendance);

                        // Set  the current holder's attendance entered to true so that the teacher cannot enter
                        // again
                        holder.changeAttendanceEntered(true);

                        //Setting up response handler
                        Response.Listener listener = new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                try {
                                    // Get the json array that came in response
                                    JSONObject addAttendanceResponse = new JSONObject(response.toString());

                                    // Get the successful boolean from the response to see if the attendance was
                                    // made or not
                                    addAttendanceSuccess = addAttendanceResponse.getBoolean("successful");

                                    // Check if the attendance entered was more than max students enrolled in
                                    // the course
                                    boolean moreThanMax = addAttendanceResponse.getBoolean("moreThanMaxStudents");
                                    if (moreThanMax) {
                                        dialog.dismiss();
                                        new AlertDialog.Builder(dialogContext).setTitle("Invalid Attendance")
                                                                              .setMessage(
                                                                                      "Attendance entered is " +
                                                                                      "more than the number of " +
                                                                                      "students enrolled.")
                                                                              .setPositiveButton("Ok",
                                                                                                 new DialogInterface.OnClickListener() {
                                                                                                     @Override
                                                                                                     public void onClick(
                                                                                                             DialogInterface dialog,
                                                                                                             int which) {
                                                                                                         dialog.dismiss();
                                                                                                     }
                                                                                                 });
                                        holder.changeAttendanceEntered(false);
                                        courses.get(position).setAttendance(-1);
                                        courses.get(position).setAttendanceAdded(false);
                                        return;
                                    }

                                    // set the attendance added status of the courses to the success variable so
                                    // that
                                    // if it was false, attendance can be added again in the belo update function
                                    courses.get(position).setAttendanceAdded(addAttendanceSuccess);


                                    // React based on success statuses
                                    if (addAttendanceSuccess) {
                                        Toast.makeText(context,
                                                       "Attendance is " + tvAttendance.getText().toString() +
                                                       "\tHolder.Attendance Entered:" +
                                                       Boolean.toString(holder.attendanceEntered) +
                                                       "Attendance Upload Success:" +
                                                       Boolean.toString(addAttendanceSuccess), Toast.LENGTH_SHORT)
                                             .show();
                                    } else {
                                        Log.d("AttendanceStatus: ", Boolean.toString(addAttendanceSuccess));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String localizedMessage = error.getLocalizedMessage();
                                if (localizedMessage != null) {
                                    Log.v("XXXXXXXXXXXXXXXXXX", error.getLocalizedMessage());
                                } else {
                                    Toast.makeText(context, "Check your internet connection and try again.",
                                                   Toast.LENGTH_LONG).show();
                                }
                            }
                        };

                        //Initialize request string with POST method
                        StringRequest request =
                                new StringRequest(Request.Method.POST, url, listener, errorListener) {
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

                        // Dismiss the dialog once this is done
                        dialog.dismiss();
                    }
                });

                // Set view of the dialog to be shown
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
                } else {
                    attendanceBuilder.create().show();
                }
            }
        });
    }

    /**
     * Function to ge the size of the courses variable
     */
    @Override
    public int getItemCount() {
        return courses.size();
    }

    /**
     * Function that updates the courses variable to add and remove courses from the list if needed.
     *
     * @param courses: the instance of the new list that needs to replace the old one.
     */
    public void updateDataSet(ArrayList<CourseModel> courses) {
        this.courses = courses;
    }

    /**
     * Class where the layout is initialized and its views are bound to variables (check constructor)
     */
    class Holder extends RecyclerView.ViewHolder {
        boolean   attendanceEntered;
        TextView  courseNameText;
        TextView  courseDaySlotText;
        ImageView courseStatusImage;
        Button    attendanceButton;

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
}
