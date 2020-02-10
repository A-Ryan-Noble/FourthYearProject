package com.example.a2in1.ui.twitter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.a2in1.ListAdapt;
import com.example.a2in1.MainActivity;
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

public class TwitterUsersPage extends Fragment {

    private final String log = getClass().getSimpleName();

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
        View root = inflater.inflate(R.layout.fragment_posts_from_user_two, container, false);

        context = getContext();

        boolean isTwitterLoggedIn = getBoolPref("TwitterLoggedIn", false, context);

        if (isTwitterLoggedIn) {
            list = root.findViewById(R.id.postsList);

            final Button refreshBtn = root.findViewById(R.id.refreshBtn);
            refreshBtn.setText(R.string.refresh);

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

            final int twitterAmount = dbHelper.getAllOfSite("Twitter").getCount();

            // Used to store the message. If no message then "None" is at index
            userPosts = new String[limit];

            // Used to store the url of an image in a message, if no URL then "None" is at index
            imageUrl = new String[limit];

            // Used to store a given Message tags. If message doesn't have a log then "None" is at the index
            msgTags = new String[limit];

            // Used to store the linkUrl attached to the Twitter Tweet
            linkUrl = new String[limit];

            if (limit != twitterAmount || twitterAmount == 0){
                twitterPosts = new TwitterPost[limit];
                getFeed(); // Downloads then calls update of UI
            }
            else {
                twitterPosts = dbHelper.getAllTwitter(); // UI is updated from the contents in the database
                UpdateUI(twitterPosts);
            }

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (list.getItemAtPosition(position)!= null) {
                        // if the text of the list item clicked is not empty
                        if (!list.getItemAtPosition(position).toString().equals("")) {

                            // Alerts the user that their isnt a reason to view it in more detail
                            if (imageUrl[position].equals("None") && msgTags[position].equals("None")&& !linkUrl[position].equals("None")) {
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
                                itemView.putExtra("tags", msgTags[position]);
                                itemView.putExtra("Url", imageUrl[position]);
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
                        builder.setMessage("Wish to refresh all of the " + twitterAmount + " tweets?");
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dbHelper.deleteSiteData("Twitter"); // Empties the database of Twitter Tweets

                                startActivity(new Intent(context, MainActivity.class));
                                getFeed();
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

    private void downloadFeed(String feed){

        try {
            JSONArray tweetsArr = new JSONArray(feed);

            for (int i = 0; i < tweetsArr.length(); i++) {

                // Obtains the individual tweet from the tweets downloaded
                JSONObject tweetFromFeed = tweetsArr.getJSONObject(i);

                String msg = "None";

                try {
                    msg = tweetFromFeed.getString("text");
                }
                catch (Exception e){
                    Log.e(log,"No message found");
                }

                String tagText = "None";
                String imgUrl = "None";
                String linkUrl= "None";

                try {
                    JSONObject entities = tweetFromFeed.getJSONObject("entities");

                    // Tries to get the attached hashtags text looping through if there's multiple
                    try {
                        JSONArray tags = entities.getJSONArray("hashtags");

                        int tagAmount = tags.length();

                        if (tagAmount>0) {
                            tagText = "";
                        }

                        for (int j = 0; j < tagAmount; j++) {
                            tagText += "#" +tags.getJSONObject(j).getString("text");

                            // Removes the given hashtag from the message
                            if (!msg.equals("None")){
                                msg= msg.replace(tagText,"");
                            }

                            // Add a comma if there is more than one tag then a comma is added as a separator
                            if (tagAmount > 1){
                                tagText+= ",";
                            }
                        }
                    }
                    catch (JSONException e) {
                        Log.e(log, "No tags found");
                        tagText = "None";
                    }

                    // Tries to get the https... styled url link
                    try {
                        JSONArray url = entities.getJSONArray("urls");

                        linkUrl = url.getJSONObject(0).getString("expanded_url");

                        // Removes the given link from the message
                        if (!msg.equals("None")){
                            msg= msg.replace(linkUrl,"");
                        }
                    }
                    catch (JSONException e) {
                        Log.e(log, "No link Url found");
                        linkUrl = "None";
                    }

                    // Tries to get the url link of an attached image media
                    try {
                        JSONArray url = entities.getJSONArray("media");

                        imgUrl = url.getJSONObject(0).getString("media_url_https");

                        // Removes the given imgage's url from the message
                        if (!msg.equals("None")){
                            msg= msg.replace(imgUrl,"");
                        }
                    }
                    catch (JSONException e) {
                        Log.e(log, "No image Url found");
                        imgUrl = "None";
                    }
                }
                catch (JSONException e){
                    Log.e(log,"No entities found");
                }

                if (msg.contains("http")){
                    String[] msgRep = msg.split("http");

                    msg = msgRep[0];
                }

                twitterPosts[i] = new TwitterPost(msg,imgUrl,tagText,linkUrl);

                dbHelper.insertIntoDB("Twitter",msg,imgUrl,tagText,linkUrl);
            }
        }
        catch (JSONException e) {
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
                imageUrl[i] = posts[i].getPicture();
            } catch (NullPointerException e) {
                Log.d(log, e.getMessage());
                imageUrl[i] = "None";
            }

            String hashtags = "";

            try {
                String[] tags = posts[i].getMessageTags();

                // Foreach loop to get each tag and add it to the hashtag string.
                for (String x : tags) {
                    hashtags = x + " ";
                }
                msgTags[i] = hashtags;
            }
            catch (NullPointerException e){
                Log.e(log,e.getMessage());
                msgTags[i] = "None";
            }

            try {
                linkUrl[i] = posts[i].getLinks();
            } catch (NullPointerException e) {
                Log.d(log, e.getMessage());
                linkUrl[i] = "None";
            }

            ListAdapt adapt = new ListAdapt(getActivity(),userPosts,msgTags,"twitter");
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

        Call<ResponseBody> call = new MyTwitterApiClient(session).getApiInterface().show("/1.1/statuses/user_timeline.json",session.getUserId(),limit);

        // Call to get from the user's page:
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    // Notifies user, parses the data and sets it to arrays which can use to update the User Interface
                    try {
                        Notifications.notifyDownload("You feed was ",context,456);

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
      /*  // Call to get from the users profile page:
        new MyTwitterApiClient(session).getApiInterface().show("/1.1/statuses/user_timeline.json",session.getUserId(),1)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            // Notifies user, parses the data and sets it to arrays which can use to update the User Interface
                            try {
                                Notifications.notifyDownload("You feed was ",context);

                                downloadFeed(response.body().string());

                                UpdateUI(twitterPosts);
                            }
                            catch (IOException e){
                                Log.e(log,e.getMessage());
                            }
                        }
                        else {
                            Log.e(log, "Failed to get Posts");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        Log.e(log, "Call Failed\n" + throwable.getMessage());
                    }
                });*/
    }
}