package com.mikuwxc.autoreply.wchook;

import android.content.Context;
import android.content.Intent;

import com.mikuwxc.autoreply.receiver.Constance;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wcutil.FileIoUtil;
import com.mikuwxc.autoreply.wcutil.GlobalUtil;
import com.mikuwxc.autoreply.wx.AbstractWeChatDb;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class LogWechatDbPathAndPwdHook {
    private static boolean EnMicroMsgNull=true;
    public static void hook(WechatEntity wechatEntity, LoadPackageParam loadPackageParam, ClassLoader r0, final Context context) throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(wechatEntity.sqlitedatabase_class_name, loadPackageParam.classLoader, "openDatabase", new Object[]{String.class, byte[].class, r0.loadClass(wechatEntity.sqlitecipherspec_class_name), r0.loadClass(wechatEntity.sqlitedatabase$cursorfactory_class_name), Integer.TYPE, loadPackageParam.classLoader.loadClass(wechatEntity.databaseerrorhandler_class_name), Integer.TYPE, new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                String str = (String) methodHookParam.args[0];
                byte[] bArr = (byte[]) methodHookParam.args[1];
                if (str.contains("EnMicroMsg.db")) {
                    AbstractWeChatDb.initWechatDb(methodHookParam.getResult());
                    FileIoUtil.setValueToPath(AbstractWeChatDb.getUniqueNameFromPath(str), false, GlobalUtil.WX_UNIQUENAME_SAVE_PATH);
                    FileIoUtil.setValueToPath("===" + System.currentTimeMillis() + "\npath:[" + str + "]\npwd:[" + new String(bArr) + "]\n", false, "sdcard/wxPwd.txt");



                    if (EnMicroMsgNull){
                        Intent in=new Intent();
                        in.setAction(Constance.action_getWechatDb);
                        in.setClassName(Constance.packageName_wechat,Constance.receiver_wechat);
                        in.putExtra(Constance.dabase_Route,str);
                        in.putExtra(Constance.dabase_Password,new String(bArr));
                        context.sendBroadcast(in);
                        EnMicroMsgNull=false;
                    }


                }
            }
        }});
    }
}