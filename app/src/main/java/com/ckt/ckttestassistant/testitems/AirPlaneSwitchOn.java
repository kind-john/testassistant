package com.ckt.ckttestassistant.testitems;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Settings;
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

import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

/**
 * Created by ckt on 18-1-31.
 */

public class AirPlaneSwitchOn extends TestItemBase {
    public static final int ID = 15;
    private static final String TITLE = "AirPlane Switch On";
    private static final String TAG = "AirPlaneSwitchOn";
    private static final String EXCEL_TITLE_RESULT = "result";
    private static final String EXCEL_TITLE_TOTAL_TIMES = "total times";
    private static final String EXCEL_TITLE_COMPLETED_TIMES = "completed times";
    private static final String EXCEL_TITLE_FAIL_TIMES = "fial times";
    private boolean aSyncTaskCompleted = false;

    public AirPlaneSwitchOn() {
        super();
        String className = this.getClass().getName();
        setClassName(className);
        setID(ID);
        setTitle(TITLE);
    }

    public AirPlaneSwitchOn(Context context) {
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
        LogUtils.d(TAG, mClassName + " doExecute");
        try {
            if (!mSystemInvoke.APMModeIsEnabled(mContext)) {
                mSystemInvoke.openAPMMode(mContext);

                mContext.registerReceiver(new MyReceiver(),
                        new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));
                int count = 0;
                while (!aSyncTaskCompleted) {
                    aSyncTaskCompleted = false;
                    count++;
                    LogUtils.d(TAG, mClassName + " sleep count = " + count);
                    if (count > MyConstants.MAX_TRY) {
                        break;
                    }
                    Thread.sleep(1000);
                    LogUtils.d(TAG, mClassName + " sleep end");
                }
            } else {
                mPassed = true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            task2();
        }

        if (finish && executeCallback != null) {
            LogUtils.d(TAG, "stop test handler");
            executeCallback.stopTestHandler();
        }
        return mPassed;
    }

    private void addRecordTitleToExcel(WritableSheet sheet, int row, int col) throws WriteException {
        String result;
        WritableFont font = new WritableFont(WritableFont.createFont("楷体"), 11, WritableFont.BOLD);
        WritableCellFormat format = new WritableCellFormat(font);

        Label label1 = new Label(col, row, EXCEL_TITLE_RESULT);
        sheet.addCell(label1);
        Label label2 = new Label(col + 1, row, EXCEL_TITLE_TOTAL_TIMES);
        sheet.addCell(label2);
        Label label3 = new Label(col + 2, row, EXCEL_TITLE_COMPLETED_TIMES);
        sheet.addCell(label3);
        Label label4 = new Label(col + 3, row, EXCEL_TITLE_FAIL_TIMES);
        sheet.addCell(label4);
    }

    private void addRecordToExcel(WritableSheet sheet, int row, int col) throws WriteException {
        String result;
        WritableCellFormat labelFormat = new WritableCellFormat();
        WritableCellFormat failFormat = new WritableCellFormat();
        if (isSuccess()) {
            result = MyConstants.SUCCESS;
            labelFormat.setBackground(Colour.GREEN);
        } else {
            result = MyConstants.FAIL;
            labelFormat.setBackground(Colour.RED);
        }
        Label label = new Label(col, row, result);
        sheet.addCell(label);
        Number totalTimes = new Number(col + 1, row, mTimes);
        sheet.addCell(totalTimes);
        Number completedTimes = new Number(col + 2, row, mCompletedTimes);
        sheet.addCell(completedTimes);
        Number failTimes = new Number(col + 3, row, mFailTimes);
        if (mFailTimes > 0) {
            failFormat.setBackground(Colour.RED);
        }
        sheet.addCell(failTimes);
    }

    @Override
    public void saveResult() {
        super.saveResult();
    }

    @Override
    public void showPropertyDialog(Context context, final boolean isNeedUpdateXml) {
        LogUtils.d(TAG, "showPropertyDialog :" + this.getClass().getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.settings_wifi_switch_on, null);
        final EditText delayEditText = (EditText) v.findViewById(R.id.delay);
        delayEditText.setText(String.valueOf(getDelay()));
        final EditText timesEditText = (EditText) v.findViewById(R.id.times);
        timesEditText.setText(String.valueOf(getTimes()));

        builder.setTitle("AirPlaneSwitchOn settings")
                .setView(v)
                .setMessage(R.string.set_propeties)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtils.d(TAG, "Positive onClick");
                        int delay = Integer.parseInt(delayEditText.getText().toString());
                        int times = Integer.parseInt(timesEditText.getText().toString());
                        LogUtils.d(TAG, "delay = " + delay + "; times = " + times);
                        if (delay >= 0 && times > 0) {
                            setDelay(delay);
                            setTimes(times);
                            if (isNeedUpdateXml) {
                                mUseCaseManager.updateTestItemOfAllUseCaseXml(AirPlaneSwitchOn.this);
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
        Node n1 = doc.createTextNode("" + mDelay);
        e1.appendChild(n1);
        element.appendChild(e1);
    }

    @Override
    public void saveParametersToXml(XmlSerializer serializer) throws Exception {
        try {
            //eg. start
            serializer.startTag(null, XmlTagConstants.XMLTAG_TESTITEM_DELAY);
            serializer.text("" + mDelay);
            serializer.endTag(null, XmlTagConstants.XMLTAG_TESTITEM_DELAY);
            //eg. end
        } catch (Exception e) {
            throw new Exception();
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
                aSyncTaskCompleted = true;
                mPassed = intent.getBooleanExtra("state", false);
            }
        }
    }
}
