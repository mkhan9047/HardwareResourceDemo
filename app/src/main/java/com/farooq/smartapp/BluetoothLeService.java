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

package com.farooq.smartapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.farooq.smartapp.datamodel.Device;
import com.farooq.smartapp.datamodel.Engine;

import java.util.UUID;

// BluetoothLeService - manages connections and data communication with given Bluetooth LE devices.
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private boolean mScanning;
    private Handler mHandler;
    private final IBinder mBinder = new LocalBinder();

    // These constant members are used to sending and receiving broadcasts
    // between BluetoothLeService and rest parts of application
    public static final String ACTION_START_SCAN = BuildConfig.APPLICATION_ID + ".ACTION_START_SCAN";
    public static final String ACTION_STOP_SCAN = BuildConfig.APPLICATION_ID + ".bledemo.ACTION_STOP_SCAN";
    public static final String ACTION_DEVICE_DISCOVERED = BuildConfig.APPLICATION_ID + ".ACTION_DEVICE_DISCOVERED";
    public static final String ACTION_GATT_CONNECTED = BuildConfig.APPLICATION_ID + ".ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = BuildConfig.APPLICATION_ID + ".ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_CONNECTION_STATE_ERROR = BuildConfig.APPLICATION_ID + ".ACTION_GATT_CONNECTION_STATE_ERROR";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = BuildConfig.APPLICATION_ID + ".ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_DATA_AVAILABLE = BuildConfig.APPLICATION_ID + ".ACTION_DATA_AVAILABLE";
    public static final String ACTION_DATA_WRITE = BuildConfig.APPLICATION_ID + ".ACTION_DATA_WRITE";
    public static final String ACTION_READ_REMOTE_RSSI = BuildConfig.APPLICATION_ID + ".ACTION_READ_REMOTE_RSSI";
    public static final String ACTION_DESCRIPTOR_WRITE = BuildConfig.APPLICATION_ID + ".ACTION_DESCRIPTOR_WRITE";

    // These constant members are used to sending and receiving extras from
    // broadcast intents
    public static final String SCAN_PERIOD = "scanPeriod";
    public static final String DISCOVERED_DEVICE = "discoveredDevice";
    public static final String DEVICE = "device";
    public static final String DEVICE_ADDRESS = "deviceAddress";
    public static final String RSSI = "rssi";
    public static final String UUID_CHARACTERISTIC = "uuidCharacteristic";
    public static final String UUID_DESCRIPTOR = "uuidDescriptor";
    public static final String GATT_STATUS = "gattStatus";
    public static final String SCAN_RECORD = "scanRecord";
    public static final String SHOULD_RETRY_CONNECTION = "retryConnection";

    public static final UUID PUMP_SERVICE_UUID = UUID.fromString("dcc2e754-6619-4eb3-86d4-6c8402df1862");
    public static final UUID RFID_SERVICE_UUID = UUID.fromString("9b3d54f4-281a-4e20-833b-aeef41a9b0d0");
    public static final UUID PUMP_CHARACTERISTIC_UUID = UUID.fromString("049c0650-d543-4636-9652-a0201913f2c8");

    // Implements callback method for scan BLE devices
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        // Called when new UUID-filtered BLE device is discovered
        // Broadcast intent is sent with following extras: device , rssi,
        // additional advertise data
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.i("BLE service", "Found pump controller @ " + device.getAddress());
            Intent broadcastIntent = new Intent(ACTION_DEVICE_DISCOVERED);
            broadcastIntent.putExtra(DISCOVERED_DEVICE, device);
            broadcastIntent.putExtra(RSSI, rssi);
            broadcastIntent.putExtra(SCAN_RECORD, scanRecord);
            sendBroadcast(broadcastIntent);
        }
    };

    // Implements callback methods for GATT events that the app cares about.
    // For example,
    // connection status has changed, services are discovered,etc...
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        // Called when device has changed connection status and appropriate
        // broadcast with device address extra is sent
        // It can be either connected or disconnected state
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("BLE service", "onConnectionStateChange - status: " + status + " - new state: " + newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Device device = Engine.getInstance().getDevice(gatt);
                    device.setConnected(true);
                    Intent updateIntent = new Intent(ACTION_GATT_CONNECTED);
                    updateIntent.putExtra(DEVICE_ADDRESS, device.getAddress());
                    sendBroadcast(updateIntent);
                    gatt.discoverServices();
                    Log.e("*********", "Bluetooth device is connected. *************************************");
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Device device = Engine.getInstance().getDevice(gatt);
                    device.setConnected(false);
                    Intent updateIntent = new Intent(ACTION_GATT_DISCONNECTED);
                    updateIntent.putExtra(DEVICE_ADDRESS, device.getAddress());
                    sendBroadcast(updateIntent);
                    Log.e("*********", "Bluetooth device is disconnected. ***************************************");
                }
            } else {
                Device device = Engine.getInstance().getDevice(gatt);
                Intent updateIntent = new Intent(ACTION_GATT_CONNECTION_STATE_ERROR);
                updateIntent.putExtra(DEVICE_ADDRESS, device.getAddress());
                sendBroadcast(updateIntent);
                if (status == 133) {
                    // StackOverflow has an endless supply of complaints about this
                    Log.d("BLE service", "Connection temporarily failed, will retry");
                    connect(device);
                }
            }
        }

        // Called when services are discovered on remote device
        // If success broadcast with device address extra is sent
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i("BLE service", "onServicesDiscovered - status: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Device device = Engine.getInstance().getDevice(gatt);
                Intent updateIntent = new Intent(ACTION_GATT_SERVICES_DISCOVERED);
                updateIntent.putExtra(DEVICE_ADDRESS, device.getAddress());
                sendBroadcast(updateIntent);
            }
        }

        // Called when characteristic was read
        // Broadcast with characteristic uuid and status is sent
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i("BLE service", "onCharacteristicRead - status: " + status + "  - UUID: " + characteristic.getUuid());
            Intent updateIntent = new Intent(ACTION_DATA_AVAILABLE);
            updateIntent.putExtra(UUID_CHARACTERISTIC, characteristic.getUuid().toString());
            updateIntent.putExtra(GATT_STATUS, status);
            sendBroadcast(updateIntent);
        }

        // Called when characteristic was written
        // Broadcast with characteristic uuid and status is sent
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i("BLE service", "onCharacteristicWrite - status: " + status + "  - UUID: " + characteristic.getUuid());
            Intent updateIntent = new Intent(ACTION_DATA_WRITE);
            updateIntent.putExtra(UUID_CHARACTERISTIC, characteristic.getUuid().toString());
            updateIntent.putExtra(GATT_STATUS, status);
            sendBroadcast(updateIntent);
        }

        // Called when remote device rssi was read
        // If success broadcast with device address extra is sent
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Log.i("BLE service", "onReadRemoteRssi - status: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Device device = Engine.getInstance().getDevice(gatt);
                device.setRssi(rssi);
                Intent updateIntent = new Intent(ACTION_READ_REMOTE_RSSI);
                updateIntent.putExtra(DEVICE_ADDRESS, device.getAddress());
                sendBroadcast(updateIntent);
            }
        }

        // Called when descriptor was written
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.i("BLE service", "onDescriptorWrite - status: " + status + "  - UUID: " + descriptor.getUuid());
            Intent updateIntent = new Intent(ACTION_DESCRIPTOR_WRITE);
            updateIntent.putExtra(GATT_STATUS, status);
            updateIntent.putExtra(UUID_DESCRIPTOR, descriptor.getUuid());
            sendBroadcast(updateIntent);
        }

        // Called when notification has been sent from remote device
        // Broadcast with characteristic uuid is sent
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i("BLE service", "onCharacteristicChanged - status: " + "  - UUID: " + characteristic.getUuid());
            Intent updateIntent = new Intent(ACTION_DATA_AVAILABLE);
            updateIntent.putExtra(UUID_CHARACTERISTIC, characteristic.getUuid().toString());
            sendBroadcast(updateIntent);
        }
    };

    // Starts scanning for new BLE devices
    public void startScanning(final int scanPeriod) {

        mHandler.postDelayed(new Runnable() {
            // Called after scanPeriod milliseconds elapsed
            // It stops scanning and sends broadcast
            @Override
            public void run() {
//                mScanning = false;
//                mBluetoothAdapter.stopLeScan(mLeScanCallback);
//
//                Intent broadcastIntent = new Intent(ACTION_STOP_SCAN);
//                sendBroadcast(broadcastIntent);
            }
        }, scanPeriod);
        Log.i("BLE service", "Started scanning w/UUID filter");
        mScanning = true;
        mBluetoothAdapter.startLeScan(new UUID[] {RFID_SERVICE_UUID}, mLeScanCallback);
        //mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    public void stopScanning() {
        Log.i("BLE service", "Stopped scanning");
        mScanning = false;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);

        Intent broadcastIntent = new Intent(ACTION_STOP_SCAN);
        sendBroadcast(broadcastIntent);
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that
        // BluetoothGatt.close() is called
        // such that resources are cleaned up properly. In this particular
        // example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    // Initializes class members. It is called only once when application is
    // starting.
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through BluetoothManager.

        mHandler = new Handler();
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    // -----------------------------------------------------------------------
    // Following methods are available from app and they operate tasks related
    // to Bluetooth Low Energy technology
    // -----------------------------------------------------------------------

    // Connects to given device
    public boolean connect(Device device) {
        if (mBluetoothAdapter == null || device == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // make sure we are NOT reusing BluetoothGatt (too slow most of the time, for no good reason)
        if (device.getBluetoothGatt() != null) {
            device.getBluetoothGatt().close();
            device.setBluetoothGatt(null);
            try {
                Thread.sleep(200); // Is 200 ms enough?
            } catch (final InterruptedException e) {
                // Ignore
            }
        }

        // If BluetoothGatt object is null, creates new object
        // else calls connect function on this object
        if (device.getBluetoothGatt() == null) {
            Log.i("BLE service", "connect() is fresh, creating new BluetoothGatt instance");
            BluetoothGatt bluetoothGatt = device.getBluetoothDevice().connectGatt(this, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
            device.setBluetoothGatt(bluetoothGatt);
        } else {
            Log.i("BLE service", "connect() is repeated, using existing BluetoothGatt instance");
//            device.getBluetoothGatt().close();
//            device.setBluetoothGatt(null);
//            try {
//                Thread.sleep(200); // Is 200 ms enough?
//            } catch (final InterruptedException e) {
//                // Ignore
//            }
//            BluetoothGatt bluetoothGatt = device.getBluetoothDevice().connectGatt(this, false, mGattCallback);
//            device.setBluetoothGatt(bluetoothGatt);
            device.getBluetoothGatt().connect();
        }

        return true;
    }

    // Disconnects from given device
    public void disconnect(Device device) {
        device.getBluetoothGatt().disconnect();
    }

    private final Object mLock = new Object();
    private boolean mInitialConnection;
    private boolean mUserDisconnected;

    public void connect1(Device device){
        //synchronized (mLock) {
            if (device.getBluetoothGatt() != null) {
                Log.i("BLE service", "connect1() is repeated, existing BluetoothGatt instance may be used");
                // There are 2 ways of reconnecting to the same device:
                // 1. Reusing the same BluetoothGatt object and calling connect() - this will force the autoConnect flag to true
                // 2. Closing it and reopening a new instance of BluetoothGatt object.
                // The gatt.close() is an asynchronous method. It requires some time before it's finished and
                // device.connectGatt(...) can't be called immediately or service discovery
                // may never finish on some older devices (Nexus 4, Android 5.0.1).
                // If shouldAutoConnect() method returned false we can't call gatt.connect() and have to close gatt and open it again.
                if (!mInitialConnection) {
                    Log.i("BLE service", "connect1() is deleting and recreating BluetoothGatt instance");
                    device.getBluetoothGatt().close();
                    device.setBluetoothGatt(null);
                    try {
                        Thread.sleep(200); // Is 200 ms enough?
                    } catch (final InterruptedException e) {
                        // Ignore
                    }
                } else {
                    // Instead, the gatt.connect() method will be used to reconnect to the same device.
                    // This method forces autoConnect = true even if the gatt was created with this flag set to false.
                    Log.i("BLE service", "connect1() is reusing existing BluetoothGatt instance");
                    mInitialConnection = false;
                    device.getBluetoothGatt().connect();
                    return;
                }
            } else {
                Log.i("BLE service", "connect1() is fresh, creating new BluetoothGatt instance");
                BluetoothGatt bluetoothGatt = device.getBluetoothDevice().connectGatt(this, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
                device.setBluetoothGatt(bluetoothGatt);
            }
        //}

        final boolean shouldAutoConnect = shouldAutoConnect();
        mUserDisconnected = !shouldAutoConnect; // We will receive Linkloss events only when the device is connected with autoConnect=true
        // The first connection will always be done with autoConnect = false to make the connection quick.
        // If the shouldAutoConnect() method returned true, the manager will automatically try to reconnect to this device on link loss.
        if (shouldAutoConnect)
            mInitialConnection = true;
    }

    protected boolean shouldAutoConnect() {
        return false;
    }

    public boolean disconnect1(Device device) {
        mUserDisconnected = true;
        mInitialConnection = false;

        //synchronized (mLock) {
            if (device.getBluetoothGatt() != null) {

                device.getBluetoothGatt().disconnect();
                //device.getBluetoothGatt().close();
                mInitialConnection = false;
                return true;
            }
        //}

        return false;
    }

    // Reads value for given characteristic
    public void readCharacteristic(Device device, BluetoothGattCharacteristic charact) {
        device.getBluetoothGatt().readCharacteristic(charact);
    }

    // Writes value for given characteristic
    public boolean writeCharacteristic(Device device, BluetoothGattCharacteristic charact) {
        return device.getBluetoothGatt().writeCharacteristic(charact);
    }

    // Enables or disables characteristic notification
    public boolean setCharacteristicNotification(Device device, BluetoothGattCharacteristic charact, boolean enabled) {
        return device.getBluetoothGatt().setCharacteristicNotification(charact, enabled);
    }

    // Writes value for given descriptor
    public boolean writeDescriptor(Device device, BluetoothGattDescriptor descriptor) {
        return device.getBluetoothGatt().writeDescriptor(descriptor);
    }

    // Reads rssi for given device
    public boolean readRemoteRssi(Device device) {
        return device.getBluetoothGatt().readRemoteRssi();
    }

    // Close all established connections
    public void close() {
        for (Device device : Engine.getInstance().getDevices()) {
            if (device.getBluetoothGatt() != null) {
                device.getBluetoothGatt().close();
                device.setBluetoothGatt(null);
            }
        }
    }

    // Checks if service is currently scanning for new BLE devices
    public boolean isScanning() {
        return mScanning;
    }

//    public static boolean is_valid_service(BluetoothGattService service_is) {
//        String mServiceUUid = Utils.getUuidText(service_is.getUuid());
//        String mServiceUUid1 = service_is.getUuid().toString();
//        if (!TextUtils.isEmpty(mServiceUUid) || !TextUtils.isEmpty(mServiceUUid1)) {
//
//            try {
//                if (mServiceUUid.toUpperCase().equalsIgnoreCase(Constants.service_uuid.toUpperCase()) ||
//                        mServiceUUid1.toUpperCase().equalsIgnoreCase(Constants.service_uuid.toUpperCase())) {
//                    return true;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }
//
//    public static boolean is_valid_charecteristics(BluetoothGattCharacteristic chat_is) {
//        String mChartUUid = Utils.getUuidText(chat_is.getUuid());
//        String mChartUUid1 = chat_is.getUuid().toString();
//        if (!TextUtils.isEmpty(mChartUUid) || TextUtils.isEmpty(mChartUUid1)) {
//
//            if (mChartUUid.toUpperCase().equalsIgnoreCase(Constants.characteristic_uuid.toUpperCase()) || mChartUUid1.toUpperCase().equalsIgnoreCase(Constants.characteristic_uuid.toUpperCase())) {
//                return true;
//            }
//        }
//        return false;
//    }
}
