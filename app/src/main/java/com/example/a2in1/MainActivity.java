package com.example.a2in1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView loginResult;

    private static final int FB_CODE = 1;
    private static final int TWITTER_CODE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginResult = findViewById(R.id.btnsClickResult);
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

        /*if (resultCode == RESULT_OK) {
            if (requestCode == FB_CODE) {
            else if (intent.getStringExtra("result") == "Failed") {
//            fbFail = true;
                } else if (intent.getStringExtra("result") == "Cancelled") {

                } else {
                    // logged in
                    loginResult.setText("Facbook logged in");
                }
            }

            if (requestCode == TWITTER_CODE) {
                if (intent.getStringExtra("result") == "Failed") {
//                twitFail = true;
                } else if (intent.getStringExtra("result") == "Cancelled") {

                } else {
                    // logged in
                }
            }
        }*/

        if (resultCode == RESULT_CANCELED | resultCode != RESULT_OK) {

        }
        else {
            if (requestCode == FB_CODE) {
                if (intent.getStringExtra("result") == "Failed") {
//                fbFail = true;
                } else if (intent.getStringExtra("result") == "Cancelled") {

                }
            } else {
                // logged in
            }
        }
        if (requestCode == TWITTER_CODE) {
            if (intent.getStringExtra("result") == "Failed") {
//                    twitFail = true;
            } else if (intent.getStringExtra("result") == "Cancelled") {

            } else {
                // logged in
            }
        }
        final GlobalVariables globalVar = (GlobalVariables) getApplicationContext();

        if (globalVar.getTwitterSessionId()>0){
            Toast.makeText(this, "Twitter id = " + globalVar.getTwitterSessionId(), Toast.LENGTH_SHORT).show();
            loginResult.setText("Twitter id = " + globalVar.getTwitterSessionId());
        }
//        if (!globalVar.getFbSignedIn() && !globalVar.getTwitterSignedIn()){
        //if (!globalVar.getFbSignedIn() &&!globalVar.getTwitterSignedIn()){
        //  Toast.makeText(this, "Not Signed in on Facebook or Twitter", Toast.LENGTH_SHORT).show();
        //}
//        else
//        if (globalVar.getFbSignedIn()){
//            Toast.makeText(this, "Facebook not Signed in", Toast.LENGTH_SHORT).show();
//                loginResult.setText("Facebook Signed in");
//        }
    /*    else if (!globalVar.getTwitterSignedIn()){
            Toast.makeText(this, "Twitter not Signed in", Toast.LENGTH_SHORT).show();
        }*/
    }
}