package com.mikuwxc.autoreply.wchook;

import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.common.util.MyFileUtil;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wcutil.FriendUtil;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import kotlin.jvm.internal.Intrinsics;

public class AddFriendHook {
        public static void hook(final WechatEntity wechatEntity, LoadPackageParam param) {
            final ClassLoader wxClassLoader = param.classLoader;
            XposedHelpers.findAndHookMethod(wechatEntity.add_search_friend_class2, wxClassLoader, wechatEntity.add_search_friend_method3, new Object[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, String.class, XposedHelpers.findClass(wechatEntity.add_search_friend_class3, wxClassLoader), byte[].class, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("===== searchFriend 222"+ new Object[0]);
                    Object localObject3;
                    Object localObject2;
                    if (Intrinsics.areEqual((Object) "Everything is OK", param.args[3].toString())) {
                        localObject3 = XposedHelpers.callMethod(param.thisObject, wechatEntity.add_search_friend_method4, new Object[0]);
                        Object  localObject1 = XposedHelpers.getObjectField(localObject3, wechatEntity.add_search_friend_field1);
                        localObject2 = XposedHelpers.getObjectField(localObject3, wechatEntity.add_search_friend_field2);
                        Object paramAnonymousMethodHookParam1 = XposedHelpers.getObjectField(localObject3,wechatEntity.add_search_friend_field3);
                        Object localObject4 = XposedHelpers.getObjectField(XposedHelpers.getObjectField(localObject3, wechatEntity.add_search_friend_field4), wechatEntity.add_search_friend_field5);

                       String addContent = MyFileUtil.readFromFile(AppConfig.APP_ADD);
                       if (addContent!=null) {
                           FriendUtil.addFriendWithUpdateRemark(wxClassLoader, wechatEntity, addContent, addContent, "", 15);
                           FriendUtil.addFriend11(wxClassLoader, wechatEntity, addContent, "ppp", 15);   //15 是通过微信号加好友*/
                           XposedBridge.log("addFriend11" + addContent);
                       }else {
                           XposedBridge.log("addContent11内存中的为空" + "");
                       }

                    }

                    XposedBridge.log("===== searchFriend 333"+ new Object[0]);

                }
            }});
            XposedHelpers.findAndHookMethod(XposedHelpers.findClass(wechatEntity.add_search_friend_class4, wxClassLoader), wechatEntity.add_search_friend_method5, new Object[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, String.class, XposedHelpers.findClass(wechatEntity.add_search_friend_class3, wxClassLoader), byte[].class, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("===== searchFriend 444"+ new Object[0]);
                    if (Integer.parseInt(param.args[1].toString()) == 4) {
                        XposedBridge.log("1111");
                        if (Intrinsics.areEqual((Object) "user need verify", param.args[3].toString())){
                            XposedBridge.log("222");
                            String addContent = MyFileUtil.readFromFile(AppConfig.APP_ADD);
                            if (addContent!=null) {
                                FriendUtil.addFriend12(wxClassLoader, wechatEntity, addContent, "你好", 15);   //15 是通过微信号加好友*/
                                XposedBridge.log("addFriend12" + addContent);
                            }else {
                                XposedBridge.log("addContent12内存中的为空" + "");
                            }
                        }

                    }
                    XposedBridge.log("===== searchFriend 555"+ new Object[0]);
                }

            }});
        }

}