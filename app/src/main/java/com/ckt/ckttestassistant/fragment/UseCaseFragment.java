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
import android.widget.TextView;

import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.adapter.UseCaseTreeRecyclerAdapter;
import com.ckt.ckttestassistant.interfaces.OnSetParametersListener;
import com.ckt.ckttestassistant.interfaces.OnTreeTestBaseClickListener;
import com.ckt.ckttestassistant.interfaces.UpdateShowPanelListener;
import com.ckt.ckttestassistant.services.DoTestIntentService;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.usecases.UseCaseBase;
import com.ckt.ckttestassistant.utils.MyConstants;

import java.util.ArrayList;

/**
 * Created by ckt on 18-1-30.
 */

public class UseCaseFragment extends Fragment implements View.OnClickListener, UseCaseManager.UseCaseChangeObserver,
        UseCaseManager.SelectedUseCaseChangeObserver, UseCaseManager.FinishExecuteObserver{
    private static final String TAG = "UseCaseFragment";
    private Handler mHandler = null;
    private RecyclerView mUseCaseList;
    private Context mContext;
    private ArrayList<TestBase> mAllItems;
    private ArrayList<TestBase> mSelectedItems;
    private UseCaseTreeRecyclerAdapter mAdapter;
    private UseCaseManager mUseCaseManager;
    private StringBuilder mShowPanelInfo = new StringBuilder();
    private TextView mUseCaseTextView;
    private Button mDeleteButton;
    private Button mStartTestButton;
    private Activity mActivity;
    private Button mImportButton;
    private Button mExportButton;
    private View mEmptyView;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_usecase_recyclerview_layout, container, false);
        mUseCaseManager.getSelectItems(); //及时与数据同步
        generateShowPanelString(mSelectedItems);

        if(needStartTest()){
            LogUtils.d(TAG, "continue start execute test work!");
            mUseCaseManager.setUseCaseNeedInitChildren(false);
            mUseCaseManager.startExecute();
            mStartTestButton.setClickable(false);
        }

        initViews(rootView);

        mAdapter = new UseCaseTreeRecyclerAdapter(mContext, mAllItems, 1);
        //mAdapter.setFocus(0);

        mAdapter.setOnSetParametersListener(new OnSetParametersListener() {
            @Override
            public void setItemParameters(TestBase tb) {
                tb.showPropertyDialog(mActivity, true);
            }
        });
        mAdapter.setUpdateShowPanelListener(new UpdateShowPanelListener() {
            @Override
            public void updateShowPanelForAdd(TestBase tb) {
                setShowPanelForAdd(mAllItems.indexOf(tb));
            }
        });

        mUseCaseList.setLayoutManager(new LinearLayoutManager(mContext));
        mUseCaseList.setAdapter(mAdapter);
        if (mAllItems == null || mAllItems.isEmpty()) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
        return rootView;
    }

    private void initViews(View rootView) {
        mEmptyView = rootView.findViewById(R.id.emptyview);
        mUseCaseTextView = (TextView) rootView.findViewById(R.id.usecasetext);
        mUseCaseTextView.setText(mShowPanelInfo.toString());
        mStartTestButton = (Button) rootView.findViewById(R.id.starttest);
        mDeleteButton = (Button) rootView.findViewById(R.id.delete);
        mImportButton = (Button) rootView.findViewById(R.id.importusecaseconfig);
        mExportButton = (Button) rootView.findViewById(R.id.exportusecaseconfig);
        mUseCaseList = (RecyclerView) rootView.findViewById(R.id.usecaselist);
        mStartTestButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
        mImportButton.setOnClickListener(this);
        mExportButton.setOnClickListener(this);
    }

    private void showEmptyView() {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(View.VISIBLE);
        }
        if (mUseCaseList != null) {
            mUseCaseList.setVisibility(View.GONE);
        }
    }

    private void hideEmptyView() {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(View.GONE);
        }
        if (mUseCaseList != null) {
            mUseCaseList.setVisibility(View.VISIBLE);
        }
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

    public void setShowPanelForAdd(int index){
        mUseCaseManager.addUseCaseToSelectedNotSave(index);
        generateShowPanelString(mSelectedItems);
        mUseCaseTextView.setText(mShowPanelInfo.toString());
    }

    private void generateShowPanelString(ArrayList<TestBase> selectItems) {
        mShowPanelInfo.delete(0, mShowPanelInfo.length());
        String startTag = getResources().getString(R.string.useCaseStartTag);
        mShowPanelInfo.append(startTag);
        if (selectItems != null && !selectItems.isEmpty()){
            mShowPanelInfo.delete(startTag.length(), mShowPanelInfo.length());
            for (int i = 0; i < selectItems.size(); i++){
                TestBase tb = selectItems.get(i);
                mShowPanelInfo.append(tb.getTitle());
                mShowPanelInfo.append(" x ");
                mShowPanelInfo.append(tb.getTimes());
                if (i < selectItems.size() - 1) {
                    mShowPanelInfo.append(MyConstants.ITEM_DIVIDER_STRING);
                }
            }
        } else {
            //处理最后一个删除不了的问题
            mShowPanelInfo.delete(startTag.length(), mShowPanelInfo.length());
        }
    }

    @Override
    public void allUseCaseChangeNofify(int position, int i) {
        LogUtils.d(TAG, "allUseCaseChangeNofify position = "+position+"; i="+i);
        if(mAllItems != null && !mAllItems.isEmpty()){
            hideEmptyView();
            //mCurrentUseCase = mAdapter.setFocus(0);
            mAdapter.notifyData();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.starttest :
                if(mSelectedItems == null || mSelectedItems.isEmpty()){
                    return;
                }
                createResultExcel();
                //保存数据，为了重启或者中断之后能继续执行
                mUseCaseManager.reInitSelectedUseCase();
                mUseCaseManager.saveSelectedUseCaseToXml();
                mUseCaseManager.startExecute();
                mStartTestButton.setClickable(false);
                break;
            case R.id.importusecaseconfig :
                AlertDialog.Builder importBuilder = new AlertDialog.Builder(mActivity);
                View importFilePathView = LayoutInflater.from(mContext).inflate(R.layout.import_config_layout, null);
                final EditText importPathEditText = (EditText)importFilePathView.findViewById(R.id.path);
                importPathEditText.setText("/sdcard/config.xml");
                importPathEditText.setSelection(importPathEditText.getText().length());
                importBuilder.setTitle(R.string.importusecaseconfig)
                        .setView(importFilePathView)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LogUtils.d(TAG, "Positive onClick");
                                String path = importPathEditText.getText().toString();
                                mUseCaseManager.importUseCaseConfig(path);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
                break;
            case R.id.exportusecaseconfig :
                AlertDialog.Builder exportBuilder = new AlertDialog.Builder(mActivity);
                View exportFilePathView = LayoutInflater.from(mContext).inflate(R.layout.import_config_layout, null);
                final EditText exportPathEditText = (EditText)exportFilePathView.findViewById(R.id.path);
                exportPathEditText.setText("/sdcard/config.xml");
                exportPathEditText.setSelection(exportPathEditText.getText().length());
                exportBuilder.setTitle(R.string.exportusecaseconfig)
                        .setView(exportFilePathView)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LogUtils.d(TAG, "Positive onClick");
                                String path = exportPathEditText.getText().toString();
                                mUseCaseManager.exportUseCaseConfig(path);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
                break;
            case R.id.delete :
                if(mSelectedItems != null && !mSelectedItems.isEmpty()){
                    TestBase tb = mSelectedItems.get(mSelectedItems.size() - 1);
                    tb.setSN(-1);
                    mSelectedItems.remove(tb);
                }
                generateShowPanelString(mSelectedItems);
                mUseCaseTextView.setText(mShowPanelInfo.toString());
                break;
            default:
                break;
        }
    }
}
