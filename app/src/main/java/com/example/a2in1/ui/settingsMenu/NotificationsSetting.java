package com.example.a2in1.ui.settingsMenu;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.example.a2in1.R;

import static com.example.a2in1.myPreferences.setBoolPref;

public class NotificationsSetting extends PreferenceFragmentCompat {
    private String log = getClass().getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.notifications_settings);

        final Context context = getContext();

        final SwitchPreference notifEnabled = findPreference("notificationSwitch"); // notification switch

        // User clicks to turns on/off the notifications
        notifEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d(log, "Notifications enabled = " + newValue);

                final boolean isEnabled = (boolean) newValue;
                setBoolPref("notificationEnabled", isEnabled, context);

                final Preference soundEnabled = findPreference("soundSwitch"); // sound switch

                final Preference vibrateEnabled = findPreference("vibrateSwitch"); // vibrate switch

                final Preference lightEnabled = findPreference("lightSwitch"); // light switch

                if (isEnabled) {
                    notifEnabled.setIcon(R.mipmap.active_notifications_icon);

                    // User clicks to turns on/off the sound
                    soundEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference otherPreference, Object otherValue) {
                            Log.d(log, "Sound enabled = " + otherValue);

                            boolean isEnabled2 = (boolean) otherValue;
                            setBoolPref("soundEnabled", isEnabled2, context);

                            // Icons downloaded from https://icons8.com
                            if (isEnabled2) {
                                soundEnabled.setIcon(R.mipmap.sound_on_icon_foreground);
                            } else {
                                soundEnabled.setIcon(R.mipmap.sound_off_icon_foreground);
                            }
                            return true;
                        }
                    });

                    // User clicks to turns on/off the vibration
                    vibrateEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference otherPreference, Object otherValue) {
                            Log.d(log, "Vibrate enabled = " + otherValue);

                            boolean isEnabled2 = (boolean) otherValue;
                            setBoolPref("vibrateEnabled", isEnabled2, context);

                            if (isEnabled2) {
                                vibrateEnabled.setIcon(R.mipmap.vibrate_on_icon_round);
                            }
                            // Icon downloaded from https://icons8.com
                            else {
                                vibrateEnabled.setIcon(R.mipmap.vibrate_off_icon_round);
                            }
                            return true;
                        }
                    });

                    // User clicks to turns on/off the light
                    lightEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference otherPreference, Object otherValue) {
                            Log.d(log, "Light enabled = " + otherValue);

                            boolean isEnabled2 = (boolean) otherValue;
                            setBoolPref("lightEnabled", isEnabled2, context);

                            // Icons downloaded from https://icons8.com
                            if (isEnabled2) {
                                lightEnabled.setIcon(R.mipmap.light_on_icon_foreground);
                            } else {
                                lightEnabled.setIcon(R.mipmap.light_off_icon_foreground);
                            }
                            return true;
                        }
                    });

                } else {
                    notifEnabled.setIcon(R.mipmap.inactive_notifications_icon);
                }

                return true;
            }
        });
    }
}