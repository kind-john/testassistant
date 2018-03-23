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
import android.widget.TextView;

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
    private ResultsTreeAdapter mUseCaseAdapter;
    private ListView mUseCaseListView;
    private TextView mEmptyView;

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
        UseCaseManager useCaseManager = UseCaseManager.getInstance(mContext, mActivity);
        useCaseManager.init(mHandler, false);
        useCaseManager.addSelectedUseCaseChangeObserver(this);
        mSelectedItems = useCaseManager.getSelectItems();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_results_layout, container, false);
        mUseCaseListView = (ListView) rootView.findViewById(R.id.testedusecaselist);
        mEmptyView = (TextView) rootView.findViewById(R.id.emptyview);
        mUseCaseAdapter = new ResultsTreeAdapter(mContext, mSelectedItems, 1);

        mUseCaseAdapter.setOnTreeTestBaseClickListener(new TreeListViewAdapter.OnTreeTestBaseClickListener() {
            @Override
            public void onClick(TestBase tb) {
                LogUtils.d(TAG, "onItemClick");
            }
        });
        mUseCaseListView.setAdapter(mUseCaseAdapter);
        mUseCaseListView.setEmptyView(mEmptyView);
        return rootView;
    }

    @Override
    public void selectedUseCaseChangeNofify() {
        mUseCaseAdapter.notifyData();
    }
}
