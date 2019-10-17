package com.example.a2in1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.Arrays;

public class FBLoginActivity extends AppCompatActivity {
    private static final String EMAIL = "email";
    private static final String AUTH = "rerequest";


    private CallbackManager fbCallbackManger;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        fbCallbackManger = CallbackManager.Factory.create();

        LoginButton fbLoginBtn = (LoginButton) findViewById(R.id.login_button);

        fbLoginBtn.setReadPermissions(Arrays.asList(EMAIL));
        fbLoginBtn.setAuthType(AUTH);

        fbLoginBtn.registerCallback(fbCallbackManger, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onCancel() {
                setResult(RESULT_CANCELED);
                finish();
            }

            @Override
            public void onError(FacebookException error) {
                // TODO: Handle this exception in some manner
            }
        });
    }
}