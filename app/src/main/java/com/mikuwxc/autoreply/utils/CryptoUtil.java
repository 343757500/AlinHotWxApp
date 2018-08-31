package com.mikuwxc.autoreply.utils;

import android.content.Context;

import com.mikuwxc.autoreply.common.MyApp;


/**
 * Created by cc on 5/25/15.
 */
public class CryptoUtil {
    public static String encodePwd(Context context, String pswd) {
        if (MyApp.publicKey == null) {
            ConnectUtil.initPublicKey(context);
        }
        return RSAutil.encode16(pswd, MyApp.publicKey);
    }
}
