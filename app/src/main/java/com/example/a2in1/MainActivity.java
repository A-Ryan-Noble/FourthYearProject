package com.example.a2in1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.a2in1.fragmentRedirects.SettingsActivity;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;

import static com.example.a2in1.myPreferences.clearPrefs;
import static com.example.a2in1.myPreferences.getBoolPref;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private String log = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Customised callback of phone back button.
           - Stops from going back to previous view that redirected back to home
        */
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(log,"Phone back button clicked");
            }
        };

        getOnBackPressedDispatcher().addCallback(this,callback);

        setContentView(R.layout.nav_draw);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_FbButton, R.id.nav_TwitterButton, R.id.nav_FbContentButton, R.id.nav_FBPosting
        )
                .setDrawerLayout(drawer)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflates the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_settings:
                Log.d(log, "Settings Option selected");
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.logOutMenu:
                Log.d(log, "Logout Option selected");
                logoutOfSites();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // User is logged out of Facebook and Twitter if they're logged in
    public void logoutOfSites() {
        // Gets the SharedPreferences for both sites logged in status
        boolean isFbLoggedIn = getBoolPref("FBLoggedIn", false, getBaseContext());
        boolean isTwitterLoggedIn = getBoolPref("TwitterLoggedIn", false, getBaseContext());

        boolean loggedOut = false;

        // checks if facebook is logged in
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();

            myPreferences.setBoolPref("FBLoggedIn", false, getBaseContext());

            loggedOut = true;

            Log.d("Logout", "Logged out of Facebook");
        }

        // checks if Twitter is logged in
        if (isTwitterLoggedIn) {
            TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.twitter_CONSUMER_KEY), getResources().getString(R.string.twitter_CONSUMER_SECRET));

            TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                    .twitterAuthConfig(authConfig)
                    .build();
            Twitter.initialize(twitterConfig);

            TwitterCore.getInstance().getSessionManager().clearActiveSession();

            myPreferences.setBoolPref("TwitterLoggedIn", false, this);

            loggedOut = true;

            Log.d("Logout", "Logged out of Twitter");
        }

        if (loggedOut) {
            new Intent(getBaseContext(), MainActivity.class);

            /* Gets the users notification setting.
               - If they didn't change it then it will remain as true or it will change to true/false
            */
            boolean canNotifiy = getBoolPref("notificationEnabled", true, getBaseContext());

            // clears the users preferences
            clearPrefs(getBaseContext());

            if (canNotifiy) {
                Notifications.notify("Logged Out", "You have been logged out of your social media on 2in1","Socials logout",
                        1, this.getClass(), true, getBaseContext());
            } else {
                Toast.makeText(this, "Logout Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)|| super.onSupportNavigateUp();
    }
}