package com.farooq.smartapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ProcedureObj {
    private String id;
    private String instrumentName;
    private String currentStepStepDefinitionName;
    private String currentStepId;
    private ArrayList<StepItem> steps = new ArrayList<>();
    private InstrumentObj instrument;
    public static ChangeType InitChangeType(int chnageType)
    {
        if (chnageType==1)
        {
            return ChangeType.StepChanged;
        }
        else if (chnageType==2)
        {
            return ChangeType.Finished;
        }
        return ChangeType.Started;
    }


    public enum ChangeType {
        Started(0),
        StepChanged(1),
        Finished(2);
        public int chnageType;
        ChangeType(int controlType) {
            this.chnageType = chnageType;
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public String getCurrentStepStepDefinitionName() {
        return currentStepStepDefinitionName;
    }

    public void setCurrentStepStepDefinitionName(String currentStepStepDefinitionName) {
        this.currentStepStepDefinitionName = currentStepStepDefinitionName;
    }

    public String getCurrentStepId() {
        return currentStepId;
    }

    public void setCurrentStepId(String currentStepId) {
        this.currentStepId = currentStepId;
    }

    public ArrayList<StepItem> getSteps() {

        try {
            Collections.sort(steps, SortByIndexComparator);
        }catch (Exception ex)
        {
            return new ArrayList<StepItem>();
        }
        return steps;
    }

    public void setSteps(ArrayList<StepItem> steps) {
        this.steps = steps;
    }
    public static Comparator<StepItem> SortByIndexComparator = new Comparator<StepItem>() {

        @Override
        public int compare(StepItem e1, StepItem e2) {
            return e1.getSortIndex() - e2.getSortIndex();
        }
    };

    public InstrumentObj getInstrument() {
        return instrument;
    }

    public void setInstrument(InstrumentObj instrument) {
        this.instrument = instrument;
    }
}
