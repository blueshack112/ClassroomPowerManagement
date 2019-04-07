package com.hassan.android.fyp_app_final;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class TeacherScheduleList extends Fragment {

    private RecyclerView recycler;
    private ScheduleListRecyclerAdapter adapter;
    private ArrayList<CourseModel> courses;

    public TeacherScheduleList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_schedule_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        courses = new ArrayList<CourseModel>();
        courses.add(new CourseModel("Test", "Test")); //JUST FOR SAFETY, REMOVE IT IN FINAL RUN
        recycler = view.findViewById(R.id.recycler_list);
        adapter = new ScheduleListRecyclerAdapter(getActivity(), courses);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(layoutManager);
    }
}