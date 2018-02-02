package com.ckt.ckttestassistant.testitems;

import com.ckt.ckttestassistant.utils.LogUtils;

import org.xmlpull.v1.XmlSerializer;

/**
 * Created by ckt on 18-1-31.
 */

public class WifiSwitchOn extends TestItemBase {
    public static final int ID = 1;
    private static final String TAG = "WifiSwitchOn";

    public WifiSwitchOn(String mTitle) {
        super(mTitle);
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
    public void saveParametersToXml(XmlSerializer serializer) throws Exception {
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
