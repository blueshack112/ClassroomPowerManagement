package com.hassan.android.fyp_app_final;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private Context context;

    public PendingRequestRecyclerAdapter(Context context) {
        this.context = context;
        requests = new ArrayList<>();
        loadData();
    }

    @Override
    public PendingRequestRecyclerAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_request_list_item, parent, false);
        return new PendingRequestRecyclerAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(PendingRequestRecyclerAdapter.Holder holder, int position) {
        holder.courseNameText.setText(requests.get(position).getCourseName());
        holder.teacherNameText.setText(requests.get(position).getRequestor());
        holder.requestTypeText.setText(requests.get(position).getRequestType());
        holder.generalReasonText.setText(requests.get(position).getMessage());
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
                                    request.getString("userID"),
                                    request.getString("roomID"),
                                    request.getString("dayOfWeek"),
                                    request.getString("slot"),
                                    request.getString("length"),
                                    request.getString("requestType"),
                                    request.getString("generalReason"),
                                    request.getString("message"))
                            );
                            notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(context, "No requests were found.", Toast.LENGTH_SHORT).show();
                        //TODO: initialize an empty request object
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

        public Holder(View itemView) {
            super(itemView);
            courseNameText = itemView.findViewById(R.id.tv_pending_request_course_name);
            teacherNameText = itemView.findViewById(R.id.tv_pending_request_teacher_name);
            requestTypeText = itemView.findViewById(R.id.tv_pending_request_request_type);
            generalReasonText = itemView.findViewById(R.id.tv_pending_request_general_reason);
        }
    }
}

