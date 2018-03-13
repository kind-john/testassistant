package com.ckt.ckttestassistant.testitems;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ckt.ckttestassistant.CktResultsHelper;
import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.adapter.CktItemDecoration;
import com.ckt.ckttestassistant.usecases.UseCaseBase;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by ckt on 18-1-26.
 */

public abstract class TestItemBase extends TestBase implements CktResultsHelper.ResultCallBack {
    private static final int DEFAULT_TESTITEM_TIMES = 1;
    private static final String TAG = "TestItemBase";
    protected UseCaseManager mUseCaseManager;

    protected Context mContext;
    //设置下一个测试项，如果结束则必须将其设置为空
    protected TestItemBase mNextTestItem;

    protected int ID = -1;
    protected int SN = -1;
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

    public TestItemBase getNextTestItem() {
        return mNextTestItem;
    }

    public void setNextTestItem(TestItemBase nextTestItem) {
        this.mNextTestItem = nextTestItem;
    }

    public void setID(int id) {
        this.ID = id;
    }

    public int getID() {
        return ID;
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

    protected void task2(boolean passed){
        if(passed){
            mFailTimes--;
            String path = mContext.getFilesDir()+"/selected_usecases.xml";
            mUseCaseManager.updateTestItemOfSelectedUseCaseXml(this);
        }
        saveResult();
        if(mCompletedTimes < mTimes){
            task();
        }else if(mCompletedTimes == mTimes){
            if(mFailTimes == 0){
                mPassed = true;
            }
        }else{
            LogUtils.e(TAG, "error: mCompletedTimes > mTimes");
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
                mCompletedTimes++;
                mFailTimes++;   //先假设本次失败，异步返回成功以后再减1
                mUseCaseManager.updateTestItemOfSelectedUseCaseXml(this);
                updateWaitProgress(mUseCaseManager.getHandler(), mCompletedTimes - 1);
                sleep(mDelay);
                mPassed = doExecute(null, false);
                List<TestBase> children = getChildren();
                if(children != null && !children.isEmpty()){
                    //这里理论上不会执行，因为目前的设计测试项不包含子测试项
                    for (TestBase child : children){
                        if(!child.task()){
                            mPassed = false;
                        }
                    }
                }
            }catch (Exception e){
                mPassed = false;
                e.printStackTrace();
            }
        }else{
            //completed
        }
        if(mCompletedTimes == mTimes){
            initTestItems();
        }
        return mPassed;
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

    private void initTestItems() {
        if(mTimes > 0 && (mCompletedTimes == mTimes)){
            mCompletedTimes = 0;
            mFailTimes = 0;
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

   /* public void setParameters(Context context){
        showPropertyDialog(context);
    }*/

    public abstract boolean doExecute(UseCaseManager.ExecuteCallback executeCallback, boolean finish);
    public abstract void saveResult();
    public abstract void saveParametersToXml(XmlSerializer serializer) throws Exception;
    public abstract void showPropertyDialog(Context context, final boolean isNeedUpdateXml);
    public abstract void saveParameters(Document doc, Element element);
}
