package com.example.a2in1.fragmentRedirects;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class About2in1 extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Replaces with the View About the app fragment
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new com.example.a2in1.ui.settingsMenu.About2in1()).commit();
    }
}