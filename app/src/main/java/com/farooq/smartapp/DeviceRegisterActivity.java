package com.farooq.smartapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.farooq.smartapp.fragment.GeneralFragment;
import com.farooq.smartapp.model.Tablet;
import com.farooq.smartapp.server.ApiConstant;
import com.farooq.smartapp.server.WebServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import static com.farooq.smartapp.Constants.IS_TESTING;
import static com.farooq.smartapp.Constants.checkInternetConnection;
import static com.farooq.smartapp.datamodel.Common.GetTabletMac;

public class DeviceRegisterActivity extends BaseActivity {

    static final String SERVER_ADDRESS = "server_address";
    static final String APP_TRACKING_DATA = "TrackingAppData";
    private EditText txtTabletName, editextServerAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_register);
        txtTabletName = findViewById(R.id.tablet_name_content);
        editextServerAddress = findViewById(R.id.tablet_server_address);
        ApiConstant.SERVER = GeneralFragment.GetServerAddress(DeviceRegisterActivity.this);
        if (!IS_TESTING) {
        if (Constants.getTabletRegisterId(this) != null) {
            goToHomePage();
        }
        else
        {
           // GetTabletInfo();
        }
        }

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAddress();
                if (IS_TESTING)
                {
                    TestRegisterDeviceProcess();
                } else {
                    registerDeviceProcess();
                }
            }
        });

        txtTabletName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (IS_TESTING)
                    {
                        TestRegisterDeviceProcess();
                    }
                    else
                    {
                        registerDeviceProcess();
                    }
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void goToHomePage() {
        this.startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void saveAddress() {
        try {
            SharedPreferences sPref = getSharedPreferences(APP_TRACKING_DATA, MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            String addressString = editextServerAddress.getText().toString().trim();
            ed.putString(SERVER_ADDRESS, addressString);
            ApiConstant.SERVER = addressString;
            ed.apply();
          //  Toast.makeText(this, "Update Address Successfully.", Toast.LENGTH_SHORT).show();
        }catch (Exception ex)
        {
            Toast.makeText(this, "Update Address Failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerDeviceProcess() {

        final String tabletName = txtTabletName.getText().toString();
        if (tabletName.trim().length() <= 0) {
            txtTabletName.setError("Please enter device name first.");
            return;
        }
        final String tabletId = GetTabletMac();
        if (tabletId.length() <= 0) {
            Toast.makeText(DeviceRegisterActivity.this, "Device MAC address not found\n" +
                    " Please check internet wifi and try again", Toast.LENGTH_LONG).show();
            return;
        }
        if (!checkInternetConnection(this))
        {
            return;
        }
        showProgress();
        WebServices.getInstance().registerProcess(this, tabletId, tabletName, new AjaxCallback<String>() {
            public void callback(String url, String json, AjaxStatus status) {
                hideProgress();
                try {
                    if (json == null) {
                        Toast.makeText(DeviceRegisterActivity.this, "Device register failed" + (status != null ? (" : " + status.getMessage() + ".") : "."), Toast.LENGTH_LONG).show();
                    } else {
                        Log.i("DeviceRegisterActivity", json);
                        JSONObject jsonObject = new JSONObject(json);
                        boolean bSuccess = jsonObject.getBoolean(Constants.Key_Success);
                        if (bSuccess) {
                            Toast.makeText(DeviceRegisterActivity.this,
                                    "Device register successful", Toast.LENGTH_LONG).show();
                            Constants.SetTabletInfo(DeviceRegisterActivity.this,
                                    jsonObject.getString(Constants.Key_Tablet));
                            goToHomePage();
                        } else {
                            if (jsonObject.has(Constants.Key_Message)) {
                                Toast.makeText(DeviceRegisterActivity.this,
                                        "" + jsonObject.getString(Constants.Key_Message), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(DeviceRegisterActivity.this,
                                        "Device register failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(DeviceRegisterActivity.this, "Device register failed" +
                            ", Please check internet and try again", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void GetTabletInfo() {
        try {
            if (!checkInternetConnection(this))
            {
                return;
            }
            showProgress();
            WebServices.getInstance().tabletsList(DeviceRegisterActivity.this, new AjaxCallback<String>() {
                public void callback(String url, String json, AjaxStatus status) {
                    hideProgress();
                    try {
                        if (json == null) {
                            //  Toast.makeText(getActivity(), "Fetching Device denial failed." + (status != null ? (" : " + status.getMessage() + ".") : "."), Toast.LENGTH_LONG).show();
                        } else {
                            Log.i("fetchingDevices", json);
                            parseTabletData(json);
                        }
                    } catch (Exception e) {

                    }

                }
            });
        } catch (Exception ignored) {

        }
    }

    private void parseTabletData(String jsonTablet) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonTablet);
        boolean bSuccess = jsonObject.getBoolean(Constants.Key_Success);
        if (bSuccess) {
            JSONArray jsonArray = jsonObject.getJSONArray("tablets");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Tablet>>() {
            }.getType();
            List<Tablet> mArrayTabletList = gson.fromJson(jsonArray.toString(), listType);
            final String tabletId = GetTabletMac();
            for (int i = 0; i < mArrayTabletList.size(); i++) {
                if (mArrayTabletList.get(i).getMacAddress().equalsIgnoreCase(tabletId)) {
                    String tabInfo = gson.toJson(mArrayTabletList.get(i));
                    Constants.SetTabletInfo(DeviceRegisterActivity.this, tabInfo);
                    goToHomePage();
                    break;
                }
            }
        } else {
            if (jsonObject.has(Constants.Key_Message)) {
                Toast.makeText(DeviceRegisterActivity.this, jsonObject.getString(Constants.Key_Message), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(DeviceRegisterActivity.this, "fetching Devices failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void TestRegisterDeviceProcess() {

        final String tabletName = txtTabletName.getText().toString();
        if (tabletName.trim().length() <= 0) {
            txtTabletName.setError("Please enter device name first.");
            return;
        }
        final String tabletId = GetTabletMac();
        if (tabletId.length() <= 0) {
            Toast.makeText(DeviceRegisterActivity.this, "Device MAC address not found\n" +
                    " Please check internet wifi and try again", Toast.LENGTH_LONG).show();
            return;
        }

        String cabinat = "{\"id\":\"b178f113-20aa-4df5-93d2-28981a4e006f\",\"macAddress\":\"c:8c:24:c1:7d:2d\",\"ipAddress\":\"108.54.249.210\"," +
                "\"displayName\":\"Cabinet\"," +
                "\"registered\":\"2019-11-12T13:30:10.766881\",\"lastSeen\":\"2019-11-12T13:30:10.766881\",\"deleted\":null}";

        String preProc = "{\"id\":\"24edf0f4-d488-4bec-a976-133f7c348313\",\"macAddress\":\"c:8c:24:dc:a6:bd\"," +
                "\"ipAddress\":\"75.99.43.187\",\"displayName\":\"Pre Procedure\"," +
                "\"registered\":\"2019-11-12T13:15:00.968378\",\"lastSeen\":\"2019-11-12T13:15:00.968377\",\"deleted\":null}";

        String postProc = "{\"id\":\"41d99a65-6b7d-4c8a-81e2-2678ba0c8e8d\",\"macAddress\":\"c:8c:24:dc:8d:e2\",\"ipAddress\":\"108.54.249.210\"," +
                "\"displayName\":\"Post Procedure\"," +
                "\"registered\":\"2019-11-12T13:04:19.856785\",\"lastSeen\":\"2019-11-12T13:04:19.856785\",\"deleted\":null}";

        String cleaningroc = "{\"tablets\":[{\"id\":\"05121851-0b4c-4982-b999-947b955f1069\",\"macAddress\":\"c:8c:24:c1:75:43\"," +
                "\"ipAddress\":\"108.54.249.210\",\"displayName\":\"Cleaning\"," +
                "\"registered\":\"2019-11-12T12:48:12.817662\",\"lastSeen\":\"2019-11-12T12:48:12.81766\",\"deleted\":null}";

        String visualInProc = "{\"id\":\"9bd01b45-6884-4525-ad7c-d5e6b078befb\",\"macAddress\":\"c:8c:24:c1:7d:37\",\"ipAddress\":\"108.54.249.210\"," +
                "\"displayName\":\"Visual Inspection\"," +
                "\"registered\":\"2019-11-12T13:41:30.560993\",\"lastSeen\":\"2019-11-12T13:41:30.560992\",\"deleted\":null}";

        String ATPProc = "{\"id\":\"bab21ff8-9d12-48ee-8fc8-760e44b2547c\",\"macAddress\":\"c:8c:24:c1:75:45\",\"ipAddress\":\"108.54.249.210\"" +
                ",\"displayName\":\"ATP\"" +
                ",\"registered\":\"2019-11-12T12:26:48.85273\",\"lastSeen\":\"2019-11-12T12:26:48.852566\",\"deleted\":null}";

        String jsonTablet = cabinat;

        Constants.SetTabletInfo(DeviceRegisterActivity.this, jsonTablet);
        goToHomePage();
        Log.d("websocket", "TestRegisterDeviceProcess: end");
    }
}
