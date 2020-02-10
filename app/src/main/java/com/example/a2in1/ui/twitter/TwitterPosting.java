package com.example.a2in1.ui.twitter;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a2in1.MainActivity;
import com.example.a2in1.R;
import com.example.a2in1.api.MyTwitterApiClient;
import com.example.a2in1.models.Post;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.TwitterApi;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.a2in1.myPreferences.getBoolPref;

public class TwitterPosting extends Fragment {

    final private int getPicVal = 0;

    private Uri imgUri;
    private Button getGallImg;

    private Bitmap img;

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

            final CheckBox checkedText = root.findViewById(R.id.checkedText);

            Button postBtn = root.findViewById(R.id.postingSubmitBtn);

            // The text inputted by the user
            final EditText msgInput = root.findViewById(R.id.msgInput);
            String msg = msgInput.getText().toString();

            // User clicks to post to the site
            postBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkedText.isChecked()) {
                        Toast.makeText(context, "Must select okay!", Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(context,"You entered: " +msgInput.getText().toString(),Toast.LENGTH_SHORT).show();

                        alertUser(getResources().getString(R.string.twitter),msgInput.getText().toString());
                    }
                }
            });
        }

        return root;
    }


    private void alertUser(String site, final String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(getResources().getString(R.string.confirmTitle));
        builder.setMessage(getResources().getString(R.string.sharingTo) + " " + site);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                TweetComposer.Builder builder = new TweetComposer.Builder(context);

                if (img != null) {
                    builder.image(imgUri);
                }

                builder.text(msg);

                builder.show();
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
                imgUri = data.getData();

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
        else {
            Log.d(log,"not pic getting act was the result");
        }
    }
}