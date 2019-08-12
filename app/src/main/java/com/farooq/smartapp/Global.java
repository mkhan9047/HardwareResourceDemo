package com.farooq.smartapp;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.SharedPreferences;

import com.farooq.smartapp.datamodel.xml.Characteristic;
import com.farooq.smartapp.datamodel.xml.Service;

import java.util.HashMap;

public class Global {

    public static BluetoothLeService mBluetoothLeService = null;
    public static   BluetoothGattCharacteristic mBluetoothCharact = null;
    public static  Characteristic mCharact = null;
    public static  Service mService = null;
    public static HashMap<String, String> gHashMapDeviceMapping = new HashMap<>();

    public static  boolean g_bTempF = true;
    public static void saveDeviceMapping(Context context) {
        SharedPreferences sf = context.getSharedPreferences("pumpapp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();

        String strData = "";
        int index = 0;
        for (String key : Global.gHashMapDeviceMapping.keySet()) {
            String strAddr = Global.gHashMapDeviceMapping.get(key);
            String strItem = String.format("%s-%s", key, strAddr);
            if (index == 0) {
                strData = strItem;
            }
            else {
                strData = String.format("%s;%s", strData, strItem);
            }
        }

        editor.putString("device_mapping", strData);
        editor.commit();
    }

    public static void readDeviceMapping(Context context) {
        SharedPreferences sf = context.getSharedPreferences("pumpapp", Context.MODE_PRIVATE);
        String strData = sf.getString("device_mapping", "");
        if (!strData.isEmpty()) {
            String[] arrMapping = strData.split(";");
            for (int i = 0; i < arrMapping.length; i++) {
                String strItem = arrMapping[i];
                String[] arrItem = strItem.split("-");
                Global.gHashMapDeviceMapping.put(arrItem[0], arrItem[1]);
            }
        }

    }
}
