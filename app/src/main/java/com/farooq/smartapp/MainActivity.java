package com.farooq.smartapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.farooq.smartapp.datamodel.Device;
import com.farooq.smartapp.datamodel.Engine;
import com.farooq.smartapp.model.RecordObj;
import com.farooq.smartapp.model.ScopeObj;
import com.farooq.smartapp.server.WebServices;
import com.farooq.smartapp.utils.DialogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    private ScopeListAdapter mAdapter;
    private ArrayList<ScopeObj> mArrScope = new ArrayList<>();
    private ArrayList<RecordObj> mArrRecord = new ArrayList<>();
    private ListView lstScope;

    private int rowwidth = 0;
    private ArrayList mArrStatus = new ArrayList();

    private ArrayList<String> arrScopes = new ArrayList<>();
    private ArrayAdapter scopeAdapter;


    //Bluetooth related variable
    private Dialog mDialog;
    private boolean bleIsSupported = true;
    private static IntentFilter bleIntentFilter;
    public static final int SCAN_PERIOD = 10000;
    private static final int BlUETOOTH_SETTINGS_REQUEST_CODE = 100;
    private String[] permissions = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"};
    private final int PERMISSION_All_REQUEST_CODE = 67;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAdapter();
        initClick();
        loadScopeList();
    }


    private void initAdapter() {
        mAdapter = new ScopeListAdapter(this, R.layout.item_scope, mArrScope);
        lstScope = (ListView)findViewById(R.id.lstScope);
        lstScope.setAdapter(mAdapter);

        lstScope.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                RecordObj recordObj = mArrRecord.get(position);
                intent.putExtra("recordobj", recordObj);
                startActivity(intent);
                //Intent intent = new Intent(MainActivity.this, ScopeCardsActivity.class);
                //Intent intent = new Intent(MainActivity.this, ScopeListActivity.class);
                //startActivity(intent);
            }
        });

        mArrStatus.add("Pre Process");
        mArrStatus.add("Post Process");
        mArrStatus.add("Pump Station");
        mArrStatus.add("ATP Test");
        mArrStatus.add("Complete");
    }

    private void initClick() {
        findViewById(R.id.btnStart).setOnClickListener(this);

        arrScopes.add("Scope1");
        arrScopes.add("Scope2");
        arrScopes.add("Scope3");
    }
    private void loadScopeList() {
        showProgress();
        WebServices.getInstance().scopeList(this,
                new AjaxCallback<String>() {
                    public void callback(String url, String json, AjaxStatus status) {
                        hideProgress();
                        parseDeviceList(json);
                    }
                });
    }

    private void parseDeviceList(String json) {
        try {
            JSONObject retObj = new JSONObject(json);
            JSONArray jsonArray = retObj.getJSONArray("records");
            mArrScope.clear();
            mArrRecord.clear();

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject recordObj = jsonArray.getJSONObject(i);
                RecordObj objRecord = new RecordObj();
                objRecord.parseFromJson(recordObj);
                mArrRecord.add(objRecord);

                JSONObject scopeObj = recordObj.getJSONObject("scope");
                ScopeObj newObj = new ScopeObj();
                newObj.parseFromJson(scopeObj);
                newObj.setStatus(recordObj.getInt("status"));
                mArrScope.add(newObj);
            }
//            for(int i = 0; i < 4; i++){
//                ScopeObj scopeObj = new ScopeObj();
//                scopeObj.setStatus(i);
//                mArrScope.add(scopeObj);
//            }

            mAdapter.notifyDataSetChanged();

        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStart:
                onClickStart();
                break;
        }
    }

    private void onClickStart() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View startDlgView = factory.inflate(R.layout.dlg_start, null);
        final AlertDialog startDlg = new AlertDialog.Builder(this).create();
        startDlg.setView(startDlgView);
        Spinner spScope = startDlgView.findViewById(R.id.spScope);
        scopeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrScopes);
        spScope.setAdapter(scopeAdapter);

        startDlgView.findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                startDlg.dismiss();
            }
        });

