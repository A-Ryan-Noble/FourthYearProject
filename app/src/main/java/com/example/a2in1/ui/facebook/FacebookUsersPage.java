package com.example.a2in1.ui.facebook;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.a2in1.ListAdapt;
import com.example.a2in1.MainActivity;
import com.example.a2in1.Notifications;
import com.example.a2in1.R;
import com.example.a2in1.api.APIClient;
import com.example.a2in1.api.APIInterface;
import com.example.a2in1.feeds.DBHelper;
import com.example.a2in1.fragmentRedirects.FbSignInActivity;
import com.example.a2in1.fragmentRedirects.FeedItemView;;
import com.example.a2in1.models.FacebookPost;
import com.facebook.AccessToken;
import com.facebook.Profile;

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

public class FacebookUsersPage extends Fragment {

    private String log = getClass().getSimpleName();

    private DBHelper dbHelper;

    private ListView list;

    private String[] userPosts;
    private String[] imageUrl;
    private String[] msgTags;
    private String[] linkUrl;

    private FacebookPost[] facebookPosts;

    private Context context;

    private int limit;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_posts_from_user, container, false);

        context = getContext();

        boolean isFbLoggedIn = getBoolPref("FBLoggedIn", false,context);

        if (isFbLoggedIn) {

            list = root.findViewById(R.id.postsList);

            final Button refreshBtn = root.findViewById(R.id.refreshBtn);
            refreshBtn.setText(R.string.refresh);

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

            final int fbAmount = dbHelper.getAllOfSite("Facebook").getCount();

            // Used to store the message. If there's no message then "None" is at index
            userPosts = new String[limit];

            // Used to store the url of an image in a message, if there's no URL then "None" is at index
            imageUrl = new String[limit];

            // Used to store a given Message tags. If message doesn't have a log then "None" is at the index
            msgTags = new String[limit];

            /* Used to store the linkUrl attached to the facebook post, due to the way Facebook Development kit works it is to note:
                        - If there is an image attached and no linkUrl, this acts as a linkUrl to the image.
                        - if there is both a linkUrl and an image the linkUrl wont be linked to the url of the image.
                        - if there neither a image or a linkUrl is provided then this becomes "None. */
            linkUrl = new String[limit];

            if(fbAmount == 0){
                facebookPosts = new FacebookPost[limit];
                getFeed(); // Downloads then calls update of UI
            }
            else {
                facebookPosts = dbHelper.getAllFacebook(); // UI is updated from the contents in the database
                UpdateUI(facebookPosts);
            }

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // if the text of the list item clicked is not empty
                    if (!list.getItemAtPosition(position).toString().equals("")){

                        // Alerts the user that their isn't a reason to view it in more detail
                        if (imageUrl[position].equals("None") && msgTags[position].equals("None")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            builder.setMessage("There is only this text content for this item");
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            });
                            builder.show();
                        }
                        else {
                            Intent itemView = new Intent(context, FeedItemView.class);
                            itemView.putExtra("msg",userPosts[position]);
                            itemView.putExtra("tags",msgTags[position]);
                            itemView.putExtra("Url",imageUrl[position]);
                            itemView.putExtra("link",linkUrl[position]);

                            startActivity(itemView);
                        }
                    }
                }
            });

            refreshBtn.setOnClickListener(new View.OnClickListener() {

                // Re-downloads the list
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Wish to refresh all of the " + fbAmount + " posts?");
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dbHelper.deleteSiteData("Facebook"); // Empties the database of Facebook posts

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
        else {
            startActivity(new Intent(getContext(), FbSignInActivity.class));
        }
        return root;
    }

    private void downloadFeed(String feed){
        try {
            JSONArray  postsArr =  new JSONObject(feed).getJSONArray("data");

            for (int i = 0; i < postsArr.length(); i++) {

                String msg = "None";

                try {
                    msg = postsArr.getJSONObject(i).getString("message"); // This gets only the message part of the array
                } catch (JSONException e) {
                    Log.e(log, "Message not found at index " + i);
                }

                String imgUrl;

                try {
                    imgUrl = postsArr.getJSONObject(i).getString("picture");
                } catch (JSONException e) {
                    Log.e(log , " Url for image not found at index " + i);
                    imgUrl = "None";
                }

                String tagText = "";

                try {
                    JSONArray tagArr = postsArr.getJSONObject(i).getJSONArray("message_tags"); // Gets the array of tags

                    int amount = tagArr.length();

                    for (int j = 0; j < amount; j++) {
                        tagText += tagArr.getJSONObject(j).getString("name") + ",";
                    }
                } catch (JSONException e) {
                    Log.e(log , " No tags found at index " + i);

                    tagText = "None";
                }

                String linkUrl;

                try {
                    linkUrl = postsArr.getJSONObject(i).getString("link");
                } catch (JSONException e) {
                    Log.e(log, " Url for links not found at index " + i);
                    linkUrl = "None";
                }

                facebookPosts[i] = new FacebookPost(msg,imgUrl,tagText,linkUrl);

                dbHelper.insertIntoDB("Facebook",msg,imgUrl,tagText,linkUrl);
            }
        }
        catch (JSONException e) {
            Log.e(log, e.getMessage());
        }
    }

    private void UpdateUI(FacebookPost[] posts){

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

            ListAdapt adapt = new ListAdapt(getActivity(),userPosts,msgTags,"fb");
            adapt.notifyDataSetChanged();

            list.invalidateViews();
            list.setAdapter(adapt);
        }
    }

    private void getFeed(){
        // Retrofit and API interface called.
        APIInterface service = APIClient.getClient("https://graph.facebook.com/v5.0/me/").create(APIInterface.class);

        /* Call to get from the users profile page:
            - Pictures, Messages, MessageTags (Hashtags), Links
         */
        Call<ResponseBody> call = service.socialFeedItems("feed?fields=picture%2Cmessage%2Cmessage_tags%2Clink&access_token=" + AccessToken.getCurrentAccessToken().getToken(), limit);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(log, "Connection Successful");

                    // Notifies user, parses the data and sets it to arrays which can use to update the User Interface
                    try {
                        Notifications.notifyDownload(Profile.getCurrentProfile().getName() + " you feed was ",context);

                        downloadFeed(response.body().string());

                        UpdateUI(facebookPosts);
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
        });
    }
}