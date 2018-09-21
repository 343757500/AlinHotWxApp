package com.mikuwxc.autoreply.wchook;

import org.jetbrains.annotations.Nullable;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DeleteContactsHook.kt */
public final class DeleteContactsHook$hook$2 extends XC_MethodHook {
    DeleteContactsHook$hook$2() {
    }

    protected void afterHookedMethod(@Nullable MethodHookParam param) throws Throwable {
        if (param == null) {
            Intrinsics.throwNpe();
        }
        XposedBridge.log("===== block contact2 %s"+ XposedHelpers.getObjectField(param.args[0], "field_username").toString());
    }
}
