package com.example.a2in1.ui.twitter;

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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.a2in1.GlobalVariables;
import com.example.a2in1.R;
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
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import static com.facebook.FacebookSdk.getApplicationContext;

public class TwitterSignInFragment extends Fragment {

    private TextView txtView;
    private TwitterLoginButton twitterLoginBtn;
    private Button twitterSignOutBtn;
    private TwitterSession session;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Fragment frag;
    private GlobalVariables globalVar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_twitter_sign_in, container, false);

        frag = (Fragment)getFragmentManager().findFragmentById(R.id.twitterFragment);

        // Initializes the Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        globalVar = (GlobalVariables) getApplicationContext();

        twitterLoginBtn = (TwitterLoginButton) root.findViewById(R.id.login_button);

        twitterSignOutBtn = (Button) root.findViewById(R.id.logout_button);

        txtView = root.findViewById(R.id.loginTxtView);

        globalVar = (GlobalVariables) getApplicationContext();

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if (firebaseAuth.getCurrentUser() != null){
//                    checkTwitterLoggedIn();
                }
            }
        };

        updateBtns();

        twitterLoginBtn.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

//                ((Main2Activity)getActivity()).firebaseTwitterSessionSignIn(result.data,mAuth);

                firebaseTwitterSessionSignIn(result.data);

                twitterLoginBtn.setVisibility(View.VISIBLE);

                Log.e("twitterLogin","login Successful");
                globalVar.setTwitterSignedIn(true);

            }

            @Override
            public void failure(TwitterException exception) {
                globalVar.setTwitterSignedIn(false);
                Log.e("twitterLogin","login failed");

                updateBtns();
            }
        });
        return root;
    }

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

    //  Called  when the fragment is started
    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser curUser = mAuth.getCurrentUser();

        if (curUser!= null){
            updateBtns();
            updateUI();
        }
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void updateUI() {
        if (TwitterCore.getInstance().getSessionManager().getActiveSession() == null) {
            txtView.setText(getResources().getString(R.string.signInMsg));
            globalVar.setTwitterSignedIn(false);

            Log.e("1z","No session");
        }
        else {
            Log.e("1z","A session");
            twitterSignOutBtn.setEnabled(true);

            if (twitterSignOutBtn.isClickable()){
                Log.d("btn","logout clickable");
            }
            else {
                Log.d("btn","logout not clickable");
            }
            twitterSignOutBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d("logout","you clicked to logout");
                    twitterSignOut();
                }
            });
            Log.d("logout","you can logout");

            txtView.setText(getResources().getString(R.string.signedIn));
            globalVar.setTwitterSignedIn(true);
        }
    }

    //  Called before the fragment is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    private void updateBtns(){
        if (TwitterCore.getInstance().getSessionManager().getActiveSession() == null){
            twitterLoginBtn.setVisibility(View.VISIBLE);
            twitterSignOutBtn.setVisibility(View.INVISIBLE);
        }
        else {
            twitterLoginBtn.setVisibility(View.INVISIBLE);
            twitterSignOutBtn.setVisibility(View.VISIBLE);
        }
    }

    private void twitterSignOut(){
//        frag.requireActivity()
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(getResources().getString(R.string.confirmTitle));
        builder.setMessage(getResources().getString(R.string.loggingOut) + " Twitter");

        builder.setPositiveButton(R.string.ok,  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked ok & is logged out of twitter
                SessionManager<TwitterSession> sessionManager = TwitterCore.getInstance().getSessionManager();

                if (sessionManager.getActiveSession() != null){
                    mAuth.signOut();
                    sessionManager.clearActiveSession();
                }

                Log.d("Logout","Twitter Logout");

                updateBtns();
                updateUI();

                globalVar.setTwitterSignedIn(false);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        builder.show();
    }

    // method used for signing in to twitter using firebase
    public void firebaseTwitterSessionSignIn(TwitterSession session){
        AuthCredential credential = TwitterAuthProvider.getCredential(session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TwitterLogin","Signed in firebase twitter successful");

                        if (!task.isSuccessful()){
                            Log.d("TwitterLogin","Auth firebase twitter failed");

                        }
                    }
                });
    }
}