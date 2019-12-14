package com.example.a2in1.ui.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.a2in1.MainActivity;
import com.example.a2in1.R;
import com.example.a2in1.TwitterSignIn;
import com.example.a2in1.TwitterSignOut;
import com.example.a2in1.myPreferences;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class TwitterSignInFragment extends Fragment {

    private String log = getClass().getSimpleName();

    private static final int TWITTER_CODE = 2;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_twitter_sign_in, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //Configures twitter sdk
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.twitter_CONSUMER_KEY), getResources().getString(R.string.twitter_CONSUMER_SECRET));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(getContext())
                .twitterAuthConfig(authConfig)
                .build();
        Twitter.initialize(twitterConfig);

        super.onActivityCreated(savedInstanceState);

        // if user isnt logged in, it calls activity to be able to login
        if (TwitterCore.getInstance().getSessionManager().getActiveSession() == null) {
            Intent login = new Intent(getContext(), TwitterSignIn.class);
            startActivityForResult(login, TWITTER_CODE);
        }
        // else calls the activity to be able to logout
        else {
            Intent signOut = new Intent(getContext(), TwitterSignOut.class);
            startActivityForResult(signOut, TWITTER_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == TWITTER_CODE) {
            if (intent.getStringExtra("result") == "Failed") {
                Log.d(log, "Twitter login Activity: Failed");
            } else if (intent.getStringExtra("result") == "Cancelled") {
                Log.d(log, "Twitter login Activity: Cancelled");
            } else if ((intent.getStringExtra("result") == "LoggedIn")) {
                Log.d(log, "Twitter Activity: Logged In");
            } else {
                Log.d(log, "Twitter Activity: Logged out");
            }
            // Acts as a fragment refresher
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
}