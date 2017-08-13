package com.lwh.jackknife.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间处理的相关工具类。
 */
public class TimeUtils {

    /**
     * 格式：年，yyyy。
     */
    public final static String FORMAT_YEAR = "yyyy";

    /**
     * 格式：月，MM。
     */
    public final static String FORMAT_MONTH = "MM";

    /**
     * 格式：日，dd。
     */
    public final static String FORMAT_DAY = "dd";

    /**
     * 格式：时，HH。
     */
    public final static String FORMAT_HOUR = "HH";

    /**
     * 格式：分，mm。
     */
    public final static String FORMAT_MINUTE = "mm";

    /**
     * 格式：秒，ss。
     */
    public final static String FORMAT_SECOND = "ss";

    /**
     * 格式：毫秒，SSS。
     */
    public final static String FORMAT_MILLISECOND = "SSS";

    /**
     * 格式：年月，yyyyMM。
     */
    public final static String FORMAT_YEAR_MONTH = FORMAT_YEAR + FORMAT_MONTH;

    /**
     * 格式：年月，yyyy.MM。
     */
    public final static String FORMAT_YEAR_MONTH_2 = FORMAT_YEAR + "." + FORMAT_MONTH;

    /**
     * 格式：年月，yyyy MM。
     */
    public final static String FORMAT_YEAR_MONTH_3 = FORMAT_YEAR + " " + FORMAT_MONTH;

    /**
     * 格式：年月，yyyy年MM月。
     */
    public final static String FORMAT_YEAR_MONTH_4 = FORMAT_YEAR + "年" + FORMAT_MONTH + "月";

    /**
     * 格式：月日，MMdd。
     */
    public final static String FORMAT_MONTH_DAY = FORMAT_MONTH + FORMAT_DAY;

    /**
     * 格式：月日，MM.dd。
     */
    public final static String FORMAT_MONTH_DAY_2 = FORMAT_MONTH + "." + FORMAT_DAY;

    /**
     * 格式：月日，MM dd。
     */
    public final static String FORMAT_MONTH_DAY_3 = FORMAT_MONTH + " " + FORMAT_DAY;

    /**
     * 格式：月日，MM月dd日。
     */
    public final static String FORMAT_MONTH_DAY_4 = FORMAT_MONTH + "月" + FORMAT_DAY + "日";

    /**
     * 格式：年月日，yyyyMMdd。
     */
    public final static String FORMAT_DATE = FORMAT_YEAR + FORMAT_MONTH + FORMAT_DAY;

    /**
     * 格式：年月日，yyyy.MM.dd。
     */
    public final static String FORMAT_DATE_2 = FORMAT_YEAR_MONTH_2 + "." + FORMAT_DAY;

    /**
     * 格式：年月日：yyyy年MM月dd日。
     */
    public final static String FORMAT_DATE_3 = FORMAT_YEAR_MONTH_4 + FORMAT_DAY + "日";

    /**
     * 格式：时分，HHmm。
     */
    public final static String FORMAT_HOUR_MINUTE = FORMAT_HOUR + FORMAT_MINUTE;

    /**
     * 格式：时分，HH:mm。
     */
    public final static String FORMAT_HOUR_MINUTE_2 = FORMAT_HOUR + ":" + FORMAT_MINUTE;

    /**
     * 格式：时分，HH时mm分。
     */
    public final static String FORMAT_HOUR_MINUTE_3 = FORMAT_HOUR + "时" + FORMAT_MINUTE + "分";

    /**
     * 格式：时分秒，HHmmss。
     */
    public final static String FORMAT_TIME = FORMAT_HOUR + FORMAT_MINUTE + FORMAT_SECOND;

    /**
     * 格式：时分秒，HH:mm:ss。
     */
    public final static String FORMAT_TIME_2 = FORMAT_HOUR + ":" + FORMAT_MINUTE + ":" + FORMAT_SECOND;

    /**
     * 格式：时分秒，HH时mm分ss秒。
     */
    public final static String FORMAT_TIME_3 = FORMAT_HOUR + "时" + FORMAT_MINUTE + "分" + FORMAT_SECOND + "秒";

    /**
     * 格式：年月日时分秒，yyyyMMddHHmmss。
     */
    public final static String FORMAT_DATE_TIME = FORMAT_DATE + FORMAT_TIME;

    /**
     * 格式：年月日时分秒，yyyy.MM.dd HH:mm:ss。
     */
    public final static String FORMAT_DATE_TIME_2 = FORMAT_DATE_2 + " " + FORMAT_TIME_2;

    /**
     * 格式：年月日时分秒，yyyy年MM月dd日HH时mm分ss秒。
     */
    public final static String FORMAT_DATE_TIME_3 = FORMAT_DATE_3 + FORMAT_TIME_3;

    /**
     * 分所包含的秒数。
     */
    public static final int SECONDS_OF_MINUTE = 60;

    /**
     * 时所包含的秒数。
     */
    public static final int SECONDS_OF_HOUR = SECONDS_OF_MINUTE * 60;

    /**
     * 日所包含的秒数。
     */
    public static final int SECONDS_OF_DAY = SECONDS_OF_HOUR * 24;

