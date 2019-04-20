package com.hassan.android.fyp_app_final;

public class RequestModel {
    private String mRequester, mCourseName, mRequestType, mReason;
    public RequestModel (String requester, String courseName, String requestType, String reason) {
        mRequester = requester;
        mCourseName = courseName;
        mRequestType = requestType;
        mReason = reason;
    }

    public String getRequester() {
        return mRequester;
    }

    public String getCourseName() {
        return mCourseName;
    }

    public String getReason() {
        return mReason;
    }

    public String getRequestType() {
        return mRequestType;
    }
}
