package com.mikuwxc.autoreply.wchook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public final class ReportVideoCallAndVoiceCallRiskOperateHook$hook$2 extends XC_MethodHook {
    ReportVideoCallAndVoiceCallRiskOperateHook$hook$2() {
    }

    protected void beforeHookedMethod(@NotNull MethodHookParam param) {
        Intrinsics.checkParameterIsNotNull(param, "param");
        XposedBridge.log("parm2"+param);
        //WxEventBus.publish(new WxVideoCallEnd());
    }
}
