package com.ckt.ckttestassistant.utils;

import android.graphics.Point;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ckt on 18-3-14.
 */

public class JSONUtils {
    private static final String TAG = "JSONUtils";

    public static void parseJsonObject(JSONObject jsonObject, String key, HashMap<String, Point> points){
        JSONObject posJson = jsonObject.optJSONObject(key);
        if(posJson != null){
            int x = posJson.optInt("x");
            int y = posJson.optInt("y");
            Point pos = new Point(x, y);
            if(points == null){
                LogUtils.d(TAG, "point is null ,new it");
                points = new HashMap<String, Point>();
            }
            if(points.containsKey(key)){
                LogUtils.e(TAG, "重复定义坐标 ： "+key);
            }else{
                LogUtils.d(TAG, key+" position : x = "+x+", y = "+y);
                points.put(key, pos);
            }
        }else{
            LogUtils.e(TAG, "do not configure "+key+"!!!");
        }
    }

    public static void parseJsonArray(String json, HashMap<String, Point> points){
        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (jsonObject != null) {
                    parseJsonObject(jsonObject, PointConstants.CAMERA_CAPTURE_POINT, points);
                    parseJsonObject(jsonObject, PointConstants.CAMERA_RECORDVIDEO_POINT, points);
                    parseJsonObject(jsonObject, PointConstants.CAMERA_SWITCH_FB_POINT, points);
                    parseJsonObject(jsonObject, PointConstants.CAMERA_SWITCH_FLASH_POINT, points);
                    parseJsonObject(jsonObject, PointConstants.CAMERA_FOCUS_POINT, points);
                    parseJsonObject(jsonObject, PointConstants.CAMERA_SETTINGS_POINT, points);
                    parseJsonObject(jsonObject, PointConstants.CAMERA_HDR_POINT, points);
                    parseJsonObject(jsonObject, PointConstants.CAMERA_PANORAMA_POINT, points);
                    parseJsonObject(jsonObject, PointConstants.CAMERA_ZOOM_IN_POINT, points);
                    parseJsonObject(jsonObject, PointConstants.CAMERA_ZOOM_OUT_POINT, points);
                    parseJsonObject(jsonObject, PointConstants.CAMERA_VIEW_PHOTO_POINT, points);
                    parseJsonObject(jsonObject, PointConstants.CAMERA_PHOTOMODE_POINT, points);
                    parseJsonObject(jsonObject, PointConstants.CAMERA_VIDEOMODE_POINT, points);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
