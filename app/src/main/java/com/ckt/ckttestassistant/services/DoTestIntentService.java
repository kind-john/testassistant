package com.ckt.ckttestassistant.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.usecases.UseCaseBase;
import com.ckt.ckttestassistant.utils.CktXmlHelper2;
import com.ckt.ckttestassistant.utils.LogUtils;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class DoTestIntentService extends IntentService {
    private static final String TAG = "DoTestIntentService";
    public static final String COMMAND = "command";
    private ArrayList<UseCaseBase> mSelectedUseCases;
    private Handler mHandler;
    private CktXmlHelper2 mXmlHelper;
    private Context mContext;
    private ArrayList<UseCaseBase> mAllUseCases;
    private HandleCallback mHandleCallback;
    public interface HandleCallback{
        void loadDataFromXml();
        void startExecuteThread();
    }
    public DoTestIntentService() {
        super("DoTestIntentService");
        setHandleCallback(UseCaseManager.getInstance(null));
    }

    private void setHandleCallback(HandleCallback handleCallback){
        mHandleCallback = handleCallback;
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtils.d(TAG, "onHandleIntent");
        if (intent != null) {
            String cmd = intent.getStringExtra(COMMAND);
            if (cmd != null){
                if(cmd.equals("init")) {
                    LogUtils.d(TAG, "onHandleIntent do init");
                    if(mHandleCallback != null){
                        mHandleCallback.loadDataFromXml();
                    }else{
                        throw new RuntimeException("error:There is no define init handler !");
                    }
                }else if(cmd.equals("start")){
                    LogUtils.d(TAG, "onHandleIntent do start");
                    if(mHandleCallback != null){
                        mHandleCallback.startExecuteThread();
                    }else{
                        throw new RuntimeException("error:There is no define start handler !");
                    }
                }

            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(TAG, "onCreate");
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        LogUtils.d(TAG, "onStart");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        LogUtils.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d(TAG, "onBind");
        return super.onBind(intent);
    }
}
