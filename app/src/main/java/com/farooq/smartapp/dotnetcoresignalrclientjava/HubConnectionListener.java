package com.farooq.smartapp.dotnetcoresignalrclientjava;

public interface HubConnectionListener {
    void onInitConnection();

    void onConnecting();

    void onConnected();

    void onDisconnected();

    void onMessage(HubMessage message);

    void onError(Exception exception);
}
