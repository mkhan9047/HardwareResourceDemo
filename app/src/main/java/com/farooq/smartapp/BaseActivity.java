package com.farooq.smartapp;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;

import com.farooq.smartapp.utils.DialogUtils;


public class BaseActivity extends AppCompatActivity {
    private Dialog pdProgress;

    /**
     * Show progress
     */
    public void showProgress() {
        try {
            if (this.isFinishing()) {
                return;
            }
            pdProgress = DialogUtils.getInstance().createProgress(this);
            pdProgress.show();
        }catch (Exception e){}
    }

    /**
     * Hide progress
     */
    public void hideProgress() {
        try {
            if (pdProgress != null && pdProgress.isShowing())
                pdProgress.dismiss();
        }catch (Exception ex){}

    }

}
