package com.hassan.android.fyp_app_final;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PendingRequestRecyclerAdapter extends RecyclerView.Adapter<PendingRequestRecyclerAdapter.Holder> {
    private ArrayList<RequestModel> requests;
    private Context context;

    public PendingRequestRecyclerAdapter ( Context context, ArrayList<RequestModel> requests) {
        this.requests = requests;
        this.context = context;
    }

    @Override
    public PendingRequestRecyclerAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_request_list_item, parent, false);
        return new PendingRequestRecyclerAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(PendingRequestRecyclerAdapter.Holder holder, int position) {
        holder.courseNameText.setText(requests.get(position).getCourseName());
        holder.teacherNameText.setText(requests.get(position).getRequester());
        holder.requestTypeText.setText(requests.get(position).getRequestType());
        holder.generalReasonText.setText(requests.get(position).getReason());
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView courseNameText;
        TextView teacherNameText;
        TextView requestTypeText;
        TextView generalReasonText;

        public Holder (View itemView) {
            super(itemView);
            courseNameText = itemView.findViewById(R.id.tv_pending_request_course_name);
            teacherNameText = itemView.findViewById(R.id.tv_pending_request_teacher_name);
            requestTypeText = itemView.findViewById(R.id.tv_pending_request_request_type);
            generalReasonText = itemView.findViewById(R.id.tv_pending_request_general_reason);
        }
    }
}

