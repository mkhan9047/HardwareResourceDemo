package com.farooq.smartapp.utils;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;

import com.farooq.smartapp.R;

/**
 * All the alert dialogs used throughout the app written here.
 */
public class DialogUtils {
    //singleton instance
    private static DialogUtils instance;

    //alert dialog
    private AlertDialog alertDialog;

    private DialogUtils() {

    }

    public static DialogUtils getInstance() {
        if (instance == null) {
            instance = new DialogUtils();
        }
        return instance;
    }

    /**
     * create progress dialog
     *
     * @param context
     */
    public Dialog createProgress(Context context) {
        final Dialog pdProgress = new Dialog(context);
        pdProgress.getWindow();
        pdProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pdProgress.setContentView(R.layout.view_progress_indicator);
        pdProgress.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        pdProgress.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        pdProgress.setCancelable(false);
        return pdProgress;
    }

    public static Dialog showAlert(CharSequence title, CharSequence message, Context context,
                                   CharSequence positiveBtnText, CharSequence negativeBtnText, DialogInterface.OnClickListener positiveListener,
                                   DialogInterface.OnClickListener negativeListener) {

        final android.app.AlertDialog alert = new android.app.AlertDialog.Builder(context).create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        if (positiveListener != null && positiveBtnText != null) {
            alert.setButton(android.app.AlertDialog.BUTTON_POSITIVE, positiveBtnText, positiveListener);
        }
        if (negativeListener != null && negativeBtnText != null) {
            alert.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, negativeBtnText, negativeListener);
        }
        alert.setTitle(title);
        alert.setMessage(message);
        alert.show();
        return alert;
    }

}
