package com.ckt.ckttestassistant.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ckt.ckttestassistant.interfaces.OnItemClickListener;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.testitems.TestItemBase;

import java.util.ArrayList;

/**
 * Created by ckt on 18-1-30.
 */

public class TestItemListAdapter extends RecyclerView.Adapter<TestItemListAdapter.TestItemListHolder>{
    private static final String TAG = "TestItemListAdapter";
    private final ArrayList<TestItemBase> mAllItems;
    private ArrayList<TestItemBase> mSelectedItems;
    private OnItemClickListener mItemClickListener;
    private UpdateShowPanelListener mUpdateShowPanelListener;
    private boolean mIsShowButton = false;
    private int mOldCheckedPosition = -1;

    public interface UpdateShowPanelListener{
        /**
         *
         * @param info
         */
        void updateShowPanel(ArrayList<TestItemBase> info);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        LogUtils.d(TAG, "setOnItemClickListener");
        this.mItemClickListener = listener;
    }

    public void setUpdateShowPanelListener(UpdateShowPanelListener listener){
        this.mUpdateShowPanelListener = listener;
    }

    public TestItemListAdapter(ArrayList<TestItemBase> allTestItems, ArrayList<TestItemBase> selectedTestItems, boolean b) {
        this.mAllItems = allTestItems;
        this.mSelectedItems = selectedTestItems;
        this.mIsShowButton = b;
    }

    @Override
    public TestItemListAdapter.TestItemListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtils.d(TAG,"onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.testitemlist_item_layout, parent, false);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new TestItemListAdapter.TestItemListHolder(view);
    }

    @Override
    public void onBindViewHolder(TestItemListAdapter.TestItemListHolder holder, final int position) {
        LogUtils.d(TAG,"onBindViewHolder");

        if(mAllItems != null && position < mAllItems.size()){
            holder.mTitle.setText(mAllItems.get(position).getTitle());
            if(mAllItems.get(position).isChecked()){
                holder.mTitle.setBackgroundColor(Color.GREEN);
            }else{
                holder.mTitle.setBackgroundColor(Color.TRANSPARENT);
            }
            final int index = position;
            holder.mItemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    LogUtils.d(TAG, "click mItemView");
                    if(position != mOldCheckedPosition){
                        if(mOldCheckedPosition > -1){
                            mAllItems.get(mOldCheckedPosition).setIsChecked(false);
                            notifyItemChanged(mOldCheckedPosition);
                        }
                        mAllItems.get(position).setIsChecked(true);
                        notifyItemChanged(position);
                        mOldCheckedPosition = position;
                    }
                    if(mItemClickListener != null){
                        mItemClickListener.onItemClick(index);
                    }
                }
            });
            if(mIsShowButton){
                holder.mAdd.setVisibility(View.VISIBLE);
                holder.mAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mUpdateShowPanelListener != null){
                            if(mSelectedItems != null){
                                mSelectedItems.add(mAllItems.get(index));
                            }
                            mUpdateShowPanelListener.updateShowPanel(mSelectedItems);
                        }
                    }
                });
            } else {
                holder.mAdd.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mAllItems != null ? mAllItems.size() : 0;
    }

    public class TestItemListHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private Button mAdd;
        private View mItemView;
        public TestItemListHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            LogUtils.d(TAG,"TestItemListHolder construction");
            mTitle = (TextView) mItemView.findViewById(R.id.title);
            mAdd = (Button) mItemView.findViewById(R.id.addtestitem);
        }
    }
}
