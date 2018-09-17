package com.mikuwxc.autoreply.wchook;

import android.content.ClipData.Item;
import android.text.SpannableString;
import android.widget.TextView.BufferType;

import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wcutil.AuthUtil;
import com.mikuwxc.autoreply.wcutil.FileIoUtil;
import com.mikuwxc.autoreply.wcutil.GlobalUtil;
import com.mikuwxc.autoreply.wcutil.OtherUtils;
import com.orhanobut.logger.Logger;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HiddenWechatIdAndPhoneNumberHook {
    public static void hookSystem(LoadPackageParam param) {
        XposedHelpers.findAndHookConstructor(Item.class, new Object[]{CharSequence.class, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (AuthUtil.isForbiddenAuth(15)) {
                    param.args[0] = "";
                }
            }
        }});
/*        XposedHelpers.findAndHookMethod("android.widget.TextView", param.classLoader, "setText", new Object[]{CharSequence.class, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) {
                CharSequence value = (CharSequence) param.args[0];
                if (value != null) {
                    String showWord = value.toString();
                    if (showWord.contains("微信号: ")) {
                       // if (AuthUtil.isForbiddenAuth(7)) {
                            param.args[0] = "微信号: 暂不可显示1";
                       // }
                    } else if (AuthUtil.isForbiddenAuth(9) && !showWord.contains("未获取") && !showWord.contains("编辑") && !showWord.contains("撤回") && !showWord.contains("退还")) {
                       // FileIoUtil.setValueToPath(showWord + "\n", true, "/mnt/sdcard/11.txt");
                        param.args[0] = HiddenWechatIdAndPhoneNumberHook.checkCellphone(showWord.replace(HiddenWechatIdAndPhoneNumberHook.hexStringToString("e280aa20"), "").replace(StringUtils.SPACE, ""), FileIoUtil.getValueFromPath(GlobalUtil.PHONE_MATCHER_SAVE_PATH));
                    }
                }
            }
        }});*/
        XposedHelpers.findAndHookMethod("android.widget.TextView", param.classLoader, "setText", new Object[]{CharSequence.class, BufferType.class, Boolean.TYPE, Integer.TYPE, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) {

                XSharedPreferences moneyStaus = new XSharedPreferences("com.mikuwxc.autoreply", "canSeewxStaus");
                boolean canSeewxStaus_put = moneyStaus.getBoolean("canSeewxStaus_put", true);

                CharSequence value = (CharSequence) param.args[0];
                if (value != null) {
                    String showWord = value.toString();
                    if (showWord.contains("微信号: ")) {
                       // if (AuthUtil.isForbiddenAuth(7)) {
                        if (canSeewxStaus_put){

                        }else {
                            param.args[0] = "微信号: 暂不可显示";
                        }

                      //  }
                    } else if (AuthUtil.isForbiddenAuth(9) && !showWord.contains("未获取") && !showWord.contains("编辑") && !showWord.contains("撤回") && !showWord.contains("退还")) {
                      //  FileIoUtil.setValueToPath(showWord + "\n", true, "/mnt/sdcard/22.txt");
                        param.args[0] = HiddenWechatIdAndPhoneNumberHook.checkCellphone(showWord.replace(HiddenWechatIdAndPhoneNumberHook.hexStringToString("e280aa20"), "").replace(StringUtils.SPACE, ""), FileIoUtil.getValueFromPath(GlobalUtil.PHONE_MATCHER_SAVE_PATH));
                    }
                }
            }
        }});
    }

    public static String hexStringToString(String split) {
        Exception e1;
        if (split == null || split.equals("")) {
            return null;
        }
        split = split.replace(StringUtils.SPACE, "");
        byte[] baKeyword = new byte[(split.length() / 2)];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (Integer.parseInt(split.substring(i * 2, (i * 2) + 2), 16) & 255);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            String split2 = new String(baKeyword, CharEncoding.UTF_8);
            try {
                String str = new String();
                return split2;
            } catch (Exception e2) {
                e1 = e2;
                split = split2;
                e1.printStackTrace();
                return split;
            }
        } catch (Exception e3) {
            e1 = e3;
            e1.printStackTrace();
            return split;
        }
    }

    public static void hookWechat(LoadPackageParam param, WechatEntity wechatEntity) {
        ClassLoader wxClassLoader = param.classLoader;
        XposedHelpers.findAndHookMethod("com.tencent.mm.ui.AddressView", wxClassLoader, "setName", new Object[]{CharSequence.class, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) {
                CharSequence value = (CharSequence) param.args[0];
                if (value != null) {
                    String showWord = value.toString();
                    if (AuthUtil.isForbiddenAuth(9) && !showWord.contains("退还")) {
                        param.args[0] = HiddenWechatIdAndPhoneNumberHook.checkCellphone(showWord, FileIoUtil.getValueFromPath(GlobalUtil.WX_MATCHER_SAVE_PATH));
                    }
                }
            }
        }});
        XposedHelpers.findAndHookMethod("com.tencent.mm.ui.base.NoMeasuredTextView", wxClassLoader, "setText", new Object[]{CharSequence.class, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) {
                CharSequence value = (CharSequence) param.args[0];
                if (value != null) {
                    String showWord = value.toString();
                    if (AuthUtil.isForbiddenAuth(9) && !showWord.contains("退还")) {
                        param.args[0] = HiddenWechatIdAndPhoneNumberHook.checkCellphone(showWord, FileIoUtil.getValueFromPath(GlobalUtil.WX_MATCHER_SAVE_PATH));
                    }
                }
            }
        }});
        XposedHelpers.findAndHookConstructor(wechatEntity.sns_forbidden_show_number_class1, wxClassLoader, new Object[]{SpannableString.class, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String mText = param.args[0].toString();
                if (AuthUtil.isForbiddenAuth(9)) {
                    mText = mText.replace(StringUtils.SPACE, "");
                    if (!mText.contains("撤回") && !mText.contains("编辑") && !mText.contains("退还")) {
                        param.args[0] = new SpannableString(HiddenWechatIdAndPhoneNumberHook.checkCellphone(mText, FileIoUtil.getValueFromPath(GlobalUtil.WX_MATCHER_SAVE_PATH)));
                    }
                }
            }
        }});
        XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.text.TextUtils", wxClassLoader), "stringOrSpannedString", new Object[]{CharSequence.class, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                CharSequence value = (CharSequence) param.args[0];
                if (value != null) {
                    String showWord = value.toString();
                    if (AuthUtil.isForbiddenAuth(8)) {
                        param.args[0] = HiddenWechatIdAndPhoneNumberHook.checkCellphone(showWord, FileIoUtil.getValueFromPath(GlobalUtil.WX_MATCHER_SAVE_PATH));
                    }
                }
            }
        }});
    }

    public static String checkCellphone(String str, String matcherValue) {
        try {
            if (OtherUtils.isEmpty(matcherValue)) {
                matcherValue = GlobalUtil.TEXT_MATCHER;
            }
            Pattern pattern = Pattern.compile(matcherValue);
            if (!OtherUtils.isEmpty(pattern)) {
                Matcher matcher = pattern.matcher(str);
                if (!OtherUtils.isEmpty(matcher)) {
                    String telNum = "";
                    while (matcher.find()) {
                        telNum = matcher.group();
                    }
                    String replaceTelNum = "";
                    if (!"".equals(telNum) && telNum.length() >= 6) {
                        String header = telNum.substring(0, 2);
                        String footer = telNum.substring(telNum.length() - 2);
                        String replace = "";
                        int replaceLength = telNum.length() - 4;
                        for (int loop = 0; loop < replaceLength; loop++) {
                            replace = replace + "*";
                        }
                        replaceTelNum = header + replace + footer;
                    }
                    if (!"".equals(replaceTelNum)) {
                        str = str.replace(telNum, replaceTelNum);
                    }
                    Logger.i("===== MATCHER %s", str);
                }
            }
        } catch (Exception e) {
            Logger.e(e, "===== MATCHER ERROR", new Object[0]);
        }
        return str;
    }
}
