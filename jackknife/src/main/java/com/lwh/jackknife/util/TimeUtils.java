/*
 * Copyright (C) 2017 The JackKnife Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS IN ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lwh.jackknife.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public final static String FORMAT_YEAR = "yyyy";

    public final static String FORMAT_MONTH = "MM";

    public final static String FORMAT_DAY = "dd";

    public final static String FORMAT_HOUR = "HH";

    public final static String FORMAT_MINUTE = "mm";

    public final static String FORMAT_SECOND = "ss";

    public final static String FORMAT_MILLISECOND = "SSS";

    public final static String FORMAT_YEAR_MONTH = FORMAT_YEAR + FORMAT_MONTH;

    public final static String FORMAT_YEAR_MONTH_2 = FORMAT_YEAR + "." + FORMAT_MONTH;

    public final static String FORMAT_YEAR_MONTH_3 = FORMAT_YEAR + " " + FORMAT_MONTH;

    public final static String FORMAT_YEAR_MONTH_4 = FORMAT_YEAR + "年" + FORMAT_MONTH + "月";

    public final static String FORMAT_MONTH_DAY = FORMAT_MONTH + FORMAT_DAY;

    public final static String FORMAT_MONTH_DAY_2 = FORMAT_MONTH + "." + FORMAT_DAY;

    public final static String FORMAT_MONTH_DAY_3 = FORMAT_MONTH + " " + FORMAT_DAY;

    public final static String FORMAT_MONTH_DAY_4 = FORMAT_MONTH + "月" + FORMAT_DAY + "日";

    public final static String FORMAT_DATE = FORMAT_YEAR + FORMAT_MONTH + FORMAT_DAY;

    public final static String FORMAT_DATE_2 = FORMAT_YEAR_MONTH_2 + "." + FORMAT_DAY;

    public final static String FORMAT_DATE_3 = FORMAT_YEAR_MONTH_4 + FORMAT_DAY + "日";

    public final static String FORMAT_HOUR_MINUTE = FORMAT_HOUR + FORMAT_MINUTE;

    public final static String FORMAT_HOUR_MINUTE_2 = FORMAT_HOUR + ":" + FORMAT_MINUTE;

    public final static String FORMAT_HOUR_MINUTE_3 = FORMAT_HOUR + "时" + FORMAT_MINUTE + "分";

    public final static String FORMAT_TIME = FORMAT_HOUR + FORMAT_MINUTE + FORMAT_SECOND;

    public final static String FORMAT_TIME_2 = FORMAT_HOUR + ":" + FORMAT_MINUTE + ":" + FORMAT_SECOND;

    public final static String FORMAT_TIME_3 = FORMAT_HOUR + "时" + FORMAT_MINUTE + "分" + FORMAT_SECOND + "秒";

    public final static String FORMAT_DATE_TIME = FORMAT_DATE + FORMAT_TIME;

    public final static String FORMAT_DATE_TIME_2 = FORMAT_DATE_2 + " " + FORMAT_TIME_2;

    public final static String FORMAT_DATE_TIME_3 = FORMAT_DATE_3 + FORMAT_TIME_3;

    public static final int SECONDS_IN_MINUTE = 60;

    public static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * 60;

    public static final int SECONDS_IN_DAY = SECONDS_IN_HOUR * 24;

    public static final int SECONDS_IN_WEEK = SECONDS_IN_DAY * 7;

    public static final int SECONDS_IN_MONTH = SECONDS_IN_DAY * 30;

    public static final int SECONDS_IN_NON_LEAP_YEAR = SECONDS_IN_DAY * 365;

    public static final int SECONDS_IN_LEAP_YEAR = SECONDS_IN_DAY * 366;

    public static final int MILLISECONDS_IN_SECOND = 1000;

    public static final int MILLISECONDS_IN_MINUTE = MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE;

    public static final int MILLISECONDS_IN_HOUR = MILLISECONDS_IN_SECOND * SECONDS_IN_HOUR;

    public static final int MILLISECONDS_IN_DAY = MILLISECONDS_IN_SECOND * SECONDS_IN_DAY;

    public static final int MILLISECONDS_IN_WEEK = MILLISECONDS_IN_SECOND * SECONDS_IN_WEEK;

    private TimeUtils(){
    }

    public static String date2str(Date data, String formatType) {
        return new SimpleDateFormat(formatType, Locale.CHINA).format(data);
    }

    public static String long2str(long currentTime,String formatType){
        Date date = long2date(currentTime, formatType);
        return date2str(date, formatType);
    }

    public static Date str2date(String strTime, String formatType){
        Date date = null;
        try {
            date = new SimpleDateFormat(formatType, Locale.CHINA).parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date long2date(long currentTime, String formatType){
        Date dateOld = new Date(currentTime);
        String sDateTime = date2str(dateOld, formatType);
        return str2date(sDateTime, formatType);
    }

    public static long str2long(String strTime, String formatType){
        Date date = str2date(strTime, formatType);
        if (date == null) {
            return 0;
        } else {
            return date2long(date);
        }
    }

    public static int getDaysOfMonth(int year, int month) {
        int result;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                result = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                result = 30;
                break;
            default:
                if (isLeapYear(year)) {
                    result = 29;
                }else{
                    result = 28;
                }
                break;
        }
        return result;
    }

    public static long date2long(Date date) {
        return date.getTime();
    }

    public static String getDescTime(long timestamp) {
        long currentTime = System.currentTimeMillis();
        //时间间隔，与现在时间相差秒数
        long timeGap = (currentTime - timestamp) / 1000;
        String timeStr;
        //我们作为一般的平年处理
        if (timeGap > SECONDS_IN_LEAP_YEAR) {
            timeStr = timeGap / SECONDS_IN_LEAP_YEAR + "年前";
            // 1个月以上
        } else if (timeGap > SECONDS_IN_MONTH) {
            timeStr = timeGap / SECONDS_IN_MONTH + "个月前";
            // 1天以上
        } else if (timeGap > SECONDS_IN_DAY) {
            timeStr = timeGap / SECONDS_IN_DAY + "天前";
            // 1小时-24小时
        } else if (timeGap > SECONDS_IN_HOUR) {
            timeStr = timeGap / SECONDS_IN_HOUR + "小时前";
            // 1分钟-59分钟
        } else if (timeGap > SECONDS_IN_MINUTE) {
            timeStr = timeGap / SECONDS_IN_MINUTE + "分钟前";
            // 1秒钟-59秒钟
        } else {
            timeStr = "刚刚";
        }
        return timeStr;
    }

    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    public static String formatTime(long milliSecs) {
        StringBuffer sb = new StringBuffer();
        long m = milliSecs / (60 * 1000);
        sb.append(NumberUtils.zeronize(String.valueOf(m), 2));
        sb.append(":");
        long s = (milliSecs % (60 * 1000)) / 1000;
        sb.append(NumberUtils.zeronize(String.valueOf(s), 2));
        return sb.toString();
    }
}
