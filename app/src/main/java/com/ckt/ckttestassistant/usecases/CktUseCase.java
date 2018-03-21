package com.ckt.ckttestassistant.usecases;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

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

    public CktUseCase(Context context) {
        super(context);
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
    public void saveParametersToXml(XmlSerializer serializer) {

    }

    @Override
    public void setChildrenSN() {
        if(children != null && !children.isEmpty()){
            for (int i = 0; i < children.size(); i++){
                children.get(i).setSN(i);
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
