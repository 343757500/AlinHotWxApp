package com.mikuwxc.autoreply;

import android.app.Activity;
import android.app.Application;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.mikuwxc.autoreply.common.util.MyFileUtil;
import com.mikuwxc.autoreply.modle.FriendBean;
import com.mikuwxc.autoreply.wcentity.LuckyMoneyMessage;
import com.mikuwxc.autoreply.wchook.DonateHook;
import com.mikuwxc.autoreply.wchook.HideModule;
import com.mikuwxc.autoreply.wchook.VersionParamNew;
import com.mikuwxc.autoreply.wcutil.PreferencesUtils;
import com.mikuwxc.autoreply.wcutil.XmlToJson;
import com.mikuwxc.autoreply.xposed.MainHook;
import com.mikuwxc.autoreply.xposed.VersionParam;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dalvik.system.PathClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.text.TextUtils.isEmpty;
import static android.widget.Toast.LENGTH_LONG;
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
 * @author DX
 * 这种方案建议只在开发调试的时候使用，因为这将损耗一些性能(需要额外加载apk文件)，调试没问题后，直接修改xposed_init文件为正确的类即可
 * 可以实现免重启，由于存在缓存，需要杀死宿主程序以后才能生效
 * 这种免重启的方式针对某些特殊情况的hook无效
 * 例如我们需要implements IXposedHookZygoteInit,并将自己的一个服务注册为系统服务，这种就必须重启了
 * Created by DX on 2017/10/4.
 */

public class HookLoader implements IXposedHookLoadPackage {
    //按照实际使用情况修改下面几项的值
    /**
     * 当前Xposed模块的包名,方便寻找apk文件
     */
    private final String modulePackage = "com.mikuwxc.autoreply";
    /**
     * 宿主程序的包名(允许多个),过滤无意义的包名,防止无意义的apk文件加载
     */
    private static List<String> hostAppPackages = new ArrayList<>();

    static {
        // TODO: Add the package name of application your want to hook!
        hostAppPackages.add("com.tencent.mm");
    }

    /**
     * 实际hook逻辑处理类
     */
    private final String handleHookClass = MainHook.class.getName();
    /**
     * 实际hook逻辑处理类的入口方法
     */
    private final String handleHookMethod = "handleLoadPackage";


    //自动收红包相关
    private static String wechatVersion = "";
    private static List<LuckyMoneyMessage> luckyMoneyMessages = new ArrayList<>();
    private static Object requestCaller;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
//        LogUtils.i("HookLoader","*****************  要启动HookLoader  **************");


