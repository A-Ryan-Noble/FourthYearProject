package com.example.a2in1.ui.facebook;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.a2in1.fragmentRedirects.FbSignInActivity;
import com.example.a2in1.R;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;

import java.io.FileNotFoundException;

import static com.example.a2in1.myPreferences.getBoolPref;

public class FacebookPosting extends Fragment {

    final private int getPicVal = 0;

    private Button getGallImg;

    private Bitmap img;

    private String log = this.getClass().getSimpleName();

    private CallbackManager callbackManager;

    private Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_facebook_posting, container, false);

        context = getContext();

        boolean isFbLoggedIn = getBoolPref("FBLoggedIn",false,context);

        // if user isn't logged in on fb then go to the sign in fragment
        if (!isFbLoggedIn){
           startActivity(new Intent(context, FbSignInActivity.class));
        }
        else {

            TextView textView = root.findViewById(R.id.postTitleMsg);
            textView.setText(getResources().getString(R.string.postTxt)+ " " + getResources().getString(R.string.fb));

            // The text inputted by the user
            final EditText msgInput = root.findViewById(R.id.msgInput);

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

            // Changes text to contain username of the user
            final CheckBox checkedText = root.findViewById(R.id.checkedText);
            String oldText =getResources().getString(R.string.postWish);
            String username = Profile.getCurrentProfile().getFirstName();
            checkedText.setText("I, "+username+ " "+ oldText);

            // User clicks to post to the site
            Button PostBtn = root.findViewById(R.id.postingSubmitBtn);
            PostBtn.setOnClickListener(new View.OnClickListener() {
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

        callbackManager = CallbackManager.Factory.create();

        // How to share a picture info learned from: https://developers.facebook.com/docs/sharing/android?sdk=fbsdk
        ShareDialog shareDialog = new ShareDialog(getParentFragment());

        if (img != null){
            SharePhoto photo = new SharePhoto.Builder().setBitmap(img).build();

            // Share dialog that shows the user the message they entered and the image
            shareDialog.show(new ShareMediaContent.Builder().addMedium(photo).setShareHashtag(new ShareHashtag.Builder()
                    .setHashtag(msg).build()).build());
        }
        else{
            // Share dialog that shows the user the message they entered
            shareDialog.show(new ShareLinkContent.Builder().setShareHashtag(new ShareHashtag.Builder().setHashtag(msg).build())
                    .build());
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == getPicVal) {
            if (data != null) {
                try {
                    img = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(data.getData()));

                    getGallImg.setText(getResources().getString(R.string.picAdded));
                    getGallImg.setClickable(false);
                }
                catch (FileNotFoundException e) {
                    Log.e(log, e.getMessage());
                }
            }
            else {
                getGallImg.setText(getResources().getString(R.string.postPic));
                getGallImg.setClickable(true);
            }
        }
        else
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}