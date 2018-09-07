package com.mikuwxc.autoreply.wchook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.common.util.LogUtils;
import com.mikuwxc.autoreply.common.util.MyFileUtil;
import com.mikuwxc.autoreply.common.util.RegularUtils;
import com.mikuwxc.autoreply.receiver.Constance;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.xposed.CommonHook;


import org.apache.commons.lang3.StringUtils;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SensitiveHook  {
    public static void hook(WechatEntity wechatEntity, final XC_LoadPackage.LoadPackageParam loadPackageParam, final Context context) throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(wechatEntity.sensitive_words_class,
                loadPackageParam.classLoader,
                wechatEntity.sensitive_words_method,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);

                        RegularUtils.SENSITIVE_WORD = MyFileUtil.readFromFile(AppConfig.APP_FOLDER + "/sensitiveIntercept");
                        RegularUtils.SENSITIVE_WORDVOISE = MyFileUtil.readFromFile(AppConfig.APP_FOLDER + "/sensitiveNotice");

                        //拦截敏感词
                        if (RegularUtils.SENSITIVE_WORD == null) {
                            RegularUtils.SENSITIVE_WORD = MyFileUtil.readFromFile(AppConfig.APP_FOLDER + "/sensitiveIntercept");
                        } else {
                            File updateFile = new File(AppConfig.APP_FOLDER + "/updateIntercept");
                            if (updateFile.exists()) {//需要更新敏感词
                                RegularUtils.SENSITIVE_WORD = MyFileUtil.readFromFile(AppConfig.APP_FOLDER + "/sensitiveIntercept");
                                updateFile.delete();
                            }
                        }

                        if (RegularUtils.SENSITIVE_WORD != null) {
                            String arg = (String) param.args[0];
                            String hasSensitive = RegularUtils.isMatchRegular(arg, RegularUtils.SENSITIVE_WORD);
                            XposedBridge.log("arg:"+arg);
                            if (StringUtils.isNotBlank(hasSensitive)){
                                Intent in=new Intent();
                                in.setAction(Constance.action_toast);
                                in.setClassName(Constance.packageName_wechat,Constance.receiver_wechat);
                                in.putExtra(Constance.str_toast,hasSensitive+"是敏感词");
                                context.sendBroadcast(in);

                                param.setResult((true));
                            }
                        }

                        //提示敏感词但是不拦截
                        if (RegularUtils.SENSITIVE_WORDVOISE == null) {
                            RegularUtils.SENSITIVE_WORDVOISE = MyFileUtil.readFromFile(AppConfig.APP_FOLDER + "/sensitiveNotice");
                        } else {
                            File updateFile = new File(AppConfig.APP_FOLDER + "/updateNotice");
                            if (updateFile.exists()) {//需要更新敏感词
                                RegularUtils.SENSITIVE_WORDVOISE = MyFileUtil.readFromFile(AppConfig.APP_FOLDER + "/sensitiveNotice");
                                updateFile.delete();
                            }
                        }

                        if (RegularUtils.SENSITIVE_WORDVOISE != null) {
                            String arg = (String) param.args[0];
                            String hasSensitiveVoise = RegularUtils.isMatchRegular(arg, RegularUtils.SENSITIVE_WORDVOISE);
                            XposedBridge.log("123456:"+RegularUtils.SENSITIVE_WORDVOISE+"789");
                            XposedBridge.log("arg:"+arg+"hasSensitive"+hasSensitiveVoise);

                            if (StringUtils.isNotBlank(hasSensitiveVoise)){
                                XposedBridge.log("你发送了敏感词");

                                Intent in=new Intent();
                                in.setAction(Constance.action_toast);
                                in.setClassName(Constance.packageName_wechat,Constance.receiver_wechat);
                                in.putExtra(Constance.str_toast,hasSensitiveVoise+"是敏感词");
                                context.sendBroadcast(in);

                            }
                        }


                    }
                }
        );
    }
}
