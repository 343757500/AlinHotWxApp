package com.mikuwxc.autoreply.wchook;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.mikuwxc.autoreply.common.net.NetApi;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.wcentity.UserEntity;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wcutil.OtherUtils;
import com.mikuwxc.autoreply.wx.WechatDb;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Call;
import okhttp3.Response;

/* compiled from: ReportDeleteWxMessageRiskOperateHook.kt */
public final class ReportDeleteWxMessageRiskOperateHook$hook$2 extends XC_MethodHook {
    final /* synthetic */ ClassLoader $classLoader;
    final /* synthetic */ WechatEntity $wechatEntity;

    ReportDeleteWxMessageRiskOperateHook$hook$2(ClassLoader $captured_local_variable$0, WechatEntity $captured_local_variable$1) {
        this.$classLoader = $captured_local_variable$0;
        this.$wechatEntity = $captured_local_variable$1;
    }

    protected void beforeHookedMethod(@Nullable MethodHookParam param) {
        if (param == null) {
            Intrinsics.throwNpe();
        }
        String table = (String) param.args[0];
        if (table == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
        }
        table = table;
        ContentValues values = (ContentValues) param.args[1];
        String arg3 = (String) param.args[2];
        if (arg3 == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
        }
        Object arg32 = arg3;
        String[] arg4 = (String[]) param.args[3];
        if (values != null) {
            switch (table.hashCode()) {
                case 954925063:
                    if (table.equals("message")) {
                        String content = values.getAsString("content");
                        if (!OtherUtils.isEmpty(content)) {
                            Intrinsics.checkExpressionValueIsNotNull(content, "content");
                            if (content.contains("撤回了一条消息") && Intrinsics.areEqual(arg32, (Object) "msgId=?") && arg4 != null){

                           // }
                          // if (StringsKt__StringsKt.contains$default((CharSequence) content, (CharSequence) "撤回了一条消息", false, 2, null) && Intrinsics.areEqual(arg32, (Object) "msgId=?") && arg4 != null) {
                                int i;
                                boolean i2;
                                if (arg4.length == 0) {
                                    i2 = true;
                                } else {
                                    i2 = false;
                                }
                                if (i2 == false) {
                                    i2 = true;
                                } else {
                                    i2 = false;
                                }
                                if (i2 != false) {
                                    long msgId = Long.parseLong(arg4[0]);
                                    XposedBridge.log("====="+ new Object[0]);
                                    XposedBridge.log("===== 撤回消息Id %s"+ Long.valueOf(msgId));
                                    XposedBridge.log("===== 撤回内容 %s"+ content);

                                    UserEntity userEntity = WechatDb.getInstance().selectSelf();
                                    String userName = userEntity.getUserName();
                                    String userTalker = userEntity.getUserTalker();
                                    String headPic = userEntity.getHeadPic();
                                    String alias = userEntity.getAlias();  //微信号
                                    XposedBridge.log("aliasaliasaliasalias::"+alias);
                                    handleMessageRecall(alias, Long.valueOf(msgId),"0");
                                    return;
                                }
                                return;
                            }
                           return;
                        }
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    private void handleMessageRecall(String wxno,Long msgId, String type) {

        OkGo.post(AppConfig.OUT_NETWORK+ NetApi.chatRecord+ "?" + "wxno=" +wxno+ "&msgId=" + msgId+"&type="+type).execute(new StringCallback() {
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

    protected void afterHookedMethod(@Nullable MethodHookParam param) {
        if (param == null) {
            Intrinsics.throwNpe();
        }
        String table = (String) param.args[0];
        if (table == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
        }
        table = table;
        ContentValues values = (ContentValues) param.args[1];
        if (values == null) {
            throw new TypeCastException("null cannot be cast to non-null type android.content.ContentValues");
        }
        values = values;
        String arg3 = (String) param.args[2];
        if (arg3 == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
        }
        arg3 = arg3;
        String[] arg4 = (String[]) param.args[3];
        /*XposedBridge.log("===== message table %s"+table);
        XposedBridge.log("===== message where %s"+ arg3);
        XposedBridge.log("===== message arg4 %s"+ Arrays.toString(arg4));
        XposedBridge.log("===== message values %s"+ values.toString());*/
        //HandleMessageConsumer.handle(this.$classLoader, this.$wechatEntity, values, table, arg3, arg4);
    }
}
