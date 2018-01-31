package com.ckt.ckttestassistant.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ckt on 18-1-30.
 */

public class CktItemDecoration extends RecyclerView.ItemDecoration {
    private final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    public static final int DECORATION_TYPE_USECASELIST = 0;
    public static final int DECORATION_TYPE_USECASE_TESTITEM = 1;
    public static final int DECORATION_TYPE_TESTCATEGORY = 2;
    public static final int DECORATION_TYPE_TESTCATEGORY_TESTITEM = 3;
    /**
     * 用于绘制间隔样式
     */
    private Drawable mDivider;
    /**
     * 列表的方向，水平/竖直
     */
    private int mOrientation;
    private int mDecorationType = 0;

    public CktItemDecoration(Context context, int orientation, int type) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
        mDecorationType = type;
    }

    private void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int padding_left = 0, padding_top = 0, padding_right = 0, padding_bottom = 0;
        switch (mDecorationType){
            case DECORATION_TYPE_USECASELIST:
                padding_left = 10;
                padding_top = 10;
                padding_right = 0;
                padding_bottom = 10;
                break;
            case DECORATION_TYPE_USECASE_TESTITEM:
                padding_left = 5;
                padding_top = 5;
                padding_right = 0;
                padding_bottom = 5;
                break;
            case DECORATION_TYPE_TESTCATEGORY:
                padding_left = 15;
                padding_top = 15;
                padding_right = 0;
                padding_bottom = 15;
                break;
            case DECORATION_TYPE_TESTCATEGORY_TESTITEM:
                padding_left = 4;
                padding_top = 4;
                padding_right = 0;
                padding_bottom = 4;
                break;
            default:
                break;
        }
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(padding_left, padding_top, padding_right,
                    mDivider.getIntrinsicHeight() + padding_bottom);
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }

    /**
     *
     * @param c
     * @param parent
     */
    private void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        switch (mDecorationType){
            case DECORATION_TYPE_USECASELIST:
                break;
            case DECORATION_TYPE_USECASE_TESTITEM:
                break;
            case DECORATION_TYPE_TESTCATEGORY:
                break;
            case DECORATION_TYPE_TESTCATEGORY_TESTITEM:
                break;
            default:
                break;
        }
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin +
                    Math.round(ViewCompat.getTranslationY(child));
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    /**
     *
     * @param c
     * @param parent
     */
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();
        switch (mDecorationType){
            case DECORATION_TYPE_USECASELIST:
                break;
            case DECORATION_TYPE_USECASE_TESTITEM:
                break;
            case DECORATION_TYPE_TESTCATEGORY:
                break;
            case DECORATION_TYPE_TESTCATEGORY_TESTITEM:
                break;
            default:
                break;
        }
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin +
                    Math.round(ViewCompat.getTranslationX(child));
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}