        if (hostAppPackages.contains(loadPackageParam.packageName)) {
            //将loadPackageParam的classloader替换为宿主程序Application的classloader,解决宿主程序存在多个.dex文件时,有时候ClassNotFound的问题
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Context context = (Context) param.args[0];
                    loadPackageParam.classLoader = context.getClassLoader();
                    invokeHandleHookMethod(context, modulePackage, handleHookClass, handleHookMethod, loadPackageParam);
                }
            });

        }


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
            log("Found wechat version:" + versionName);
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
                        XposedBridge.log("11111111111111111.");
                    }
                }
        );

        findAndHookMethod(VersionParamNew.luckyMoneyReceiveUI, loadPackageParam.classLoader, VersionParamNew.receiveUIFunctionName, int.class, int.class, String.class, VersionParamNew.receiveUIParamName, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                // if (PreferencesUtils.quickOpen()) {
                XposedBridge.log("2222222222222222222222.");
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
                // if (PreferencesUtils.showWechatId()) {
                Activity activity = (Activity) param.thisObject;
                ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                String wechatId = activity.getIntent().getStringExtra("Contact_User");
                cmb.setText(wechatId);
              //  Toast.makeText(activity, "微信ID:" + wechatId + "已复制到剪切板", LENGTH_LONG).show();
                //  }
            }
        });

        findAndHookMethod("com.tencent.mm.plugin.chatroom.ui.ChatroomInfoUI", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                //if (PreferencesUtils.showWechatId()) {
                Activity activity = (Activity) param.thisObject;
                String wechatId = activity.getIntent().getStringExtra("RoomInfo_Id");
                ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(wechatId);
             //   Toast.makeText(activity, "微信ID:" + wechatId + "已复制到剪切板", LENGTH_LONG).show();
                //   }
            }
        });

        new HideModule().hide(loadPackageParam);
    }

    /**
     * 安装app以后，系统会在/data/app/下备份了一份.apk文件，通过动态加载这个apk文件，调用相应的方法
     * 这样就可以实现，只需要第一次重启，以后修改hook代码就不用重启了
     *
     * @param context           context参数
     * @param modulePackageName 当前模块的packageName
     * @param handleHookClass   指定由哪一个类处理相关的hook逻辑
     * @param loadPackageParam  传入XC_LoadPackage.LoadPackageParam参数
     * @throws Throwable 抛出各种异常,包括具体hook逻辑的异常,寻找apk文件异常,反射加载Class异常等
     */
    private void invokeHandleHookMethod(Context context, String modulePackageName, String handleHookClass, String handleHookMethod, XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
//        File apkFile = findApkFileBySDK(modulePackageName);//会受其它Xposed模块hook 当前宿主程序的SDK_INT的影响
//        File apkFile = findApkFile(modulePackageName);
        //原来的两种方式不是很好,改用这种新的方式
        File apkFile = findApkFile(context, modulePackageName);
        if (apkFile == null) {
            throw new RuntimeException("寻找模块apk失败");
        }
        //加载指定的hook逻辑处理类，并调用它的handleHook方法
        PathClassLoader pathClassLoader = new PathClassLoader(apkFile.getAbsolutePath(), ClassLoader.getSystemClassLoader());
        Class<?> cls = Class.forName(handleHookClass, true, pathClassLoader);
        Object instance = cls.newInstance();
        Method method = cls.getDeclaredMethod(handleHookMethod, XC_LoadPackage.LoadPackageParam.class);
        method.invoke(instance, loadPackageParam);
    }

    /**
     * 根据包名构建目标Context,并调用getPackageCodePath()来定位apk
     *
     * @param context           context参数
     * @param modulePackageName 当前模块包名
     * @return return apk file
     */
    private File findApkFile(Context context, String modulePackageName) {
        if (context == null) {
            return null;
        }
        try {
            Context moudleContext = context.createPackageContext(modulePackageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            String apkPath = moudleContext.getPackageCodePath();
            return new File(apkPath);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 寻找这个Android设备上的当前apk文件,不受其它Xposed模块hook SDK_INT的影响
     *
     * @param modulePackageName 当前模块包名
     * @return File 返回apk文件
     * @throws FileNotFoundException 在/data/app/下的未找到本模块apk文件,请检查本模块包名配置是否正确.
     *                               具体检查build.gradle中的applicationId和AndroidManifest.xml中的package
     */
    @Deprecated
    private File findApkFile(String modulePackageName) throws FileNotFoundException {
        File apkFile = null;
        try {
            apkFile = findApkFileAfterSDK21(modulePackageName);
        } catch (Exception e) {
            try {
                apkFile = findApkFileBeforeSDK21(modulePackageName);
            } catch (Exception e2) {
                //忽略这个异常
            }
        }
        if (apkFile == null) {
            throw new FileNotFoundException("没在/data/app/下找到文件对应的apk文件");
        }
        return apkFile;
    }

    /**
     * 根据当前的SDK_INT寻找这个Android设备上的当前apk文件
     *
     * @param modulePackageName 当前模块包名
     * @return File 返回apk文件
     * @throws FileNotFoundException 在/data/app/下的未找到本模块apk文件,请检查本模块包名配置是否正确.
     *                               具体检查build.gradle中的applicationId和AndroidManifest.xml中的package
     */
    @Deprecated
    private File findApkFileBySDK(String modulePackageName) throws FileNotFoundException {
        File apkFile;
        //当前Xposed模块hook了Build.VERSION.SDK_INT不用担心，因为这是发生在hook之前，不会有影响
        //但是其它的Xposed模块hook了当前宿主的这个值以后，就会有影响了,所以这里没有使用这个方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            apkFile = findApkFileAfterSDK21(modulePackageName);
        } else {
            apkFile = findApkFileBeforeSDK21(modulePackageName);
        }
        return apkFile;
    }

    /**
     * 寻找apk文件(api_21之后)
     * 在Android sdk21以及之后，apk文件的路径发生了变化
     *
     * @param packageName 当前模块包名
     * @return File 返回apk文件
     * @throws FileNotFoundException apk文件未找到
     */
    @Deprecated
    private File findApkFileAfterSDK21(String packageName) throws FileNotFoundException {
        File apkFile;
        File path = new File(String.format("/data/app/%s-%s", packageName, "1"));
        if (!path.exists()) {
            path = new File(String.format("/data/app/%s-%s", packageName, "2"));
        }
        if (!path.exists() || !path.isDirectory()) {
            throw new FileNotFoundException(String.format("没找到目录/data/app/%s-%s", packageName, "1/2"));
        }
        apkFile = new File(path, "base.apk");
        if (!apkFile.exists() || apkFile.isDirectory()) {
            throw new FileNotFoundException(String.format("没找到文件/data/app/%s-%s/base.apk", packageName, "1/2"));
        }
        return apkFile;
    }

    /**
     * 寻找apk文件(api_21之前)
     *
     * @param packageName 当前模块包名
     * @return File 返回apk文件
     * @throws FileNotFoundException apk文件未找到
     */
    @Deprecated
    private File findApkFileBeforeSDK21(String packageName) throws FileNotFoundException {
        File apkFile = new File(String.format("/data/app/%s-%s.apk", packageName, "1"));
        if (!apkFile.exists()) {
            apkFile = new File(String.format("/data/app/%s-%s.apk", packageName, "2"));
        }
        if (!apkFile.exists() || apkFile.isDirectory()) {
            throw new FileNotFoundException(String.format("没找到文件/data/app/%s-%s.apk", packageName, "1/2"));
        }
        return apkFile;
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