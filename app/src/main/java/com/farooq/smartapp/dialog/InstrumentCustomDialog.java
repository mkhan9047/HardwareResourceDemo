package com.farooq.smartapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.farooq.smartapp.Constants;
import com.farooq.smartapp.R;
import com.farooq.smartapp.model.InstrumentObj;
import com.farooq.smartapp.server.WebServices;
import com.farooq.smartapp.utils.DialogUtils;

import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.farooq.smartapp.Constants.checkInternetConnection;

public class InstrumentCustomDialog extends Dialog {

    private Context context;

    private EditText rfValue, model, name, notes, lowerLimit, upperLimit;
    private Button btnSubmit;
    private Dialog pdProgress;
    private InstrumentDialogCallback instrumentDialogCallback;


    public InstrumentCustomDialog(final Context context) {
        super(context);
        this.context = context;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout container = new LinearLayout(getContext());
        container.setLayoutParams(params);
        container.setBackgroundColor(Color.WHITE);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(0, 10, 0, 10);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.instrumentpopup);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Objects.requireNonNull(getWindow()).setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.CENTER);
        }
        findId();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (rfValue.getText().toString().trim().length() == 0) {
                    rfValue.setError("RF Value can`t be empty ?");
                } else if (model.getText().toString().trim().length() <= 0) {
                    model.setError("Model can`t be empty !");
                } else if (name.getText().toString().trim().length() <= 0) {
                    name.setError("Name can`t be empty !");
                } else if (lowerLimit.getText().toString().trim().length() <= 0) {
                    lowerLimit.setError("Lower Limit can`t be empty.");
                } else if (upperLimit.getText().toString().trim().length() <= 0) {
                    upperLimit.setError("Upper Limit can`t be empty.");
                } else if (!validateLimit()) {
                    lowerLimit.setError("Lower limit should be less than upper Limit.");
                } else {
                    createTestInstrument();
                }
            }
        });
    }

    private boolean validateLimit() {
        try {
            int upper = Integer.parseInt(upperLimit.getText().toString().trim());
            int lower = Integer.parseInt(lowerLimit.getText().toString().trim());
            return lower < upper;

        } catch (NumberFormatException ignored) {

        }
        return false;
    }

    private void createTestInstrument() {
        InstrumentObj instrumentObj = new InstrumentObj();
        instrumentObj.setRfValue(rfValue.getText().toString());
        instrumentObj.setBarcodeValue("");
        instrumentObj.setModel(model.getText().toString());
        instrumentObj.setName(name.getText().toString());
        instrumentObj.setNotes(notes.getText().toString());
        instrumentObj.setLowerLimit(Integer.parseInt(lowerLimit.getText().toString()));
        instrumentObj.setUpperLimit(Integer.parseInt(upperLimit.getText().toString()));
        if (!checkInternetConnection(getContext()))
        {
            return;
        }
        showProgress();
        WebServices.getInstance().registerInstrument(context, instrumentObj, new AjaxCallback<String>() {
            public void callback(String url, String json, AjaxStatus status) {
                hideProgress();
                try {
                    if (json == null) {
                        Toast.makeText(context, "Instrument register failed"
                                + (status != null ? (" : " + status.getMessage() + ".") : "."), Toast.LENGTH_LONG).show();
                    } else {
                        Log.i("DeviceRegisterActivity", json);
                        JSONObject jsonObject = new JSONObject(json);
                        boolean bSucess = jsonObject.getBoolean(Constants.Key_Success);
                        if (bSucess) {
                            Toast.makeText(context, "Instrument register successful", Toast.LENGTH_LONG).show();
                            instrumentDialogCallback.onRegisterSuccess(true);
                        } else {
                            if (jsonObject.has(Constants.Key_Message)) {
                                Toast.makeText(context, "" + jsonObject.getString(Constants.Key_Message), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "Instrument register failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Instrument register failed" +
                            ", Please check internet and try again", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void showProgress() {
        if (pdProgress == null)
            pdProgress = DialogUtils.getInstance().createProgress(context);
        pdProgress.show();
    }

    private void hideProgress() {
        if (pdProgress != null && pdProgress.isShowing())
            pdProgress.dismiss();
    }

    private void findId() {
        btnSubmit = findViewById(R.id.btnSubmit);
        rfValue = findViewById(R.id.edt_rfValue);
        model = findViewById(R.id.edt_model);
        name = findViewById(R.id.edt_name);
        notes = findViewById(R.id.edt_notes);
        lowerLimit = findViewById(R.id.edt_lowerLimit);
        upperLimit = findViewById(R.id.edt_upperLimit);
    }


    public void setInstrumentDialogCallback(InstrumentDialogCallback consentDialogCallback) {
        this.instrumentDialogCallback = consentDialogCallback;
    }

    public boolean setScanTag(String scanTag) {
        if (rfValue != null) {
            rfValue.setText(scanTag);
            rfValue.setEnabled(false);
            return true;
        } else {
            rfValue.setEnabled(true);
        }
        return false;
    }

    public interface InstrumentDialogCallback {
        void onRegisterSuccess(boolean isSuccess);
    }
}
