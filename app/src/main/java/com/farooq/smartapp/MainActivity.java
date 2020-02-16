package com.farooq.smartapp;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.atp.rfreaderinterface.RFReader;
import com.crashlytics.android.Crashlytics;
import com.farooq.smartapp.datamodel.Device;
import com.farooq.smartapp.datamodel.Engine;
import com.farooq.smartapp.fragment.InstrumentFragment;
import com.farooq.smartapp.model.ProcedureObj;
import com.farooq.smartapp.model.ProcedureObj.ChangeType;
import com.farooq.smartapp.server.ApiConstant;
import com.farooq.smartapp.server.WebServices;
import com.farooq.smartapp.utils.DialogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.signalr.Action3;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.fabric.sdk.android.Fabric;

import static com.farooq.smartapp.Constants.IS_TESTING;
import static com.farooq.smartapp.Constants.checkInternetConnection;

//HubEventListener,
public class MainActivity extends BaseActivity implements View.OnClickListener, InternetConnectivityListener {

    public static final int SCAN_PERIOD = 10000;
    private static final int BlUETOOTH_SETTINGS_REQUEST_CODE = 100;
    private static IntentFilter bleIntentFilter;
    private final int PERMISSION_All_REQUEST_CODE = 67;
    RFReader mPort;
    //    public static final String CMD_WAKE = "00";
//    public static final String CMD_SLEEP = "020720420401002938";
//    public static final String CMD_SELET_TAG = "02052014009F9D";
//    public static final String CMD_LOOP_TAG = "0205211400C541";
    int iRecLines = 0;
    private ProcedureAdapter mAdapter;
    public ArrayList<ProcedureObj> mArrayProcedureList = new ArrayList<>();
    private RecyclerView lstProcedure;
    private SwipeRefreshLayout pullToRefresh;
    private FrameLayout fragment_Container;
    //Bluetooth related variable
    private Dialog mDialog;
    private boolean bleIsSupported = true;
    private String[] permissions = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"};
    private long lLastEventSendTime = 0;
    private LinearLayout teslayfortag;

