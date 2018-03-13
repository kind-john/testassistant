package com.ckt.ckttestassistant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;


import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.interfaces.OnTreeTestBaseClickListener;

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
    protected List<TestBase> mTestBases = new ArrayList<>();
    protected LayoutInflater mInflater;

    /**
     * 存储所有的TestBase
     */
    protected List<TestBase> mAllTestBases = new ArrayList<>();

    /**
     * 点击的回调接口
     */
    private OnTreeTestBaseClickListener onTreeTestBaseClickListener;
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
    public TreeRecyclerAdapter(RecyclerView mTree, Context context, List<TestBase> datas,
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
        mAllTestBases = TreeHelper.getSortedTestBases(datas, defaultExpandLevel);
        /**
         * 过滤出可见的TestBase
         */
        mTestBases = TreeHelper.filterVisibleTestBase(mAllTestBases);
        mInflater = LayoutInflater.from(context);
    }

    /**
     *
     * @param mTree
     * @param context
     * @param datas
     * @param defaultExpandLevel
     *            默认展开几级树
     */
    public TreeRecyclerAdapter(RecyclerView mTree, Context context, List<TestBase> datas,
                           int defaultExpandLevel) {
        this(mTree,context,datas,defaultExpandLevel,-1,-1);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        TestBase TestBase = mTestBases.get(position);
//        convertView = getConvertView(TestBase, position, convertView, parent);
        // 设置内边距
        holder.itemView.setPadding(TestBase.getLevel() * 30, 3, 3, 3);
        /**
         * 设置节点点击时，可以展开以及关闭,将事件继续往外公布
         */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandOrCollapse(position);
                if (onTreeTestBaseClickListener != null) {
                    onTreeTestBaseClickListener.onClick(mTestBases.get(position),
                            position);
                }
            }
        });
        onBindViewHolder(TestBase,holder,position);
    }

    @Override
    public int getItemCount() {
        return mTestBases.size();
    }

    /**
     * 清除掉之前数据并刷新  重新添加
     * @param mlists
     * @param defaultExpandLevel 默认展开几级列表
     */
    public void addDataAll(List<TestBase> mlists,int defaultExpandLevel){
        mAllTestBases.clear();
        addData(-1,mlists,defaultExpandLevel);
    }

    /**
     * 在指定位置添加数据并刷新 可指定刷新后显示层级
     * @param index
     * @param mlists
     * @param defaultExpandLevel 默认展开几级列表
     */
    public void addData(int index,List<TestBase> mlists,int defaultExpandLevel){
        this.defaultExpandLevel = defaultExpandLevel;
        notifyData(index,mlists);
    }

    /**
     * 在指定位置添加数据并刷新
     * @param index
     * @param mlists
     */
    public void addData(int index,List<TestBase> mlists){
        notifyData(index,mlists);
    }

    /**
     * 添加数据并刷新
     * @param mlists
     */
    public void addData(List<TestBase> mlists){
        addData(mlists,defaultExpandLevel);
    }

    /**
     * 添加数据并刷新 可指定刷新后显示层级
     * @param mlists
     * @param defaultExpandLevel
     */
    public void addData(List<TestBase> mlists,int defaultExpandLevel){
        this.defaultExpandLevel = defaultExpandLevel;
        notifyData(-1,mlists);
    }

    /**
     * 添加数据并刷新
     * @param TestBase
     */
    public void addData(TestBase TestBase){
        addData(TestBase,defaultExpandLevel);
    }

    /**
     * 添加数据并刷新 可指定刷新后显示层级
     * @param TestBase
     * @param defaultExpandLevel
     */
    public void addData(TestBase TestBase,int defaultExpandLevel){
        List<TestBase> TestBases = new ArrayList<>();
        TestBases.add(TestBase);
        this.defaultExpandLevel = defaultExpandLevel;
        notifyData(-1,TestBases);
    }

    /**
     * 刷新数据
     * @param index
     * @param mListTestBases
     */
    private void notifyData(int index,List<TestBase> mListTestBases){
        for (int i = 0; i < mListTestBases.size(); i++) {
            TestBase TestBase = mListTestBases.get(i);
            TestBase.getChildren().clear();
            TestBase.iconExpand = iconExpand;
            TestBase.iconNoExpand = iconNoExpand;
        }
        for (int i = 0; i < mAllTestBases.size(); i++) {
            TestBase TestBase = mAllTestBases.get(i);
            TestBase.getChildren().clear();
        }
        if (index != -1){
            mAllTestBases.addAll(index,mListTestBases);
        }else {
            mAllTestBases.addAll(mListTestBases);
        }
        /**
         * 对所有的TestBase进行排序
         */
        mAllTestBases = TreeHelper.getSortedTestBases(mAllTestBases, defaultExpandLevel);
        /**
         * 过滤出可见的TestBase
         */
        mTestBases = TreeHelper.filterVisibleTestBase(mAllTestBases);
        //刷新数据
        notifyDataSetChanged();
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
            if (!n.isLeaf())
            {
                n.setExpand(!n.isExpand());
                mTestBases = TreeHelper.filterVisibleTestBase(mAllTestBases);
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
