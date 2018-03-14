package com.ckt.ckttestassistant.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.adapter.ResultsTestItemAdapter;
import com.ckt.ckttestassistant.adapter.ResultsTreeAdapter;
import com.ckt.ckttestassistant.adapter.ResultsUseCaseAdapter;
import com.ckt.ckttestassistant.adapter.TreeListViewAdapter;
import com.ckt.ckttestassistant.adapter.UseCaseTreeAdapter;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.usecases.UseCaseBase;
import com.ckt.ckttestassistant.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by ckt on 18-1-30.
 */

public class ResultsFragment extends Fragment implements UseCaseManager.SelectedUseCaseChangeObserver {
    private static final String TAG = "ResultsFragment";
    private Handler mHandler = null;
    private Context mContext;
    private UseCaseManager mUseCaseManager;
    private ArrayList<TestBase> mSelectedItems;
    private ListView mUseCaseListView;
    private ListView mTestItemListView;
    private ResultsTreeAdapter mUseCaseAdapter;
    private int mCurrentUseCase = 0;
    private ResultsTestItemAdapter mTestItemAdapter;
    private ArrayList<TestBase> mCurrentTestItems = null;
    private Activity mActivity;

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
        mUseCaseManager = UseCaseManager.getInstance(mContext, mActivity);
        mUseCaseManager.init(mHandler, false);
        mUseCaseManager.addSelectedUseCaseChangeObserver(this);
        mSelectedItems = mUseCaseManager.getSelectItems();
        if(mSelectedItems != null && !mSelectedItems.isEmpty()){
            mCurrentTestItems = mSelectedItems.get(mCurrentUseCase).getChildren();
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_results_layout, container, false);
        mUseCaseManager.getSelectedUseCaseFromXml();
        mUseCaseListView = (ListView) rootView.findViewById(R.id.testedusecaselist);
        //initUseCaseListFocus(mSelectedItems);
        mUseCaseAdapter = new ResultsTreeAdapter(mContext, mSelectedItems, 1);
        mUseCaseAdapter.setFocus(0);

        mUseCaseAdapter.setOnTreeTestBaseClickListener(new TreeListViewAdapter.OnTreeTestBaseClickListener() {
            @Override
            public void onClick(TestBase tb) {
                LogUtils.d(TAG, "onItemClick");
            }
        });
        mUseCaseListView.setAdapter(mUseCaseAdapter);
        /*mUseCaseAdapter = new ResultsUseCaseAdapter(mContext, mSelectedItems);
        mUseCaseListView.setAdapter(mUseCaseAdapter);
        mUseCaseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.d(TAG, "UseCaseList onItemClick mCurrentUseCase = " + position);
                mCurrentUseCase = position;
                updateTestItemView();
            }
        });
        mTestItemListView = (ListView) rootView.findViewById(R.id.testitemresultlist);
        initTestItemList();*/
        return rootView;
    }

    private void updateTestItemView() {
        if(mSelectedItems == null){
            mCurrentTestItems = null;
        }else{
            if(mSelectedItems.size() > 0){
                if(mCurrentUseCase > mSelectedItems.size() - 1){
                    mCurrentUseCase = 0;
                }
                mCurrentTestItems = mSelectedItems.get(mCurrentUseCase).getChildren();
            }else{
                mCurrentTestItems = null;
            }
        }
        mTestItemAdapter = new ResultsTestItemAdapter(mContext, mCurrentTestItems);
        mTestItemListView.setAdapter(mTestItemAdapter);
        //mTestItemAdapter.notifyDataSetChanged();
    }

    private void initTestItemList() {
        if(mSelectedItems == null){
            mCurrentTestItems = null;
        }else{
            if(mSelectedItems.size() > 0){
                if(mCurrentUseCase > mSelectedItems.size() - 1){
                    mCurrentUseCase = 0;
                }
                mCurrentTestItems = mSelectedItems.get(mCurrentUseCase).getChildren();
            }else{
                mCurrentTestItems = null;
            }
        }
        mTestItemAdapter = new ResultsTestItemAdapter(mContext, mCurrentTestItems);
        mTestItemListView.setAdapter(mTestItemAdapter);
    }

    @Override
    public void selectedUseCaseChangeNofify() {
        mUseCaseAdapter.notifyData();
    }
}
