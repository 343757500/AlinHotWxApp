package com.mikuwxc.autoreply.wchook;

import com.mikuwxc.autoreply.wcutil.AuthUtil;
import com.mikuwxc.autoreply.wcutil.RiskActionUtil;
import com.mikuwxc.autoreply.xposed.MainHook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ReportVideoCallAndVoiceCallRiskOperateHook.kt */
public final class ReportVideoCallAndVoiceCallRiskOperateHook$hook$1 extends XC_MethodHook {
    ReportVideoCallAndVoiceCallRiskOperateHook$hook$1() {
    }

    protected void beforeHookedMethod(@NotNull MethodHookParam param) throws Throwable {
        Intrinsics.checkParameterIsNotNull(param, "param");
        XposedBridge.log("parm1"+param.method.toString());
        RiskActionUtil.send(26, true);
        if (AuthUtil.isForbiddenAuth(3)) {

        }
        //WxEventBus.publish(new WxVideoCallStart(2));
    }
}
