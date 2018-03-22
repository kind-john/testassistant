package com.ckt.ckttestassistant.utils;

import android.content.Context;

/**
 * Created by ckt on 18-3-19.
 */

public interface SystemInvokeInterface {
    boolean checkAllSimIsReady(Context context);

    boolean launchSuccess(Context context, String packageName, String activityName);

    void openWifi(Context context);
    void closeWifi(Context context);
    boolean wifiIsEnabled();

    void openAPMMode(Context context);
    void closeAPMMode(Context context);
    boolean APMModeIsEnabled();

    void openBluetooth(Context context);
    void closeBluetooth(Context context);
    boolean BluetoothIsEnabled();

    void screenOn(Context context);
    void screenOff(Context context);

    void openGps(Context context);
    void closeGps(Context context);
    boolean GpsIsEnabled();

    void MyWakeUp(Context context);
    void MySleep(Context context);

    void MyReboot(Context context);
    void MyBrightnessChange(Context context, int value);

    void openNFC(Context context);
    void closeNFC(Context context);
    boolean NFCIsEnabled();
}
