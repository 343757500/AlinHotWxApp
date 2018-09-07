package com.mikuwxc.autoreply.xposed;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.common.util.LogUtils;
import com.mikuwxc.autoreply.common.util.MyFileUtil;
import com.mikuwxc.autoreply.common.util.SharedPrefsUtils;
import com.mikuwxc.autoreply.common.util.Utils;
import com.mikuwxc.autoreply.receiver.Constance;
import com.mikuwxc.autoreply.receiver.MountReceiver;
import com.mikuwxc.autoreply.wcapi.WechatEntityFactory;
import com.mikuwxc.autoreply.wcentity.LuckyMoneyMessage;
import com.mikuwxc.autoreply.wcentity.UserEntity;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wchook.AddFriendHook;
import com.mikuwxc.autoreply.wchook.DonateHook;
import com.mikuwxc.autoreply.wchook.HideModule;
import com.mikuwxc.autoreply.wchook.LogWechatDbPathAndPwdHook;
import com.mikuwxc.autoreply.wchook.MessageHook;
import com.mikuwxc.autoreply.wchook.SensitiveHook;
import com.mikuwxc.autoreply.wchook.VersionParamNew;
import com.mikuwxc.autoreply.wchook.WalletHook;
import com.mikuwxc.autoreply.wcutil.PreferencesUtils;
import com.mikuwxc.autoreply.wcutil.XmlToJson;
import com.mikuwxc.autoreply.wx.WechatDb;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.text.TextUtils.isEmpty;
import static com.mikuwxc.autoreply.wchook.VersionParamNew.getNetworkByModelMethod;
import static com.mikuwxc.autoreply.wchook.VersionParamNew.getTransferRequest;
import static com.mikuwxc.autoreply.wchook.VersionParamNew.networkRequest;
import static com.mikuwxc.autoreply.wchook.VersionParamNew.receiveLuckyMoneyRequest;
import static de.robv.android.xposed.XposedBridge.log;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.findFirstFieldByExactType;
import static de.robv.android.xposed.XposedHelpers.newInstance;

/**
 * Created by Dusan (duqian) on 2017/5/6 - 16:29.
 * E-mail: duqian2010@gmail.com
 * Description:MainHook 入口函数
 * remarks:
 */
public class MainHook implements IXposedHookLoadPackage {
    private final String TAG = MainHook.class.getSimpleName();
    private static String wechat_package = "com.tencent.mm";
    private Context mContext;
    private HookMessage hookMessage;
    private CommonHook commonHook;



    //自动收红包相关
    private static String wechatVersion = "";
    private static List<LuckyMoneyMessage> luckyMoneyMessages = new ArrayList<>();
    private static Object requestCaller;



    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XSharedPreferences pre = new XSharedPreferences("com.mikuwxc.autoreply", "test");
        boolean test_put = pre.getBoolean("test_put", true);

