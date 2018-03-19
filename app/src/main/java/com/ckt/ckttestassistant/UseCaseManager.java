package com.ckt.ckttestassistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.ckt.ckttestassistant.services.DoTestIntentService;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.usecases.CktUseCase;
import com.ckt.ckttestassistant.usecases.UseCaseBase;
import com.ckt.ckttestassistant.utils.CktXmlHelper;
import com.ckt.ckttestassistant.utils.CktXmlHelper2;
import com.ckt.ckttestassistant.utils.HandlerMessageWhat;
import com.ckt.ckttestassistant.utils.JSONUtils;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by ckt on 18-1-26.
 */

public class UseCaseManager implements DoTestIntentService.HandleCallback{
    private static final String TAG = "UseCaseManager";
    private ArrayList<TestBase> mAllUseCases = new ArrayList<TestBase>();
    private ArrayList<TestBase> mSelectedUseCases = new ArrayList<TestBase>();
    private HashMap<String, Point> mTouchPosConfig = new HashMap<String, Point>();
    private ArrayList<UseCaseChangeObserver> mUseCaseChangeListener = new ArrayList<UseCaseChangeObserver>();
    private ArrayList<SelectedUseCaseChangeObserver> mSelectedUseCaseChangeObserver = new ArrayList<SelectedUseCaseChangeObserver>();
    private static Context mContext;
    private static Activity mActivity;
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
    private InitStatus mInitStatus = InitStatus.NOINIT;

    private void notifyAllObserverOfFinishExecute() {
        LogUtils.d(TAG, "method notifyAllObserverOfFinishExecute enter");
        for (FinishExecuteObserver observer : mFinishExecuteObservers){
            observer.finishExecueHandler();
        }
    }


    private UseCaseManager(){
        LogUtils.d(TAG, "Singleton has loaded");
    }

    public HashMap<String, Point> getTouchPosConfig() {
        return mTouchPosConfig;
    }

    public void setTouchPosConfig(HashMap<String, Point> touchPosConfig) {
        this.mTouchPosConfig = touchPosConfig;
    }
    /**
     * 单例模式实现管理类，以方便全区操作，数据操作都在此类完成
     * @param context
     * @return
     */
    public static UseCaseManager getInstance(Context context, Activity activity){
        if(instance==null){
            synchronized (UseCaseManager.class){
                if(context != null){
                    mContext = context;
                }
                if(activity != null){
                    mActivity = activity;
                }
                if(instance==null){
                    instance=new UseCaseManager();
                }
            }
        }
        return instance;
    }

