package com.mikuwxc.autoreply.wchook;


import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.mikuwxc.autoreply.common.net.NetApi;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.wcentity.UserEntity;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wcutil.ChatroomUtil;
import com.mikuwxc.autoreply.wx.WechatDb;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import okhttp3.Call;
import okhttp3.Response;

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

                UserEntity userEntity = WechatDb.getInstance().selectSelf();
                String alias = userEntity.getAlias();  //微信号
                handleMessageCreateChatroom(alias,ChatroomList);
            }

            private void handleMessageCreateChatroom(String alias, ArrayList chatroomList) {
                OkGo.post(AppConfig.OUT_NETWORK+ NetApi.creatchatroom+alias).upJson(chatroomList.toString()).execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        XposedBridge.log("sssssss");
                    }


                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        XposedBridge.log("sssssss"+e.toString());
                    }
                });
            }
        }});
    }
}
