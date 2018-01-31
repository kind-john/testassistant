package com.ckt.ckttestassistant.testitems;

import android.content.Context;

import com.ckt.ckttestassistant.CktResultsHelper;
import com.ckt.ckttestassistant.utils.LogUtils;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;

/**
 * Created by ckt on 18-1-26.
 */

public abstract class TestItemBase implements CktResultsHelper.ResultCallBack {
    private static final int DEFAULT_TESTITEM_TIMES = 1;
    private static final String TAG = "TestItemBase";
    //设置下一个测试项，如果结束则必须将其设置为空
    protected TestItemBase mNextTestItem;
    protected int mTimes = DEFAULT_TESTITEM_TIMES;
    protected String mTitle = "testitem";
    protected  boolean mIsChecked = false;
    private int ID = -1;

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

    public TestItemBase(TestItemBase mNextTestItem) {
        this.mNextTestItem = mNextTestItem;
    }

    public TestItemBase() {

    }

    public TestItemBase(String mTitle) {
        this.mTitle = mTitle;
    }

    public TestItemBase(int mTimes, String mTitle, boolean mIsChecked) {
        this.mTimes = mTimes;
        this.mTitle = mTitle;
        this.mIsChecked = mIsChecked;
    }

    public void execute(){
        for(int times = 0; times < mTimes; times++){
            LogUtils.d(TAG, "  testItem : " + this.getClass().getName() + "extends TestItemBase execute times = " + times);
            doExecute();
            saveResult();
        }

        if(mNextTestItem != null){
            mNextTestItem.execute();
        }
    }
    public abstract boolean doExecute();
    public abstract void saveResult();
    public abstract void saveParametersToXml(XmlSerializer serializer) throws Exception;

    public void setID(int id) {
        this.ID = id;
    }

    public int getID() {
        return ID;
    }
}
