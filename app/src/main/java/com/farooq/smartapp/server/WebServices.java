package com.farooq.smartapp.server;

import android.content.Context;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;

import org.json.JSONObject;

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
            if(progressBar != null){
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

}
