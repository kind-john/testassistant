package com.ckt.ckttestassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by ckt on 18-2-8.
 */

public class ResultsTestItemAdapter extends BaseAdapter{
    private static final String TAG = "ResultsTestItemAdapter";
    private Context mContext;
    private ArrayList<TestItemBase> mItems;

    public ResultsTestItemAdapter(Context context, ArrayList<TestItemBase> items) {
        mContext = context;
        mItems = items;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LogUtils.d(TAG, "getView");
        ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.result_testitem_list_item, null, true);
            mHolder.title = (TextView) convertView.findViewById(R.id.title);
            mHolder.status = (TextView) convertView.findViewById(R.id.status);

            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
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
