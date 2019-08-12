package com.farooq.smartapp;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.farooq.smartapp.model.RecordObj;
import com.farooq.smartapp.model.ScopeObj;

import java.util.ArrayList;
import java.util.List;

public class ScopeListActivity extends BaseActivity {


    private ArrayList<ScopeObj> mArrScopes = new ArrayList<>();
    private ListView lstScope;
    private ScopeListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scope_list);
        initActivity();
    }

    private void initActivity() {
        lstScope = (ListView)findViewById(R.id.lstScope);

        ScopeObj scopeObj = new ScopeObj();
        scopeObj.setName("Scope1");
        scopeObj.setStatus(0);
        mArrScopes.add(scopeObj);

        ScopeObj scopeObj1 = new ScopeObj();
        scopeObj1.setName("Scope2");
        scopeObj1.setStatus(1);
        mArrScopes.add(scopeObj1);

        ScopeObj scopeObj2 = new ScopeObj();
        scopeObj2.setName("Scope3");
        scopeObj2.setStatus(2);
        mArrScopes.add(scopeObj2);

        mAdapter = new ScopeListAdapter(this, android.R.layout.simple_spinner_item, mArrScopes);
        lstScope.setAdapter(mAdapter);
    }


    public class ScopeListAdapter extends ArrayAdapter<ScopeObj> {
        private List<ScopeObj> devices;
        private LayoutInflater inflater;

        public ScopeListAdapter(Context context, int textViewResourceId, List<ScopeObj> items) {
            super(context, textViewResourceId, items);
            this.devices = items;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            final ScopeObj scopeObj = mArrScopes.get(position);

            if (view == null) {
                view = inflater.inflate(R.layout.item_scope_list, null);

            }
            else {

            }
            TextView txtName = (TextView)view.findViewById(R.id.txtScopeName);
            TextView txtStatus = (TextView)view.findViewById(R.id.txtScopeStatus);

            txtName.setText(scopeObj.getName());
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
            }
            return view;
        }
    }
}
