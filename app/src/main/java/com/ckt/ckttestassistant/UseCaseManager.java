package com.ckt.ckttestassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;

import com.ckt.ckttestassistant.services.DoTestIntentService;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.usecases.CktUseCase;
import com.ckt.ckttestassistant.usecases.UseCaseBase;
import com.ckt.ckttestassistant.utils.CktXmlHelper;
import com.ckt.ckttestassistant.utils.CktXmlHelper2;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by ckt on 18-1-26.
 */

public class UseCaseManager implements DoTestIntentService.HandleCallback{
    private static final String FILENAME = "usecases.xml";
    private static final String TAG = "UseCaseManager";
    private ArrayList<TestBase> mAllUseCases = new ArrayList<TestBase>();
    private ArrayList<TestBase> mSelectedUseCases = new ArrayList<TestBase>();
    private ArrayList<UseCaseChangeObserver> mUseCaseChangeListener = new ArrayList<UseCaseChangeObserver>();
    private ArrayList<SelectedUseCaseChangeObserver> mSelectedUseCaseChangeObserver = new ArrayList<SelectedUseCaseChangeObserver>();
    private static Context mContext;
    private volatile static UseCaseManager instance;
    private CktXmlHelper mXmlHelper;
    private CktXmlHelper2 mXmlHelper2;
    private Handler mHandler;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private boolean mStatus;

    private ExecuteCallback mExecuteCallback = new ExecuteCallback() {

        @Override
        public void stopTestHandler() {
            notifyAllObserverOfFinishExecute();
        }
    };
    private ArrayList<FinishExecuteObserver> mFinishExecuteObservers = new ArrayList<FinishExecuteObserver>();
    private boolean mReboot = false;

    private void notifyAllObserverOfFinishExecute() {
        LogUtils.d(TAG, "enter notifyAllObserverOfFinishExecute");
        for (FinishExecuteObserver observer : mFinishExecuteObservers){
            observer.finishExecueHandler();
        }
    }


    private UseCaseManager(){
        System.out.println("Singleton has loaded");
    }

