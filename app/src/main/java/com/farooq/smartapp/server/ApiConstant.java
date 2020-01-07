package com.farooq.smartapp.server;


/**
 * Api Constants
 */
public class ApiConstant {

    //Server
    //public static final String SERVER = "http://52.22.152.156";
    //public static final String SERVER = "http://192.168.2.111:9095";
    //public static final String SERVER = "http://smartatp-dev.o7tc.com";
    public static String SERVER = "http://192.168.0.2";
    //public static final String SERVER = "http://medstir.appsolutionsdesign.net";
    //public static final String SERVER = "http://209.205.200.10:5000";

    public static final String URL_PATH = SERVER + "/api/";
    public static final String URL_PATH_SERVICES = SERVER + "/api/Service/";

    //Request headers
    public static final String HEADER_NAME_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_VALUE_CONTENT_TYPE = "application/json";
    //public static final String URL_SOCKET = "ws://tracking-dev.o7tc.com/hubs/Bluetooth";
    //public static final String URL_SOCKET = SERVER + "/Hubs/Bluetooth";
    public static final String WEBSOCKET_TRACKING = "/hubs/Tracking";
    //

}