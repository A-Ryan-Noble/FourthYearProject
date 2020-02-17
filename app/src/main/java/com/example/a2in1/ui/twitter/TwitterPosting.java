package com.example.a2in1.ui.twitter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a2in1.R;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import static com.example.a2in1.myPreferences.getBoolPref;

public class TwitterPosting extends Fragment {

    final private int getPicVal = 0;

    private Uri imgUri;
    private Button getGallImg;

    private String log = this.getClass().getSimpleName();

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_twitter_posting, container, false);

        context = getContext();

        boolean isTwitterLoggedIn = getBoolPref("TwitterLoggedIn", false, context);

        if (!isTwitterLoggedIn) {
            startActivity(new Intent(context, TwitterSignIn.class));
        }
        else {
            TextView textView = root.findViewById(R.id.postTitleMsg);

            textView.setText(getResources().getString(R.string.postTxt)+ " " + getResources().getString(R.string.twitter));

            getGallImg = root.findViewById(R.id.getPicBtn);

            getGallImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // boolean for if the user allowed for the permission
                    boolean hasPermission = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                    if (hasPermission){
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, getPicVal);
                    }
                    else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);

                        Log.d(log, "Requested access to External storage from user");
                    }
                }
            });

            //Configures twitter sdk
            TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.twitter_CONSUMER_KEY), getResources().getString(R.string.twitter_CONSUMER_SECRET));

            TwitterConfig twitterConfig = new TwitterConfig.Builder(context)
                    .twitterAuthConfig(authConfig)
                    .build();
            Twitter.initialize(twitterConfig);

            final CheckBox checkedText = root.findViewById(R.id.checkedText);
            // Changes text to contain username of the user
            String oldText =getResources().getString(R.string.postWish);
            String username = TwitterCore.getInstance().getSessionManager().getActiveSession().getUserName();
            checkedText.setText("I, "+username+ " "+ oldText);


            Button postBtn = root.findViewById(R.id.postingSubmitBtn);

            // The text inputted by the user
            final EditText msgInput = root.findViewById(R.id.msgInput);

            // User clicks to post to the site
            postBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkedText.isChecked()) {
                        Toast.makeText(context, R.string.postingCheckBox, Toast.LENGTH_SHORT).show();
                    } else {
                        alertUser(msgInput.getText().toString());
                    }
                }
            });
        }

        return root;
    }

    private void alertUser(final String msg) {

        TweetComposer.Builder builder = new TweetComposer.Builder(context);

        if (imgUri != null) {
            builder.image(imgUri);
        }

        builder.text(msg);

        builder.show();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == getPicVal) {
            if (data != null) {
                imgUri = data.getData();

                getGallImg.setText(getResources().getString(R.string.picAdded));
                getGallImg.setClickable(false);
            }
        }
    }
}