package com.example.a2in1.ui.settingsMenu;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.a2in1.R;
import com.example.a2in1.feeds.DBHelper;

import static com.example.a2in1.myPreferences.setIntPref;

public class AccountsSetting extends PreferenceFragmentCompat {
    private String log = getClass().getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.accounts_settings);

        EditTextPreference fbFeedAmount = findPreference("MaxFbPosts"); // mac post limit

        EditTextPreference maxTweetsAmount = findPreference("MaxTweets"); // max tweet limit

        // Limit to the posts preference from Facebook is set
        fbFeedAmount.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int num = Integer.parseInt(newValue.toString().trim());

                if (num > 0) {
                    Log.d(log, "FB Posts limited to " + num);

                    setIntPref("MaxFbNum",num,getContext());

                    // Remove instances in DB (removes crash error when viewing feed after change)
                    DBHelper db = new DBHelper(getContext());
                    db.deleteSiteData("Facebook");

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

                    // Remove instances in DB (removes crash error when viewing feed after change)
                    DBHelper db = new DBHelper(getContext());
                    db.deleteSiteData("Twitter");
                    db.deleteSiteData("TwitterTimeline");

                    return true;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.alert));
                builder.setMessage(getResources().getString(R.string.feedAmountAlertMsg)+" "+ num + ".");
                builder.setIcon(R.mipmap.error_icon);
                builder.show();

                return false;
                };
            });
    }
}