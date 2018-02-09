package com.ckt.ckttestassistant.utils;

import android.content.Context;
import android.os.Environment;

/**
 * Created by ckt on 18-2-6.
 */

public final class MyConstants {
    private static final String TAG = "MyConstants";
    public static final String ROOT_DIR = "testauxiliarytool";
    public static final String PROGRESS_TITLE = "progresstitle";
    public static final String PROGRESS_MESSAGE = "progressmessage";
    public static final String UPDATE_USECASEFRAGMENT_POSOTION = "usecaselistitem";
    public static final String UPDATE_USECASEFRAGMENT_TYPE = "usecaselisttype";
    public static final int UPDATE_PROGRESS = 0;
    public static final int UPDATE_PROGRESS_TITLE = 1;
    public static final int UPDATE_PROGRESS_MESSAGE = 2;
    public static final int UPDATE_USECASEFRAGMENT_USECASELIST = 3;
    public static final int UPDATE_SELECTEDUSECASES_UI = 4;
    public static final String PREF_TEST_STATUS = "test_status";

    /**
     * @Description 私有化构造方法
     */
    private MyConstants() {
    }
}
