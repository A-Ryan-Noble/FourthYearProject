package com.example.a2in1.ui.facebook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.a2in1.ListAdapt;
import com.example.a2in1.Notifications;
import com.example.a2in1.R;
import com.example.a2in1.fragmentRedirects.FbSignInActivity;
import com.example.a2in1.fragmentRedirects.FeedItemView;
import com.example.a2in1.api.feeds.APIService;
import com.example.a2in1.models.FacebookPost;
import com.facebook.AccessToken;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.a2in1.myPreferences.getBoolPref;
import static com.example.a2in1.myPreferences.getIntPref;

public class FacebookUsersPage extends Fragment {

    private String log = this.getClass().getSimpleName();

    private ListView list;

    private String[] imageUrl;
    private String[] msgTags;
    private String[] userPosts;

    private FacebookPost[] facebookPosts;

    private Context context;

//    ImageView img;

    private int limit;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_facebook_users_page, container, false);

        boolean isFbLoggedIn = getBoolPref("FBLoggedIn", false,getContext());

        if (isFbLoggedIn) {
//            img = root.findViewById(R.id.feedImage);

            context = getContext();

            // Gets value from the the SharedPreferences
            limit = getIntPref("MaxFbNum",5,context);

            // If the array of posts is empty
            if (facebookPosts == null) {
                userPosts = new String[limit]; // Used to store the message

                imageUrl = new String[limit]; // Used to store the url of an image in a message

                msgTags = new String[limit]; // Used to store a given Message tags. If message doesn't have a log then empty value at the index

                facebookPosts = new FacebookPost[limit];
            }

            list = root.findViewById(R.id.postsList);

            final Button refreshBtn = root.findViewById(R.id.refreshBtn);

            refreshBtn.setText("Download Feed");
            refreshBtn.setOnClickListener(new View.OnClickListener() {
                // Re-downloads the list
                @Override
                public void onClick(View v) {

                    refreshBtn.setText("Refresh Feed");

                    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://graph.facebook.com/v5.0/me/").build();

                    APIService service = retrofit.create(APIService.class);

                    Call<ResponseBody> call = service.socialFeedItems(
                            "feed?fields=picture%2Cmessage%2Cmessage_tags&access_token=" + AccessToken.getCurrentAccessToken().getToken(),
                            limit);

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Log.d(log, "server contacted and has file");
                                try {
                                    String feed = response.body().string();

                                    downloadNotify();

                                    ListAdapt adapt = new ListAdapt(getActivity(),userPosts,msgTags,"fb");
                                    adapt.notifyDataSetChanged();

                                    list.invalidateViews();
                                    list.setAdapter(adapt);

                                    UpdateUI(feed);
                                    //Log.d("zzz",response.body().string());
                                }
                                catch (IOException e){
                                    Log.e(log,e.getMessage());
                                }
                            }
                            else {
                                Log.e(log, "Failed to get URL");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                            Log.e(log, "Call Failed\n" + throwable.getMessage());
                        }
                    });

                    list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                            String itemValue = list.getItemAtPosition(position).toString(); // gets the text of the list item clicked

                            if (itemValue != "") { // not blank item text

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

    private void downloadNotify(){
        if(limit>20)
        {
            limit = 20;
        }

        if (getBoolPref("notificationEnabled",true,context)){
            Notifications.notify("Feed Updated ", Profile.getCurrentProfile().getName() + " you feed was downloaded",
                    "FB feed Download", 1000, this.getClass(), false, context);
        }
        else {
            Toast.makeText(context,"Feed Updated "+ Profile.getCurrentProfile().getName() + " you feed was downloaded", Toast.LENGTH_SHORT).show();
        }
    }

    private void UpdateUI(String feed){
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
                        tagText += tagArr.getJSONObject(j).getString("name") + " ";
                    }
                } catch (JSONException e) {
                    Log.e(log + " No tags found at index " + i, e.getMessage());

                    tagText = "None";
                }

                userPosts[i] = msg;
                imageUrl[i] = imgUrl;
                msgTags[i] = tagText;

                facebookPosts[i] = new FacebookPost(msg, imgUrl, tagText);

                Log.d("zzz", facebookPosts[i].toString());
            }
        }
        catch (JSONException e) {
            Log.e(log, e.getMessage());
        }
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