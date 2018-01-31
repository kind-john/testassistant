package com.ckt.ckttestassistant.fragment;

import android.support.v4.app.Fragment;
import android.util.SparseArray;

/**
 * Created by ckt on 18-1-30.
 */

class FragmentFactory {
    private static SparseArray<Fragment> fragmentSparseArray = new SparseArray<>();

    public static Fragment getFragment(int position){
        Fragment fragment = fragmentSparseArray.get(position);
        if (fragment == null){
            switch (position){
                case 0:
                    fragment = new UseCaseFragment();
                    break;
                case 1:
                    fragment = new DefineUseCaseFragment();
                    break;
                case 2:
                    fragment = new ResultsFragment();
                    break;
            }
            fragmentSparseArray.put(position,fragment);
        }
        return fragment;
    }


}
