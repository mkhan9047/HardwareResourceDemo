package com.farooq.smartapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.farooq.smartapp.fragment.ItemClickListener;
import com.farooq.smartapp.model.InstrumentObj;

import java.util.ArrayList;

public class InstrumentAdapter extends RecyclerView.Adapter<InstrumentAdapter.InstrumentHolder> {
    private ArrayList<InstrumentObj> procedureList;
    private Context mContext;

    private ItemClickListener clickListener;

    public InstrumentAdapter(ArrayList<InstrumentObj> instrumentsList, Context context) {
        this.procedureList = instrumentsList;
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
        final InstrumentObj instrument = procedureList.get(i);

        instrumentHolder.setName(instrument.getName());
        instrumentHolder.setModel(instrument.getModel());
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return procedureList == null ? 0 : procedureList.size();
    }

    public void refresh(ArrayList<InstrumentObj> mArrayInstrument) {
        this.procedureList = mArrayInstrument;
        this.notifyDataSetChanged();
    }

    public class InstrumentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtName, txtModel;

        InstrumentHolder(View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtInstrumentName);
            txtModel = itemView.findViewById(R.id.txtInstrumentModel);

            itemView.setOnClickListener(this);
        }

        public void setName(String name) {
            txtName.setText(name);
        }

        public void setModel(String model) {
            txtModel.setText(model);
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getAdapterPosition());
        }
    }
}


