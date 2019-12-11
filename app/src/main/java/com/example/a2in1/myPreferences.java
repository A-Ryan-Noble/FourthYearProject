package com.example.a2in1;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class myPreferences {

    public static void setIntPref(String key, int value, Context context){
        SharedPreferences mPreferences = context.getSharedPreferences("savedDataFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getIntPref(String key, int defaultVal, Context context){
        SharedPreferences mPreferences = context.getSharedPreferences("savedDataFile", MODE_PRIVATE);
        return mPreferences.getInt(key,defaultVal);
    }

    public static void setBoolPref(String key, boolean value, Context context){
        SharedPreferences mPreferences = context.getSharedPreferences("savedDataFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolPref(String key, boolean defaultVal, Context context){
        SharedPreferences mPreferences = context.getSharedPreferences("savedDataFile", MODE_PRIVATE);
        return mPreferences.getBoolean(key,defaultVal);
    }
}
