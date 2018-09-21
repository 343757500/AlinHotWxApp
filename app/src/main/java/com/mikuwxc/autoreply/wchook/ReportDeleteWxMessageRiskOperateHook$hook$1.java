package com.mikuwxc.autoreply.wchook;

import com.mikuwxc.autoreply.wcentity.MessageEntity;
import com.mikuwxc.autoreply.wx.WechatDb;

import org.jetbrains.annotations.Nullable;

import de.robv.android.xposed.XC_MethodHook;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

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
               // WxEventBus.publish(new WxMessageDeleted(messageEntity));
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
}
