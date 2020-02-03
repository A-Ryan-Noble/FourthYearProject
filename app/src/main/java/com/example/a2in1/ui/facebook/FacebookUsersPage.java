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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.a2in1.ListAdapt;
import com.example.a2in1.Notifications;
import com.example.a2in1.R;
import com.example.a2in1.api.feeds.APIClient;
import com.example.a2in1.api.feeds.APIInterface;
import com.example.a2in1.api.feeds.DBHelper;
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

    private String log = this.getClass().getSimpleName();

    private DBHelper dbHelper;

    private ListView list;

    private String[] imageUrl;
    private String[] msgTags;
    private String[] userPosts;

    private FacebookPost[] facebookPosts;

    private Context context;

    private int limit;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_facebook_users_page, container, false);

        context = getContext();

        boolean isFbLoggedIn = getBoolPref("FBLoggedIn", false,context);

        if (isFbLoggedIn) {

            list = root.findViewById(R.id.postsList);

            final Button refreshBtn = root.findViewById(R.id.refreshBtn);

            // Gets value from the the SharedPreferences
            limit = getIntPref("MaxFbNum",5,context);

            SQLiteDatabase DB = context.openOrCreateDatabase("feeds", MODE_PRIVATE, null);

            if (!DB.isOpen()) {
                DB = context.openOrCreateDatabase("feeds", MODE_PRIVATE, null);
            }

            dbHelper = new DBHelper(context);

            if (dbHelper.getAllOfSite("Facebook").getCount() == 0) {
                // Used to store the message. If no message then " " is at index
                userPosts = new String[limit];

                // Used to store the url of an image in a message, if no URL then null is at index
                imageUrl = new String[limit];

                // Used to store a given Message tags. If message doesn't have a log then "None" is at the index
                msgTags = new String[limit];

                facebookPosts = new FacebookPost[limit];

                refreshBtn.setText(R.string.downloadFeed);

                getFeed();

            }
            else{
                userPosts = new String[dbHelper.getAllOfSite("Facebook").getCount()];

                imageUrl = new String[dbHelper.getAllOfSite("Facebook").getCount()];

                msgTags = new String[dbHelper.getAllOfSite("Facebook").getCount()];

                refreshBtn.setText(R.string.refresh);

                //UI is updated from the contents in the database
                facebookPosts = dbHelper.getAllFacebook();

                for(int i = 0; i< facebookPosts.length; i++){
                    Log.d("ZZZ", userPosts[i] + " "+imageUrl[i] + " "+ msgTags[i]);
                }
                Log.d("ZZZ","End\n");

                UpdateUI(facebookPosts);
//                UpdateUI(dbHelper.getAllFacebook());

                Toast.makeText(context,"There is " +  dbHelper.getAll().getCount() + " in the database",Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Wish to remove all?");
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.emptyDB();
                        refreshBtn.setText(R.string.downloadFeed);

                        Toast.makeText(context,"There is " +  dbHelper.getAll().getCount() + " in the database",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }

            ListAdapt adapt = new ListAdapt(getActivity(),userPosts,msgTags,"fb");
            adapt.notifyDataSetChanged();

            list.invalidateViews();
            list.setAdapter(adapt);

            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (list.getItemAtPosition(position).toString() != "") { // Gets the text of the list item clicked

                        // Alerts the user that their isnt a reason to view it in more detail
                        if (imageUrl[position] == null && msgTags[position] == null) {
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
                            startActivity(itemView);
                        }
                    }
                    return false;
                }
            });

            refreshBtn.setOnClickListener(new View.OnClickListener() {

                // Re-downloads the list
                @Override
                public void onClick(View v) {

                        Toast.makeText(context,"Refresh clicked",Toast.LENGTH_SHORT).show();

//                    new postsOfUser().execute()
                }
            });
        }
        // if user isn't logged in on fb then go to the sign in fragment
        else {
            startActivity(new Intent(getContext(), FbSignInActivity.class));
        }
        return root;
    }

    private void notifyDownload(String feed) {
        if (limit > 20) {
            limit = 20;
        }

        String userMsg = Profile.getCurrentProfile().getName() + " you feed was ";

        if (getBoolPref("notificationEnabled", true, context)) {
            Notifications.notify("Feed Updated ", userMsg + " downloaded",
                    "FB feed Download", 1000, this.getClass(), false, context);
        } else {
            Toast.makeText(context, "Feed Updated " + userMsg + " downloaded", Toast.LENGTH_SHORT).show();
        }

        downloadFeed(feed);
    }

    private void downloadFeed(String feed){
        try {
            JSONObject feedUser = new JSONObject(feed);

            JSONArray  obj = feedUser.getJSONArray("data");

            for (int i = 0; i < obj.length(); i++) {

                String msg = "Post doesn't have a message";

                try {
                    msg = obj.getJSONObject(i).getString("message"); // This gets only the message part of the array
                } catch (JSONException e) {
                    Log.e(log + " Message not found at index " + i, e.getMessage());
                }

                String imgUrl;

                try {
                    imgUrl = obj.getJSONObject(i).getString("picture");
                } catch (JSONException e) {
                    Log.e(log + " Url for image not found at index " + i, e.getMessage());
                    imgUrl = null;
                }

                String tagText = "";

                try {
                    JSONArray tagArr = obj.getJSONObject(i).getJSONArray("message_tags"); // Gets the array of tags

                    int amount = tagArr.length();

                    for (int j = 0; j < amount; j++) {
                        tagText += tagArr.getJSONObject(j).getString("name") + ",";
                    }
                } catch (JSONException e) {
                    Log.e(log + " No tags found at index " + i, e.getMessage());

                    tagText = "None";
                }

                facebookPosts[i] = new FacebookPost(msg,imgUrl,tagText);

                dbHelper.insertIntoDB("Facebook",msg,imgUrl,tagText);

//                Log.d("zzz",facebookPosts[i].toString());
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
                userPosts[i] = "No message";
            }

            try {
                imageUrl[i] = posts[i].getPicture();
            } catch (NullPointerException e) {
                Log.d(log, e.getMessage());
                imageUrl[i] = null;
            }

            String hashtags = null;

            try {
                String[] tags = posts[i].getMessageTags();
                for (String x : posts[i].getMessageTags()) {
                    hashtags = x + " ";
                }
                msgTags[i] = hashtags;
            }
            catch (NullPointerException e){
                Log.e(log,e.getMessage());
            }

//            Log.d("ZZZ",facebookPosts.toString());
            ListAdapt adapt = new ListAdapt(getActivity(),userPosts,msgTags,"fb");
            adapt.notifyDataSetChanged();

            list.invalidateViews();
            list.setAdapter(adapt);

//            Log.d("ZZZ", userPosts[i] + " "+imageUrl[i] + " "+ msgTags[i]);
        }
    }

    private void getFeed(){
        // Retrofit API interface called.
        APIInterface service = APIClient.getClient("https://graph.facebook.com/v5.0/me/").create(APIInterface.class);

        Call<ResponseBody> call = service.socialFeedItems("feed?fields=picture%2Cmessage%2Cmessage_tags&access_token=" + AccessToken.getCurrentAccessToken().getToken(), limit);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(log, "Connection Successful");

                    try {
                        notifyDownload(response.body().string());

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
/*
    class postsOfUser extends AsyncTask<String, String, String> { // pass list view here

        String log = this.getClass().getSimpleName();

        int limit = FacebookUsersPage.this.limit;

        String[] imageUrl = FacebookUsersPage.this.imageUrl;
        String[] msgTags = FacebookUsersPage.this.msgTags;
        String[] userPosts = FacebookUsersPage.this.userPosts;

        StringBuffer buffer;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(limit>20)
            {
                limit = 20;
            }
            String username = Profile.getCurrentProfile().getName();

            boolean canNotify = getBoolPref("notificationEnabled",true,getContext());

            if (canNotify){
                Notifications.notify("Feed Updated ", username+ " you feed was downloaded", "FB feed Download", 1000, this.getClass(), false, getContext());
            }
            else {
                Toast.makeText(getContext(),"Feed Updated "+ username+ " you feed was downloaded", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String access_token = "&access_token=" + AccessToken.getCurrentAccessToken().getToken();

            HttpURLConnection conn = null;
            BufferedReader reader = null;

            String link = "https://graph.facebook.com/v5.0/me/feed?fields=picture%2Cmessage%2Cmessage_tags&limit(" + limit + ")" +access_token;

            try {
                URL url = new URL(link);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                buffer = new StringBuffer();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append((line + "\n"));
                }

                Log.d(log, "Downloaded");
                return buffer.toString();
            } catch (MalformedURLException e) {
                Log.e(log, "Malformed URL: " + e.getMessage());
            } catch (IOException e) {
                Log.e(log, "IO Exception: " + e.getMessage());
            }
            // closes everything that was opened
            finally {
                if (conn != null) {
                    conn.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    Log.e(log, e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            newItemsPopulate(s);

            ListAdapt adapt = new ListAdapt(getActivity(),userPosts,msgTags,"fb");
            adapt.notifyDataSetChanged();

            list.invalidateViews();
            list.setAdapter(adapt);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String itemValue = list.getItemAtPosition(position).toString(); // gets the text of the list item clicked

                    if (itemValue != "") { // not blank item text

                        // Alerts the user that their isnt a reason to view it in more detail
                        if (imageUrl[position] == null && msgTags[position] == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder.setMessage("There is only this text content for this item");
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            });
                            builder.show();
                        }
                        else {
                            Intent itemView = new Intent(getContext(), FeedItemView.class);
                            itemView.putExtra("msg",userPosts[position]);
                            itemView.putExtra("tags",msgTags[position]);
                            itemView.putExtra("Url",imageUrl[position]);
                            startActivity(itemView);
                        }
                    }
                }
            });

            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    String itemValue = list.getItemAtPosition(position).toString(); // gets the text of the list item clicked

                    if (itemValue != "") { // not blank item text
                        Toast.makeText(getContext(), itemValue, Toast.LENGTH_SHORT).show();
                    }

                    return false;
                }
            });
        }

        private void newItemsPopulate(String result) {
            for (int i = 0; i < limit; i++) {
                userPosts[i] = "";
            }
            listUpdate(result);
        }

        private void listUpdate(String feed) {
            try {
                JSONObject feedUser = new JSONObject(feed);

                JSONArray obj = feedUser.getJSONArray("data"); // this gets the posts data

                for (int i = 0; i < obj.length() && i < limit; i++) {

                    String msg = "Post doesn't have a message";

                    try {
                        msg = obj.getJSONObject(i).getString("message"); // This gets only the message part of the array
                    }
                    catch (JSONException e){
                        Log.e(log + " Message not found at index " + i,e.getMessage());
                    }

                    String imgUrl;

                    try {
                        imgUrl = obj.getJSONObject(i).getString("picture");
                    }
                    catch (JSONException e){
                        Log.e(log + " Url for image not found at index " + i,e.getMessage());
                        imgUrl = null;
                    }

                    String tagText ="";

                    try {
                        JSONArray tagArr = obj.getJSONObject(i).getJSONArray("message_tags"); // Gets the array of tags

                        int amount= tagArr.length();

                        for (int j = 0; j< amount; j++){
                            tagText += tagArr.getJSONObject(j).getString("name") + " ";
                        }
                    }
                    catch (JSONException e){
                        Log.e(log + " No tags found at index " + i,e.getMessage());

                        tagText = null;
                    }

                    userPosts[i] = msg;
                    imageUrl[i] = imgUrl;
                    msgTags[i] = tagText;


                }
            } catch (JSONException e) {
                Log.e(log, e.getMessage());
            }
        }
    }*/
}