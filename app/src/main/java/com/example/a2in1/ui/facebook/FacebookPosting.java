package com.example.a2in1.ui.facebook;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.a2in1.fragmentRedirects.FbSignInActivity;
import com.example.a2in1.R;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.share.model.ShareContent;
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
            callbackManager = CallbackManager.Factory.create();

            getGallImg = root.findViewById(R.id.getPicBtn);

            getGallImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // boolean for if the user allowed for the permission
                    boolean hasPermission = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                    if (hasPermission){
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, getPicVal);
                    }else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
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
                        alertUser(getResources().getString(R.string.fb));
                    }
                }
            });
        }
        return root;
    }

    private void alertUser(String site) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(getResources().getString(R.string.confirmTitle));
        builder.setMessage(getResources().getString(R.string.sharingTo) + " " + site);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // How to share a picture info learned from: https://developers.facebook.com/docs/sharing/android?sdk=fbsdk
                ShareDialog shareDialog = new ShareDialog(getParentFragment());

                if (img != null) {
                    SharePhoto photo = new SharePhoto.Builder().setBitmap(img).build();

                    ShareContent shareContent = new ShareMediaContent.Builder().addMedium(photo).build();

                    shareDialog.show(shareContent, ShareDialog.Mode.AUTOMATIC);
                } else {
                    //Facebook Share link content builder for just text
                    shareDialog.show(new ShareLinkContent.Builder().build());
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(context, getResources().getString(R.string.cancel) + "ed Posting", Toast.LENGTH_SHORT).show(); // Canceled message
            }
        });
        builder.setIcon(R.mipmap.upload_icon);
        builder.show();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == getPicVal) {
            if (data != null) {
                Uri imgUri = data.getData();

                try {
                    img = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imgUri));

                    Log.d(log, "Image chosen was made into Bitmap");

                    getGallImg.setText(getResources().getString(R.string.picAdded));
                    getGallImg.setClickable(false);
                }
                catch (FileNotFoundException e) {
                    Log.e(log, e.getMessage());
                }
            }
            else {
                getGallImg.setText(getResources().getString(R.string.postPic));
                getGallImg.setClickable(false);
            }
        }
        else
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}