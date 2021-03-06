package com.ckt.ckttestassistant.usecases;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.HandlerMessageWhat;
import com.ckt.ckttestassistant.utils.XmlTagConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by ckt on 18-1-26.
 */

public class CktUseCase extends UseCaseBase {
    private static final String TAG = "CktUseCase";

    public CktUseCase() {
        String className = this.getClass().getName();
        setClassName(className);
    }

    @Override
    public void showPropertyDialog(Context context, boolean isNeedUpdateXml) {
        LogUtils.d(TAG, mClassName+" showPropertySetting");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(mContext).inflate(R.layout.settings_usecase, null);
        final EditText delayEditText = (EditText)v.findViewById(R.id.delay);
        delayEditText.setText(String.valueOf(mDelay));
        final EditText timesEditText = (EditText)v.findViewById(R.id.times);
        timesEditText.setText(String.valueOf(mTimes));

        builder.setTitle(mTitle)
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
                            mDelay = delay;
                            mTimes = times;
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

    public CktUseCase(Context context, Activity activity) {
        super(context, activity);
        String className = this.getClass().getName();
        setClassName(className);
    }

    /*@Override
    public boolean execute(Handler mHandler, UseCaseManager.ExecuteCallback mExecuteCallback) {
        super.execute(mHandler, mExecuteCallback);
        return true;
    }*/

    @Override
    protected void updateWaitProgress(Handler handler, int times) {
        //String className = this.getClass().getSimpleName();
        //LogUtils.d(TAG, "UseCase : " + className + " extends UseCaseBase execute times = " + times);
        Message msg = Message.obtain();
        msg.what = HandlerMessageWhat.UPDATE_PROGRESS_TITLE;
        Bundle data = new Bundle();
        data.putString(HandlerMessageWhat.PROGRESS_TITLE, mTitle +" : "+times);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    @Override
    protected void createExcelSheet() {
        LogUtils.d(TAG, "createExcelSheet");
        String file = mUseCaseManager.getCurrentExcelFile();
        try {
            Workbook wb = Workbook.getWorkbook(new File(file));
            WritableWorkbook book = Workbook.createWorkbook(new File(file), wb);
            WritableSheet sheet;
            sheet = book.getSheet(mTitle);
            if(sheet == null){
                book.createSheet(mTitle, SN);
            }
            book.write();
            book.close();
            wb.close();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setChildrenSN() {
        if(children != null && !children.isEmpty()){
            for (int i = 0; i < children.size(); i++){
                TestBase child = children.get(i);
                child.setSN(i);
                if (child instanceof UseCaseBase) {
                    ((UseCaseBase)child).setChildrenSN();
                }
            }
        }
    }

    @Override
    public void saveParameters(Document doc, Element root) {
        Element e1 = doc.createElement(XmlTagConstants.XMLTAG_USECASE_DELAY);
        Node n1 = doc.createTextNode(""+mDelay);
        e1.appendChild(n1);
        root.appendChild(e1);
    }
}
