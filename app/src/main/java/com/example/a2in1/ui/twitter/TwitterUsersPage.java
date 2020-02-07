package com.example.a2in1.ui.twitter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.a2in1.R;
import com.example.a2in1.api.MyTwitterApiClient;
import com.example.a2in1.feeds.DBHelper;
import com.example.a2in1.models.TwitterPost;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.a2in1.myPreferences.getBoolPref;
import static com.example.a2in1.myPreferences.getIntPref;

public class TwitterUsersPage extends Fragment {

    private String log = getClass().getSimpleName();

    private DBHelper dbHelper;

    private ListView list;

    private String[] userPosts;
    private String[] imageUrl;
    private String[] msgTags;
    private String[] linkUrl;

    private TwitterPost[] twitterPosts;

    private Context context;

    private int limit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_posts_from_user, container, false);

        context = getContext();

        boolean isTwitterLoggedIn = getBoolPref("TwitterLoggedIn", false, context);

        if (isTwitterLoggedIn) {
            list = root.findViewById(R.id.postsList);

            final Button refreshBtn = root.findViewById(R.id.refreshBtn);
            refreshBtn.setText(R.string.downloadFeed);

            // Gets value from the the SharedPreferences
            limit = getIntPref("MaxFbNum",5,context);

            if (limit > 20) {
                limit = 20;
            }

            SQLiteDatabase DB = context.openOrCreateDatabase("feeds", MODE_PRIVATE, null);

            if (!DB.isOpen()) {
                DB = context.openOrCreateDatabase("feeds", MODE_PRIVATE, null);
            }

            dbHelper = new DBHelper(context);

            final int twitterAmount = dbHelper.getAllOfSite("Twitter").getCount();

            userPosts = new String[limit];
            imageUrl = new String[limit];
            msgTags = new String[limit];
            linkUrl = new String[limit];

//            if (twitterAmount == 0){
//                twitterPosts = new TwitterPost[limit];
//
//                refreshBtn.setText(R.string.refresh);
//
//                getFeed();
//
//            }
//            else {
//                refreshBtn.setText(R.string.refresh);
//
//    //            UI is updated from the contents in the database
//                twitterPosts = dbHelper.getAllTwitter();
//
////                    UpdateUI(twitterPosts);
//            }
            getFeed();
        }

        // if user isn't logged in on twitter then go to the sign in fragment
        else {
            startActivity(new Intent(getContext(), TwitterSignIn.class));
        }
        return root;

    }

    private void getFeed(){

        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.twitter_CONSUMER_KEY), getResources().getString(R.string.twitter_CONSUMER_SECRET));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(getActivity())
                .twitterAuthConfig(authConfig)
                .build();
        Twitter.initialize(twitterConfig);


        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        /* Call to get from the users profile page:
            - see TTWITTER jsonformatter.txt on desktop for output.
         */
        new MyTwitterApiClient(session).getApiInterface().show("/1.1/statuses/user_timeline.json",session.getUserId(),1)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                Log.d(log,response.body().string());
                            } catch (IOException e) {
                                Log.e(log, e.getMessage());
                            }
                        }
                        else {
                            Log.e(log,"Call unsuccessful");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(log,"Call Failed" + t.getMessage());
                    }
                });

    }
}