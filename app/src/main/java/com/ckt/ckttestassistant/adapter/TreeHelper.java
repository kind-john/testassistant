package com.ckt.ckttestassistant.adapter;

import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.usecases.UseCaseBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckt on 18-3-9.
 */

public class TreeHelper {
    /**
     * 传入TestBase  返回排序后的TestBase
     *
     * @param datas
     * @param defaultExpandLevel
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static List<TestBase> getSortedTestBases(List<TestBase> datas,
                                            int defaultExpandLevel) {
        List<TestBase> result = new ArrayList<TestBase>();
        // 拿到根节点
        List<TestBase> rootTestBases = getRootTestBases(datas);
        // 排序以及设置TestBase间关系
        for (TestBase tb : rootTestBases) {
            addTestBase(result, tb, defaultExpandLevel, 1);
        }
        return result;
    }

    /**
     * 过滤出所有可见的TestBase
     *
     * @param TestBases
     * @return
     */
    public static List<TestBase> filterVisibleTestBase(List<TestBase> tbs) {
        List<TestBase> result = new ArrayList<TestBase>();

        for (TestBase tb : tbs) {
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

    private static List<TestBase> getRootTestBases(List<TestBase> TestBases) {
        List<TestBase> root = new ArrayList<TestBase>();
        for (TestBase tb : TestBases) {
            if (tb.isRoot())
                root.add(tb);
        }
        return root;
    }

    /**
     * 把一个节点上的所有的内容都挂上去
     */
    private static  void addTestBase(List<TestBase> tbs, TestBase tb,
                                     int defaultExpandLeval, int currentLevel) {
        tbs.add(tb);

        if (tb.isLeaf())
            return;
        for (int i = 0; i < tb.getChildren().size(); i++) {
            addTestBase(tbs, tb.getChildren().get(i), defaultExpandLeval,
                    currentLevel + 1);
        }
    }
}
