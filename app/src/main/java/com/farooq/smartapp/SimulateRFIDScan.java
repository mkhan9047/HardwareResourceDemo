package com.farooq.smartapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.farooq.smartapp.model.ScopeObj;

import java.util.ArrayList;

public class SimulateRFIDScan extends AppCompatActivity {

    private GridView gridView;
    private ArrayList<ScopeObj> mArrProcedure = new ArrayList<>();
    private ProcedureAdapter mProcedureAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulate_rfidscan);

        ActionBar supportActionBar;
        supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.hide();

         ImageView back = findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView title = findViewById(R.id.title);
        title.setText("Start procedure");
        initActivity();

    }

    private void initActivity() {
        gridView = findViewById(R.id.grdProcedure);
        for (int i = 0; i < 10; i++) {
            ScopeObj scopeObj = new ScopeObj();
            mArrProcedure.add(scopeObj);
        }
        mProcedureAdapter = new ProcedureAdapter(SimulateRFIDScan.this, mArrProcedure);
        gridView.setAdapter(mProcedureAdapter);
    }

    public static class ProcedureAdapter extends BaseAdapter {
        private Context context;
        private final ArrayList<ScopeObj> mArrProcedure;

        public ProcedureAdapter(Context context, ArrayList<ScopeObj> arrScopes) {
            this.context = context;
            this.mArrProcedure = arrScopes;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View gridView;
            if (convertView == null) {
                gridView = new View(context);
                gridView = inflater.inflate(R.layout.grid_item_procedure, null);
            } else {
                gridView = convertView;
            }

            /*TextView txtName = (TextView)view.findViewById(R.id.txtScopeName);
            TextView txtStatus = (TextView)view.findViewById(R.id.txtScopeStatus);

            txtName.setText(mArrProcedure.getName());
            switch (scopeObj.getStatus()) {
                case 0:
                    txtStatus.setText("ATP Test");
                    break;
                case 1:
                    txtStatus.setText("Pump Station");
                    break;
                case 2:
                    txtStatus.setText("Camera");
                    break;
            }*/

            return gridView;
        }

        @Override
        public int getCount() {
            return mArrProcedure.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }
}