    /**
     * 单例模式实现管理类，以方便全区操作，数据操作都在此类完成
     * @param context
     * @return
     */
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
    public boolean importUseCaseConfig(String path){
        boolean result = false;
        ArrayList<TestBase> ucs = new ArrayList<TestBase>();
        try{
            //从配置文件中读取用例信息
            mXmlHelper.readxml(mContext, path, ucs);
            mAllUseCases.addAll(ucs);
            //将读取的配置文件写入xml
            String target_path = mContext.getFilesDir()+"/usecases.xml";
            mXmlHelper.addUsecase(target_path, mAllUseCases, true);
            notifyAllUseCaseChange();
            result = true;
        }catch(Exception e){
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    public boolean exportUseCaseConfig(String path){
        boolean result = false;
        ArrayList<TestBase> ucs = new ArrayList<TestBase>();
        try{
            //从本地xml文件中读取用例信息
            String source_path = mContext.getFilesDir()+"/usecases.xml";
            mXmlHelper.readxml(mContext, source_path, ucs);
            //将用例信息写入制定文件
            mXmlHelper.addUsecase(path, ucs, true);
            result = true;
        }catch(Exception e){
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    /**
     * 初始化数据，UI显示、测试任务需要这些数据
     * @param handler
     * @param reboot
     */
    public void init(Handler handler, boolean reboot){
        mHandler = handler;
        mReboot = reboot;
        mXmlHelper = new CktXmlHelper();
        mXmlHelper2 = new CktXmlHelper2();
        mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mPref.edit();
        mStatus = getTestStatus();
        Intent it = new Intent(mContext, DoTestIntentService.class);
        it.putExtra(DoTestIntentService.COMMAND, "init");
        mContext.startService(it);
    }

    /**
     * 获取测试状态
     * @return
     */
    public boolean getTestStatus(){
        boolean status = mPref.getBoolean(MyConstants.PREF_TEST_STATUS, false);
        return status;
    }

    public boolean isTestCompleted() {
        boolean result = true;
        for (TestBase tb : mSelectedUseCases){
            if (tb.getCompletedTimes() < tb.getTimes()) {
                return false;
            } else {
                if(tb.getChildren() != null && !tb.getChildren().isEmpty()){
                    result = isUseCaseTestCompleted(tb);
                }
            }
        }
        return result;
    }

    private boolean isUseCaseTestCompleted(TestBase stb) {
            if(stb.getCompletedTimes() < stb.getTimes()){
                return false;
            }else{
                for (TestBase tb : stb.getChildren()){
                    if (tb.getCompletedTimes() < tb.getTimes()){
                        return false;
                    }else{
                        return isUseCaseTestCompleted(tb);
                    }
                }
            }
            return true;
    }

    /**
     * 记录测试任务是否完成
     * @param status
     * true : 正在测试，下次进入应用时将接续测试
     * false : 不在测试，或者测试已经完成
     */
    public void setTestStatus(boolean status){
        mEditor.putBoolean(MyConstants.PREF_TEST_STATUS, status);
        mEditor.commit();
    }

    /**
     * 开始执行测试任务
     * @return
     */
    public boolean startExecute(){
        setTestStatus(true);
        Intent it = new Intent(mContext, DoTestIntentService.class);
        it.putExtra(DoTestIntentService.COMMAND, "start");
        mContext.startService(it);

        return true;
    }

    /**
     *将已所有用例序列保存到xml
     */
    private void saveAllUseCasesToXml(){
        if(mAllUseCases == null || mAllUseCases.isEmpty()){
            LogUtils.d(TAG, "There is no usecase need save,so don't save anything!");
            return;
        }
        String path = mContext.getFilesDir()+"/usecases.xml";

        try {
            mXmlHelper.addUsecase(path,mAllUseCases, true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *将已选择的用例序列保存到xml
     */
    public void saveSelectedUseCaseToXml(){
        if(mSelectedUseCases == null || mSelectedUseCases.isEmpty()){
            LogUtils.d(TAG, "don't select any testitem,so don't save anything!");
            return;
        }
        String path = mContext.getFilesDir()+"/selected_usecases.xml";
        File file = new File(path);
        if(file != null && file.exists()){
            file.delete();
        }
        try {
            mXmlHelper.addUsecase(path, mSelectedUseCases, true);
            //mXmlHelper2.reCreateXml(path, mSelectedUseCases);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void updateUseCaseOfXml(String path, UseCaseBase uc){
        ArrayList<UseCaseBase> usecases = new ArrayList<UseCaseBase>();
        try {
            mXmlHelper.updateUseCase(path, uc);
            //mXmlHelper.readxml(mContext, path, usecases);
            //mXmlHelper2.getUseCases(mContext, path, usecases);
            if(updateUsecase(uc)){
                //mXmlHelper.updateUseCase(path, uc);
                //mXmlHelper2.reCreateXml(path, usecases);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean updateUsecase(UseCaseBase uc) {
        return false;
    }

    public void updateTestItemOfSelectedUseCaseXml(TestItemBase ti){
        try {
            String path = mContext.getFilesDir()+"/selected_usecases.xml";
            mXmlHelper.updateTestItem(path, ti, true);
            if(updateTestItem(ti)){
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean updateTestItem(TestItemBase ti) {
        return false;
    }

    /**
     * 从usecases.xml文件中读取数据
     */
    private void getAllUseCaseFromXml() {
        String path = mContext.getFilesDir()+"/usecases.xml";
        try{
            mAllUseCases.clear();
            mXmlHelper.readxml(mContext, path, mAllUseCases);
            notifyAllUseCaseChange();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 从selected_usecases.xml文件中读取数据
     */
    public void getSelectedUseCaseFromXml(){
        String path = mContext.getFilesDir()+"/selected_usecases.xml";

        try {
            mSelectedUseCases.clear();
            mXmlHelper.readxml(mContext, path, mSelectedUseCases);
            dealWithRebootCase();
            notifySelectedUseCaseChange();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void dealWithRebootCase() {
        if(mReboot){
            LogUtils.d(TAG, "is reboot : true");
            //找到正在重启的任务
            if(mSelectedUseCases != null && !mSelectedUseCases.isEmpty()){
                for (TestBase tb : mSelectedUseCases){
                    int ucTotalTimes = tb.getTimes();
                    int ucCompletedTimes = tb.getCompletedTimes();
                    if(ucCompletedTimes == ucTotalTimes){
                        continue;
                    }
                    if(tb.getClassName().equals("com.ckt.ckttestassistant.testitems.Reboot")){
                        LogUtils.d(TAG, "change reboot times");
                        int tiFailTimes = tb.getFailTimes();
                        if(tiFailTimes > 0){
                            tiFailTimes--;
                            tb.setFailTimes(tiFailTimes);
                            updateTestItemOfSelectedUseCaseXml((TestItemBase) tb);
                        }
                    }
                }
            }
        }
    }

    /**
     * 在应用终止时，需要把数据保存，以备下次启动时使用
     */
    public void saveDataWhenExit(){
        //saveAllUseCasesToXml();
        //saveSelectedUseCaseToXml();
    }

    /**
     * 自定义用例界面点击保存时，将产生一个用例，调用此方法保存到xml文件
     * @param selectedTestItems
     * @param name
     */
    public void addUsecaseToAllUseCaseXml(ArrayList<TestBase> selectedTestItems, String name) {
        if(selectedTestItems == null || selectedTestItems.isEmpty()){
            LogUtils.d(TAG, "don't select any testitem,so don't save anything!");
            return;
        }
        String path = mContext.getFilesDir()+"/usecases.xml";
        UseCaseBase uc = new CktUseCase(mContext);
        uc.setTitle(name);
        uc.setChildren(selectedTestItems);
        ArrayList<TestBase> ucs = new ArrayList<TestBase>();
        ucs.add(uc);
        try {
            mXmlHelper.addUsecase(path, ucs, false);
            //mXmlHelper2.addUseCases(mContext, path, uc);
            getAllUseCaseFromXml();
            notifyAllUseCaseChange();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取所有用例引用
     * @return
     */
    public ArrayList<TestBase> getAllItems() {
        return mAllUseCases != null ? mAllUseCases : null;
    }

    /**
     * 获取已选用例引用
     * @return
     */
    public ArrayList<TestBase> getSelectItems() {
        return mSelectedUseCases != null ? mSelectedUseCases : null;
    }

    /**
     * 通过IntentService异步读取xml中数据
     */
    @Override
    public void loadDataFromXml() {
        //load from xml
        getAllUseCaseFromXml();
        getSelectedUseCaseFromXml();
    }

    /**
     * 通过IntentService异步执行测试任务
     */
    @Override
    public void startExecuteThread() {
        int size = 0;
        if(mSelectedUseCases == null || mSelectedUseCases.isEmpty()){
            LogUtils.d(TAG, "startExecuteThread exit : no selected use case");
            return ;
        }
        for (int index = 0; index < mSelectedUseCases.size() - 1; index++){
            TestBase tb = mSelectedUseCases.get(index);
            tb.task();
        }
    }
    /*@Override
    public void startExecuteThread() {
        int size = 0;
        if(mSelectedUseCases == null || mSelectedUseCases.isEmpty()){
            LogUtils.d(TAG, "startExecuteThread exit : no selected use case");
            return ;
        }
        for (int index = 0; index < mSelectedUseCases.size() - 1; index++){
            UseCaseBase tmp = mSelectedUseCases.get(index);
            tmp.setNextUseCase(mSelectedUseCases.get(index + 1));
        }
        mSelectedUseCases.get(mSelectedUseCases.size() - 1).setNextUseCase(null);
        mSelectedUseCases.get(0).execute(mHandler, mExecuteCallback);
    }*/

    public void updateSelectedUseCases(int index) {
        TestBase tb = mAllUseCases.get(index).clone();
        //tb.setChecked(true);
       /* int sn = mSelectedUseCases == null ? 0 : mSelectedUseCases.size();
        tb.setSN(sn);
        List<TestBase> tis = tb.getChildren();
        if(tis != null && !tis.isEmpty()){
            for(TestBase item : tis){
                int uc_id = item.getID(); //可以删除
                int uc_sn = item.getSN();
                LogUtils.d(TAG, "uc_id = "+uc_id+"; uc_sn = "+uc_sn);
                item.setParent(tb);
                //item.setUseCaseID(uc_id);
                //item.setUseCaseSN(uc_sn);
            }
        }*/
        mSelectedUseCases.add(tb);
        //saveSelectedUseCaseToXml();
        notifySelectedUseCaseChange();
    }

    public void setCurrentExcelFile(String fileName) {
        mEditor.putString(MyConstants.PREF_CURRENT_EXCEL_FILR, fileName);
        mEditor.commit();
    }

    public String getCurrentExcelFile(){
        return mPref.getString(MyConstants.PREF_CURRENT_EXCEL_FILR, "error");
    }

    public void createExcel(String path) {
        LogUtils.d(TAG, "createExcel path : " + path);
        try {
            WritableWorkbook book = Workbook.createWorkbook(new File(getCurrentExcelFile()));
            book.createSheet(mSelectedUseCases.get(0).getTitle(), 0);
            book.write();
            book.close();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateTestItemOfAllUseCaseXml(TestItemBase ti) {
        String path = mContext.getFilesDir()+"/usecases.xml";
        try{
            mXmlHelper.updateTestItem(path, ti, false);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void reInitSelectedUseCase() {
        LogUtils.d(TAG, "clear selected usecase,and read original from all usecase");
        reInitUseCase(mSelectedUseCases);
    }

    private void reInitUseCase(List<TestBase> tbs) {
        for (TestBase tb : tbs){
            tb.setCompletedTimes(0);
            tb.setFailTimes(0);
            List<TestBase> children = tb.getChildren();
            if(children != null && !children.isEmpty()){
                reInitUseCase(children);
            }
        }
    }

    /**
     * Created by ckt on 18-2-1.
     * 需要监听所有用例数据变化的类，必需实现此接口
     */
    public static interface UseCaseChangeObserver {
        public void allUseCaseChangeNofify(int position, int i);
    }

    /**
     * 需要监听已选用例数据变化的类，必需实现此接口
     */
    public static interface SelectedUseCaseChangeObserver {
        public void selectedUseCaseChangeNofify();
    }

    /**
     * 添加所有用例数据观察者类
     * @param observer
     */
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

    /**
     * 添加已选用例数据观察者类
     * @param observer
     */
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

    /**
     * 获取已选用例所有观察者类
     * @return
     */
    public ArrayList<SelectedUseCaseChangeObserver> getSelectedUseCaseChangeListener() {
        return mSelectedUseCaseChangeObserver;
    }

    /**
     * 通知所有观察者数据已经变化
     */
    public void notifySelectedUseCaseChange(){
        mHandler.sendEmptyMessage(MyConstants.UPDATE_SELECTEDUSECASES_UI);
    }

    /**
     * 通知所有观察者数据已经变化
     */
    public void notifyAllUseCaseChange(){
        /*if(mUseCaseChangeListener != null && !mUseCaseChangeListener.isEmpty()){
            for (UseCaseChangeObserver observer : mUseCaseChangeListener){
                observer.allUseCaseChangeNofify(position, i);
            }
        }*/
        Message msg = Message.obtain();
        msg.what = MyConstants.UPDATE_USECASEFRAGMENT_USECASELIST;
        Bundle b = new Bundle();
        b.putInt(MyConstants.UPDATE_USECASEFRAGMENT_POSITION, 0);
        b.putInt(MyConstants.UPDATE_USECASEFRAGMENT_TYPE, 3);
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    /**
     * 测试任务执行完成后的回调接口，
     * 状态提示框就是实现此回调关闭的
     */
    public static interface ExecuteCallback{
        /*public void closeProgressView();
        public void updateProgressTitle(String title);
        public void updateProgressMessage(String message);*/
        public void stopTestHandler();
        //public void clearSelectedUseCase();
    }

    /**
     * 设置回调实现
     * @param executeCallback
     */
    public void setExecuteCallback(ExecuteCallback executeCallback) {
        this.mExecuteCallback = executeCallback;
    }

    public static interface FinishExecuteObserver{
        public void finishExecueHandler();
    }
    public ArrayList<FinishExecuteObserver> getFinishExecuteObserver() {
        return mFinishExecuteObservers;
    }

    /**
     * 添加已选用例数据观察者类
     * @param observer
     */
    public void addFinishExecuteObserver(FinishExecuteObserver observer){
        if(observer == null){
            LogUtils.e(TAG, "FinishExecuteObserver is null !!!");
            return ;
        }
        if(mFinishExecuteObservers != null){
            if(!mFinishExecuteObservers.contains(observer)){
                mFinishExecuteObservers.add(observer);
            }else{
                LogUtils.d(TAG, "FinishExecuteObserver :" + observer.getClass().getName() + "has added");
            }
        }else{
            LogUtils.e(TAG, "FinishExecuteObserver error!!");
        }
    }

}
