package com.hassan.android.fyp_app_final;

public class CourseModel {

    private String mRoom;
    private String mCourseName;
    private String mDaySlotLength;

    public CourseModel (String courseName, String courseDaySlot) {
        mCourseName = courseName;
        mDaySlotLength = courseDaySlot;
    }

    public String getCourseName () {
        return mCourseName;
    }
    public String getCourseDaySlot () {
        return mDaySlotLength;
    }
}
