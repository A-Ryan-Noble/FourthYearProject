package com.example.a2in1.api.feeds;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a2in1.MainActivity;
import com.example.a2in1.R;
import com.example.a2in1.fragmentRedirects.FeedItemView;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.http.GET;

public class FeedItemViewFragment extends Fragment {

    private String log = getClass().getSimpleName();

    private String message;
    private String hashtags;
    private String imageUrl;
    private String link;

    public FeedItemViewFragment(String msg, String hashtags, String Url, String link){
        this.message = msg;
        this.hashtags = hashtags;
        this.imageUrl = Url;
        this.link = link;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_feed_item_view, container, false);

        TextView msg = (TextView) root.findViewById(R.id.feedMsg);

        final TextView tags = (TextView) root.findViewById(R.id.feedHastags);

        Button linkBtn= (Button) root.findViewById(R.id.feedLinkBtn);

        ImageView pic = (ImageView)root.findViewById(R.id.feedPic);

        msg.setText(message);

        tags.setText(getResources().getString(R.string.hashtags) + " " + hashtags);

        if (imageUrl != null) {
            Log.d(log,"Image from Post is displayed");

            pic.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(imageUrl).fit().centerInside().into(pic);
            Toast.makeText(getContext(), "NOTE: Image may appear blurry", Toast.LENGTH_SHORT).show();

            linkBtn.setVisibility(View.VISIBLE);
        }

        // Link is open in an external device browser
        linkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);
            }
        });

        // Image is clicked
        pic.setClickable(true);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Goes to view the feed again but passing the argument stating it wants only to view the image.
                Intent imageViewing = new Intent(getContext(), FeedItemView.class);
                imageViewing.putExtra("msg",message);
                imageViewing.putExtra("tags",hashtags);
                imageViewing.putExtra("Url",imageUrl);
                imageViewing.putExtra("link",link);
                imageViewing.putExtra("onlyImage",true);

                startActivity(imageViewing);
            }
        });

        return root;
    }
}