package com.farooq.smartapp.model;

import com.baoyachi.stepview.bean.StepBean;

import org.json.JSONObject;

import java.io.Serializable;

public class StepItem implements Serializable {

    private String id;
    private String procedureDefinitionId;
    private String stepDefinitionName;
    private String stepDefinitionId;
    private String userId;
    private String time;
    //    private double duration;
    private int sortIndex;
    private boolean skipped;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProcedureDefinitionId() {
        return procedureDefinitionId;
    }

    public void setProcedureDefinitionId(String procedureDefinitionId) {
        this.procedureDefinitionId = procedureDefinitionId;
    }

    public String getStepDefinitionName() {
        return stepDefinitionName;
    }

    public void setStepDefinitionName(String stepDefinitionName) {
        this.stepDefinitionName = stepDefinitionName;
    }

    public String getStepDefinitionId() {
        return stepDefinitionId;
    }

    public void setStepDefinitionId(String stepDefinitionId) {
        this.stepDefinitionId = stepDefinitionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public boolean isSkipped() {
        return skipped;
    }

    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }


    public StepBean getUIStepBean() {
        String stepLabel=" ";
        int state = StepBean.STEP_COMPLETED;
        if (time==null)
        {
            state = StepBean.STEP_UNDO;
        }
        if (skipped)
        {
            state = StepBean.STEP_CURRENT;
        }
        return new StepBean(stepLabel, state);
    }

    @Override
    public String toString() {
        return getStepDefinitionName();
    }
}
