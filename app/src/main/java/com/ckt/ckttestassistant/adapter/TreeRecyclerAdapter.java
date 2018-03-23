package com.ckt.ckttestassistant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;


import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.interfaces.OnSetParametersListener;
import com.ckt.ckttestassistant.interfaces.OnTreeTestBaseClickListener;
import com.ckt.ckttestassistant.interfaces.UpdateShowPanelListener;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.usecases.UseCaseBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by houjian on 2018-3-12.
 */
public abstract class TreeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    protected Context mContext;
    /**
     * 存储所有可见的TestBase
     */
    protected ArrayList<TestBase> mTestBases = new ArrayList<>();
    protected LayoutInflater mInflater;

    /**
     * 存储所有的TestBase
     */
    protected ArrayList<TestBase> mAllTestBases = new ArrayList<>();

    /**
     * 点击的回调接口
     */
    protected OnTreeTestBaseClickListener onTreeTestBaseClickListener;

    public void setOnTreeTestBaseClickListener(
            OnTreeTestBaseClickListener onTreeTestBaseClickListener) {
        this.onTreeTestBaseClickListener = onTreeTestBaseClickListener;
    }

    protected UpdateShowPanelListener mUpdateShowPanelListener = null;

    /** 展开与关闭的图片*/
    public void setUpdateShowPanelListener(UpdateShowPanelListener updateShowPanelListener) {
        this.mUpdateShowPanelListener = updateShowPanelListener;
    }

    protected OnSetParametersListener mOnSetParametersListener;

    public void setOnSetParametersListener(OnSetParametersListener setParametersListener) {
        this.mOnSetParametersListener = setParametersListener;
    }

    /**
     * 默认不展开
     */
    private int defaultExpandLevel = 0;
    private int iconExpand = -1,iconNoExpand = -1;
    public TreeRecyclerAdapter(Context context, ArrayList<TestBase> datas,
                           int defaultExpandLevel, int iconExpand, int iconNoExpand) {

        this.iconExpand = iconExpand;
        this.iconNoExpand = iconNoExpand;

        this.defaultExpandLevel = defaultExpandLevel;
        mContext = context;
        /*
          对所有的TestBase进行排序
         */
        mAllTestBases = datas;
        /*
          过滤出可见的TestBase
         */
        mTestBases = filterVisibleTestBase(mAllTestBases);
        mInflater = LayoutInflater.from(context);
    }

    private ArrayList<TestBase> filterVisibleTestBase(ArrayList<TestBase> tbs) {
        ArrayList<TestBase> results = new ArrayList<TestBase>();
        if(tbs != null && !tbs.isEmpty()) {
            for (int index = 0; index < tbs.size(); index++) {
                TestBase tb = tbs.get(index);
                if (tb.isRoot() || tb.isParentExpand()){
                    results.add(tb);
                    addVisibleChildTestBase(results, tb);
                }
            }
        }
        return results;
    }

    private static void addVisibleChildTestBase(List<TestBase> result, TestBase tb) {
        if(tb.isExpand()){
            List<TestBase> children = tb.getChildren();
            if(children != null && !children.isEmpty()){
                for (TestBase child : children){
                    result.add(child);
                    if (child instanceof UseCaseBase) {
                        addVisibleChildTestBase(result, child);
                    }
                }
            }
        }
    }

    /**
     *
     * @param context
     * @param datas
     * @param defaultExpandLevel
     *            默认展开几级树
     */
    public TreeRecyclerAdapter(Context context, ArrayList<TestBase> datas,
                           int defaultExpandLevel) {
        this(context,datas,defaultExpandLevel,-1,-1);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        TestBase TestBase = mTestBases.get(position);
        // 设置内边距
        holder.itemView.setPadding(TestBase.getLevel() * 30, 3, 3, 3);
        /*
          设置节点点击时，可以展开以及关闭,将事件继续往外公布
         */

        onBindViewHolder(TestBase,holder,position);
    }

    @Override
    public int getItemCount() {
        return mTestBases.size();
    }

    /**
     * 刷新数据
     */
    public void notifyData(){
        mTestBases = filterVisibleTestBase(mAllTestBases);
        //刷新数据
        notifyDataSetChanged();
    }

    /**
     * 获取排序后所有节点
     * @return
     */
    public ArrayList<TestBase> getAllTestBases(){
        if(mAllTestBases == null)
            mAllTestBases = new ArrayList<TestBase>();
        return mAllTestBases;
    }

    /**
     * 相应ListView的点击事件 展开或关闭某节点
     *
     * @param tb
     */
    public void expandOrCollapse(TestBase tb) {
        if (tb != null) {// 排除传入参数错误异常
            if (!tb.isLeaf())
            {
                tb.setExpand(!tb.isExpand());
                mTestBases = filterVisibleTestBase(mAllTestBases);
                notifyDataSetChanged();// 刷新视图
            }
        }
    }
    /**
     * 设置多选
     * @param TestBase
     * @param checked
     */
    protected void setChecked(final TestBase TestBase, boolean checked) {
        TestBase.setChecked(checked);
        setChildChecked(TestBase, checked);
        if(TestBase.getParent()!=null)
            setTestBaseParentChecked(TestBase.getParent(), checked);
        notifyDataSetChanged();
    }
    /**
     * 设置是否选中
     * @param TestBase
     * @param checked
     */
    public void setChildChecked(TestBase TestBase,boolean checked){
        if(!TestBase.isLeaf()){
            TestBase.setChecked(checked);
            for (TestBase childrenTestBase : TestBase.getChildren()) {
                setChildChecked(childrenTestBase, checked);
            }
        }else{
            TestBase.setChecked(checked);
        }
    }

    private void setTestBaseParentChecked(TestBase TestBase,boolean checked){
        if(checked){
            TestBase.setChecked(checked);
            if(TestBase.getParent()!=null)
                setTestBaseParentChecked(TestBase.getParent(), checked);
        }else{
            List<TestBase> childrens = TestBase.getChildren();
            boolean isChecked = false;
            for (TestBase children : childrens) {
                if(children.isChecked()){
                    isChecked = true;
                }
            }
            //如果所有自节点都没有被选中 父节点也不选中
            if(!isChecked){
                TestBase.setChecked(checked);
            }
            if(TestBase.getParent()!=null)
                setTestBaseParentChecked(TestBase.getParent(), checked);
        }
    }

    public abstract void onBindViewHolder(TestBase TestBase,RecyclerView.ViewHolder holder,final int position);
}
