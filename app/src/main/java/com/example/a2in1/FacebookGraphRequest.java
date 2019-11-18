package com.example.a2in1;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FacebookGraphRequest {

    private String tag = "GraphRequest";
    private List<String> posts = new ArrayList<>();

    private AccessToken accessToken = AccessToken.getCurrentAccessToken();
    public List<String> getLoggedInUserPosts() {
//            (AccessToken accessToken) {
//        List<String> posts = new ArrayList<>();

//        GraphRequest request = new GraphRequest(accessToken,"/me/feed",
//                null,HttpMethod.GET,new GraphRequest.Callback() {
//            public void onCompleted(GraphResponse response) {
//                JSONObject obj = response.getJSONObject();
//                JSONArray arr;
//                try {
//                    arr = obj.getJSONArray("data");
//                    for(int i=0; i<arr.length();i++){
//                        String msg = arr.getJSONObject(0).toString();
//                        Log.e("ZZZ",msg);
//                    }
//                }
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            JSONArray obj = object.getJSONObject("posts").getJSONArray("data"); // This gets the posts data
                            for (int i = 0; i < obj.length(); i++) {
                                String msg = obj.getJSONObject(i).getString("message"); // This gets only the message part of the array
                                Log.d(tag, msg);
                                posts.add(msg);
                            }
                        } catch (JSONException e) {
                            Log.e(tag, e.toString());
                        }
                    }
                }
        );

        Bundle parameters = new Bundle();
        parameters.putString("fields", "posts.limit(20)");
        request.setParameters(parameters);
        request.executeAsync();

        return posts;
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