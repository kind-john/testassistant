package com.ckt.ckttestassistant.usecases;


import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.utils.ExcelUtils;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jxl.Cell;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

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
        msg.what = MyConstants.UPDATE_PROGRESS_TITLE;
        Bundle data = new Bundle();
        data.putString(MyConstants.PROGRESS_TITLE, mTitle +" : "+times);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    private boolean isNeedCreateNewFile() {
        boolean result = true;
        boolean status = mUseCaseManager.getTestStatus();
        if(status){
            result = false;
        }else{
            result = mUseCaseManager.isTestCompleted();
        }
        //return result;   //后续要优化，暂时返回true测试excel读写
        return true;
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
}
