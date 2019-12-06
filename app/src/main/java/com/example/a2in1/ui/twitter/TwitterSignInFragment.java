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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.example.a2in1.MainActivity;
import com.example.a2in1.R;
import com.example.a2in1.TwitterSignIn;
import com.example.a2in1.TwitterSignOut;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class TwitterSignInFragment extends Fragment {

    private static String tag;
    private static final int TWITTER_CODE = 2;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_twitter_sign_in, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tag = getClass().getName();

        ViewModel root = ViewModelProviders.of(this).get(TwitterViewModel.class);

        //Configures twitter sdk
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.twitter_CONSUMER_KEY), getResources().getString(R.string.twitter_CONSUMER_SECRET));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(getContext())
                .twitterAuthConfig(authConfig)
                .build();
        Twitter.initialize(twitterConfig);

        if (TwitterCore.getInstance().getSessionManager().getActiveSession() == null) {
            Intent twitterIntent = new Intent(getContext(), TwitterSignIn.class);
            startActivityForResult(twitterIntent, TWITTER_CODE);
        } else {
            Intent signOutTwitterIntent = new Intent(getContext(), TwitterSignOut.class);
            startActivityForResult(signOutTwitterIntent, TWITTER_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_CANCELED | resultCode != RESULT_OK) {
            Log.d(tag, "Activity returned: Not Okay / Cancelled");
            startActivity(new Intent(getContext(), MainActivity.class));
        } else {
            if (requestCode == TWITTER_CODE) {
                if (intent.getStringExtra("result") == "Failed") {
                    Log.d(tag, "Twitter login Activity: Failed");
                } else if (intent.getStringExtra("result") == "Cancelled") {
                    Log.d(tag, "Twitter login Activity: Cancelled");
                } else if ((intent.getStringExtra("result") == "LoggedIn")) {
                    Log.d(tag, "Twitter Activity: Logged In");
                } else {
                    Log.d(tag, "Twitter Activity: Logged out");
                }
                // Acts as a fragment refresher
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            }
        }
    }
}