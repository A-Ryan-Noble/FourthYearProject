package com.example.a2in1.ui.settingsMenu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.a2in1.fragmentRedirects.About2in1;
import com.example.a2in1.R;
import com.example.a2in1.fragmentRedirects.AccSettings;
import com.example.a2in1.fragmentRedirects.NotifySettings;

public class SettingsScreen extends PreferenceFragmentCompat {

    private String log = getClass().getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.preference);

        Preference aboutApp = findPreference("aboutApp"); // Account Item

        Preference accounts = findPreference("accounts"); // Account Item

        Preference notify = findPreference("notify"); // Account Item

        // User clicks to go to their view what the app is about
        aboutApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(log,"@string/about2in1App" + " screen called");

                startActivity(new Intent(getContext(), About2in1.class));

                return true;
            }
        });

        // User clicks to go to their view their account settings
        accounts.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(log,"@string/accountSection" + " screen called");

                startActivity(new Intent(getContext(), AccSettings.class));

                return true;
            }
        });

        // User clicks to go to their view their notification settings
        notify.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(log,"@string/notify" + " screen called");

                startActivity(new Intent(getContext(), NotifySettings.class));

                return true;
            }
        });
    }
}