    private InternetAvailabilityChecker mInternetAvailabilityChecker;
    private ImageView setting;
    private Storage storage;
    private boolean iFirstTime;
    private RelativeLayout relPulltoInst;
    //private String authHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6Ijc5NzhjMjI3LWViMGItNGMwOS1iYWEyLTEwYmE0MjI4YWE4OSIsImNlcnRzZXJpYWxudW1iZXIiOiJtYWNfYWRkcmVzc19vZl9waG9uZSIsInNlY3VyaXR5U3RhbXAiOiJlMTAxOWNiYy1jMjM2LTQ0ZTEtYjdjYy0zNjMxYTYxYzMxYmIiLCJuYmYiOjE1MDYyODQ4NzMsImV4cCI6NDY2MTk1ODQ3MywiaWF0IjoxNTA2Mjg0ODczLCJpc3MiOiJCbGVuZCIsImF1ZCI6IkJsZW5kIn0.QUh241IB7g3axLcfmKR2899Kt1xrTInwT6BBszf6aP4";
    private String authHeader = null;
    //    private HubConnection connection = new WebSocketHubConnectionP2(ApiConstant.SERVER + ApiConstant.WEBSOCKET_TRACKING, authHeader);
    com.microsoft.signalr.HubConnection hubConnection = HubConnectionBuilder.create(ApiConstant.SERVER + ApiConstant.WEBSOCKET_TRACKING).build();
    private TextView tag1, tag2, tag3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception ex) {
        }
        try {
            ActionBar supportActionBar;
            supportActionBar = getSupportActionBar();
            if (supportActionBar != null)
                supportActionBar.hide();
        } catch (Exception ex) {
        }

        storage = new Storage(this);

        findById();
        try {
            ((TextView) findViewById(R.id.title)).setText(Constants.getTablet(this).getDisplayName());
        } catch (Exception ex) {
        }
        if (!Utils.permission_check_only(this, permissions)) {
            ActivityCompat.requestPermissions(MainActivity.this, permissions, PERMISSION_All_REQUEST_CODE);
        }
        try {
            Engine.getInstance().init(this.getApplicationContext());
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this,
                    "Failed to open bluetooth." + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        initAdapter();
        initClick();
        iFirstTime = true;
        loadProcedureList(true);
        connectWebSocket();
        //checkBluetoothAdapter();
        initRFIDReader();

        InternetAvailabilityChecker.init(this);
        mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        mInternetAvailabilityChecker.addInternetConnectivityListener(this);

        if (IS_TESTING) {
            teslayfortag.setVisibility(View.VISIBLE);
            testTag();
        } else {
            teslayfortag.setVisibility(View.GONE);
        }
        HubChecker();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.isEmpty()) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
            if (getSupportFragmentManager().getFragments().isEmpty()) {
                fragment_Container.setVisibility(View.GONE);
                setTitle("Live Procedure Tracking");
//                this.invalidateOptionsMenu();
            }
        }

    }

    private void findById() {
        lstProcedure = findViewById(R.id.lstProcedure);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        fragment_Container = findViewById(R.id.fragment_container);
        setting = findViewById(R.id.settings);
        relPulltoInst = findViewById(R.id.relPulltoInst);
        teslayfortag = findViewById(R.id.teslayfortag);
    }

    private void initAdapter() {
        mAdapter = new ProcedureAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        lstProcedure.setLayoutManager(mLayoutManager);
        lstProcedure.setItemAnimator(new DefaultItemAnimator());
        lstProcedure.setAdapter(mAdapter);
    }

    private void initClick() {
        findViewById(R.id.btnStart).setOnClickListener(this);
        pullToRefresh.setOnRefreshListener(() -> {
            pullToRefresh.setRefreshing(true);
            loadProcedureList(true);
        });

        setting.setOnClickListener(v -> {
            fragment_Container.setVisibility(View.VISIBLE);
            InstrumentFragment newFragment = new InstrumentFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

    }

    private void getScanResults(@NonNull final List<ScanResult> results) {
        for (ScanResult result : results) {
            if (result.SSID.contains(storage.getWifiName())) {
                connectToWifi(result.SSID);
                break;
            }
        }

    }

    private void connectToWifi(String ss) {
        WifiUtils.withContext(getApplicationContext())
                .connectWith(ss.trim(), storage.getPassword())
                .onConnectionResult(this::checkResult)
                .start();
    }

    private void checkResult(boolean isSuccess) {
        if (isSuccess)
            Toast.makeText(MainActivity.this, "CONNECTED " + storage.getWifiName(), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.this, "COULDN'T CONNECT!",
                    Toast.LENGTH_SHORT).show();
    }


    private void showWifiSetupDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_wifi_info_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //views of the dialog
        EditText wifiName = dialog.findViewById(R.id.edit_text_wifi_name);
        EditText password = dialog.findViewById(R.id.etPassword);
        TextView cancel = dialog.findViewById(R.id.txt_cancel);
        TextView save = dialog.findViewById(R.id.txt_save);
        cancel.setOnClickListener(v -> dialog.dismiss());
        save.setOnClickListener(v -> {
            if (wifiName.getText().toString().length() != 0) {
                if (password.getText().toString().length() >= 6) {
                    storage.saveWifiName(wifiName.getText().toString());
                    storage.savePassword(password.getText().toString());
                    dialog.dismiss();
                    WifiUtils.withContext(getApplicationContext()).scanWifi(
                            MainActivity.this::getScanResults).start();
                } else {
                    Toast.makeText(this, "Password can't be less than 6 character", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Name can't be empty!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                //onClickStart();
                startActivity(new Intent(MainActivity.this, SimulateRFIDScan.class));
                break;
        }
    }

    // Proceduree


    private void loadProcedureList(final boolean needProgress) {
        try {
            if (!checkInternetConnection(this)) {
                if (pullToRefresh.isRefreshing()) {
                    pullToRefresh.setRefreshing(false);
                }
                return;
            }
            if (needProgress) {
                showProgress();
            }
            WebServices.getInstance().GetActiveProceduresList(this,
                    new AjaxCallback<String>() {
                        public void callback(String url, String json, AjaxStatus status) {
                            if (needProgress) {
                                hideProgress();
                            }
                            if (pullToRefresh.isRefreshing()) {
                                pullToRefresh.setRefreshing(false);
                            }
                            if (json == null) {
                                Toast.makeText(MainActivity.this,
                                        "Failed to load procedures, " + status.getMessage(), Toast.LENGTH_SHORT).show();
                                NOProcedureUI();
                            } else {
                                parseDeviceList(json);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,
                    "Failed to call get procedures : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void NOProcedureUI() {
        if (mArrayProcedureList.size() <= 0) {
            relPulltoInst.setVisibility(View.VISIBLE);
        } else {
            relPulltoInst.setVisibility(View.GONE);
        }
    }

    private void parseDeviceList(String json) {
        try {
            JSONObject retObj = new JSONObject(json);
            Log.d("DeviceList", "parseDeviceList: " + json);
            JSONArray jsonArray = retObj.getJSONArray("procedures");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ProcedureObj>>() {
            }.getType();
            mArrayProcedureList = gson.fromJson(jsonArray.toString(), listType);
            Log.d("WebSocket---", "mArrayProcedureList assign new list " + mArrayProcedureList.size());
            if (mArrayProcedureList == null) {
                mArrayProcedureList = new ArrayList<>();
            }
            //code change by Mujahid at 2-16-2020 for removing finished procedures
            for (Iterator<ProcedureObj> iterator = mArrayProcedureList.iterator();
                 iterator.hasNext(); ) {
                ProcedureObj value = iterator.next();
                if (value.getFinished() != null) {
                    iterator.remove();
                }
            }
            mAdapter.refresh();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,
                    "Failed to parse procedures response :"
                            + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        NOProcedureUI();
    }


    private void instrumentProcessUpdate(ProcedureObj updatedProcedureObj, Double stateChange) {

        try {
            boolean isUpdatedProcedureExist = false;
            ChangeType changeType;
            try {
                changeType = ProcedureObj.InitChangeType(stateChange.intValue());
            } catch (Exception ex) {
                changeType = ChangeType.StepChanged;
                Log.d("WebSocket---", "ChangeType not recognise " + stateChange);
            }

            for (int i = 0; i < mArrayProcedureList.size(); i++) {
                if (updatedProcedureObj.getId().equalsIgnoreCase(mArrayProcedureList.get(i).getId())) {
                    isUpdatedProcedureExist = true;
                    if (ChangeType.Finished == changeType) {
                        mArrayProcedureList.remove(i);
                        mAdapter.procedureRemove(i);
                        NOProcedureUI();
                        Toast.makeText(MainActivity.this, "Finish procedure.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (ChangeType.StepChanged == changeType) {
                            if (updatedProcedureObj.getSteps().size() <= 0) {
                                updatedProcedureObj.setSteps(mArrayProcedureList.get(i).getSteps());
                            }
                            mArrayProcedureList.set(i, updatedProcedureObj);
                        }
                        mAdapter.procedureMoveToTop(i, updatedProcedureObj);
                    }
                    //code change by Mujahid at 2-16-2020 for removing finished procedures
                    if (mArrayProcedureList.get(i).getFinished() != null) {
                        mArrayProcedureList.remove(i);
                        mAdapter.procedureRemove(i);
                    }
                    break;
                }
            }
            if (!isUpdatedProcedureExist && ChangeType.Started == changeType) {
                Toast.makeText(MainActivity.this, "Added procedure.", Toast.LENGTH_SHORT).show();
                mAdapter.procedureAddonTop(updatedProcedureObj);
                NOProcedureUI();
            } else {
                Log.d("WebSocket---", isUpdatedProcedureExist + ", ChangeType!=Started");
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Reload procedure list", Toast.LENGTH_SHORT).show();
            loadProcedureList(true);
        }
    }


//    @Override
//    public void onEventMessage(HubMessage message) {
//        final String hm = message.toString();
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(MainActivity.this, "on event" + hm, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


//    @Override
//    public void onDisconnected() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
//                closeConnection();
//                connectWebSocket();
//            }
//        });
//    }

//    @Override
//    public void onMessage(HubMessage message) {
//        final HubMessage hubMessage = message;
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                String target = hubMessage.getTarget();
//                if (target.contains("Procedure")) {
//                    instrumentProcessUpdate(hubMessage.getArguments());
//                } else if (target.equals("ScopePicked")) {
//                } else if (target.equalsIgnoreCase("test")) {
//                    Toast.makeText(MainActivity.this, "online", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//    }

//    @Override
//    public void onError(final Exception exception) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                //Toast.makeText(MainActivity.this, "onError "+ exception.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


    ////// websocket code


    private void showRFIDDialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_custom_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView text = (TextView) dialog.findViewById(R.id.txt_rfid_tag);
        text.setText(message);
        TextView dialogButton = dialog.findViewById(R.id.btn_ok);
        dialogButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    private void connectWebSocket() {
        try {
            hubConnection.start().blockingAwait();
            Log.d("sr-data", hubConnection.getConnectionState().toString());
            hubConnection.on("ProcedureChanged", new Action3<ProcedureObj, Double, Object>() {
                @Override
                public void invoke(ProcedureObj procedureObj, Double stateChange, Object param3) {
                    //toastonmain("got data");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            instrumentProcessUpdate(procedureObj, stateChange);
                        }
                    });
                    Log.d("sr-data", stateChange + "---" + procedureObj.getInstrumentName());
                }
            }, ProcedureObj.class, Double.class, Object.class);

            hubConnection.onClosed(exception -> {
                try {
                    hubConnection.stop();
                    runOnUiThread(() -> {
                        Log.d("sr-data", hubConnection.getConnectionState().toString());
                        connectWebSocket();
                    });
                } catch (Exception ex) {
                    Log.d("sr-error", ex.getMessage());
                }
                ;
            });

        } catch (Exception e) {
            Log.d("sr-error2", e.getMessage());
        }
    }


    private void HubChecker() {
        new Handler().postDelayed(() -> {
            if (hubConnection != null) {
                if (hubConnection.getConnectionState() != HubConnectionState.CONNECTED) {
                    hubConnection.stop();
                    try {
                        connectWebSocket();
                    } catch (Exception ex) {
                        Log.e("sr-reconnect", ex.getMessage());
                    }
                    loadProcedureList(true);
                }
                HubChecker();
            }

        }, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInternetAvailabilityChecker
                .removeInternetConnectivityChangeListener(this);
        closeConnection();
    }

    protected void closeConnection() {
        try {
            if (hubConnection != null) {
                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                    hubConnection.stop();
                }
            }
        } catch (Exception ex) {
        }

    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (isConnected) {
            if (!iFirstTime) {
                runOnUiThread(() -> {
                    if (hubConnection != null) {
                        if (hubConnection.getConnectionState() != HubConnectionState.CONNECTED) {
                            hubConnection.stop();
                        }
                    }
                    if (hubConnection != null) {
                        try {
                            closeConnection();
                            connectWebSocket();
                        } catch (Exception ex) {
                            Log.e("WebSocket", ex.getMessage());
                        }
                    }
                    loadProcedureList(true);
                });
            }
            Toast.makeText(this, "Internet connected", Toast.LENGTH_SHORT).show();
        } else {
            //show a dialog and ask for wifi data and try to re-connect
            if (storage.getPassword() != null && storage.getWifiName() != null) {
                WifiUtils.withContext(getApplicationContext()).scanWifi(
                        MainActivity.this::getScanResults).start();
            } else {
                showWifiSetupDialog();
            }
            // WifiUtils.enableLog(true);
        }
        if (iFirstTime) {
            iFirstTime = false;
        }

    }


    /////// RFID

    private void scanRfid(String scanTag) {
        if (isInvalidTag(scanTag)) {
            ShowMessage(scanTag);
            return;
        }
        boolean isScanTagForNewIntrument = false;
        showRFIDDialog(scanTag);
        if (fragment_Container.getVisibility() == View.VISIBLE) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (!fragments.isEmpty()) {
                for (int i = 0; i < fragments.size(); i++) {
                    if (fragments.get(i) instanceof InstrumentFragment) {
                        InstrumentFragment instrumentFragment = (InstrumentFragment) fragments.get(i);
                        isScanTagForNewIntrument = instrumentFragment.setScanIdForNewIntrument(scanTag);
                        break;
                    }
                }
            }
        }
        if (!isScanTagForNewIntrument) {
            processIdentity(scanTag);
        }
    }

    private void initRFIDReader() {
        try {
            mPort = new RFReader() {
                @Override
                protected void onErrorMessage(String mMsg) {
                    ShowMessage(mMsg);
                }

                @Override
                protected void recievedTagId(final String tagId) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DispRecData(tagId);
                        }
                    });
                }
            };
        } catch (Exception ex) {
            Toast.makeText(this, "Failed to open serial port.", Toast.LENGTH_SHORT).show();
        }
    }

    private void ShowMessage(final String sMsg) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this,
                sMsg,
                Toast.LENGTH_SHORT).show());
    }

    private void DispRecData(String ComRecData) {
        scanRfid(ComRecData.trim());
        iRecLines++;
        if ((iRecLines > 500)) //500 automatic clears
        {
            iRecLines = 0;
        }
    }

    private boolean isInvalidTag(String rfidData) {
        return rfidData.length() > 20 &&
                !rfidData.contains("error") &&
                !rfidData.contains("failed") &&
                !rfidData.contains("serial") &&
                !rfidData.contains("port");
    }

    private void processIdentity(String scanedrfid) {
        if (!checkInternetConnection(this)) {
            return;
        }
        showProgress();
        WebServices.getInstance().InstrumentProcessIdentity(MainActivity.this, scanedrfid, new AjaxCallback<String>() {
            public void callback(String url, String json, AjaxStatus status) {
                hideProgress();
                try {
                    if (json == null) {
                        Toast.makeText(MainActivity.this, "Identity failed"
                                + (status != null ? (" : " + status.getMessage() + ".") : "."), Toast.LENGTH_LONG).show();
                    } else {
                        Log.i("DeviceRegisterActivity", json);
                        JSONObject jsonObject = new JSONObject(json);
                        boolean bSuccess = jsonObject.getBoolean(Constants.Key_Success);
                        String message = "";
                        if (jsonObject.has(Constants.Key_Message)) {
                            message = jsonObject.getString(Constants.Key_Message).trim();
                            if (message.equalsIgnoreCase("null"))
                                message = "";
                        }
                        if (bSuccess) {
                            Toast.makeText(MainActivity.this, "Success" + (message.length() > 0 ? " : " + message : ""), Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(MainActivity.this, "Failed" + (message.length() > 0 ? " : " + message : ""), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Process Identity failed" +
                            ", Please check internet and try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    //// TEST

    private void testTag() {
        tag1 = findViewById(R.id.tag1);
        tag2 = findViewById(R.id.tag2);
        tag3 = findViewById(R.id.tag3);
        tag1.setOnClickListener(v -> processIdentity("F9 C5 62 8E 60 80"));
        tag2.setOnClickListener(v -> processIdentity("F1 C5 62 8E 60 80"));
        tag3.setOnClickListener(v -> processIdentity("ED C5 62 8E 60 80"));
    }


    ///// bluetooth code
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Global.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!Global.mBluetoothLeService.initialize()) {
                Toast.makeText(MainActivity.this, "Test : Bluetooth Init Failed.", Toast.LENGTH_SHORT).show();
                finish();
            }
            startScanning();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Global.mBluetoothLeService = null;
            Toast.makeText(MainActivity.this,
                    "Test : Bluetooth Disconnected...",
                    Toast.LENGTH_SHORT).show();
        }
    };


    // Implements receive methods that handle a specific intent actions from
    // mBluetoothLeService
    private final BroadcastReceiver mBluetoothLeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_DEVICE_DISCOVERED.equals(action)) {
                BluetoothDevice bluetoothDevice = intent
                        .getParcelableExtra(BluetoothLeService.DISCOVERED_DEVICE);
                int rssi = intent.getIntExtra(BluetoothLeService.RSSI, 0);
                byte[] scanRecord = intent.getByteArrayExtra(BluetoothLeService.SCAN_RECORD);

                String serialNumber = Utils.bytesToHex(scanRecord).substring(56, 68);
                String value = Utils.bytesToHex(scanRecord).substring(78, 22);

//                serialNumber = Utils.hexToAscii(serialNumber);
//                if(checkDeviceExist(serialNumber)){
//                    Engine.getInstance().prepareToAddDevice();
//                    Engine.getInstance().addBluetoothDevice(bluetoothDevice, rssi, scanRecord);
                refreshViewOnUiThread("Device : srNO : " + serialNumber + "\n val : " + value);
                sendBluetoothEvent(serialNumber, value);
//                }

            } else if (intent.getAction().equals(BluetoothLeService.ACTION_STOP_SCAN)) {
                refreshViewOnUiThread("Stop Scan");
            } else if (intent.getAction().equals(BluetoothLeService.ACTION_GATT_CONNECTED)) {
                refreshViewOnUiThread("GATT_CONNECTED");
            } else if (intent.getAction().equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)
                    || intent.getAction().equals(BluetoothLeService.ACTION_READ_REMOTE_RSSI)
                    || intent.getAction().equals(BluetoothLeService.ACTION_GATT_CONNECTION_STATE_ERROR)) {
                refreshViewOnUiThread("GATT_DISCONNECTED");
            } else if (action.equals(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)) {
                refreshViewOnUiThread("SERVICES_DISCOVERED");
            }
        }
    };

    private void refreshViewOnUiThread(final String from) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Toast.makeText(MainActivity.this, "Test " + from, Toast.LENGTH_SHORT).show();
                // mAdapter.notifyDataSetChanged();
            }
        });
    }


    private static IntentFilter getGattUpdateIntentFilter() {
        if (bleIntentFilter == null) {
            bleIntentFilter = new IntentFilter();
            bleIntentFilter.addAction(BluetoothLeService.ACTION_DEVICE_DISCOVERED);
            //bleIntentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
            bleIntentFilter.addAction(BluetoothLeService.ACTION_STOP_SCAN);
            bleIntentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
            bleIntentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
            bleIntentFilter.addAction(BluetoothLeService.ACTION_READ_REMOTE_RSSI);
            bleIntentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTION_STATE_ERROR);
        }
        return bleIntentFilter;
    }

    private void checkBluetoothAdapter() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            bluetoothNotSupported();
        } else if (!bluetoothAdapter.isEnabled()) {
            bluetoothEnable();
        } else {
            connectService();
        }
    }

    // Displays dialog with information that phone doesn't support Bluetooth
    private void bluetoothNotSupported() {
        mDialog = DialogUtils.showAlert(getText(R.string.app_name),
                getText(R.string.bluetooth_not_supported), this,
                getText(android.R.string.ok), null,
                (dialog, which) -> finish(), null);
    }

    // Displays dialog and request user to enable Bluetooth
    private void bluetoothEnable() {
        mDialog = DialogUtils.showAlert(this.getText(R.string.no_bluetooth_dialog_title_text), this
                        .getText(R.string.no_bluetooth_dialog_text),
                this, getText(android.R.string.ok),
                getText(android.R.string.cancel), (dialog, id) -> {
                    Intent intentBluetooth = new Intent();
                    intentBluetooth.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                    MainActivity.this.startActivityForResult(intentBluetooth,
                            BlUETOOTH_SETTINGS_REQUEST_CODE);
                }, (dialog, id) -> MainActivity.this.finish());
    }

    private void connectService() {
        Toast.makeText(MainActivity.this, "Test : bluetooth searching...", Toast.LENGTH_SHORT).show();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void sendBluetoothEvent(String macAddress, String value) {
        long lCurTime = System.currentTimeMillis();
        if (lCurTime > (lLastEventSendTime + 30000)) {
            WebServices.getInstance().sendBlutoothEvent(this, macAddress, value,
                    new AjaxCallback<String>() {
                        public void callback(String url, String json, AjaxStatus status) {

                        }
                    });
        }
    }

    private void startScanning() {
        Iterator<Device> device = Engine.getInstance().getDevices().iterator();
        while (device.hasNext()) {
            if (device.next().getBluetoothDevice() != null) {
                Global.mBluetoothLeService.readRemoteRssi(device.next());
            }
        }
//        mAdapter.notifyDataSetChanged();//sdf
        // Starts a scan for Bluetooth LE devices for SCAN_PERIOD milliseconds
        Global.mBluetoothLeService.startScanning(SCAN_PERIOD);
        registerReceiver(mBluetoothLeReceiver, getGattUpdateIntentFilter());
    }

}