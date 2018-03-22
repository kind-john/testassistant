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
import android.widget.ListView;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.adapter.ResultsTestItemAdapter;
import com.ckt.ckttestassistant.adapter.ResultsTreeAdapter;
import com.ckt.ckttestassistant.adapter.TreeListViewAdapter;
import com.ckt.ckttestassistant.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by ckt on 18-1-30.
 */

public class ResultsFragment extends Fragment implements UseCaseManager.SelectedUseCaseChangeObserver {
    private static final String TAG = "ResultsFragment";
    private Handler mHandler = null;
    private Context mContext;
    private ArrayList<TestBase> mSelectedItems;
    private ListView mTestItemListView;
    private ResultsTreeAdapter mUseCaseAdapter;
    private ResultsTestItemAdapter mTestItemAdapter;

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
        UseCaseManager mUseCaseManager = UseCaseManager.getInstance(mContext, mActivity);
        mUseCaseManager.init(mHandler, false);
        mUseCaseManager.addSelectedUseCaseChangeObserver(this);
        mSelectedItems = mUseCaseManager.getSelectItems();
        if(mSelectedItems != null && !mSelectedItems.isEmpty()){
            int mCurrentUseCase = 0;
            ArrayList<TestBase> mCurrentTestItems = mSelectedItems.get(mCurrentUseCase).getChildren();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_results_layout, container, false);
        ListView mUseCaseListView = (ListView) rootView.findViewById(R.id.testedusecaselist);
        mUseCaseAdapter = new ResultsTreeAdapter(mContext, mSelectedItems, 1);

        mUseCaseAdapter.setOnTreeTestBaseClickListener(new TreeListViewAdapter.OnTreeTestBaseClickListener() {
            @Override
            public void onClick(TestBase tb) {
                LogUtils.d(TAG, "onItemClick");
            }
        });
        mUseCaseListView.setAdapter(mUseCaseAdapter);
        return rootView;
    }

    @Override
    public void selectedUseCaseChangeNofify() {
        mUseCaseAdapter.notifyData();
    }
}
