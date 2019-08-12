package com.farooq.smartapp.model;

import org.json.JSONObject;

import java.io.Serializable;

public class ScopeObj implements Serializable {

    private String     id;
    private String  companyId;
    private int     status;
    private String  rfValue;
    private String  model;
    private String  name;
    private String  notes;
    private int     lowerLimit;
    private int     upperLimit;
    private String  imagePath;
    private String  thumbnailPath;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRfValue() {
        return rfValue;
    }

    public void setRfValue(String rfValue) {
        this.rfValue = rfValue;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(int lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public int getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(int upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public void parseFromJson(JSONObject jsonObject) {

        try {
            this.id = jsonObject.getString("id");
            this.companyId = jsonObject.getString("companyId");
            this.rfValue = jsonObject.getString("rfValue");
            this.model = jsonObject.getString("model");
            this.name = jsonObject.getString("name");
            this.notes = jsonObject.getString("notes");
            this.lowerLimit = jsonObject.getInt("lowLimit");
            this.upperLimit = jsonObject.getInt("upperLimit");
            this.imagePath = jsonObject.getString("imagePath");
            this.thumbnailPath = jsonObject.getString("thumbnailPath");
        }
        catch (Exception e){

        }


    }
}
