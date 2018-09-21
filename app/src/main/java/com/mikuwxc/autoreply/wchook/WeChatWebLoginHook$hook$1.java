package com.mikuwxc.autoreply.wchook;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.mikuwxc.autoreply.common.net.NetApi;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.wcentity.UserEntity;
import com.mikuwxc.autoreply.wx.WechatDb;
import com.orhanobut.logger.Logger;

import org.jetbrains.annotations.Nullable;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Call;
import okhttp3.Response;

/* compiled from: WeChatWebLoginHook.kt */
public final class WeChatWebLoginHook$hook$1 extends XC_MethodHook {
    WeChatWebLoginHook$hook$1() {
    }

    protected void afterHookedMethod(@Nullable MethodHookParam param) throws Throwable {
        String str = "===== login webwx %s";
        Object[] objArr = new Object[1];
        if (param == null) {
            Intrinsics.throwNpe();
        }
        objArr[0] = param.args[0];
        Logger.i(str, objArr);
        XposedBridge.log(("===== login webwx %s"+param.args[0]));
        XposedBridge.log(("===== login webwx %s"+param.args[1]));
        XposedBridge.log("===== login webwx %s"+ param.args[2]);

        UserEntity userEntity = WechatDb.getInstance().selectSelf();
        String userName = userEntity.getUserName();
        String userTalker = userEntity.getUserTalker();
        String headPic = userEntity.getHeadPic();
        String alias = userEntity.getAlias();  //微信号
        XposedBridge.log("aliasaliasaliasalias::"+alias);
        handleMessageLoginWebWx(alias, param.args[0].toString());
    }

    private void handleMessageLoginWebWx(String wxno, String s) {
        OkGo.post(AppConfig.OUT_NETWORK+ NetApi.loginweb+ "?" + "wxno=" +wxno).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                XposedBridge.log("sssssss"+s);
            }
            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                XposedBridge.log("sssssss"+e.toString());
            }
        });
    }
}
