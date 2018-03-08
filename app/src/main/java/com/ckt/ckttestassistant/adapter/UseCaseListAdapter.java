package com.ckt.ckttestassistant.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.interfaces.OnItemClickListener;
import com.ckt.ckttestassistant.usecases.UseCaseBase;
import com.ckt.ckttestassistant.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by ckt on 18-1-30.
 */

public class UseCaseListAdapter extends RecyclerView.Adapter<UseCaseListAdapter.UseCaseListHolder>{
    private static final String TAG = "UseCaseListAdapter";
    private final Context mContext;
    private final ArrayList<UseCaseBase> mAllItems;
    private final ArrayList<UseCaseBase> mSelectedItems;
    private UpdateShowPanelListener mUpdateShowPanelListener;
    private OnItemClickListener mItemClickListener;
    private int mSelectedPosition = -1;

    public interface UpdateShowPanelListener{
        /**
         *
         * @param index
         */
        void updateShowPanelForAdd(int index);
    }
    public UseCaseListAdapter(Context context, ArrayList<UseCaseBase> allItems, ArrayList<UseCaseBase> selectedItems) {
        this.mContext = context;
        this.mAllItems = allItems;
        this.mSelectedItems = selectedItems;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public void setUpdateShowPanelListener(UpdateShowPanelListener listener){
        this.mUpdateShowPanelListener = listener;
    }

    @Override
    public UseCaseListAdapter.UseCaseListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtils.d(TAG,"onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usecaselist_item_layout, parent, false);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new UseCaseListHolder(view);
    }

    @Override
    public void onBindViewHolder(UseCaseListAdapter.UseCaseListHolder holder, final int position) {
        LogUtils.d(TAG,"onBindViewHolder");

        if(mAllItems != null && position < mAllItems.size()){
            holder.mTitle.setText(mAllItems.get(position).getTitle());
            holder.mTimes.setText(String.valueOf(mAllItems.get(position).getTimes()));
            if(mSelectedPosition == position){
                holder.mTitle.setBackgroundColor(Color.GREEN);
            }
            final int index = position;
            holder.mTitle.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    mSelectedPosition = position;
                    notifyItemChanged(mSelectedPosition);
                    if(mItemClickListener != null){
                        mItemClickListener.onItemClick(index);
                    }
                }
            });
            holder.mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUpdateShowPanelListener != null) {
                        /*if(mSelectedItems != null) {
                            mSelectedItems.add(mAllItems.get(index));
                        }*/
                        mUpdateShowPanelListener.updateShowPanelForAdd(index);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mAllItems != null ? mAllItems.size() : 0;
    }

    public class UseCaseListHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private EditText mTimes;
        private Button mAdd;
        public UseCaseListHolder(View itemView) {
            super(itemView);
            LogUtils.d(TAG,"UseCaseListHolder construction");
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mTimes = (EditText) itemView.findViewById(R.id.times);
            mAdd = (Button) itemView.findViewById(R.id.addusecase);

        }
    }
}
