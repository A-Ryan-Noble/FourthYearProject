package com.example.a2in1.feeds;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.a2in1.R;
import com.example.a2in1.fragmentRedirects.FeedItemView;
import com.squareup.picasso.Picasso;

public class TimelineItemViewFragment extends Fragment {

    private String log = getClass().getSimpleName();

    private String message;
    private String username;
    private String link;

    public TimelineItemViewFragment(String msg, String username, String link){
        this.message = msg;
        this.username = username;
        this.link = link;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_feed_item_view, container, false);

        TextView msg = (TextView) root.findViewById(R.id.feedMsg);

        final TextView otherInfo = (TextView) root.findViewById(R.id.feedSecondaryInfo);

        Button linkBtn = (Button) root.findViewById(R.id.feedLinkBtn);

        msg.setText(message);

        otherInfo.setText(getResources().getString(R.string.postedBy) + " " + username);

        linkBtn.setVisibility(View.VISIBLE);

        // if there is no link on the tweet, the link is changed to allow the user can click to open twitter
        if (link.equals("None")){
            link = "https://twitter.com/home";
            linkBtn.setText("Open " + getResources().getString(R.string.twitter));
        }

        // Link is open in an external device browser
        linkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);
            }
        });

        return root;
    }
}