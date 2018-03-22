package com.ckt.ckttestassistant.testitems;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ckt.ckttestassistant.CktResultsHelper;
import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.utils.ExcelUtils;
import com.ckt.ckttestassistant.utils.HandlerMessageWhat;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.SystemInvokeImpl;
import com.ckt.ckttestassistant.utils.SystemInvokeInterface;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

public abstract class TestItemBase extends TestBase implements CktResultsHelper.ResultCallBack {
    private static final int DEFAULT_TESTITEM_TIMES = 1;
    private static final String TAG = "TestItemBase";
    protected final SystemInvokeInterface mSystemInvoke;

    //设置下一个测试项，如果结束则必须将其设置为空
    protected TestItemBase mNextTestItem;

    protected int mUseCaseID = -1;
    protected int mUseCaseSN = -1;
    protected volatile boolean mPassed = false;

    private boolean mAllTimesPassed = true;
    private String[] mExcelTitles = {
            "result",
            "total times",
            "completed times",
            "fial times"
    };

    public boolean isPassed() {
        return mPassed;
    }

    public void setPassed(boolean passed) {
        this.mPassed = passed;
    }

    public int getUseCaseID() {
        return mUseCaseID;
    }

    public void setUseCaseID(int useCaseID) {
        this.mUseCaseID = useCaseID;
    }

    public int getUseCaseSN() {
        return mUseCaseSN;
    }

    public void setUseCaseSN(int useCaseSN) {
        this.mUseCaseSN = useCaseSN;
    }

    public TestItemBase getNextTestItem() {
        return mNextTestItem;
    }

    public void setNextTestItem(TestItemBase nextTestItem) {
        this.mNextTestItem = nextTestItem;
    }

    public TestItemBase() {
        mSystemInvoke = SystemInvokeImpl.getInstance();
    }

