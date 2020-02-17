package com.example.a2in1.ui.twitter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a2in1.MainActivity;
import com.example.a2in1.R;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;

import static com.example.a2in1.myPreferences.setBoolPref;

public class TwitterSignOut extends AppCompatActivity {

    private Intent returnIntent;
    private String log = getClass().getSimpleName();

    private Context context = getBaseContext();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Configures twitter sdk
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.twitter_CONSUMER_KEY), getResources().getString(R.string.twitter_CONSUMER_SECRET));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();
        Twitter.initialize(twitterConfig);

        setContentView(R.layout.activity_twitter_sign_out);

        setTitle(getTitle().toString() + " Sign out" );

        returnIntent = new Intent(this, TwitterSignInOutFragment.class);

        Button signOutBtn = findViewById(R.id.logout_button);
        signOutBtn.setEnabled(true);

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterSignOut();
            }
        });
    }

    private void twitterSignOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.confirmTitle));
        builder.setMessage(getResources().getString(R.string.loggingOut) + " Twitter");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            // User clicked ok & is logged out of twitter
            public void onClick(DialogInterface dialog, int id) {
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
                // Logged in status put into SharedPreferences for later
                setBoolPref("TwitterLoggedIn",false, context);

                Log.d(log, "Signed out of Twitter");
                returnIntent.putExtra("result", "LoggedOut");
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.show();
    }

    // customised callback of phone back button
    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, MainActivity.class));
    }
}