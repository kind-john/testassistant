package com.ckt.ckttestassistant;

import com.ckt.ckttestassistant.usecases.UseCaseBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckt on 18-3-9.
 */

public abstract class TestBase  implements Cloneable {
    private static final int DEFAULT_TIMES = 1;
    /**
     * 设置开启 关闭的图片
     */
    public int iconExpand=-1, iconNoExpand = -1;

    protected String mTitle = "case";

    /**
     * 当前的级别
     */
    private int level = 0;

    /**
     * 是否展开
     */
    private boolean isExpand = false;
    
    /**
     * 下一级的子TestBase
     */
    protected ArrayList<TestBase> children = new ArrayList<>();

    protected int mTimes = DEFAULT_TIMES;
    protected int mCompletedTimes = 0;
    protected int mFailTimes = 0;
    protected String mClassName = "ClassName";
    protected int ID = -1;
    protected int SN = -1;
    /**
     * 父TestBase
     */
    protected TestBase mParent = null;
    /**
     * 是否被checked选中
     */
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public TestBase() {}

    public TestBase(String title) {
        super();
        this.mTitle = title;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String title)
    {
        this.mTitle = title;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public ArrayList<TestBase> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<TestBase> children) {
        this.children = children;
    }

    public TestBase getParent() {
        return mParent;
    }

    public void setParent(TestBase parent) {
        this.mParent = parent;
    }

    public int getCompletedTimes() {
        return mCompletedTimes;
    }

    public void setCompletedTimes(int completedTimes) {
        this.mCompletedTimes = completedTimes;
    }

    public int getTimes() {
        return mTimes;
    }

    public void setTimes(int times) {
        this.mTimes = times;
    }

    public int getFailTimes() {
        return mFailTimes;
    }

    public void setFailTimes(int failTimes) {
        this.mFailTimes = failTimes;
    }

    public String getClassName() {
        return mClassName;
    }

    public void setClassName(String className) {
        this.mClassName = className;
    }


    public int getSN() {
        return SN;
    }

    public void setSN(int sn) {
        this.SN = sn;
    }

    public void setID(int id) {
        this.ID = id;
    }

    public int getID() {
        return ID;
    }
    /**
     * 是否为跟节点
     *
     * @return
     */
    public boolean isRoot() {
        return mParent == null;
    }

    /**
     * 判断父节点是否展开
     *
     * @return
     */
    public boolean isParentExpand() {
        if (mParent == null)
            return false;
        return mParent.isExpand();
    }

    /**
     * 是否是叶子界点
     *
     * @return
     */
    public boolean isLeaf()
    {
        return children.size() == 0;
    }

    /**
     * 获取level
     */
    public int getLevel() {

        return mParent == null ? 0 : mParent.getLevel() + 1;
    }

    /**
     * 设置展开
     *
     * @param isExpand
     */
    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
        if (!isExpand) {

            for (TestBase tb : children) {
                tb.setExpand(isExpand);
            }
        }
    }
    @Override
    public TestBase clone() {
        TestBase clone = null;
        try {
            clone = (TestBase) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

    public abstract boolean task();
}
