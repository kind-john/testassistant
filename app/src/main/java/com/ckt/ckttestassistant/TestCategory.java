package com.ckt.ckttestassistant;

/**
 * Created by ckt on 18-1-30.
 */

public class TestCategory {
    private String mTitle = "testitem";
    private boolean mIsChecked = false;

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean isChecked) {
        this.mIsChecked = isChecked;
    }

    public TestCategory(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }
}
