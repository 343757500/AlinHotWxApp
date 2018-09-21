package com.mikuwxc.autoreply.wchook;


import com.mikuwxc.autoreply.wcentity.WechatEntity;

import org.jetbrains.annotations.NotNull;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;

/* compiled from: WeChatWebLoginHook.kt */
public final class WeChatWebLoginHook {
    public static final WeChatWebLoginHook INSTANCE = new WeChatWebLoginHook();

    private WeChatWebLoginHook() {
    }

    @JvmStatic
    public static final void hook(@NotNull WechatEntity wechatEntity, @NotNull LoadPackageParam loadPackageParam) {
        Intrinsics.checkParameterIsNotNull(wechatEntity, "wechatEntity");
        Intrinsics.checkParameterIsNotNull(loadPackageParam, "loadPackageParam");
        XposedHelpers.findAndHookConstructor(wechatEntity.wechat_web_login_class1, loadPackageParam.classLoader, new Object[]{String.class, String.class, JvmClassMappingKt.getJavaPrimitiveType(Reflection.getOrCreateKotlinClass(Boolean.TYPE)), new WeChatWebLoginHook$hook$1()});
    }
}
