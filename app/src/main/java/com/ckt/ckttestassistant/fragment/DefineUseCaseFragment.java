package com.ckt.ckttestassistant.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ckt.ckttestassistant.CktTestAssistantMainActivity;
import com.ckt.ckttestassistant.adapter.CktItemDecoration;
import com.ckt.ckttestassistant.testitems.CktTestItem;
import com.ckt.ckttestassistant.testitems.WifiSwitchOn;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.TestCategory;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.adapter.TestCategoryListAdapter;
import com.ckt.ckttestassistant.adapter.TestItemListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ckt on 18-1-30.
 */

public class DefineUseCaseFragment extends Fragment {
    private static final String TAG = "DefineUseCaseFragment";
    private RecyclerView mTestCategoryList;
    private Context mContext;
    private ArrayList<TestCategory> mTestCategoryItems = new ArrayList<TestCategory>();
    private TestCategoryListAdapter mAdapter;
    private RecyclerView mTestItemList;
    private HashMap<String, ArrayList<TestItemBase>> mAllTestItems = new HashMap<String,ArrayList<TestItemBase>>();
    private ArrayList<TestItemBase> mSelectedTestItems = new ArrayList<TestItemBase>();
    private String[] mTestCategory = {
            "camera",
            "switch",
            "system",
            "telecom",
            "time length",
            "HW test",
            "current"
    };
    private int mCurrentType = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_defineusecase_layout, container, false);
        mTestCategoryList = (RecyclerView) rootView.findViewById(R.id.testcategorylist);
        mTestItemList = (RecyclerView) rootView.findViewById(R.id.testitemlist);
        mAdapter = new TestCategoryListAdapter(mContext, mTestCategoryItems);
        mAdapter.setOnItemClickListener(new TestCategoryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                LogUtils.d(TAG, "onItemClick");
                mCurrentType = pos;
                updateTestItemList();
            }
        });

        mTestCategoryList.setHasFixedSize(true);
        mTestCategoryList.setLayoutManager(new LinearLayoutManager(mContext));
        mTestCategoryList.addItemDecoration(new CktItemDecoration(mContext,
                LinearLayoutManager.VERTICAL,
                CktItemDecoration.DECORATION_TYPE_TESTCATEGORY));
        mTestCategoryList.setAdapter(mAdapter);
        initTestItemList();
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate");
        mContext = getActivity().getApplicationContext();
        for (int i = 0; i < mTestCategory.length; i++){
            mTestCategoryItems.add(new TestCategory(mTestCategory[i]));
            ArrayList<TestItemBase> itemList = new ArrayList<TestItemBase>();

            CktTestItem item1 = new CktTestItem("ckt test item");
            itemList.add(item1.ID,item1);
            WifiSwitchOn item2 = new WifiSwitchOn("wifi switch on");
            itemList.add(item2.ID,item2);

            mAllTestItems.put(mTestCategory[i], itemList);
        }
    }
    private void updateTestItemList() {
        LogUtils.d(TAG, "updateTestItemList");
        TestItemListAdapter adapter = new TestItemListAdapter(mAllTestItems.get(mTestCategory[mCurrentType]), mSelectedTestItems, true);
        adapter.setUpdateShowPanelListener(new TestItemListAdapter.UpdateShowPanelListener() {
            @Override
            public void updateShowPanel(ArrayList<TestItemBase> info) {
                ((CktTestAssistantMainActivity)getActivity()).setShowPanel(info);
            }
        });
        mTestItemList.setAdapter(adapter);
    }

    private void initTestItemList() {
        LogUtils.d(TAG, "initTestItemList");
        TestItemListAdapter adapter = new TestItemListAdapter(mAllTestItems.get(mTestCategory[mCurrentType]), mSelectedTestItems, true);
        adapter.setUpdateShowPanelListener(new TestItemListAdapter.UpdateShowPanelListener() {
            @Override
            public void updateShowPanel(ArrayList<TestItemBase> info) {
                ((CktTestAssistantMainActivity)getActivity()).setShowPanel(info);
            }
        });
        mTestItemList.setHasFixedSize(true);
        mTestItemList.setLayoutManager(new LinearLayoutManager(mContext));
        mTestItemList.addItemDecoration(new CktItemDecoration(mContext,
                LinearLayoutManager.VERTICAL,
                CktItemDecoration.DECORATION_TYPE_TESTCATEGORY_TESTITEM));
        mTestItemList.setAdapter(adapter);
    }
}
