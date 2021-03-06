package com.example.a2in1.ui.settingsMenu;

import android.content.Context;
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

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        final String log = getClass().getSimpleName();

        final Context context = getContext();

        addPreferencesFromResource(R.xml.preference);

        Preference aboutApp = findPreference("aboutApp"); // About screen

        Preference accounts = findPreference("accounts"); // User social media account settings

        Preference notify = findPreference("notify"); // App Notification Settings

        // User clicks to go to their view what the app is about
        aboutApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(log,"@string/about2in1App" + " screen called");

                startActivity(new Intent(context, About2in1.class));

                return true;
            }
        });

        // User clicks to go to their view their account settings
        accounts.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(log,"@string/accountSection" + " screen called");

                startActivity(new Intent(context, AccSettings.class));

                return true;
            }
        });

        // User clicks to go to their view their notification settings
        notify.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(log,"@string/notify" + " screen called");

                startActivity(new Intent(context, NotifySettings.class));

                return true;
            }
        });
    }
}
