package com.ckt.ckttestassistant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.interfaces.OnSetParametersListener;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.usecases.UseCaseBase;

import java.util.ArrayList;

/**
 * Created by ckt on 18-3-21.
 */

public class UseCaseTreeRecyclerAdapter extends TreeRecyclerAdapter {
    private static final String TAG = "UseCaseTreeRecyclerAdapter";


    public UseCaseTreeRecyclerAdapter(Context context, ArrayList<TestBase> datas, int defaultExpandLevel, int iconExpand, int iconNoExpand) {
        super(context, datas, defaultExpandLevel, iconExpand, iconNoExpand);
    }

    public UseCaseTreeRecyclerAdapter(Context context, ArrayList<TestBase> datas, int defaultExpandLevel) {
        super(context, datas, defaultExpandLevel);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usecaselist_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TestBase tb, RecyclerView.ViewHolder holder, final int position) {

        final MyViewHolder viewHolder = (MyViewHolder) holder;

        viewHolder.mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUpdateShowPanelListener != null) {
                    mUpdateShowPanelListener.updateShowPanelForAdd(tb);
                }
            }
        });

        viewHolder.mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSetParametersListener != null) {
                    mOnSetParametersListener.setItemParameters(tb);
                }
            }
        });
        viewHolder.mTitle.setText(tb.getTitle());

        if(tb.getLevel() != 0){
            viewHolder.mAdd.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.mAdd.setVisibility(View.VISIBLE);
        }

        viewHolder.mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            TestBase tb = mTestBases.get(position);
            expandOrCollapse(tb);
            }
        });
        if (tb instanceof UseCaseBase) {
            viewHolder.mIcon.setVisibility(View.VISIBLE);

            if (tb.isExpand()) {
                viewHolder.mIcon.setImageResource(R.drawable.noexpend);
            } else {
                viewHolder.mIcon.setImageResource(R.drawable.expend);
            }
        } else {
            viewHolder.mIcon.setVisibility(View.GONE);
        }
    }
    /**
     * 设置多选
     * @param index
     */
    private void setChecked(int index) {
        if(mTestBases != null && !mTestBases.isEmpty()){
            for (int i = 0; i < mTestBases.size(); i++){
                TestBase tb = mTestBases.get(i);
                if(i == index){
                    tb.setChecked(true);
                }else{
                    tb.setChecked(false);
                }
            }
        }
        notifyDataSetChanged();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView mIcon;
        private TextView mTitle;
        private Button mAdd;
        public MyViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mAdd = (Button) itemView.findViewById(R.id.addusecase);
        }
    } 
}
