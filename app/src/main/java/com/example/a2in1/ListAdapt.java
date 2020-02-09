package com.example.a2in1;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapt extends ArrayAdapter<String> {

    private final String log = getClass().getSimpleName();
    private final Activity context;
    private final String[] post;
    private final String[] hashtags;
    private final Integer [] imgid;

    private String site;

    public ListAdapt(Activity context, String[] post, String[] hashtags,String site) {
        super(context, R.layout.list_adapt, post);

        this.site = site;

        this.context = context;
        this.post = post;
        this.hashtags = hashtags;
        this.imgid = new Integer[2];

        imgid[0] = R.drawable.com_facebook_favicon_blue;
        imgid[1] = R.drawable.twitter_logo;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_adapt, null,true);

        TextView postTxt = (TextView) rowView.findViewById(R.id.postTitle);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.feedImage);
        TextView hashtagTxt = (TextView) rowView.findViewById(R.id.hashtagTxt);

        String postItemTxt = post[position];

        String hashtagItemTxt;
        try {
            hashtagItemTxt = hashtags[position];
        }
        catch (NullPointerException e){
            hashtagItemTxt = "None";
            Log.e(log,e.getMessage());
        }

        // If/else ensures that the post doesn't contain hastags
        if (hashtagItemTxt.equals("None")) {
            postTxt.setText(postItemTxt);
        }
        else {
            try {
                String noTagsInPost[] = postItemTxt.split("#");
                postTxt.setText(noTagsInPost[0]);
                post[position] = post[position].replace(postItemTxt, noTagsInPost[0]);
            }catch (NullPointerException e){
                Log.e(log,e.getMessage());
            }
        }

        if (site == "fb"){
            imageView.setImageResource(imgid[0]);
        }
        else {
            imageView.setImageResource(imgid[1]);
        }

        if (hashtags[position] != null) {
            hashtagTxt.setText(context.getResources().getString(R.string.hashtags) + hashtagItemTxt);
        }
        else {
            hashtagTxt.setText("None");
        }
        return rowView;
    }
}