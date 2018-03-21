package com.ckt.ckttestassistant.testitems;

import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.PointConstants;
import com.ckt.ckttestassistant.utils.XmlTagConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlpull.v1.XmlSerializer;

import java.util.HashMap;

/**
 * Created by ckt on 18-1-31.
 */

public class CameraEnterVideoMode extends TestItemBase {
    public static final int ID = 86;
    private static final String TITLE = "Camera Enter Video Mode";
    private static final String TAG = "CameraEnterVideoMode";

    public CameraEnterVideoMode() {
        super();
        String className = this.getClass().getName();
        setClassName(className);
        setID(ID);
        setTitle(TITLE);
    }

    public CameraEnterVideoMode(Context context) {
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
        LogUtils.d(TAG, "CameraEnterVideoMode doExecute");
        new Thread() {
            public void run() {
                try{
                    Point point = mUseCaseManager.getTouchPosConfig(PointConstants.CAMERA_VIDEOMODE_POINT);
                    if(point != null){
                        Instrumentation inst=new Instrumentation();
                        inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, point.x, point.y, 0));
                        inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                                SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, point.x, point.y, 0));
                        mPassed = true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
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

        builder.setTitle("CameraEnterVideoMode settings")
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
                                mUseCaseManager.updateTestItemOfAllUseCaseXml(CameraEnterVideoMode.this);
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
