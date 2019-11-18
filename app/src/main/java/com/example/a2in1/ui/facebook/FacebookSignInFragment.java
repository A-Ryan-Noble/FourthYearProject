package com.example.a2in1.ui.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.example.a2in1.GlobalVariables;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import com.example.a2in1.R;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FacebookSignInFragment extends Fragment {

    private CallbackManager callbackManager;
    private GlobalVariables globalVar;
    private boolean isLoggedIn;

    private static String tag = "Facebook Sign in";

    private TextView loginTxt;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_facebook_sign_in_out, container, false);

        loginTxt  = root.findViewById(R.id.loginTxtView);

        LoginButton fbLoginBtn = root.findViewById(R.id.fbLoginButton);
        fbLoginBtn.setReadPermissions(Arrays.asList("email", "public_profile","user_status"));

        //Since the login button is inside a fragment, this allows for activity result to be controlled
        fbLoginBtn.setFragment(this);

        globalVar = (GlobalVariables) getApplicationContext();

        fbAccount();

        if (!isLoggedIn) {
            globalVar.setFbSignedIn(false);
            loginTxt.setText(getResources().getString(R.string.signInMsg));
        }
        else {
            globalVar.setFbSignedIn(true);
            loginTxt.setText(getResources().getString(R.string.fbSignedIn));
        }

        return root;
    }

    private void fbAccount() {
        callbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        isLoggedIn = accessToken != null && !accessToken.isExpired();

        // This allows for: Then you can later perform the actual login, such as in a custom button's OnClickListener:
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                globalVar.setFbSignedIn(true);
                Log.d(tag,"Facebook Login");
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException error) {
                Log.e(tag, error.toString());
            }
        });

        // Method dealing for when logout button is clicked
        new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {

                    globalVar.setFbSignedIn(false);
                    Log.d(tag, "FB Logout");
                    signOutAlert("Facebook");
                }
            }
        };
    }

    private void signOutAlert(final String accountName){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(getResources().getString(R.string.confirmTitle));
        builder.setMessage(getResources().getString(R.string.loggingOut) + " " + accountName);
        builder.show();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}