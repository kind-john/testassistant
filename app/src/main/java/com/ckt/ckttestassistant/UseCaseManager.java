package com.ckt.ckttestassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.ckt.ckttestassistant.services.DoTestIntentService;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.usecases.CktUseCase;
import com.ckt.ckttestassistant.usecases.UseCaseBase;
import com.ckt.ckttestassistant.utils.CktXmlHelper2;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by ckt on 18-1-26.
 */

public class UseCaseManager implements DoTestIntentService.HandleCallback{
    private static final String FILENAME = "usecases.xml";
    private static final String TAG = "UseCaseManager";
    private ArrayList<UseCaseBase> mAllUseCases = new ArrayList<UseCaseBase>();
    private ArrayList<UseCaseBase> mSelectedUseCases = new ArrayList<UseCaseBase>();
    private ArrayList<UseCaseChangeObserver> mUseCaseChangeListener = new ArrayList<UseCaseChangeObserver>();
    private ArrayList<SelectedUseCaseChangeObserver> mSelectedUseCaseChangeObserver = new ArrayList<SelectedUseCaseChangeObserver>();
    private static Context mContext;
    private volatile static UseCaseManager instance;
    private CktXmlHelper2 mXmlHelper;
    private Handler mHandler;
    private ExecuteCallback mExecuteCallback = null;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;


    private UseCaseManager(){
        System.out.println("Singleton has loaded");
    }
    public static UseCaseManager getInstance(Context context){
        if(instance==null){
            synchronized (UseCaseManager.class){
                if(context != null){
                    mContext = context;
                }
                if(instance==null){
                    instance=new UseCaseManager();
                }
            }
        }
        return instance;
    }
    boolean importUseCaseConfig(String path){
        return true;
    }

    boolean outputUseCaseConfig(String path){
        return true;
    }

    boolean addUseCaseToConfigureFile(UseCaseBase usecase){
        if(!mSelectedUseCases.contains(usecase)){
            mSelectedUseCases.add(usecase);
        }
        return true;
    }
    public void init(Handler handler){
        mHandler = handler;
        mXmlHelper = new CktXmlHelper2();
        mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mPref.edit();

        Intent it = new Intent(mContext, DoTestIntentService.class);
        it.putExtra(DoTestIntentService.COMMAND, "init");
        mContext.startService(it);
        //load from xml
        /*mXmlHelper = new CktXmlHelper2();
        String path = mContext.getFilesDir()+"/usecases.xml";
        mAllUseCases.clear();
        mXmlHelper.getUseCases(path, mAllUseCases);
        useCaseChangeNotify(mAllUseCases.size(), 3); *///暂时全部刷新

        /*CktXmlHelper.readxml(mContext, FILENAME, mAllUseCases);
        UseCaseBase usecase1 = new CktUseCase("test1 stub");
        usecase1.addTestItem(new CktTestItem());
        usecase1.addTestItem(new CktTestItem());
        usecase1.addTestItem(new CktTestItem());
        mAllUseCases.add(usecase1);
        UseCaseBase usecase2 = new CktUseCase("test2 stub");
        usecase2.addTestItem(new CktTestItem());
        usecase2.addTestItem(new CktTestItem());
        usecase2.addTestItem(new CktTestItem());
        mAllUseCases.add(usecase2);*/
    }

    public boolean getTestStatus(){
        boolean status = mPref.getBoolean(MyConstants.PREF_TEST_STATUS, false);
        return status;
    }

