package com.enyteam.abc.smartkitchen.Storage;

import android.content.Context;
import android.content.SharedPreferences;

public class SmartSharedPreference {

    public static final String PREFS_NAME = "com.enyteam.abc.smartkitchen";
    public static final String PREFS_KEY = "UID";
    public static final String STORE_KEY = "STORE_ID";
    public static final String STORE_PHONE = "STORE_PHONE";
    public static final String STORE_CUST_NAME = "CUST_NAME";
    public static final String STORE_CUST_ADDR = "CUST_ADDR";

    public SmartSharedPreference() {
        super();
    }

    public void saveUID(Context context, String text) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        if(settings.getString(PREFS_KEY, null)!=null) {
            clearSharedPreference(context);
        }
        editor = settings.edit();
        editor.putString(PREFS_KEY, text);
        editor.commit();
    }

    public String getUID(Context context) {
        SharedPreferences settings;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString(PREFS_KEY, null);
    }

    public void clearSharedPreference(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.commit();
    }

    public int getPreferedStoreKey(Context context) {
        SharedPreferences settings;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getInt(STORE_KEY, 0);
    }

    public void savePreferedStoreKey(Context context, int key) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit();
        editor.putInt(STORE_KEY, key);
        editor.commit();
    }

    public void clearPreferedStore(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit();
        editor.remove(STORE_KEY);
        editor.remove(STORE_PHONE);
        editor.commit();
    }

    public long getPreferedStorePhone(Context context) {
        SharedPreferences settings;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getLong(STORE_PHONE, 0);
    }

    public void savePreferedStorePhone(Context context, long phone) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit();
        editor.putLong(STORE_PHONE, phone);
        editor.commit();
    }

    public void saveCustomerDetails(Context context, String name, String address) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit();
        editor.putString(STORE_CUST_NAME, name);
        editor.putString(STORE_CUST_ADDR, address);
        editor.commit();
    }
}
