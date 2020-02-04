package com.example.a2in1.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class Post implements Parcelable {

    private String message;

    private String picture;

    private String[] messageTags;

    private String links;

    public String getMessage() {
        return message;
    }

    public String getPicture() {
        return picture;
    }

    public String[] getMessageTags() {
        return messageTags;
    }

    public String getLinks() {
        return links;
    }

    public Post(String message, String picture, String messageTags, String links) {
        this.message = message;
        this.picture = picture;
        String[] strings = new String[messageTags.split(",").length];
        strings[0] = messageTags.replace(","," ");

        this.messageTags = strings;
        this.links = links;
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
        dest.writeString(this.links);
    }

    protected Post(Parcel in) {
        this.message = in.readString();
        this.picture = in.readString();
        this.messageTags = in.createStringArray();
        this.links = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return String.format("Message:" + getMessage()+ "\nPicture URL: " + getPicture() +
                "\nHashTags: " + Arrays.toString(messageTags) +"\nLinks: " + getLinks());
    }
}
