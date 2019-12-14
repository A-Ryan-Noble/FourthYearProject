package com.example.a2in1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.a2in1.ui.twitter.TwitterSignInFragment;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import static com.example.a2in1.myPreferences.setBoolPref;

public class TwitterSignIn extends AppCompatActivity {

    private TwitterLoginButton twitterLoginBtn;

    private String log = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Configures twitter sdk
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.twitter_CONSUMER_KEY), getResources().getString(R.string.twitter_CONSUMER_SECRET));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();
        Twitter.initialize(twitterConfig);

        setContentView(R.layout.activity_twitter_sign_in);

        setTitle(getTitle().toString() + " Sign in");

        final Intent returnIntent = new Intent(this, TwitterSignInFragment.class);

        twitterLoginBtn = (TwitterLoginButton) findViewById(R.id.login_button);

        twitterLoginBtn.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(log,"Successful Login");

                //Logged In Added to SharedPreferences for later
                setBoolPref("TwitterLoggedIn",true, getBaseContext());

                returnIntent.putExtra("result", "LoggedIn");
                setResult(RESULT_OK, returnIntent);
                finish();
            }
            @Override
            public void failure(TwitterException exception) {
                Log.e(log,"login failed");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Passes the activity result to the login button.
        twitterLoginBtn.onActivityResult(requestCode, resultCode, data);
    }
}