package com.example.a2in1;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.a2in1.feeds.DBHelper;
import com.example.a2in1.fragmentRedirects.SettingsActivity;
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;

import static com.example.a2in1.myPreferences.clearPrefs;
import static com.example.a2in1.myPreferences.getBoolPref;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private AppBarConfiguration mAppBarConfiguration;

    private String log = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.nav_draw);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_settings,
                R.id.nav_FbButton, R.id.nav_TwitterButton,
                R.id.nav_FbContentButton,R.id.nav_FBPosting,
                R.id.nav_TwitterContentButton, R.id.nav_TwitterPosting,
                R.id.nav_BothFeeds, R.id.nav_PostingBoth
        ).setDrawerLayout(drawer).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /* Uses Class SwipeListener to detect if the user swipes right or left:
            - If right then opens navigation draw otherwise they swiped left and thus close the navigation draw
         */
        drawer.setOnTouchListener(new SwipeListener(this){
            @Override
            public void onSwipeRight() {

                // If draw is not open on the left of the screen
                if (!drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.openDrawer(Gravity.LEFT);
                }
            }

            @Override
            public void onSwipeLeft() {
                // If draw is open on the left of the screen
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT);
                }
            }
        });

        // Custom icon for Options menu on toolbar
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.settings_icon);
        toolbar.setOverflowIcon(drawable);

        // Custom icon for navigation bar draw icon
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflates the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return super.onContextItemSelected(item);
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

            case R.id.db_DEV:
                Log.d(log, "Empty Database Option selected");

                DBHelper dbHelper = new DBHelper(this);
                dbHelper.emptyDB();

                Toast.makeText(this,item.getTitle() + " was clicked",Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // User is logged out of Facebook and/or Twitter if they're logged in
    public void logoutOfSites() {

        // Gets the SharedPreferences for both sites logged in status
        boolean isFbLoggedIn = getBoolPref("FBLoggedIn", false, getBaseContext());
        boolean isTwitterLoggedIn = getBoolPref("TwitterLoggedIn", false, getBaseContext());

        boolean loggedOut = false;

        DBHelper dbHelper = new DBHelper(this);

        // checks if facebook is logged in
        if (isFbLoggedIn){
            LoginManager.getInstance().logOut();

            myPreferences.setBoolPref("FBLoggedIn", false, getBaseContext());

            loggedOut = true;

            Log.d("Logout", "Logged out of Facebook");

            dbHelper.emptyDB();
            Log.d("Logout", "Empty Database Option selected");
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

            dbHelper.emptyDB();
            Log.d("Logout", "Empty Database Option selected");
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
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)|| super.onSupportNavigateUp();
    }
}