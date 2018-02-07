package com.ckt.ckttestassistant.fragment;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Created by ckt on 18-1-30.
 */

public class FragmentAdapter extends FragmentPagerAdapter {
    private final Handler mHandler;
    private String[] mTabTitles;
    public FragmentAdapter(FragmentManager fm, String[] tabTitles, Handler handler) {
        super(fm);
        mTabTitles = tabTitles;
        mHandler = handler;
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentFactory.getFragment(position, mHandler);
    }

    @Override
    public int getCount() {
        return mTabTitles == null ? 0 : mTabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }
}