//        deleteDialogView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deleteDialog.dismiss();
//            }
//        });

        startDlg.show();
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
        mDialog = DialogUtils.showAlert(getText(R.string.app_name), getText(R.string.bluetooth_not_supported), this,
                getText(android.R.string.ok), null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }, null);
    }

    // Displays dialog and request user to enable Bluetooth
    private void bluetoothEnable() {
        mDialog = DialogUtils.showAlert(this.getText(R.string.no_bluetooth_dialog_title_text), this
                        .getText(R.string.no_bluetooth_dialog_text), this, getText(android.R.string.ok),
                getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent intentBluetooth = new Intent();
                        intentBluetooth.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                        MainActivity.this.startActivityForResult(intentBluetooth, BlUETOOTH_SETTINGS_REQUEST_CODE);
                    }
                }, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        MainActivity.this.finish();
                    }
                });
    }

    private void connectService() {
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Global.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!Global.mBluetoothLeService.initialize()) {
                finish();
            }
            startScanning();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Global.mBluetoothLeService = null;
        }
    };

    // Implements receive methods that handle a specific intent actions from
    // mBluetoothLeService
    private final BroadcastReceiver mBluetoothLeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_DEVICE_DISCOVERED.equals(action)) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent
                        .getParcelableExtra(BluetoothLeService.DISCOVERED_DEVICE);
                int rssi = (int) intent.getIntExtra(BluetoothLeService.RSSI, 0);
                byte[] scanRecord = intent.getByteArrayExtra(BluetoothLeService.SCAN_RECORD);

//                String serialNumber =  Utils.bytesToHex(scanRecord).substring(56, 68).toString();
//                serialNumber = Utils.hexToAscii(serialNumber);
//                if(checkDeviceExist(serialNumber)){
                    Engine.getInstance().prepareToAddDevice();
                    Engine.getInstance().addBluetoothDevice(bluetoothDevice, rssi, scanRecord);
                    refreshViewOnUiThread();
