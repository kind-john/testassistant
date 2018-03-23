package com.ckt.ckttestassistant.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckt on 18-3-13.
 */

public class ResultsTreeAdapter extends TreeListViewAdapter {
    private static final String TAG = "ResultsTreeAdapter";

    public ResultsTreeAdapter(Context context, List<TestBase> datas, int defaultExpandLevel, int iconExpand, int iconNoExpand) {
        super(context, datas, defaultExpandLevel, iconExpand, iconNoExpand);
    }

    public ResultsTreeAdapter(Context context, List<TestBase> datas,
                              int defaultExpandLevel) {
        super(context, datas, defaultExpandLevel);
    }

    @Override
    public void expandOrCollapse(int position) {
        int index = 0;
        int size = mTestBases.size();
        if(size <= 0){
            return ;
        }
        if(position > 0 && position < size){
            index = position;
        }
        TestBase n = mTestBases.get(index);
        if (n != null) {
            if (!n.isLeaf()) {
                n.setExpand(!n.isExpand());
                mTestBases = filterVisibleTestBase(mAllTestBases);
            }
        }
    }

    protected List<TestBase> filterVisibleTestBase(List<TestBase> tbs) {
        List<TestBase> result = tbs;
        return result;
    }
    @Override
    public View getConvertView(final TestBase tb , final int position, View convertView, ViewGroup parent)
    {
        LogUtils.d(TAG, "getConvertView");
        final ResultsTreeAdapter.UseCaseListHolder viewHolder ;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.result_usecase_list_item, parent, false);
            viewHolder = new ResultsTreeAdapter.UseCaseListHolder();
            viewHolder.mTitle = (TextView) convertView
                    .findViewById(R.id.title);
            viewHolder.mStatus = (TextView) convertView
                    .findViewById(R.id.status);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ResultsTreeAdapter.UseCaseListHolder) convertView.getTag();
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*expandOrCollapse(position);
                if (!tb.isChecked()) {
                    setChecked(position);
                }
                notifyDataSetChanged();*/
            }
        });
        viewHolder.mTitle.setText(tb.getTitle());
        if(tb.isChecked()){
            viewHolder.mTitle.setBackgroundResource(R.drawable.background_of_listitem_focus);
            //viewHolder.mTitle.setBackgroundColor(Color.GREEN);
        }else{
            viewHolder.mTitle.setBackgroundColor(Color.TRANSPARENT);
        }
        boolean status = tb.isPassed();
        viewHolder.mStatus.setText(String.valueOf(status));
        if(status){
            convertView.setBackgroundResource(R.drawable.background_of_pass_text);

        }else{
            convertView.setBackgroundResource(R.drawable.background_of_file_text);
        }
        return convertView;
    }

    public void setFocus(int position) {
        LogUtils.d(TAG, "setFocus");
        int index = 0;
        int size = mTestBases.size();
        if (size <= 0) {
            return;
        }
        if(position > 0 && position < size){
            index = position;
        }
        setChecked(index);
        expandOrCollapse(index);
        notifyDataSetChanged();
    }

    @Override
    public void notifyData() {
        mTestBases = filterVisibleTestBase(mAllTestBases);
        notifyDataSetChanged();
    }

    private final class UseCaseListHolder
    {
        private TextView mTitle;
        private TextView mStatus;
    }
}
