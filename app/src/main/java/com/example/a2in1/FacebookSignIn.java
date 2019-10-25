package com.example.a2in1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class FacebookSignIn extends AppCompatActivity {
//public class FacebookSignIn extends AccountsSignInOut {

    private CallbackManager callbackManager;
    private Intent returnIntent;
    private LoginButton fbLoginBtn;
    private GlobalVariables globalVar;
    private boolean isLoggedIn;

    TextView loginTxt;

    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_sign_in);

        loginTxt  = findViewById(R.id.loginTxtView);

        fbLoginBtn = findViewById(R.id.fbLoginButton);
        fbLoginBtn.setReadPermissions(Arrays.asList("email", "public_profile"));

        globalVar = (GlobalVariables) getApplicationContext();
        returnIntent = new Intent();

        fbAccount();

        if (!isLoggedIn) {
            globalVar.setFbSignedIn(false);
            loginTxt.setText(getResources().getString(R.string.signInMsg));
        }
        else {
            globalVar.setFbSignedIn(true);
            loginTxt.setText(getResources().getString(R.string.signedIn) + "\nFacebook");
        }
    }

    protected void fbAccount() {
        callbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        isLoggedIn = accessToken != null && !accessToken.isExpired();

        // This allows for: Then you can later perform the actual login, such as in a custom button's OnClickListener:
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                returnIntent.putExtra("result", "Successful");
                setResult(RESULT_OK, returnIntent);
                globalVar.setFbSignedIn(true);
                finish();
            }

            @Override
            public void onCancel() {
                returnIntent.putExtra("result", "Cancelled");
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }

            @Override
            public void onError(FacebookException error) {
                returnIntent.putExtra("result", "Failed");
                setResult(RESULT_CANCELED,returnIntent);

                Log.e("Exception",error.toString());
                finish();
            }
        });

        // Method dealing for when logout button is clicked
        new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {

                    returnIntent.putExtra("result", "Successful");
                    setResult(RESULT_OK, returnIntent);
                    globalVar.setFbSignedIn(false);
                    Log.d("Logout", "FB Logout");
                    signOutAlert("Facebook");
                    finish();
                }
            }
        };
    }

    private void signOutAlert(final String accountName){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.confirmTitle));
        builder.setMessage(getResources().getString(R.string.loggingOut) + " " + accountName);
        builder.show();
    }

    public void goBack(View view){
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