    public TestItemBase(Context context) {
        this();
        mContext = context;
    }
    public void saveResult(){
        LogUtils.d(TAG, getClass().getSimpleName()+" saveResult");
        UseCaseManager usm = UseCaseManager.getInstance(null, null);
        String file = usm.getCurrentExcelFile();
        try {
            Workbook wb = Workbook.getWorkbook(new File(file));
            WritableWorkbook book = Workbook.createWorkbook(new File(file), wb);
            WritableSheet sheet = book.getSheet(mParent.getTitle());
            if(sheet == null){
                LogUtils.e(TAG, "sheet can not find : "+ mParent.getTitle());
            }else{
                WritableFont font = new WritableFont(WritableFont.createFont("楷体"), 11, WritableFont.BOLD);
                WritableCellFormat format = new WritableCellFormat(font);
                Cell cell = sheet.findCell(getTitle());
                Label label;
                if(cell != null){
                    LogUtils.d(TAG, "found cell of "+ getTitle()+", insert record!");
                    LogUtils.d(TAG, "rows = " + sheet.getRows());
                    //找到合适的地方插入一行记录
                    int row, col;
                    row = cell.getRow();
                    col = cell.getColumn();
                    //在标题后插入一行
                    sheet.insertRow(row + 2);
                    //在添加的新空行写入数据
                    ExcelUtils.addRecordToExcel(sheet, row + 2, col, this);
                }else{
                    LogUtils.d(TAG, "there is no cell of "+ getTitle()+", so create it.");
                    //找到空白地方插入label标记
                    //int emptyRow = ExcelUtils.findEmptyRowFromSheet(sheet, 2, 1);
                    int rows = sheet.getRows();
                    LogUtils.d(TAG, "rows = " + rows);
                    label = new Label(0, rows, getTitle(), format);
                    sheet.addCell(label);
                    ExcelUtils.addRecordTitleToExcel(sheet, rows + 1, 0, mExcelTitles);
                    //sheet.insertRow(emptyRow + 2);
                    ExcelUtils.addRecordToExcel(sheet, rows + 2, 0, this);
                }
                book.write();
                book.close();
                wb.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用此方法前必须先确定mPassed，否则状态不能记录到excel
     */
    public void task2(){
        if("com.ckt.ckttestassistant.testitems.Reboot".equals(mClassName)){
            LogUtils.d(TAG, "完成重启后剩余的工作");
            mCompletedTimes++;
            mFailTimes--;
            mUseCaseManager.updateTestItemOfSelectedUseCaseXml(this);
            saveResult();
        }else{
            if(mPassed){
                LogUtils.d(TAG, "测试成功，失败次数减一");
                mFailTimes--;
                mUseCaseManager.updateTestItemOfSelectedUseCaseXml(this);
            }
            saveResult();
            if(mCompletedTimes < mTimes){
                task();
            }else if(mCompletedTimes == mTimes){
                // TODO
            }else{
                LogUtils.e(TAG, "error: mCompletedTimes > mTimes");
            }
        }
    }

    @Override
    public boolean task() {
        LogUtils.d(TAG, "class name = "+mClassName);
        LogUtils.d(TAG, "mCompletedTimes = "+mCompletedTimes);
        LogUtils.d(TAG, "mTimes = "+mTimes);
        LogUtils.d(TAG, "mFailTimes = "+mFailTimes);
        if(mCompletedTimes < mTimes){
            try{
                int progress = mCompletedTimes;
                if("com.ckt.ckttestassistant.testitems.Reboot".equals(mClassName)){
                    //重启的次数等到开机后再累计
                }else{
                    mCompletedTimes++;
                }
                mFailTimes++;   //先假设本次失败，异步返回成功以后再减1
                mUseCaseManager.updateTestItemOfSelectedUseCaseXml(this);
                updateWaitProgress(mUseCaseManager.getHandler(), progress);
                Thread.sleep(mDelay);
                mPassed = false;
                if (!doExecute(null, false)) {
                    mAllTimesPassed = false;
                }
                List<TestBase> children = getChildren();
                if(children != null && !children.isEmpty()){
                    //这里理论上不会执行，因为目前的设计测试项不包含子测试项
                    for (TestBase child : children){
                        if(!child.task()){
                            mAllTimesPassed = false;
                        }
                    }
                }
            }catch (Exception e){
                mAllTimesPassed = false;
                e.printStackTrace();
            }
        }else{
            //completed
        }
        return mAllTimesPassed;
    }
/*public boolean execute(Handler handler, UseCaseManager.ExecuteCallback executeCallback, boolean usecaseFinish){
        int needTimes = mTimes - mCompletedTimes;
        boolean testItemFinish = false;
        boolean isPassed = false;
        LogUtils.d(TAG, "class name = "+mClassName);
        LogUtils.d(TAG, "mCompletedTimes = "+mCompletedTimes);
        LogUtils.d(TAG, "mTimes = "+mTimes);
        LogUtils.d(TAG, "mFailTimes = "+mFailTimes);
        LogUtils.d(TAG, "needTimes = "+needTimes);
        try{
            if(needTimes > 0){
                for(int times = 0; times < needTimes; times++){
                    LogUtils.d(TAG, "usecaseFinish : "+usecaseFinish);
                    try{
                        updateWaitProgress(handler, times);
                        if(executeCallback != null){
                            //executeCallback.updateProgressMessage(className+" : "+times);
                        }
                        sleep(500);
                        LogUtils.d(TAG, "testItemFinish 1 : "+testItemFinish);
                        if((mNextTestItem == null) && (times == needTimes - 1)){
                            testItemFinish = true;
                            LogUtils.d(TAG, "testItemFinish 2 : "+testItemFinish);
                        }
                        isPassed = doExecute(executeCallback, (usecaseFinish && testItemFinish));
                        saveResult();
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        mCompletedTimes += 1;
                        if(!isPassed){
                            mFailTimes++;
                        }
                        mUseCaseManager.updateTestItemOfSelectedUseCaseXml(this);
                    }
                }
            }else{
                testItemFinish = true;
            }
            closeWaitProgress(handler, (usecaseFinish && testItemFinish));

            if(mNextTestItem != null){
                initTestItems(mNextTestItem);
                mNextTestItem.execute(handler, executeCallback, usecaseFinish);
            }
        }catch (Exception e){
            e.printStackTrace();
            isPassed = false;
        }
        return isPassed;
    }*/

    private void updateWaitProgress(Handler handler, int times) {
        //String className = this.getClass().getSimpleName();
        //LogUtils.d(TAG, "  testItem : " + className + " extends TestItemBase execute times = " + times);

        Message msg = Message.obtain();
        msg.what = HandlerMessageWhat.UPDATE_PROGRESS_MESSAGE;
        Bundle data = new Bundle();
        data.putString(HandlerMessageWhat.PROGRESS_MESSAGE, mTitle + " : "+times);
        msg.setData(data);
        handler.sendMessage(msg);
    }

   /* public void setParameters(Context context){
        showPropertyDialog(context);
    }*/

    public abstract boolean doExecute(UseCaseManager.ExecuteCallback executeCallback, boolean finish);
    public abstract void saveParametersToXml(XmlSerializer serializer) throws Exception;
    public abstract void showPropertyDialog(Context context, final boolean isNeedUpdateXml);
    public abstract void saveParameters(Document doc, Element element);
}
