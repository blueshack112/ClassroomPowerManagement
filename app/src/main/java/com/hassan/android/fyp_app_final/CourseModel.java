package com.hassan.android.fyp_app_final;

public class CourseModel {

    private String mCourseName;
    private String mCourseDaySlot;

    public CourseModel (String courseName, String courseDaySlot) {
        mCourseName = courseName;
        mCourseDaySlot = courseDaySlot;
    }

    public String getCourseName () {
        return mCourseName;
    }
    public String getCourseDaySlot () {
        return mCourseDaySlot;
    }
}
