package com.example.a2in1.ui.settingsMenu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.example.a2in1.About2in1;
import com.example.a2in1.R;
import static com.example.a2in1.myPreferences.setIntPref;
import static com.example.a2in1.myPreferences.setBoolPref;

public class SettingsScreen extends PreferenceFragmentCompat {

    private String log = getClass().getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference);

        EditTextPreference fbFeedAmount = findPreference("MaxFbPosts"); // mac post limit

        EditTextPreference maxTweetsAmount = findPreference("MaxTweets"); // max tweet limit

        Preference aboutApp = findPreference("aboutApp"); // Account Item

        SwitchPreference notifEnabled = findPreference("notificationSwitch"); // notification switch

        // Limit to the posts preference from Facebook is set
        fbFeedAmount.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int num = Integer.parseInt(newValue.toString().trim());

                if (num > 0) {
                    Log.d(log, "FB Posts limited to " + num);

                    setIntPref("MaxFbNum",num,getContext());

                    return true;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.alert));
                builder.setMessage(getResources().getString(R.string.feedAmountAlertMsg)+" "+ num + ".");
                builder.setIcon(R.mipmap.error_icon);
                builder.show();

                return false;
            }
        });

        // Limit to the tweets preference from Twitter is set
        maxTweetsAmount.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int num = Integer.parseInt(newValue.toString().trim());

                if (num > 0) {
                    Log.d(log,"Tweets limited to "+ num);

                    setIntPref("MaxTweetsNum",num,getContext());

                    return true;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.alert));
                builder.setMessage(getResources().getString(R.string.feedAmountAlertMsg)+" "+ num + ".");
                builder.setIcon(R.mipmap.error_icon);
                builder.show();

                return false;
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

        // User clicks to turn on/off notifications
        notifEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d(log,"Notifications enabled = " + newValue);

                boolean enabled = (boolean)newValue;
                setBoolPref("notificationEnabled",enabled,getContext());

                return true;
            }
        });
    }
}
