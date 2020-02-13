package com.farooq.smartapp.wifi_connector;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;

public class WifiUtils {

    private Context context;
    private String networkSSID;
    private String networkPassword;

    public WifiUtils(String networkSSID, String networkPassword, Context context) {
        this.networkSSID = networkSSID;
        this.context = context;
        this.networkPassword = networkPassword;
    }

    public void connectToWifi(){
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "\"" + networkSSID + "\"";
        wifiConfiguration.wepKeys[0] = "\"" + networkPassword + "\"";
        wifiConfiguration.wepTxKeyIndex = 0;
        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wifiConfiguration.preSharedKey = "\""+ networkPassword +"\"";
        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(wifiConfiguration);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }

    interface wifiListener{
        void onConnectSuccess();
        void onConnectError();
    }

}
