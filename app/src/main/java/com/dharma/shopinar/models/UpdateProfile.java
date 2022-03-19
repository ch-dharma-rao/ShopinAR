package com.dharma.shopinar.models;

public class UpdateProfile {

    String mUserID;
    String mImageURL;

    public UpdateProfile(){
        // empty constructor needed
    }

    public UpdateProfile(String mUserID, String mImageURL) {
        this.mUserID = mUserID;
        this.mImageURL = mImageURL;
    }

    public String getmUserID() {
        return mUserID;
    }

    public void setmUserID(String mUserID) {
        this.mUserID = mUserID;
    }

    public String getmImageURL() {
        return mImageURL;
    }

    public void setmImageURL(String mImageURL) {
        this.mImageURL = mImageURL;
    }
}
