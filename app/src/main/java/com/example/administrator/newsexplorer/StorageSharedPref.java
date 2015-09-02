package com.example.administrator.newsexplorer;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sunny on 7/9/2015.
 */
public class StorageSharedPref {
    private static final String MY_PREFS_NAME = "MyPrefsFile";
    Context c;
    StorageSharedPref(Context con){
        this.c = con;
    }
    public void StorePrefs(String key, String value){
        SharedPreferences.Editor editor = c.getSharedPreferences(MY_PREFS_NAME, c.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String GetPrefs(String key,String DefaultValue){
        SharedPreferences prefs = c.getSharedPreferences(MY_PREFS_NAME, c.MODE_PRIVATE);
        return  prefs.getString(key, DefaultValue);
    }

}
