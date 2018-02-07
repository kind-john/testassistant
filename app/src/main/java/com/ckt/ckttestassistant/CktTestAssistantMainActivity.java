package com.ckt.ckttestassistant;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ckt.ckttestassistant.fragment.FragmentAdapter;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;

import java.util.ArrayList;

public class CktTestAssistantMainActivity extends AppCompatActivity implements UseCaseManager.ExecuteCallback{

    private static final String TAG = "CktTestAssistantMainActivity";
    private ArrayList<TestItemBase> mSelectedTestItems;
    private UseCaseManager mUseCaseManager;

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
                case MyConstants.UPDATE_USECASEFRAGMENT_USECASELIST:
                    ArrayList<UseCaseManager.UseCaseChangeObserver> la = mUseCaseManager.getUseCaseChangeListener();
                    if(la != null && !la.isEmpty()){
                        for (UseCaseManager.UseCaseChangeObserver observer : la){
                            int position = data.getInt(MyConstants.UPDATE_USECASEFRAGMENT_POSOTION, 0);
                            int type = data.getInt(MyConstants.UPDATE_USECASEFRAGMENT_TYPE, 3);
                            observer.allUseCaseChangeNofify(position, type);
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
        setContentView(R.layout.activity_ckt_test_assistant_main);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        ViewPager viewPager = (ViewPager) findViewById(R.id.fragment);
        mUseCaseManager = UseCaseManager.getInstance(getApplicationContext());
        mUseCaseManager.init(mHandler);
        mUseCaseManager.setmExecuteCallback(this);

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), getTabTitles(), mHandler);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private String[] getTabTitles() {
        return getResources().getStringArray(R.array.tabTitles);
    }

    @Override
    public void closeProgressView() {
        if(mDialog != null){
            mDialog.cancel();
            mDialog = null;
        }
        if(mProgressDialogBuilder != null){
            mProgressDialogBuilder = null;
        }
    }

    @Override
    public void updateProgressMessage(String message) {
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

    @Override
    public void updateProgressTitle(String title) {
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
}
