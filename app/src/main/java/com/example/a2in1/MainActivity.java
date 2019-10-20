package com.example.a2in1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
        Intent fbIntent = new Intent(this, facebookSignIn.class);
        startActivityForResult(fbIntent, FB_CODE);
    }


    public void twitterBtnClick(View view) {
        Intent twitterIntent = new Intent(this, twitterSignIn.class);
        startActivityForResult(twitterIntent, TWITTER_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Boolean fbFail = false, twitFail = false;

        if (resultCode == RESULT_CANCELED | resultCode != RESULT_OK) {

        } else {
            if (intent.getStringExtra("result") == "Failed") {
                fbFail = true;
            } else if (intent.getStringExtra("result") == "Cancelled") {

            } else {
                // logged in
            }

            if (requestCode == TWITTER_CODE) {
                if (intent.getStringExtra("result") == "Failed") {
                    twitFail = true;
                } else if (intent.getStringExtra("result") == "Cancelled") {

                } else {
                    // logged in
                }
            }
        }

        if (fbFail && twitFail){
            loginResult.setText("Facebook & Twitter Logins failed");
        }
    }
}