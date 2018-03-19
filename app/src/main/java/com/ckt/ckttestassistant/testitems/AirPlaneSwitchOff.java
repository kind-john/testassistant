package com.ckt.ckttestassistant.testitems;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.utils.ExcelUtils;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.LabelCell;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by ckt on 18-1-31.
 */

public class AirPlaneSwitchOff extends TestItemBase {
    public static final int ID = 16;
    private static final String TITLE = "AirPlane Switch Off";
    private static final String TAG = "AirPlaneSwitchOff";
    private boolean mState = false;
    private boolean aSyncTaskCompleted = false;

    public AirPlaneSwitchOff() {
        super();
        String className = this.getClass().getName();
        setClassName(className);
        setID(ID);
        setTitle(TITLE);
    }

    public AirPlaneSwitchOff(Context context) {
        super(context);
        String className = this.getClass().getName();
        setClassName(className);
        setID(ID);
        setTitle(TITLE);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public void saveTestData() {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean doExecute(UseCaseManager.ExecuteCallback executeCallback, boolean finish) {
        LogUtils.d(TAG, mClassName+" doExecute");
        boolean passed = false;
        try {
            boolean isOff = Settings.Global.getInt(mContext.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON) == 0 ? true : false;
            if(!isOff){
                Settings.Global.putInt(mContext.getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON,
                        0);
                Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
                intent.putExtra("state", false);
                mContext.sendBroadcast(intent);
                mContext.registerReceiver(new MyReceiver(),
                        new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));
                int count = 0;
                while (!aSyncTaskCompleted){
                    count++;
                    LogUtils.d(TAG, mClassName+" sleep count = "+count);
                    if(count > MyConstants.MAX_TRY){
                        break;
                    }
                    Thread.sleep(1000);
                    LogUtils.d(TAG, mClassName+" sleep end");
                }

                if(mState){
                    LogUtils.d(TAG, mClassName+" passed");
                    passed = true;
                }
            }else{
                passed = true;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            task2(passed);
        }

        if(finish && executeCallback != null){
            LogUtils.d(TAG, "stop test handler");
            //executeCallback.stopTestHandler();
        }
        return false;
    }

    @Override
    public void saveResult() {
        super.saveResult();
    }



    @Override
    public void showPropertyDialog(Context context, final boolean isNeedUpdateXml) {
        LogUtils.d(TAG, "showPropertyDialog :"+this.getClass().getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.settings_wifi_switch_on, null);
        final EditText delayEditText = (EditText)v.findViewById(R.id.delay);
        delayEditText.setText(String.valueOf(getDelay()));
        final EditText timesEditText = (EditText)v.findViewById(R.id.times);
        timesEditText.setText(String.valueOf(getTimes()));

        builder.setTitle("AirPlaneSwitchOff settings")
                .setView(v)
                .setMessage("for test")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtils.d(TAG, "Positive onClick");
                        int delay = Integer.parseInt(delayEditText.getText().toString());
                        int times = Integer.parseInt(timesEditText.getText().toString());
                        LogUtils.d(TAG, "delay = "+delay+"; times = "+times);
                        if(delay >= 0 && times > 0){
                            setDelay(delay);
                            setTimes(times);
                            if(isNeedUpdateXml){
                                mUseCaseManager.updateTestItemOfAllUseCaseXml(AirPlaneSwitchOff.this);
                            }
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtils.d(TAG, "Negative onClick");
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        LogUtils.d(TAG, "onDismiss");
                    }
                }).create().show();
    }

    @Override
    public void saveParameters(Document doc, Element element) {
        Element e1 = doc.createElement(MyConstants.XMLTAG_TESTITEM_DELAY);
        Node n1 = doc.createTextNode(""+mDelay);
        e1.appendChild(n1);
        element.appendChild(e1);
    }

    @Override
    public void saveParametersToXml(XmlSerializer serializer) throws Exception {
        try{
            //eg. start
            serializer.startTag(null, MyConstants.XMLTAG_TESTITEM_DELAY);
            serializer.text(""+mDelay);
            serializer.endTag(null, MyConstants.XMLTAG_TESTITEM_DELAY);
            //eg. end
        }catch (Exception e) {
            throw new Exception();
        }
    }
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())){
                aSyncTaskCompleted = true;
                mState = !intent.getBooleanExtra("state", true);
            }
        }
    }
}
