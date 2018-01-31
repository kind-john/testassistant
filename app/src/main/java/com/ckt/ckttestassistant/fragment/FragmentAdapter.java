package com.ckt.ckttestassistant.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Created by ckt on 18-1-30.
 */

public class FragmentAdapter extends FragmentPagerAdapter {
    private String[] mTabTitles;
    public FragmentAdapter(FragmentManager fm, String[] tabTitles) {
        super(fm);
        mTabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentFactory.getFragment(position);
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
