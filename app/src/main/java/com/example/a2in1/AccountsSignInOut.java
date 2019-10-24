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

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Arrays;

public class AccountsSignInOut extends AppCompatActivity {


    private CallbackManager callbackManager;
    private Intent returnIntent;
    private LoginButton fbLoginBtn;
    private GlobalVariables globalVar;
    private boolean isLoggedIn;
    private TextView loginLogoutTxt;

    private static final String EMAIL = "email";

    private TwitterLoginButton twitterLoginBtn;
    private TwitterSession session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Configures twitter sdk
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.twitter_CONSUMER_KEY), getResources().getString(R.string.twitter_CONSUMER_SECRET));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();
        Twitter.initialize(twitterConfig);

        setContentView(R.layout.activity_accounts_sign_in_out);

        loginLogoutTxt = findViewById(R.id.loginTxtView);

        fbLoginBtn = findViewById(R.id.fbLoginButton);
        fbLoginBtn.setReadPermissions(Arrays.asList("email", "public_profile"));

        twitterLoginBtn = (TwitterLoginButton) findViewById(R.id.login_button);


        globalVar = (GlobalVariables) getApplicationContext();
        returnIntent = new Intent();

        fbAccount();twitterAccount();

        accountLoggedIn();
    }
    protected void fbAccount() {
        callbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        isLoggedIn = accessToken != null && !accessToken.isExpired();

        // This allows for: Then you can later perform the actual login, such as in a custom button's OnClickListener:
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                returnIntent.putExtra("result", "Successful");
                setResult(RESULT_OK, returnIntent);
                globalVar.setFbSignedIn(true);
                finish();
            }

            @Override
            public void onCancel() {
                returnIntent.putExtra("result", "Cancelled");
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }

            @Override
            public void onError(FacebookException error) {
                returnIntent.putExtra("result", "Failed");
                setResult(RESULT_CANCELED,returnIntent);

                Log.e("Exception",error.toString());
                finish();
            }
        });

        // Used for dealing for when logout button is clicked
        new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    globalVar.setFbSignedIn(false);
                    signOutAlert(getResources().getString(R.string.fb));
                }
            }
        };
    }

    protected void twitterAccount(){
        twitterLoginBtn.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession twitterSession = result.data;

                session = TwitterCore.getInstance().getSessionManager().getActiveSession();

                TwitterAuthToken authToken = session.getAuthToken();

                String token = authToken.token;
                String secret = authToken.secret;

                System.out.println(session.getId());

                globalVar.setTwitterSignedIn(true);

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
    }

    private void signOutAlert(final String accountName) {
        if (accountName.equals(getResources().getString(R.string.fb))){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getResources().getString(R.string.confirmTitle));
            builder.setMessage(getResources().getString(R.string.loggingOut) + " " + accountName);
            builder.show();
            Log.d("Logout", accountName + " Logout");

        } else if (accountName.equals(getResources().getString(R.string.twtr))){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getResources().getString(R.string.confirmTitle));
            builder.setMessage(getResources().getString(R.string.loggingOut) + " " + accountName);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    TwitterCore.getInstance().getSessionManager().clearActiveSession();
                    globalVar.setTwitterSignedIn(false);
                    setResult(RESULT_OK, returnIntent);
                    Log.d("Logout", accountName + " Logout");
//                    finish();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {/*Does Nothing*/}
            });
            builder.show();
        }
    }

    public void goBack(View view) {
        if ((globalVar.getFbSignedIn() && globalVar.getTwitterSignedIn())) {
            returnIntent.putExtra("result", "Successful");
            setResult(RESULT_OK, returnIntent);

        } else if ((globalVar.getFbSignedIn() | globalVar.getTwitterSignedIn())) {
            returnIntent.putExtra("result", "Successful");
            setResult(RESULT_OK, returnIntent);

        } else {
            returnIntent.putExtra("result", "Cancelled");
            setResult(RESULT_OK, returnIntent);
        }
        finish();
    }

    private void accountLoggedIn(){
        // if both Twitter & Facebook are Logged in
        if (isLoggedIn && checkTwitterLogin()){
            globalVar.setFbSignedIn(true);
            globalVar.setTwitterSignedIn(true);
            loginLogoutTxt.setText(getResources().getString(R.string.signOutMsg));
        }
        // if Facebook is only logged out
        if(!isLoggedIn && checkTwitterLogin()){
            globalVar.setFbSignedIn(false);
            globalVar.setTwitterSignedIn(true);
            loginLogoutTxt.setText(getResources().getString(R.string.signOutMsg));
        }
        // if Twitter is logged out
        if (isLoggedIn && !checkTwitterLogin()){
            globalVar.setFbSignedIn(true);
            globalVar.setTwitterSignedIn(false);
            loginLogoutTxt.setText(getResources().getString(R.string.signOutMsg));
        }
        // both are logged out
        else {
            globalVar.setFbSignedIn(false);
            globalVar.setTwitterSignedIn(false);
            loginLogoutTxt.setText(getResources().getString(R.string.signInMsg));
        }
    }

    public boolean checkTwitterLogin(){

        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if ( session != null){
            final Button signOut = (Button)findViewById(R.id.logout_button);

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
                // User clicked ok & is logged out of account
                public void onClick(View v) {
                    signOutAlert(getResources().getString(R.string.twtr));
                }
            });
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}