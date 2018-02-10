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

    public static final String XMLTAG_USECASE = "usecase";
    public static final String XMLTAG_USECASE_SN = "usecase_sn";
    public static final String XMLTAG_USECASE_TITLE = "usecase_title";
    public static final String XMLTAG_USECASE_TIMES = "usecase_times";
    public static final String XMLTAG_USECASE_FAILTIMES = "usecase_failtimes";
    public static final String XMLTAG_USECASE_COMPLETEDTIMES = "usecase_completedtimes";
    public static final String XMLTAG_USECASE_SELECTED = "usecase_selected";
    public static final String XMLTAG_USECASE_DELAY = "usecase_delay";

    public static final String XMLTAG_TESTITEM = "testitem";
    public static final String XMLTAG_TESTITEM_SN = "testitem_sn";
    public static final String XMLTAG_TESTITEM_TITLE = "testitem_title";
    public static final String XMLTAG_TESTITEM_TIMES = "testitem_times";
    public static final String XMLTAG_TESTITEM_FAILTIMES = "testitem_failtimes";
    public static final String XMLTAG_TESTITEM_COMPLETEDTIMES = "testitem_completedtimes";
    public static final String XMLTAG_TESTITEM_SELECTED = "testitem_selected";

    public static final String XMLTAG_TESTITEM_DELAY = "testitem_delay";


    /**
     * @Description 私有化构造方法
     */
    private MyConstants() {
    }
}
