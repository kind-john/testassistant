package com.ckt.ckttestassistant.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by ckt on 18-3-19.
 */

public class SystemInvokeImpl implements SystemInvokeInterface{
    private static final String TAG = "SystemInvokeImpl";
    private static volatile SystemInvokeImpl instance;
    private WifiManager mWifiManager;
    private SystemInvokeImpl() {

    }

    public static SystemInvokeImpl getInstance(){
        if (instance == null) {
            synchronized (SystemInvokeImpl.class) {
                if (instance == null) {
                    instance = new SystemInvokeImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 判断startActivity是否成功
     * @param context
     * @param packageName
     * @param activityName
     * @return
     */
    @Override
    public boolean launchSuccess(Context context, String packageName, String activityName){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final ActivityManager am = (ActivityManager)
                    context.getSystemService(Context.ACTIVITY_SERVICE);
            final List<ActivityManager.RecentTaskInfo> recentTasks =
                    am.getRecentTasks(1, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
            ActivityManager.RecentTaskInfo rt = recentTasks.get(0);
            String pn = rt.topActivity.getPackageName();
            String an = rt.topActivity.getClassName();
            if(packageName.equals(packageName) &&
                    activityName.equals(an)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearAllRecentsApp(Context context) {
        //EventBus.getDefault().send(new DismissAllTaskViewsEvent());
    }

    /**
     * 判断是否所有的SIM卡都已经准备好
     * @param context
     * @return
     */
    @Override
    public boolean checkAllSimIsReady(Context context){
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simCounts = 0;
        boolean simIsOK = false;
        try {
            if (telephonyManager != null) {
                Method getPhoneCount = telephonyManager.getClass().getDeclaredMethod("getPhoneCount");
                getPhoneCount.setAccessible(true);
                simCounts = (Integer) getPhoneCount.invoke(telephonyManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < simCounts; i++) {
            int newState = TelephonyManager.SIM_STATE_UNKNOWN;
            try {
                if (telephonyManager != null) {
                    Method getSimState = telephonyManager.getClass().
                            getDeclaredMethod("getSimState",
                                    new Class[]{int.class});
                    getSimState.setAccessible(true);
                    newState = (Integer) getSimState.invoke(telephonyManager, i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                simIsOK = (newState == TelephonyManager.SIM_STATE_READY);
                if(!simIsOK){
                    return false;
                }
            }
        }
        return simIsOK;
    }

    /**
     * 同步方法，执行到返回结果
     * @param context
     * @return
     */
    @Override
    public void openWifi(Context context) {
        boolean passed = false;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }
    }

    @Override
    public boolean wifiIsEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    /**
     * 同步方法，执行到返回结果
     * @param context
     * @return
     */
    @Override
    public void closeWifi(Context context) {
        boolean passed = false;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void openAPMMode(Context context) {
        Settings.Global.putInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON,
                1);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        intent.putExtra("state", true);
        context.sendBroadcast(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void closeAPMMode(Context context) {
        Settings.Global.putInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON,
                0);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        intent.putExtra("state", false);
        context.sendBroadcast(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean APMModeIsEnabled(Context context) {
        boolean isOn = false;
        try {
            isOn = Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON) == 1;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return isOn;
    }

    @Override
    public void openBluetooth(Context context) {

    }

    @Override
    public void closeBluetooth(Context context) {

    }

    @Override
    public boolean BluetoothIsEnabled() {
        return false;
    }

    @Override
    public void screenOn(Context context) {

    }

    @Override
    public void screenOff(Context context) {

    }

    @Override
    public void openGps(Context context) {

    }

    @Override
    public void closeGps(Context context) {

    }

    @Override
    public boolean gpsIsEnabled() {
        return false;
    }

    @Override
    public void myWakeUp(Context context) {

    }

    @Override
    public void mySleep(Context context) {

    }

    @Override
    public void myReboot(Context context) {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void mySetBrightness(Context context, int value) {
        /*Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                value);*/
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        try {
            Method set = pm.getClass().getMethod("setBacklightBrightness", new Class[]{int.class});
            set.setAccessible(true);
            set.invoke(pm, value);
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    value);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public int myGetBrightness(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, -1);
    }

    @Override
    public void openNFC(Context context) {

    }

    @Override
    public void closeNFC(Context context) {

    }

    @Override
    public boolean nfcIsEnabled() {
        return false;
    }

}
