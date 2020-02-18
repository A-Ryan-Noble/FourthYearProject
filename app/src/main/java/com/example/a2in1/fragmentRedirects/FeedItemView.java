package com.example.a2in1.fragmentRedirects;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a2in1.feeds.FeedItemViewFragment;
import com.example.a2in1.feeds.ImageFromItemViewer;
import com.example.a2in1.feeds.TimelineItemViewFragment;

public class FeedItemView extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String msg = getIntent().getStringExtra("msg");
        String hashtag = getIntent().getStringExtra("tags");
        String Url = getIntent().getStringExtra("Url");
        String userName = getIntent().getStringExtra("userNameOfPost");
        String link = getIntent().getStringExtra("link");
        boolean viewOnlyImage = getIntent().getBooleanExtra("onlyImage", false);

        // If viewOnlyImage is passed and is true , only then will it this redirect to view the image by itself
        if (viewOnlyImage) {
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new ImageFromItemViewer(msg, hashtag, Url, link)).commit();
        }
        else if(userName != null){
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new TimelineItemViewFragment(msg, userName, link)).commit();
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new FeedItemViewFragment(msg, hashtag, Url, link)).commit();
        }
    }
}