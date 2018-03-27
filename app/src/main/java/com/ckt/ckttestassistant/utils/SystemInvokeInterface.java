package com.ckt.ckttestassistant.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by ckt on 18-3-19.
 */

public interface SystemInvokeInterface {
    boolean checkAllSimIsReady(Context context);

    boolean launchSuccess(Context context, String packageName, String activityName);
    void clearAllRecentsApp(Context context);

    void openWifi(Context context);
    void closeWifi(Context context);
    boolean wifiIsEnabled();

    void openAPMMode(Context context);
    void closeAPMMode(Context context);

    boolean APMModeIsEnabled(Context context);

    void openBluetooth(Context context);
    void closeBluetooth(Context context);
    boolean BluetoothIsEnabled();

    void screenOn(Context context);
    void screenOff(Context context);

    void openGps(Context context);
    void closeGps(Context context);
    boolean gpsIsEnabled();

    void myWakeUp(Context context);
    void mySleep(Context context);

    void myReboot(Context context);

    void mySetBrightness(Context context, int value);
    int myGetBrightness(Context context);

    void openNFC(Context context);
    void closeNFC(Context context);

    boolean nfcIsEnabled();
}
