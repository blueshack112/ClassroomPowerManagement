package com.hassan.android.fyp_app_final;

// TODO: Documentation
public class RequestModel {

    private String mCourseName;
    private String mCourseID;
    private String mRequestor;
    private String mRequestorID;
    private String mRoomID;
    private String mRequestType;
    private String mDayOfWeek;
    private String mSlot;
    private String mLength;
    private String mGeneralReason;
    private String mMessage;

    public RequestModel(String courseName,
                        String courseID,
                        String requestor,
                        String requestorID,
                        String roomID,
                        String dayOfWeek,
                        String slot,
                        String length,
                        String requestType,
                        String generalReason,
                        String message) {
        mCourseName = courseName;
        mCourseID = courseID;
        mRequestor = requestor;
        mRequestorID = requestorID;
        mRoomID = roomID;
        mRequestType = requestType;
        mDayOfWeek = dayOfWeek;
        mSlot = slot;
        mLength = length;
        mGeneralReason = generalReason;
        mMessage = message;
    }

    public String getCourseName() {
        return mCourseName;
    }

    public String getCourseID() {
        return mCourseID;
    }

    public String getRequestor() {
        return mRequestor;
    }

    public String getRequestorID() {
        return mRequestorID;
    }

    public String getRequestType() {
        return mRequestType;
    }

    public String getRoomID() {
        return mRoomID;
    }

    public String getDayOfWeek() {
        return mDayOfWeek;
    }

    public String getSlot() {
        return mSlot;
    }

    public String getLength() {
        return mLength;
    }

    public String getGeneralReason() {
        return mGeneralReason;
    }

    public String getMessage() {
        return mMessage;
    }

}
