package com.ckt.ckttestassistant.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ckt on 18-3-15.
 */

public class ParseDurationFromLogs {
    private static final String TAG = "ParseDurationFromLogs";

    public static ArrayList<Long> computeDuration(String path, String tagStart, String tagEnd){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return computeDuration(br, tagStart, tagEnd);
    }

    public static ArrayList<Long> computeDuration(File file, String tagStart, String tagEnd){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return computeDuration(br, tagStart, tagEnd);
    }

    public static ArrayList<Long> computeDuration(BufferedReader br, String tagStart, String tagEnd){
        ArrayList<Long> results = new ArrayList<Long>();
        if(br != null){
            long startTime = 0;
            long endTime = 0;
            String line;
            try {
                while ((line = br.readLine()) != null){
                    int startIndex = line.indexOf(tagStart);
                    if(startIndex >= 0){
                        startTime = getTimeByTag(line);
                        LogUtils.d(TAG, "startTime = "+startTime);
                    }
                    int endIndex = line.indexOf(tagEnd);
                    if(endIndex >= 0){
                        endTime = getTimeByTag(line);
                        LogUtils.d(TAG, "endTime = "+endTime);
                    }
                    if(endTime > startTime){
                        results.add(endTime - startTime);
                        startTime = 0;
                        endTime = 0;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    private static long getTimeByTag(String str) {
        String timeReg = "\\d{2}:\\d{2}:\\d{2}.\\d{3}";
        String dateReg = "^\\d{2}-\\d{2}";
        Pattern timePattern = Pattern.compile(timeReg);
        Matcher timeMatcher = timePattern.matcher(str);
        String timeStr = "";
        Pattern datePattern = Pattern.compile(dateReg);
        Matcher dateMatcher = datePattern.matcher(str);
        String dateStr = "";
        int month = 1,days = 1,hours = 0,minutes = 0,seconds = 0,milliseconds = 0;
        if(timeMatcher.find()) {
            timeStr = timeMatcher.group();
            LogUtils.d(TAG, "Found value: " + timeStr);
        }
        if(!timeStr.equals("")){
            hours = Integer.parseInt(timeStr.substring(0,2));
            minutes = Integer.parseInt(timeStr.substring(3,5));
            seconds = Integer.parseInt(timeStr.substring(6,8));
            milliseconds = Integer.parseInt(timeStr.substring(9));
            LogUtils.d(TAG, "hours:"+hours);
            LogUtils.d(TAG, "minutes:"+minutes);
            LogUtils.d(TAG, "seconds:"+seconds);
            LogUtils.d(TAG, "milliseconds:"+milliseconds);
        }

        if(dateMatcher.find()) {
            dateStr = dateMatcher.group();
            LogUtils.d(TAG, "Found value: " + dateStr);
        }
        if(!dateStr.equals("")){
            month = Integer.parseInt(dateStr.substring(0,2));
            days = Integer.parseInt(dateStr.substring(3));
            LogUtils.d(TAG, "month:"+month);
            LogUtils.d(TAG, "day:"+days);
        }
        Date d = new Date(1970, month, days, hours, minutes, seconds);
        return d.getTime()+milliseconds;
        /*long h = duration/(60*60*1000);
        long m = (duration%(60*60*1000))/(60*1000);
        long s = (duration%(60*1000))/1000;
        long ms = duration%1000;
        LogUtils.d(TAG, "h:"+h);
        LogUtils.d(TAG, "m:"+m);
        LogUtils.d(TAG, "s:"+s);
        LogUtils.d(TAG, "ms:"+ms);*/
    }
}
