package com.lwh.jackknife.util;

import android.util.Log;

/**
 * 企业级日志管理系统，通过一个boolean变量来控制日志的输出。
 */
public class Logger {

    /**
     * 是否启用调试模式。true表示启用调试模式，将会输出所有本类产生的日志。false表示不启用调试模式，将不会输出任
     * 何本类产生的日志。
     */
    public static boolean DEBUG = true;

    private static final String TAG = Logger.class.getSimpleName();

    /**
     * 输出info级别的日志。
     *
     * @param msg 日志内容。
     */
    public static void info(String msg){
        info(TAG, msg);
    }

    /**
     * 输出info级别的日志。
     *
     * @param tag 日志标签。
     * @param msg 日志内容。
     */
    public static void info(String tag, String msg){
        if (DEBUG){
            Log.i(tag, msg);
        }
    }

    /**
     * 输出error级别的日志。
     *
     * @param msg 日志内容。
     */
    public static void error(String msg){
        error(TAG, msg);
    }

    /**
     * 输出error级别的日志。
     *
     * @param tag 日志标签。
     * @param msg 日志内容。
     */
    public static void error(String tag, String msg){
        if (DEBUG){
            Log.e(tag, msg);
        }
    }

    /**
     * 输出debug级别的日志。
     *
     * @param msg 日志内容。
     */
    public static void debug(String msg){
        debug(TAG, msg);
    }

    /**
     * 输出debug级别的日志。
     *
     * @param tag 日志标签。
     * @param msg 日志内容。
     */
    public static void debug(String tag, String msg){
        if (DEBUG){
            Log.d(tag, msg);
        }
    }

    /**
     * 输出warn级别的日志。
     *
     * @param msg 日志内容。
     */
    public static void warn(String msg){
        warn(TAG, msg);
    }

    /**
     * 输出warn级别的日志。
     *
     * @param tag 日志标签。
     * @param msg 日志内容。
     */
    public static void warn(String tag, String msg){
        if (DEBUG){
            Log.w(tag, msg);
        }
    }

    /**
     * 输出verbose级别的日志。
     *
     * @param msg 日志内容。
     */
    public static void verbose(String msg){
        verbose(TAG, msg);
    }

    /**
     * 输出verbose级别的日志。
     *
     * @param tag 日志标签。
     * @param msg 日志内容。
     */
    public static void verbose(String tag, String msg){
        if (DEBUG){
            Log.v(tag, msg);
        }
    }
}
