package com.example.a2in1.ui.settingsMenu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.a2in1.R;

import static android.content.Context.MODE_PRIVATE;

public class SettingsScreen extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference);

        EditTextPreference feedAmtFrmUser = findPreference("MaxPosts");

       feedAmtFrmUser.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int num = Integer.parseInt(newValue.toString().trim());

                Toast.makeText(getContext(),"Num entered was: "+num,Toast.LENGTH_SHORT).show();

                SharedPreferences mPreferences = getContext().getSharedPreferences("savedDataFile", MODE_PRIVATE);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt("MaxNum",num);
                editor.commit();

                return true;
            }
        });


    }
}
