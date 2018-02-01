package com.ckt.ckttestassistant.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.adapter.CktItemDecoration;
import com.ckt.ckttestassistant.usecases.CktUseCase;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.usecases.UseCaseBase;
import com.ckt.ckttestassistant.adapter.TestItemListAdapter;
import com.ckt.ckttestassistant.adapter.UseCaseListAdapter;

import java.util.ArrayList;

/**
 * Created by ckt on 18-1-30.
 */

public class UseCaseFragment extends Fragment implements UseCaseManager.UseCaseChangeObserver{
    private static final String TAG = "UseCaseFragment";
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate");
        mContext = getActivity().getApplicationContext();
        mUseCaseManager = UseCaseManager.getInstance(mContext);
        mUseCaseManager.init();
        mUseCaseManager.addUseCaseChangeObserver(this);
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
        mUseCaseTextView.setText(mShowPanelInfo.toString());
        mDeleteButton = (Button) rootView.findViewById(R.id.delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                /*int start = mShowPanelInfo.lastIndexOf(">") - 1;
                int end = mShowPanelInfo.length();
                LogUtils.d(TAG, "start = "+start+ "; end = "+end);
                mShowPanelInfo.delete(start, end);*/
                if(mSelectedItems != null){
                    mSelectedItems.remove(mSelectedItems.size() - 1);
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
            }
        });
        mUseCaseList = (RecyclerView) rootView.findViewById(R.id.usecaselist);
        mUseCaseTestItemList = (RecyclerView) rootView.findViewById(R.id.testitemlist);
        mAdapter = new UseCaseListAdapter(mContext, mAllItems, mSelectedItems);
        mAdapter.setOnItemClickListener(new UseCaseListAdapter.OnItemClickListener() {
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
        initTestItemList();
        return rootView;
    }

    private void initTestItemList() {
        LogUtils.d(TAG, "initTestItemList");
        TestItemListAdapter adapter = new TestItemListAdapter(
                mCurrentUseCase == null ? null : mCurrentUseCase.getTestItems(),
                null, false);
        mUseCaseTestItemList.setHasFixedSize(true);
        mUseCaseTestItemList.setLayoutManager(new LinearLayoutManager(mContext));
        mUseCaseTestItemList.addItemDecoration(new CktItemDecoration(mContext,
                LinearLayoutManager.VERTICAL,
                CktItemDecoration.DECORATION_TYPE_USECASE_TESTITEM));
        mUseCaseTestItemList.setAdapter(adapter);
    }

    private void updateTestItemList() {
        LogUtils.d(TAG, "updateTestItemList");
        TestItemListAdapter adapter = new TestItemListAdapter(
                mCurrentUseCase == null ? null : mCurrentUseCase.getTestItems(),
                null, false);
        mUseCaseTestItemList.setAdapter(adapter);
    }

    public void setShowPanel(ArrayList<UseCaseBase> selectItems){
        mSelectedItems = selectItems;
        generateShowPanelString(mSelectedItems);
        mUseCaseTextView.setText(mShowPanelInfo.toString());
    }

    private void generateShowPanelString(ArrayList<UseCaseBase> selectItems) {
        if (selectItems != null && !selectItems.isEmpty()){
            mShowPanelInfo.delete(11, mShowPanelInfo.length());
            for (int i = 0; i < selectItems.size(); i++){
                mShowPanelInfo.append(" > " + selectItems.get(i).getTitle());
            }
        }
    }

    @Override
    public void allUseCaseChangeNofify(int position, int i) {
        LogUtils.d(TAG, "allUseCaseChangeNofify position = "+position+"; i="+i);
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
}
