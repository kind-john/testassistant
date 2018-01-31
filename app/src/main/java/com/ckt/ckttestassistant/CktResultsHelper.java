package com.ckt.ckttestassistant;

/**
 * Created by ckt on 18-1-26.
 */

public class CktResultsHelper {
    public interface ResultCallBack{
        public boolean isSuccess();
        public void saveTestData();
    }

    private ResultCallBack mCallBack;
    public void setCallBack(ResultCallBack callback){
        mCallBack = callback;
    }
    public void showResults(){
        //only show success or fail
    }

    public void saveResults(){
        mCallBack.saveTestData();
    }
}
