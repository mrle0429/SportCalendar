package com.test.sport.utils;

import android.util.Log;

// TODO: 日志工具
public class LogUtils {

    private static final boolean DEBUG = true;
    private static final String TAG="ling";

    public static void showLog(String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
        }
    }
}
