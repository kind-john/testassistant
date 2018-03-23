package com.ckt.ckttestassistant.testitems;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;
import com.ckt.ckttestassistant.utils.XmlTagConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlpull.v1.XmlSerializer;

/**
 * Created by ckt on 18-1-31.
 */

public class WifiSwitchOn extends TestItemBase {
    public static final int ID = 6;
    private static final String TITLE = "Wifi Switch On";
    private static final String TAG = "WifiSwitchOn";

    public WifiSwitchOn() {
        super();
        String className = this.getClass().getName();
        setClassName(className);
        setID(ID);
        setTitle(TITLE);
    }

    public WifiSwitchOn(Context context) {
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

    @Override
    public boolean doExecute(UseCaseManager.ExecuteCallback executeCallback, boolean finish) {
        LogUtils.d(TAG, mClassName+" doExecute");
        mSystemInvoke.openWifi(mContext);
        int count = 0;
        while (!mSystemInvoke.wifiIsEnabled()){
            try {
                LogUtils.d(TAG, "sleep count : " + count);
                count++;
                if(count <= MyConstants.MAX_TRY){
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(mSystemInvoke.wifiIsEnabled()){
            mPassed = true;
        }
        task2();
        if(finish && executeCallback != null){
            LogUtils.d(TAG, "stop test handler");
            executeCallback.stopTestHandler();
        }
        return mPassed;
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

        builder.setTitle("Wifi settings")
                .setView(v)
                .setMessage(R.string.set_propeties)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
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
                                mUseCaseManager.updateTestItemOfAllUseCaseXml(WifiSwitchOn.this);
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
        Element e1 = doc.createElement(XmlTagConstants.XMLTAG_TESTITEM_DELAY);
        Node n1 = doc.createTextNode(""+mDelay);
        e1.appendChild(n1);
        element.appendChild(e1);
    }

    @Override
    public void saveParametersToXml(XmlSerializer serializer) throws Exception {
        try{
            //eg. start
            serializer.startTag(null, XmlTagConstants.XMLTAG_TESTITEM_DELAY);
            serializer.text(""+mDelay);
            serializer.endTag(null, XmlTagConstants.XMLTAG_TESTITEM_DELAY);
            //eg. end
        }catch (Exception e) {
            throw new Exception();
        }
    }
}
