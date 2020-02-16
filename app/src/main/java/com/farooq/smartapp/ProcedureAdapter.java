package com.farooq.smartapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.farooq.smartapp.model.ProcedureObj;
import com.farooq.smartapp.model.StepItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProcedureAdapter extends RecyclerView.Adapter<ProcedureAdapter.InstrumentHolder> {

    private MainActivity mainActivity;

    public ProcedureAdapter(MainActivity activity) {
        this.mainActivity = activity;
    }


    @Override
    public InstrumentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_scope, parent, false);
        return new InstrumentHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull InstrumentHolder instrumentHolder, int i) {
        final ProcedureObj procedureObj = mainActivity.mArrayProcedureList.get(i);
        try {
            instrumentHolder.setName(procedureObj.getInstrumentName());
        } catch (Exception ignored) {
        }
        instrumentHolder.setSkippedBackGround(instrumentHolder
                .setStepViewName(procedureObj));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mainActivity.mArrayProcedureList == null ? 0 :
                mainActivity.mArrayProcedureList.size();
    }

    public void refresh() {
        this.notifyDataSetChanged();
    }

    public void procedureRemove(int position) {
        this.notifyItemRemoved(position);
    }

    public void procedureAddonTop(ProcedureObj addedProcedureObj) {
        mainActivity.mArrayProcedureList.add(0, addedProcedureObj);
        refresh();
    }

    void procedureMoveToTop(int position, ProcedureObj updatedProcedureObj) {
//        if (updatedProcedureObj != null) {
//            for (int i = 0; i < mainActivity.mArrayProcedureList.size(); i++) {
//                if (mainActivity.mArrayProcedureList.get(i).getId().equalsIgnoreCase(updatedProcedureObj.getId())) {
//                    if (updatedProcedureObj.getSteps().size() <= 0) {
//                        updatedProcedureObj.setSteps(mainActivity.mArrayProcedureList.get(i).getSteps());
//                    }
//                    mainActivity.mArrayProcedureList.set(i, updatedProcedureObj);
//                    break;
//                }
//            }
//        }
        notifyDataSetChanged();
        if (position > 0) {
            Collections.swap(mainActivity.mArrayProcedureList, position, 0);
            notifyItemMoved(position, 0);
        }
    }

    public class InstrumentHolder extends RecyclerView.ViewHolder {
        private MaterialCardView card_view_procedure;
        private TextView txtName;
        private HorizontalStepView progressStepView;
        private View itemView;
        private TextView txtDisplayName;

        InstrumentHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            txtName = itemView.findViewById(R.id.txtScopeName);
            progressStepView = itemView.findViewWithTag(R.id.stepview);
            card_view_procedure = itemView.findViewById(R.id.card_view_procedure);
            // txtDisplayName = itemView.findViewById(R.id.txt_display_name);
        }

        public void setName(String name) {
            txtName.setText(name);
        }

        private boolean setStepViewName(ProcedureObj procedureObj) {
            progressStepView = itemView.findViewById(R.id.stepview);
            progressStepView
                    .setTextSize(10)//set textSize
                    .setStepsViewIndicatorCompletedLineColor(mainActivity.getResources().getColor(R.color.colorPrimary))
                    .setStepsViewIndicatorUnCompletedLineColor(mainActivity.getResources().getColor(R.color.colorPrimary))
                    .setStepViewComplectedTextColor(mainActivity.getResources().getColor(R.color.black_transfarent))
                    .setStepViewUnComplectedTextColor(mainActivity.getResources().getColor(R.color.clr_orange));
            progressStepView.setStepViewTexts(new ArrayList<>());
            return UpdateProgressViewWithSelectedScope(progressStepView, procedureObj);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void setSkippedBackGround(boolean isStepView) {
            if (isStepView) {
                //this.itemView.setBackground(mainActivity.getDrawable(R.drawable.skipedbg));
                card_view_procedure.setStrokeColor(mainActivity.getResources().getColor(R.color.clr_red));
            } else {
                //this.itemView.setBackgroundColor(Color.TRANSPARENT);
                card_view_procedure.setStrokeColor(mainActivity.getResources().getColor(R.color.clr_white));
            }
        }
    }

    private boolean UpdateProgressViewWithSelectedScope(HorizontalStepView progressStepView, ProcedureObj selectedProcedureObj) {
        List<StepItem> mArrSteps = selectedProcedureObj.getSteps();
        List<StepBean> stepsBeanList = new ArrayList<>();
        Boolean isSkipped = false;
        for (int i = 0; i < mArrSteps.size(); i++) {
            StepBean stepBean = mArrSteps.get(i).getUIStepBean();
            if (mArrSteps.get(i).getId().equalsIgnoreCase(selectedProcedureObj.getCurrentStepId())) {
                if (selectedProcedureObj.getDisplayName() != null) {
                    stepBean.setName(String.format("%s - %s", selectedProcedureObj.getCurrentStepStepDefinitionName(),
                            selectedProcedureObj.getDisplayName()));
                } else {
                    stepBean.setName(String.format("%s", selectedProcedureObj.getCurrentStepStepDefinitionName()));
                }

            }
            if (!isSkipped)
                isSkipped = mArrSteps.get(i).isSkipped();
            stepsBeanList.add(stepBean);
        }
        if (stepsBeanList.size() <= 0) {
            progressStepView.setVisibility(View.GONE);
        } else {
            progressStepView.setVisibility(View.VISIBLE);
        }
        progressStepView.setStepViewTexts(stepsBeanList);
        return isSkipped;
    }

}