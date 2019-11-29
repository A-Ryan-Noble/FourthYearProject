package com.example.a2in1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FacebookGraphRequest extends AsyncTask<String,String,String> {

    private int limit;
    private String tag = "GraphRequest";
    private List<String> posts = new ArrayList<>();
    StringBuffer buffer;

    private AccessToken accessToken = AccessToken.getCurrentAccessToken();

    public FacebookGraphRequest(ListView listView, int maxPosts) {
        super();
        this.limit = maxPosts;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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
    }
    /*
    public interface LoggedInUserPostCallback{
        void OntLoggedInUserPosts(List<String> posts);
    }*/

/*
    public JSONObject userName(AccessToken accessToken) {
        JSONObject req = getUserObj(accessToken);

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name");
        req.setParameters(parameters);
        req.executeAsync();

        return req;
    }
*/
}