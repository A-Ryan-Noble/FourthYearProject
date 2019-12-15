package com.example.a2in1.fragmentRedirects;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NotifySettings extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Replaces with the View Notification settings fragment
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new com.example.a2in1.ui.settingsMenu.NotificationsSetting()).commit();
    }
}