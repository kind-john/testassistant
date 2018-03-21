package com.ckt.ckttestassistant.utils;

import android.content.Context;

/**
 * Created by ckt on 18-3-19.
 */

public interface SystemInvokeInterface {
    public boolean checkAllSimIsReady(Context context);

    public boolean launchSuccess(Context context, String packageName, String activityName);

    public void openWifi(Context context);
    public void closeWifi(Context context);
    public boolean wifiIsEnabled();

    public void openAPMMode(Context context);
    public void closeAPMMode(Context context);
    public boolean APMModeIsEnabled();

    public void openBluetooth(Context context);
    public void closeBluetooth(Context context);
    public boolean BluetoothIsEnabled();

    public void screenOn(Context context);
    public void screenOff(Context context);

    public void openGps(Context context);
    public void closeGps(Context context);
    public boolean GpsIsEnabled();

    public void MyWakeUp(Context context);
    public void MySleep(Context context);

    public void MyReboot(Context context);
    public void MyBrightnessChange(Context context, int value);

    public void openNFC(Context context);
    public void closeNFC(Context context);
    public boolean NFCIsEnabled();
}