    public void setTestStatus(boolean status){
        mEditor.putBoolean(MyConstants.PREF_TEST_STATUS, status);
        mEditor.commit();
    }
    public boolean startExecute(){
        setTestStatus(true);
        Intent it = new Intent(mContext, DoTestIntentService.class);
        it.putExtra(DoTestIntentService.COMMAND, "start");
        mContext.startService(it);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                int size = 0;
                if(mSelectedUseCases == null || mSelectedUseCases.isEmpty()){
                    return;
                }
                for (int index = 0; index < mSelectedUseCases.size() - 1; index++){
                    UseCaseBase tmp = mSelectedUseCases.get(index);
                    tmp.setNextUseCase(mSelectedUseCases.get(index + 1));
                }
                mSelectedUseCases.get(mSelectedUseCases.size() - 1).setNextUseCase(null);
                mSelectedUseCases.get(0).execute(mHandler, mExecuteCallback);
            }
        }).start();*/
        return true;
    }

    public void saveSelectedUseCaseToXml(ArrayList<UseCaseBase> selectedUseCase){
        if(selectedUseCase == null || selectedUseCase.isEmpty()){
            LogUtils.d(TAG, "don't select any testitem,so don't save anything!");
            return;
        }
        String path = mContext.getFilesDir()+"/selected_usecases.xml";

        try {
            mXmlHelper.reCreateXml(path, selectedUseCase);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void updateUseCaseOfXml(String path, UseCaseBase uc){
        ArrayList<UseCaseBase> usecases = new ArrayList<UseCaseBase>();
        try {
            mXmlHelper.getUseCases(path, usecases);
            if(updateUsecase(uc)){
                //mXmlHelper.updateUseCase(path, uc);
                mXmlHelper.reCreateXml(path, usecases);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean updateUsecase(UseCaseBase uc) {
        return false;
    }

    public void updateTestItemOfXml(String path, TestItemBase ti){
        ArrayList<UseCaseBase> usecases = new ArrayList<UseCaseBase>();
        try {
            mXmlHelper.getUseCases(path, usecases);
            if(updateTestItem(ti)){
                //mXmlHelper.updateUseCase(path, uc);
                mXmlHelper.reCreateXml(path, usecases);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean updateTestItem(TestItemBase ti) {
        return false;
    }

    private void getAllUseCaseFromXml() {
        String path = mContext.getFilesDir()+"/usecases.xml";
        mAllUseCases.clear();
        mXmlHelper.getUseCases(path, mAllUseCases);
        //refreshUseCaseList();
        useCaseChangeNotify();
    }
    public void getSelectedUseCaseFromXml(){
        String path = mContext.getFilesDir()+"/selected_usecases.xml";

        try {
            mSelectedUseCases.clear();
            mXmlHelper.getUseCases(path, mSelectedUseCases);
            selectedUseCaseChangeNotify();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveAllUseCaseToXml(ArrayList<TestItemBase> selectedTestItems, String name) {
        if(selectedTestItems == null || selectedTestItems.isEmpty()){
            LogUtils.d(TAG, "don't select any testitem,so don't save anything!");
            return;
        }
        String path = mContext.getFilesDir()+"/usecases.xml";
        UseCaseBase uc = new CktUseCase(name);
        uc.setTestItems(selectedTestItems);
        try {
            mXmlHelper.addUseCases(path, uc);
            getAllUseCaseFromXml();
            useCaseChangeNotify();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<UseCaseBase> getAllItems() {
        return mAllUseCases != null ? mAllUseCases : null;
    }

    public ArrayList<UseCaseBase> getSelectItems() {
        return mSelectedUseCases != null ? mSelectedUseCases : null;
    }

    @Override
    public void loadDataFromXml() {
        //load from xml
        getAllUseCaseFromXml();
        getSelectedUseCaseFromXml();
    }

    @Override
    public void startExecuteThread() {
        int size = 0;
        if(mSelectedUseCases == null || mSelectedUseCases.isEmpty()){
            return ;
        }
        for (int index = 0; index < mSelectedUseCases.size() - 1; index++){
            UseCaseBase tmp = mSelectedUseCases.get(index);
            tmp.setNextUseCase(mSelectedUseCases.get(index + 1));
        }
        mSelectedUseCases.get(mSelectedUseCases.size() - 1).setNextUseCase(null);
        mSelectedUseCases.get(0).execute(mHandler, mExecuteCallback);
    }

    public void updateSelectedUseCases(int index) {
        mSelectedUseCases.add(mAllUseCases.get(index));
        selectedUseCaseChangeNotify();
    }

    /**
     * Created by ckt on 18-2-1.
     */

    public static interface UseCaseChangeObserver {
        public void allUseCaseChangeNofify(int position, int i);
    }

    public static interface SelectedUseCaseChangeObserver {
        public void selectedUseCaseChangeNofify();
    }
    public void addUseCaseChangeObserver(UseCaseChangeObserver observer){
        if(observer == null){
            LogUtils.e(TAG, "UseCaseChangeObserver is null !!!");
            return ;
        }
        if(mUseCaseChangeListener != null){
            if(!mUseCaseChangeListener.contains(observer)){
                mUseCaseChangeListener.add(observer);
            }else{
                LogUtils.d(TAG, "UseCaseChangeObserver :" + observer.getClass().getName() + "has added");
            }
        }else{
            LogUtils.e(TAG, "addUseCaseChangeObserver error!!");
        }
    }
    public ArrayList<UseCaseChangeObserver> getUseCaseChangeListener() {
        return mUseCaseChangeListener;
    }
    public void addSelectedUseCaseChangeObserver(SelectedUseCaseChangeObserver observer){
        if(observer == null){
            LogUtils.e(TAG, "SelectedUseCaseChangeObserver is null !!!");
            return ;
        }
        if(mSelectedUseCaseChangeObserver != null){
            if(!mSelectedUseCaseChangeObserver.contains(observer)){
                mSelectedUseCaseChangeObserver.add(observer);
            }else{
                LogUtils.d(TAG, "SelectedUseCaseChangeObserver :" + observer.getClass().getName() + "has added");
            }
        }else{
            LogUtils.e(TAG, "addSelectedUseCaseChangeObserver error!!");
        }
    }
    public ArrayList<SelectedUseCaseChangeObserver> getSelectedUseCaseChangeListener() {
        return mSelectedUseCaseChangeObserver;
    }
    public void selectedUseCaseChangeNotify(){
        mHandler.sendEmptyMessage(MyConstants.UPDATE_SELECTEDUSECASES_UI);
    }
    public void useCaseChangeNotify(){
        /*if(mUseCaseChangeListener != null && !mUseCaseChangeListener.isEmpty()){
            for (UseCaseChangeObserver observer : mUseCaseChangeListener){
                observer.allUseCaseChangeNofify(position, i);
            }
        }*/
        Message msg = Message.obtain();
        msg.what = MyConstants.UPDATE_USECASEFRAGMENT_USECASELIST;
        Bundle b = new Bundle();
        b.putInt(MyConstants.UPDATE_USECASEFRAGMENT_POSOTION, 0);
        b.putInt(MyConstants.UPDATE_USECASEFRAGMENT_TYPE, 3);
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    public static interface ExecuteCallback{
        public void closeProgressView();
        public void updateProgressTitle(String title);
        public void updateProgressMessage(String message);
    }

    public void setmExecuteCallback(ExecuteCallback executeCallback) {
        this.mExecuteCallback = executeCallback;
    }
}
