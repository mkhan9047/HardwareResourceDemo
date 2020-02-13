package com.farooq.smartapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.farooq.smartapp.model.InstrumentObj;

import java.util.ArrayList;

public class InstrumentListAdapter extends RecyclerView.Adapter<InstrumentListAdapter.InstrumentHolder> {
    private ArrayList<InstrumentObj> instrumentsList;
    private Context mContext;

    public InstrumentListAdapter(ArrayList<InstrumentObj> instrumentsList, Context context) {
        this.instrumentsList = instrumentsList;
        this.mContext = context;
    }

    @Override
    public InstrumentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.instrument_list_item, parent, false);
        return new InstrumentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstrumentHolder instrumentHolder, int i) {
        final InstrumentObj instrument = instrumentsList.get(i);
        instrumentHolder.setName(instrument.getName());
        instrumentHolder.setModel(instrument.getModel());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return instrumentsList == null ? 0 : instrumentsList.size();
    }

    public void refresh(ArrayList<InstrumentObj> mArrayInstrument) {
        this.instrumentsList = mArrayInstrument;
        this.notifyDataSetChanged();
    }

    public class InstrumentHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtModel;

        InstrumentHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtInstrumentName);
            txtModel = itemView.findViewById(R.id.txtInstrumentModel);
        }

        public void setName(String name) {
            txtName.setText(name);
        }

        public void setModel(String model) {
            txtModel.setText(model);
        }
    }
}