    /**
     * 导入用例配置
     * @param path
     * @return
     */
    public synchronized boolean importUseCaseConfig(String path){
        LogUtils.d(TAG, "method importUseCaseConfig enter");
        boolean result = false;
        ArrayList<TestBase> ucs = new ArrayList<TestBase>();
        try{
            //从配置文件中读取用例信息
            mXmlHelper.readxml(mContext, mActivity, path, ucs);
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

    /**
     * 导出用例配置
     * @param path
     * @return
     */
    public synchronized boolean exportUseCaseConfig(String path){
        LogUtils.d(TAG, "exportUseCaseConfig");
        boolean result = false;
        ArrayList<TestBase> ucs = new ArrayList<TestBase>();
        try{
            //从本地xml文件中读取用例信息
            String source_path = mContext.getFilesDir()+"/usecases.xml";
            mXmlHelper.readxml(mContext, mActivity, source_path, ucs);
            //将用例信息写入制定文件
            mXmlHelper.addUsecase(path, ucs, true);
            result = true;
        }catch(Exception e){
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取主线程handler，方便更新UI
     * @return
     */
    public Handler getHandler() {
        return mHandler;
    }

    /**
     * 设置主线程handler，方便更新UI
     * @return
     */
    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    /**
     * 初始化数据：
     * 1、加载用例配置
     * 2、加载触摸位置定义
     * 3、加载正在测试的用例信息
     * 4、处理重启后自动启动的善后工作
     * @param handler
     * @param reboot
     */
    public void init(Handler handler, boolean reboot){
        LogUtils.d(TAG, "method init enter");
        LogUtils.d(TAG, "mInitStatus = " + mInitStatus);
        if(mInitStatus == InitStatus.NOINIT){
            mInitStatus = InitStatus.DOING;
            mHandler = handler;
            mReboot = reboot;
            mXmlHelper = new CktXmlHelper();
            //mXmlHelper2 = new CktXmlHelper2();
            mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            mEditor = mPref.edit();
            mStatus = getTestStatus();
            LogUtils.d(TAG,"init current threadid :"+Thread.currentThread().getId());
            Intent it = new Intent(mContext, DoTestIntentService.class);
            it.putExtra(DoTestIntentService.COMMAND, DoTestIntentService.INIT_COMMAND);
            mContext.startService(it);
        }
        LogUtils.d(TAG, "method init exit");
    }

    /**
     * 关闭进度提示框
     * @param handler
     * @param finish
     */
    private void closeWaitProgress(Handler handler, boolean finish) {
        if(finish){
            Message msg = Message.obtain();
            msg.what = HandlerMessageWhat.UPDATE_PROGRESS_CLOSE;
            handler.sendMessage(msg);
        }else{
            LogUtils.e(TAG, "error: progress close fail!!!");
        }
    }

    /**
     * 从sharedpreference获取测试状态
     * @return
     */
    public boolean getTestStatus(){
        boolean status = mPref.getBoolean(MyConstants.PREF_TEST_STATUS, false);
        return status;
    }

    /**
     * 从SharedPreference获取重启标志
     * @return
     */
    public boolean getRebootFlagFromSharedPreference(){
        boolean flag = mPref.getBoolean(MyConstants.REBOOT_FLAG, false);
        return flag;
    }

    /**
     * 设置重启标志到sharedPreference
     * @param flag
     */
    public void setRebootFlagToSharedPreference(boolean flag){
        mEditor.putBoolean(MyConstants.REBOOT_FLAG, flag);
        mEditor.apply();
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

    /**
     * 判断子测试项是否都已经完成，用来在重启后决定是否给测试用例次数加1
     * @param parent
     * @return
     */
    public boolean isChildrenTestCompleted(TestBase parent) {
        boolean result = true;
        if(parent != null && !parent.isChecked()){
            ArrayList<TestBase> children = parent.getChildren();
            for (TestBase child : children){
                if(child.getCompletedTimes() < child.getTimes()){
                    return false;
                }

                if(child.getChildren().size() > 0){
                    if(!isChildrenTestCompleted(child)){
                        return false;
                    }
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
        mEditor.apply();
    }

    /**
     * 开始执行测试任务
     * @return
     */
    public boolean startExecute(){
        LogUtils.d(TAG, "method startExecute enter");
        setTestStatus(true);
        Intent it = new Intent(mContext, DoTestIntentService.class);
        it.putExtra(DoTestIntentService.COMMAND, DoTestIntentService.STARTEXECUTE_COMMAND);
        mContext.startService(it);

        return true;
    }
    /**
     * 自定义用例界面点击保存时，将产生一个用例，调用此方法保存到xml文件
     * @param selectedTestItems
     * @param name
     */
    public synchronized void addUsecaseToAllUseCaseXml(ArrayList<TestBase> selectedTestItems, String name) {
        LogUtils.d(TAG, "method addUsecaseToAllUseCaseXml enter");
        if(selectedTestItems == null || selectedTestItems.isEmpty()){
            LogUtils.d(TAG, "don't select any testitem,so don't save anything!");
            return;
        }
        String path = mContext.getFilesDir()+"/usecases.xml";
        UseCaseBase uc = new CktUseCase(mContext);
        uc.setTitle(name);
        uc.setChildren(selectedTestItems);
        uc.setChildrenSN();
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
        LogUtils.d(TAG, "method addUsecaseToAllUseCaseXml exit");
    }
    /**
     * 从usecases.xml文件中读取数据
     */
    private synchronized void getAllUseCaseFromXml() {
        LogUtils.d(TAG, "method getAllUseCaseFromXml enter");
        String path = mContext.getFilesDir()+"/usecases.xml";
        LogUtils.d(TAG, "getAllUseCaseFromXml");
        try{
            mAllUseCases.clear();
            mXmlHelper.readxml(mContext, mActivity, path, mAllUseCases);
            notifyAllUseCaseChange();
        }catch (Exception e){
            e.printStackTrace();
        }
        LogUtils.d(TAG, "method getAllUseCaseFromXml exit");
    }

    /**
     * 从selected_usecases.xml文件中读取数据
     */
    public synchronized void getSelectedUseCaseFromXml(){
        LogUtils.d(TAG, "method getSelectedUseCaseFromXml enter");
        String path = mContext.getFilesDir()+"/selected_usecases.xml";
        LogUtils.d(TAG, "getSelectedUseCaseFromXml");
        try {
            mSelectedUseCases.clear();
            mXmlHelper.readxml(mContext, mActivity, path, mSelectedUseCases);
            notifySelectedUseCaseChange();
        }catch (Exception e){
            e.printStackTrace();
        }
        LogUtils.d(TAG, "method getSelectedUseCaseFromXml exit");
    }
    /**
     *将已选择的用例序列保存到xml
     */
    public synchronized void saveSelectedUseCaseToXml(){
        LogUtils.d(TAG, "method saveSelectedUseCaseToXml enter");
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

    /**
     * 保存用例到usecase.xml
     * @param uc
     */
    public synchronized void updateAllUseCaseOfXml(UseCaseBase uc){
        String path = mContext.getFilesDir()+"/usecases.xml";
        updateUseCaseOfXml(path, uc);
    }
    /**
     * 保存用例到selected_usecase.xml
     * @param uc
     */
    public synchronized void updateSelectedUseCaseOfXml(UseCaseBase uc){
        String path = mContext.getFilesDir()+"/selected_usecases.xml";
        updateUseCaseOfXml(path, uc);
    }

    /**
     * 保存用例到 path
     * @param path
     * @param uc
     */
    public synchronized void updateUseCaseOfXml(String path, UseCaseBase uc){
        LogUtils.d(TAG, "metod updateUseCaseOfXml enter");
        ArrayList<UseCaseBase> usecases = new ArrayList<UseCaseBase>();
        try {
            mXmlHelper.updateUseCase(path, uc);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 保存测试项到 selected_usecase.xml
     * @param ti
     */
    public synchronized void updateTestItemOfAllUseCaseXml(TestItemBase ti) {
        LogUtils.d(TAG, "method updateTestItemOfAllUseCaseXml enter");
        String path = mContext.getFilesDir()+"/usecases.xml";
        updateTestItemOfXml(path, ti);

        LogUtils.d(TAG, "method updateTestItemOfAllUseCaseXml exit");
    }
    /**
     * 保存测试项到 usecase.xml
     * @param ti
     */
    public synchronized void updateTestItemOfSelectedUseCaseXml(TestItemBase ti){
        LogUtils.d(TAG, "method updateTestItemOfSelectedUseCaseXml enter");
        String path = mContext.getFilesDir()+"/selected_usecases.xml";
        updateTestItemOfXml(path, ti);
    }
    /**
     * 保存测试项到 path
     * @param path
     * @param ti
     */
    public synchronized void updateTestItemOfXml(String path, TestItemBase ti){
        LogUtils.d(TAG, "method updateTestItemOfXml enter");
        try {
            mXmlHelper.updateTestItem(path, ti, true);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 处理重启后的剩余工作
     */
    private void dealWithRebootCase() {
        LogUtils.d(TAG, "method dealWithRebootCase enter");
        if(mReboot){
            LogUtils.d(TAG, "is reboot : true");
            mReboot = false;
            setRebootFlagToSharedPreference(false);
            //找到正在重启的任务
            if(mSelectedUseCases != null && !mSelectedUseCases.isEmpty()){
                for (TestBase tb : mSelectedUseCases){
                    int ucTotalTimes = tb.getTimes();
                    int ucCompletedTimes = tb.getCompletedTimes();
                    if(ucCompletedTimes == ucTotalTimes){
                        continue;
                    }
                    TestBase rebootItem = findTestingRebootNode(tb);
                    LogUtils.d(TAG, "find reboot node : "+rebootItem.toString());
                    if(rebootItem != null){
                        LogUtils.d(TAG, "found ... TestingRebootNode");
                        ((TestItemBase)rebootItem).task2(true);
                        setParentParam(rebootItem);
                        return;
                    }
                }
            }
        }
        LogUtils.d(TAG, "method dealWithRebootCase exit");
    }

    /**
     * 设置父节点completedTimes
     * @param tb
     */
    private void setParentParam(TestBase tb){
        LogUtils.d(TAG, "method setParentParam enter");
        TestBase parent = tb.getParent();
        if(parent != null){
            if(isChildrenTestCompleted(parent)){
                LogUtils.d(TAG, "update parent node : "+parent.toString());
                parent.setCompletedTimes(parent.getCompletedTimes()+1);
                if (parent instanceof UseCaseBase){
                    updateSelectedUseCaseOfXml((UseCaseBase)parent);
                } else if (parent instanceof TestItemBase){
                    updateTestItemOfSelectedUseCaseXml((TestItemBase) parent);
                }
                setParentParam(parent);
            }
        }
        LogUtils.d(TAG, "method setParentParam exit");
    }

    /**
     * 找到正在测试的reboot项
     * @param tb
     * @return 找不到则为null
     */
    private TestBase findTestingRebootNode(TestBase tb) {
        LogUtils.d(TAG, "method findTestingRebootNode enter");
        TestBase result = null;
        ArrayList<TestBase> children = tb.getChildren();
        if(children != null && !children.isEmpty()){
            for (TestBase child : children){
                if (child instanceof UseCaseBase){
                    result = findTestingRebootNode(child);
                    if(result != null){
                        break;
                    }
                }else{
                    if("com.ckt.ckttestassistant.testitems.Reboot".equals(child.getClassName())){
                        LogUtils.d(TAG, "change reboot times");
                        int total = child.getTimes();
                        int done = child.getCompletedTimes();
                        if(done < total){
                            result = child;
                            break;
                        }
                    }
                }
            }
        }
        LogUtils.d(TAG, "method findTestingRebootNode exit");
        return result;
    }

    /**
     * 在应用终止时，需要把数据保存，以备下次启动时使用
     */
    public void saveDataWhenExit(){
        //saveAllUseCasesToXml();
        //saveSelectedUseCaseToXml();
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
        LogUtils.d(TAG, "method loadDataFromXml enter");
        LogUtils.d(TAG,"loadDataFromXml current thread :"+Thread.currentThread().getId());
        getAllUseCaseFromXml();
        getSelectedUseCaseFromXml();
        dealWithRebootCase();
        LogUtils.d(TAG, "method loadDataFromXml exit");
    }

    /**
     * 加载模拟触摸坐标
     */
    @Override
    public void loadTouchPos() {
        //load position of touch panel
        LogUtils.d(TAG, "method loadTouchPos enter");
        LogUtils.d(TAG,"loadTouchPos current thread :"+Thread.currentThread().getId());
        mTouchPosConfig.clear();
        try {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(mContext.getAssets().open("pos.json")));
                StringBuilder sb = new StringBuilder();
                String str;
                while ((str = br.readLine()) != null){
                    sb.append(str);
                    sb.append("\n");
                }
                JSONUtils.parseJsonArray(sb.toString(), mTouchPosConfig);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "method loadTouchPos exit");
    }

    /**
     * 通过IntentService异步执行测试任务
     */
    @Override
    public void startExecuteThread() {
        LogUtils.d(TAG, "method startExecuteThread enter");
        LogUtils.d(TAG,"startExecuteThread current thread :"+Thread.currentThread().getId());
        int size = 0;
        if(mSelectedUseCases == null || mSelectedUseCases.isEmpty()){
            LogUtils.d(TAG, "startExecuteThread exit : no selected use case");
            return ;
        }
        for (int index = 0; index < mSelectedUseCases.size(); index++){
            TestBase tb = mSelectedUseCases.get(index);
            tb.task();
        }
        closeWaitProgress(mHandler, true);
        notifyAllObserverOfFinishExecute();
        LogUtils.d(TAG, "method startExecuteThread exit");
    }

    /**
     * 表示初始化状态的枚举
     */
    private enum InitStatus {
        // 还没有初始化
        NOINIT,
        // 正在初始化
        DOING,
        // 初始化完成
        DONE
    }

    /**
     * 初始化回调方法，完成后修改状态
     */
    @Override
    public void initDone() {
        mInitStatus = InitStatus.DONE;
    }

    /**
     * 创建Excel文件
     * @return
     */
    @Override
    public boolean createExcelFile() {
        LogUtils.d(TAG, "method createExcelFile enter");
        boolean result = false;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Date date = new Date(System.currentTimeMillis());
            String fileName = simpleDateFormat.format(date)+".xls";
            LogUtils.d(TAG, "fileName = "+fileName);
            //String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ckttestassistant";
            //String path2 = Environment.getExternalStorageDirectory().getAbsolutePath();
            String dirPath = mContext.getFilesDir()+"/ckttestassistant";

            //File dir = new File(MyConstants.TEST_RESULT_EXCEL_DIR);
            File dir = new File(dirPath);
            if(!dir.exists()){
                if(!dir.mkdirs()){
                    return false;
                }
            }
            String filePath = dirPath+"/"+fileName;
            File excelfile = new File(filePath);
            excelfile.createNewFile();
            setCurrentExcelFile(filePath);
            createExcel(filePath);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "method addUsecaseToAllUseCaseXml exit");
        return result;
    }

    /**
     * UI点击add时调用，将用例保存到内存（ArrayList），执行测试任务时才保存到XML
     * @param index
     */
    public void addUseCaseToSelectedNotSave(int index) {
        LogUtils.d(TAG, "method addUseCaseToSelectedNotSave enter");
        TestBase tb = mAllUseCases.get(index).deepClone(null);
        //tb.setChecked(true);
        int sn = mSelectedUseCases == null ? 0 : mSelectedUseCases.size();
        tb.setSN(sn);
        List<TestBase> tis = tb.getChildren();
        if(tis != null && !tis.isEmpty()){
            for(TestBase item : tis){
                item.setParent(tb);
            }
        }
        mSelectedUseCases.add(tb);
        //notifySelectedUseCaseChange();
        LogUtils.d(TAG, "method addUseCaseToSelectedNotSave exit");
    }

    /**
     * 记录当前Excel文件，后续测试结果保存要用到，没启动一次任务创建一个文件
     * @param fileName
     */
    public void setCurrentExcelFile(String fileName) {
        mEditor.putString(MyConstants.PREF_CURRENT_EXCEL_FILR, fileName);
        mEditor.commit();
    }

    /**
     * 获取当前Excel文件
     * @return
     */
    public String getCurrentExcelFile(){
        return mPref.getString(MyConstants.PREF_CURRENT_EXCEL_FILR, "error");
    }

    /**
     * 创建Excel以及一个sheet
     * @param path
     */
    public void createExcel(String path) {
        LogUtils.d(TAG, "method createExcel enter");
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
        LogUtils.d(TAG, "method createExcel exit");
    }

    /**
     * 每次点击start前需要初始化测试项，开始一次新的测试任务
     */
    public void reInitSelectedUseCase() {
        LogUtils.d(TAG, "method reInitSelectedUseCase enter");
        reInitUseCase(mSelectedUseCases);
        LogUtils.d(TAG, "method reInitSelectedUseCase exit");
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
     * 设置子用例开始测试时是否要将其包含的测试项初始化，
     * 因为一个用例可能测试多次，而次数等状态就需要合理的重置
     * @param isNeed
     */
    public void setUseCaseNeedInitChildren(boolean isNeed) {
        LogUtils.d(TAG, "method setUseCaseNeedInitChildren enter");
        if(mSelectedUseCases != null && !mSelectedUseCases.isEmpty()){
            for (TestBase tb : mSelectedUseCases){
                setNeedInitChildren(tb, isNeed);
            }
        }
        LogUtils.d(TAG, "method setUseCaseNeedInitChildren exit");
    }
    private void setNeedInitChildren(TestBase tb, boolean isNeed) {
        if (tb instanceof UseCaseBase){
            ((UseCaseBase) tb).setNeedInitFlag(isNeed);
            List<TestBase> children = tb.getChildren();
            if(children != null && !children.isEmpty()){
                for (TestBase child : children){
                    if(child instanceof UseCaseBase){
                        setNeedInitChildren(child, isNeed);
                    }
                }
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
        mHandler.sendEmptyMessage(HandlerMessageWhat.UPDATE_SELECTEDUSECASES_UI);
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
        msg.what = HandlerMessageWhat.UPDATE_USECASEFRAGMENT_USECASELIST;
        Bundle b = new Bundle();
        b.putInt(HandlerMessageWhat.UPDATE_USECASEFRAGMENT_POSITION, 0);
        b.putInt(HandlerMessageWhat.UPDATE_USECASEFRAGMENT_TYPE, 3);
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
