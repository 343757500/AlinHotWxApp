package com.mikuwxc.autoreply.common.util;

import android.util.Log;

/**
 * Created by l2yc on 2017/7/3.
 * TODO
 */
public class Logger {
    /**
     * whether print log info
     */
    public final static boolean weatherPrint = true;
//    public final static boolean weatherPrint = false;//打包去掉log
    static String className;//类名
    static String methodName;//方法名
    static int lineNumber;//行数
    private static void getMethodNames(StackTraceElement[] sElements){
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }
    private static String createLog(String log ) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(methodName);
        buffer.append("(").append(className).append(":").append(lineNumber).append(")");
        buffer.append(log);
        return buffer.toString();
    }

    public static void d( String msg) {
        if (!weatherPrint)
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.i(className, createLog(msg));
    }

    public static void v( String msg) {
        if (!weatherPrint)
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.v(className, createLog(msg));
    }

    public static void i( String msg) {

        if (!weatherPrint)
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.i(className, createLog(msg));
    }

    public static void w( String msg) {

        if (!weatherPrint)
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.w(className, createLog(msg));
    }

    public static void e( String msg) {

        if (!weatherPrint)
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.e(className, createLog(msg));
    }

    public static boolean isLoggable( int level) {
        //return Log.isLoggable(tag.substring(0, Math.min(23, tag.length())), level);
        return true;
    }
}
