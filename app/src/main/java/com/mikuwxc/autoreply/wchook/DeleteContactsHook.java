package com.mikuwxc.autoreply.wchook;


import com.mikuwxc.autoreply.wcentity.WechatEntity;

import org.jetbrains.annotations.NotNull;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DeleteContactsHook.kt */
public final class DeleteContactsHook {
    public static final DeleteContactsHook INSTANCE = new DeleteContactsHook();

    private DeleteContactsHook() {
    }

    @JvmStatic
    public static final void hook(@NotNull WechatEntity wechatEntity, @NotNull LoadPackageParam loadPackageParam) {
        Intrinsics.checkParameterIsNotNull(wechatEntity, "wechatEntity");
        Intrinsics.checkParameterIsNotNull(loadPackageParam, "loadPackageParam");
        ClassLoader classLoader = loadPackageParam.classLoader;
        XposedHelpers.findAndHookConstructor(wechatEntity.delete_contact_class3, classLoader, new Object[]{String.class, new DeleteContactsHook$hook$1()});
        Class class_model_s = XposedHelpers.findClass(wechatEntity.forbidden_friend_class1, classLoader);
        Class class_storage_ab = XposedHelpers.findClass(wechatEntity.forbidden_friend_class2, classLoader);
        XposedHelpers.findAndHookMethod(class_model_s, wechatEntity.forbidden_friend_method1, new Object[]{class_storage_ab, new DeleteContactsHook$hook$2()});
        XposedHelpers.findAndHookMethod(class_model_s, wechatEntity.recorver_forbidden_friend_method1, new Object[]{class_storage_ab, new DeleteContactsHook$hook$3()});
    }
}
