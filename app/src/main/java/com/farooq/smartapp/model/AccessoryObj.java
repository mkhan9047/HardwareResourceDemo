package com.farooq.smartapp.model;

import org.json.JSONObject;

import java.io.Serializable;

public class AccessoryObj implements Serializable {

    private String  id;
    private int     itemCodeId;
    private String  barcodeValue;
    private int     status;
    private ItemCodeObj itemCodeObj;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getItemCodeId() {
        return itemCodeId;
    }

    public void setItemCodeId(int itemCodeId) {
        this.itemCodeId = itemCodeId;
    }

    public String getBarcodeValue() {
        return barcodeValue;
    }

    public void setBarcodeValue(String barcodeValue) {
        this.barcodeValue = barcodeValue;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ItemCodeObj getItemCodeObj() {
        return itemCodeObj;
    }

    public void setItemCodeObj(ItemCodeObj itemCodeObj) {
        this.itemCodeObj = itemCodeObj;
    }

    public void parseFromJson(JSONObject jsonObject){

        try
        {
            this.id = jsonObject.getString("id");
            this.itemCodeId = jsonObject.getInt("itemCodeId");
            this.barcodeValue = jsonObject.getString("barcodeValue");
            this.status = jsonObject.getInt("status");

            JSONObject itemObj = jsonObject.getJSONObject("itemCode");
            this.itemCodeObj = new ItemCodeObj();
            this.itemCodeObj.parseFromJson(itemObj);
        }
        catch (Exception e){

        }
    }
}
