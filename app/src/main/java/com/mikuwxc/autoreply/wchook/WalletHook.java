package com.mikuwxc.autoreply.wchook;

import android.content.Context;

import com.mikuwxc.autoreply.wcentity.MessageEntity;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wx.WechatDb;
import com.mikuwxc.autoreply.xposed.HookMessage;

import org.json.JSONObject;

import java.math.BigDecimal;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class WalletHook
{
  public static void hook(WechatEntity paramWechatEntity, final XC_LoadPackage.LoadPackageParam paramLoadPackageParam, final ClassLoader classLoader1, final Context context)
  {
    final ClassLoader localClassLoader = paramLoadPackageParam.classLoader;
    Class localClass = XposedHelpers.findClass(paramWechatEntity.confirm_transfer_accounts_class3, localClassLoader);
    Class paramLoadPackageParam1 = XposedHelpers.findClass(paramWechatEntity.confirm_transfer_accounts_class2, localClassLoader);
    /*XposedHelpers.findAndHookMethod(localClass, paramWechatEntity.confirm_transfer_accounts_method6, new Object[] { Integer.TYPE, String.class, JSONObject.class, new XC_MethodHook()
    {
      protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
              throws Throwable
      {
        paramAnonymousMethodHookParam = new JSONObject(FileIoUtil.getValueFromPath(new String[] { GlobalUtil.TRANS_VALUE_BASE_SAVE_PATH }));
        String str1 = paramAnonymousMethodHookParam.getString("transactionId");
        String str2 = paramAnonymousMethodHookParam.getString("transferId");
        int i = paramAnonymousMethodHookParam.getInt("invalidTime");
        paramAnonymousMethodHookParam = XposedHelpers.newInstance(XposedHelpers.findClass(this.val$wechatEntity.confirm_transfer_accounts_class4, localClassLoader), new Object[] { Integer.valueOf(1), str1, str2, Integer.valueOf(i) });
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(this.val$wechatEntity.confirm_transfer_accounts_class1, localClassLoader), this.val$wechatEntity.confirm_transfer_accounts_method1, new Object[0]), this.val$wechatEntity.confirm_transfer_accounts_method7, new Object[] { paramAnonymousMethodHookParam, Integer.valueOf(0) });
        FileIoUtil.setValueToPath("2", false, new String[] { GlobalUtil.TRANS_STEP_BASE_SAVE_PATH });
      }
    } });
    XposedHelpers.findAndHookMethod(paramLoadPackageParam, paramWechatEntity.confirm_transfer_accounts_method4, new Object[] { Integer.TYPE, String.class, JSONObject.class, new XC_MethodHook()
    {
      protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
              throws Throwable
      {
        if (FileIoUtil.getValueFromPath(new String[] { GlobalUtil.TRANS_STEP_BASE_SAVE_PATH }).equals("0"))
        {
          Object localObject = new JSONObject(FileIoUtil.getValueFromPath(new String[] { GlobalUtil.TRANS_VALUE_BASE_SAVE_PATH }));
          paramAnonymousMethodHookParam = ((JSONObject)localObject).getString("transactionId");
          String str = ((JSONObject)localObject).getString("transferId");
          int i = ((JSONObject)localObject).getInt("invalidTime");
          localObject = ((JSONObject)localObject).getString("sendWechatId");
          paramAnonymousMethodHookParam = XposedHelpers.newInstance(XposedHelpers.findClass(this.val$wechatEntity.confirm_transfer_accounts_class3, localClassLoader), new Object[] { paramAnonymousMethodHookParam, str, Integer.valueOf(0), "confirm", localObject, Integer.valueOf(i) });
          XposedHelpers.setObjectField(paramAnonymousMethodHookParam, this.val$wechatEntity.confirm_transfer_accounts_field1, "RemittanceProcess");
          XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(this.val$wechatEntity.confirm_transfer_accounts_class1, localClassLoader), this.val$wechatEntity.confirm_transfer_accounts_method1, new Object[0]), this.val$wechatEntity.confirm_transfer_accounts_method5, new Object[] { paramAnonymousMethodHookParam, Integer.valueOf(0) });
          FileIoUtil.setValueToPath("1", false, new String[] { GlobalUtil.TRANS_STEP_BASE_SAVE_PATH });
        }
        for (;;)
        {
          return;
          FileIoUtil.setValueToPath("2", false, new String[] { GlobalUtil.TRANS_STEP_BASE_SAVE_PATH });
        }
      }
    } });
    XposedHelpers.findAndHookMethod(XposedHelpers.findClass(paramWechatEntity.open_lucky_money_class1, localClassLoader), paramWechatEntity.open_lucky_money_method3, new Object[] { Integer.TYPE, String.class, JSONObject.class, new XC_MethodHook()
    {
      protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
              throws Throwable
      {
        Object localObject = new JSONObject(paramAnonymousMethodHookParam.args[2].toString());
        paramAnonymousMethodHookParam = ((JSONObject)localObject).optString("timingIdentifier");
        String str = ((JSONObject)localObject).optString("sendUserName");
        localObject = XposedHelpers.findClass(this.val$wechatEntity.open_lucky_money_class3, localClassLoader);
        UserEntity localUserEntity = WechatDb.getInstance().selectSelf();
        JSONObject localJSONObject = new JSONObject(FileIoUtil.getValueFromPath(new String[] { GlobalUtil.LUCKY_MONEY_VALUE_BASE_SAVE_PATH }));
        paramAnonymousMethodHookParam = XposedHelpers.newInstance((Class)localObject, new Object[] { Integer.valueOf(1), Integer.valueOf(1), localJSONObject.getString("payMsgId"), localJSONObject.getString("nativeurl"), localUserEntity.getHeadPic(), localUserEntity.getUserName(), str, "v1.0", paramAnonymousMethodHookParam });
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(this.val$wechatEntity.open_lucky_money_class4, localClassLoader), this.val$wechatEntity.open_lucky_money_method4, new Object[0]), this.val$wechatEntity.open_lucky_money_method5, new Object[] { paramAnonymousMethodHookParam, Integer.valueOf(0) });
      }
    } });*/
    XposedHelpers.findAndHookMethod(paramWechatEntity.open_lucky_money_class3, localClassLoader, paramWechatEntity.open_lucky_money_method6, new Object[] { Integer.TYPE, String.class, JSONObject.class, new XC_MethodHook()
    {
      protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
              throws Throwable
      {
        JSONObject paramAnonymousMethodHookParam1 = (JSONObject)paramAnonymousMethodHookParam.args[2];
        XposedBridge.log("WWWWWWWWWWWWWWWW"+paramAnonymousMethodHookParam1.toString());
       // FileIoUtil.setValueToPath(paramAnonymousMethodHookParam.toString() + "\n", true, new String[] { "/mnt/sdcard/testlucky.txt" });
        if (!paramAnonymousMethodHookParam1.isNull("amount"))
        {
          int i = paramAnonymousMethodHookParam1.getInt("amount");
          String sendUserName = paramAnonymousMethodHookParam1.getString("sendUserName");
          String sessionUserName = paramAnonymousMethodHookParam1.getString("sessionUserName");
          MessageEntity paramAnonymousMethodHookParam2 = new MessageEntity();
          paramAnonymousMethodHookParam2.setContent("你领取的金额是:[" + BigDecimal.valueOf(i).divide(new BigDecimal(100)).toString() + "]元");
          XposedBridge.log("WWWWWWWWWWWWWWWW"+"你领取的金额是:[" + BigDecimal.valueOf(i).divide(new BigDecimal(100)).toString() + "]元");
          paramAnonymousMethodHookParam2.setType(55535);
          paramAnonymousMethodHookParam2.setTalker(sendUserName);
          paramAnonymousMethodHookParam2.setCreateTime(System.currentTimeMillis());
          paramAnonymousMethodHookParam2.setSelf(WechatDb.getInstance().selectSelf().getUserTalker());
         // LocalSocketClient.getInstance().uploadMessage(paramAnonymousMethodHookParam);
            XposedBridge.log("WWWWWWWWWWWWWWWW"+paramAnonymousMethodHookParam2.toString());

          HookMessage hookMessage=new HookMessage(classLoader1, context,paramLoadPackageParam);
          hookMessage.handleMessage(0, 3, sessionUserName, "你领取的金额是:[" + BigDecimal.valueOf(i).divide(new BigDecimal(100)).toString() + "]元", "55535", System.currentTimeMillis(),"");
          XposedBridge.log("同步金额成功");

        }
      }
    } });
  }
}
