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
import androidx.fragment.app.FragmentTransaction;

import com.example.a2in1.GlobalVariables;
import com.example.a2in1.Main2Activity;
import com.example.a2in1.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import static com.facebook.FacebookSdk.getApplicationContext;

public class TwitterSignInFragment extends Fragment {

    private TextView txtView;
    private TwitterLoginButton twitterLoginBtn;
    private Button signOut;
    private TwitterSession session;

    private View root; // global class access to the root view

    private GlobalVariables globalVar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((Main2Activity) getActivity()).frag = this;

        //Configures twitter sdk
        TwitterAuthConfig authConfig=new TwitterAuthConfig(getResources().getString(R.string.twitter_CONSUMER_KEY),getResources().getString(R.string.twitter_CONSUMER_SECRET));

        TwitterConfig twitterConfig=new TwitterConfig.Builder(getActivity())
                .twitterAuthConfig(authConfig)
                .build();
        Twitter.initialize(twitterConfig);

        root = inflater.inflate(R.layout.fragment_twitter_sign_in, container, false);

        globalVar = (GlobalVariables) getApplicationContext();

        twitterLoginBtn = (TwitterLoginButton) root.findViewById(R.id.login_button);
        signOut = (Button) root.findViewById(R.id.logout_button);

        txtView = root.findViewById(R.id.loginTxtView);

        globalVar = (GlobalVariables) getApplicationContext();

//        twitterAccount();

        if (checkTwitterLoggedIn()){
            txtView.setText(getResources().getString(R.string.signedIn)+ "\nTwitter");
            globalVar.setTwitterSignedIn(true);
        }
        else {
            txtView.setText(getResources().getString(R.string.signInMsg));
            globalVar.setTwitterSignedIn(false);

        }

//        return root;
//    }

//    private void twitterAccount(){
        twitterLoginBtn.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession twitterSession = result.data;

                session = TwitterCore.getInstance().getSessionManager().getActiveSession();

                TwitterAuthToken authToken = session.getAuthToken();

                String token = authToken.token;
                String secret = authToken.secret;

                globalVar.setTwitterSignedIn(true);

//                returnIntent.putExtra("result", "Successful");
//                setResult(RESULT_OK, returnIntent);
                globalVar.setTwitterSignedIn(true);

//                finish();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.e("Error",exception.toString());
//                returnIntent.putExtra("result", "Failed");
//                setResult(RESULT_OK, returnIntent);
                globalVar.setTwitterSignedIn(false);
            }
        });
        return root;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the fragment, which will then pass the result to the login
        // button.
        Fragment fragment = ((Main2Activity) getActivity()).frag ;
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public boolean checkTwitterLoggedIn(){

        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if ( session != null){
            Button signOut = (Button)root.findViewById(R.id.logout_button);

             /*
                Switches the available buttons if user is logged in they can logout.
                    The opposite is also true
              */
            twitterLoginBtn.setClickable(false);
            twitterLoginBtn.setVisibility(View.INVISIBLE);
            signOut.setClickable(true);
            signOut.setEnabled(true);
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    twitterSignOut();
                }
            });
            return true;
        }

        return false;
    }

    private void twitterSignOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getResources().getString(R.string.confirmTitle));
        builder.setMessage(getResources().getString(R.string.loggingOut) + " Twitter");
        builder.setPositiveButton(R.string.ok,  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked ok & is logged out of twitter
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
                Log.d("Logout","Twitter Logout");

                globalVar.setTwitterSignedIn(false);

//                setResult(RESULT_OK, returnIntent);
//                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        builder.show();
    }

    private void fragmentReload(){
        // Reload current fragment
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.detach(this).attach(this).commit();
    }
}