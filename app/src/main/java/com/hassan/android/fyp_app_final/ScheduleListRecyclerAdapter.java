package com.hassan.android.fyp_app_final;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ScheduleListRecyclerAdapter extends RecyclerView.Adapter <ScheduleListRecyclerAdapter.Holder> {

    private ArrayList<CourseModel> courses;
    private Context context;

    public ScheduleListRecyclerAdapter ( Context context, ArrayList<CourseModel> courses) {
        this.courses = courses;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_schedule_list_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.courseNameText.setText(courses.get(position).getCourseName());
        holder.courseDaySlotText.setText(courses.get(position).getCourseDaySlot());
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView courseNameText;
        TextView courseDaySlotText;
        ImageView courseStatusImage;

        public Holder (View itemView) {
            super(itemView);
            courseNameText = itemView.findViewById(R.id.tv_course_name);
            courseDaySlotText = itemView.findViewById(R.id.tv_day_slot);
            courseStatusImage = itemView.findViewById(R.id.img_status);
        }
    }
}
