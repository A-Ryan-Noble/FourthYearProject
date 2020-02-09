package com.example.a2in1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.a2in1.myPreferences.getBoolPref;

public class Notifications {

    // Creates a notification on the user's phone
    public static void notify(String title, String msg, String id, int id_int, Class className, boolean openable, Context context) {
        Log.d("Notifications", "Notification method called");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = new NotificationChannel(id, title, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(msg);

        notificationManager.createNotificationChannel(notificationChannel);

        Intent openIntent = new Intent(context,className);

        PendingIntent openApp = PendingIntent.getActivity(context, id_int, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, id)
                .setSmallIcon(R.mipmap.notification_icon)
                .setContentTitle(title)
                .setContentText(msg)
                .setLights(Color.BLUE, 2000, 1000)
                .setColor(context.getResources().getColor(R.color.tealCol)) // Teal colour in hexadecimal
                .setContentIntent(openApp) // When notification is clicked it will open to the passed activity
                .setAutoCancel(true);

        boolean isSoundEnabled = getBoolPref("soundEnabled", true, context);
        boolean isVibrateEnabled = getBoolPref("vibrateEnabled", true, context);
        boolean isLightEnabled = getBoolPref("lightEnabled", true, context);

        if (openable){
            notificationBuilder.setContentIntent(openApp);// When notification is clicked it will open
        }

        /*
         Checks to see if all the device methods of alerting the user are enabled in app settings:
             if all are enabled then all are set as notification default
        */
        if (isSoundEnabled && isVibrateEnabled && isLightEnabled) {
            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        }

        // checks the individual methods if they're enabled
        else {
            if (isSoundEnabled) {
                notificationBuilder.setDefaults(Notification.DEFAULT_SOUND); // Sound is set to notification builder
            }
            if (isVibrateEnabled) {
                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE); // Vibrate is set to notification builder
            }
            if (isLightEnabled) {
                notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS); // Light is set to notification builder
            }
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(id_int, notificationBuilder.build());
    }

    public static void notifyDownload(String userMsg, Context context) {

        if (getBoolPref("notificationEnabled", true, context)) {
            Notifications.notify("Feed Updated ", userMsg + "downloaded",
                    "FB feed Download", 1000, MainActivity.class, true, context);
        } else {
            Toast.makeText(context, "Feed Updated " + userMsg + " downloaded", Toast.LENGTH_SHORT).show();
        }

    }
}