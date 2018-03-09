package com.ckt.ckttestassistant;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ckt.ckttestassistant.fragment.FragmentAdapter;
import com.ckt.ckttestassistant.fragment.FragmentFactory;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;

import java.util.ArrayList;

public class CktTestAssistantMainActivity extends AppCompatActivity implements UseCaseManager.FinishExecuteObserver{

    private static final String TAG = "CktTestAssistantMainActivity";
    private ArrayList<TestItemBase> mSelectedTestItems;
    private UseCaseManager mUseCaseManager;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            //update UI
            switch (msg.what){
                case MyConstants.UPDATE_PROGRESS:
                    LogUtils.d(TAG,"MyConstants.UPDATE_PROGRESS");
                    break;
                case MyConstants.UPDATE_PROGRESS_TITLE:
                    LogUtils.d(TAG,"MyConstants.UPDATE_PROGRESS_TITLE");
                    String title = data.getString(MyConstants.PROGRESS_TITLE);
                    updateProgressTitle(title);
                    break;
                case MyConstants.UPDATE_PROGRESS_MESSAGE:
                    LogUtils.d(TAG,"MyConstants.UPDATE_PROGRESS_MESSAGE");
                    String message = data.getString(MyConstants.PROGRESS_MESSAGE);
                    updateProgressMessage(message);
                    break;
                case MyConstants.UPDATE_PROGRESS_CLOSE:
                    LogUtils.d(TAG,"MyConstants.UPDATE_PROGRESS_CLOSE");
                    closeProgressView();
                    break;
                case MyConstants.UPDATE_USECASEFRAGMENT_USECASELIST:
                    ArrayList<UseCaseManager.UseCaseChangeObserver> ul = mUseCaseManager.getUseCaseChangeListener();
                    if(ul != null && !ul.isEmpty()){
                        for (UseCaseManager.UseCaseChangeObserver observer : ul){
                            int position = data.getInt(MyConstants.UPDATE_USECASEFRAGMENT_POSITION, 0);
                            int type = data.getInt(MyConstants.UPDATE_USECASEFRAGMENT_TYPE, 3);
                            observer.allUseCaseChangeNofify(position, type);
                        }
                    }
                    break;
                case MyConstants.UPDATE_SELECTEDUSECASES_UI:
                    ArrayList<UseCaseManager.SelectedUseCaseChangeObserver> sl = mUseCaseManager.getSelectedUseCaseChangeListener();
                    if(sl != null && !sl.isEmpty()){
                        for (UseCaseManager.SelectedUseCaseChangeObserver observer : sl){
                            observer.selectedUseCaseChangeNofify();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private AlertDialog.Builder mProgressDialogBuilder = null;
    private View mProgressView;
    private TextView mProgressTitleTextView;
    private TextView mProgressMessageTextView;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate");
        setContentView(R.layout.activity_ckt_test_assistant_main);
        Intent it = getIntent();
        boolean reboot = it.getBooleanExtra("reboot", false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(PackageManager.PERMISSION_GRANTED != checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE")){
                LogUtils.d(TAG, "request permission: android.permission.WRITE_EXTERNAL_STORAGE");
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
            }
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        ViewPager viewPager = (ViewPager) findViewById(R.id.fragment);
        mUseCaseManager = UseCaseManager.getInstance(getApplicationContext());
        mUseCaseManager.init(mHandler, reboot);
        mUseCaseManager.addFinishExecuteObserver(this);

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), getTabTitles(), mHandler);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mDialog != null){
            if(!mDialog.isShowing()){
                LogUtils.d(TAG, "show progress");
                mDialog.show();
            }
        }
    }

    @Override
    protected void onPause() {
        if(mDialog != null){
            if(mDialog.isShowing()){
                LogUtils.d(TAG, "hide progress");
                mDialog.hide();
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        LogUtils.d(TAG, "onDestroy");
        mUseCaseManager.saveDataWhenExit();
        closeProgressView();
        super.onDestroy();
    }

    private String[] getTabTitles() {
        return getResources().getStringArray(R.array.tabTitles);
    }

    private void closeProgressView() {
        if(mDialog != null){
            mDialog.cancel();
            mDialog = null;
        }
        if(mProgressDialogBuilder != null){
            mProgressDialogBuilder = null;
        }
        if(mProgressTitleTextView != null){
            mProgressTitleTextView = null;
        }
        if(mProgressMessageTextView != null){
            mProgressMessageTextView = null;
        }
        //mUseCaseManager.setTestStatus(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.d(TAG,"CktTestAssistantMainActivity onKeyDown : "+keyCode);
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        LogUtils.d(TAG,"CktTestAssistantMainActivity onKeyUp : "+keyCode);
        return true;
    }

    private void updateProgressMessage(String message) {
        LogUtils.d(TAG, "updateProgressMessage message = "+message);
        if(mProgressDialogBuilder == null){
            LogUtils.d(TAG, "updateProgressMessage mProgressDialogBuilder is null!!!");
            mProgressDialogBuilder = new AlertDialog.Builder(CktTestAssistantMainActivity.this);
            mProgressDialogBuilder.setCancelable(false);
            mProgressView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.progress_layout, null);
            mProgressMessageTextView = (TextView)mProgressView.findViewById(R.id.message);
            mProgressMessageTextView.setText(message);
            mDialog = mProgressDialogBuilder.setView(mProgressView).create();
            mDialog.show();
        }else{
            LogUtils.d(TAG, "updateProgressMessage mProgressDialogBuilder not null, only update message");
            if(mProgressMessageTextView == null){
                mProgressMessageTextView = (TextView)mProgressView.findViewById(R.id.message);
            }
            mProgressMessageTextView.setText(message);
        }
    }

    private void updateProgressTitle(String title) {
        LogUtils.d(TAG, "updateProgressTitle title = "+title);
        if (mProgressDialogBuilder == null) {
            LogUtils.d(TAG, "updateProgressTitle mProgressDialogBuilder is null!!!");
            mProgressDialogBuilder = new AlertDialog.Builder(CktTestAssistantMainActivity.this);
            mProgressDialogBuilder.setCancelable(false);
            mProgressView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.progress_layout, null);
            mProgressTitleTextView = (TextView)mProgressView.findViewById(R.id.title);
            mProgressTitleTextView.setText(title);
            mDialog = mProgressDialogBuilder.setView(mProgressView).create();
            mDialog.show();
        } else {
            LogUtils.d(TAG, "updateProgressTitle mProgressDialogBuilder not null, only update title");
            if(mProgressTitleTextView == null){
                mProgressTitleTextView = (TextView)mProgressView.findViewById(R.id.title);
            }
            mProgressTitleTextView.setText(title);
        }

    }

    @Override
    public void finishExecueHandler() {
        LogUtils.d(TAG, "CktTestAssistantMainActivity finishExecueHandler");
    }
}
