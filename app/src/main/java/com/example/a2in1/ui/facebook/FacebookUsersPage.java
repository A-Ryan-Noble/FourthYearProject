package com.example.a2in1.ui.facebook;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.a2in1.R;
import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FacebookUsersPage extends Fragment {

    private ListView list;
    private String tag = "FacebookUsersPage";

    private int MAX_SIZE = 20;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_facebook_users_page, container, false);

        list = root.findViewById(R.id.postsList);
        final Button refreshBtn = root.findViewById(R.id.refreshBtn);

        refreshBtn.setText("Download Feed");
//        refreshBtn.setText(R.string.downloadFeed);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            //Re downloads the list
            public void onClick(View v) {

                refreshBtn.setText("Refresh Feed");
                new postsOfUser().execute();
            }
        });
        return root;
    }

    class postsOfUser extends AsyncTask<String, String, String> { // pass list view here

        String[] userPosts = new String[MAX_SIZE];

        StringBuffer buffer;

        private AccessToken accessToken = AccessToken.getCurrentAccessToken();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Toast.makeText(getContext(), "Data is downloading", Toast.LENGTH_SHORT).show();

            makeNotify("Downloading Feed","Facebook Feed is being downloaded",FacebookUsersPage.class,"FB feed Download",false);
            }

        @Override
        protected String doInBackground(String... params) {
            String access_token = "&access_token=" + accessToken.getToken();
//            https://graph.facebook.com/v4.0/me?fields=posts.limit(20)&access_token=EAAK2VfZBixyEBAFc8b05vSlRqjv67pVbOvya3CZAviEIeidcfEJZBNDoI720xenukNHyAu6entvsJsZCmda9f9NeGgFjcZBZB9yxmh2EsoYokQ8FugzKtNcZBEVYTFf7nTZCFmZAazd6v0yZCmOebojHL9SCPRInZB2vlZAOnsu19XX0dIKZCohkwJiaX8cma6OVAGhviH4nY2z0gnhacSaHWhBqd
            HttpURLConnection conn = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("https://graph.facebook.com/v4.0/me?fields=posts.limit(20)" + access_token);
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
        }

        // makeNotify Method that creates a notification on the user's phone
        public void makeNotify(String title, String msg, Class name, String id, boolean openable){

            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context .NOTIFICATION_SERVICE);

            NotificationChannel notificationChannel = new NotificationChannel(id, id+" channel", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Description of channel");

            if (notificationChannel != null){
                notificationManager.createNotificationChannel(notificationChannel);
            }

            Intent openIntent = new Intent(getContext(), name);
            PendingIntent openApp = PendingIntent.getActivity(getContext(),0,openIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(), id)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setLights(Color.RED, 1000, 1000)
                    .setColor(Color.BLUE)
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