package com.michnovka.dockontrol.androidgw.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private final String status = "STATUS";
    private final String url = "URL";
    private final String secret = "SECRET";
    private static final String BOOT_KAY = "BOOT_KAY";

    @SuppressLint("CommitPrefEdits")
    public SharedPreferenceHelper(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public boolean setUrl(String value){
        editor.putString(url, value);
        return editor.commit();
    }

    public String getUrl(){
        return preferences.getString(url, null);
    }

    public boolean setSecret(String value){
        editor.putString(secret, value);
        return editor.commit();
    }

    public String getSecret(){
        return preferences.getString(secret, null);
    }

    public boolean setStatus(boolean value){
        editor.putBoolean(status, value);
        return editor.commit();
    }

    public boolean getStatus(){
        return preferences.getBoolean(status, false);
    }

    public boolean setBoot(boolean value){
        editor.putBoolean(BOOT_KAY, value);
        return editor.commit();
    }

    public boolean getBoot(){
        return preferences.getBoolean(BOOT_KAY, false);
    }
}
