package com.example.a2in1;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a2in1.ui.settingsMenu.SettingsScreen;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Replaces with the Setting screen
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsScreen()).commit();
    }
}
