package com.mikuwxc.autoreply.wxid;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.mikuwxc.autoreply.utils.PreferenceUtil;


/**
 * Created by Administrator on 2017/2/14.
 */
public class WxIdUtil {
    public static void initId(Context context) {
        String wxNumVersion = PreferenceUtil.getWxNumVersion(context);
        Log.d("当前微信版本", "initId: " + wxNumVersion);
        if (wxNumVersion.equals("微信 6.3.31")) {
            //默认微信版本
        } else if (wxNumVersion.equals("微信 6.5.3")) {
            WxId653.init();
            Log.d("当前微信版本", "initId: 初始化版本Id 微信 6.5.3");
        } else if (wxNumVersion.equals("微信 6.5.4")) {
            WxId654.init();
            Log.d("当前微信版本", "initId: 初始化版本Id 微信 6.5.4");
        } else if (wxNumVersion.equals("微信 6.5.6")) {
            WxId656.init();
            Log.d("当前微信版本", "initId: 初始化版本Id 微信 6.5.6");
        } else if (wxNumVersion.equals("微信 6.5.7")) {
            WxId657.init();
            Log.d("当前微信版本", "initId: 初始化版本Id 微信 6.5.7");
        } else if (wxNumVersion.equals("微信 6.6.6")) {
            WxId666.init();
            Log.d("当前微信版本", "initId: 初始化版本Id 微信 6.6.6");
        } else {
            Toast.makeText(context, "请更新至多群最新版本！", Toast.LENGTH_SHORT).show();
        }
    }
}
