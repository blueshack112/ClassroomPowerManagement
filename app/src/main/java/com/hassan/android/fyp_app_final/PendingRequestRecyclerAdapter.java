package com.hassan.android.fyp_app_final;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.Map;

//TODO: Documentation

public class PendingRequestRecyclerAdapter extends RecyclerView.Adapter<PendingRequestRecyclerAdapter.Holder> {
    private ArrayList<RequestModel> requests;
    private Context                 context;

    public PendingRequestRecyclerAdapter(Context context) {
        this.context = context;
        requests = new ArrayList<>();
        loadData();
    }

    @Override
    public PendingRequestRecyclerAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.pending_request_list_item, parent, false);
        return new PendingRequestRecyclerAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(PendingRequestRecyclerAdapter.Holder holder, final int position) {
        // Getting request parameters and setting it to the views
        holder.courseNameText.setText(requests.get(position).getCourseName());
        holder.teacherNameText.setText(requests.get(position).getRequestor());
        holder.requestTypeText.setText(requests.get(position).getRequestType());
        holder.generalReasonText.setText(requests.get(position).getMessage());

        // Setting up the button functionality
        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment infoDialog = new PendingRequestInfoDialog();

                // Creating the bundle through which the data will be sent
                Bundle b = new Bundle();
                b.putString("requestType", requests.get(position).getRequestType());
                b.putString("teacherName", requests.get(position).getRequestor());
                b.putString("teacherID", requests.get(position).getRequestorID());
                b.putString("courseName", requests.get(position).getCourseName());
                b.putString("courseID", requests.get(position).getCourseID());
                b.putString("room", requests.get(position).getRoomID());
                b.putString("day", requests.get(position).getDayOfWeek());
                b.putString("slot", requests.get(position).getSlot());
                b.putString("length", requests.get(position).getLength());
                b.putString("generalReason", requests.get(position).getGeneralReason());
                b.putString("message", requests.get(position).getMessage());

                // Send the arguments and show the dialog
                infoDialog.setArguments(b);
                infoDialog.show(((AppCompatActivity)context).getSupportFragmentManager(), "infoDialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    /**
     * This function will call the pendingRequests.php script and load request data
     */
    public void loadData() {
        //Start calling pendingRequest.php and check if the request could be submitted
        // URL of the API
        String url = "http://" + MainActivity.URL + "/AreebaFYP/pendingRequests.php";

        //Setting up response handler
        Response.Listener listener = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                try {
                    // Converting the response into JSON object
                    JSONObject pendingResponse = new JSONObject(response.toString());

                    // If even a single course item arrived in the response
                    if (pendingResponse.getBoolean("requestsFound")) {
                        JSONArray jsonRequests = pendingResponse.getJSONArray("pendingRequests");
                        for (int i = 0; i < jsonRequests.length(); i++) {
                            JSONObject request = jsonRequests.getJSONObject(i);
                            requests.add(new RequestModel(request.getString("courseName"),
                                                          request.getString("courseID"),
                                                          request.getString("userName"),
                                                          request.getString("userID"), request.getString("roomID"),
                                                          request.getString("dayOfWeek"),
                                                          request.getString("slot"), request.getString("length"),
                                                          request.getString("requestType"),
                                                          request.getString("generalReason"),
                                                          request.getString("message")));
                            notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(context, "No requests were found.", Toast.LENGTH_SHORT).show();
                        String emp = "";
                        requests.add(
                                new RequestModel("No Requests Present", emp, emp, emp, emp, emp, emp, emp, emp,
                                                 emp, emp));
                        notifyDataSetChanged();
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
                return param;
            }
        };

        //Execute request
        Volleyton.getInstance(context).addToRequestQueue(request);
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView courseNameText;
        TextView teacherNameText;
        TextView requestTypeText;
        TextView generalReasonText;
        Button   detailsButton;

        public Holder(View itemView) {
            super(itemView);
            courseNameText = itemView.findViewById(R.id.tv_pending_request_course_name);
            teacherNameText = itemView.findViewById(R.id.tv_pending_request_teacher_name);
            requestTypeText = itemView.findViewById(R.id.tv_pending_request_request_type);
            generalReasonText = itemView.findViewById(R.id.tv_pending_request_general_reason);
            detailsButton = itemView.findViewById(R.id.pending_request_detail_button);
        }
    }
}

