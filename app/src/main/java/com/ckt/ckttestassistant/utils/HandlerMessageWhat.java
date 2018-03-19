package com.ckt.ckttestassistant.utils;

/**
 * Created by ckt on 18-3-19.
 */

public class HandlerMessageWhat {
    private HandlerMessageWhat() {
    }
    public static final String PROGRESS_TITLE = "progresstitle";

    public static final String PROGRESS_MESSAGE = "progressmessage";

    public static final String UPDATE_USECASEFRAGMENT_POSITION = "usecaselistitem";

    public static final String UPDATE_USECASEFRAGMENT_TYPE = "usecaselisttype";

    public static final int UPDATE_PROGRESS = 0;

    public static final int UPDATE_PROGRESS_TITLE = 1;

    public static final int UPDATE_PROGRESS_MESSAGE = 2;

    public static final int UPDATE_PROGRESS_CLOSE = 3;

    public static final int UPDATE_USECASEFRAGMENT_USECASELIST = 4;

    public static final int UPDATE_SELECTEDUSECASES_UI = 5;
}
