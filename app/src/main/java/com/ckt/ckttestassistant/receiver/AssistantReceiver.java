package com.ckt.ckttestassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.ckt.ckttestassistant.utils.LogUtils;

import java.util.List;

public class AssistantReceiver extends BroadcastReceiver {
    private static final String ACTION_START_CKT_REBOOT = "com.ckt.action.reboot";
    private static final String TAG = "AssistantReceiver";
    public static Intent findApp(Context context, Intent intent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(intent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() < 1) {
            //return null;
            return intent;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        LogUtils.d(TAG, "findApp packageName = "+packageName);
        LogUtils.d(TAG, "findApp className = "+className);
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent foundIntent = new Intent(intent);

        // Set the component to be explicit
        foundIntent.setComponent(component);

        return foundIntent;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.d(TAG, "onReceive");
        if(action.equals("android.intent.action.BOOT_COMPLETED")){
            LogUtils.d(TAG, "boot completed!");
            Intent it = new Intent(ACTION_START_CKT_REBOOT);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent findIntent = new Intent(findApp(context,it));
            context.startActivity(findIntent);
        }
    }
}
