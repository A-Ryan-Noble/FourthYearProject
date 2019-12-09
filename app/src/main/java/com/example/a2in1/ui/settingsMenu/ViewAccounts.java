package com.example.a2in1.ui.settingsMenu;


import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

import com.example.a2in1.R;

public class ViewAccounts extends PreferenceFragmentCompat {
    private String log = getClass().getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.view_accounts);

        //
    }
}
