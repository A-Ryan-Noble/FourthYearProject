package com.example.a2in1.models;

public class TwitterPost extends Post {

    private String nameOfUser;

    public String getNameOfUser() {
        return nameOfUser;
    }

    public void setNameOfUser(String nameOfUser) {
        this.nameOfUser = nameOfUser;
    }

    public TwitterPost(String message, String picture, String messageTags, String links){
        super(message,picture,messageTags,links);
    }

    public TwitterPost(String message, String picture, String messageTags, String links, String nameOfUser){
        super(message,picture,messageTags,links);
        this.nameOfUser = nameOfUser;
    }
}