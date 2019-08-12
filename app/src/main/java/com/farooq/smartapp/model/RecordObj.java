package com.farooq.smartapp.model;

import org.json.JSONObject;

import java.io.Serializable;

public class RecordObj implements Serializable {
    private String id;
    private String userId;
    private AccessoryObj accessoryObj;
    private ScopeObj scopeObj;
    private String preProcessTime;
    private String postProcessTime;
    private String pumpActivityTime;
    private String atpTime;
    private int rluValue;
    private String returnTime;
    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public AccessoryObj getAccessoryObj() {
        return accessoryObj;
    }

    public void setAccessoryObj(AccessoryObj accessoryObj) {
        this.accessoryObj = accessoryObj;
    }

    public ScopeObj getScopeObj() {
        return scopeObj;
    }

    public void setScopeObj(ScopeObj scopeObj) {
        this.scopeObj = scopeObj;
    }

    public String getPreProcessTime() {
        return preProcessTime;
    }

    public void setPreProcessTime(String preProcessTime) {
        this.preProcessTime = preProcessTime;
    }

    public String getPostProcessTime() {
        return postProcessTime;
    }

    public void setPostProcessTime(String postProcessTime) {
        this.postProcessTime = postProcessTime;
    }

    public String getPumpActivityTime() {
        return pumpActivityTime;
    }

    public void setPumpActivityTime(String pumpActivityTime) {
        this.pumpActivityTime = pumpActivityTime;
    }

    public String getAtpTime() {
        return atpTime;
    }

    public void setAtpTime(String atpTime) {
        this.atpTime = atpTime;
    }

    public int getRluValue() {
        return rluValue;
    }

    public void setRluValue(int rluValue) {
        this.rluValue = rluValue;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void parseFromJson(JSONObject jsonObject) {
        try{
            this.id = jsonObject.getString("id");
            this.userId = jsonObject.getString("userId");
            this.preProcessTime = jsonObject.getString("preProcessTime").equals("null") ? null : jsonObject.getString("preProcessTime");
            this.postProcessTime = jsonObject.getString("postProcessTIme").equals("null") ?  null : jsonObject.getString("postProcessTIme");
            this.pumpActivityTime = jsonObject.getString("pumpActivityTIme").equals("null") ? null : jsonObject.getString("pumpActivityTIme");
            this.atpTime = jsonObject.getString("atpTime").equals("null") ? null : jsonObject.getString("atpTime");
            this.rluValue = jsonObject.getInt("rluValue");
            this.returnTime = jsonObject.getString("returnTime").equals("null") ? null : jsonObject.getString("returnTime");
            this.status = jsonObject.getInt("status");
            JSONObject accObj = jsonObject.getJSONObject("accessory");
            this.accessoryObj = new AccessoryObj();
            this.accessoryObj.parseFromJson(accObj);

            this.scopeObj = new ScopeObj();
            JSONObject scopeJson = jsonObject.getJSONObject("scope");
            this.scopeObj.parseFromJson(scopeJson);

        }
        catch (Exception e){

        }

    }
}
