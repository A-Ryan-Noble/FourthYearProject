package com.example.a2in1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import com.example.a2in1.ui.mainMenu.SettingsMenu;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            case R.id.action_settings: new SettingsMenu().menuChosen(item,"Settings");
                Toast.makeText(this,"Settings Selected",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logOutMenu: Notification("Logged out","You have just been logged out of both Facebook and Twitter");
                Toast.makeText(this,"Logout Selected",Toast.LENGTH_SHORT).show();
                return true;
//            case R.id.action_settings: Toast.makeText(this,"Settings Selected",Toast.LENGTH_SHORT).show(); return true;
//            case R.id.logOutMenu: Toast.makeText(this,"Logout Selected",Toast.LENGTH_SHORT).show(); return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    // Notification Method that creates a notification on the user's phone
    public void Notification(String title, String msg){

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String id = "channel1";
        NotificationChannel notificationChannel = new NotificationChannel(id, "channel1Name", NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription("Description of channel");

        if (notificationChannel != null){
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent openIntent = new Intent(getBaseContext(),MainActivity.class);
        PendingIntent openApp = PendingIntent.getActivity(getBaseContext(),0,openIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getBaseContext(), id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setLights(Color.BLUE, 1000, 1000)
                .setColor(Color.RED)
                .setContentIntent(openApp) // When notification is clicked it will open Main Activity
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL); // Vibrate,Sound & Lights are set

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getBaseContext());
        notificationManagerCompat.notify(1000, notificationBuilder.build());
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}