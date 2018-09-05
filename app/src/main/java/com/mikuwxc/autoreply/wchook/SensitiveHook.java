package com.mikuwxc.autoreply.wchook;

import com.mikuwxc.autoreply.wcentity.WechatEntity;


import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SensitiveHook  {
    public static void hook(WechatEntity wechatEntity, final XC_LoadPackage.LoadPackageParam loadPackageParam) throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(wechatEntity.sensitive_words_class,
                loadPackageParam.classLoader,
                wechatEntity.sensitive_words_method,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("````````````````````");
                        String arg = (String) param.args[0];
                        XposedBridge.log("arg:"+arg);
                        if (arg.equals("Test")){
                            param.setResult((true));
                        }
                    }
                }
        );
    }
}
