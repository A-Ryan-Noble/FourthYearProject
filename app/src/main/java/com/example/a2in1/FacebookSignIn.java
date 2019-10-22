package com.example.a2in1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class FacebookSignIn extends AppCompatActivity {

    private CallbackManager callbackManager;
    Intent returnIntent;
    private LoginButton fbLoginBtn;

    TextView loginTxt;

    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_sign_in);

        Button goBack = findViewById(R.id.mainMenu);

        loginTxt  = findViewById(R.id.loginTxtView);

        fbLoginBtn = findViewById(R.id.fbLoginButton);
        fbLoginBtn.setReadPermissions(Arrays.asList(EMAIL));

        callbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

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

        //        This allows for: Then you can later perform the actual login, such as in a custom button's OnClickListener:
        //        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        final GlobalVariables globalVariable = (GlobalVariables) getApplicationContext();

        returnIntent = new Intent();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                returnIntent.putExtra("result", "Successful");
                setResult(RESULT_OK, returnIntent);
                globalVariable.setFbSignedIn(true);
                finish();

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

        if (!isLoggedIn) {
            globalVariable.setFbSignedIn(false);
        loginTxt.setText(getResources().getString(R.string.signInMsg));
        }
        else {
            globalVariable.setFbSignedIn(true);
            loginTxt.setText(getResources().getString(R.string.signedIn) + "\nFacebook");
        }
    }

    public void goBack(View view){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "Cancelled");
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}