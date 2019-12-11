package com.example.a2in1.ui.facebook;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.a2in1.FbSignInActivity;
import com.example.a2in1.R;
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

import static android.content.Context.MODE_PRIVATE;
import static com.example.a2in1.myPreferences.getBoolPref;
import static com.example.a2in1.myPreferences.getIntPref;

public class FacebookUsersPage extends Fragment {

    private ListView list;
    private String log = getClass().getSimpleName();

    private SharedPreferences mPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_facebook_users_page, container, false);

//        mPreferences = getContext().getSharedPreferences("savedDataFile", MODE_PRIVATE);

//        boolean isFbLoggedIn = mPreferences.getBoolean("FBLoggedIn", false);

        boolean isFbLoggedIn = getBoolPref("FBLoggedIn", false,getContext());

        if (isFbLoggedIn) {

            list = root.findViewById(R.id.postsList);
            final Button refreshBtn = root.findViewById(R.id.refreshBtn);

//            String title = getContext().getResources().getString(R.string.fbSocialFeed);
//            String loggedInUser = Profile.getCurrentProfile().getFirstName();

//        getActivity().getActionBar().setTitle("e");
//    getActivity().setTitle("e");//         .setTitle(title + " for : "+ loggedInUser);

            refreshBtn.setText("Download Feed");
            refreshBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                //Re downloads the list
                public void onClick(View v) {

                    refreshBtn.setText("Refresh Feed");
                    new postsOfUser().execute();
                }
            });
        }

        // if user isn't logged in on fb then go to the sign in fragment
        else {
            startActivity(new Intent(getContext(), FbSignInActivity.class));
        }
        return root;

    }

    class postsOfUser extends AsyncTask<String, String, String> { // pass list view here

        String tag = this.getClass().getSimpleName();

        // Gets value from the the SharedPreferences
        //int limit = getContext().getSharedPreferences("savedDataFile", MODE_PRIVATE).getInt("MaxFbNum",20);
        int limit = getIntPref("MaxFbNum",20,getContext());

        String[] userPosts;

        StringBuffer buffer;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(limit>20)
            {
                limit = 20;
            }
            userPosts = new String[limit];

            String username = Profile.getCurrentProfile().getName();
            makeNotify("Feed Updated ",username+ " you feed was downloaded",FacebookUsersPage.class,"FB feed Download",false);
        }

        @Override
        protected String doInBackground(String... params) {
            String access_token = "&access_token=" + AccessToken.getCurrentAccessToken().getToken();

            HttpURLConnection conn = null;
            BufferedReader reader = null;

            String link = "https://graph.facebook.com/v4.0/me?fields=posts.limit(" + limit + ")" +access_token;

            Log.e(tag,access_token);
            try {
//                URL url = new URL("https://graph.facebook.com/v4.0/me?fields=posts.limit(20)" + access_token);
                URL url = new URL(link);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                buffer = new StringBuffer();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append((line + "\n"));
                    Log.d(tag, " " + line);
                }

                return buffer.toString();
            } catch (MalformedURLException e) {
                Log.e(tag, "Malformed URL: " + e.getMessage());
            } catch (IOException e) {
                Log.e(tag, "IO Exception: " + e.getMessage());
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
                    Log.e(tag, e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            newItemsPopulate(s);
            ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, userPosts);

            arrayAdapter.notifyDataSetChanged();

            list.invalidateViews();
            list.setAdapter(arrayAdapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String itemValue = list.getItemAtPosition(position).toString(); // gets the text of the list item clicked

                    if (itemValue != "") { // not blank item text
                        Toast.makeText(getContext(), itemValue, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    String itemValue = list.getItemAtPosition(position).toString(); // gets the text of the list item clicked

                    if (itemValue != "") { // not blank item text
                        Toast.makeText(getContext(),"You long clicked on item no." + (position+1)+ " of the list.", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
        }

        private void newItemsPopulate(String result) {
            for (int i = 0; i < userPosts.length; i++) {
                userPosts[i] = "";
            }
            listUpdate(result);
        }

        private void listUpdate(String feed) {
            try {
                JSONObject feedUser = new JSONObject(feed);

                JSONArray obj = feedUser.getJSONObject("posts").getJSONArray("data"); // this gets the posts data
                for (int i = 0; i < obj.length(); i++) {
                    userPosts[i] = obj.getJSONObject(i).getString("message"); // This gets only the message part of the array
                }
            } catch (JSONException e) {
                Log.e(tag, e.getMessage());
            }

//            SharedPreferences.Editor editor = mPreferences.edit();
//            editor.put
//            editor.commit();

        }

        // makeNotify Method that creates a notification on the user's phone
        public void makeNotify(String title, String msg, Class name, String id, boolean openable){

            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context .NOTIFICATION_SERVICE);

            NotificationChannel notificationChannel = new NotificationChannel(id, id, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(title);

            if (notificationChannel != null){
                notificationManager.createNotificationChannel(notificationChannel);
            }

            Intent openIntent = new Intent(getContext(), name);
            PendingIntent openApp = PendingIntent.getActivity(getContext(),0,openIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(), id)
                    .setSmallIcon(R.mipmap.download_icon)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setLights(Color.BLUE, 1000, 1000)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL); // Vibrate,Sound & Lights are set

            if (openable){
                notificationBuilder.setContentIntent(openApp);// When notification is clicked it will open
            }

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
            notificationManagerCompat.notify(1000, notificationBuilder.build());
        }
    }
}