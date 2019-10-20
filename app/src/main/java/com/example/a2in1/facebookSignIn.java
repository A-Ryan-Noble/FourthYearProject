package com.example.a2in1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class facebookSignIn extends AppCompatActivity {

    private CallbackManager callbackManager;
    Intent returnIntent;
    private LoginButton fbLoginBtn;

    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_sign_in);

        returnIntent = new Intent();

        fbLoginBtn = findViewById(R.id.fbLoginButton);
        fbLoginBtn.setReadPermissions(Arrays.asList(EMAIL));

        callbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        //callback registration
        fbLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code here
            }

            @Override
            public void onCancel() {
                // App code here
            }

            @Override
            public void onError(FacebookException error) {
                // App code here
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                returnIntent.putExtra("result", "Successful");
                setResult(RESULT_OK, returnIntent);
            }

            @Override
            public void onCancel() {
                returnIntent.putExtra("result", "Cancelled");
                setResult(RESULT_OK, returnIntent);
            }

            @Override
            public void onError(FacebookException error) {
                returnIntent.putExtra("result", "Failed");
                setResult(RESULT_OK, returnIntent);
                // App code here
            }
        });
    }

    public void goBack(View view){

//        Intent temp = returnIntent;
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
