package com.farooq.smartapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.farooq.smartapp.model.ScopeObj;

import java.util.ArrayList;

public class ScopeCardsActivity extends BaseActivity {


    private GridView gridView;
    private ArrayList<ScopeObj> mArrScopes = new ArrayList<>();
    private ScopeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scope_cards);
        initActivity();
    }

    private void initActivity() {
        gridView = findViewById(R.id.grdScope);
        for (int i = 0; i < 10; i++) {
            ScopeObj scopeObj = new ScopeObj();
            mArrScopes.add(scopeObj);
        }
        mAdapter = new ScopeAdapter(this, mArrScopes);
        gridView.setAdapter(mAdapter);
    }

    public static class ScopeAdapter extends BaseAdapter {
        private Context context;
        private final ArrayList<ScopeObj> mArrScopes;

        public ScopeAdapter(Context context, ArrayList<ScopeObj> arrScopes) {
            this.context = context;
            this.mArrScopes = arrScopes;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View gridView;

            if (convertView == null) {

                gridView = new View(context);

                // get layout from mobile.xml
                gridView = inflater.inflate(R.layout.grid_item_scope, null);

            } else {
                gridView = convertView;
            }

            return gridView;
        }

        @Override
        public int getCount() {
            return mArrScopes.size();
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
