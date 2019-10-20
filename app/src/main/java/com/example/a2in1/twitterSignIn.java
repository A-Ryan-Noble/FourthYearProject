package com.example.a2in1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class twitterSignIn extends AppCompatActivity {

    private TwitterLoginButton twitterLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_sign_in);

        twitterLoginBtn = findViewById(R.id.twitterLoginButton);

        //callback registration
        twitterLoginBtn.setCallback(new Callback<TwitterSession>() {

            Intent returnIntent = new Intent();

            @Override
            public void success(Result<TwitterSession> result) {
                // App code here
                returnIntent.putExtra("result", "Successful");
                setResult(RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void failure(TwitterException exception) {
                returnIntent.putExtra("result", "Failed");
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    public void goBack(){
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Passes the activity result to the login button.
        twitterLoginBtn.onActivityResult(requestCode, resultCode, data);
    }
}