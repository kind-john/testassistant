package com.ckt.ckttestassistant.testitems;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ckt.ckttestassistant.CktResultsHelper;
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.usecases.UseCaseBase;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import static java.lang.Thread.sleep;

/**
 * Created by ckt on 18-1-26.
 */

public abstract class TestItemBase implements CktResultsHelper.ResultCallBack {
    private static final int DEFAULT_TESTITEM_TIMES = 1;
    private static final String TAG = "TestItemBase";
    protected UseCaseManager mUseCaseManager;

    public UseCaseBase getParentUseCase() {
        return mParentUseCase;
    }

    public void setParentUseCase(UseCaseBase parentUseCase) {
        this.mParentUseCase = parentUseCase;
    }

    protected UseCaseBase mParentUseCase = null;
    protected Context mContext;
    //设置下一个测试项，如果结束则必须将其设置为空
    protected TestItemBase mNextTestItem;
    protected int mTimes = DEFAULT_TESTITEM_TIMES;
    protected int mFailTimes = 0;
    protected int mCompletedTimes = 0;
    protected String mTitle = "testitem";
    protected  boolean mIsChecked = false;
    protected int ID = -1;
    protected int SN = -1;
    protected String mClassName = "TestItemBase";
    protected int mUseCaseID = -1;
    protected int mUseCaseSN = -1;
    protected boolean mPassed = false;

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

    public int getSN() {
        return SN;
    }

    public void setSN(int sn) {
        this.SN = sn;
    }

    public int getCompletedTimes() {
        return mCompletedTimes;
    }

    public void setCompletedTimes(int completedTimes) {
        this.mCompletedTimes = completedTimes;
    }

    public String getClassName() {
        return mClassName;
    }

    public void setClassName(String className) {
        this.mClassName = className;
    }

    public TestItemBase getNextTestItem() {
        return mNextTestItem;
    }

    public void setNextTestItem(TestItemBase nextTestItem) {
        this.mNextTestItem = nextTestItem;
    }

    public int getTimes() {
        return mTimes;
    }

    public void setTimes(int times) {
        this.mTimes = times;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.mIsChecked = isChecked;
    }
    public void setID(int id) {
        this.ID = id;
    }

    public int getID() {
        return ID;
    }

    public int getFailTimes() {
        return mFailTimes;
    }

    public void setFailTimes(int failTimes) {
        this.mFailTimes = failTimes;
    }

    public TestItemBase(TestItemBase mNextTestItem) {
        this.mNextTestItem = mNextTestItem;
    }

    public TestItemBase() {
        mUseCaseManager = UseCaseManager.getInstance(null);
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public TestItemBase(Context context) {
        mContext = context;
        mUseCaseManager = UseCaseManager.getInstance(null);
    }

    public boolean execute(Handler handler, UseCaseManager.ExecuteCallback executeCallback, boolean usecaseFinish){
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
                        String path = mContext.getFilesDir()+"/selected_usecases.xml";
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
    }

    private void initTestItems(TestItemBase ti) {
        int completedTimes = ti.getCompletedTimes();
        int totalTimes = ti.getTimes();
        if(totalTimes > 0 && (completedTimes == totalTimes)){
            ti.setCompletedTimes(0);
            ti.setFailTimes(0);
        }
    }

    private void updateWaitProgress(Handler handler, int times) {
        //String className = this.getClass().getSimpleName();
        //LogUtils.d(TAG, "  testItem : " + className + " extends TestItemBase execute times = " + times);

        Message msg = Message.obtain();
        msg.what = MyConstants.UPDATE_PROGRESS_MESSAGE;
        Bundle data = new Bundle();
        data.putString(MyConstants.PROGRESS_MESSAGE, mTitle + " : "+times);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    private void closeWaitProgress(Handler handler, boolean finish) {
        if(finish){
            Message msg = Message.obtain();
            msg.what = MyConstants.UPDATE_PROGRESS_CLOSE;
            handler.sendMessage(msg);
        }else{
            LogUtils.e(TAG, "error: progress close fail!!!");
        }
    }

   /* public void setParameters(Context context){
        showPropertyDialog(context);
    }*/

    public abstract boolean doExecute(UseCaseManager.ExecuteCallback executeCallback, boolean finish);
    public abstract void saveResult();
    public abstract void saveParametersToXml(XmlSerializer serializer) throws Exception;
    public abstract void showPropertyDialog(Context context, final boolean isNeedUpdateXml);
    public abstract void saveParameters(Document doc, Element element);
}
