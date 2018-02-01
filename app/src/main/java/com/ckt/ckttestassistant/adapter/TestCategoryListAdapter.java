package com.ckt.ckttestassistant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
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
    private final Context mContext;
    private final ArrayList<TestCategory> mItems;
    private OnItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mItemClickListener = listener;
    }
    public TestCategoryListAdapter(Context context, ArrayList<TestCategory> items) {
        this.mContext = context;
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
    public void onBindViewHolder(TestCategoryListAdapter.TestCategoryListHolder holder, int position) {
        LogUtils.d(TAG,"onBindViewHolder");

        if(mItems != null && position < mItems.size()){
            holder.mTitle.setText(mItems.get(position).getTitle());
            final int index = position;
            holder.mTitle.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    if(mItemClickListener != null){
                        mItemClickListener.onItemClick(index);
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
        public TestCategoryListHolder(View itemView) {
            super(itemView);
            LogUtils.d(TAG,"TestCategoryListHolder construction");
            mTitle = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
