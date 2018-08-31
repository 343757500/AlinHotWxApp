package com.mikuwxc.autoreply.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/11/21.
 */
public class PreferenceUtil {
    public static final String PREFERENCE = "mikuhide";
    public static final String ISLOGIN = "isLogin";
    public static final String OPENID = "openId";
    public static final String HEADIMGURL = "headimgurl";
    public static final String NICKNAME = "nickname";
    private static final String CURRENT_USERID = "currentUserId";
    private static final String CURRENT_WXID = "currentWxId";
    private static final String CURRENT_USER = "currentUser";
    private static final String WX_NUM = "current_wx_num";
    private static final String WX_VERSION = "wx_version";
    private static final String WX_ACCESS_TOKEN = "access_token";
    private static final String WX_REFRESH_TOKEN = "refresh_token";

    public static SharedPreferences getPreference(Context context) {
        SharedPreferences preferences = null;
        if (context != null) {
            preferences = context.getSharedPreferences(PREFERENCE, Activity.MODE_WORLD_READABLE);
        }
        return preferences;
    }

    public static void setLoginStatus(Context context, boolean isLogin) {
        getPreference(context).edit().putBoolean(ISLOGIN, isLogin).apply();
    }

    public static boolean isLogin(Context context) {
        return getPreference(context).getBoolean(ISLOGIN, false);
    }

    public static void setWxAccessToken(Context context, String accessToken) {
        getPreference(context).edit().putString(WX_ACCESS_TOKEN, accessToken).apply();
    }

    public static String getWxAccessToken(Context context) {
        return getPreference(context).getString(WX_ACCESS_TOKEN, "");
    }

    public static void setWxRefreshToken(Context context, String refreshToken) {
        getPreference(context).edit().putString(WX_REFRESH_TOKEN, refreshToken).apply();
    }

    public static String getWxRefreshToken(Context context) {
        return getPreference(context).getString(WX_REFRESH_TOKEN, "");
    }

    public static void setOpenId(Context context, String openId) {
        getPreference(context).edit().putString(OPENID, openId).apply();
    }

    public static String getOpenid(Context context) {
        return getPreference(context).getString(OPENID, "");
    }

    public static void setHeadimgurl(Context context, String headimgurl) {
        getPreference(context).edit().putString(HEADIMGURL, headimgurl).apply();
    }

    public static String getHeadimgurl(Context context) {
        return getPreference(context).getString(HEADIMGURL, "");
    }

    public static void setNickname(Context context, String nickname) {
        getPreference(context).edit().putString(NICKNAME, nickname).apply();
    }

    public static String getNickname(Context context) {
        return getPreference(context).getString(NICKNAME, "");
    }

    public static void setCurrentUserId(Context context, long userId) {
        getPreference(context).edit().putString(CURRENT_USERID, String.valueOf(userId)).apply();
    }

    public static String getCurrentUserid(Context context) {
        return getPreference(context).getString(CURRENT_USERID, "");
    }

    public static void setCurrentWxId(Context context, long wxId) {
        getPreference(context).edit().putString(CURRENT_WXID, String.valueOf(wxId)).apply();
    }

    public static String getCurrentWxid(Context context) {
        return getPreference(context).getString(CURRENT_WXID, "");
    }

    public static void setCurrentUser(Context context, String wxNum) {
        getPreference(context).edit().putString(CURRENT_USER, wxNum).apply();
    }

    public static String getCurrentUser(Context context) {
        return getPreference(context).getString(CURRENT_USER, "");
    }

    /**
     * 手机号码/QQ号/微信号
     *
     * @param context
     */
    public static void setCurrentWxNum(Context context, String wxNum) {
        getPreference(context).edit().putString(WX_NUM, wxNum).apply();
    }

    public static String getCurrentWxNum(Context context) {
        return getPreference(context).getString(WX_NUM, "无");
    }

    /**
     * 微信版本
     *
     * @param context
     */
    public static void setWxNumVersion(Context context, String wxNum) {
        getPreference(context).edit().putString(WX_VERSION, wxNum).apply();
    }

    public static String getWxNumVersion(Context context) {
        return getPreference(context).getString(WX_VERSION, "微信 6.3.31");
    }
}
