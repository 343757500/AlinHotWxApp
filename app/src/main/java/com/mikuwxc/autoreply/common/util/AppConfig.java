package com.mikuwxc.autoreply.common.util;


import android.os.Environment;

/**
 *
 *
 * Description
 * sp里面的数据。
 * 登录的返回的信息存在缓存里
 */

public class AppConfig {
    private static String identifier;//用户帐号
    private static String userSig;//用户帐号签名
    private static String selectHost;//选择服务器
    public static final String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String APP_FOLDER = ROOT + "/HSL_WXFZ";
    public static String OUT_NETWORK = "http://192.168.4.14:8085/";
   // public static String OUT_NETWORK = "http://192.168.4.50:8080/";
   // public static String OUT_NETWORK = "http://47.107.77.220:8080/";
    public static String Registration_Id = null;
    public static final String YOUPAIYUN ="http://cloned.test.upcdn.net";

    public static void init() {
        identifier = SharedPrefsUtils.getString("identifier");
        userSig = SharedPrefsUtils.getString("userSig");
        selectHost = SharedPrefsUtils.getString("selectHost");
    }

    public static String getIdentifier() {
        return identifier;
    }

    public static void setIdentifier(String identifier) {
        SharedPrefsUtils.putString("identifier",identifier);
        AppConfig.identifier = identifier;
    }

    public static String getUserSig() {
        return userSig;
    }

    public static void setUserSig(String userSig) {
        SharedPrefsUtils.putString("userSig",userSig);
        AppConfig.userSig = userSig;
    }

    public static String getSelectHost() {
        return selectHost;
    }

    public static void setSelectHost(String selectHost) {
        SharedPrefsUtils.putString("selectHost",selectHost);
        AppConfig.selectHost = selectHost;
    }

    public static void setHost(String selectHost) {
        AppConfig.selectHost = selectHost;
    }
}