//                }

            } else if (intent.getAction().equals(BluetoothLeService.ACTION_STOP_SCAN)) {

            } else if (intent.getAction().equals(BluetoothLeService.ACTION_GATT_CONNECTED)) {

            } else if (intent.getAction().equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)
                    || intent.getAction().equals(BluetoothLeService.ACTION_READ_REMOTE_RSSI)
                    || intent.getAction().equals(BluetoothLeService.ACTION_GATT_CONNECTION_STATE_ERROR)) {
                refreshViewOnUiThread();
            }
            else if (action.equals(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)) {
            }
        }
    };

    private void startScanning() {

        Iterator<Device> device = Engine.getInstance().getDevices().iterator();
        while (device.hasNext()) {
            if (device.next().getBluetoothDevice() != null) {
                Global.mBluetoothLeService.readRemoteRssi(device.next());
            }
        }
        mAdapter.notifyDataSetChanged();
        // Starts a scan for Bluetooth LE devices for SCAN_PERIOD miliseconds
        Global.mBluetoothLeService.startScanning(SCAN_PERIOD);
        registerReceiver(mBluetoothLeReceiver, getGattUpdateIntentFilter());
    }

    private void refreshViewOnUiThread() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
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


    public class ScopeListAdapter extends ArrayAdapter<ScopeObj> {
        private List<ScopeObj> devices;
        private LayoutInflater inflater;

        public ScopeListAdapter(Context context, int textViewResourceId, List<ScopeObj> items) {
            super(context, textViewResourceId, items);
            this.devices = items;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {


            final ScopeObj scopeObj = mArrScope.get(position);

            if (view == null) {
                view = inflater.inflate(R.layout.item_scope, null);

                final RelativeLayout relativeLayout = view.findViewById(R.id.rlStatus);
                final LinearLayout lnCircle = (LinearLayout)view.findViewById(R.id.lnCircle);
                ViewTreeObserver viewTreeObserver = relativeLayout.getViewTreeObserver();
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        relativeLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        rowwidth  = relativeLayout.getMeasuredWidth();
                        int height = relativeLayout.getMeasuredHeight();


                        TextView textView = new TextView(MainActivity.this);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(rowwidth/3, height/2);
                        params.topMargin = 50;

                        int index = scopeObj.getStatus();
                        if(index > 4)
                            index = 4;

                        if(index == 0){
                            params.leftMargin = 0;
                            textView.setGravity(Gravity.LEFT);
                        }
                        else if(index == 4){
                            params.leftMargin = rowwidth - rowwidth / 3;
                            textView.setGravity(Gravity.RIGHT);
                        }
                        else {
                            params.leftMargin = index * rowwidth / 4 - rowwidth / 6;
                            textView.setGravity(Gravity.CENTER);
                        }
                        relativeLayout.addView(textView, params);

                        index = scopeObj.getStatus();
                        if (index == 10)
                            index = 4;
                        textView.setText((String)mArrStatus.get(index));
                        textView.setTag("statusview");

                        ImageView imageView = new ImageView(MainActivity.this);
                        imageView.setBackgroundResource(R.drawable.circle_point);
                        int cwidth = 40;
                        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(cwidth, cwidth);

                        int centerh = (int)lnCircle.getY() + lnCircle.getHeight() / 2;
                        params1.topMargin = centerh - cwidth / 2;

                        params1.leftMargin = scopeObj.getStatus() * rowwidth / 4 - cwidth / 2;
                        if (scopeObj.getStatus() == 0)
                            params1.leftMargin = -2;
                        if (scopeObj.getStatus() == 10){
                            params1.leftMargin = 4 * rowwidth / 4 - cwidth;
                        }
                        imageView.setTag("imageview");
                        relativeLayout.addView(imageView, params1);
                    }
                });

                TextView txtName = (TextView)view.findViewById(R.id.txtScopeName);
                txtName.setText(scopeObj.getName());
            }
            else {
                RelativeLayout rlStatus = view.findViewById(R.id.rlStatus);
                TextView txtStatus = (TextView)rlStatus.findViewWithTag("statusview");
                int index = scopeObj.getStatus() - 1;
                if(index > 4)
                    index = 4;


                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)txtStatus.getLayoutParams();
                if(index == 0){
                    params.leftMargin = 0;
                    txtStatus.setGravity(Gravity.LEFT);
                }
                else if(index == 4){
                    params.leftMargin = rowwidth - rowwidth / 3;
                    txtStatus.setGravity(Gravity.RIGHT);
                }
                else {
                    params.leftMargin = index * rowwidth / 4 - rowwidth / 6;
                    txtStatus.setGravity(Gravity.CENTER);
                }
                txtStatus.setLayoutParams(params);


                index = scopeObj.getStatus();
                if (index == 10)
                    index = 4;
                txtStatus.setText((String)mArrStatus.get(index));

                ImageView imageView = (ImageView)rlStatus.findViewWithTag("imageview");
                int cwidth = 40;
                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)imageView.getLayoutParams();
                params1.topMargin = 0;

                params1.leftMargin = scopeObj.getStatus() * rowwidth / 4 - cwidth / 2;
//                if(scopeObj.getStatus() == 10){
//                    params1.leftMargin = 4 * rowwidth / 4 - cwidth / 2;
//                }
                if (scopeObj.getStatus() == 0)
                    params1.leftMargin = -2;
                if (scopeObj.getStatus() == 10){
                    params1.leftMargin = 4 * rowwidth / 4 - cwidth;
                }
                imageView.setLayoutParams(params1);

                TextView txtName = (TextView)view.findViewById(R.id.txtScopeName);
                txtName.setText(scopeObj.getName());
            }
            return view;
        }
    }
}
