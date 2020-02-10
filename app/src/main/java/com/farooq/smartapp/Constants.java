package com.farooq.smartapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.farooq.smartapp.model.Tablet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

public class Constants {


    public final static boolean IS_TESTING = false;

    public final static String Key_Success = "success";
    public final static String Key_Message = "message";
    public final static String Key_Tablet = "tablet";
    public final static String Key_Instrument = "instrument";


    public static String service_uuid = "dcc2e754-6619-4eb3-86d4-6c8402df1862";
    public static String characteristic_uuid = "049c0650-d543-4636-9652-a0201913f2c8";

    private final static String Tablet_Register_Info = "Tablet_Register_Name_Response_Server";

    public final static String Tablet_STORAGE_KEY = "Tablet_Local_Storage_Key";

    public static String getTabletRegisterId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.Tablet_STORAGE_KEY, 0);
        String tablet_Register_info = sharedPreferences.getString(Constants.Tablet_Register_Info, null);
        try {
            if (tablet_Register_info != null) {
                JSONObject tablet_Register_jsoninfo = new JSONObject(tablet_Register_info);
                return tablet_Register_jsoninfo.getString("id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Tablet getTablet(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.Tablet_STORAGE_KEY, 0);
        String tablet_Register_info = sharedPreferences.getString(Constants.Tablet_Register_Info, null);

        try {
            if (tablet_Register_info != null) {

                Gson gson = new Gson();
                Type listType = new TypeToken<Tablet>() {
                }.getType();
                Tablet tablet = gson.fromJson(tablet_Register_info, listType);

                return tablet;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void SetTabletInfo(Context context, String tabletIdJSON) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.Tablet_STORAGE_KEY, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.Tablet_Register_Info, tabletIdJSON);
        editor.apply();
    }

    public static void setFragment(AppCompatActivity activity, Fragment fragment) {

        activity.getSupportFragmentManager().popBackStack();
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (conMgr != null) {
            netInfo = conMgr.getActiveNetworkInfo();
        }
        if (netInfo == null) {
            Toast.makeText(context,"Please check your connectivity.",Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
