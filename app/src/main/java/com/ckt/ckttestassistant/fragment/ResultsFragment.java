package com.ckt.ckttestassistant.fragment;

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
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.adapter.ResultsTestItemAdapter;
import com.ckt.ckttestassistant.adapter.ResultsUseCaseAdapter;
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
    private ArrayList<UseCaseBase> mSelectedItems;
    private ListView mUseCaseListView;
    private ListView mTestItemListView;
    private ResultsUseCaseAdapter mUseCaseAdapter;
    private int mCurrentUseCase = 0;
    private ResultsTestItemAdapter mTestItemAdapter;
    private ArrayList<TestItemBase> mCurrentTestItems = null;

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mUseCaseManager = UseCaseManager.getInstance(mContext);
        mUseCaseManager.init(mHandler, false);
        mUseCaseManager.addSelectedUseCaseChangeObserver(this);
        mSelectedItems = mUseCaseManager.getSelectItems();
        if(mSelectedItems != null && !mSelectedItems.isEmpty()){
            mCurrentTestItems = mSelectedItems.get(mCurrentUseCase).getTestItems();
        }
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_results_layout, container, false);
        mUseCaseManager.getSelectedUseCaseFromXml();
        mUseCaseListView = (ListView) rootView.findViewById(R.id.testedusecaselist);
        initUseCaseListFocus(mSelectedItems);
        mUseCaseAdapter = new ResultsUseCaseAdapter(mContext, mSelectedItems);
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
        initTestItemList();
        return rootView;
    }

    private void updateTestItemView() {
        mCurrentTestItems = mSelectedItems.get(mCurrentUseCase).getTestItems();
        mTestItemAdapter = new ResultsTestItemAdapter(mContext, mCurrentTestItems);
        mTestItemListView.setAdapter(mTestItemAdapter);
        //mTestItemAdapter.notifyDataSetChanged();
    }

    private void initTestItemList() {
        mCurrentTestItems = mSelectedItems.get(mCurrentUseCase).getTestItems();
        mTestItemAdapter = new ResultsTestItemAdapter(mContext, mCurrentTestItems);
        mTestItemListView.setAdapter(mTestItemAdapter);
    }

    @Override
    public void selectedUseCaseChangeNofify() {
        mUseCaseAdapter.notifyDataSetChanged();
    }
}
