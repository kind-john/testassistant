package com.ckt.ckttestassistant.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.TextView;

import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.adapter.CktItemDecoration;
import com.ckt.ckttestassistant.adapter.TreeListViewAdapter;
import com.ckt.ckttestassistant.adapter.UseCaseTreeAdapter;
import com.ckt.ckttestassistant.interfaces.OnItemClickListener;
import com.ckt.ckttestassistant.services.DoTestIntentService;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.usecases.UseCaseBase;
import com.ckt.ckttestassistant.adapter.TestItemListAdapter;

import java.util.ArrayList;

/**
 * Created by ckt on 18-1-30.
 */

public class UseCaseFragment extends Fragment implements UseCaseManager.UseCaseChangeObserver,
        UseCaseManager.SelectedUseCaseChangeObserver, UseCaseManager.FinishExecuteObserver{
    private static final String TAG = "UseCaseFragment";
    private Handler mHandler = null;
    private ListView mUseCaseList;
    private Context mContext;
    private ArrayList<TestBase> mAllItems;
    private ArrayList<TestBase> mSelectedItems;
    private TestBase mCurrentUseCase;
    private UseCaseTreeAdapter mAdapter;
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
        mUseCaseManager = UseCaseManager.getInstance(mContext, mActivity);
        mUseCaseManager.init(mHandler, false);
        mUseCaseManager.addUseCaseChangeObserver(this);
        mUseCaseManager.addSelectedUseCaseChangeObserver(this);
        mUseCaseManager.addFinishExecuteObserver(this);
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
        LogUtils.d(TAG, "mystarttag");
        View rootView = inflater.inflate(R.layout.fragment_usecase_layout, container, false);
        mUseCaseTextView = (TextView) rootView.findViewById(R.id.usecasetext);
        mUseCaseManager.getSelectItems(); //及时与数据同步
        generateShowPanelString(mSelectedItems);
        mUseCaseTextView.setText(mShowPanelInfo.toString());
        mStartTestButton = (Button) rootView.findViewById(R.id.starttest);
        mStartTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedItems == null || mSelectedItems.isEmpty()){
                    return;
                }
                createResultExcel();
                //保存数据，为了重启或者中断之后能继续执行
                mUseCaseManager.reInitSelectedUseCase();
                mUseCaseManager.saveSelectedUseCaseToXml();
                mUseCaseManager.startExecute();
                mStartTestButton.setClickable(false);

            }
        });

        if(needStartTest()){
            LogUtils.d(TAG, "continue start execute test work!");
            mUseCaseManager.setUseCaseNeedInitChildren(false);
            mUseCaseManager.startExecute();
            mStartTestButton.setClickable(false);
        }

        mDeleteButton = (Button) rootView.findViewById(R.id.delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                if(mSelectedItems != null && !mSelectedItems.isEmpty()){
                    TestBase tb = mSelectedItems.get(mSelectedItems.size() - 1);
                    tb.setSN(-1);
                    mSelectedItems.remove(tb);
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
        mUseCaseList = (ListView) rootView.findViewById(R.id.usecaselist);
        //initUseCaseListFocus(mAllItems);
        mAdapter = new UseCaseTreeAdapter(mContext, mAllItems, 1);
        mCurrentUseCase = mAdapter.setFocus(0);
        mAdapter.setUpdateShowPanelListener(new UseCaseTreeAdapter.UpdateShowPanelListener() {
            @Override
            public void updateShowPanelForAdd(TestBase tb) {
                //TestBase tb = mAllItems.get(index);
                int index = mAllItems.indexOf(tb);
                showPropertySetting((UseCaseBase)tb, index);
            }
        });
        mAdapter.setOnTreeTestBaseClickListener(new TreeListViewAdapter.OnTreeTestBaseClickListener() {
            @Override
            public void onClick(TestBase tb) {
                LogUtils.d(TAG, "onItemClick");
                mCurrentUseCase = tb;
                updateTestItemList();
            }
        });

        mUseCaseList.setAdapter(mAdapter);
        mUseCaseTestItemList = (RecyclerView) rootView.findViewById(R.id.testitemlist);
        initTestItemList();
        LogUtils.d(TAG, "myendtag");
        return rootView;
    }

    private void initUseCaseListFocus(ArrayList<TestBase> tbs) {
        if(tbs != null && !tbs.isEmpty()){
            TestBase tb;
            for (int i = 0; i< tbs.size(); i++){
                tb = tbs.get(i);
                if(i == 0){
                    LogUtils.d(TAG, "initUseCaseListFocus : 0");
                    tb.setChecked(true);
                }else{
                    LogUtils.d(TAG, "initUseCaseListFocus : "+i);
                    tb.setChecked(false);
                }
            }
        }
    }

    private void createResultExcel() {
        Intent it = new Intent(mContext, DoTestIntentService.class);
        it.putExtra(DoTestIntentService.COMMAND, DoTestIntentService.CREATEEXCELFILE_COMMAND);
        mContext.startService(it);
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
            return !isTestCompleted(mSelectedItems);
        }
        /*if(!need){
            mUseCaseManager.setTestStatus(false);
        }*/
        return false;
    }

    private boolean isTestCompleted(ArrayList<TestBase> tbs) {
        for (TestBase tb : tbs){
            int alltimes = tb.getTimes();
            int completedTimes = tb.getCompletedTimes();
            if(alltimes > completedTimes){
                return false;
            }
            ArrayList<TestBase> children = tb.getChildren();
            if(children != null && !children.isEmpty()){
                return isTestCompleted(children);
            }
        }
        return true;
    }

    private void initTestItemList() {
        LogUtils.d(TAG, "initTestItemList");
        ArrayList<TestBase> tis = mCurrentUseCase == null ? null : mCurrentUseCase.getChildren();
        //initTestItemListFocus(tis);
        mTestItemListAdapter = new TestItemListAdapter(tis, null, false);
        mTestItemListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                TestBase tb = mCurrentUseCase.getChildren().get(pos);
                if(tb instanceof TestItemBase){
                    TestItemBase ti = (TestItemBase)tb;
                    LogUtils.d(TAG, "test item title clicked :"+pos+" : "+ti.getClass().getName());
                    ti.showPropertyDialog(mActivity, true);
                }
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
                    ti.setChecked(true);
                }else{
                    ti.setChecked(false);
                }
            }
        }
    }

    private void updateTestItemList() {
        LogUtils.d(TAG, "updateTestItemList");
        ArrayList<TestBase> tbs = mCurrentUseCase == null ? null : mCurrentUseCase.getChildren();
        //initTestItemListFocus(tis);
        mTestItemListAdapter = new TestItemListAdapter(tbs, null, false);
        mTestItemListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                TestBase tb = mCurrentUseCase.getChildren().get(pos);
                if(tb instanceof TestItemBase){
                    TestItemBase ti = (TestItemBase)tb;
                    LogUtils.d(TAG, "test item title clicked :"+pos+" : "+ti.getClass().getName());
                    ti.showPropertyDialog(mActivity, true);
                }
            }
        });
        mUseCaseTestItemList.setAdapter(mTestItemListAdapter);
    }

    public void setShowPanelForAdd(int index){
        mUseCaseManager.addUseCaseToSelectedNotSave(index);
        generateShowPanelString(mSelectedItems);
        mUseCaseTextView.setText(mShowPanelInfo.toString());
    }

    private void generateShowPanelString(ArrayList<TestBase> selectItems) {
        mShowPanelInfo.delete(0, mShowPanelInfo.length());
        mShowPanelInfo.append("use case : ");
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
            mCurrentUseCase = mAdapter.setFocus(0); //mAllItems.get(0);
            //initUseCaseListFocus(mAllItems);
        }
        //mAdapter.notifyDataSetChanged();
        mAdapter.notifyData();
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
