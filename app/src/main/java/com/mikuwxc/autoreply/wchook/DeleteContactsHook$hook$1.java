package com.mikuwxc.autoreply.wchook;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.mikuwxc.autoreply.bean.DeleteFriendBean;
import com.mikuwxc.autoreply.common.net.NetApi;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.wcentity.UserEntity;
import com.mikuwxc.autoreply.wx.WechatDb;

import org.jetbrains.annotations.Nullable;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Call;
import okhttp3.Response;

/* compiled from: DeleteContactsHook.kt */
public final class DeleteContactsHook$hook$1 extends XC_MethodHook {
    DeleteContactsHook$hook$1() {
    }

    protected void afterHookedMethod(@Nullable MethodHookParam param) throws Throwable {
        if (param == null) {
            Intrinsics.throwNpe();
        }
        XposedBridge.log("===== delete contact1 %s"+ param.args[0].toString());
        UserEntity userEntity = WechatDb.getInstance().selectSelf();
        String userName = userEntity.getUserName();
        String userTalker = userEntity.getUserTalker();
        String headPic = userEntity.getHeadPic();
        String alias = userEntity.getAlias();  //微信号
        XposedBridge.log("aliasaliasaliasalias::"+alias);
        handleMessageDeleteFriend(alias, param.args[0].toString());
    }

    public void handleMessageDeleteFriend(String wxno,String wxid){
        OkGo.delete(AppConfig.OUT_NETWORK+ NetApi.deletefriend+ "?" + "wxno=" +wxno+ "&friendWxid=" + wxid).execute(new StringCallback() {
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