    /**
     * 周所包含的秒数。
     */
    public static final int SECONDS_OF_WEEK = SECONDS_OF_DAY * 7;

    /**
     * 月所包含的秒数，大致数据。
     */
    public static final int SECONDS_OF_MONTH = SECONDS_OF_DAY * 30;

    /**
     * 平年所包含的秒数。
     */
    public static final int SECONDS_OF_NON_LEAP_YEAR = SECONDS_OF_DAY * 365;

    /**
     * 闰年所包含的秒数。
     */
    public static final int SECONDS_OF_LEAP_YEAR = SECONDS_OF_DAY * 366;

    /**
     * 秒所包含的毫秒数。
     */
    public static final int MILLISECONDS_OF_SECOND = 1000;

    /**
     * 分所包含的毫秒数。
     */
    public static final int MILLISECONDS_OF_MINUTE = MILLISECONDS_OF_SECOND * SECONDS_OF_MINUTE;

    /**
     * 时所包含的毫秒数。
     */
    public static final int MILLISECONDS_OF_HOUR = MILLISECONDS_OF_SECOND * SECONDS_OF_HOUR;

    /**
     * 日所包含的毫秒数。
     */
    public static final int MILLISECONDS_OF_DAY = MILLISECONDS_OF_SECOND * SECONDS_OF_DAY;

    /**
     * 周所包含的毫秒数。
     */
    public static final int MILLISECONDS_OF_WEEK = MILLISECONDS_OF_SECOND * SECONDS_OF_WEEK;

    private TimeUtils(){
    }

    /**
     * date转换为string。
     *
     * @param data 日期。
     * @param formatType 格式化类型。
     * @return string类型的时间。
     */
    public static String date2str(Date data, String formatType) {
        return new SimpleDateFormat(formatType, Locale.CHINA).format(data);
    }

    /**
     * long转换为string。
     *
     * @param currentTime 当前时间。
     * @param formatType 格式化类型。
     * @return string类型的时间。
     */
    public static String long2str(long currentTime,String formatType){
        String strTime="";
        Date date = long2date(currentTime, formatType);
        strTime = date2str(date, formatType);
        return strTime;
    }

    /**
     * string转换为date。
     *
     * @param strTime 字符串时间。
     * @param formatType 格式化类型。
     * @return date类型的时间。
     */
    public static Date str2date(String strTime, String formatType){
        Date date = null;
        try {
            date = new SimpleDateFormat(formatType, Locale.CHINA).parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * long转换为date。
     *
     * @param currentTime 当前时间。
     * @param formatType 格式化类型。
     * @return date类型的时间。
     */
    public static Date long2date(long currentTime, String formatType){
        Date dateOld = new Date(currentTime);
        String sDateTime = date2str(dateOld, formatType);
        Date date = str2date(sDateTime, formatType);
        return date;
    }

    /**
     * string转为long。
     *
     * @param strTime 字符串时间。
     * @param formatType 格式化类型。
     * @return long类型的时间。
     */
    public static long str2long(String strTime, String formatType){
        Date date = str2date(strTime, formatType);
        if (date == null) {
            return 0;
        } else {
            long currentTime = date2long(date);
            return currentTime;
        }
    }

    /**
     * 得到一个月的天数。
     *
     * @param year 年份。
     * @param month 月份。
     * @return 一个月的天数。
     */
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

    /**
     * 日期转为long。
     *
     * @param date 日期。
     * @return long类型的时间。
     */
    public static long date2long(Date date) {
        return date.getTime();
    }

    /**
     * 得到描述性时间。
     *
     * @param timestamp 时间戳。
     * @return 描述性时间。
     */
    public static String getDescTime(long timestamp) {
        long currentTime = System.currentTimeMillis();
        //时间间隔，与现在时间相差秒数
        long timeGap = (currentTime - timestamp) / 1000;
        String timeStr = null;
        //我们作为一般的平年处理
        if (timeGap > SECONDS_OF_LEAP_YEAR) {
            timeStr = timeGap / SECONDS_OF_LEAP_YEAR + "年前";
            // 1个月以上
        } else if (timeGap > SECONDS_OF_MONTH) {
            timeStr = timeGap / SECONDS_OF_MONTH + "个月前";
            // 1天以上
        } else if (timeGap > SECONDS_OF_DAY) {
            timeStr = timeGap / SECONDS_OF_DAY + "天前";
            // 1小时-24小时
        } else if (timeGap > SECONDS_OF_HOUR) {
            timeStr = timeGap / SECONDS_OF_HOUR + "小时前";
            // 1分钟-59分钟
        } else if (timeGap > SECONDS_OF_MINUTE) {
            timeStr = timeGap / SECONDS_OF_MINUTE + "分钟前";
            // 1秒钟-59秒钟
        } else {
            timeStr = "刚刚";
        }
        return timeStr;
    }

    /**
     * 判断某个年份是否为闰年。
     *
     * @param year 要判断的年份。
     * @return 是否为闰年，闰年366天，平年365天。
     */
    public static boolean isLeapYear(int year) {
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0){
            return true;
        }else{
            return false;
        }
    }
}
