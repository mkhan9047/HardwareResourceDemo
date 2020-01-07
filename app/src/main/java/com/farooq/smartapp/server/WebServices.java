package com.farooq.smartapp.server;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.farooq.smartapp.Constants;
import com.farooq.smartapp.model.InstrumentObj;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class WebServices {

    private static WebServices instance;

    private WebServices() {
    }

    public static WebServices getInstance() {
        if (instance == null) {
            instance = new WebServices();
        }
        return instance;
    }

    //Api requests
    private static final String RQ_SCOPE_LIST = "/api/ProcessHistory/ActiveRecords";
    private static final String RQ_START_PROCESS = "/api/ProcessHistory/StartProcess";
    private static final String RQ_DEVICE_REGISTER = "/api/Tablets/Register";
    private static final String RQ_PROCESS_LIST = "/api/ProcedureDefinition/List";
    private static final String RQ_INSTRUMENT_REGISTER = "/api/Instrument/Register";
    private static final String RQ_INSTRUMENT_LIST = "/api/Instrument/List";
    private static final String RQ_ACTIVE_PROCEDURES = "/api/Procedure/GetActiveProcedures";
    private static final String RQ_PROCESS_IDENTITY = "/api/Instrument/ProcessIdentity";

    private static final String RQ_PUMPACTINITY_LIST = "/api/PumpActivity/List";
    private static final String RQ_PUMPACTINITY_INSERT = "/api/PumpActivity/Insert";
    private static final String RQ_TABLETS_LIST = "/api/Tablets/List";


    /**
     * Add required headers to the request
     *
     * @param callback
     */
    private void addHeaders(AjaxCallback<String> callback) {
        callback.header(ApiConstant.HEADER_NAME_CONTENT_TYPE, ApiConstant.HEADER_VALUE_CONTENT_TYPE);
    }

    /**
     * Make api call
     *
     * @param context
     * @param requestUrl
     * @param data
     * @param callback
     */
    private void makeApiCall(Context context, String requestUrl, JSONObject data, AjaxCallback<String> callback) {
        try {
            AQuery aq = new AQuery(context);
            if (data != null) {
                aq.post(requestUrl, data, String.class, callback);
            } else {
                aq.ajax(requestUrl, String.class, callback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Make api call for form
     *
     * @param context
     * @param requestUrl
     * @param data
     * @param callback
     */
    private void makeApiCall(Context context, String requestUrl, Map<String, Object> data, ProgressBar progressBar, AjaxCallback<String> callback) {
        try {
            AQuery aq = new AQuery(context);
            if (progressBar != null) {
                aq.progress(progressBar);
                callback.progress(progressBar);
            }
            if (data != null) {
                aq.ajax(requestUrl, data, String.class, callback);
            } else {
                aq.ajax(requestUrl, String.class, callback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /* Users */

    /**
     * Scope List
     */
    public void scopeList(Context context, AjaxCallback<String> callback) {
        try {
            String requestUrl = ApiConstant.SERVER + RQ_SCOPE_LIST;

            //add required headers
            addHeaders(callback);

            //add params
            JSONObject data = new JSONObject();

            //make api call
            makeApiCall(context, requestUrl, null, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetActiveProceduresList(Context context, AjaxCallback<String> callback) {
        try {
            String requestUrl = ApiConstant.SERVER + RQ_ACTIVE_PROCEDURES;

            //add required headers
            addHeaders(callback);

            //make api call
            makeApiCall(context, requestUrl, null, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startProcess(Context context, String scopeId, String accessoryId, AjaxCallback<String> callback) {
        try {
            String requestUrl = ApiConstant.SERVER + RQ_START_PROCESS;

            //add required headers
            addHeaders(callback);

            //add params
            JSONObject data = new JSONObject();
            data.put("scopeId", scopeId);
            data.put("accessoryId", accessoryId);

            //make api call
            makeApiCall(context, requestUrl, data, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendBlutoothEvent(Context context, String macAddress, String value, AjaxCallback<String> callback) {
        try {
            String requestUrl = ApiConstant.SERVER + RQ_START_PROCESS;

            //add required headers
            addHeaders(callback);

            //add params
            JSONObject data = new JSONObject();
            data.put("macAddress", macAddress);
            data.put("value", value);

            String strTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            //make api call
            makeApiCall(context, requestUrl, data, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Register Instrument
     */
    public boolean registerInstrument(Context context, InstrumentObj instrumentObj, AjaxCallback<String> callback) {
        //add params
        JSONObject data = instrumentObj.parseToJSON();
        if (data == null) {
            Toast.makeText(context, "Please try again with proper instrument data.", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            String requestUrl = ApiConstant.SERVER + RQ_INSTRUMENT_REGISTER;
            //add required headers
            addHeaders(callback);
            //make api call
            makeApiCall(context, requestUrl, data, callback);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean InstrumentProcessIdentity(Context context, String scanedrfid, AjaxCallback<String> callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tabletId", Constants.getTabletRegisterId(context));
            jsonObject.put("rfid", scanedrfid);
            jsonObject.put("barcode", "");
            jsonObject.put("userBadge", "");
            String requestUrl = ApiConstant.SERVER + RQ_PROCESS_IDENTITY;
            addHeaders(callback);
            makeApiCall(context, requestUrl, jsonObject, callback);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Instrument List
     */
    public void instrumentList(Context context, AjaxCallback<String> callback) {
        try {
            String requestUrl = ApiConstant.SERVER + RQ_INSTRUMENT_LIST;

            //add required headers
            addHeaders(callback);

            //make api call
            makeApiCall(context, requestUrl, null, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Register Tablet
     */
    public void registerProcess(Context context, String tabletMacAddress, String tabletName, AjaxCallback<String> callback) {
        try {
            String requestUrl = ApiConstant.SERVER + RQ_DEVICE_REGISTER;

            //add required headers
            addHeaders(callback);

            //add params
            JSONObject data = new JSONObject();
            data.put("macAddress", tabletMacAddress);
            data.put("displayName", tabletName);

            //make api call
            makeApiCall(context, requestUrl, data, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Process List
     */
    public void pumpList(Context context, AjaxCallback<String> callback) {
        try {
            String requestUrl = ApiConstant.SERVER + RQ_PUMPACTINITY_LIST;

            //add required headers
            addHeaders(callback);

            //make api call
            makeApiCall(context, requestUrl, null, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tablets List
     */
    public void tabletsList(Context context, AjaxCallback<String> callback) {
        try {
            String requestUrl = ApiConstant.SERVER + RQ_TABLETS_LIST;

            //add required headers
            addHeaders(callback);

            //make api call
            makeApiCall(context, requestUrl, null, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert Pump
     */
    public void insertPump(Context context, String tabletMacAddress, String tabletName, AjaxCallback<String> callback) {
        try {
            String requestUrl = ApiConstant.SERVER + RQ_PUMPACTINITY_INSERT;

            //add required headers
            addHeaders(callback);

            //add params
            JSONObject data = new JSONObject();
            data.put("macAddress", tabletMacAddress);
            data.put("displayName", tabletName);

            //make api call
            makeApiCall(context, requestUrl, data, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}