package com.example.a2in1.ui.twitter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.a2in1.ListAdaptTweets;
import com.example.a2in1.Notifications;
import com.example.a2in1.R;
import com.example.a2in1.api.MyTwitterApiClient;
import com.example.a2in1.feeds.DBHelper;
import com.example.a2in1.fragmentRedirects.FeedItemView;
import com.example.a2in1.models.TwitterPost;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.a2in1.myPreferences.getBoolPref;
import static com.example.a2in1.myPreferences.getIntPref;

public class TwitterUsersTimeline extends Fragment {

    private final String log = getClass().getSimpleName();

    private DBHelper dbHelper;

    private ListView list;

    private String[] userPosts;
    private String[] linkUrl;
    private String[] userNameOfPost;

    private TwitterPost[] twitterPosts;

    private Context context;

    private int limit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_timeline_of_user, container, false);

        context = getContext();

        boolean isTwitterLoggedIn = getBoolPref("TwitterLoggedIn", false, context);

        if (isTwitterLoggedIn) {
            list = root.findViewById(R.id.postsList);

            final Button refreshBtn = root.findViewById(R.id.refreshBtn);
            refreshBtn.setText(getResources().getString(R.string.refresh) + " Twitter");

            // Gets value from the the SharedPreferences
            limit = getIntPref("MaxTweetsNum",5,context);

            if (limit > 20) {
                limit = 20;
            }

            SQLiteDatabase DB = context.openOrCreateDatabase("feeds", MODE_PRIVATE, null);

            if (!DB.isOpen()) {
                DB = context.openOrCreateDatabase("feeds", MODE_PRIVATE, null);
            }

            dbHelper = new DBHelper(context);

            int twitterTimelineAmount = dbHelper.getAllOfSite("TwitterTimeline").getCount();

            if(twitterTimelineAmount == 0){
                // Used to store the message. If no message then "None" is at index
                userPosts = new String[limit];

                // Used to store the linkUrl attached to the Twitter Tweet
                linkUrl = new String[limit];

                // Used to store a given Message owner. If message doesn't have a log then "None" is at the index
                userNameOfPost = new String[limit];

                twitterPosts = new TwitterPost[limit];

                getFeed(); // Downloads then calls update of UI
            }
            else {
                userPosts = new String[twitterTimelineAmount];
                userNameOfPost = new String[twitterTimelineAmount];
                linkUrl = new String[twitterTimelineAmount];

                // UI is updated from the contents in the database
                twitterPosts = dbHelper.getAllTwitterTimeline();
                UpdateUI(twitterPosts);
            }

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // if the text of the list item clicked is not empty
                    if (list.getItemAtPosition(position)!= null) {
                        // if the item at the given index text isn't "None"
                        if (!list.getItemAtPosition(position).toString().equals("None")) {

                            // Alerts the user that there isn't a reason to view it in more detail
                            if (userNameOfPost[position].equals("None")&& linkUrl[position].equals("None")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                builder.setMessage("There is only this text content for this item");
                                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                                builder.show();
                            } else {
                                Intent itemView = new Intent(context, FeedItemView.class);
                                itemView.putExtra("msg", userPosts[position]);
                                itemView.putExtra("userNameOfPost", userNameOfPost[position]);
                                itemView.putExtra("link", linkUrl[position]);

                                startActivity(itemView);
                            }
                        }
                    }
                }
            });
            // Re-downloads the list
            refreshBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Wish to refresh all of the " + dbHelper.getAllOfSite("TwitterTimeline").getCount() + " tweets?");
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dbHelper.deleteSiteData("TwitterTimeline"); // Empties the database of Twitter Tweets

                            getFeed();

                            reloadFrag(); // Fragment is reloaded
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    builder.show();
                }
            });
        }

        // if user isn't logged in on fb then go to the sign in fragment
        else  {
            startActivity(new Intent(getContext(), TwitterSignIn.class));
        }
        return root;
    }

    private void reloadFrag(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    private void downloadFeed(String feed) {

//        Log.d("ZZZ",feed);
        try {
            JSONArray tweetsArray = new JSONArray(feed);

            for (int i = 0; i < tweetsArray.length(); i++) {

                String msg = "None";
                String postedBy = "None";
                String linkUrl = "None";

                try {
                    JSONObject tweetFromArr = tweetsArray.getJSONObject(i);

                    // This gets only the message part of the array
                    try {
                        msg = tweetFromArr.getString("text");

                        // overrides the message with the message without the http link attached
                        if (msg.contains("http")) {
                            String[] msgRep = msg.split("http");

                            msg = msgRep[0];
                            linkUrl=msgRep[1];

                            Log.d("ZZZ","link is now:\n"+linkUrl);
                        }
                    } catch (JSONException e) {
                        Log.d(log, "Message not found at index " + i);
                    }

                    // Tries to get the url  of the post
                    try {
                        JSONObject entitiesObj = tweetFromArr.getJSONObject("entities");

                        for (int j = 0; j < entitiesObj.length(); j++) {
                            try {
                                JSONArray urlArr = entitiesObj.getJSONArray("urls");

                                linkUrl = urlArr.getJSONObject(0).getString("expanded_url");
                            } catch (JSONException e) {
                                Log.d(log, "Url not found in url array");
                            }
                        }
                    } catch (JSONException e) {
                        Log.d(log, "Entities not found at index " + i);
                    }

                    try {
                        JSONObject userObj = tweetFromArr.getJSONObject("user");

                        for (int j = 0; j < userObj.length(); j++) {

                            // Tries to get the name of the poster
                            try {
                                postedBy = userObj.getString("name");
                            } catch (JSONException e) {
                                Log.d(log, "Poster name not found at index " + j);
                            }
                        }
                    } catch (JSONException e) {
                        Log.d(log, "User not found");
                    }
                } catch (JSONException e) {
                    Log.d(log, "Unable to get Json Object at index " + i);
                }

//                Log.d(log,"msg: "+ msg + " link url: "+ linkUrl + ", posted by: " + postedBy);

                twitterPosts[i] = new TwitterPost(msg, "None", "None", linkUrl, postedBy);

                dbHelper.insertIntoDB2("TwitterTimeline", msg, postedBy, linkUrl);
            }
        } catch (JSONException e) {
            Log.e(log, e.getMessage());
        }
    }

    private void UpdateUI(TwitterPost[] posts){

        for (int i = 0; i< posts.length; i++) {

            try {
                userPosts[i] = posts[i].getMessage();
            }catch (NullPointerException e){
                Log.d(log,e.getMessage());
                userPosts[i] = "None";
            }

            try {
                userNameOfPost[i] = posts[i].getNameOfUser();
            }catch (NullPointerException e){
                Log.d(log,"user name");
                userNameOfPost[i] = "None";
            }

            try {
                linkUrl[i] = posts[i].getLinks();
            } catch (NullPointerException e) {
                Log.d(log, e.getMessage());
                linkUrl[i] = "None";
            }

            ListAdaptTweets adapt = new ListAdaptTweets(getActivity(),userPosts,userNameOfPost);
            adapt.notifyDataSetChanged();

            list.invalidateViews();
            list.setAdapter(adapt);
        }
    }

    private void getFeed(){

        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.twitter_CONSUMER_KEY), getResources().getString(R.string.twitter_CONSUMER_SECRET));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(getActivity())
                .twitterAuthConfig(authConfig)
                .build();
        Twitter.initialize(twitterConfig);

        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        Call<ResponseBody> call = new MyTwitterApiClient(session).getApiInterface().getTweets("/1.1/statuses/home_timeline.json",limit);

        // Call to get from the user's page:
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    // Notifies user, parses the data and sets it to arrays which can use to update the User Interface
                    try {
                        Notifications.notifyDownload("Latest " + limit + " tweets from Twitter were ",context,789);

                        downloadFeed(response.body().string());

                        UpdateUI(twitterPosts);
                    }
                    catch (IOException e){
                        Log.e(log,e.getMessage());
                    }
                }
                else {
                    Log.e(log, "Failed to get Tweets");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e(log, "Call Failed\n" + throwable.getMessage());
            }
        });
    }
}