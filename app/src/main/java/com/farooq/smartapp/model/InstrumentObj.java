package com.farooq.smartapp.model;

import org.json.JSONObject;

import java.io.Serializable;

public class InstrumentObj implements Serializable {

    private String id;
    private String rfValue;
    private String barcodeValue;
    private String model;
    private String name;
    private String notes;
    private int lowerLimit;
    private int upperLimit;
    private String department;
    private String organization;
    private String facility;

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getDepartment() {
        return department;
    }

    public String getOrganization() {
        return organization;
    }

    public String getFacility() {
        return facility;
    }

    private String procedureDefinitionId;
    private int sortIndex;
    private boolean required;
    private int requiredEveryNProcedures;
    private boolean superUserCanSkip;
    private String icon;
    private String instructions;

    private String userBadge;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRfValue() {
        return rfValue;
    }

    public void setRfValue(String rfValue) {
        this.rfValue = rfValue;
    }

    public String getBarcodeValue() {
        return barcodeValue;
    }

    public void setBarcodeValue(String barcodeValue) {
        this.barcodeValue = barcodeValue;
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

    public String getProcedureDefinitionId() {
        return procedureDefinitionId;
    }

    public void setProcedureDefinitionId(String procedureDefinitionId) {
        this.procedureDefinitionId = procedureDefinitionId;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public int getRequiredEveryNProcedures() {
        return requiredEveryNProcedures;
    }

    public void setRequiredEveryNProcedures(int requiredEveryNProcedures) {
        this.requiredEveryNProcedures = requiredEveryNProcedures;
    }

    public boolean isSuperUserCanSkip() {
        return superUserCanSkip;
    }

    public void setSuperUserCanSkip(boolean superUserCanSkip) {
        this.superUserCanSkip = superUserCanSkip;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getUserBadge() {
        return userBadge;
    }

    public void setUserBadge(String userBadge) {
        this.userBadge = userBadge;
    }


    public JSONObject parseToJSON() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("rfValue", rfValue);
            jsonObject.put("barcodeValue", barcodeValue);
            jsonObject.put("model", model);
            jsonObject.put("name", name);
            jsonObject.put("notes", notes);
            jsonObject.put("lowerLimit", lowerLimit);
            jsonObject.put("upperLimit", upperLimit);
            jsonObject.put("facility", facility);
            jsonObject.put("organization", organization);
            jsonObject.put("department", department);
            return jsonObject;
        } catch (Exception e) {
        }
        return null;
    }
}
