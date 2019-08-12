package com.farooq.smartapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.farooq.smartapp.model.RecordObj;

public class DetailActivity extends BaseActivity implements View.OnClickListener {

    private RecordObj mRecordObj;
    private TextView txtScopeName, txtAccessoryName, txtPostTime, txtPumpStartTime, txtATPTime, txtVisualTime, txtReturnTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
    }

    private void initView() {

        mRecordObj = (RecordObj) getIntent().getSerializableExtra("recordobj");

        txtScopeName = (TextView)findViewById(R.id.txtScopeName);
        txtAccessoryName = (TextView)findViewById(R.id.txtAccessoryName);
        txtPostTime = (TextView)findViewById(R.id.txtPost);
        txtPumpStartTime = (TextView)findViewById(R.id.txtPumpStart);
        txtATPTime = (TextView)findViewById(R.id.txtATP);
        txtVisualTime = (TextView)findViewById(R.id.txtVisual);
        txtReturnTime = (TextView)findViewById(R.id.txtReturn);

        txtScopeName.setText(mRecordObj.getScopeObj().getName());
        txtAccessoryName.setText(mRecordObj.getAccessoryObj().getItemCodeObj().getName());

        if (mRecordObj.getPostProcessTime() != null){
            txtPostTime.setText("Post process operation done at " + mRecordObj.getPostProcessTime().replace("T", " "));
        }
        else {
            txtPostTime.setText("Post process operation done at ");
        }

        if (mRecordObj.getPumpActivityTime() != null) {
            txtPumpStartTime.setText("Pump started at " + mRecordObj.getPumpActivityTime().replace("T", " "));
        }
        else {
            txtPumpStartTime.setText("Pump started at ");
        }

        if (mRecordObj.getAtpTime() != null){
            txtATPTime.setText("ATP Test started at " + mRecordObj.getAtpTime().replace("T" , " "));
        }
        else {
            txtATPTime.setText("ATP Test started at ");
        }

        txtVisualTime.setText("VisualInspection started at");

        if(mRecordObj.getReturnTime() != null){
            txtReturnTime.setText("Scope returned to cabinet at " + mRecordObj.getReturnTime().replace("T" , " "));
        }
        else {
            txtReturnTime.setText("Scope returned to cabinet at ");
        }


        findViewById(R.id.imgBack).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }

}
