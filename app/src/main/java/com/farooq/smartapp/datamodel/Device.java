/*
 * Bluegiga’s Bluetooth Smart Android SW for Bluegiga BLE modules
 * Contact: support@bluegiga.com.
 *
 * This is free software distributed under the terms of the MIT license reproduced below.
 *
 * Copyright (c) 2013, Bluegiga Technologies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files ("Software")
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF 
 * ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A  PARTICULAR PURPOSE.
 */
package com.farooq.smartapp.datamodel;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import com.farooq.smartapp.Utils;

// Device - it's wrapper for BLE device object
public class Device {
    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt bluetoothGatt;
    private boolean connected;
    private boolean hasAdvertDetails;
    private int rssi;
    private byte[] advertData;

    public static int MAX_EXTRA_DATA = 3;

    private int duration;
    private int temperature;
    private int encode;
    private String devicestatus;
    private String serialNumber;
    private long lStartTime = 0;
    private long lUploadTime = 0;
    private int deviceType = 0;
    private String deviceId;
    public Device() {

    }

    public Device(BluetoothDevice bluetoothDevice, int rssi, byte[] advertisements) {
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
        this.connected = false;
        this.hasAdvertDetails = false;
        this.bluetoothGatt = null;
        this.advertData = advertisements;

        if (this.advertData == null) {
            return;
        }
        serialNumber =  Utils.bytesToHex(this.getAdvertData()).substring(56, 68).toString();
        serialNumber = Utils.hexToAscii(serialNumber);
        devicestatus = Utils.bytesToHex(this.getAdvertData()).substring(76, 78).toString();
        String strtime = Utils.bytesToHex(this.getAdvertData()).substring(78, 82).toString();
        String strEncoder = Utils.bytesToHex(this.getAdvertData()).substring(82, 86).toString();
        String strTemperature = Utils.bytesToHex(this.getAdvertData()).substring(90, 93).toString();
        deviceId = Utils.bytesToHex(this.getAdvertData()).substring(78, 94).toString();
        deviceId = Utils.hexToAscii(deviceId);

        setDuration(Utils.convertLittleEndian2Int(strtime));
        setEncode(Utils.convertLittleEndian2Int(strEncoder));
        setTemperature(Integer.parseInt(strTemperature, 16));

        lStartTime = System.currentTimeMillis();
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public boolean isPump(){
        String strPrefix = serialNumber.substring(0,2);
        if(strPrefix.equals("07")){
            return true;
        }
        return false;
    }

    public long getlUploadTime() {
        return lUploadTime;
    }

    public void setlUploadTime(long lUploadTime) {
        this.lUploadTime = lUploadTime;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void updateInfo(byte[] advertisements){

        String prestatus = devicestatus;
        String strHex = Utils.bytesToHex(this.getAdvertData());
        serialNumber =  Utils.bytesToHex(this.getAdvertData()).substring(56, 68).toString();
        serialNumber = Utils.hexToAscii(serialNumber);
        devicestatus = Utils.bytesToHex(this.getAdvertData()).substring(76, 78).toString();
        String strtime = Utils.bytesToHex(this.getAdvertData()).substring(78, 82).toString();
        String strEncoder = Utils.bytesToHex(this.getAdvertData()).substring(82, 86).toString();
        String strTemperature = Utils.bytesToHex(this.getAdvertData()).substring(86, 90).toString();
        deviceId = Utils.bytesToHex(this.getAdvertData()).substring(78, 94).toString();
        deviceId = Utils.hexToAscii(deviceId);

        String sign = strTemperature.substring(0, 2);

        int temp = Integer.parseInt(strTemperature, 16);
        temp = temp >> 2;

        if (sign.equals("11")) {
            temp = 16383 - temp;
        }
        else {

        }


        if (prestatus.equals("00") && devicestatus.equals("01")) {
            setDuration(Utils.convertLittleEndian2Int(strtime));
            lStartTime = System.currentTimeMillis();
        }
        else if(prestatus.equals("01") && devicestatus.equals("00")) {
            setDuration(Utils.convertLittleEndian2Int(strtime));
            lStartTime = 0;
        }


        setEncode(Utils.convertLittleEndian2Int(strEncoder));
        setTemperature(temp);
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getName() {
        return bluetoothDevice.getName();
    }

    public String getAddress() {
        return bluetoothDevice.getAddress();
    }

    public void setAdvertData(byte[] advertisements) {
        this.advertData = advertisements;
    }

    public byte[] getAdvertData() {
        return advertData;
    }

    public long getlStartTime() {
        return lStartTime;
    }

    public void setlStartTime(long lStartTime) {
        this.lStartTime = lStartTime;
    }
    //    public boolean hasAdvertDetails() {
//        return (hasAdvertDetails || advertData.size() > MAX_EXTRA_DATA);
//    }

    public void setAdvertDetails(boolean hasAdvertDetails) {
        this.hasAdvertDetails = hasAdvertDetails;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        if(temperature > 100)
            temperature = 0;
        this.temperature = temperature;
    }

    public int getEncode() {
        return encode;
    }

    public void setEncode(int encode) {
        this.encode = encode;
    }

    public String getDevicestatus() {
        return devicestatus;
    }

    public void setDevicestatus(String devicestatus) {
        this.devicestatus = devicestatus;
    }
}
