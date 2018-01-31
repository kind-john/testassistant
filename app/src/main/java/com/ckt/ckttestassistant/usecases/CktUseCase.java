package com.ckt.ckttestassistant.usecases;


import com.ckt.ckttestassistant.testitems.TestItemBase;

import org.xmlpull.v1.XmlSerializer;

import java.util.ArrayList;

/**
 * Created by ckt on 18-1-26.
 */

public class CktUseCase extends UseCaseBase {
    public CktUseCase(ArrayList<TestItemBase> mTestItems) {
        super(mTestItems);
    }

    public CktUseCase(int ID) {
        super(ID);
    }

    public CktUseCase(ArrayList<TestItemBase> mTestItems, String mTitle) {
        super(mTestItems, mTitle);
    }

    public CktUseCase(String mTitle) {
        super(mTitle);
    }

    public CktUseCase(ArrayList<TestItemBase> mTestItems, UseCaseBase mNextUseCase) {
        super(mTestItems, mNextUseCase);
    }

    public CktUseCase(ArrayList<TestItemBase> mTestItems, int mTimes, String mTitle, boolean mIsChecked) {
        super(mTestItems, mTimes, mTitle, mIsChecked);
    }

    @Override
    public boolean execute() {
        super.execute();
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
