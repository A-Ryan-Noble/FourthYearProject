package com.example.a2in1.ui.mainMenu;

import android.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class SettingsMenu {

    private String tag = this.getClass().getSimpleName();

    public void menuChosen(@NonNull MenuItem item,String option){
      if (option.equals("Logout")) {
          Log.d(tag, "Log out chosen");
      }
      if (option.equals("Settings")){
        Log.d(tag,"Settings");
      }
    }

//            AuthCredential credential = TwitterAuthProvider.getCredential(session.getAuthToken().token, session.getAuthToken().secret);

    // User is logged out of Facebook and Twitter
    private void logoutOfSites(){
//        AlertDialog.
    }
}
