package com.ckt.ckttestassistant.testitems;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlpull.v1.XmlSerializer;

import jxl.Workbook;

/**
 * Created by ckt on 18-1-31.
 */

public class AutoGenerateContacts extends TestItemBase {
    public static final int ID = 78;
    private static final String TITLE = "Auto Generate Contacts";
    private static final String TAG = "AutoGenerateContacts";
    private volatile boolean passed = false;
    public AutoGenerateContacts() {
        super();
        String className = this.getClass().getName();
        setClassName(className);
        setID(ID);
        setTitle(TITLE);
    }

    public AutoGenerateContacts(Context context) {
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
    private H myHandler;
    private Object lock = new Object();
    private int flag = 0;
    private class WorkThread extends Thread{
        public WorkThread() {
            super("generate contacts");
        }

        @Override
        public void run() {
            LogUtils.d(TAG, mClassName+"run start");
            try {
                //do task
                sleep(3000);
                passed = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean doExecute(UseCaseManager.ExecuteCallback executeCallback, boolean finish) {
        LogUtils.d(TAG, mClassName+" doExecute");
        //do test,then close progressview
        LogUtils.d(TAG,"doExecute current thread :"+Thread.currentThread().getId());
        WorkThread wt = new WorkThread();
        wt.start();
        try {
            wt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        task2(passed);
        if(finish && executeCallback != null){
            LogUtils.d(TAG, "stop test handler");
            executeCallback.stopTestHandler();
        }
        return passed;
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

        builder.setTitle("AutoGenerateContacts settings")
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
                                mUseCaseManager.updateTestItemOfAllUseCaseXml(AutoGenerateContacts.this);
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

    private class H extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                LogUtils.d(TAG,"handleMessage current thread :"+Thread.currentThread().getId());
            }
        }
    }
}
