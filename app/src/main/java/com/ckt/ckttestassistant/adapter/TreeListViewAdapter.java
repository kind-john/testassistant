package com.ckt.ckttestassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.ckt.ckttestassistant.TestBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckt on 18-3-9.
 */

public abstract class TreeListViewAdapter extends BaseAdapter
{

    protected Context mContext;
    /**
     * 存储所有可见的TestBase
     */
    protected List<TestBase> mTestBases = new ArrayList<>();
    protected LayoutInflater mInflater;

    /**
     * 存储所有的TestBase
     */
    protected List<TestBase> mAllTestBases = new ArrayList<>();

    /**
     * 点击的回调接口
     */
    protected OnTreeTestBaseClickListener onTreeTestBaseClickListener = null;
    protected UpdateShowPanelListener mUpdateShowPanelListener = null;
    /**
     * 默认不展开
     */
    private int defaultExpandLevel = 0;
    /** 展开与关闭的图片*/
    private int iconExpand = -1,iconNoExpand = -1;

    public void setOnTreeTestBaseClickListener(
            OnTreeTestBaseClickListener onTreeTestBaseClickListener) {
        this.onTreeTestBaseClickListener = onTreeTestBaseClickListener;
    }

    public void notifyData() {
        mTestBases = TreeHelper.filterVisibleTestBase(mAllTestBases);
        notifyDataSetChanged();
    }

    public interface OnTreeTestBaseClickListener {
        void onClick(TestBase tb);
    }

    public interface UpdateShowPanelListener{
        /**
         *
         * @param index
         */
        void updateShowPanelForAdd(TestBase tb);
    }
    public void setUpdateShowPanelListener(UpdateShowPanelListener listener){
        this.mUpdateShowPanelListener = listener;
    }
    public TreeListViewAdapter(Context context, List<TestBase> datas,
                               int defaultExpandLevel, int iconExpand, int iconNoExpand) {

        this.iconExpand = iconExpand;
        this.iconNoExpand = iconNoExpand;

        for (TestBase TestBase:datas){
            //TestBase.getChildren().clear();
            TestBase.iconExpand = iconExpand;
            TestBase.iconNoExpand = iconNoExpand;
        }

        this.defaultExpandLevel = defaultExpandLevel;
        mContext = context;
        /**
         * 对所有的TestBase进行排序
         */
        mAllTestBases = datas;//TreeHelper.getSortedTestBases(datas, defaultExpandLevel);
        /**
         * 过滤出可见的TestBase
         */
        mTestBases = TreeHelper.filterVisibleTestBase(mAllTestBases);
        mInflater = LayoutInflater.from(context);
        /**
         * 设置节点点击时，可以展开以及关闭；并且将ItemClick事件继续往外公布
         */

    }

    /**
     *
     * @param context
     * @param datas
     * @param defaultExpandLevel
     *            默认展开几级树
     */
    public TreeListViewAdapter(Context context, List<TestBase> datas,
                               int defaultExpandLevel) {
        this(context, datas, defaultExpandLevel, -1, -1);
    }

    /**
     * 获取排序后所有节点
     * @return
     */
    public List<TestBase> getAllTestBases(){
        if(mAllTestBases == null)
            mAllTestBases = new ArrayList<TestBase>();
        return mAllTestBases;
    }

    /**
     * 相应ListView的点击事件 展开或关闭某节点
     *
     * @param position
     */
    public void expandOrCollapse(int position) {
        TestBase n = mTestBases.get(position);

        if (n != null) {// 排除传入参数错误异常
            if (!n.isLeaf()) {
                n.setExpand(!n.isExpand());
                mTestBases = TreeHelper.filterVisibleTestBase(mAllTestBases);
                notifyDataSetChanged();// 刷新视图
            }
        }
    }

    public void expand(int position) {
        TestBase n = mTestBases.get(position);

        if (n != null) {// 排除传入参数错误异常
            if (!n.isLeaf()) {
                n.setExpand(true);
                mTestBases = TreeHelper.filterVisibleTestBase(mAllTestBases);
                notifyDataSetChanged();// 刷新视图
            }
        }
    }

    @Override
    public int getCount()
    {
        return mTestBases.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mTestBases.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TestBase TestBase = mTestBases.get(position);
        convertView = getConvertView(TestBase, position, convertView, parent);
        // 设置内边距
        convertView.setPadding(TestBase.getLevel() * 30, 3, 3, 3);
        return convertView;
    }

    /**
     * 设置多选
     * @param index
     */
    protected void setChecked(int index) {
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

    public abstract View getConvertView(TestBase TestBase, int position,
                                        View convertView, ViewGroup parent);

}

