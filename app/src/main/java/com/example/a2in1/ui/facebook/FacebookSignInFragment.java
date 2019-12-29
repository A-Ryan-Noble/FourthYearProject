package com.example.a2in1.ui.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;

import com.example.a2in1.MainActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import com.example.a2in1.R;
import com.squareup.picasso.Picasso;

import static com.example.a2in1.myPreferences.getStringPref;
import static com.example.a2in1.myPreferences.setBoolPref;
import static com.example.a2in1.myPreferences.setStringPref;

public class FacebookSignInFragment extends Fragment {

    private CallbackManager callbackManager;

    private boolean isLoggedIn;

    private String log = getClass().getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // customised callback of phone back button
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(log,"Phone back button clicked. Redirecting to Home Screen");
                startActivity(new Intent(getContext(),MainActivity.class));
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this,callback);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_facebook_sign_in_out, container, false);

        TextView loginTxt  = root.findViewById(R.id.loginTxtView);

        LoginButton fbLoginBtn = root.findViewById(R.id.fbLoginButton);
        fbLoginBtn.setReadPermissions(Arrays.asList("email", "public_profile","user_status"));

        //Since the login button is inside a fragment, this allows for activity result to be controlled
        fbLoginBtn.setFragment(this);

        ImageView profile = (ImageView)root.findViewById(R.id.FbProfilePic);

        fbAccount();

        if (!isLoggedIn) {
            profile.setVisibility(View.INVISIBLE);
            loginTxt.setText(getResources().getString(R.string.signInMsg));
        }
        else {
            profile.setVisibility(View.VISIBLE);

            int width = profile.getMaxWidth();
            int height = profile.getMaxHeight();

            Profile user = Profile.getCurrentProfile(); // The longeed in users Profile

            String userID = user.getId();

            String username = user.getName();

            try {
                URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?width=" + width + "&height=" + height);
                Picasso.with(getContext()).load(imageURL.toString()).into(profile);

                Log.d(log,username+ "'s profile pic was loaded and displayed");
            }
            catch (MalformedURLException e){
                Log.e(log,e.getMessage());
            }

            String oldMsg[] = getResources().getString(R.string.fbSignedIn).split("[\\r\\n]+");

            // signedIn message with the logged in user's profile name in the middle of it
            String signedIngMsg = oldMsg[0] + " " + username + ". " +oldMsg[1];

            loginTxt.setText(signedIngMsg);
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
                // Logged in status put into SharedPreferences for later
                setBoolPref("FBLoggedIn",true,getContext());

                // Puts Facebook user into shared preference
                String username = Profile.getCurrentProfile().getName();
                setStringPref("FbUsername",username,getContext());

                Log.d(log,"Facebook Logged in");

                startActivity(new Intent(getContext(), MainActivity.class));
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException error) {
                Log.e(log, error.toString());
            }
        });

        // Method dealing for when logout button is clicked
        new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {

                    // Logged in status put into SharedPreferences for later
                    setBoolPref("FBLoggedIn",false,getContext());

                    Log.d(log, "FB Logout");
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

        startActivity(new Intent(getContext(), MainActivity.class));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}