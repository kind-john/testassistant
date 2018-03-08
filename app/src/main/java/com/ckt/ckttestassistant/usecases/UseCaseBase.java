package com.ckt.ckttestassistant.usecases;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;

import org.xmlpull.v1.XmlSerializer;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by ckt on 18-1-26.
 */

public abstract class UseCaseBase implements Cloneable{
    private static final int DEFAULT_TIMES = 1;
    private static final String TAG = "UseCaseBase";
    protected UseCaseManager mUseCaseManager;
    protected ArrayList<TestItemBase> mTestItems = new ArrayList<TestItemBase>();
    protected UseCaseBase mNextUseCase;
    protected int mTimes = DEFAULT_TIMES;
    protected int mDelay = 0;
    protected int mFailTimes = 0;
    protected int mCompletedTimes = 0;
    protected String mTitle = "case";
    protected  boolean mIsChecked = false;
    protected int ID = -1;
    protected int SN = -1;
    protected String mClassName = "UseCaseBase";
    protected Context mContext;

    public int getDelay() {
        return mDelay;
    }

    public void setDelay(int delay) {
        this.mDelay = delay;
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

    public UseCaseBase() {
        mUseCaseManager = UseCaseManager.getInstance(null);
    }

    public UseCaseBase(Context context) {
        mContext = context;
        mUseCaseManager = UseCaseManager.getInstance(null);
    }

    public void setContext(Context context) {
        this.mContext = context;
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

    @Override
    public UseCaseBase clone() {
        UseCaseBase clone = null;
        try {
            clone = (UseCaseBase) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

    public boolean execute(Handler handler, UseCaseManager.ExecuteCallback executeCallback){
        boolean isPassed = false;
        try{
            if(mTestItems == null || mTestItems.isEmpty()){
                return true;
            }

            for (int index = 0; index < mTestItems.size() - 1; index++){
                mTestItems.get(index).setNextTestItem(mTestItems.get(index + 1));
            }
            mTestItems.get(mTestItems.size() - 1).setNextTestItem(null);

            int needTimes = mTimes - mCompletedTimes;
            boolean usecaseFinish = false;
            LogUtils.d(TAG, "mCompletedTimes = "+mCompletedTimes);
            LogUtils.d(TAG, "mTimes = "+mTimes);
            LogUtils.d(TAG, "needTimes = "+needTimes);
            createExcelSheet();
            if(needTimes > 0){
                for (int times = 0; times < needTimes; times++) {
                    try{
                        updateWaitProgress(handler, times);
                        //writeUsecaseLabelToExcel(mCompletedTimes + 1);
                        LogUtils.d(TAG, "usecaseFinish 1 : "+usecaseFinish);
                        if((mNextUseCase == null) && (times == needTimes - 1)){
                            usecaseFinish = true;
                            LogUtils.d(TAG, "usecaseFinish 2 : "+usecaseFinish);
                        }
                        isPassed = mTestItems.get(0).execute(handler, executeCallback, usecaseFinish);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        mCompletedTimes += 1;
                        if(!isPassed){
                            mFailTimes++;
                        }
                        String path = mContext.getFilesDir()+"/selected_usecases.xml";
                        mUseCaseManager.updateUseCaseOfXml(path, this);
                        initTestItemOfUseCase();
                    }
                }
            }

            if(mNextUseCase != null){
                initUseCases(mNextUseCase);

                mNextUseCase.execute(handler, executeCallback);
            }
        }catch (Exception e){
            e.printStackTrace();
            isPassed = false;
        }
        return true;
    }

    private void initTestItemOfUseCase() {
        for (TestItemBase ti : mTestItems){
            ti.setCompletedTimes(0);
            ti.setFailTimes(0);
        }
    }

    private void initUseCases(UseCaseBase uc) {
        int completedTimes = uc.getCompletedTimes();
        int totalTimes = uc.getTimes();
        if(totalTimes > 0 && (completedTimes == totalTimes)){
            uc.setCompletedTimes(0);
        }
    }

    protected abstract void writeUsecaseLabelToExcel(int times);

    protected abstract void updateWaitProgress(Handler handler, int times);

    protected abstract void createExcelSheet();

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
