package com.example.a2in1.feeds;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.a2in1.fragmentRedirects.FbSignInActivity;
import com.example.a2in1.ui.facebook.FacebookSignInOutFragment;
import com.example.a2in1.ui.twitter.TwitterSignInOutFragment;
import com.facebook.CallbackManager;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.FileNotFoundException;

import static com.example.a2in1.myPreferences.getBoolPref;

public class PostToBothFeeds extends Fragment {

    final private int getPicVal = 0;

    private Button getGallImg;

    private Bitmap img;

    private Uri imgUri;

    private String log = this.getClass().getSimpleName();

    private CallbackManager callbackManager;

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_post_to_both_feeds, container, false);

        context = getContext();

        boolean isTwitterLoggedIn = getBoolPref("TwitterLoggedIn", false, context);
        boolean isFbLoggedIn = getBoolPref("FBLoggedIn", false, context);

        // User must be logged in to view
        if (isTwitterLoggedIn && isFbLoggedIn) {

            TextView textView = root.findViewById(R.id.postTitleMsg);
            textView.setText(getResources().getString(R.string.postTxt) + " " + getResources().getString(R.string.fb) + " and " + getResources().getString(R.string.twitter));

            getGallImg = root.findViewById(R.id.getPicBtn2);

            getGallImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // boolean for if the user allowed for the permission
                    boolean hasPermission = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                    if (hasPermission) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, getPicVal);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);

                        Log.d(log, "Requested access to External storage from user");
                    }
                }
            });

            final CheckBox checkedText = root.findViewById(R.id.checked);

            Button postBtn = root.findViewById(R.id.postingBothSubmitBtn);

            // The text inputted by the user
            final EditText msgInput = root.findViewById(R.id.msgForBothInput);

            // User clicks to post to the sites
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

        // The user isn't logged in to one/both of the sites
        else {
            if (!isTwitterLoggedIn) {
                startActivity(new Intent(context, TwitterSignInOutFragment.class));
            }
            if (!isFbLoggedIn) {
                startActivity(new Intent(context, FbSignInActivity.class));
            }
        }
        return root;
    }

    private void alertUser(final String msg) {
        toFacebook(msg); // Facebook share dialog

        // Twitter share dialog
        TweetComposer.Builder builder = new TweetComposer.Builder(context);

        if (imgUri != null){
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

                try {
                    img = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imgUri));

                    Log.d(log, "Image chosen was made into Bitmap");

                    getGallImg.setText(getResources().getString(R.string.picAdded));
                    getGallImg.setClickable(false);
                } catch (FileNotFoundException e) {
                    Log.e(log, e.getMessage());
                }
            } else {
                getGallImg.setText(getResources().getString(R.string.postPic));
                getGallImg.setClickable(true);
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void toFacebook(String msg) {
        callbackManager = CallbackManager.Factory.create();

        // How to share a picture info learned from: https://developers.facebook.com/docs/sharing/android?sdk=fbsdk
        ShareDialog shareDialog = new ShareDialog(getParentFragment());

        if (img != null) {
            SharePhoto photo = new SharePhoto.Builder().setBitmap(img).build();

            shareDialog.show(new ShareMediaContent.Builder().addMedium(photo).setShareHashtag(new ShareHashtag.Builder()
                    .setHashtag(msg).build()).build());
        } else {
            shareDialog.show(new ShareLinkContent.Builder()
                    .setShareHashtag(new ShareHashtag.Builder().setHashtag(msg).build())
                    .build());
        }
    }
}