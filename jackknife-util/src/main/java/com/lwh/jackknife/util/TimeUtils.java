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

    /**
     * yyyy.MM.dd
     */
    public final static String FORMAT_DATE = "yyyy.MM.dd";

    /**
     * yyyy-MM-dd
     */
    public final static String FORMAT_DATE_2 = "yyyy-MM-dd";

    /**
     * yyyyMMdd
     */
    public final static String FORMAT_DATE_3 = "yyyyMMdd";

    /**
     * HH:mm:ss
     */
    public final static String FORMAT_TIME = "HH:mm:ss";

    /**
     * HHmmss
     */
    public final static String FORMAT_TIME_2 = "HHmmss";

    private TimeUtils() {
    }

    // <editor-folder desc="日期时间转换">

    public static String getString(Date data, String formatType) {
        return new SimpleDateFormat(formatType, Locale.ENGLISH).format(data);
    }

    public static String getString(long currentTime, String formatType) {
        Date date = getDate(currentTime, formatType);
        return getString(date, formatType);
    }

    public static Date getDate(String strTime, String formatType) {
        Date date = null;
        try {
            date = new SimpleDateFormat(formatType, Locale.ENGLISH).parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getDate(long currentTime, String formatType) {
        Date dateOld = new Date(currentTime);
        String sDateTime = getString(dateOld, formatType);
        return getDate(sDateTime, formatType);
    }

    public static long getLong(String strTime, String formatType) {
        Date date = getDate(strTime, formatType);
        if (date == null) {
            return 0;
        } else {
            return getLong(date);
        }
    }

    public static long getLong(Date date) {
        return date.getTime();
    }

    // </editor-folder>

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
                } else {
                    result = 28;
                }
                break;
        }
        return result;
    }

    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }
}
