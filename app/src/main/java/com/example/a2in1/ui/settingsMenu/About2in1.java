package com.example.a2in1.ui.settingsMenu;


import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.a2in1.R;

public class About2in1 extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.about_app);
    }
}
