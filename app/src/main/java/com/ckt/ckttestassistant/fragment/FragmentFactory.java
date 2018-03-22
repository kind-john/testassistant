package com.ckt.ckttestassistant.fragment;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

/**
 * Created by ckt on 18-1-30.
 */

public class FragmentFactory {
    private static SparseArray<Fragment> fragmentSparseArray = new SparseArray<>();

    public static Fragment getFragment(int position, Handler handler){
        Fragment fragment = fragmentSparseArray.get(position);
        if (fragment == null){
            switch (position){
                case 0:
                    fragment = new UseCaseFragment();
                    ((UseCaseFragment)fragment).setHandler(handler);
                    break;
                case 1:
                    fragment = new DefineUseCaseFragment();
                    ((DefineUseCaseFragment)fragment).setHandler(handler);
                    break;
                case 2:
                    fragment = new ResultsFragment();
                    ((ResultsFragment)fragment).setHandler(handler);
                    break;
            }
            fragmentSparseArray.put(position,fragment);
        }
        return fragment;
    }


}
