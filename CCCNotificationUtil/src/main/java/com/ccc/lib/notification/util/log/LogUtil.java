package com.ccc.lib.notification.util.log;

import android.content.Context;
import android.util.Log;

/**
 * Date：2018/7/26 11:03
 * <p>
 * author: CodingCodersCode
 * <p>
 * Log日志打印
 */
public class LogUtil {
    private static boolean DEBUG = true;

    public static void setDebugable(boolean debug) {
        DEBUG = debug;
    }

    public static void printLog(String window, Context context, String tag, int msgResId) {

        String msg = "未知msg内容";
        if (msgResId > 0) {
            msg = context.getResources().getString(msgResId);
        }

        printLog(window, tag, msg);
    }

    public static void printLog(String window, String tag, String msg) {

        printLog(window, tag, msg, null);

    }

    public static void printLog(String window, String tag, Throwable tr) {
        printLog(window, tag, "— —", tr);
    }

    public static void printLog(String window, String tag, String msg, Throwable tr) {
        if (DEBUG) {
            switch (window) {
                case "d":
                    if (tr == null) {
                        Log.d(tag, msg);
                    } else {
                        Log.d(tag, msg, tr);
                    }
                    break;
                case "e":
                    if (tr == null) {
                        Log.e(tag, msg);
                    } else {
                        Log.e(tag, msg, tr);
                    }
                    break;
                case "v":
                    if (tr == null) {
                        Log.v(tag, msg);
                    } else {
                        Log.v(tag, msg, tr);
                    }
                    break;
                case "w":
                    if (tr == null) {
                        Log.w(tag, msg);
                    } else {
                        Log.w(tag, msg, tr);
                    }
                    break;
                case "i":
                    if (tr == null) {
                        Log.i(tag, msg);
                    } else {
                        Log.i(tag, msg, tr);
                    }
                    break;
            }
        }
    }
}