        if (!wechat_package.equals(lpparam.packageName)) {
            return;
        }
        //判断是否具有全部微信权限
        if (test_put){
        XposedBridge.log("权限开启中");
        ClassLoader classLoader = lpparam.classLoader;
        commonHook = CommonHook.getInstance();
        if (mContext == null) {
            mContext = commonHook.getContext();
            Utils.init(mContext);
        }
        if (TextUtils.isEmpty(CommonHook.wechatVersionName)) {
            //获取wechat版本
            CommonHook.initWechatVersion(mContext);
            commonHook.showToast(mContext, "wechat hooked " + CommonHook.wechatVersionName);
            MyFileUtil.writeToNewFile(AppConfig.APP_FOLDER + "/version", CommonHook.wechatVersionName);
        }
        WechatEntity create = WechatEntityFactory.create(CommonHook.wechatVersionName);
        ClassLoader classLoader1 = lpparam.classLoader;
            //读取微信数据库聊天历史的时候要先初始化这个
        LogWechatDbPathAndPwdHook.hook(create,lpparam,classLoader1,mContext);
            //监听钱包
        WalletHook.hook(create, lpparam,classLoader1,mContext);

        //敏感词操作
            SensitiveHook.hook(create, lpparam,mContext);

            //加好友时需要hook到
            AddFriendHook.hook(create, lpparam);

        //操作微信相关
        Class receiver=classLoader.loadClass(Constance.receiver_wechat);
        XposedBridge.hookAllMethods(receiver,"onReceive",new MountReceiver());


        //聊天信息监听
        CommonHook.markAllActivity();
        try {
            if (hookMessage == null) {
                hookMessage = new HookMessage(classLoader, mContext,lpparam);
            }
            hookMessage.hookConversationItem();
        } catch (Exception e) {
            LogUtils.d(TAG, "hook error " + e);
        }
            XSharedPreferences moneyStaus = new XSharedPreferences("com.mikuwxc.autoreply", "moneyStaus");
            boolean moneyStaus_put = moneyStaus.getBoolean("moneyStaus_put", true);
            if (moneyStaus_put){
                //自动抢红包
                hookLuckyMoney(lpparam);
                XposedBridge.log("自动抢红包开启");
            }else{
                XposedBridge.log("自动抢红包关闭");
            }
        }else {
            XposedBridge.log("权限关闭中");
        }
    }

    private void parseResult(String result) {
        String text = "";
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("result") == 0) {
                text = "操作成功";
            } else {
                text = jsonObject.getString("errmsg");
            }
        } catch (Exception e) {
            text = "解析结果失败";
        }
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
    }


   private void hookLuckyMoney(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (isEmpty(wechatVersion)) {
            Context context = (Context) callMethod(callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
            String versionName = null;
            try {
                versionName = context.getPackageManager().getPackageInfo(loadPackageParam.packageName, 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            wechatVersion = versionName;
            new DonateHook().hook(loadPackageParam);
            VersionParamNew.init(versionName);
        }
        findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", loadPackageParam.classLoader, "insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ContentValues contentValues = (ContentValues) param.args[2];
                String tableName = (String) param.args[0];
                if (TextUtils.isEmpty(tableName) || !tableName.equals("message")) {
                    return;
                }
                Integer type = contentValues.getAsInteger("type");
                if (null == type) {
                    return;
                }
                if (type == 436207665 || type == 469762097) {
                    XposedBridge.log("---"+type);
                    handleLuckyMoney(contentValues, loadPackageParam);
                } else if (type == 419430449) {
                    handleTransfer(contentValues, loadPackageParam);
                }
            }
        });

        findAndHookMethod(receiveLuckyMoneyRequest, loadPackageParam.classLoader, "a", int.class, String.class, JSONObject.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws JSONException {
                          /* if (!VersionParam.hasTimingIdentifier) {
                                return;
                            }

                            if (luckyMoneyMessages.size() <= 0) {
                                return;
                            }*/

                        String timingIdentifier = ((JSONObject) (param.args[2])).getString("timingIdentifier");
                            /*if (isEmpty(timingIdentifier)) {
                                return;
                            }*/
                        LuckyMoneyMessage luckyMoneyMessage = luckyMoneyMessages.get(0);
                        Object luckyMoneyRequest = newInstance(findClass(VersionParamNew.luckyMoneyRequest, loadPackageParam.classLoader),
                                luckyMoneyMessage.getMsgType(), luckyMoneyMessage.getChannelId(), luckyMoneyMessage.getSendId(), luckyMoneyMessage.getNativeUrlString(), "", "", luckyMoneyMessage.getTalker(), "v1.0", timingIdentifier);
                        callMethod(requestCaller, "a", luckyMoneyRequest, getDelayTime());
                        luckyMoneyMessages.remove(0);
                    }
                }
        );

        findAndHookMethod(VersionParamNew.luckyMoneyReceiveUI, loadPackageParam.classLoader, VersionParamNew.receiveUIFunctionName, int.class, int.class, String.class, VersionParamNew.receiveUIParamName, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                // if (PreferencesUtils.quickOpen()) {
                Button button = (Button) findFirstFieldByExactType(param.thisObject.getClass(), Button.class).get(param.thisObject);
                if (button.isShown() && button.isClickable()) {
                    button.performClick();
                }
                // }
            }
        });

        findAndHookMethod("com.tencent.mm.plugin.profile.ui.ContactInfoUI", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                String wechatId = activity.getIntent().getStringExtra("Contact_User");
                cmb.setText(wechatId);
            }
        });

        findAndHookMethod("com.tencent.mm.plugin.chatroom.ui.ChatroomInfoUI", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                String wechatId = activity.getIntent().getStringExtra("RoomInfo_Id");
                ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(wechatId);
            }
        });

        new HideModule().hide(loadPackageParam);
    }



    private void handleLuckyMoney(ContentValues contentValues, XC_LoadPackage.LoadPackageParam lpparam) throws XmlPullParserException, IOException, JSONException {
       /* if (!PreferencesUtils.open()) {
            return;
        }*/

        int status = contentValues.getAsInteger("status");
       /* if (status == 4) {
            return;
        }*/

        String talker = contentValues.getAsString("talker");

     /*   String blackList = PreferencesUtils.blackList();
        if (!isEmpty(blackList)) {
            for (String wechatId : blackList.split(",")) {
                if (talker.equals(wechatId.trim())) {
                    return;
                }
            }
        }*/

        int isSend = contentValues.getAsInteger("isSend");
        /*if (isSend != 0) {
            return;
        }*/


       /* if (!isGroupTalk(talker)) {
            return;
        }

        if (!isGroupTalk(talker) && isSend != 0) {
            return;
        }*/

        String content = contentValues.getAsString("content");
        if (!content.startsWith("<msg")) {
            content = content.substring(content.indexOf("<msg"));
        }

        JSONObject wcpayinfo = new XmlToJson.Builder(content).build()
                .getJSONObject("msg").getJSONObject("appmsg").getJSONObject("wcpayinfo");
        String senderTitle = wcpayinfo.getString("sendertitle");
      /*  String notContainsWords = PreferencesUtils.notContains();
        if (!isEmpty(notContainsWords)) {
            for (String word : notContainsWords.split(",")) {
                if (senderTitle.contains(word)) {
                    return;
                }
            }
        }*/

        String nativeUrlString = wcpayinfo.getString("nativeurl");
        Uri nativeUrl = Uri.parse(nativeUrlString);
        int msgType = Integer.parseInt(nativeUrl.getQueryParameter("msgtype"));
        int channelId = Integer.parseInt(nativeUrl.getQueryParameter("channelid"));
        String sendId = nativeUrl.getQueryParameter("sendid");
        requestCaller = callStaticMethod(findClass(networkRequest, lpparam.classLoader), getNetworkByModelMethod);

        // if (VersionParam.hasTimingIdentifier) {
        callMethod(requestCaller, "a", newInstance(findClass(receiveLuckyMoneyRequest, lpparam.classLoader), channelId, sendId, nativeUrlString, 0, "v1.0"), 0);
        luckyMoneyMessages.add(new LuckyMoneyMessage(msgType, channelId, sendId, nativeUrlString, talker));
        //    return;
        // }
        Object luckyMoneyRequest = newInstance(findClass(VersionParamNew.luckyMoneyRequest, lpparam.classLoader),
                msgType, channelId, sendId, nativeUrlString, "", "", talker, "v1.0");

        callMethod(requestCaller, "a", luckyMoneyRequest, getDelayTime());
        XposedBridge.log("00000000000000");
    }

    private void handleTransfer(ContentValues contentValues, XC_LoadPackage.LoadPackageParam lpparam) throws IOException, XmlPullParserException, PackageManager.NameNotFoundException, InterruptedException, JSONException {
      /*  if (!PreferencesUtils.receiveTransfer()) {
            return;
        }*/
        JSONObject wcpayinfo = new XmlToJson.Builder(contentValues.getAsString("content")).build()
                .getJSONObject("msg").getJSONObject("appmsg").getJSONObject("wcpayinfo");

        int paysubtype = wcpayinfo.getInt("paysubtype");
        if (paysubtype != 1) {
            return;
        }

        String transactionId = wcpayinfo.getString("transcationid");
        String transferId = wcpayinfo.getString("transferid");
        int invalidtime = wcpayinfo.getInt("invalidtime");

        if (null == requestCaller) {
            requestCaller = callStaticMethod(findClass(networkRequest, lpparam.classLoader), getNetworkByModelMethod);
        }

        String talker = contentValues.getAsString("talker");
        callMethod(requestCaller, "a", newInstance(findClass(getTransferRequest, lpparam.classLoader), transactionId, transferId, 0, "confirm", talker, invalidtime), 0);
    }


    private int getDelayTime() {
        int delayTime = 0;
        if (PreferencesUtils.delay()) {
            delayTime = getRandom(PreferencesUtils.delayMin(), PreferencesUtils.delayMax());
        }
        return delayTime;
    }

    private boolean isGroupTalk(String talker) {
        return talker.endsWith("@chatroom");
    }


    private int getRandom(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }

}