package com.farooq.smartapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.farooq.smartapp.Constants;
import com.farooq.smartapp.R;
import com.farooq.smartapp.model.Tablet;
import com.farooq.smartapp.server.ApiConstant;
import com.farooq.smartapp.server.WebServices;
import com.farooq.smartapp.utils.DialogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.farooq.smartapp.Constants.IS_TESTING;
import static com.farooq.smartapp.Constants.checkInternetConnection;
import static com.farooq.smartapp.Constants.setFragment;

public class GeneralFragment extends Fragment implements View.OnClickListener {

    static final String SERVER_ADDRESS = "server_address";
    static final String APP_TRACKING_DATA = "TrackingAppData";
    private EditText address;
    private ImageView send, back;
    TextView instrument, general, device_name, device_mac;
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    private Dialog pdProgress;

    public GeneralFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general, container, false);

        address = view.findViewById(R.id.edt_address);
        send = view.findViewById(R.id.send);
        instrument = view.findViewById(R.id.tv_instrument);
        general = view.findViewById(R.id.tv_general);

        device_name = view.findViewById(R.id.tab_name);
        device_mac = view.findViewById(R.id.tab_mac);
        back = view.findViewById(R.id.btn_back);


        send.setOnClickListener(this);
        address.setText(GetServerAddress(getActivity()));

        instrument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment((AppCompatActivity) GeneralFragment.this.getActivity(), new InstrumentFragment());
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        setTabletInfo(Constants.getTablet(getActivity()));

        if (!IS_TESTING) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    GetTabletInfo();
                }
            }, 1000);
        }
        return view;
    }

    private void setTabletInfo(Tablet tablet)
    {
        device_name.setText("Name: " + tablet.getDisplayName());
        device_mac.setText("Mac: " + tablet.getMacAddress());
    }

    private void GetTabletInfo() {
        try {
            showProgress();
            WebServices.getInstance().tabletsList(getActivity(), new AjaxCallback<String>() {
                public void callback(String url, String json, AjaxStatus status) {
                    hideProgress();
                    try {
                        if (json == null) {
                            Toast.makeText(getActivity(), "Fetching Device denial failed." + (status != null ? (" : " + status.getMessage() + ".") : "."), Toast.LENGTH_LONG).show();
                        } else {
                            Log.i("fetchingDevices", json);
                            parseTabletData(json);
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Fetching Devices failed" +
                                ", Please check internet and try again", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }catch (Exception ex){
            Toast.makeText(getActivity(), "Fetching Devices request failed." +
                    ", Please check internet and try again", Toast.LENGTH_LONG).show();
        }
    }

    private void parseTabletData(String jsonTablet) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonTablet);
        boolean bSuccess = jsonObject.getBoolean(Constants.Key_Success);
        if (bSuccess) {
            //Toast.makeText(getActivity(), "fetching Devices successful.", Toast.LENGTH_LONG).show();
            JSONArray jsonArray = jsonObject.getJSONArray("tablets");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Tablet>>() {
            }.getType();
            List<Tablet> mArrayTabletList = gson.fromJson(jsonArray.toString(), listType);
            String tabId = Constants.getTabletRegisterId(GeneralFragment.this.getActivity());
            for (int i = 0; i < mArrayTabletList.size(); i++) {
                if (mArrayTabletList.get(i).getId().equalsIgnoreCase(tabId)) {
                    String tabInfo = gson.toJson(mArrayTabletList.get(i));
                    Constants.SetTabletInfo(GeneralFragment.this.getActivity(),tabInfo);
                    setTabletInfo(mArrayTabletList.get(i));
                    break;
                }
            }
        } else {
            if (jsonObject.has(Constants.Key_Message)) {
                Toast.makeText(getActivity(), jsonObject.getString(Constants.Key_Message), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "fetching Devices failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showProgress() {
        try {
        if (pdProgress == null)
            pdProgress = DialogUtils.getInstance().createProgress(getActivity());
        pdProgress.show();
        }catch (Exception e){}
    }

    private void hideProgress() {
        try {
            if (pdProgress != null && pdProgress.isShowing())
                pdProgress.dismiss();
        }catch (Exception e){}
    }

    private void saveAddress() {
        try {
            SharedPreferences sPref = getActivity().getSharedPreferences(APP_TRACKING_DATA, MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            String addressString = address.getText().toString().trim();
            if (addressString.length() <= 0) {
                addressString = ApiConstant.SERVER;
            }
            ed.putString(SERVER_ADDRESS, addressString);
            ed.apply();
            Toast.makeText(getActivity(), "Update Address Successfully.", Toast.LENGTH_SHORT).show();
        }catch (Exception ex)
        {
            Toast.makeText(getActivity(), "Update Address Failed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static String GetServerAddress(Context context) {
        SharedPreferences sPref = context.getSharedPreferences(APP_TRACKING_DATA, MODE_PRIVATE);
        return sPref.getString(SERVER_ADDRESS, ApiConstant.SERVER);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                    saveAddress();

                break;
        }
    }
}
