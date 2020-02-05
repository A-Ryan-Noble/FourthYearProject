package com.example.a2in1.feeds;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.a2in1.R;
import com.example.a2in1.fragmentRedirects.FeedItemView;
import com.squareup.picasso.Picasso;

public class ImageFromItemViewer extends Fragment {

    private String message;
    private String hashtags;
    private String imageUrl;
    private String link;

    public ImageFromItemViewer(String msg,String hashtag,String Url, String link){
        this.message = msg;
        this.hashtags = hashtag;
        this.imageUrl = Url;
        this.link = link;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This is useful for when the device rotates and the class constructor is called with none of the variables previously passed
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.feed_item_image_view,container,false);

        ImageView img = (ImageView)root.findViewById(R.id.feedItemImage);   // The image that picasso uses to set the image to

        Picasso.with(getContext()).load(imageUrl).into(img);

        //  Button to go back to feed
        Button button = (Button) root.findViewById(R.id.goBackToFeed);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Goes back to view the feed through the same redirecting class.
                Intent itemView = new Intent(getContext(), FeedItemView.class);
                itemView.putExtra("msg",message);
                itemView.putExtra("tags",hashtags);
                itemView.putExtra("Url",imageUrl);
                itemView.putExtra("link",link);

                startActivity(itemView);
            }
        });

        return root;
    }
}
