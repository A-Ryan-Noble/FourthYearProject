package com.example.a2in1.ui.settingsMenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.example.a2in1.About2in1;
import com.example.a2in1.R;

import static android.content.Context.MODE_PRIVATE;

public class SettingsScreen extends PreferenceFragmentCompat {

    private String log = getClass().getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference);

        final SharedPreferences mPreferences = getContext().getSharedPreferences("savedDataFile", MODE_PRIVATE);

        EditTextPreference fbFeedAmount = findPreference("MaxFbPosts"); // mac post limit

        EditTextPreference maxTweetsAmount = findPreference("MaxTweets"); // max tweet limit

        Preference aboutApp = findPreference("aboutApp"); // Account Item

        SwitchPreference notifEnabled = findPreference("notificationSwitch"); // notification switch

        // Limit to the posts preference from Facebook is set
        fbFeedAmount.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int num = Integer.parseInt(newValue.toString().trim());

                Log.d(log,"FB Posts limited to "+ num);

                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt("MaxFbNum",num);
                editor.commit();

                return true;
            }
        });

        // Limit to the tweets preference from Twitter is set
        maxTweetsAmount.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int num = Integer.parseInt(newValue.toString().trim());

                Log.d(log,"Tweets limited to "+ num);

                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt("MaxTweetsNum",num);
                editor.commit();

                return true;
            }
        });

        // User clicks to go to their view their accounts
        aboutApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(log,"@string/about2in1App" + " screen called");

                startActivity(new Intent(getContext(), About2in1.class));

                return true;
            }
        });

        notifEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d(log,"Notifications enabled = " + newValue);

                boolean enabled = (boolean)newValue;

                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("notificationEnabled",enabled);
                editor.commit();

                return true;
            }
        });
    }
}
