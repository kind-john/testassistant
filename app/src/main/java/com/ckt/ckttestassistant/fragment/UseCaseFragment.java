package com.ckt.ckttestassistant.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.ckt.ckttestassistant.interfaces.OnItemClickListener;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.usecases.UseCaseBase;
import com.ckt.ckttestassistant.adapter.TestItemListAdapter;
import com.ckt.ckttestassistant.adapter.UseCaseListAdapter;

import java.util.ArrayList;

/**
 * Created by ckt on 18-1-30.
 */

public class UseCaseFragment extends Fragment implements UseCaseManager.UseCaseChangeObserver,
        UseCaseManager.SelectedUseCaseChangeObserver{
    private static final String TAG = "UseCaseFragment";
    private Handler mHandler = null;
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
    private TestItemListAdapter mTestItemListAdapter;
    private Button mStartTestButton;

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate");
        mContext = getActivity().getApplicationContext();
        mUseCaseManager = UseCaseManager.getInstance(mContext);
        mUseCaseManager.init(mHandler);
        mUseCaseManager.addUseCaseChangeObserver(this);
        mUseCaseManager.addSelectedUseCaseChangeObserver(this);
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
        mUseCaseManager.getSelectedUseCaseFromXml(); //及时与数据同步
        if(needStartTest()){
            mUseCaseManager.startExecute();
        }
        mUseCaseTextView.setText(mShowPanelInfo.toString());
        mStartTestButton = (Button) rootView.findViewById(R.id.starttest);
        mStartTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存数据，为了重启或者中断之后能继续执行
                mUseCaseManager.saveSelectedUseCaseToXml(mSelectedItems);
                mUseCaseManager.startExecute();
                mStartTestButton.setClickable(false);
            }
        });
        mDeleteButton = (Button) rootView.findViewById(R.id.delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                if(mSelectedItems != null && !mSelectedItems.isEmpty()){
                    mSelectedItems.remove(mSelectedItems.size() - 1);
                }
                generateShowPanelString(mSelectedItems);
                mUseCaseTextView.setText(mShowPanelInfo.toString());
            }
        });
        /*mSaveButton = (Button) rootView.findViewById(R.id.save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                //mUseCaseManager.saveUseCaseToXml(mContext, mSelectedItems, "usecase");
                LogUtils.d(TAG, "save onClick");
                mUseCaseManager.saveSelectedUseCaseToXml(mSelectedItems);
            }
        });*/
        mUseCaseList = (RecyclerView) rootView.findViewById(R.id.usecaselist);
        mUseCaseTestItemList = (RecyclerView) rootView.findViewById(R.id.testitemlist);
        mAdapter = new UseCaseListAdapter(mContext, mAllItems, mSelectedItems);
        mAdapter.setUpdateShowPanelListener(new UseCaseListAdapter.UpdateShowPanelListener() {
            @Override
            public void updateShowPanelForAdd(int index) {
                setShowPanelForAdd(index);
            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
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

    private boolean needStartTest() {
        if(!mUseCaseManager.getTestStatus()){
            return false;
        }
        boolean need = false;
        if(mSelectedItems != null && !mSelectedItems.isEmpty()){
            for (UseCaseBase uc : mSelectedItems){
                if(uc.getTimes() != uc.getCompletedTimes()){
                    need = true;
                }
                for (TestItemBase ti : uc.getTestItems()){
                    if(ti.getTimes() != ti.getCompletedTimes()){
                        need = true;
                    }
                }
            }
        }
        if(!need){
            mUseCaseManager.setTestStatus(false);
        }
        return need;
    }

    private void initTestItemList() {
        LogUtils.d(TAG, "initTestItemList");
        mTestItemListAdapter = new TestItemListAdapter(
                mCurrentUseCase == null ? null : mCurrentUseCase.getTestItems(),
                null, false);
        mTestItemListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                TestItemBase ti = mCurrentUseCase.getTestItems().get(pos);
                LogUtils.d(TAG, "test item title clicked :"+pos+" : "+ti.getClass().getName());
                ti.showPropertyDialog(mContext);
            }
        });
        mUseCaseTestItemList.setHasFixedSize(true);
        mUseCaseTestItemList.setLayoutManager(new LinearLayoutManager(mContext));
        mUseCaseTestItemList.addItemDecoration(new CktItemDecoration(mContext,
                LinearLayoutManager.VERTICAL,
                CktItemDecoration.DECORATION_TYPE_USECASE_TESTITEM));
        mUseCaseTestItemList.setAdapter(mTestItemListAdapter);
    }

    private void updateTestItemList() {
        LogUtils.d(TAG, "updateTestItemList");
        mTestItemListAdapter = new TestItemListAdapter(
                mCurrentUseCase == null ? null : mCurrentUseCase.getTestItems(),
                null, false);
        mTestItemListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                TestItemBase ti = mCurrentUseCase.getTestItems().get(pos);
                LogUtils.d(TAG, "test item title clicked :"+pos+" : "+ti.getClass().getName());
                ti.showPropertyDialog(getActivity());
            }
        });
        mUseCaseTestItemList.setAdapter(mTestItemListAdapter);
    }

    public void setShowPanelForAdd(int index){
        mUseCaseManager.updateSelectedUseCases(index);
        generateShowPanelString(mSelectedItems);
        mUseCaseTextView.setText(mShowPanelInfo.toString());
    }

    private void generateShowPanelString(ArrayList<UseCaseBase> selectItems) {
        if (selectItems != null && !selectItems.isEmpty()){
            mShowPanelInfo.delete(11, mShowPanelInfo.length());
            for (int i = 0; i < selectItems.size(); i++){
                mShowPanelInfo.append(" > " + selectItems.get(i).getTitle());
            }
        } else {
            //处理最后一个删除不了的问题
            mShowPanelInfo.delete(11, mShowPanelInfo.length());
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

    @Override
    public void selectedUseCaseChangeNofify() {
        generateShowPanelString(mSelectedItems);
        mUseCaseTextView.setText(mShowPanelInfo.toString());
    }
}
