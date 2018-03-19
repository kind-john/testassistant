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
    public static final String UPDATE_USECASEFRAGMENT_POSITION = "usecaselistitem";
    public static final String UPDATE_USECASEFRAGMENT_TYPE = "usecaselisttype";

    public static final String TEST_RESULT_EXCEL_DIR = "/mnt/sdcard/ckttestassistant";

    public static final int UPDATE_PROGRESS = 0;
    public static final int UPDATE_PROGRESS_TITLE = 1;
    public static final int UPDATE_PROGRESS_MESSAGE = 2;
    public static final int UPDATE_PROGRESS_CLOSE = 3;
    public static final int UPDATE_USECASEFRAGMENT_USECASELIST = 4;
    public static final int UPDATE_SELECTEDUSECASES_UI = 5;
    public static final String PREF_TEST_STATUS = "test_status";
    public static final String PREF_CURRENT_EXCEL_FILR = "current_excel";

    public static final String XMLTAG_ROOT = "usecases";
    public static final String XMLTAG_ID = "id";
    public static final String XMLTAG_USECASE = "usecase";
    public static final String XMLTAG_USECASE_CLASSNAME = "usecase_classname";
    public static final String XMLTAG_USECASE_SN = "usecase_sn";
    public static final String XMLTAG_USECASE_TITLE = "usecase_title";
    public static final String XMLTAG_USECASE_TIMES = "usecase_times";
    public static final String XMLTAG_USECASE_FAILTIMES = "usecase_failtimes";
    public static final String XMLTAG_USECASE_COMPLETEDTIMES = "usecase_completedtimes";
    public static final String XMLTAG_USECASE_SELECTED = "usecase_selected";

    public static final String XMLTAG_USECASE_DELAY = "usecase_delay";

    public static final String XMLTAG_TESTITEM = "testitem";
    public static final String XMLTAG_TESTITEM_CLASSNAME = "testitem_classname";
    public static final String XMLTAG_TESTITEM_SN = "testitem_sn";
    public static final String XMLTAG_TESTITEM_TITLE = "testitem_title";
    public static final String XMLTAG_TESTITEM_TIMES = "testitem_times";
    public static final String XMLTAG_TESTITEM_FAILTIMES = "testitem_failtimes";
    public static final String XMLTAG_TESTITEM_COMPLETEDTIMES = "testitem_completedtimes";
    public static final String XMLTAG_TESTITEM_SELECTED = "testitem_selected";
    public static final String XMLTAG_TESTITEM_DELAY = "testitem_delay";
    public static final String XMLTAG_TESTITEM_USECASESN = "testitem_usecase_sn";
    public static final String XMLTAG_TESTITEM_USECASEID = "testitem_usecase_id";
    public static final String SUCCESS = "pass";
    public static final String FAIL = "fail";

    public static final String BROWSER_ACTION = "android.intent.action.VIEW";
    public static final int BROWSER_REQUESTCODE = 1;
    public static final String CAMERA_CAPTURE_POINT = "camera capture";
    public static final String CAMERA_RECORDVIDEO_POINT = "camera record video";
    public static final String CAMERA_SWITCH_FB_POINT = "camera switch fb";
    public static final String CAMERA_SWITCH_FLASH_POINT = "camera switch flashlight";
    public static final String CAMERA_FOCUS_POINT = "camera focus";
    public static final String CAMERA_SETTINGS_POINT = "camera open settings";
    public static final String CAMERA_HDR_POINT = "camera HDR";
    public static final String CAMERA_PANORAMA_POINT = "camera panorama";
    public static final String CAMERA_ZOOM_IN_POINT = "camera zoom in";
    public static final String CAMERA_ZOOM_OUT_POINT = "camera zoom out";
    public static final String CAMERA_VIEW_PHOTO_POINT = "camera view photo";
    public static final String CAMERA_PHOTOMODE_POINT = "camera photo mode";
    public static final String CAMERA_VIDEOMODE_POINT = "camera video mode";

    public static final int MAX_TRY = 5;
    /**
     * @Description 私有化构造方法
     */
    private MyConstants() {
    }
}
