package com.ckt.ckttestassistant.testitems;

import android.content.Context;

import com.ckt.ckttestassistant.utils.LogUtils;

import org.xmlpull.v1.XmlSerializer;

/**
 * Created by ckt on 18-1-30.
 */

public class CktTestItem extends TestItemBase {
    public static final int ID = 0;
    private static final String TAG = "CktTestItem";

    public CktTestItem(TestItemBase mNextTestItem) {
        super(mNextTestItem);
    }

    public CktTestItem() {
        String className = this.getClass().getName();
        setClassName(className);
    }

    public CktTestItem(String mTitle) {
        super(mTitle);
        String className = this.getClass().getName();
        setClassName(className);
    }

    public CktTestItem(int mTimes, String mTitle, boolean mIsChecked) {
        super(mTimes, mTitle, mIsChecked);
        String className = this.getClass().getName();
        setClassName(className);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public void saveTestData() {

    }

    @Override
    public boolean doExecute() {
        LogUtils.d(TAG, "doExecute");
        return false;
    }

    @Override
    public void saveResult() {

    }

    @Override
    public void showPropertyDialog(Context mContext) {

    }

    @Override
    public void saveParametersToXml(XmlSerializer serializer) throws Exception{
        try{
            //eg. start
            serializer.startTag(null, "delay");
            serializer.text("100");
            serializer.endTag(null, "delay");

            serializer.startTag(null, "total");
            serializer.text("1");
            serializer.endTag(null, "total");
            //eg. end
        }catch (Exception e) {
            throw new Exception();
        }

    }
}
