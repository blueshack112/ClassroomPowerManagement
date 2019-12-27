package com.hassan.android.fyp_app_final;

/**
 * !!HOW THE PENDING REQUEST FORM DIALOG IS GOING TO WORK!!
 * - The form will have all the information required for the HOD to make the decision.
 * - Three buttons will be available to use in this dialog:
 * - Accept: The request will be accepted and the classroom manager will see it as an active session.
 * - Reject: The request will be rejected will not show again in the pending request column.
 * - Hold: No changes will be made to the request and the request will remain in the pending request section.
 * <p>
 * TODO: the request should be removed from the models also!
 * TODO: sort the requests based on day of week and slot!
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class PendingRequestInfoDialog extends DialogFragment {

    // Views
    private View formView;

    // Text Views
    private TextView requestTypeTextView;
    private TextView teacherNameTextView;
    private TextView courseNameTextView;
    private TextView roomTextView;
    private TextView dayTextView;
    private TextView slotTextView;
    private TextView lengthTextView;
    private TextView generalReasonTextView;
    private TextView messageTextView;

    // Data
    private String requestType;
    private String teacherName;
    private String teacherID;
    private String courseName;
    private String courseID;
    private String room;
    private String roomID;
    private String day;
    private String slot;
    private String length;
    private String generalReason;
    private String message;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        formView = inflater.inflate(R.layout.dialog_extra_request_form, null);

        //Creating functionality for dialog objects
        setupViews();

        // Set the builder's view to your view that has been setup now
        builder.setView(formView);

        // Setup accept, reject, and hold buttons
        // TODO: these buttons
        builder.setNegativeButton("HOLD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submitRequest(dialog, getContext(), "ACCEPT");
            }
        });
        builder.setNeutralButton("REJECT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submitRequest(dialog, getContext(), "REJECT");
            }
        });

        return builder.create();
    }

    /**
     * Fucntion that will assign all the ids to the UI objects and call the load the data into each UI object
     */
    public void setupViews() {
        // Assign layout ids to the corresponding element so that it can be manipulated
        requestTypeTextView = formView.findViewById(R.id.pending_request_info_request_type_text_view);
        teacherNameTextView = formView.findViewById(R.id.pending_request_info_teacher_text_view);
        courseNameTextView = formView.findViewById(R.id.pending_request_info_course_name_text_view);
        roomTextView = formView.findViewById(R.id.pending_request_info_room_text_view);
        dayTextView = formView.findViewById(R.id.pending_request_info_day_text_view);
        slotTextView = formView.findViewById(R.id.pending_request_info_slot_text_view);
        lengthTextView = formView.findViewById(R.id.pending_request_info_length_text_view);
        generalReasonTextView = formView.findViewById(R.id.pending_request_info_general_reason_text_view);
        messageTextView = formView.findViewById(R.id.pending_request_info_message_text_view);

        //Create functionality for elements
        setupRequestTypeTextView();
        setupTeacherNameTextView();
        setupcourseNameTextView();
        setupRoomTextView();
        setupDayTextView();
        setupSlotTextView();
        setupLengthTextView();
        setupGeneralReasonTextView();
        setupMessageTextView();
    }

    /**
     * Function that will set the request type text view's text
     */
    private void setupRequestTypeTextView() {
        requestTypeTextView.setText(requestType);
    }

    /**
     * Function that will set the request type teacher name text view's text
     */
    private void setupTeacherNameTextView() {
        teacherNameTextView.setText(teacherName);
    }

    /**
     * Function that will set the request type course text view's text
     */
    private void setupcourseNameTextView() {
        courseNameTextView.setText(courseName);
    }

    /**
     * Function that will set the request type room text view's text
     */
    private void setupRoomTextView() {
        roomTextView.setText(room);
    }

    /**
     * Function that will set the request type day text view's text
     */
    private void setupDayTextView() {
        dayTextView.setText(day);
    }

    /**
     * Function that will set the request type slot text view's text
     */
    private void setupSlotTextView() {
        slotTextView.setText(slot);
    }

    /**
     * Function that will set the request type length text view's text
     */
    private void setupLengthTextView() {
        lengthTextView.setText(length);
    }

    /**
     * Function that will set the request type general reason text view's text
     */
    private void setupGeneralReasonTextView() {
        generalReasonTextView.setText(generalReason);
    }

    /**
     * Function that will set the request type message text view's text
     */
    private void setupMessageTextView() {
        messageTextView.setText(message);
    }

    /**
     * Function where the request will either be rejected or accepted based on the HOD's decision.
     * In both cases a php script will be called which will validate the request, execute it, and return a true or false response.
     * Based on that true or false response, the behavior of the function may differ.
     */
    public void submitRequest(final DialogInterface dialogFragment, final Context context, String decision) {
        if (decision.equals("ACCEPT")) {
            //Start calling acceptRequest.php and check if the reuest could be submitted
            // URL of the API
            String url = "http://" + MainActivity.URL + "/AreebaFYP/acceptRequest.php";

            //Setting up response handler
            Response.Listener listener = new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    try {
                        // Converting the response into JSON object
                        JSONObject extraResponse = new JSONObject(response.toString());
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
                    // Put request details in the data set
                    param.put("userID", teacherID);
                    param.put("courseID", courseID);
                    param.put("dayOfWeek", day);
                    param.put("slot", slot);
                    param.put("length", length);
                    param.put("requestType", requestType);
                    return param;
                }
            };
            //Execute request
            //Volleyton.getInstance(getContext()).addToRequestQueue(request);
        } else {
            //Start calling acceptRequest.php and check if the reuest could be submitted
            // URL of the API
            String url = "http://" + MainActivity.URL + "/AreebaFYP/rejectRequest.php";

            //Setting up response handler
            Response.Listener listener = new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    try {
                        // Converting the response into JSON object
                        JSONObject extraResponse = new JSONObject(response.toString());
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
                    // Put request details in the data set
                    param.put("userID", teacherID);
                    param.put("courseID", courseID);
                    param.put("dayOfWeek", day);
                    param.put("slot", slot);
                    param.put("length", length);
                    param.put("requestType", requestType);
                    return param;
                }
            };
            //Execute request
            //Volleyton.getInstance(getContext()).addToRequestQueue(request);
        }
    }

    /**
     * This function is how the activity will send arguments to the dialog.
     * @param args: Bundle: must contain all the required request perimiters
     */
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        // Loading the data from the bundle that was passed
        requestType = args.getString("requestType");
        teacherName = args.getString("teacherName");
        teacherID = args.getString("teacherID");
        courseName = args.getString("courseName");
        courseID= args.getString("courseID");
        roomID = args.getString("room");
        day = args.getString("day");
        slot = args.getString("slot");
        length = args.getString("length");
        generalReason = args.getString("generalReason");
        message = args.getString("message");

        // Change Room to 'Room A' format from '1001' format
        char roomchar = (char)(Integer.parseInt(roomID) - 1000 + 64);
        room = "Room " + roomchar;
    }
}
