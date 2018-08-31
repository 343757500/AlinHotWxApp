package com.mikuwxc.autoreply.wcutil;

import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class WalletUtil {
    public static void confirmTransferAccounts(ClassLoader classLoader, WechatEntity wechatEntity, String str, String str2, String str3, int i) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("transactionId", str);
            jSONObject.put("transferId", str2);
            jSONObject.put("invalidTime", i);
            jSONObject.put("sendWechatId", str3);
            FileIoUtil.setValueToPath(jSONObject.toString(), false, GlobalUtil.TRANS_VALUE_BASE_SAVE_PATH);
            Object callStaticMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.confirm_transfer_accounts_class1, classLoader), wechatEntity.confirm_transfer_accounts_method1, new Object[0]);
            Class findClass = XposedHelpers.findClass(wechatEntity.confirm_transfer_accounts_class2, classLoader);
            FileIoUtil.setValueToPath("0", false, GlobalUtil.TRANS_STEP_BASE_SAVE_PATH);
            Object newInstance = XposedHelpers.newInstance(findClass, new Object[]{Integer.valueOf(0), str, str2, Integer.valueOf(i)});
            XposedHelpers.callMethod(callStaticMethod, wechatEntity.confirm_transfer_accounts_method2, new Object[]{newInstance, Integer.valueOf(0)});
        } catch (Throwable e) {
            Logger.e(e, "===== CONFIRM TRANS ===== ERROR!!!", new Object[0]);
        }
    }

    public static void openLuckyMoney(ClassLoader classLoader, WechatEntity wechatEntity, String str, String str2) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("payMsgId", str);
            jSONObject.put("nativeurl", str2);
            FileIoUtil.setValueToPath(jSONObject.toString(), false, GlobalUtil.LUCKY_MONEY_VALUE_BASE_SAVE_PATH);
            XposedBridge.log("123456");
            Object newInstance = XposedHelpers.newInstance(XposedHelpers.findClass(wechatEntity.open_lucky_money_class1, classLoader), new Object[]{Integer.valueOf(1), str, str2, Integer.valueOf(1), "v1.0"});
            XposedBridge.log("789101112");
            XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.open_lucky_money_class2, classLoader), wechatEntity.open_lucky_money_method1, new Object[0]), wechatEntity.open_lucky_money_method2, new Object[]{newInstance, Integer.valueOf(0)});
            XposedBridge.log("987654321");
        } catch (Throwable e) {
            XposedBridge.log(e.toString()+"===== CONFIRM LUCKY MONEY ===== ERROR!!!"+ new Object[0]);
        }
    }
}