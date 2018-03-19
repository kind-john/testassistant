package com.ckt.ckttestassistant.utils;

import android.content.Context;
import android.os.Environment;

/**
 * Created by ckt on 18-2-6.
 */

public final class MyConstants {

    private static final String TAG = "MyConstants";

    public static final String ROOT_DIR = "testauxiliarytool";

    public static final String TEST_RESULT_EXCEL_DIR = "/mnt/sdcard/ckttestassistant";

    public static final String PREF_TEST_STATUS = "test_status";

    public static final String PREF_CURRENT_EXCEL_FILR = "current_excel";

    public static final String SUCCESS = "pass";

    public static final String FAIL = "fail";

    public static final int BROWSER_REQUESTCODE = 1;

    public static final int MAX_TRY = 5;

    public static final String REBOOT_FLAG = "reboot_flag";

    /**
     * @Description 私有化构造方法
     */
    private MyConstants() {
    }
}
