package com.example.a2in1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class TwitterSignIn extends AppCompatActivity {

    private TextView txtView;
    private TwitterLoginButton twitterLoginBtn;
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
        txtView = findViewById(R.id.loginTxtView);

        final GlobalVariables globalVar = (GlobalVariables) getApplicationContext();

        Boolean f =checkLoggedIn();

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
                globalVar.setTwitterSignedIn(false);
            }
        });

        if (checkLoggedIn()){
            txtView.setText(getResources().getString(R.string.signedIn)+ "\nTwitter");
            globalVar.setTwitterSignedIn(true);
        }
        else {
            txtView.setText(getResources().getString(R.string.signInMsg));
            globalVar.setTwitterSignedIn(false);
        }
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
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    public boolean checkLoggedIn(){

        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if ( session != null){
            Button signOut = (Button)findViewById(R.id.logout_button);

             /*
                Switches the available buttons if user is logged in they can logout.
                    The opposite is also true
              */
            twitterLoginBtn.setClickable(false);
            twitterLoginBtn.setVisibility(View.INVISIBLE);
            signOut.setClickable(true);
            signOut.setEnabled(true);
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    twitterSignOut();
                }
            });
            return true;
        }
        return false;
    }

    private void twitterSignOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Alert");
        builder.setMessage("Signing out of Twitter");
        builder.setPositiveButton(R.string.ok,  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked ok & is logged out of twitter
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
                Log.d("Twitter","Twitter Logout");

                GlobalVariables gv = (GlobalVariables)getApplicationContext();
                gv.setTwitterSignedIn(false);
                gv.setTwitterSessionId(0);

                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        builder.show();
    }
}