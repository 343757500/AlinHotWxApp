package com.mikuwxc.autoreply.common.util;

import android.text.TextUtils;
import android.util.Log;

/**
 * @author CenYongxiong
 * @version V1.0
 * @ClassName: [com.cncbk.groupbuy.util.LogUtils]
 * @Description: TODO (用一句话描述该文件做什么)
 * @Date 2017/11/15 14:58
 */
public class LogUtils {
    private static final boolean VERBOSE = true;
    private static final boolean DEBUG = true;
    private static final boolean INFO = true;
    private static final boolean WARN = true;
    private static final boolean ERROR = true;
    private static final String SEPARATOR = ",";
    private static Class CLAZZ = LogUtils.class;

    public static void v(String message) {
        if (VERBOSE) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.v(tag, getOutputMsg(CLAZZ, message));
        }
    }

    public static void v(String tag, String message) {
        if (VERBOSE) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.v(tag, getOutputMsg(CLAZZ, message));
        }
    }

    public static void d(String message) {
        if (DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.d(tag, getOutputMsg(CLAZZ, message));
        }
    }

    public static void d(String tag, String message) {
        if (DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.d(tag, getOutputMsg(CLAZZ, message));
        }
    }

    public static void i(String message) {
        if (INFO) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.i(tag, getOutputMsg(CLAZZ, message));
        }
    }

    public static void i(String tag, String message) {
        if (INFO) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.i(tag, getOutputMsg(CLAZZ, message));
        }
    }

    public static void w(String message) {
        if (WARN) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.w(tag, getOutputMsg(CLAZZ, message));
        }
    }

    public static void w(String tag, String message) {
        if (WARN) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.w(tag, getOutputMsg(CLAZZ, message));
        }
    }

    public static void e(String message) {
        if (ERROR) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.e(tag, getOutputMsg(CLAZZ, message));
        }
    }

    public static void e(String tag, String message) {
        if (ERROR) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.e(tag, getOutputMsg(CLAZZ, message));
        }
    }

    /**
     * 获取默认的TAG名称. 比如在MainActivity.java中调用了日志输出. 则TAG为MainActivity
     */
    public static String getDefaultTag(StackTraceElement stackTraceElement) {
        String fileName = stackTraceElement.getFileName();
        String stringArray[] = fileName.split("\\.");
        String tag = stringArray[0];
        return tag;
    }

    /**
     * 输出日志所包含的信息
     */
    public static String getLogInfo(StackTraceElement stackTraceElement) {
        StringBuilder logInfoStringBuilder = new StringBuilder();
        // 获取线程名
        String threadName = Thread.currentThread().getName();
        // 获取线程ID
        long threadID = Thread.currentThread().getId();
        // 获取文件名.即xxx.java
        String fileName = stackTraceElement.getFileName();
        // 获取类名.即包名+类名
        String className = stackTraceElement.getClassName();
        // 获取方法名称
        String methodName = stackTraceElement.getMethodName();
        // 获取生日输出行数
        int lineNumber = stackTraceElement.getLineNumber();
        logInfoStringBuilder.append("[ ");
        logInfoStringBuilder.append("threadID=" + threadID).append(SEPARATOR);
        logInfoStringBuilder.append("threadName=" + threadName).append(SEPARATOR);
        logInfoStringBuilder.append("fileName=" + fileName).append(SEPARATOR);
        logInfoStringBuilder.append("className=" + className).append(SEPARATOR);
        logInfoStringBuilder.append("methodName=" + methodName).append(SEPARATOR);
        logInfoStringBuilder.append("lineNumber=" + lineNumber);
        logInfoStringBuilder.append(" ] ");
        return logInfoStringBuilder.toString();
    }

    public static String getOutputMsg(Class clazz, String msg) {
        StackTraceElement element = getTargetStackTraceElement(clazz);
        return msg + "(" + element.getFileName() + ":" + element.getLineNumber() + ")";
    }

    private static StackTraceElement getTargetStackTraceElement(Class clazz) {
        StackTraceElement target = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTraceArr = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceArr) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(clazz.getName());
            if (shouldTrace && !isLogMethod) {
                target = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return target;
    }
}
