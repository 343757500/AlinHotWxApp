package com.mikuwxc.autoreply.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mikuwxc.autoreply.activity.RunningActivity;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.common.util.LogUtils;
import com.mikuwxc.autoreply.common.util.MyFileUtil;
import com.mikuwxc.autoreply.common.util.SharedPrefsUtils;
import com.mikuwxc.autoreply.common.util.ToastUtil;
import com.mikuwxc.autoreply.modle.ImMessageBean;
import com.mikuwxc.autoreply.view.activity.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = MyReceiver.class.getSimpleName();

    private String[] search = {
            //  "input keyevent 3",// 返回到主界面，数值与按键的对应关系可查阅KeyEvent
            // "sleep 1",// 等待1秒
            // 打开微信的启动界面，am命令的用法可自行百度、Google// 等待3秒
            "am force-stop com.tencent.mm",
            "am start -a com.tencent.mm.action.BIZSHORTCUT -f 67108864"
            // "am  start  service  com.mikuwxc.autoreply.AutoReplyService"// 打开微信的搜索
            // 像搜索框中输入123，但是input不支持中文，蛋疼，而且这边没做输入法处理，默认会自动弹出输入法
    };


    private String[] search1 = {
            //  "input keyevent 3",// 返回到主界面，数值与按键的对应关系可查阅KeyEvent
            // "sleep 1",// 等待1秒
            // 打开微信的启动界面，am命令的用法可自行百度、Google// 等待3秒
            "am force-stop com.mikuwxc.autoreply",
            "adb shell am start -n  com.mikuwxc.autoreply"
            // "am  start  service  com.mikuwxc.autoreply.AutoReplyService"// 打开微信的搜索
            // 像搜索框中输入123，但是input不支持中文，蛋疼，而且这边没做输入法处理，默认会自动弹出输入法
    };


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
//            LogUtils.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                LogUtils.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                SharedPrefsUtils.putString("regId", regId);
                AppConfig.Registration_Id = regId;
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//				processCustomMessage(context, bundle);
                String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
                LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + message);
                LogUtils.i(TAG, "[MyReceiver] 接收到推送下来的extras:" + extras);
                if (message != null) {
                    MyFileUtil.writeToNewFile(AppConfig.APP_FOLDER + "/update", "update sensitive word");//告知微信hoook有敏感词需要更新
                    MyFileUtil.writeToNewFile(AppConfig.APP_FOLDER + "/sensitive", message);
                    ToastUtil.showLongToast("更新敏感词");



                    try {
                        ImMessageBean imMessageBean = new Gson().fromJson(message, ImMessageBean.class);
                        String content = imMessageBean.getContent();
                        String type = imMessageBean.getType();
                        if ("202".equals(type)) {

                            SharedPreferences sp = context.getSharedPreferences("test", Activity.MODE_WORLD_READABLE);
                            SharedPreferences.Editor ditor = sp.edit();
                            ditor.putBoolean("test_put", true).commit();
                            Runtime runtime = Runtime.getRuntime();
                            try {
                                Process process = runtime.exec("su");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            search[1] = chineseToUnicode(search[1]);
                            execShell(search);
                           /* if ("0".equals(content)) {
                                SharedPreferences sp = context.getSharedPreferences("test", Activity.MODE_WORLD_READABLE);
                                SharedPreferences.Editor ditor = sp.edit();
                                ditor.putBoolean("test_put", true).commit();
                                ToastUtil.showLongToast("开启所有权限");

                                // 获取Runtime对象  获取root权限
                                Runtime runtime = Runtime.getRuntime();
                                try {
                                    Process process = runtime.exec("su");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                search[1] = chineseToUnicode(search[1]);
                                execShell(search);
                            } else {
                                SharedPreferences sp = context.getSharedPreferences("test", Activity.MODE_WORLD_READABLE);
                                SharedPreferences.Editor ditor = sp.edit();
                                ditor.putBoolean("test_put", false).commit();
                                ToastUtil.showLongToast("关闭所有权限");
                                // 获取Runtime对象  获取root权限
                                Runtime runtime = Runtime.getRuntime();
                                try {
                                    Process process = runtime.exec("su");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                search[1] = chineseToUnicode(search[1]);
                                execShell(search);
                            }
*/
                        }else if("203".equals(type)){
                            Context context1 = ContextHolder.getContext();
                            Intent intent1 = context1.getPackageManager().getLaunchIntentForPackage(context1.getPackageName());
                            PendingIntent restartIntent = PendingIntent.getActivity(context1, 0, intent1, PendingIntent.FLAG_ONE_SHOT);
                            AlarmManager mgr = (AlarmManager)context1.getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 50, restartIntent);
                            android.os.Process.killProcess(android.os.Process.myPid());






                        }

                    }catch (Exception e){
                        ToastUtil.showLongToast(e.toString());
                    }

                }

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                LogUtils.d(TAG, "[MyReceiver] 用户点击打开了通知");

                //打开自定义的Activity
                Intent i = new Intent(context, RunningActivity.class);
                i.putExtras(bundle);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                LogUtils.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
//                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
//                LogUtils.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                LogUtils.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {

        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    LogUtils.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    LogUtils.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    public String chineseToUnicode(String str){
        String result="";
        for (int i = 0; i < str.length(); i++){
            int chr1 = (char) str.charAt(i);
            if(chr1>=19968&&chr1<=171941){//汉字范围 \u4e00-\u9fa5 (中文)
                result+="\\u" + Integer.toHexString(chr1);
            }else{
                result+=str.charAt(i);
            }
        }
        return result;
    }


    /**
     30      * 执行Shell命令
     31      *
     32      * @param commands
     33      *            要执行的命令数组
     34      */
    public void execShell(String[] commands) {
        // 获取Runtime对象
        Runtime runtime = Runtime.getRuntime();

        DataOutputStream os = null;
        try {
            // 获取root权限
            Process process = runtime.exec("su");
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                os.write(command.getBytes());
                os.writeBytes("\n");
                os.flush();
            }
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
