package com.ckt.ckttestassistant.testitems;

import android.content.Context;

import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlpull.v1.XmlSerializer;

/**
 * Created by ckt on 18-1-30.
 */

public class CktTestItem extends TestItemBase {
    public static final int ID = 0;
    private static final String TAG = "CktTestItem";
    private static final String TITLE = "Ckt TestItem";

    public int getDelay() {
        return mDelay;
    }

    public void setDelay(int delay) {
        this.mDelay = delay;
    }

    private int mDelay = 0;

    public CktTestItem() {
        super();
        String className = this.getClass().getName();
        setClassName(className);
        setID(ID);
        setTitle(TITLE);
    }

    public CktTestItem(TestItemBase mNextTestItem) {
        super(mNextTestItem);
    }

    public CktTestItem(Context context) {
        super(context);
        String className = this.getClass().getName();
        setClassName(className);
        setID(ID);
        setTitle(TITLE);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public void saveTestData() {

    }

    @Override
    public boolean doExecute(UseCaseManager.ExecuteCallback executeCallback, boolean finish) {
        LogUtils.d(TAG, "CktTestItem doExecute");
        //do test,then close progressview
        if(finish && executeCallback != null){
            LogUtils.d(TAG, "stop test handler");
            executeCallback.stopTestHandler();
        }
        return false;
    }

    @Override
    public void saveResult() {

    }

    @Override
    public void showPropertyDialog(Context mContext, final boolean isNeedUpdateXml) {

    }

    @Override
    public void saveParametersToXml(XmlSerializer serializer) throws Exception{
        try{
            //eg. start
            serializer.startTag(null, "delay");
            serializer.text(""+mDelay);
            serializer.endTag(null, "delay");

            serializer.startTag(null, "total");
            serializer.text("1");
            serializer.endTag(null, "total");
            //eg. end
        }catch (Exception e) {
            throw new Exception();
        }
    }

    @Override
    public void saveParameters(Document doc, Element element) {
        Element e1 = doc.createElement(MyConstants.XMLTAG_TESTITEM_DELAY);
        Node n1 = doc.createTextNode(""+mDelay);
        e1.appendChild(n1);
        element.appendChild(e1);

        Element e2 = doc.createElement("total");
        Node n2 = doc.createTextNode("1");
        e2.appendChild(n2);
        element.appendChild(e2);
    }
}
