package com.ckt.ckttestassistant.usecases;


import android.os.Handler;

import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.testitems.TestItemBase;

import org.xmlpull.v1.XmlSerializer;

import java.util.ArrayList;

/**
 * Created by ckt on 18-1-26.
 */

public class CktUseCase extends UseCaseBase {
    public CktUseCase() {
        String className = this.getClass().getName();
        setClassName(className);
    }

    public CktUseCase(ArrayList<TestItemBase> mTestItems) {
        super(mTestItems);
        String className = this.getClass().getName();
        setClassName(className);
    }

    public CktUseCase(int ID) {
        super(ID);
        String className = this.getClass().getName();
        setClassName(className);
    }

    public CktUseCase(ArrayList<TestItemBase> mTestItems, String mTitle) {
        super(mTestItems, mTitle);
        String className = this.getClass().getName();
        setClassName(className);
    }

    public CktUseCase(String mTitle) {
        super(mTitle);
        String className = this.getClass().getName();
        setClassName(className);
    }

    public CktUseCase(ArrayList<TestItemBase> mTestItems, UseCaseBase mNextUseCase) {
        super(mTestItems, mNextUseCase);
        String className = this.getClass().getName();
        setClassName(className);
    }

    public CktUseCase(ArrayList<TestItemBase> mTestItems, int mTimes, String mTitle, boolean mIsChecked) {
        super(mTestItems, mTimes, mTitle, mIsChecked);
        String className = this.getClass().getName();
        setClassName(className);
    }

    @Override
    public boolean execute(Handler mHandler, UseCaseManager.ExecuteCallback mExecuteCallback) {
        super.execute(mHandler, mExecuteCallback);
        return true;
    }

    @Override
    public void saveParametersToXml(XmlSerializer serializer) {

    }

    @Override
    public void addTestItem(TestItemBase testItem) {
        super.addTestItem(testItem);
    }
}
