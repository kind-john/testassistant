package com.ckt.ckttestassistant.testitems;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ckt.ckttestassistant.CktResultsHelper;
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;

import org.xmlpull.v1.XmlSerializer;

import static java.lang.Thread.sleep;

/**
 * Created by ckt on 18-1-26.
 */

public abstract class TestItemBase implements CktResultsHelper.ResultCallBack {
    private static final int DEFAULT_TESTITEM_TIMES = 3;
    private static final String TAG = "TestItemBase";
    private UseCaseManager mUseCaseManager;
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

    public void execute(Handler handler, UseCaseManager.ExecuteCallback executeCallback, boolean usecaseFinish){
        for(int times = 0; times < mTimes; times++){
            String className = this.getClass().getSimpleName();
            boolean testItemFinish = false;
            LogUtils.d(TAG, "  testItem : " + className + " extends TestItemBase execute times = " + times);
            LogUtils.d(TAG, "usecaseFinish : "+usecaseFinish);
            try{
                Message msg = Message.obtain();
                msg.what = MyConstants.UPDATE_PROGRESS_MESSAGE;
                Bundle data = new Bundle();
                data.putString(MyConstants.PROGRESS_MESSAGE, className+" : "+times);
                msg.setData(data);
                handler.sendMessage(msg);
                if(executeCallback != null){
                    //executeCallback.updateProgressMessage(className+" : "+times);
                }
                sleep(2000);
            }catch (Exception e){
                e.printStackTrace();
            }
            if((mNextTestItem == null) && (times == mTimes - 1)){
                testItemFinish = true;
            }
            LogUtils.d(TAG, "testItemFinish : "+testItemFinish);
            doExecute(executeCallback, (usecaseFinish && testItemFinish));
            mCompletedTimes += 1;
            saveResult();
        }

        if(mNextTestItem != null){
            mNextTestItem.execute(handler, executeCallback, usecaseFinish);
        }
    }
    public abstract boolean doExecute(UseCaseManager.ExecuteCallback executeCallback, boolean finish);
    public abstract void saveResult();
    public abstract void saveParametersToXml(XmlSerializer serializer) throws Exception;
    public abstract void showPropertyDialog(Context context);
}
