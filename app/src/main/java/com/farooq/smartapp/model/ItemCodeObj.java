package com.farooq.smartapp.model;

import org.json.JSONObject;

import java.io.Serializable;

public class ItemCodeObj implements Serializable {

    private String     id;
    private String  code;
    private String  name;
    private String  description;
    private String  imagePath;
    private String  thumbnailPath;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void parseFromJson(JSONObject jsonObject){
        try{
            this.id = jsonObject.getString("id");
            this.code = jsonObject.getString("code");
            this.name = jsonObject.getString("name");
            this.description = jsonObject.getString("description");
            this.imagePath = jsonObject.getString("imagePath");
            this.thumbnailPath = jsonObject.getString("thumbnailPath");
        }
        catch (Exception e){

        }
    }
}
