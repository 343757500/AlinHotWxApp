package com.mikuwxc.autoreply.wchook;

import android.os.Bundle;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public final class ReportVideoCallAndVoiceCallRiskOperateHook {
    public static final ReportVideoCallAndVoiceCallRiskOperateHook INSTANCE = new ReportVideoCallAndVoiceCallRiskOperateHook();

    private ReportVideoCallAndVoiceCallRiskOperateHook() {
    }

    @JvmStatic
    public static final void hook(@NotNull LoadPackageParam param) throws ClassNotFoundException {
        Intrinsics.checkParameterIsNotNull(param, "param");
        ClassLoader wxClassLoader = param.classLoader;
        XposedHelpers.findAndHookMethod(wxClassLoader.loadClass("com.tencent.mm.plugin.voip.ui.VideoActivity"), "onCreate", new Object[]{Bundle.class, new ReportVideoCallAndVoiceCallRiskOperateHook$hook$1()});
        XposedHelpers.findAndHookMethod(wxClassLoader.loadClass("com.tencent.mm.plugin.voip.ui.VideoActivity"), "onDestroy", new Object[]{new ReportVideoCallAndVoiceCallRiskOperateHook$hook$2()});

    }
}
