package com.example.a2in1.models;

public class FacebookPost {

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

        this.messageTags = messageTags.split(" ");
    }
}