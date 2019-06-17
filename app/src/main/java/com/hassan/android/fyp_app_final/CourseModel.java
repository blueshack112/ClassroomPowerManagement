package com.hassan.android.fyp_app_final;

import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

public class CourseModel {

    private String mRoom;
    private String mCourseID;
    private String mCourseName;
    private String mDay;
    private String mSlot;
    private String mLength;
    private String mDaySlotLength;
    private boolean mIsActive;

    public CourseModel(String courseName, String day, String slot, String length, String room, String courseID) {
        mCourseName = courseName;
        mDaySlotLength = "";
        mDay = day;
        mSlot = slot;
        mLength = length;
        mIsActive = true;
        mRoom = room;
        mCourseID = courseID;
    }

    public String getCourseName() {
        return mCourseName;
    }

    public String getCourseDaySlot() {
        if (mDay.equals("NA")) {
            return "NA";
        }
        Calendar cal = Calendar.getInstance();
        String daySlotLength = "";
        cal.set(Calendar.DAY_OF_WEEK, Integer.parseInt(mDay)+1);

        daySlotLength += cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
        daySlotLength += " | " + mSlot;
        if (Integer.parseInt(mSlot) == 1)
            daySlotLength += "st slot";
        else if (Integer.parseInt(mSlot) == 2)
            daySlotLength += "nd slot";
        else if (Integer.parseInt(mSlot) == 3)
            daySlotLength += "rd slot";
        else
            daySlotLength += "th slot";
        daySlotLength += " | " + mLength + " sessions";
        return daySlotLength;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public void setIsActive(boolean x) {
        mIsActive = x;
    }

    public String getCourseID () {
        return mCourseID;
    }
    public String getRoomID () {
        return mRoom;
    }

    public boolean isTime() {
        boolean answer = false;
        int day = MainActivity.getCurrentDayOfWeek();
        int slot = MainActivity.getCurrentSlot();
        if (Integer.parseInt(mDay) == day && Integer.parseInt(mSlot) == slot) {
            answer = true;
        } else if (Integer.parseInt(mDay) == day && Integer.parseInt(mLength) == 2 && Integer.parseInt(mSlot)+1 == slot+1) {
            answer = true;
        } else {
            answer = false;
        }
        return answer;
    }
}
