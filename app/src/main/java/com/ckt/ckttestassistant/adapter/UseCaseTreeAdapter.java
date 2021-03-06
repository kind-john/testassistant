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
 * Created by ckt on 18-3-9.
 */

public class UseCaseTreeAdapter extends TreeListViewAdapter {
    private static final String TAG = "UseCaseTreeAdapter";

    public UseCaseTreeAdapter(Context context, List<TestBase> datas, int defaultExpandLevel, int iconExpand, int iconNoExpand) {
        super(context, datas, defaultExpandLevel, iconExpand, iconNoExpand);
    }

    private static void addVisibleChildTestBase(List<TestBase> result, TestBase tb) {
        if(tb.isExpand()){
            List<TestBase> children = tb.getChildren();
            if(children != null && !children.isEmpty()){
                for (TestBase child : children){
                    if(child instanceof UseCaseBase){
                        result.add(child);
                        addVisibleChildTestBase(result, child);
                    }
                }
            }
        }
    }

    @Override
    protected List<TestBase> filterVisibleTestBase(List<TestBase> allTestBases) {
        List<TestBase> result = new ArrayList<TestBase>();

        for (TestBase tb : allTestBases) {
            // 如果为跟节点，或者上层目录为展开状态
            if (tb.isRoot() || tb.isParentExpand()) {
                if(tb instanceof UseCaseBase){
                    result.add(tb);
                    addVisibleChildTestBase(result, tb);
                }

            }
        }
        return result;
    }

    public UseCaseTreeAdapter(Context context, List<TestBase> datas,
                              int defaultExpandLevel) {
        super(context, datas, defaultExpandLevel);
    }

    @Override
    public View getConvertView(final TestBase tb , final int position, View convertView, ViewGroup parent)
    {
        LogUtils.d(TAG, "getConvertView");
        final UseCaseListHolder viewHolder ;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.usecaselist_item_layout, parent, false);
            viewHolder = new UseCaseListHolder();
            viewHolder.mTitle = (TextView) convertView
                    .findViewById(R.id.title);
            viewHolder.mAdd = (Button) convertView
                    .findViewById(R.id.addusecase);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (UseCaseListHolder) convertView.getTag();
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandOrCollapse(position);
                if(!tb.isChecked()){
                    setChecked(position);
                }
                if(onTreeTestBaseClickListener != null){
                    TestBase lastTb = getLastMenu(tb);
                    if(lastTb != null){
                        onTreeTestBaseClickListener.onClick(lastTb);
                    }
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
                    mUpdateShowPanelListener.updateShowPanelForAdd(tb);
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
            viewHolder.mTitle.setBackgroundResource(R.drawable.background_of_listitem_focus);
            //viewHolder.mTitle.setBackgroundColor(Color.GREEN);
        }else{
            viewHolder.mTitle.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    public TestBase setFocus(int position) {
        LogUtils.d(TAG, "setFocus");
        TestBase back = null;
        int focus = 0;
        if (position > 0){
            focus = position;
        }
        if(mTestBases !=null && focus < mTestBases.size()){
            TestBase tb = mTestBases.get(focus);
            if(isAllChildren(tb.getChildren())){
                back = tb;
                setChecked(position);
            }else{
                tb.setExpand(true);
                mTestBases = filterVisibleTestBase(mAllTestBases);
                List<TestBase> children = tb.getChildren();
                if(children != null && !children.isEmpty()){
                    for (int i = 0; i < children.size(); i++){
                        TestBase child = children.get(i);
                        if(child instanceof UseCaseBase){
                            int index = mTestBases.indexOf(child);
                            back = setFocus(index);
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }
        return back;
    }

    private TestBase getLastMenu(TestBase tb) {
        TestBase result = null;
        if(tb != null){
            List<TestBase> children = tb.getChildren();

            if(children != null && !children.isEmpty()){
                if(isAllChildren(children)){
                    return tb;
                }else{
                    TestBase uc = findFirstUseCase(children);
                    result = getLastMenu(uc);

                }
                result = getLastMenu(children.get(0));
            }else{
                //result = tb;
                LogUtils.e(TAG, "error: usecase has no item!!!");
            }
        }
        return result;
    }

    private TestBase findFirstUseCase(List<TestBase> children) {
        for (int i = 0; i < children.size(); i++){
            if(children.get(i) instanceof UseCaseBase){
                return children.get(i);
            }
        }
        return null;
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

    private boolean hasNextMenu(TestBase tb) {
        List<TestBase> children = tb.getChildren();

        if(children != null && !children.isEmpty()){
            for (int i = 0; i < children.size(); i++){
                if(children.get(i) instanceof UseCaseBase){
                    return true;
                }
            }
        }
        return false;
    }

    private final class UseCaseListHolder
    {
        private TextView mTitle;
        private Button mAdd;
    }
}
