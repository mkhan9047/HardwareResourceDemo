package com.farooq.smartapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import java.util.UUID;

public class Utils {

    public static int convertLittleEndian2Int(String hexStr){
        int retVal = 0;
        int len = hexStr.length() / 2;
        if (hexStr.equals("FFFFFFFF"))
        {
            retVal = 0;
            return retVal;
        }
        for (int i = 0; i < len; i++){
            int curval = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 2), 16);
            retVal = retVal + curval  * (int)(Math.pow(2, 8 * i));
        }
        return retVal;

    }

    public static String convertInt2LittleEndian(int value){
        String retStr = "";
        String hexStr = Integer.toString(value, 16);
        int halflen = hexStr.length() / 2;
        int len = hexStr.length();
        for(int i = 0; i < halflen; i++){
            String strtmp = hexStr.substring(len - (i + 1) * 2,  len - i * 2);
            retStr = String.format("%s%S", strtmp, retStr);
        }
        if (len % 2 == 1) {
            retStr = String.format("0%s%s", hexStr.substring(0, 1), retStr);
        }
        if (retStr.length() < 4) {
            retStr = retStr + "00";
        }
        return retStr;
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    // Converts string given in hexadecimal system to byte array
    public static byte[] hexToByteArray(String hex) {
        byte byteArr[] = new byte[hex.length() / 2];
        for (int i = 0; i < byteArr.length; i++) {
            int temp = Integer.parseInt(hex.substring(i * 2, (i * 2) + 2), 16);
            byteArr[i] = (byte) (temp & 0xFF);
        }
        return byteArr;
    }

    public static String hexToAscii(String hexStr){
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i+=2){
            String str = hexStr.substring(i, i + 2);
            output.append((char)Integer.parseInt(str, 16));
        }

        return output.toString();
    }
    public static void TextviewSubtextSmallSize(TextView tv, String tv_text, String tv_sub_text){

        try {
            SpannableString ss1=  new SpannableString(tv_text);
            ss1.setSpan(new RelativeSizeSpan(2f), 0,tv_text.indexOf(tv_sub_text) -1, 0); // set size
            ss1.setSpan(new ForegroundColorSpan(Color.WHITE), 0, tv_text.indexOf(tv_sub_text) -1, 0);// set color

            tv.setText(ss1);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                tv.setText(tv_text);
            } catch (Exception e1) {
                e.printStackTrace();
            }
        } catch (Error e){
            e.printStackTrace();
            try {
                tv.setText(tv_text);
            } catch (Exception e1) {
                e.printStackTrace();
            }
        }

    }

    private static final int START_INDEX_UUID = 4;
    private static final int END_INDEX_UUID = 8;
    public static final String BLUETOOTH_BASE_UUID_PREFIX = "0000";
    public static final String BLUETOOTH_BASE_UUID_POSTFIX = "-0000-1000-8000-00805F9B34FB";

    // Converts UUID from 128-bit to 16-bit form
    public static String convert128to16UUID(String uuid) {
        return uuid.substring(START_INDEX_UUID, END_INDEX_UUID);
    }

    // Returns UUID text in 16 bits version if it is standard Bluetooth UUID or
    // in 128 bits form if not
    public static String getUuidText(UUID uuid) {
        String strUuid = uuid.toString().toUpperCase();

        if (strUuid.startsWith(Utils.BLUETOOTH_BASE_UUID_PREFIX)
                && strUuid.endsWith(Utils.BLUETOOTH_BASE_UUID_POSTFIX)) {
            return "0x" + Utils.convert128to16UUID(strUuid);
        } else {
            return strUuid;
        }
    }

    public static boolean permission_check_only(Context context, String[] permission_array){

        boolean one_by_one_check = true;

        if(android.os.Build.VERSION.SDK_INT >= 23){
            for (int i = 0; i < permission_array.length; i++) {

                if (ActivityCompat.checkSelfPermission(context, permission_array[i]) != PackageManager.PERMISSION_GRANTED) {
                    // Request missing location permission.
                    one_by_one_check = false;
                }
            }
        }

        return  one_by_one_check;
    }


}
