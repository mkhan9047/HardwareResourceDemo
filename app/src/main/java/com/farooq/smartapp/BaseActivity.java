package com.farooq.smartapp;

import android.app.Activity;
import android.app.Dialog;

import com.farooq.smartapp.utils.DialogUtils;


public class BaseActivity extends Activity {
    private Dialog pdProgress;

    /**
     * Show progress
     */
    public void showProgress() {
        if (pdProgress == null)
            pdProgress = DialogUtils.getInstance().createProgress(this);
        pdProgress.show();
    }

    /**
     * Hide progress
     */
    public void hideProgress() {
        if (pdProgress != null && pdProgress.isShowing())
            pdProgress.dismiss();
    }

}
