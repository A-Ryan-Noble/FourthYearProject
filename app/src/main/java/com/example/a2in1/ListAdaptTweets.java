package com.example.a2in1;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListAdaptTweets extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] post;
    private final String[] username;

    public ListAdaptTweets(Activity context, String[] post, String[] username) {
        super(context, R.layout.list_adapt_tweets, post);

        this.context = context;
        this.username = username;
        this.post = post;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_adapt_tweets, null,true);

        TextView usernameTxt = (TextView) rowView.findViewById(R.id.usernameOfPoster);
        TextView postTxt = (TextView) rowView.findViewById(R.id.postTitle);

        String usernameItemTxt = username[position];
        String postItemTxt = post[position];

        String oldText = usernameTxt.getText().toString();

        // Updates the text of the UI
        usernameTxt.setText(oldText + " "+ usernameItemTxt);
        postTxt.setText(postItemTxt);

        return rowView;
    }
}