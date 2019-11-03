package com.example.a2in1.ui.twitter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.example.a2in1.FacebookSignIn;
import com.example.a2in1.GlobalVariables;
import com.example.a2in1.Main2Activity;
import com.example.a2in1.R;
import com.example.a2in1.TwitterSignIn;
import com.example.a2in1.ui.mainMenu.MainMenuFragment;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class TwitterSignInFragment extends Fragment {


    private TextView txtView;
    private TwitterLoginButton twitterLoginBtn;
    private Button twitterSignOutBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Choose authentication providers
//    AuthUI.IdpConfig providers;

    private static final int SigningIn = 0;

    private final String classTag = this.getClass().getName();

    private GlobalVariables globalVar;

    private static final int TWITTER_CODE = 2;

    Intent twitterIntent;

    public static TwitterSignInFragment newInstance() {
        return new TwitterSignInFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View roots = inflater.inflate(R.layout.activity_twitter_sign_in, container, false);

        return roots;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewModel root = ViewModelProviders.of(this).get(TwitterViewModel.class);

        //Configures twitter sdk
        TwitterAuthConfig authConfig=new TwitterAuthConfig(getResources().getString(R.string.twitter_CONSUMER_KEY),getResources().getString(R.string.twitter_CONSUMER_SECRET));

        TwitterConfig twitterConfig=new TwitterConfig.Builder(getContext())
                .twitterAuthConfig(authConfig)
                .build();
        Twitter.initialize(twitterConfig);

        if (TwitterCore.getInstance().getSessionManager().getActiveSession()== null){
            Intent twitterIntent = new Intent(getContext(), TwitterSignIn.class);
            startActivityForResult(twitterIntent, TWITTER_CODE);
        }

        else {
            Log.e("ZZZZ","logged in");
            Intent otherIntent = new Intent(getContext(), MainMenuFragment.class);
            startActivity(otherIntent);
        }

    }

    //        private void checkTwitterLoggedIn(){
//        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
//
//        if ( session == null){
//            twitterLoginBtn.setVisibility(View.VISIBLE);
//            twitterSignOutBtn.setVisibility(View.INVISIBLE);
//
//            txtView.setText(getResources().getString(R.string.signInMsg));
//            globalVar.setTwitterSignedIn(false);
//
//            Log.e("1z","No session");
//        }
//        else {
//            Log.e("1z","A session");
//            twitterLoginBtn.setVisibility(View.INVISIBLE);
//            twitterSignOutBtn.setVisibility(View.VISIBLE);
//
//            twitterSignOutBtn.setEnabled(true);
//
//            if (twitterSignOutBtn.isClickable()){
//                Log.d("btn","logout clickable");
//            }
//            else {
//                Log.d("btn","logout not clickable");
//            }
//            twitterSignOutBtn.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Log.d("logout","you clicked to logout");
//                    twitterSignOut();
//                }
//            });
//            Log.d("logout","you can logout");
//
//            txtView.setText(getResources().getString(R.string.signedIn));
//            globalVar.setTwitterSignedIn(true);
//        }
//    }
//
//    private void twitterSignOut(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//
//        builder.setTitle(getResources().getString(R.string.confirmTitle));
//        builder.setMessage(getResources().getString(R.string.loggingOut) + " Twitter");
//
//        builder.setPositiveButton(R.string.ok,  new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                 User clicked ok & is logged out of twitter
//                SessionManager<TwitterSession> sessionManager = TwitterCore.getInstance().getSessionManager();
//
//                if (sessionManager.getActiveSession() != null){
//                    sessionManager.clearActiveSession();
//                }
//
//                Log.d("Logout","Twitter Logout");
//
//                checkTwitterLoggedIn();
//
//            }
//        });
//
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {}
//        });
//        builder.show();
//    }
//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_CANCELED | resultCode != RESULT_OK) {
            Log.d(null, "Activity returned: Not Okay / Cancelled");
        } else {
            if (requestCode == TWITTER_CODE) {
                if (intent.getStringExtra("result") == "Failed") {
                    Log.d(null, "Twitter Activity: Failed");
                } else if (intent.getStringExtra("result") == "Cancelled") {

                } else {
                    // logged in
                    Log.d(null, "Twitter Activity: Logged In");
                }
            }
        }
    }
}
     /*   // Initializes the Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        globalVar = (GlobalVariables) getApplicationContext();

        twitterLoginBtn = (TwitterLoginButton)findViewById(R.id.login_button);

        twitterSignOutBtn = (Button) root.findViewById(R.id.logout_button);
        twitterSignOutBtn.setEnabled(true);

        twitterSignOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(classTag,"you clicked to logout");
                twitterSignOut();
            }
        });

        txtView = findViewById(R.id.loginTxtView);

        globalVar = (GlobalVariables) getApplicationContext();

        updateBtns();

        twitterLoginBtn.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.e("twitterLogin","login Successful");

                twitterSignIn();

                updateBtns();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.e("twitterLogin","login failed");
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateBtns();
            }
        };
    }

    private void twitterSignOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(getResources().getString(R.string.confirmTitle));
        builder.setMessage(getResources().getString(R.string.loggingOut) + " Twitter");

        builder.setPositiveButton(R.string.ok,  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ((Main2Activity)getActivity()).signOut();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        builder.show();
    }

*//*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginBtn.onActivityResult(requestCode, resultCode, data);
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Pass the activity result to the fragment, which will then pass the result to the login
//        // button.
//        Fragment fragment = getFragmentManager().findFragmentById(R.id.twitterFragment);
//        if (fragment != null) {
//            twitterLoginBtn.onActivityResult(requestCode, resultCode, data);
//        }
//    }

//    private void checkTwitterLoggedIn(){
//        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
//
//        if ( session == null){
//            twitterLoginBtn.setVisibility(View.VISIBLE);
//            twitterSignOutBtn.setVisibility(View.INVISIBLE);
//
//            txtView.setText(getResources().getString(R.string.signInMsg));
//            globalVar.setTwitterSignedIn(false);
//
//            Log.e("1z","No session");
//        }
//        else {
//            Log.e("1z","A session");
//            twitterLoginBtn.setVisibility(View.INVISIBLE);
//            twitterSignOutBtn.setVisibility(View.VISIBLE);
//
//            twitterSignOutBtn.setEnabled(true);
//
//            if (twitterSignOutBtn.isClickable()){
//                Log.d("btn","logout clickable");
//            }
//            else {
//                Log.d("btn","logout not clickable");
//            }
//            twitterSignOutBtn.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Log.d("logout","you clicked to logout");
//                    twitterSignOut();
//                }
//            });
//            Log.d("logout","you can logout");
//
//            txtView.setText(getResources().getString(R.string.signedIn));
//            globalVar.setTwitterSignedIn(true);
//        }
//    }
*//*

    //  Called  when the fragment is started
    @Override
    public void onStart() {
        super.onStart();
        Log.d(classTag,"twitter fragment started");
        mAuth.addAuthStateListener(mAuthListener);
    }

    //  Called before the fragment is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    private void updateBtns(){
//        if (TwitterCore.getInstance().getSessionManager().getActiveSession() == null){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            twitterLoginBtn.setVisibility(View.VISIBLE);
            twitterSignOutBtn.setVisibility(View.INVISIBLE);

            Log.e(classTag,"No session");

            txtView.setText(getResources().getString(R.string.signInMsg));
            globalVar.setTwitterSignedIn(false);
        }
        else {
            twitterLoginBtn.setVisibility(View.INVISIBLE);
            twitterSignOutBtn.setVisibility(View.VISIBLE);
            twitterSignOutBtn.setEnabled(true);

            Log.e(classTag,"A session");

            txtView.setText(getResources().getString(R.string.signedIn));
            globalVar.setTwitterSignedIn(true);

            Log.d(classTag,"you can logout");
        }
    }

    *//*
        A method used for signing in using Firebase's AuthUI
            - Creates a sign in intent with the preferred sign-in methods. ie Twitter
     *//*
    public void twitterSignIn(){
        // Choose authentication providers

        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .build(),
                SigningIn);
    }
    *//*    private void firebaseTwitterSessionSignIn(TwitterSession session){
        AuthCredential credential = TwitterAuthProvider.getCredential(session.getAuthToken().token, session.getAuthToken().secret);

        mAuth.signInWithCredential(credential).addOnCompleteListener(this.getActivity(),new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(classTag,"Signed in firebase twitter successful");

                        if (!task.isSuccessful()){
                            Log.d(classTag,"Auth firebase twitter failed");
                        }
                    }
                });
    }*//*

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SigningIn){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK){
                // Successfully Signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(classTag,"This user signed in with Twitter");

            }
            else
            {
                Log.d(classTag,"Result of Sign in Intent wasn't OK");

                String error = response.getError().getMessage();
                Log.e(classTag,error);
            }
            updateBtns();
        }
    }


    //        @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.twitterFragment);
//
//        if (fragment != null) {
//            fragment.onActivityResult(requestCode, resultCode, data);
//        }
//
//        else Log.d("twitterLogin", "Twitter SignIn Fragment is null");
//    }
}*/