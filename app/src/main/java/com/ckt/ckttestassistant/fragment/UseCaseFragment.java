package com.ckt.ckttestassistant.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.adapter.CktItemDecoration;
import com.ckt.ckttestassistant.interfaces.OnItemClickListener;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.usecases.UseCaseBase;
import com.ckt.ckttestassistant.adapter.TestItemListAdapter;
import com.ckt.ckttestassistant.adapter.UseCaseListAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ckt on 18-1-30.
 */

public class UseCaseFragment extends Fragment implements UseCaseManager.UseCaseChangeObserver,
        UseCaseManager.SelectedUseCaseChangeObserver, UseCaseManager.FinishExecuteObserver{
    private static final String TAG = "UseCaseFragment";
    private Handler mHandler = null;
    private RecyclerView mUseCaseList;
    private Context mContext;
    private ArrayList<UseCaseBase> mAllItems;
    private ArrayList<UseCaseBase> mSelectedItems;
    private UseCaseBase mCurrentUseCase;
    private UseCaseListAdapter mAdapter;
    private RecyclerView mUseCaseTestItemList;
    private UseCaseManager mUseCaseManager;
    private StringBuilder mShowPanelInfo = new StringBuilder();
    private TextView mUseCaseTextView;
    private Button mDeleteButton;
    private Button mSaveButton;
    private TestItemListAdapter mTestItemListAdapter;
    private Button mStartTestButton;
    private Activity mActivity;
    private Button mImportButton;
    private Button mExportButton;

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate");
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
        mUseCaseManager = UseCaseManager.getInstance(mContext);
        mUseCaseManager.init(mHandler, false);
        mUseCaseManager.addUseCaseChangeObserver(this);
        mUseCaseManager.addSelectedUseCaseChangeObserver(this);
        mUseCaseManager.addFinishExecuteObserver(this);
        mShowPanelInfo.append("use case : ");
        mAllItems = mUseCaseManager.getAllItems();
        mSelectedItems = mUseCaseManager.getSelectItems();
        if(mAllItems != null && !mAllItems.isEmpty()){
            mCurrentUseCase = mAllItems.get(0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_usecase_layout, container, false);
        mUseCaseTextView = (TextView) rootView.findViewById(R.id.usecasetext);
        mUseCaseManager.getSelectedUseCaseFromXml(); //及时与数据同步

        mUseCaseTextView.setText(mShowPanelInfo.toString());
        mStartTestButton = (Button) rootView.findViewById(R.id.starttest);
        mStartTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedItems == null || mSelectedItems.isEmpty()){
                    return;
                }
                if(createResultExcel()){
                    //保存数据，为了重启或者中断之后能继续执行
                    mUseCaseManager.reInitSelectedUseCase();
                    mUseCaseManager.saveSelectedUseCaseToXml();
                    mUseCaseManager.startExecute();
                    mStartTestButton.setClickable(false);
                }else{
                    Toast.makeText(mContext, R.string.createresultfail,Toast.LENGTH_LONG);
                }
            }
        });

        if(needStartTest()){
            LogUtils.d(TAG, "continue start execute test work!");
            mUseCaseManager.startExecute();
            mStartTestButton.setClickable(false);
        }

        mDeleteButton = (Button) rootView.findViewById(R.id.delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                if(mSelectedItems != null && !mSelectedItems.isEmpty()){
                    UseCaseBase uc = mSelectedItems.get(mSelectedItems.size() - 1);
                    uc.setIsChecked(false);
                    uc.setSN(-1);
                    mSelectedItems.remove(uc);
                }
                generateShowPanelString(mSelectedItems);
                mUseCaseTextView.setText(mShowPanelInfo.toString());
            }
        });
        mSaveButton = (Button) rootView.findViewById(R.id.save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                //mUseCaseManager.saveUseCaseToXml(mContext, mSelectedItems, "usecase");
                LogUtils.d(TAG, "save onClick");
                mUseCaseManager.saveSelectedUseCaseToXml();
            }
        });
        mImportButton = (Button) rootView.findViewById(R.id.importusecaseconfig);
        mImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                View importFilePathView = LayoutInflater.from(mContext).inflate(R.layout.import_config_layout, null);
                final EditText pathET = (EditText)importFilePathView.findViewById(R.id.path);
                pathET.setText("/sdcard/config.xml");
                pathET.setSelection(pathET.getText().length());
                builder.setTitle(R.string.importusecaseconfig)
                        .setView(importFilePathView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LogUtils.d(TAG, "Positive onClick");
                                String path = pathET.getText().toString();
                                mUseCaseManager.importUseCaseConfig(path);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LogUtils.d(TAG, "Negative onClick");
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                LogUtils.d(TAG, "onDismiss");
                            }
                        }).create().show();
            }
        });
        mExportButton = (Button) rootView.findViewById(R.id.exportusecaseconfig);
        mExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                View exportFilePathView = LayoutInflater.from(mContext).inflate(R.layout.import_config_layout, null);
                final EditText pathET = (EditText)exportFilePathView.findViewById(R.id.path);
                pathET.setText("/sdcard/config.xml");
                pathET.setSelection(pathET.getText().length());
                builder.setTitle(R.string.exportusecaseconfig)
                        .setView(exportFilePathView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LogUtils.d(TAG, "Positive onClick");
                                String path = pathET.getText().toString();
                                mUseCaseManager.exportUseCaseConfig(path);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LogUtils.d(TAG, "Negative onClick");
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                LogUtils.d(TAG, "onDismiss");
                            }
                        }).create().show();
            }
        });
        mUseCaseList = (RecyclerView) rootView.findViewById(R.id.usecaselist);
        initUseCaseListFocus(mAllItems);
        mAdapter = new UseCaseListAdapter(mContext, mAllItems, mSelectedItems);
        mAdapter.setUpdateShowPanelListener(new UseCaseListAdapter.UpdateShowPanelListener() {
            @Override
            public void updateShowPanelForAdd(int index) {
                showPropertySetting(mAllItems.get(index), index);
            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                LogUtils.d(TAG, "onItemClick");
                mCurrentUseCase = mAllItems.get(pos);
                updateTestItemList();
            }
        });
        mUseCaseList.setHasFixedSize(true);
        mUseCaseList.setLayoutManager(new LinearLayoutManager(mContext));
        mUseCaseList.addItemDecoration(new CktItemDecoration(mContext,
                LinearLayoutManager.VERTICAL,
                CktItemDecoration.DECORATION_TYPE_USECASELIST));
        mUseCaseList.setAdapter(mAdapter);
        mUseCaseTestItemList = (RecyclerView) rootView.findViewById(R.id.testitemlist);
        initTestItemList();
        return rootView;
    }

    private void initUseCaseListFocus(ArrayList<UseCaseBase> ucs) {
        if(ucs != null && !ucs.isEmpty()){
            UseCaseBase uc;
            for (int i = 0; i< ucs.size(); i++){
                uc = ucs.get(i);
                if(i == 0){
                    LogUtils.d(TAG, "initUseCaseListFocus : 0");
                    uc.setIsChecked(true);
                }else{
                    LogUtils.d(TAG, "initUseCaseListFocus : "+i);
                    uc.setIsChecked(false);
                }
            }
        }
    }

    private boolean createResultExcel() {
        boolean result = false;
        if(isNeedCreateNewFile()){
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                Date date = new Date(System.currentTimeMillis());
                String fileName = simpleDateFormat.format(date)+".xls";
                LogUtils.d(TAG, "fileName = "+fileName);
                String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ckttestassistant";
                //String path2 = Environment.getExternalStorageDirectory().getAbsolutePath();
                //String dirPath = mContext.getFilesDir()+"/ckttestassistant";

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
                mUseCaseManager.setCurrentExcelFile(filePath);
                mUseCaseManager.createExcel(filePath);
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            result = true;
        }
        return result;
    }

    private boolean isNeedCreateNewFile() {
        boolean result = true;
        boolean status = mUseCaseManager.getTestStatus();
        if(status){
            result = false;
        }else{
            result = mUseCaseManager.isTestCompleted();
        }
        //return result;   //后续要优化，暂时返回true测试excel读写
        return true;
    }

    private void showPropertySetting(final UseCaseBase uc, final int index) {
        LogUtils.d(TAG, "showPropertySetting :"+this.getClass().getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View v = LayoutInflater.from(mContext).inflate(R.layout.settings_usecase, null);
        final EditText delayEditText = (EditText)v.findViewById(R.id.delay);
        delayEditText.setText(String.valueOf(uc.getDelay()));
        final EditText timesEditText = (EditText)v.findViewById(R.id.times);
        timesEditText.setText(String.valueOf(uc.getTimes()));

        builder.setTitle(uc.getTitle())
                .setView(v)
                .setMessage("set properties")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtils.d(TAG, "Positive onClick");
                        int delay = Integer.parseInt(delayEditText.getText().toString());
                        int times = Integer.parseInt(timesEditText.getText().toString());
                        LogUtils.d(TAG, "delay = "+delay+"; times = "+times);
                        if(delay >= 0 && times > 0){
                            uc.setDelay(delay);
                            uc.setTimes(times);
                            setShowPanelForAdd(index);
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtils.d(TAG, "Negative onClick");
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        LogUtils.d(TAG, "onDismiss");
                    }
                }).create().show();
    }

    private boolean needStartTest() {
        /*if(!mUseCaseManager.getTestStatus()){
            return false;
        }*/
        LogUtils.d(TAG, "enter needStartTest");
        //boolean need = false;
        if(mSelectedItems != null && !mSelectedItems.isEmpty()){
            for (UseCaseBase uc : mSelectedItems){
                int uc_alltimes = uc.getTimes();
                int uc_completedTimes = uc.getCompletedTimes();
                LogUtils.d(TAG, "uc_alltimes = "+uc_alltimes+"; uc_completedTimes = "+uc_completedTimes);
                if(uc_alltimes > uc_completedTimes){
                    return true;
                }
                for (TestItemBase ti : uc.getTestItems()){
                    int ti_alltimes = ti.getTimes();
                    int ti_completedTimes = ti.getCompletedTimes();
                    LogUtils.d(TAG, "ti_alltimes = "+ti_alltimes+"; ti_completedTimes = "+ti_completedTimes);
                    if(ti_alltimes > ti_completedTimes){
                        return true;
                    }
                }
            }
        }
        /*if(!need){
            mUseCaseManager.setTestStatus(false);
        }*/
        return false;
    }

    private void initTestItemList() {
        LogUtils.d(TAG, "initTestItemList");
        ArrayList<TestItemBase> tis = mCurrentUseCase == null ? null : mCurrentUseCase.getTestItems();
        //initTestItemListFocus(tis);
        mTestItemListAdapter = new TestItemListAdapter(tis, null, false);
        mTestItemListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                TestItemBase ti = mCurrentUseCase.getTestItems().get(pos);
                LogUtils.d(TAG, "test item title clicked :"+pos+" : "+ti.getClass().getName());
                ti.showPropertyDialog(mActivity, true);
            }
        });
        mUseCaseTestItemList.setHasFixedSize(true);
        mUseCaseTestItemList.setLayoutManager(new LinearLayoutManager(mContext));
        mUseCaseTestItemList.addItemDecoration(new CktItemDecoration(mContext,
                LinearLayoutManager.VERTICAL,
                CktItemDecoration.DECORATION_TYPE_USECASE_TESTITEM));
        mUseCaseTestItemList.setAdapter(mTestItemListAdapter);
    }

    private void initTestItemListFocus(ArrayList<TestItemBase> tis) {
        if(tis != null && !tis.isEmpty()){
            TestItemBase ti;
            for (int i = 0; i< tis.size(); i++){
                ti = tis.get(i);
                if(i == 0){
                    ti.setIsChecked(true);
                }else{
                    ti.setIsChecked(false);
                }
            }
        }
    }

    private void updateTestItemList() {
        LogUtils.d(TAG, "updateTestItemList");
        ArrayList<TestItemBase> tis = mCurrentUseCase == null ? null : mCurrentUseCase.getTestItems();
        //initTestItemListFocus(tis);
        mTestItemListAdapter = new TestItemListAdapter(tis, null, false);
        mTestItemListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                TestItemBase ti = mCurrentUseCase.getTestItems().get(pos);
                LogUtils.d(TAG, "test item title clicked :"+pos+" : "+ti.getClass().getName());
                ti.showPropertyDialog(mActivity, true);
            }
        });
        mUseCaseTestItemList.setAdapter(mTestItemListAdapter);
    }

    public void setShowPanelForAdd(int index){
        mUseCaseManager.updateSelectedUseCases(index);
        generateShowPanelString(mSelectedItems);
        mUseCaseTextView.setText(mShowPanelInfo.toString());
    }

    private void generateShowPanelString(ArrayList<UseCaseBase> selectItems) {
        if (selectItems != null && !selectItems.isEmpty()){
            mShowPanelInfo.delete(11, mShowPanelInfo.length());
            for (int i = 0; i < selectItems.size(); i++){
                mShowPanelInfo.append(" > " + selectItems.get(i).getTitle());
                mShowPanelInfo.append(" x ");
                mShowPanelInfo.append(selectItems.get(i).getTimes());
            }
        } else {
            //处理最后一个删除不了的问题
            mShowPanelInfo.delete(11, mShowPanelInfo.length());
        }
    }

    @Override
    public void allUseCaseChangeNofify(int position, int i) {
        LogUtils.d(TAG, "allUseCaseChangeNofify position = "+position+"; i="+i);
        if(mAllItems != null && !mAllItems.isEmpty()){
            mCurrentUseCase = mAllItems.get(0);
            initUseCaseListFocus(mAllItems);
        }

        switch (i){
            case 0:
                mAdapter.notifyItemInserted(position);
                break;
            case 1:
                mAdapter.notifyItemChanged(position);
                break;
            case 2:
                mAdapter.notifyItemRemoved(position);
                break;
            default:
                mAdapter.notifyDataSetChanged();
                break;

        }
    }

    @Override
    public void selectedUseCaseChangeNofify() {
        generateShowPanelString(mSelectedItems);
        mUseCaseTextView.setText(mShowPanelInfo.toString());
    }

    @Override
    public void finishExecueHandler() {
        LogUtils.d(TAG, "finishExecueHandler");
        mStartTestButton.setClickable(true);
    }
}
