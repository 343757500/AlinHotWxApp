package com.mikuwxc.autoreply.wchook;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.mikuwxc.autoreply.common.net.NetApi;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.wcentity.MessageEntity;
import com.mikuwxc.autoreply.wcentity.UserEntity;
import com.mikuwxc.autoreply.wx.WechatDb;

import org.jetbrains.annotations.Nullable;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Call;
import okhttp3.Response;

/* compiled from: ReportDeleteWxMessageRiskOperateHook.kt */
public final class ReportDeleteWxMessageRiskOperateHook$hook$1 extends XC_MethodHook {
    ReportDeleteWxMessageRiskOperateHook$hook$1() {
    }

    protected void beforeHookedMethod(@Nullable MethodHookParam param) {
        int i = 1;
        if (param == null) {
            Intrinsics.throwNpe();
        }
        String arg1 = (String) param.args[0];
        if (arg1 == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
        }
        Object arg12 = arg1;
        String arg2 = (String) param.args[1];
        if (arg2 == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
        }
        Object arg22 = arg2;
        String[] arg3 = (String[]) param.args[2];
        if (Intrinsics.areEqual(arg12, (Object) "message") && Intrinsics.areEqual(arg22, (Object) "msgId=?") && arg3 != null) {
            int i2;
            if (arg3.length == 0) {
                i2 = 1;
            } else {
                boolean i22 = false;
            }
           /* if (arg22 != 0) {
                i = 0;
            }*/
            if (i != 0) {
                MessageEntity messageEntity = WechatDb.getInstance().selectMsgByMsgId(Long.parseLong(arg3[0]));
                Intrinsics.checkExpressionValueIsNotNull(messageEntity, "messageEntity");
                XposedBridge.log("messageEntitymessageEntity"+messageEntity.toString());

                UserEntity userEntity = WechatDb.getInstance().selectSelf();
                String userName = userEntity.getUserName();
                String userTalker = userEntity.getUserTalker();
                String headPic = userEntity.getHeadPic();
                String alias = userEntity.getAlias();  //微信号
                XposedBridge.log("aliasaliasaliasalias::"+alias);
                handleMessageDeleteWxMessage(alias, Long.valueOf(messageEntity.getMsgId()),"1");

                return;
            }
        }
    /*    if (Intrinsics.areEqual(arg12, (Object) "message") (CharSequence) " talker= '", false, 2, null) && arg3 == null) {
            String talker = ReportDeleteWxMessageRiskOperateHook.INSTANCE.extractTalker(arg22);
            if (talker != null) {
                switch (talker.hashCode()) {
                    case -952171561:
                        if (talker.equals("qqmail")) {
                            return;
                        }
                        break;
                    case -21328065:
                        if (talker.equals("blogapp")) {
                            return;
                        }
                        break;
                }
            }
            if (talker == null) {
                Intrinsics.throwNpe();
            }
        //    WxEventBus.publish(new WxConversationDeleted(talker));
        }*/
    }

    //统计删除聊天记录
    private void handleMessageDeleteWxMessage(String wxno,Long msgId, String type) {
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
}
