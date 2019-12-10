package com.example.a2in1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private String log = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String sharedPrefFile = "savedDataFile";

        SharedPreferences mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_FbButton, R.id.nav_TwitterButton,R.id.nav_FbContentButton,R.id.nav_FBPosting
        )
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //makeNotify("Welcome to 2in1","Welcome to my App!","WelcomeMsg");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflates the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_settings:
//                Toast.makeText(this,"Settings Selected",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,SettingsActivity.class));
                return true;

            case R.id.logOutMenu: logoutOfSites();
                Toast.makeText(this,"Logout Selected", Toast.LENGTH_SHORT).show();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    // User is logged out of Facebook and Twitter
    public void logoutOfSites() {
        SharedPreferences mPreferences = getBaseContext().getSharedPreferences("savedDataFile", MODE_PRIVATE);

        // Gets the sharedprefrences for both sites logged in status
        boolean isFbLoggedIn = mPreferences.getBoolean("FBLoggedIn",false);
        boolean isTwitterLoggedIn = mPreferences.getBoolean("TwitterLoggedIn",false);

        boolean loggedOut = false;

        // checks if facebook is logged in
        if (isFbLoggedIn) {
            LoginManager.getInstance().logOut();

            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean("FBLoggedIn",false);
            editor.commit();

            loggedOut = true;

            Log.d("Logout","Logged out of Facebook");
        }

        // checks if Twitter is logged in
//        if (TwitterCore.getInstance().getSessionManager().getActiveSession() != null) {
        if (isTwitterLoggedIn){

            TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.twitter_CONSUMER_KEY), getResources().getString(R.string.twitter_CONSUMER_SECRET));

            TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                    .twitterAuthConfig(authConfig)
                    .build();
            Twitter.initialize(twitterConfig);

            TwitterCore.getInstance().getSessionManager().clearActiveSession();

            loggedOut = true;

            Log.d("Logout","Logged out of Twitter");
        }

        if (loggedOut == true) {
            new Intent(getApplicationContext(), MainActivity.class);

            // gets the users notification setting.
            // If they didn't change it then it will remain as true or it will change to true/false
            Boolean canNotifiy = mPreferences.getBoolean("notificationEnabled", true);

            if (canNotifiy) {
                showNotification("Logged Out", "You have been logged out of your Social media on 2in1");
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void showNotification(String title, String msg) {
        Log.d(log, "Notification method called");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String id = "2in1 Notification";
        NotificationChannel notificationChannel = new NotificationChannel(id, title, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(msg);

        notificationManager.createNotificationChannel(notificationChannel);

        Intent openIntent = new Intent(getBaseContext(), MainActivity.class);

        PendingIntent openApp = PendingIntent.getActivity(getBaseContext(), 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getBaseContext(), id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setLights(Color.BLUE, 1000, 1000)
                .setColor(Color.MAGENTA)
                .setContentIntent(openApp) // When notification is clicked it will open Main Activity
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL); // Vibrate,Sound & Lights are all set

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getBaseContext());
        notificationManagerCompat.notify(1000, notificationBuilder.build());
    }

}