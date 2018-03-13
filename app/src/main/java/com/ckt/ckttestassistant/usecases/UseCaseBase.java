package com.ckt.ckttestassistant.usecases;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by ckt on 18-1-26.
 */

public abstract class UseCaseBase extends TestBase{

    private static final String TAG = "UseCaseBase";
    protected UseCaseManager mUseCaseManager;
    protected UseCaseBase mNextUseCase;

    protected Context mContext;
    private boolean mPassed = false;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        super.setTitle(title);
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

    public void setNextUseCase(UseCaseBase nextUseCase){
        this.mNextUseCase = nextUseCase;
    }

    @Override
    public boolean task() {
        boolean isPassed = true;
        try{
            if(children == null || children.isEmpty()){
                return true;
            }
            createExcelSheet();
            int needTimes = mTimes - mCompletedTimes;
            LogUtils.d(TAG, "mCompletedTimes = "+mCompletedTimes);
            LogUtils.d(TAG, "mTimes = "+mTimes);
            LogUtils.d(TAG, "needTimes = "+needTimes);
            if(needTimes > 0){
                for (int times = 0; times < needTimes; times++) {
                    try{
                        Thread.sleep(mDelay);
                        mCompletedTimes++;
                        mFailTimes++;
                        String path = mContext.getFilesDir()+"/selected_usecases.xml";
                        mUseCaseManager.updateUseCaseOfXml(path, this);
                        updateWaitProgress(mUseCaseManager.getHandler(), mCompletedTimes - 1);
                        for (TestBase tb : children){
                            if(!tb.task()){
                                isPassed = false;
                            }
                        }
                    }catch (Exception e){
                        isPassed = false;
                        e.printStackTrace();
                    }finally {
                        if(isPassed){
                            mFailTimes--;
                            String path = mContext.getFilesDir()+"/selected_usecases.xml";
                            mUseCaseManager.updateUseCaseOfXml(path, this);
                        }
                    }
                }
            }else{
                //is completed
            }
            if(mCompletedTimes == mTimes){
                initTestItemOfUseCase();
            }
        }catch (Exception e){
            isPassed = false;
            e.printStackTrace();
        }
        return isPassed;
    }
    /*public boolean execute(Handler handler, UseCaseManager.ExecuteCallback executeCallback){
        boolean isPassed = false;
        try{
            if(children == null || children.isEmpty()){
                return true;
            }

            *//*for (int index = 0; index < children.size() - 1; index++){
                children.get(index).setNextTestItem(children.get(index + 1));
            }
            children.get(children.size() - 1).setNextTestItem(null);*//*

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
                        isPassed = (children.get(0)).execute(handler, executeCallback, usecaseFinish);
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
    }*/

    private void initTestItemOfUseCase() {
        for (TestBase ti : children){
            if(ti instanceof UseCaseBase){
                ((UseCaseBase)ti).initTestItemOfUseCase();
            }else if(ti instanceof TestItemBase){
                ((TestItemBase)ti).setCompletedTimes(0);
                ((TestItemBase)ti).setFailTimes(0);
            }
        }
    }

    private void initUseCases(UseCaseBase uc) {
        int completedTimes = uc.getCompletedTimes();
        int totalTimes = uc.getTimes();
        if(totalTimes > 0 && (completedTimes == totalTimes)){
            uc.setCompletedTimes(0);
        }
    }

    protected abstract void updateWaitProgress(Handler handler, int times);

    protected abstract void createExcelSheet();

    public void addTestItem(TestBase testItem){
        children.add(testItem);
    }

    public void saveParametersToXml(XmlSerializer serializer) throws Exception{
        try {
            serializer.startTag(null, "usecase");
            serializer.attribute(null, "id", "1");
            if(children != null){
                for (TestBase item : children){
                    if(item instanceof UseCaseBase){
                        saveParametersToXml(serializer);
                    }else if(item instanceof TestItemBase){
                        ((TestItemBase)item).saveParametersToXml(serializer);
                    }
                }
            }
            serializer.endTag(null, "usecase");
        } catch (Exception e) {
            throw new Exception();
        }
    }

    public abstract void setChildrenSN();

    public abstract void saveParameters(Document doc, Element root);
}
