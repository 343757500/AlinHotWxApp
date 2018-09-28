package com.mikuwxc.autoreply.wchook;


import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wcutil.ChatroomUtil;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class CreateChatroomHook {
    public static void hook(final WechatEntity wechatEntity, LoadPackageParam param) {
        final ClassLoader wxClassLoader = param.classLoader;
        Class class_q = XposedHelpers.findClass("com.tencent.mm.network.q", wxClassLoader);
        XposedHelpers.findAndHookMethod(wechatEntity.create_chatroom_class2, wxClassLoader, wechatEntity.create_chatroom_method2, new Object[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, String.class, class_q, byte[].class, new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                //WxEventBus.publish(new WxRefreashChatroomList(wechatEntity, wxClassLoader, param.thisObject));
                XposedBridge.log(" param.thisObject::"+param.thisObject.toString());
                XposedBridge.log(" param.thisObject::"+param.args[0]);
                XposedBridge.log(" param.thisObject::"+param.args[1]);
                XposedBridge.log(" param.thisObject::"+param.args[2]);
                XposedBridge.log(" param.thisObject::"+param.args[3]);
                XposedBridge.log(" param.thisObject::"+param.args[4]);
                XposedBridge.log(" param.thisObject::"+param.args[5]);

                ArrayList ChatroomList= ChatroomUtil.refreashCreateChatroom(wxClassLoader, wechatEntity, param.thisObject);
                XposedBridge.log(ChatroomList.size()+"ChatroomList.size()");
            }
        }});
    }
}
