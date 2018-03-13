package com.ckt.ckttestassistant.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.TestBase;

import java.util.List;

/**
 * Created by ckt on 18-3-12.
 */

public class TestItemTreeAdapter extends TreeListViewAdapter {
    private static final String TAG = "UseCaseTreeAdapter";

    public TestItemTreeAdapter(Context context, List<TestBase> datas, int defaultExpandLevel, int iconExpand, int iconNoExpand) {
        super(context, datas, defaultExpandLevel, iconExpand, iconNoExpand);
    }

    public TestItemTreeAdapter(Context context, List<TestBase> datas,
                              int defaultExpandLevel) {
        super(context, datas, defaultExpandLevel);
    }

    @Override
    public View getConvertView(final TestBase tb , final int position, View convertView, ViewGroup parent)
    {

        final TestItemListHolder viewHolder ;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.usecaselist_item_layout, parent, false);
            viewHolder = new TestItemListHolder();
            viewHolder.mTitle = (TextView) convertView
                    .findViewById(R.id.title);
            viewHolder.mAdd = (Button) convertView
                    .findViewById(R.id.addusecase);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (TestItemListHolder) convertView.getTag();
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandOrCollapse(position);
                if(!tb.isChecked()){
                    setChecked(position);
                }
            }
        });
        viewHolder.mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUpdateShowPanelListener != null) {
                        /*if(mSelectedItems != null) {
                            mSelectedItems.add(mAllItems.get(index));
                        }*/
                    mUpdateShowPanelListener.updateShowPanelForAdd(mTestBases.get(position));
                }
            }
        });
        viewHolder.mTitle.setText(tb.getTitle());
        if(tb.getLevel() != 0){
            viewHolder.mAdd.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.mAdd.setVisibility(View.VISIBLE);
        }

        if(tb.isChecked()){
            viewHolder.mTitle.setBackgroundColor(Color.GREEN);
        }else{
            viewHolder.mTitle.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    private final class TestItemListHolder
    {
        private TextView mTitle;
        private Button mAdd;
    }
}
