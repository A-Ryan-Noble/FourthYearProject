package com.example.a2in1.ui.facebook;


import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a2in1.LoadImage;
import com.example.a2in1.R;
import com.squareup.picasso.Picasso;

public class FeedItemViewFragment extends Fragment {

    private String log = getClass().getSimpleName();

    private String message;
    private String hashtags;
    private String imageUrl;

    public FeedItemViewFragment(String msg,String hashtag,String Url){
        this.message = msg;
        this.hashtags = hashtag;
        this.imageUrl = Url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_feed_item_view, container, false);

        // TODO: 14/12/2019 load in image

        int maxHeight = root.getMeasuredHeight();
        int maxWidth = root.getMeasuredWidth();
        TextView msg = (TextView) root.findViewById(R.id.feedMsg);
        TextView hashtag = (TextView) root.findViewById(R.id.feedHastags);
        final ImageView pic = (ImageView)root.findViewById(R.id.feedPic);

        msg.setText(message);
        if (hashtag != null) {
            hashtag.setText(getResources().getString(R.string.hashtags) + hashtags);
        }

        if (imageUrl != null) {
            pic.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(imageUrl).fit().centerInside().into(pic);
            Toast.makeText(getContext(), "NOTE: Image may appear blurry", Toast.LENGTH_LONG).show();
        }
        else {
            pic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.com_facebook_favicon_blue));
        }

        pic.setClickable(true);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return root;
    }
}
