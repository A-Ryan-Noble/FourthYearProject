package com.example.a2in1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView loginResult;

    private static final int FB_CODE = 1;
    private static final int TWITTER_CODE = 2;
    private static final int BOTH_ACCOUNTS_CODE = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main01);

        loginResult = findViewById(R.id.btnsClickResult);

        checkLogin();
    }
    public void signInOut(View view){
        Intent socialsAcc = new Intent(this,AccountsSignInOut.class);
        startActivityForResult(socialsAcc,BOTH_ACCOUNTS_CODE);
    }

    public void fbBtnClick(View view) {
        Intent fbIntent = new Intent(this, FacebookSignIn.class);
        startActivityForResult(fbIntent, FB_CODE);
    }


    public void twitterBtnClick(View view) {
        Intent twitterIntent = new Intent(this, TwitterSignIn.class);
        startActivityForResult(twitterIntent, TWITTER_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_CANCELED | resultCode != RESULT_OK) {
            Log.d(null, "Activity returned: Not Okay / Cancelled");
        } else {
            if (requestCode == BOTH_ACCOUNTS_CODE) {
                if (intent.getStringExtra("result") == "Failed") {
                    Log.d(null, "Accounts Activity: Failed");
                } else {
                    // logged in
                    Log.d(null, "Account(S) Activity: BothLogged In");
                }
            }
            /*if (requestCode == FB_CODE) {
                if (intent.getStringExtra("result") == "Failed") {
                    Log.d(null, "Facebook Activity: Failed");
                } else {
                    // logged in
                    Log.d(null, "Twitter Activity: Logged In");
                }
            }

            if (requestCode == TWITTER_CODE) {
                if (intent.getStringExtra("result") == "Failed") {
                    Log.d(null, "Twitter Activity: Failed");
                } else if (intent.getStringExtra("result") == "Cancelled") {

                } else {
                    // logged in
                    Log.d(null, "Twitter Activity: Logged In");
                }
            }*/
        }
        checkLogin();
    }

    private void checkLogin() {
        final GlobalVariables globalVar = (GlobalVariables) getApplicationContext();

        // if both are Signed in
        if (globalVar.getFbSignedIn() && globalVar.getTwitterSignedIn()) {
            loginResult.setText("Facebook & Twitter are Logged in");
        } else{
            // if facebook is only signed in
            if (globalVar.getFbSignedIn() && !globalVar.getTwitterSignedIn()) {
                loginResult.setText("Facebook Signed in");
            }
            // if Twitter is only signed in
            if (!globalVar.getFbSignedIn() && globalVar.getTwitterSignedIn()) {
                loginResult.setText("Twitter Signed in");
            }
            // Both aren't signed in
            else if (!globalVar.getFbSignedIn() && !globalVar.getTwitterSignedIn()) {
                loginResult.setText("Sign in through the buttons above");
            }
        }
    }
}