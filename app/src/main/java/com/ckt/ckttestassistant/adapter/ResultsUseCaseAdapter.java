package com.ckt.ckttestassistant.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by ckt on 18-2-8.
 */

public class ResultsUseCaseAdapter extends BaseAdapter{
    private static final String TAG = "ResultsUseCaseAdapter";
    private ArrayList<TestBase> mItems = null;
    private Context mContext;
    private int mOldCheckedPosition = 0;

    public ResultsUseCaseAdapter(Context context, ArrayList<TestBase> items) {
        mItems = items;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LogUtils.d(TAG, "getView");
        ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.result_usecase_list_item, null, true);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.d(TAG, "click convertView");
                    if(position != mOldCheckedPosition){
                        if(mOldCheckedPosition > -1){
                            mItems.get(mOldCheckedPosition).setChecked(false);
                            //notifyItemChanged(mOldCheckedPosition);
                        }
                        mItems.get(position).setChecked(true);
                        //notifyItemChanged(position);
                        notifyDataSetChanged();
                        mOldCheckedPosition = position;
                    }
                }
            });
            mHolder.title = (TextView) convertView.findViewById(R.id.title);
            mHolder.status = (TextView) convertView.findViewById(R.id.status);

            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        if(mItems.get(position).isChecked()){
            convertView.setBackgroundColor(Color.GREEN);
        }else{
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        String title = mItems.get(position).getTitle();
        String status = mItems.get(position).getFailTimes() == 0 ? "success" : "fail";
        mHolder.title.setText(title);
        mHolder.status.setText(status);
        return convertView;
    }

    private class ViewHolder {
        private TextView title;
        private TextView status;
    }
}
