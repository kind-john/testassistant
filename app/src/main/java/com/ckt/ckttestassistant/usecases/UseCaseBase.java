package com.ckt.ckttestassistant.usecases;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;

import org.xmlpull.v1.XmlSerializer;

import java.util.ArrayList;

/**
 * Created by ckt on 18-1-26.
 */

public abstract class UseCaseBase {
    private static final int DEFAULT_TIMES = 3;
    private static final String TAG = "UseCaseBase";
    private UseCaseManager mUseCaseManager;
    protected ArrayList<TestItemBase> mTestItems = new ArrayList<TestItemBase>();
    protected UseCaseBase mNextUseCase;
    protected int mTimes = DEFAULT_TIMES;
    protected int mFailTimes = 0;
    protected int mCompletedTimes = 0;
    protected String mTitle = "case";
    protected  boolean mIsChecked = false;
    protected int ID = -1;
    protected String mClassName = "UseCaseBase";

    public int getCompletedTimes() {
        return mCompletedTimes;
    }

    public void setCompletedTimes(int completedTimes) {
        this.mCompletedTimes = completedTimes;
    }

    public UseCaseBase() {
        mUseCaseManager = UseCaseManager.getInstance(null);
    }

    public UseCaseBase(String title) {
        this.mTitle = mTitle;
        mUseCaseManager = UseCaseManager.getInstance(null);
    }

    public int getFailTimes() {
        return mFailTimes;
    }

    public void setFailTimes(int failTimes) {
        this.mFailTimes = failTimes;
    }

    public void setNextUseCase(UseCaseBase nextUseCase){
        this.mNextUseCase = nextUseCase;
    }

    public String getClassName() {
        return mClassName;
    }

    public void setClassName(String className) {
        this.mClassName = className;
    }

    public boolean execute(Handler handler, UseCaseManager.ExecuteCallback executeCallback){
        if(mTestItems == null || mTestItems.isEmpty()){
            return true;
        }

        for (int index = 0; index < mTestItems.size() - 1; index++){
            mTestItems.get(index).setNextTestItem(mTestItems.get(index + 1));
        }
        mTestItems.get(mTestItems.size() - 1).setNextTestItem(null);

        for (int times = 0; times < mTimes; times++) {
            boolean usecaseFinish = false;
            String className = this.getClass().getSimpleName();
            LogUtils.d(TAG, "UseCase : " + className + " extends UseCaseBase execute times = " + times);
            Message msg = Message.obtain();
            msg.what = MyConstants.UPDATE_PROGRESS_TITLE;
            Bundle data = new Bundle();
            data.putString(MyConstants.PROGRESS_TITLE, className+" : "+times);
            msg.setData(data);
            handler.sendMessage(msg);
            if(executeCallback != null){
                //executeCallback.updateProgressTitle(className+" : "+times);
            }
            if((mNextUseCase == null) && (times == mTimes - 1)){
                usecaseFinish = true;
            }
            mTestItems.get(0).execute(handler, executeCallback, usecaseFinish);
            mCompletedTimes += 1;

        }
        if(mNextUseCase != null){
            mNextUseCase.execute(handler, executeCallback);
        }
        return true;
    }

    public void addTestItem(TestItemBase testItem){
        mTestItems.add(testItem);
    }

    public void deleteLatestTestItem(){
        if(mTestItems != null && !mTestItems.isEmpty()){
            mTestItems.remove(mTestItems.size() - 1);
        }
    }

    public void saveParametersToXml(XmlSerializer serializer) throws Exception{
        try {
            serializer.startTag(null, "usecase");
            serializer.attribute(null, "id", "1");
            if(mTestItems != null){
                for (TestItemBase item : mTestItems){
                    item.saveParametersToXml(serializer);
                }
            }
            serializer.endTag(null, "usecase");
        } catch (Exception e) {
            throw new Exception();
        }
    }

    public void deleteAllTestItems(){
        if(mTestItems != null && !mTestItems.isEmpty()){
            for (int index = 0; index < mTestItems.size(); index++){
                mTestItems.get(index).setNextTestItem(null);
            }
            mTestItems.clear();
        }
    }

    public void setTestItems(ArrayList<TestItemBase> testItems) {
        this.mTestItems = testItems;
    }

    public ArrayList<TestItemBase> getTestItems() {
        return mTestItems;
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
}
