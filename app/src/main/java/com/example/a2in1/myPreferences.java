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

    public static int getIntPref(String key, int defaultValue, Context context){
        SharedPreferences mPreferences = context.getSharedPreferences("savedDataFile", MODE_PRIVATE);
        return mPreferences.getInt(key,defaultValue);
    }

    public static void setBoolPref(String key, boolean value, Context context){
        SharedPreferences mPreferences = context.getSharedPreferences("savedDataFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolPref(String key, boolean defaultValue, Context context){
        SharedPreferences mPreferences = context.getSharedPreferences("savedDataFile", MODE_PRIVATE);
        return mPreferences.getBoolean(key,defaultValue);
    }

    public static void setStringPref(String key, String value, Context context){
        SharedPreferences mPreferences = context.getSharedPreferences("savedDataFile",MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringPref(String key, String defaultValue, Context context){
        SharedPreferences mPreferences = context.getSharedPreferences("savedDataFile",MODE_PRIVATE);
        return mPreferences.getString(key,defaultValue);
    }

    public static void clearPrefs(Context context){
        SharedPreferences mPreferences = context.getSharedPreferences("savedDataFile",MODE_PRIVATE);
        mPreferences.edit().clear().commit();
    }
}
