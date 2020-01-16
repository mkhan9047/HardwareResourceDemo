package com.farooq.smartapp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.farooq.smartapp.Constants;
import com.farooq.smartapp.InstrumentAdapter;
import com.farooq.smartapp.MainActivity;
import com.farooq.smartapp.R;
import com.farooq.smartapp.datamodel.Engine;
import com.farooq.smartapp.dialog.InstrumentCustomDialog;
import com.farooq.smartapp.model.InstrumentObj;
import com.farooq.smartapp.server.WebServices;
import com.farooq.smartapp.utils.DialogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.farooq.smartapp.Constants.checkInternetConnection;
import static com.farooq.smartapp.Constants.setFragment;

public class InstrumentFragment extends Fragment implements ItemClickListener {
    private ArrayList<InstrumentObj> mArrayInstrument = new ArrayList<>();
    private InstrumentAdapter instrumentListAdapter;
    private RecyclerView instrumentalist;
    private FloatingActionButton fab;
    private ImageView back;
    private Dialog pdProgress;
    private SwipeRefreshLayout pullToRefresh;
    TextView instrument, general;
    private InstrumentCustomDialog addNewInstrumentDialog;
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";


    public InstrumentFragment() {
        // Required empty public constructor
        setHasOptionsMenu(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Settings");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instrument, container, false);
        instrumentListAdapter = new InstrumentAdapter(mArrayInstrument, getActivity());
        instrumentalist = view.findViewById(R.id.lstInstrument);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        instrument = view.findViewById(R.id.tv_instrument);
        general = view.findViewById(R.id.tv_general);
        fab = view.findViewById(R.id.fab);
        back = view.findViewById(R.id.btn_back);

        onClick();
        initAdapter();
        loadInstrumentList();
        return view;
    }

    private void onClick() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewInstrumentDialog = new InstrumentCustomDialog(getActivity());
                addNewInstrumentDialog.show();

                addNewInstrumentDialog.setInstrumentDialogCallback(new InstrumentCustomDialog.InstrumentDialogCallback() {
                    @Override
                    public void onRegisterSuccess(boolean isSuccess) {
                        if (isSuccess) {
                            loadInstrumentList();
                            addNewInstrumentDialog.dismiss();
                        }
                    }
                });
            }
        });
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                loadInstrumentList();
            }
        });

        general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment((AppCompatActivity) InstrumentFragment.this.getActivity(), new GeneralFragment());
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }


    private void initAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        instrumentalist.setLayoutManager(mLayoutManager);
        instrumentalist.setItemAnimator(new DefaultItemAnimator());
        instrumentalist.setAdapter(instrumentListAdapter);

        instrumentListAdapter.setClickListener(this);
    }

    private void loadInstrumentList() {
        try {
            if (!checkInternetConnection(Objects.requireNonNull(getActivity())))
            {
                if (pullToRefresh.isRefreshing()) {
                    pullToRefresh.setRefreshing(false);
                }
                return;
            }
            showProgress();
            WebServices.getInstance().instrumentList(getActivity(),
                    new AjaxCallback<String>() {
                        public void callback(String url, String json, AjaxStatus status) {
                            hideProgress();
                            if (pullToRefresh.isRefreshing()) {
                                pullToRefresh.setRefreshing(false);
                            }
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<InstrumentObj>>() {
                                }.getType();
                                mArrayInstrument = gson.fromJson(jsonObject.getString(Constants.Key_Instrument), listType);
                                instrumentListAdapter.refresh(mArrayInstrument);
                            } catch (Exception ignored) {
                                Toast.makeText(InstrumentFragment.this.getActivity(), "Failed to parse Instrument response.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception ex) {
            Toast.makeText(InstrumentFragment.this.getActivity(), "Failed to get Instrument." + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgress() {
        if (pdProgress == null)
            pdProgress = DialogUtils.getInstance().createProgress(getActivity());
        pdProgress.show();
    }

    private void hideProgress() {
        if (pdProgress != null && pdProgress.isShowing())
            pdProgress.dismiss();
    }


    @Override
    public void onClick(View view, int position) {
        /* tap on any instruments you should call /api/Instrument/ProcessIdentity
         * and pass tabletId, RFID of instrument and show the result in a toast
         *
         * *leave barcode and userbadge empty
         * */
    }


    public boolean setScanIdForNewIntrument(String scanTag) {
        if (addNewInstrumentDialog != null) {
            if (addNewInstrumentDialog.isShowing()) {
                return addNewInstrumentDialog.setScanTag(scanTag);
            }
        }
        return false;
    }
}