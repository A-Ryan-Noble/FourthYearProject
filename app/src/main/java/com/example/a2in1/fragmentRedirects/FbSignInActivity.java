package com.example.a2in1.fragmentRedirects;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a2in1.ui.facebook.FacebookSignInOutFragment;

public class FbSignInActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Replaces with the View Facebook sign in fragment
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new FacebookSignInOutFragment()).commit();
    }
}
