package com.example.a2in1.ui.facebook;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a2in1.LoadImage;
import com.example.a2in1.R;

public class FeedItemView extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_feed_item_view, container, false);

        String name = getActivity().getIntent().getExtras().getString("msg");

        String hasht = getActivity().getIntent().getExtras().getString("hashtags");
        String urlTxt = getActivity().getIntent().getExtras().getString("Url");

        // TODO: 13/12/2019 load in image 
        
        
        TextView msg = (TextView) root.findViewById(R.id.feedMsg);
        TextView hashtag = (TextView) root.findViewById(R.id.feedHastags);
        ImageView pic = (ImageView)root.findViewById(R.id.feedPic);


        LoadImage loadImage = new LoadImage(pic);
        loadImage.execute(urlTxt);

        return root;
    }
}
