package com.mikuwxc.autoreply.wcutil;

import android.util.SparseArray;

import java.util.List;

public class OtherUtils {
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        try {
            String valueOf = String.valueOf(obj);
            return valueOf.equals("null") || valueOf.equals("");
        } catch (Throwable e) {
            //ThrowableExtension.printStackTrace(e);
            return true;
        }
    }

    public static <T> boolean isListNotEmpty(List<T> list) {
        if (list == null) {
            return false;
        }
        try {
            return list.size() > 0;
        } catch (Throwable e) {
            //ThrowableExtension.printStackTrace(e);
            return false;
        }
    }

    public static boolean isNotEmpty(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            String trim = String.valueOf(obj).trim();
            return (trim.equals("") || trim.equals("null")) ? false : true;
        } catch (Throwable e) {
            //ThrowableExtension.printStackTrace(e);
            return false;
        }
    }

    public static <T> boolean isSparseArrayNotEmpty(SparseArray<T> sparseArray) {
        if (sparseArray == null) {
            return false;
        }
        try {
            return sparseArray.size() > 0;
        } catch (Throwable e) {
           // ThrowableExtension.printStackTrace(e);
            return false;
        }
    }

    public static String isStrNullTo(String str, String str2) {
        return str != null ? str : str2;
    }
}