package com.hassan.android.fyp_app_final;

import android.os.Parcel;
import android.os.Parcelable;
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
    private int mAttendance;
    private boolean mIsActive;
    private boolean mAttendanceAdded;

    /**
     * Constructor to initialize a course item, the list of which will be recieved from the database.
     *
     * @param courseName: Name of the course
     * @param day:        Day of week
     * @param slot:       Slot of day (1-7)
     * @param length:     Class length
     * @param room:       Room ID
     * @param courseID:   Course ID
     */
    public CourseModel(String courseName, String day, String slot, String length, String room, String courseID, String attendance) {
        mCourseName = courseName;
        mDay = day;
        mSlot = slot;
        mLength = length;
        mIsActive = true;
        // If attendance is null or empty, set attendance to -1
        if (attendance == null || attendance.isEmpty() || attendance.equals("NA")) {
            mAttendance = -1;
            mAttendanceAdded = false;
        }
        else {
            Log.d("AttendanceStringProblem", attendance);
            mAttendance = Integer.parseInt(attendance);
            mRoom = room;
        }
        mCourseID = courseID;
    }

    /**
     * Fucntion to get the course name of the item
     *
     * @return String: course name.
     */
    public String getCourseName() {
        return mCourseName;
    }

    /**
     * Function that generates a string which contains the current course's day of week, slot and
     * class length.
     *
     * @return String: Formatted course day slot and length
     */
    public String getCourseDaySlot() {
        if (mDay.equals("NA")) {
            return "NA";
        }
        Calendar cal = Calendar.getInstance();
        String daySlotLength = "";
        cal.set(Calendar.DAY_OF_WEEK, Integer.parseInt(mDay) + 1);

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

    /**
     * Function to get the id of the course
     *
     * @return String: course ID
     */
    public String getCourseID() {
        return mCourseID;
    }

    /**
     * Function to get the id of the room
     *
     * @return String: room ID
     */
    public String getRoomID() {
        return mRoom;
    }

    /**
     * Function to get the attendance fo the current course
     * @return int: attendance
     */
    public int getAttendance() {
        return mAttendance;
    }

    /**
     * Function to set the attendance of the course
     * @param attendance: int: attendance
     */
    public void setAttendance(int attendance) {
        mAttendance = attendance;
    }

    /**
     * Function to check if the course is active
     *
     * @return boolean active status
     */
    public boolean isActive() {
        return mIsActive;
    }

    /**
     * Set the course's active status to active if its time has arrived
     *
     * @param x: boolean: new active status
     */
    public void setIsActive(boolean x) {
        mIsActive = x;
    }

    /**
     * Function to check if the attendance of the course has been added to the database or not
     *
     * @return boolean: attendance added status
     */
    public boolean isAttendanceAdded() {
        return mAttendanceAdded;
    }

    /**
     * Function to set the attendance added status if the attendance was added successfully
     *
     * @param attendanceAdded: boolean: new attendance added status
     */
    public void setAttendanceAdded(boolean attendanceAdded) {
        mAttendanceAdded = attendanceAdded;
    }

    /**
     * Function that compares the slot of the course and checks to see if the the time fo the course
     * has arrived or not.
     *
     * @return boolean: true or false as the active status
     */
    public boolean isTime() {
        boolean answer = false;
        int day = MainActivity.getCurrentDayOfWeek();
        int slot = MainActivity.getCurrentSlot();
        if (Integer.parseInt(mDay) == day && Integer.parseInt(mSlot) == slot) {
            answer = true;
        } else if (Integer.parseInt(mDay) == day && Integer.parseInt(mLength) == 2 && Integer.parseInt(mSlot) + 1 == slot + 1) {
            answer = true;
        } else {
            answer = false;
        }
        return answer;
    }
}
