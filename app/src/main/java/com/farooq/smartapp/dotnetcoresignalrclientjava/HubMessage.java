package com.farooq.smartapp.dotnetcoresignalrclientjava;

import com.google.gson.JsonElement;

import org.json.JSONArray;

public class HubMessage {
    private String invocationId = "";
    private String target = "";
    private JSONArray arguments;

    public HubMessage(String invocationId, String target, JSONArray arguments) {
        this.invocationId = invocationId;
        this.target = target;
        this.arguments = arguments;
    }

    public String getInvocationId() {
        return invocationId;
    }

    public void setInvocationId(String invocationId) {
        this.invocationId = invocationId;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public JSONArray getArguments() {
        return arguments;
    }

    public void setArguments(JSONArray arguments) {
        this.arguments = arguments;
    }
}
