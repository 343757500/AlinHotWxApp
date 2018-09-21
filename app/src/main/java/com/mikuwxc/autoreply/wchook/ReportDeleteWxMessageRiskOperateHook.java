package com.mikuwxc.autoreply.wchook;

import android.content.ContentValues;


import com.mikuwxc.autoreply.wcentity.RequestParameters;
import com.mikuwxc.autoreply.wcentity.WechatEntity;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;

/* compiled from: ReportDeleteWxMessageRiskOperateHook.kt */
public final class ReportDeleteWxMessageRiskOperateHook {
    public static final ReportDeleteWxMessageRiskOperateHook INSTANCE = new ReportDeleteWxMessageRiskOperateHook();
    private static final Pattern compilePattern = Pattern.compile(" talker= '(.*)'");

    private ReportDeleteWxMessageRiskOperateHook() {
    }

    @JvmStatic
    public static final void hook(@NotNull WechatEntity wechatEntity, @NotNull LoadPackageParam param) {
        Intrinsics.checkParameterIsNotNull(wechatEntity, "wechatEntity");
        Intrinsics.checkParameterIsNotNull(param, "param");
        ClassLoader classLoader = param.classLoader;
        String dbClassName = wechatEntity.sqlitedatabase_class_name;
        XposedHelpers.findAndHookMethod(dbClassName, classLoader, RequestParameters.SUBRESOURCE_DELETE, new Object[]{String.class, String.class, String[].class, new ReportDeleteWxMessageRiskOperateHook$hook$1()});
        XposedHelpers.findAndHookMethod(dbClassName, classLoader, "updateWithOnConflict", new Object[]{String.class, ContentValues.class, String.class, String[].class, JvmClassMappingKt.getJavaPrimitiveType(Reflection.getOrCreateKotlinClass(Integer.TYPE)), new ReportDeleteWxMessageRiskOperateHook$hook$2(classLoader, wechatEntity)});
    }

    private final String extractTalker(String inStr) {
        String rts = null;
        try {
            Matcher m = compilePattern.matcher(inStr);
            while (m.find()) {
                rts = m.group(1);
            }
            if (rts == null) {
                return rts;
            }
            CharSequence $receiver$iv$iv = rts;
            int startIndex$iv$iv = 0;
            int endIndex$iv$iv = $receiver$iv$iv.length() - 1;
            boolean startFound$iv$iv = false;
            while (startIndex$iv$iv <= endIndex$iv$iv) {
                int index$iv$iv;
                boolean match$iv$iv;
                if (startFound$iv$iv) {
                    index$iv$iv = endIndex$iv$iv;
                } else {
                    index$iv$iv = startIndex$iv$iv;
                }
                if ($receiver$iv$iv.charAt(index$iv$iv) <= ' ') {
                    match$iv$iv = true;
                } else {
                    match$iv$iv = false;
                }
                if (startFound$iv$iv) {
                    if (!match$iv$iv) {
                        break;
                    }
                    endIndex$iv$iv--;
                } else if (match$iv$iv) {
                    startIndex$iv$iv++;
                } else {
                    startFound$iv$iv = true;
                }
            }
            return $receiver$iv$iv.subSequence(startIndex$iv$iv, endIndex$iv$iv + 1).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return rts;
        }
    }
}
