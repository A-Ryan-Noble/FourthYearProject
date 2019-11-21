package com.example.a2in1.ui.facebook;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.a2in1.R;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class FacebookPosting extends Fragment {

    private CallbackManager callbackManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_facebook_posting, container, false);

        final CheckBox checkedText = root.findViewById(R.id.checkedText);
        String oldText =getResources().getString(R.string.postWish);
        String username = Profile.getCurrentProfile().getFirstName();
        checkedText.setText("I, "+username+ " "+ oldText);

        Button PostBtn = root.findViewById(R.id.fbPostSubmitBtn);
        PostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkedText.isChecked()) {
                    Toast.makeText(getContext(), "Must select okay!", Toast.LENGTH_SHORT).show();
                } else {
                    confirmPost();
                }
            }
        });

        return root;
    }

    private void confirmPost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(getResources().getString(R.string.confirmTitle));
        builder.setMessage("Facebook share window will Open");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                callbackManager = CallbackManager.Factory.create();
                ShareDialog shareDialog = new ShareDialog(getParentFragment());

                //Facebook Share link content builder for just text
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .build();
                shareDialog.show(content);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getContext(), getResources().getString(R.string.cancel) + "ed Post", Toast.LENGTH_SHORT).show(); // cancel message
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}