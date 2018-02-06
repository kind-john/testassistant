package com.ckt.ckttestassistant.usecases;

import android.content.Context;
import android.widget.Toast;

import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.testitems.TestItemBase;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ckt on 18-1-26.
 */

public abstract class UseCaseBase {
    private static final int DEFAULT_TIMES = 1;
    private static final String TAG = "UseCaseBase";
    protected ArrayList<TestItemBase> mTestItems = new ArrayList<TestItemBase>();
    protected UseCaseBase mNextUseCase;
    protected int mTimes = DEFAULT_TIMES;
    protected int mFailTimes = 0;
    protected String mTitle = "case";
    protected  boolean mIsChecked = false;
    protected int ID = -1;
    protected String mClassName = "UseCaseBase";

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

    public UseCaseBase() {
    }

    public UseCaseBase(int ID) {
        this.ID = ID;
    }

    public UseCaseBase(ArrayList<TestItemBase> mTestItems) {
        this.mTestItems = mTestItems;
    }

    public UseCaseBase(String mTitle) {
        this.mTitle = mTitle;
    }

    public UseCaseBase(ArrayList<TestItemBase> mTestItems, String mTitle) {
        this.mTestItems = mTestItems;
        this.mTitle = mTitle;
    }

    public UseCaseBase(ArrayList<TestItemBase> mTestItems, UseCaseBase mNextUseCase) {
        this.mTestItems = mTestItems;
        this.mNextUseCase = mNextUseCase;
    }

    public UseCaseBase(ArrayList<TestItemBase> mTestItems, int mTimes, String mTitle, boolean mIsChecked) {
        this.mTestItems = mTestItems;
        this.mTimes = mTimes;
        this.mTitle = mTitle;
        this.mIsChecked = mIsChecked;
    }

    public boolean execute(){
        if(mTestItems == null || mTestItems.isEmpty()){
            return true;
        }

        for (int index = 0; index < mTestItems.size() - 1; index++){
            mTestItems.get(index).setNextTestItem(mTestItems.get(index + 1));
        }
        mTestItems.get(mTestItems.size() - 1).setNextTestItem(null);

        for (int times = 0; times < mTimes; times++) {
            LogUtils.d(TAG, "UseCase : " + this.getClass().getName() + "extends UseCaseBase execute times = " + times);
            mTestItems.get(0).execute();
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
