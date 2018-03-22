package com.ckt.ckttestassistant.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ckt.ckttestassistant.interfaces.OnItemClickListener;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.TestCategory;

import java.util.ArrayList;

/**
 * Created by ckt on 18-1-30.
 */

public class TestCategoryListAdapter extends RecyclerView.Adapter<TestCategoryListAdapter.TestCategoryListHolder>{
    private static final String TAG = "TestCategoryListAdapter";
    private final ArrayList<TestCategory> mItems;
    private OnItemClickListener mItemClickListener;
    private int mOldCheckedPosition = 0;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mItemClickListener = listener;
    }
    public TestCategoryListAdapter(Context context, ArrayList<TestCategory> items) {
        Context mContext = context;
        this.mItems = items;
    }

    @Override
    public TestCategoryListAdapter.TestCategoryListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtils.d(TAG,"onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.testcategorylist_item_layout, parent, false);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new TestCategoryListHolder(view);
    }

    @Override
    public void onBindViewHolder(TestCategoryListAdapter.TestCategoryListHolder holder, final int position) {
        LogUtils.d(TAG,"onBindViewHolder");

        if(mItems != null && position < mItems.size()){
            holder.mTitle.setText(mItems.get(position).getTitle());
            if(mItems.get(position).isChecked()){
                holder.mTitle.setBackgroundResource(R.drawable.background_of_listitem_focus);
            }else{
                holder.mTitle.setBackgroundColor(Color.TRANSPARENT);
            }
            holder.mItemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    if(position != mOldCheckedPosition){
                        if(mOldCheckedPosition > -1){
                            mItems.get(mOldCheckedPosition).setChecked(false);
                        }
                        mItems.get(position).setChecked(true);
                        notifyDataSetChanged();
                        mOldCheckedPosition = position;
                    }
                    if(mItemClickListener != null){
                        mItemClickListener.onItemClick(position);
                        //v.setBackgroundColor(Color.BLUE);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size(): 0;
    }

    public class TestCategoryListHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private View mItemView;
        public TestCategoryListHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            LogUtils.d(TAG,"TestCategoryListHolder construction");
            mTitle = (TextView) mItemView.findViewById(R.id.title);
        }
    }
}
