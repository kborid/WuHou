package com.prj.sdk.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.prj.sdk.BuildConfig;

/**
 * 对android自带日志的一个简单封装，方便调用
 *
 * @author liao
 */
public class LogUtil {

    private static long startTime = 0;

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    // 冗余信息输出
    public static void v(String tag, String msg) {
        if (isDebug()) {
            Log.v(tag, "[" + Thread.currentThread().getName() + "]:" + msg);
        }
    }

    // 调试信息输出
    public static void d(String tag, String msg) {
        if (isDebug()) {
            Log.d(tag, "[" + Thread.currentThread().getName() + "]:" + msg);
        }
    }

    // 提示信息输出
    public static void i(String tag, String msg) {
        if (isDebug()) {
            Log.i(tag, "[" + Thread.currentThread().getName() + "]:" + msg);
        }
    }

    // 警告信息输出
    public static void w(String tag, String msg) {
        if (isDebug()) {
            Log.w(tag, "[" + Thread.currentThread().getName() + "]:" + msg);
        }
    }

    // 错误信息输出
    public static void e(String tag, String msg) {
        if (isDebug()) {
            Log.e(tag, "[" + Thread.currentThread().getName() + "]:" + msg);
        }
    }

    /*
     * 记录方法调用的开始时间
     */
    public static void startTime() {
        startTime = System.currentTimeMillis();
        d("记录方法调用的开始时间", "start time:" + startTime);
    }

    /*
     * 记录方法调用的使用时间
     */
    public static void useTime() {
        long endTime = System.currentTimeMillis();
        d("记录方法调用的使用时间", "use time:" + (endTime - startTime));
    }
}
