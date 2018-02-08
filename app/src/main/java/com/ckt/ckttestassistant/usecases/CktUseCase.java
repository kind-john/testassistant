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
        super();
        String className = this.getClass().getName();
        setClassName(className);
    }

    public CktUseCase(String title) {
        super(title);
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
