package com.example.a2in1;

import android.app.Application;

public class GlobalVariables extends Application {
    private Boolean fbSignedIn = false;
    private Boolean twitterSignedIn = false;

    public Boolean getFbSignedIn() {
        return fbSignedIn;
    }

    public void setFbSignedIn(Boolean fbSignedIn) {
        this.fbSignedIn = fbSignedIn;
    }

    public Boolean getTwitterSignedIn() {
        return twitterSignedIn;
    }

    public void setTwitterSignedIn(Boolean twitterSignedIn) {
        this.twitterSignedIn = twitterSignedIn;
    }
}
