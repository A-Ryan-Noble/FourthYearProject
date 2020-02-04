package com.example.a2in1.fragmentRedirects;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a2in1.api.feeds.FeedItemViewFragment;

public class FeedItemView extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String msg = getIntent().getStringExtra("msg");
        String hashtag = getIntent().getStringExtra("tags");
        String Url = getIntent().getStringExtra("Url");
        String link = getIntent().getStringExtra("link");

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new FeedItemViewFragment(msg,hashtag,Url,link)).commit();
    }
}