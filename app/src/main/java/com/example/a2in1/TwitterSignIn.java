package com.example.a2in1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class TwitterSignIn extends AppCompatActivity {

    private TwitterLoginButton twitterLoginBtn;
    //    private TwitterAuthClient twitterAuthClient;
    private TwitterSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_sign_in);

        //Configures twitter sdk
        TwitterAuthConfig authConfig=new TwitterAuthConfig(getResources().getString(R.string.twitter_CONSUMER_KEY),getResources().getString(R.string.twitter_CONSUMER_SECRET));

        TwitterConfig twitterConfig=new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();
        Twitter.initialize(twitterConfig);

        twitterLoginBtn = (TwitterLoginButton) findViewById(R.id.login_button);
        Button goBack = findViewById(R.id.mainMenu);

        final GlobalVariables globalVar = (GlobalVariables) getApplicationContext();

        Boolean f =checkLoggedIn(globalVar);

        twitterLoginBtn.setCallback(new Callback<TwitterSession>() {
            Intent returnIntent = new Intent();

            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession twitterSession = result.data;

                session = TwitterCore.getInstance().getSessionManager().getActiveSession();

                //                    TwitterCore.getInstance().getSessionManager().clearActiveSession();

                TwitterAuthToken authToken = session.getAuthToken();

                String token = authToken.token;
                String secret = authToken.secret;

                System.out.println(session.getId());

                globalVar.setTwitterSignedIn(true);
                globalVar.setTwitterSessionId(session.getId());

                returnIntent.putExtra("result", "Successful");
                setResult(RESULT_OK, returnIntent);
                globalVar.setTwitterSignedIn(true);

                finish();
            }

            @Override
            public void failure(TwitterException exception) {
                returnIntent.putExtra("result", "Failed");
                setResult(RESULT_OK, returnIntent);
//                final GlobalVariables globalVar = (GlobalVariables) getApplicationContext();
                globalVar.setTwitterSignedIn(false);
            }
        });
//        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Passes the activity result to the login button.
        twitterLoginBtn.onActivityResult(requestCode, resultCode, data);
    }

    public void goBack(View view){
        Intent returnIntent = new Intent();

        returnIntent.putExtra("result", "Cancelled");
        setResult(RESULT_OK, returnIntent);

        finish();

    }

    public boolean checkLoggedIn(GlobalVariables globalVar){
        if ( globalVar.getTwitterSignedIn() != null){
            System.out.println("USER LOGGED IN ALREADY");

            twitterLoginBtn.setClickable(false);

            // Make it so you cant login again / it tells you your already logged in
            return true;
        }
        System.out.println("USER NOT!!! LOGGED IN ALREADY");
        return false;
    }
}