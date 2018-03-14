package com.ckt.ckttestassistant.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.usecases.UseCaseBase;
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
                notifyDataSetChanged();// 刷新视图
            }
        }
    }

    private static void addVisibleChildTestBase(List<TestBase> result, TestBase tb) {
        if(tb.isExpand()){
            List<TestBase> children = tb.getChildren();
            if(children != null && !children.isEmpty()){
                for (TestBase child : children){
                        result.add(child);
                        addVisibleChildTestBase(result, child);
                }
            }
        }
    }

    private List<TestBase> filterVisibleTestBase(List<TestBase> tbs) {
        List<TestBase> result = new ArrayList<TestBase>();

        for (TestBase tb : tbs) {
            // 如果为跟节点，或者上层目录为展开状态
            if (tb.isRoot() || tb.isParentExpand()) {
                result.add(tb);
                addVisibleChildTestBase(result, tb);

            }
        }
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
                expandOrCollapse(position);
            }
        });
        viewHolder.mTitle.setText(tb.getTitle());
        boolean status = tb.isPassed();
        viewHolder.mStatus.setText(String.valueOf(status));
        if(status){
            viewHolder.mStatus.setBackgroundColor(Color.GREEN);
        }else{
            viewHolder.mStatus.setBackgroundColor(Color.RED);
        }
        return convertView;
    }

    public void setFocus(int position) {
        LogUtils.d(TAG, "setFocus");
        int index = 0;
        int size = mTestBases.size();
        if(position > 0 && position < size){
            index = position;
        }
        expandOrCollapse(index);
    }

    private boolean isAllChildren(List<TestBase> children) {
        if(children != null && !children.isEmpty()){
            for (int i = 0; i < children.size(); i++){
                if(children.get(i) instanceof UseCaseBase){
                    return false;
                }
            }
        }
        return true;
    }

    private final class UseCaseListHolder
    {
        private TextView mTitle;
        private TextView mStatus;
    }
}
