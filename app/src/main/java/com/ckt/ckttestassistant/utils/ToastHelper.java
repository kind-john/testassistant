package com.ckt.ckttestassistant.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

/**
 * Created by ckt on 18-3-23.
 */

public class ToastHelper {
    private static Toast myToast = null;
    public static void showToast(Context context, @StringRes int resId, int duration) {
        if (myToast == null) {
            myToast = Toast.makeText(context, resId, duration);
        } else {
            myToast.setText(resId);
        }
        myToast.show();
    }

    public static void showToast(Context context, String message, int duration) {
        if (myToast == null) {
            myToast = Toast.makeText(context, message, duration);
        } else {
            myToast.setText(message);
        }
        myToast.show();
    }
}
