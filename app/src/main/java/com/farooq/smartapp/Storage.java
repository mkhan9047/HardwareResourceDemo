package com.farooq.smartapp;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {

    private static final String WIFI_NAME = null;
    private static final String PASSWORD = null;


    private Context context;

    public Storage(Context context) {
        this.context = context;
    }


    private SharedPreferences.Editor getPreferencesEditor() {
        return getsharedPreferences().edit();
    }

    private SharedPreferences getsharedPreferences() {
        return context.getSharedPreferences("Smart-Tracking", Context.MODE_PRIVATE);
    }


    public void saveWifiName(String p) {
        getPreferencesEditor().putString("WIFI_NAME", p).commit();
    }

    public String getWifiName() {
        return getsharedPreferences().getString("WIFI_NAME", WIFI_NAME);
    }


    public void savePassword(String p) {
        getPreferencesEditor().putString("PASSWORD", p).commit();
    }

    public String getPassword() {
        return getsharedPreferences().getString("PASSWORD", PASSWORD);
    }

}
