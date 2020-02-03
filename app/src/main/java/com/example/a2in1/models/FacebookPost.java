package com.example.a2in1.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class FacebookPost implements Parcelable {

    private String message;

    private String picture;

    private String[] messageTags;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String[] getMessageTags() {
        return messageTags;
    }

    public void setMessageTags(String[] messageTags) {
        this.messageTags = messageTags;
    }

    public FacebookPost(String message, String picture, String messageTags) {
        this.message = message;
        this.picture = picture;
        String[] strings = new String[messageTags.split(",").length];
        strings[0] = messageTags.replace(","," ");

        this.messageTags = strings;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.message);
        dest.writeString(this.picture);
        dest.writeStringArray(this.messageTags);
    }

    protected FacebookPost(Parcel in) {
        this.message = in.readString();
        this.picture = in.readString();
        this.messageTags = in.createStringArray();
    }

    public static final Creator<FacebookPost> CREATOR = new Creator<FacebookPost>() {
        @Override
        public FacebookPost createFromParcel(Parcel source) {
            return new FacebookPost(source);
        }

        @Override
        public FacebookPost[] newArray(int size) {
            return new FacebookPost[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return String.format("Message:" + getMessage()+ "\nPicture URL: " + getPicture() + "\nHashTags: " + Arrays.toString(messageTags));
    }
}