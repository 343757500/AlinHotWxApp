package com.mikuwxc.autoreply;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mikuwxc.autoreply.bean.BindWXRespon;
import com.mikuwxc.autoreply.bean.FriendBean;
import com.mikuwxc.autoreply.bean.TaskJob;
import com.mikuwxc.autoreply.common.MyApp;
import com.mikuwxc.autoreply.common.UI;
import com.mikuwxc.autoreply.common.util.Constants;
import com.mikuwxc.autoreply.common.util.EventBusUtil;
import com.mikuwxc.autoreply.common.util.LogUtils;
import com.mikuwxc.autoreply.common.util.ToastUtil;
import com.mikuwxc.autoreply.db.PhoneUserHelper;
import com.mikuwxc.autoreply.modle.C;
import com.mikuwxc.autoreply.modle.Event;
import com.mikuwxc.autoreply.utils.HttpUtils;
import com.mikuwxc.autoreply.utils.LogToFile;
import com.mikuwxc.autoreply.utils.PersistentCookieStore;
import com.mikuwxc.autoreply.utils.PreferenceUtil;
import com.mikuwxc.autoreply.wxid.WxId;
import com.mikuwxc.autoreply.wxid.WxIdUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class AutoReplyService extends AccessibilityService {
    private static final String TAG = AutoReplyService.class.getSimpleName();

    private boolean canReply = false;//能否回复且每次收到消息只回复一次
    private boolean enableKeyguard = true;//默认有屏幕锁
    private int mode = 1;//微信通知模式：1.详细通知2.非详细通知
    private AccessibilityNodeInfo editText = null;
    private String[] wechatName = {"wing团", "SL1519818108"};
    //锁屏、唤醒相关
    private KeyguardManager km;
    private KeyguardLock kl;
    private PowerManager pm;
    private PowerManager.WakeLock wl = null;
    private ScreenOffReceiver sreceiver;
    private PhoneReceiver preceiver;
    private int addFriendStep = 0;
    private int addressBookIndex = 0;
    private int friendIndex = 0;
    private boolean isBackHome = false;
    private boolean isResponse = false;//当收到用户接受好友添加时，自动打招呼
    private int createGroupStep;//创建群步骤
    private CookieManager manager;
    private PackageManager packageManager;
    private String wxNum = null;
    private String replyContent = null;

    /**
     * 唤醒和解锁相关
     */
    private void wakeAndUnlock(boolean unLock) {
        if (unLock) {
            if (!pm.isScreenOn()) {
                //获取电源管理器对象
                wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "bright");
                //点亮屏幕
                wl.acquire();
                Log.i("demo", "亮屏");
            }
            if (km.inKeyguardRestrictedInputMode()) {
                //解锁
                enableKeyguard = false;
                //kl.reenableKeyguard();
                kl.disableKeyguard();
                Log.i("demo", "解锁");
            }
        } else {
            if (!enableKeyguard) {
                //锁屏
                kl.reenableKeyguard();
                Log.i("demo", "加锁");
            }
            if (wl != null) {
                //释放wakeLock，关灯
                wl.release();
                wl = null;
                Log.i("demo", "关灯");
            }
        }
    }

    /**
     * 通过文本查找
     */
    public AccessibilityNodeInfo findNodeInfosByText(AccessibilityNodeInfo nodeInfo, String text) {
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    //通过组件名递归查找编辑框
    private AccessibilityNodeInfo findNodeInfosByName(AccessibilityNodeInfo nodeInfo, String name) {
        if (name.equals(nodeInfo.getClassName())) {
            return nodeInfo;
        }
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo nodeInfosByName = findNodeInfosByName(nodeInfo.getChild(i), name);
            if (nodeInfosByName != null) {
                return nodeInfosByName;
            }
        }
        return null;
    }

    /**
     * 点击事件
     */
    public void performClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        if (nodeInfo.isClickable()) {
            Log.d("isDoJob>>>", "performClick: " + nodeInfo.getClassName());
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            performClick(nodeInfo.getParent());
        }
    }

    /**
     * 长按事件
     */
    public void performLongClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        if (nodeInfo.isLongClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
        } else {
            performLongClick(nodeInfo.getParent());
        }
    }

    /**
     * 返回事件
     */
    public void performBack(AccessibilityService service) {
        if (service == null) {
            return;
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    /**
     * 返回事件
     */
    public void performHome(AccessibilityService service) {
        if (service == null) {
            return;
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    public void jumpToIndex() {
        Intent intent = new Intent();
        intent = packageManager.getLaunchIntentForPackage("com.mikuwxc.autoreply");
        startActivity(intent);
    }

    /**
     * 处理相关事件
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        wakeAndUnlock(true);
        int eventType = event.getEventType();
//        Log.d("onAccessibilityEvent: ", "触发onAccessibilityEvent事件：" + eventType);
        switch (eventType) {
            //第一步：监听通知栏消息
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                Log.d("onAccessibilityEvent: ", "通知栏事件");
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                    String message = texts.get(0).toString();
                    Log.d("onAccessibilityEvent: ", "onAccessibilityEvent: " + texts.size());
                    for (int i = 0; i < texts.size(); i++) {
                        Log.d("onAccessibilityEvent: ", "onAccessibilityEvent:aaaaa " + texts.get(i) + "\n" + message.split(":")[0]);
                    }
                    if (message.contains("请求添加你为朋友")) {//通过好友验证请求
                        try {
                            Notification notification = (Notification) event.getParcelableData();
                            PendingIntent pendingIntent = notification.contentIntent;
                            if (StaticData.isDoJob) {//如果正在执行任务，则把此任务存进任务队列
                                StaticData.intentList.add(pendingIntent);
                                TaskJob taskJob = new TaskJob();
                                taskJob.setType(500);
                                StaticData.jobList.add(taskJob);
                            } else {
                                StaticData.isCheckFriend = true;
                                pendingIntent.send();
                            }
                            Log.d("收到添加好友请求", "onAccessibilityEvent: 收到添加好友请求");
                        } catch (CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                    String wechatNick = message.split(":")[0];
                    if (message.contains("我通过了你的朋友验证请求，现在我们可以开始聊天了")) {//自动打招呼

                    }
                    LogToFile.v(TAG, "昵称:" + wechatNick + " autoreply:" + StaticData.autoReply + " isStartTask:" + StaticData.isStartTask);
                    //收到通知栏的信息
                    if (StaticData.autoReply && StaticData.isStartTask) {
                        String chatMsg = message.substring(wechatNick.length() + 1);
                        Iterator<Map.Entry<String, String>> iterator = StaticData.autoReplyMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<String, String> next = iterator.next();
                            String key = next.getKey();
//                            Log.d(TAG,"收到:"+chatMsg+" 关键词:"+key);
                            if (chatMsg.contains(key)) {
                                //有关键词,需要点击进入回复
                                try {
                                    Log.v(TAG, "点击通知栏");
                                    StaticData.autoReplyFromNotification = true;
                                    replyContent = StaticData.autoReplyMap.get(key);
                                    Notification notification = (Notification) event.getParcelableData();
                                    PendingIntent pendingIntent = notification.contentIntent;
                                    pendingIntent.send();
                                    return;
                                } catch (CanceledException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    //过滤微信内部通知消息
                    if (isInside(message)) {
                        return;
                    }

                    StaticData.total++;
                    setData(message);

                    //Log.i("demo", "收到通知栏消息:" + message);
                    //收到信息发送更新锁屏界面广播
                    Intent i = new Intent("com.example.autoreply.SHOW_ACTION");
                    sendBroadcast(i);

                    if (StaticData.wechatAuto) {
                        Intent intent = new Intent();
                        intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                        startActivity(intent);
                        sleepTime(1000);
                        clickWechat();
                    }

                    if (!StaticData.autoReply && !StaticData.isTransmitGroup)
                        return;

                    //微信的两种通知消息类型，mode=1为详细内容，mode=2为通用类型
                    if (message.equals("微信：你收到了一条消息。")) {
                        mode = 2;
                    } else {
                        mode = 1;
                    }
//                    //模拟打开通知栏消息
//                    if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
//                        Log.i("demo", "标题栏canReply=true");
//                        canReply = true;
//                        wakeAndUnlock(true);
//                        try {
//                            Notification notification = (Notification) event.getParcelableData();
//                            PendingIntent pendingIntent = notification.contentIntent;
//                            pendingIntent.send();
//                        } catch (CanceledException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    break;
                }

                //第二步：监听界面内容是否改变
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//                Log.d("onAccessibilityEvent: ", "界面内容发生改变： " + event.getClassName().toString());
                if (isBackHome) {
                    performBack(this);
                    break;
                }
                /**
                 * 自动回复
                 */
//                if (canReply) {
//                    canReply = false;
//                    if (StaticData.isTransmitGroup) {
//                        transmitGroup();
//                    } else {
//                        reply("");
//                        performBack(this);
//                    }
//                }
                break;
            //是否跳转页面
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.d("onAccessibilityEvent: ", "页面跳转: " + event.getClassName().toString());
                String className = event.getClassName().toString();
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                    isBackHome = false;
                }
                if (isBackHome) {
                    performBack(this);
                    break;
                }

                if (StaticData.isGetNum) {
                    getNum();
                }

                if (StaticData.isGetWxInfo) {
                    getCurrentUserInfo();
                    if (Constants.isGetWxInfo) {
                        getCurrentWxNum();
                    }
                }

                /**
                 * 自动回复
                 */
                if (StaticData.wechatAuto && !StaticData.isReply && className.equals("com.tencent.mm.ui.LauncherUI")) {
                    autoReply(className);
                }

                /**
                 * 之前不在微信才有通知栏,已在微信里是不会有通知栏.点击通知栏进入的聊天页面
                 */
                if (StaticData.autoReplyFromNotification) { // && className.endsWith(WxId.CHATTING_UI)
                    autoReplyOnChatUI(replyContent);
//                    performBack(this);
                }

                /**
                 * 群转发
                 */
                if (StaticData.isTransmitGroup) {
                    transmitGroup1(className);
                }
                /**
                 * 创建群聊
                 */
                if (StaticData.isCreateGroup) {
                    createGroup(className);
                }
                /**
                 * 验证好友申请
                 */
                if (StaticData.isCheckFriend) {
                    checkFriendApply(className);
                }
                /**
                 * 互聊功能开启
                 */
                if (StaticData.isChatOthers && StaticData.isStartTask /*PreferenceUtil.getChatOtherStatus(this)*/) {
                    startToChatOrAddFriend(className);
                }
                /**
                 * 获取联系人信息
                 */
                if (StaticData.isGetFriendInfo) {
                    getFriendInfo(className);
                }
                /**
                 * 添加好友
                 */
                if (StaticData.isAddFriend) {
                    addFriendControl2(className);
                }
                /**
                 * 发朋友圈
                 */
                if (StaticData.isSendFriendCircle) {
                    sendFriendCircle(className);
                }
                break;
        }
    }

    int HULIAO_INDEX = 0;
    boolean isBack = false;

    /**
     * 互聊功能，添加好友
     *
     * @param className
     */
    private void startToChatOrAddFriend(String className) {
        AccessibilityNodeInfo window = getRootInActiveWindow();
        if (className.equals("com.tencent.mm.ui.LauncherUI")) {
            LogToFile.i("互聊中", "1、开始");
            if (HULIAO_INDEX >= StaticData.friendBeanList.size()) {//已经互聊完毕或者添加好友完毕
                performBack(this);
                jumpToIndex();
                StaticData.isChatOthers = false;
                HULIAO_INDEX = 0;
                LogToFile.i("互聊中", "互聊结束LauncherUI" + HULIAO_INDEX);
                return;
            }
            List<AccessibilityNodeInfo> search = window.findAccessibilityNodeInfosByText("搜索");//右上角放大镜图标
            if (!search.isEmpty()) {
                LogToFile.i(TAG, "1、点击右上角搜索按钮");
                performClick(search.get(0));
            } else {
                LogToFile.e("互聊中", "1、找不到右上角搜索按钮");
                StaticData.isChatOthers = false;
                jumpToIndex();
            }
        } else if (className.endsWith(WxId.SEARCH_UI)) {//搜索页面
            isBack = false;
            LogToFile.i("互聊中", "1、点击右上角搜索按钮");
            sleepTime(500);
            window = getRootInActiveWindow();
            if (HULIAO_INDEX >= StaticData.friendBeanList.size()) {//已经互聊完毕或者添加好友完毕
                LogToFile.i("互聊中", "互聊结束FTSMainUI" + HULIAO_INDEX);
                performBack(this);
                jumpToIndex();
                StaticData.isChatOthers = false;
                StaticData.isDoJob = false;
                HULIAO_INDEX = 0;
                return;
            }
            List<AccessibilityNodeInfo> searchEdit = window.findAccessibilityNodeInfosByViewId(WxId.huliao_searchId);//搜索编辑框
            List<AccessibilityNodeInfo> delete = window.findAccessibilityNodeInfosByText("清除");//清除X按钮
            if (!delete.isEmpty()) {//编辑框有内容就清空
                LogToFile.i("互聊中", "2.1编辑框有内容，点击X清空");
                performClick(delete.get(0));
            } else {
                LogToFile.i("互聊中", "找不到X清除按钮，编辑框无内容");
            }
            if (!searchEdit.isEmpty()) {
                AccessibilityNodeInfo edit = searchEdit.get(0);
                LogToFile.v(TAG, "黏贴的WechatNum:" + StaticData.friendBeanList.get(HULIAO_INDEX).getWechatNum());
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("message", StaticData.friendBeanList.get(HULIAO_INDEX).getWechatNum());
                HULIAO_INDEX++;
                clipboard.setPrimaryClip(clip);
                boolean b = edit.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                //粘贴进入内容
                boolean b1 = edit.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                LogToFile.i("互聊中", "3粘贴联系人信息");
            } else {
                LogToFile.i("互聊中", "3找不到搜索框");
                performBack(this);
                jumpToIndex();
                StaticData.isChatOthers = false;
                StaticData.isDoJob = false;
            }
            sleepTime(1000);
            window = getRootInActiveWindow();
            if (window == null)
                return;
            /**
             * 点击搜索按钮或者直接聊天按钮
             */
            List<AccessibilityNodeInfo> searchItem = window.findAccessibilityNodeInfosByViewId(WxId.huliao_searchItemId);
            List<AccessibilityNodeInfo> searchIv = window.findAccessibilityNodeInfosByViewId(WxId.huliao_searchIv);
            if (!searchIv.isEmpty()) {
                LogToFile.i("互聊中", "4点击查找手机/QQ号/微信号");
                performClick(searchIv.get(0));
            } else {
                if (!searchItem.isEmpty()) {
                    LogToFile.i("互聊中", "4点击好友Item，直接聊天");
                    performClick(searchItem.get(1));
                }
            }
        } else if (className.equals("com.tencent.mm.ui.base.h")) {
            List<AccessibilityNodeInfo> noUser;
            boolean isChange = true;
            do {
                noUser = window.findAccessibilityNodeInfosByViewId(WxId.huliao_noUser);
                if (!noUser.isEmpty()) {
                    Log.d("操作》》《《", "startToChatOrAddFriend: " + noUser.get(0).getText().toString());
                    if (noUser.get(0).getText().toString().contains("过于频繁")) {
                        LogToFile.i("互聊中", "7操作过于频繁");
                        StaticData.isChatOthers = false;
                        StaticData.isDoJob = false;
                        performBack(this);
                        sleepTime(500);
                        performBack(this);
                        performBack(this);
                        jumpToIndex();
                    } else {
                        LogToFile.i("互聊中", "7没有此用户");
                        performBack(this);
                    }
                    isChange = false;
                }
            } while (isChange);
        } else if (className.equals("com.tencent.mm.ui.chatting.ChattingUI") || className.equals("com.tencent.mm.ui.chatting.En_5b8fbb1e")) {//聊天界面
            LogToFile.i("互聊中", "5聊天界面，发送消息");
            sleepTime(1000);
            sendMessage();
        } else if (className.equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
            LogToFile.i("互聊中", "5好友信息界面");
            boolean isChatting = false;
            if (!isBack) {
                isChatting = huliaoAddFriend();
                isBack = true;
            }
            if (!isChatting) {
                sleepTime(1000);
                performBack(this);
            }
        } else {
            LogToFile.i("互聊中", "但来到了:" + className);
        }
    }

    private boolean huliaoAddFriend() {
        isBack = true;
        //添加通讯录
        AccessibilityNodeInfo nodeInfo = null;
        List<AccessibilityNodeInfo> noUser = null;
        List<AccessibilityNodeInfo> list = null;
        List<AccessibilityNodeInfo> sendMessage;
        sleepTime(1000);
        int i = 0;
        do {
            sleepTime(1000);
            if (i >= 5) {
                performBack(this);
                performBack(this);
                performBack(this);
                jumpToIndex();
                StaticData.isChatOthers = false;
                StaticData.isDoJob = false;
            }
            nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null) {
                return false;
            }
            noUser = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.huliao_noUser);
            list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.addfriendId);
            if (list.isEmpty())
                list = nodeInfo.findAccessibilityNodeInfosByText("添加到通讯录");
            sendMessage = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.huliao_sendMessage);
            LogToFile.i("互聊中", "6判断是否刷新完毕");
            i++;
        } while (noUser.isEmpty() && list.isEmpty() && sendMessage.isEmpty());
        sleepTime(1000);
        nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return false;
        }
        noUser = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.huliao_noUser);
        list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.huliao_addFriend);
        if (list.isEmpty())
            list = nodeInfo.findAccessibilityNodeInfosByText("添加到通讯录");
        sendMessage = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.huliao_sendMessage);
        if (noUser.isEmpty()) {
            AccessibilityNodeInfo targetNode = null;
            //找到控件
            if (!list.isEmpty()) {
                targetNode = list.get(0);
                performClick(targetNode);
            } else {
                //对方已经是好友
                LogToFile.i("互聊中", "7已经是好友，直接点击发送消息");
                if (!sendMessage.isEmpty()) {
                    performClick(sendMessage.get(0));
                    return true;
                }
            }
        } else {
            //没有此用户
            LogToFile.i("互聊中", "7没有此用户");
            return false;
        }
        sleepTime(500);
        nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return false;
        }
//编辑验证消息
        AccessibilityNodeInfo targetNode = null;
        int j = 0;
        do {
            sleepTime(1000);
            if (j >= 5) {
                performBack(this);
                performBack(this);
                performBack(this);
                jumpToIndex();
                StaticData.isChatOthers = false;
                StaticData.isDoJob = false;
            }
            nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null) {
                return false;
            }
            list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.permissionEtId);
            LogToFile.i("互聊中", "8验证消息编辑框");
            j++;
        } while (list.isEmpty());
        if (!list.isEmpty()) {
            targetNode = list.get(0);
            //使用剪切板
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("message", "我是Song啊");
            clipboard.setPrimaryClip(clip);
            targetNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            //粘贴进入内容
            targetNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            LogToFile.i("互聊中", "9粘贴验证消息");
            addFriendStep = 7;
        } else {
            LogToFile.i("互聊中", "8找不到验证消息编辑框");
        }
        sleepTime(500);
        //发送验证消息
        nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return false;
        }
        //找到控件
        list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.sendPermissionId);
        if (!list.isEmpty()) {
            LogToFile.i("互聊中", "10点击发送按钮");
            targetNode = list.get(0);
            performClick(targetNode);
        } else {
            LogToFile.i("互聊中", "10找不到发送按钮");
            performBack(this);
            performBack(this);
            performBack(this);
            performBack(this);
            performBack(this);
            jumpToIndex();
            StaticData.isChatOthers = false;
            StaticData.isDoJob = false;
        }
        return false;
    }

    /**
     * 自动回复
     */
    @SuppressLint("NewApi")
    private void sendMessage() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }

        AccessibilityNodeInfo targetNode = null;
        AccessibilityNodeInfo editText = null;
        //查找文本编辑框
        Log.i("demo", "正在查找编辑框...");
        //第一种查找方法
        List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.editId);
        if (!list1.isEmpty())
            editText = list1.get(0);
        //第二种查找方法
        if (editText == null)
            editText = findNodeInfosByName(nodeInfo, "android.widget.EditText");
        targetNode = editText;

        //粘贴回复信息
        if (targetNode != null) {
            //android >= 21=5.0时可以用ACTION_SET_TEXT
            //android >= 18=4.3时可以通过复制粘贴的方法,先确定焦点，再粘贴ACTION_PASTE
            //使用剪切板
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            String message = StaticData.chatContent;
//            String message = isResponse ? "我现在自动打招呼啊！" : StaticData.message;
            ClipData clip = ClipData.newPlainText("message", message);
            clipboard.setPrimaryClip(clip);
            //Log.i("demo", "设置粘贴板");
            //焦点 （n是AccessibilityNodeInfo对象）
            boolean b = targetNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            sleepTime(500);
            //Log.i("demo", "获取焦点");
            //粘贴进入内容
            boolean b1 = targetNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            sleepTime(500);
            //Log.i("demo", "粘贴内容");
        }

        //查找发送按钮
        if (targetNode != null) { //通过组件查找
            Log.i("demo", "查找发送按钮...");
            targetNode = null;
            List<AccessibilityNodeInfo> list2 = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.sendId);
            if (!list2.isEmpty())
                targetNode = list2.get(0);
            //第二种查找方法
            if (targetNode == null)
                targetNode = findNodeInfosByText(nodeInfo, "发送");
        }

        //点击发送按钮
        if (targetNode != null) {
            Log.i("demo", "点击发送按钮中...");
            final AccessibilityNodeInfo n = targetNode;
            performClick(n);
        }
        performBack(this);
    }

    /**
     * 自动回复
     */
    private void autoReply(String className) {
        if (className.equals("com.tencent.mm.ui.LauncherUI") && !StaticData.autoIng) {
            clickWechat();
        }
    }

    /**
     * 进入聊天页面自动回复
     *
     * @param content
     */
    private void autoReplyOnChatUI(String content) {
        //找搜索栏
        AccessibilityNodeInfo window = getRootInActiveWindow();
        List<AccessibilityNodeInfo> editInput = window.findAccessibilityNodeInfosByViewId(WxId.editId);//搜索编辑框
        AccessibilityNodeInfo editText = null;
        if (!editInput.isEmpty()) {
            LogToFile.v(TAG, "autoReplyOnChatUI:找到editInput");
            editText = editInput.get(0);
        }
        if (editText == null) {
            LogToFile.v(TAG, "autoReplyOnChatUI:找edittext");
            editText = findNodeInfosByName(window, "android.widget.EditText");
        }
        StaticData.autoReplyFromNotification = false;
        //黏贴
        if (editText != null) {
            Bundle args = new Bundle();
            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content);
            editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
            LogToFile.i(TAG, "autoReplyOnChatUI:找到输入框并黏贴:" + content);
        } else {
            Log.e(TAG, "autoReplyOnChatUI---找不到输入框!");
            performClick(editText);
            jumpToIndex();
            return;
        }
        //点击发送
        List<AccessibilityNodeInfo> buttons = window.findAccessibilityNodeInfosByViewId(WxId.sendId);
        AccessibilityNodeInfo sendBtn = null;
        if (!buttons.isEmpty())
            sendBtn = buttons.get(0);
        //第二种查找方法
        if (sendBtn == null)
            sendBtn = findNodeInfosByText(window, "发送");
        //点击发送按钮
        if (sendBtn != null) {
            Log.i("demo", "点击发送按钮中...");
            performClick(sendBtn);
        } else {
            Log.e(TAG, "autoReplyOnChatUI---找不到发送按钮!");
        }
        sleepTime(500);
        performBack(this);
        sleepTime(500);
        performBack(this);
        sleepTime(500);
        jumpToIndex();
    }

    int size = 0;//未读消息的数量
    int sizeNew = 0;//最新的未读消息的数量


    private void clickWechat() {
        StaticData.isReply = true;
        int replyStep = 0;
        wakeAndUnlock(true);
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null)
            return;
        AccessibilityNodeInfo targetNode = null;
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("微信");
        if (!list.isEmpty()) {
            if (list.size() >= 2) {
                if (list.get(1).getText().equals("微信"))
                    performClick(list.get(1));
                else if (list.size() >= 3) {
                    if (list.get(2).getText().equals("微信"))
                        performClick(list.get(2));
                }
            } else
                performClick(list.get(0));
        }
        sleepTime(500);
        nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null)
            return;
        List<AccessibilityNodeInfo> chatList = null;
        chatList = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.chatUserItemId);//com.tencent.mm:id/aac
        if (!chatList.isEmpty()) {
            performClick(chatList.get(0));
            StaticData.autoIng = true;//正在自动回复
        } else {
            jumpToIndex();
            StaticData.isReply = false;
            return;
        }
        sleepTime(500);
        nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null)
            return;
        boolean isDanLiao = true;
        String toChatName = "";
        //判断是否群聊以及mode=2时是否匹配好友
        List<AccessibilityNodeInfo> qunName = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.qunId);
        List<AccessibilityNodeInfo> tengxun = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.tengxunxinwenId);
        if (!qunName.isEmpty()) {
            targetNode = qunName.get(0);
            String temp = targetNode.getText().toString();
            toChatName = temp;
            if (temp.matches(".*\\(([3-9]|[1-9]\\d+)\\)")/* || (mode == 2 && StaticData.isfriend && (!temp.equals(StaticData.friend)))*/) {//如果是群聊则返回
                isDanLiao = false;
            }
        } else if (!tengxun.isEmpty()) {
            toChatName = tengxun.get(0).getText().toString();
        }
        List<AccessibilityNodeInfo> gongzhonghao = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.gongzhonghaoId);
        List<AccessibilityNodeInfo> inputKuang = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.editId);
        if (/*!gongzhonghao.isEmpty() || */inputKuang.isEmpty()) {
//            return;
            isDanLiao = false;
        }
        if (isDanLiao) {
            list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.chatContentListId);
            if (!list.isEmpty()) {
                boolean b;
                do {
                    b = list.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                    sleepTime(200);
                } while (b);
                messageList = new ArrayList<>();
                getUnReadMessage(list.get(0));
                Log.d("isDoJob>>>", "clickWechat: 读取信息列表成功，正在回复");
                replyMsgList = new ArrayList<>();
                size = messageList.size();
                sizeNew = size;
                replyMessage(list.get(0));
            }
        }
        if (toChatName.equals("腾讯新闻")) {
            performBack(this);
            sleepTime(1000);
            performLongClick(chatList.get(0));
            sleepTime(500);
            nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null)
                return;
            list = nodeInfo.findAccessibilityNodeInfosByText("删除该聊天");
            if (!list.isEmpty()) {
                performClick(list.get(0));
            }
        } else {
            performLongClick(chatList.get(0));
            sleepTime(500);
            nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null)
                return;
            list = nodeInfo.findAccessibilityNodeInfosByText("删除该聊天");
            if (!list.isEmpty()) {
                performClick(list.get(0));
            }
            sleepTime(1000);
            performBack(this);
        }
        StaticData.autoIng = false;//自动回复结束
        clickWechat();
    }

    private void replyMessage(AccessibilityNodeInfo accessibilityNodeInfo) {
        for (int i = 0; i < sizeNew; i++) {
            reply(messageList.get(i));
            if (i + 1 < sizeNew)
                sleepTime(500);
        }
        Log.d("isDoJob>>>", "clickWechat: 检查是否有新消息");
        checkNewMsg(accessibilityNodeInfo);
    }

    boolean isNewMsg = false;

    private void checkNewMsg(AccessibilityNodeInfo accessibilityNodeInfo) {
        AccessibilityNodeInfo nodeInfo;
        List<AccessibilityNodeInfo> list;
        nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null)
            return;
        list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.chatContentTextId);
        if (!list.isEmpty()) {
            if (messageList.contains(list.get(0).getText().toString()) || isNewMsg) {//如果列表第一条消息在消息集合里，则不用往上翻页了
                isNewMsg = true;
                boolean hasNewMsg = false;
                for (int i = 0; i < list.size(); i++) {
                    String s = list.get(i).getText().toString();
                    if (!messageList.contains(s) && !replyMsgList.contains(s)) {
                        messageList.add(s);
                        Log.d("isDoJob>>>", "新收到的消息:" + s);
                        hasNewMsg = true;
                    }
                }
                Boolean b;
                b = accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                sleepTime(500);
                if (b) {
                    checkNewMsg(accessibilityNodeInfo);
                    return;
                }
                Log.d("checkNewMsg", "checkNewMsg: " + messageList.size() + "<<>>" + size);
                if (hasNewMsg) {
                    sizeNew = messageList.size() - size;
                    size = messageList.size();
                    Toast.makeText(getApplicationContext(), "有新消息", Toast.LENGTH_SHORT).show();
                    replyMessage(accessibilityNodeInfo);
                }
                isNewMsg = false;
            } else {
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                sleepTime(500);
                checkNewMsg(accessibilityNodeInfo);
            }
        }
    }

    private List<String> messageList;
    private List<String> replyMsgList;

    private void getUnReadMessage(AccessibilityNodeInfo accessibilityNodeInfo) {
        AccessibilityNodeInfo nodeInfo;
        List<AccessibilityNodeInfo> list;
        nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null)
            return;
        list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.chatContentTextId);
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i).getText().toString();
                if (!messageList.contains(s))
                    messageList.add(s);
            }
            boolean b = accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            sleepTime(200);
            if (b) {
                getUnReadMessage(accessibilityNodeInfo);
            } else {
                Toast.makeText(getApplicationContext(), "获取消息成功", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < messageList.size(); i++) {
                    Log.d("aaaaa", "getUnReadMessage: " + messageList.get(i));
                }
            }
        }
    }

    private void transmitGroup1(String className) {
        if (className.equals("com.tencent.mm.ui.base.k")) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null)
                return;
            AccessibilityNodeInfo targetNode = null;
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("转发");
            if (!list.isEmpty()) {
                performClick(list.get(0));
            }
        } else if (className.equals("com.tencent.mm.ui.transmit.SelectConversationUI")) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null)
                return;
            AccessibilityNodeInfo targetNode = null;
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("多选");
            if (!list.isEmpty()) {
                performClick(list.get(0));
            }
            sleepTime(200);
            list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.searchFriendId);
            if (!list.isEmpty()) {
                targetNode = list.get(0);
                targetNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                //使用剪切板
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("message", "傻不拉几");
                clipboard.setPrimaryClip(clip);
                //粘贴进入内容
                targetNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                sleepTime(200);
                list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.friendLineId);
                if (!list.isEmpty()) {
                    performClick(list.get(0));
                }
                sleepTime(200);
                list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.editFriendInfoFinish);
                if (!list.isEmpty()) {
                    performClick(list.get(0));
                }
            }
        } else if (className.equals("com.tencent.mm.ui.base.h")) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null)
                return;
            AccessibilityNodeInfo targetNode = null;
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.sendGroupId);
            if (!list.isEmpty()) {
                performClick(list.get(0));
            }
            sleepTime(1000);
            isBackHome = true;
            performBack(this);
        }
    }

    private void transmitGroup() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null)
            return;
        AccessibilityNodeInfo targetNode = null;
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("Song（ˉ﹃ˉ）头像");
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                AccessibilityNodeInfo child = list.get(i).getParent().getChild(0);
                CharSequence text = list.get(i).getParent().getChild(0).getText();
                if (TextUtils.isEmpty(text))
                    text = "null";
                Log.d("transmitGroup: ", text.toString() + "<<>>" + child.getClassName());
            }
            list.get(list.size() - 1).getParent().getChild(0).performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
        }
    }

    private void createGroup(String className) {
        if (className.equals("com.tencent.mm.ui.LauncherUI")) {
            clickJiaHao();//点击加号
        } else if (className.equals("android.widget.FrameLayout")) {
            clickFaQiQunLiao();
        } else if (className.equals("com.tencent.mm.ui.contact.SelectContactUI")) {
            chooseFriend();
        } else if (className.endsWith(WxId.CHATTING_UI)) {
            jumpToEditGroupInfo();
        } else if (className.equals("com.tencent.mm.plugin.chatroom.ui.ChatroomInfoUI")) {
            jumpToEditGroupName();
        } else if (className.equals("com.tencent.mm.plugin.chatroom.ui.ModRemarkRoomNameUI")) {
            completeGroupName();
        }
    }

    private void completeGroupName() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null)
            return;
        AccessibilityNodeInfo targetNode = null;
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.etgroupNameId);
        if (!list.isEmpty()) {
            targetNode = list.get(0);
            targetNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            //使用剪切板
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("message", "测试群命名");
            clipboard.setPrimaryClip(clip);
            //粘贴进入内容
            targetNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
        list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.editFriendInfoFinish);
        if (!list.isEmpty())
            performClick(list.get(0));
        StaticData.isCreateGroup = false;
        sleepTime(200);
        performBack(this);
    }

    private void jumpToEditGroupName() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null)
            return;
        AccessibilityNodeInfo targetNode = null;
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.groupNameId);
        if (list.isEmpty()) {
            List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.groupInfoListId);
            if (!infos.isEmpty()) {
                infos.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                jumpToEditGroupName();
            }
        } else {
            performClick(list.get(0));
        }
    }

    private void jumpToEditGroupInfo() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null)
            return;
        AccessibilityNodeInfo targetNode = null;
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("聊天信息");
        if (!list.isEmpty()) {
            targetNode = list.get(0);
            performClick(targetNode);
        }
    }

    private void chooseFriend() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null)
            return;
        AccessibilityNodeInfo targetNode = null;
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.searchFriendId);
        if (!list.isEmpty()) {
            targetNode = list.get(0);
            for (int i = 0; i < wechatName.length; i++) {
                targetNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                //使用剪切板
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("message", wechatName[i]);
                clipboard.setPrimaryClip(clip);
                targetNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                sleepTime(500);
                nodeInfo = getRootInActiveWindow();
                if (nodeInfo == null)
                    return;
                List<AccessibilityNodeInfo> nodeInfos = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.friendLineId);
                if (!nodeInfos.isEmpty())
                    performClick(nodeInfos.get(0));
                sleepTime(500);
            }
            List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.createGroupChatConfirm);
            if (!infos.isEmpty()) {
                performClick(infos.get(0));
            } else {
                infos = nodeInfo.findAccessibilityNodeInfosByText("确定");
                if (!infos.isEmpty()) {
                    performClick(infos.get(0));
                } else {
                    Toast.makeText(AutoReplyService.this, "找不到确定按钮", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void clickFaQiQunLiao() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null)
            return;
        AccessibilityNodeInfo targetNode = null;
        List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByText("发起群聊");
        if (!infos.isEmpty()) {
            performClick(infos.get(0));
        }
    }

    private void clickJiaHao() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null)
            return;
        AccessibilityNodeInfo targetNode = null;
        //找到控件
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.jiahaoId);
        if (!list.isEmpty()) {
            performClick(list.get(0));
        }
        createGroupStep = 1;
    }

    private boolean isSayHello = false;

    /**
     * 验证好友申请
     *
     * @param className
     */
    private void checkFriendApply(String className) {
        if (className.equals("com.tencent.mm.plugin.subapp.ui.friend.FMessageConversationUI")) {
            LogToFile.i("验证好友申请", "1验证好友申请");
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null) {
                return;
            }
            AccessibilityNodeInfo targetNode = null;
            //找到控件
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.receiveFriend);
            if (!list.isEmpty()) {
                clickReceive(list, list.get(0), 0);
            } else {
                LogToFile.i("验证好友申请", "2找不到接受按钮");
                performBack(this);
                StaticData.isCheckFriend = false;
                jumpToIndex();
                StaticData.isDoJob = false;
            }
        } else if (className.equals("com.tencent.mm.ui.chatting.ChattingUI")) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null) {
                return;
            }
            AccessibilityNodeInfo targetNode = null;
            AccessibilityNodeInfo editText = null;
            //查找文本编辑框
            Log.i("demo", "正在查找编辑框...");
            //第一种查找方法
            List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.editId);
            if (!list1.isEmpty())
                editText = list1.get(0);
            //第二种查找方法
            if (editText == null)
                editText = findNodeInfosByName(nodeInfo, "android.widget.EditText");
            targetNode = editText;

            //粘贴回复信息
            if (targetNode != null) {
                //android >= 21=5.0时可以用ACTION_SET_TEXT
                //android >= 18=4.3时可以通过复制粘贴的方法,先确定焦点，再粘贴ACTION_PASTE
                //使用剪切板
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("message", "你好啊，我已经通过了你的好友申请了");
                clipboard.setPrimaryClip(clip);
                //Log.i("demo", "设置粘贴板");
                //焦点 （n是AccessibilityNodeInfo对象）
                targetNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                //Log.i("demo", "获取焦点");
                //粘贴进入内容
                targetNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                //Log.i("demo", "粘贴内容");
            }

            //查找发送按钮
            if (targetNode != null) { //通过组件查找
                Log.i("demo", "查找发送按钮...");
                targetNode = null;
                List<AccessibilityNodeInfo> list2 = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.sendId);
                if (!list2.isEmpty())
                    targetNode = list2.get(0);
                //第二种查找方法
                if (targetNode == null)
                    targetNode = findNodeInfosByText(nodeInfo, "发送");
            }

            //点击发送按钮
            if (targetNode != null) {
                Log.i("demo", "点击发送按钮中...");
                final AccessibilityNodeInfo n = targetNode;
                performClick(n);
            }
            isSayHello = false;
            performBack(this);
        }
    }

    private void clickReceive(List<AccessibilityNodeInfo> list, AccessibilityNodeInfo item, int index) {
        performClick(item);
        sleepTime(500);
        boolean flag = true;
        int time = 0;
        do {
            sleepTime(500);
            if (time >= 5) {
                LogToFile.d("验证好友申请", "超时无法验证通过");
                StaticData.isCheckFriend = false;
                performBack(this);
                jumpToIndex();
                StaticData.isDoJob = false;
            }
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            List<AccessibilityNodeInfo> completeAdd = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.completeAdd);
            LogToFile.d("验证好友申请", "寻找完成按钮");
            if (completeAdd.isEmpty())
                completeAdd = nodeInfo.findAccessibilityNodeInfosByText("完成");
            if (completeAdd.isEmpty())
                flag = true;
            else
                flag = false;
        } while (flag);
        LogToFile.d("验证好友申请", "进入SayHiWithSnsPermissionUI页面");
        sleepTime(500);
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> completeAdd = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.completeAdd);
        List<AccessibilityNodeInfo> permissionTv = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.permissionTv);
        if (!permissionTv.isEmpty()) {
            if (permissionTv.get(0).getText().toString().contains("Song啊")) {
                boolean isC = false;
                if (!completeAdd.isEmpty()) {
                    LogToFile.d("验证好友申请", "点击完成按钮");
                    performClick(completeAdd.get(0));
                    isC = true;
                } else {
                    completeAdd = nodeInfo.findAccessibilityNodeInfosByText("完成");
                    if (!completeAdd.isEmpty()) {
                        performClick(completeAdd.get(0));
                        isC = true;
                    } else {
                        LogToFile.d("验证好友申请", "找不到完成按钮");
                        performBack(this);
                        performBack(this);
                        performBack(this);
                        StaticData.isCheckFriend = false;
                        jumpToIndex();
                        StaticData.isDoJob = false;
                    }
                }
                if (isC) {
                    sleepTime(2000);
                    nodeInfo = getRootInActiveWindow();
                    if (nodeInfo == null) {
                        LogToFile.d("验证好友申请", "nodeInfo为空~");
                        return;
                    }
                    time = 0;
                    do {
                        if (time >= 5) {
                            LogToFile.d("验证好友申请", "超时无法完成加好友");
                            performBack(this);
                            performBack(this);
                            performBack(this);
                            jumpToIndex();
                            StaticData.isCheckFriend = false;
                            StaticData.isDoJob = false;
                        }
                        LogToFile.d("验证好友申请", "寻找发消息按钮");
                        List<AccessibilityNodeInfo> communicateId = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.communicateId);
                        if (communicateId.isEmpty())
                            communicateId = nodeInfo.findAccessibilityNodeInfosByText("发消息");
                        if (communicateId.isEmpty())
                            flag = true;
                        else
                            flag = false;
                        sleepTime(1000);
                        time++;
                    } while (flag);
                    LogToFile.d("验证好友申请", "跳转到详细资料页面");
                    performBack(this);
                }
            } else {
                performBack(this);
                sleepTime(1000);
                index++;
                if (index < list.size())
                    clickReceive(list, list.get(index), index);
                else {
                    LogToFile.i("验证好友申请", "结束认证");
                    StaticData.isCheckFriend = false;
                    performBack(this);
                    performBack(this);
                    performBack(this);
                    jumpToIndex();
                    StaticData.isDoJob = false;
                }
            }
        } else {
            performBack(this);
            sleepTime(1000);
            index++;
            if (index < list.size())
                clickReceive(list, list.get(index), index);
            else {
                LogToFile.i("验证好友申请", "结束认证");
                StaticData.isCheckFriend = false;
                performBack(this);
                performBack(this);
                performBack(this);
                jumpToIndex();
                StaticData.isDoJob = false;
            }
        }
    }

    private int cFriendIndex = 0;
    private boolean isAddressBookLast = false;//通讯录是否到底了
    int childCount;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    boolean isStart = false;

    /**
     * 获取好友信息操作
     *
     * @param className
     */
    private void getFriendInfo(String className) {
        wakeAndUnlock(true);
        if (className.equals("com.tencent.mm.ui.LauncherUI")) {
            if (StaticData.isGetFriendInfoStart) {
                StaticData.fbList.clear();
                StaticData.isGetFriendInfoStart = false;
                isStart = false;
                //10秒后如果未开始获取好友信息，判断任务失败
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isStart) {
                            doOneTaskStatus(-1);
                            StaticData.isGetFriendInfo = false;
                        }
                    }
                }, 10000);
                LogToFile.i("获取通讯录", "1、点击跳转到通讯录页面");
                jumpToAddrassBook();//打开通讯录
                sleepTime(1000);

                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo == null) {
                    LogToFile.e("获取通讯录", "1.1、获取不到界面节点");
                    return;
                }
                AccessibilityNodeInfo targetNode = null;
                //找到控件
                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.addressBookListId);
                if (!list.isEmpty()) {
                    boolean b = false;
                    LogToFile.i("获取通讯录", "1.2、上划到通讯录顶部");
                    do {
                        b = list.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                    } while (b);
                } else {
                    LogToFile.e("获取通讯录", "1.2、找不到通讯录ListViewId");
                }
                sleepTime(1000);
                nodeInfo = getRootInActiveWindow();//重新获取该窗口的view
                if (nodeInfo == null) {
                    LogToFile.e("获取通讯录", "1.3、获取不到界面节点");
                    return;
                }
                targetNode = null;
                //找到控件
                list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.addressBookListId);
                if (!list.isEmpty()) {
                    AccessibilityNodeInfo info = list.get(0);
                    Log.d("isDoJob>>>", "getFriendInfo: 点击第一个联系人");
                    LogToFile.i("获取通讯录", "1.4、点击第一个联系人");
                    performClick(info.getChild(1));
                } else {
                    Log.d("isDoJob>>>", "getFriendInfo: 通讯录Id失效");
                    LogToFile.e("获取通讯录", "1.4、通讯录ListView的Id失效");
                    Toast.makeText(AutoReplyService.this, "通讯录Id失效", Toast.LENGTH_SHORT).show();
                    doOneTaskStatus(-1);
                }
            } else {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo == null) {
                    LogToFile.e("获取通讯录", "2.1、获取不到界面节点");
                    return;
                }
                AccessibilityNodeInfo targetNode = null;
                //找到控件
                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.addressBookListId);
                if (!list.isEmpty()) {
                    childCount = list.get(0).getChildCount();
                    if (cFriendIndex >= childCount) {
                        cFriendIndex = 0;
                        list.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        LogToFile.i("获取通讯录", "2.1、下滑到下一页");
                        sleepTime(1000);
                    }
                }
                nodeInfo = getRootInActiveWindow();
                if (nodeInfo == null) {
                    LogToFile.e("获取通讯录", "2.2、获取不到界面节点");
                    return;
                }
                targetNode = null;
                //找到通讯录listview控件
                list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.addressBookListId);
                if (!list.isEmpty()) {
                    AccessibilityNodeInfo info = list.get(0);
                    Log.d("：：", "classname: " + cFriendIndex + ":::" + info.getChild(cFriendIndex).getClassName());
                    if (/*!infos.isEmpty() && */cFriendIndex == 0) {
                        LogToFile.i("获取通讯录", "2.2、点击下一个用户");
                        performClick(info.getChild(1));
                    } else {
                        LogToFile.i("获取通讯录", "2.2、点击下一个用户");
                        performClick(info.getChild(cFriendIndex));
                    }
                    if (info.getChild(cFriendIndex).getClassName().equals("android.widget.FrameLayout")) {//最后一条联系人
                        LogToFile.i("获取通讯录", "2.3、获取联系人完成" + StaticData.fbList.size());
                        StaticData.isGetFriendInfo = false;
                        cFriendIndex = 0;
                        Log.d("总联系人：最后一个了", ": " + StaticData.friendBeanList.size());
                        Toast.makeText(this, "总共获取到" + StaticData.fbList.size() + "个联系人", Toast.LENGTH_SHORT).show();
                        if (StaticData.isUpdateFriend)
                            setData();
                        doOneTaskStatus(1);
//                        wakeAndUnlock(false);
                    }
                }
            }
        } else if (className.equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
            isStart = true;//开始获取好友信息
            sleepTime(500);
            FriendBean bean = new FriendBean();
            cFriendIndex++;
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null) {
                LogToFile.e("获取通讯录", "个人信息页获取不到界面节点");
                return;
            }
            AccessibilityNodeInfo targetNode = null;
            //找到微信号控件
            List<AccessibilityNodeInfo> wechatNum = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.wechatNum);
            if (!wechatNum.isEmpty()) {//获取微信号
                LogToFile.i("获取通讯录", "获取微信号");
                AccessibilityNodeInfo info = wechatNum.get(0);
                if (!TextUtils.isEmpty(info.getText()) && info.getText().toString().split(":").length >= 2) {
                    bean.setWechatNum(info.getText().toString().split(":")[1].trim());
                    Log.d("好友信息：", "wechatNum:" + info.getText().toString().split(":")[1].trim());
                }
            } else {
                LogToFile.i("获取通讯录", "找不到微信号的控件");
            }
            //找到微信昵称控件
            wechatNum = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.wechatNick);
            //找到微信备注控件
            List<AccessibilityNodeInfo> wechatRemark = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.wechatRemark);
            if (!wechatNum.isEmpty()) {//获取微信昵称
                LogToFile.i("获取通讯录", "获取微信昵称");
                AccessibilityNodeInfo info = wechatNum.get(0);
                if (!TextUtils.isEmpty(info.getText()) && info.getText().toString().split(":").length >= 2) {
                    bean.setWechatNick(info.getText().toString().split(":")[1].trim());
                    Log.d("好友信息：", "wechatNick:" + info.getText().toString().split(":")[1]);
                }
            } else {
                LogToFile.i("获取通讯录", "找不到微信昵称控件Id");
            }
            if (!wechatRemark.isEmpty()) {//获取微信备注
                LogToFile.i("获取通讯录", "获取微信备注");
                AccessibilityNodeInfo info = wechatRemark.get(0);
                bean.setWechatNick(info.getText().toString().trim());
                Log.d("好友信息：", "wechatRemark:" + SpannableString.valueOf(info.getText()));
            } else {
                LogToFile.i("获取通讯录", "获取不到微信备注控件Id");
            }
            wechatNum = nodeInfo.findAccessibilityNodeInfosByViewId("");
            if (!wechatNum.isEmpty()) {
                LogToFile.i("获取通讯录", "获取手机号码");
                if (wechatNum.get(0).getChildCount() >= 2) {
                    AccessibilityNodeInfo info = wechatNum.get(0).getChild(1);
                    if (info != null) {
                        bean.setWechatPhone(info.getText().toString());
                        Log.d("好友信息：", "wechatPhone:" + info.getText());
                    }
                }
            } else {
                LogToFile.i("获取通讯录", "获取不到手机号码控件Id");
            }
            if (!StaticData.fbList.contains(bean) && !bean.getWechatNum().isEmpty() && !bean.getWechatNum().equals("weixin") && !bean.getWechatNum().equals("filehelper") && !bean.getWechatNum().equals("myron20")) {
                StaticData.fbList.add(bean);
                StaticData.friendBeanList.add(bean);
                if (StaticData.fbList.size() >= 10) {
                    if (StaticData.isUpdateFriend)
                        setData();
                }
                Log.d("人数：", ": " + StaticData.fbList.size());
            }
            performBack(this);
        }
    }

    private String getCurrentUserInfo() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return "";
        }
        AccessibilityNodeInfo targetNode = null;

        targetNode = findNodeInfosByText(nodeInfo, "我");
        if (targetNode == null) {
            Log.e(TAG, "找不到'我'");
            return "";
        }
        performClick(targetNode);
        sleepTime(500);
        nodeInfo.refresh();
        nodeInfo = getRootInActiveWindow();
        targetNode = findNodeInfosByText(nodeInfo, "设置");
        if (targetNode == null) {
            Log.e(TAG, "找不到'设置'");
            return "";
        }
        performClick(targetNode);
        sleepTime(500);
        nodeInfo.refresh();
        nodeInfo = getRootInActiveWindow();
        targetNode = findNodeInfosByText(nodeInfo, "帐号与安全");
        if (targetNode == null) {
            Log.e(TAG, "找不到'账号与安全'");
            return "";
        }
        performClick(targetNode);
        sleepTime(500);
        nodeInfo.refresh();
        nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/summary");

        String phoneNo = null;
        String qqNo = null;
        AccessibilityNodeInfo wxnum = findNodeInfosByText(nodeInfo, "微信号");
        if (wxnum != null) {
            if (wxnum.getParent().getChildCount() > 1) {
                wxNum = wxnum.getParent().getChild(1).getText().toString();
                Log.d("getCurrentUserInfo>>>", "getCurrentUserInfo: 微信号" + wxNum);
            }

            AccessibilityNodeInfo qqnum = findNodeInfosByText(nodeInfo, "QQ号");
            if (qqnum != null) {
                if (qqnum.getParent().getChildCount() > 1) {
                    qqNo = qqnum.getParent().getChild(1).getText().toString();
                    Log.d("getCurrentUserInfo>>>", "getCurrentUserInfo: QQ号" + qqNo);
                }
            }
            AccessibilityNodeInfo phonenum = findNodeInfosByText(nodeInfo, "手机号");
            if (phonenum != null) {
                if (phonenum.getParent().getChildCount() > 1) {
                    phoneNo = phonenum.getParent().getChild(1).getText().toString();
                    Log.d("getCurrentUserInfo>>>", "getCurrentUserInfo: 手机号" + phoneNo);
                }
            }
            if (phoneNo == null || "未绑定".equals(phoneNo)) {
                if (qqNo != null && !"未绑定".equals(qqNo)) {
                    wxNum = qqNo;
                }
            } else {
                wxNum = phoneNo;
            }
            //原来是下面这行代码.好装逼的写法.但上面连是否为空和size都没判断.一到这就崩溃. by Temper
//                String wxNum = phonenum.getParent().getChild(1).getText().toString().equals("未绑定") ? qqnum.getParent().getChild(1).getText().toString().equals("未绑定") ? wxnum.getParent().getChild(1).getText().toString() : qqnum.getParent().getChild(1).getText().toString() : phonenum.getParent().getChild(1).getText().toString();
            LogToFile.v(TAG, "保存的wxNum:" + wxNum);
            PreferenceUtil.setCurrentWxNum(this, wxNum);
        }
        uploadDeviceInfo();
        performBack(this);
        sleepTime(500);
        nodeInfo.refresh();
        nodeInfo = getRootInActiveWindow();
        targetNode = findNodeInfosByText(nodeInfo, "关于微信");
        if (targetNode == null) {
            Log.e(TAG, "找不到'关于微信'");
            return "";
        }
        performClick(targetNode);
        sleepTime(500);
        nodeInfo.refresh();
        nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> wx = nodeInfo.findAccessibilityNodeInfosByText("微信");
        PreferenceUtil.setWxNumVersion(this, wx.get(1).getText().toString());
        performBack(this);
        sleepTime(500);
        performBack(this);
        sleepTime(500);
        jumpToIndex();
        WxIdUtil.initId(this);
        return StaticData.currentUser;
    }

    /**
     * 上传机器、微信信息
     */
    private void uploadDeviceInfo() {
        Log.d("wxnumnumnum", "uploadDeviceInfo: " + PreferenceUtil.getCurrentWxNum(this));
        Map<String, String> map = new HashMap<>();
        map.put("deviceInfo", MyApp.tm.getDeviceId());
        map.put("deviceName", MyApp.bd.MODEL);
        map.put("lemonName", PreferenceUtil.getNickname(this));
        map.put("wxPic", PreferenceUtil.getHeadimgurl(this));
        map.put("openId", PreferenceUtil.getOpenid(this));
        map.put("wxNum", PreferenceUtil.getCurrentWxNum(this));
        LogToFile.v(TAG, "发送的的wxNum:" + PreferenceUtil.getCurrentWxNum(this));
        StaticData.isGetWxInfo = false;
        final String mRequestBody = appendParameter(HttpUtils.ADD_DEVICE_INFO_URL, map);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, HttpUtils.ADD_DEVICE_INFO_URL, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                BindWXRespon bindWXRespon = JSON.parseObject(response.toString(), BindWXRespon.class);
                if (bindWXRespon.getCode() == 1) {
                    Toast.makeText(AutoReplyService.this, "请重新登陆！", Toast.LENGTH_SHORT).show();
                    return;
                }
                PreferenceUtil.setCurrentUserId(AutoReplyService.this, bindWXRespon.getResult().getWxInfo().getUserId());
                PreferenceUtil.setCurrentWxId(AutoReplyService.this, bindWXRespon.getResult().getWxInfo().getId());
                PreferenceUtil.setCurrentUser(AutoReplyService.this, bindWXRespon.getResult().getWxInfo().getWxNum());
                Log.d("onResponse", "onResponse: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
            }

            @Override
            public byte[] getBody() {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            mRequestBody, PROTOCOL_CHARSET);
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.d("Cookie", "getHeaders: " + MyApp.manager.getCookieStore().getCookies().toString());
                Map<String, String> mHeaders = new HashMap<String, String>(1);
                mHeaders.put("Cookie", MyApp.manager.getCookieStore().getCookies().toString());
                return mHeaders;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private String appendParameter(String url, Map<String, String> params) {
        Uri uri = Uri.parse(url);
        Uri.Builder builder = uri.buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build().getQuery();
    }

    /**
     * 分批上传用户信息  20个传一次
     */
    private void setData() {
        JSONArray jsonArray = new JSONArray();
        JSONObject tmpObj = null;
        int count = StaticData.fbList.size();
        try {
            for (int i = 0; i < count; i++) {
                tmpObj = new JSONObject();

                tmpObj.put("wechatNick", StaticData.fbList.get(i).getWechatNick());

                tmpObj.put("wechatNum", StaticData.fbList.get(i).getWechatNum());
                tmpObj.put("wechatPhone", StaticData.fbList.get(i).getWechatPhone());
                tmpObj.put("wechatRemark", StaticData.fbList.get(i).getWechatRemark());
                jsonArray.put(tmpObj);
                tmpObj = null;
            }
            Log.d("obj：：", "getFriendInfo: " + jsonArray.toString());
            updateData(jsonArray);
            StaticData.fbList.clear();
            cFriendIndex = 0;
            writeJSONObjectToSdCard(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交通讯录列表用户信息
     *
     * @param jsonArray
     */
    private void updateData(final JSONArray jsonArray) {
        LogToFile.i("获取通讯录", "上传联系人" + StaticData.fbList.size() + "个");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonRequest = new StringRequest(Request.Method.POST, HttpUtils.ADD_FRIEND_INFO_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("", "response -> " + response);
                        LogToFile.i("获取通讯录", "上传联系人成功：" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ffff", error.getMessage());
                LogToFile.e("获取通讯录", "上传联系人失败：" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("userId", PreferenceUtil.getCurrentUserid(AutoReplyService.this));
                map.put("username", PreferenceUtil.getCurrentUser(AutoReplyService.this));
                map.put("data", jsonArray.toString());
                map.put("wxId", PreferenceUtil.getCurrentWxid(AutoReplyService.this));
                return map;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonRequest);
    }

    /**
     * 更新任务执行状态
     *
     * @param status -1失败 0=未开始 1成功 2异常 -2删掉
     */
    private void doOneTaskStatus(final int status) {
        Log.d("isDoJob>>>", "doOneTaskStatus: AutoReplyService更新任务状态" + StaticData.currentTaskId);
        if (StaticData.currentTaskId.isEmpty()) {
            Toast.makeText(AutoReplyService.this, "任务Id为空哦", Toast.LENGTH_SHORT).show();
            jumpToIndex();
            return;
        }
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonRequest = new StringRequest(Request.Method.POST, HttpUtils.UPDATE_TASK_STATUS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("更新任务状态》》", "response -> " + response);
                        Log.e("111", "response -> " + response);
                        jumpToIndex();
                        Log.d("isDoJob>>>", "执行任务结束》》》>>执行状态" + StaticData.isDoJob);
                        Log.e("111", "执行任务结束》》》>>执行状态" + StaticData.isDoJob);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("111", error.getMessage(), error);
                doOneTaskStatus(status);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("taskId", StaticData.currentTaskId);
                map.put("status", String.valueOf(status));
                return map;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonRequest);
    }

    /**
     * 更新添加好友电话的状态
     */
    private void changePhoneListStatus() {
        Log.d("isDoJob>>>", "changePhoneListStatus: AutoReplyService更新电话状态");
        PhoneUserHelper.updateType(phoneId, addType);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonRequest = new StringRequest(Request.Method.POST, HttpUtils.CHANGE_PHONE_STATUS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("更新电话状态", "response -> " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ffff", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("wxId", PreferenceUtil.getCurrentWxid(AutoReplyService.this));
                map.put("deviceInfo", tm.getDeviceId());
                map.put("ids", phoneId + "#" + addType + "#" + isenable);
                return map;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonRequest);
    }

    /**
     * 更新添加好友成功的状态
     *
     * @param wechatNick
     */
    private void addSuccess(final String wechatNick) {
        Log.d("isDoJob>>>", "changePhoneListStatus: AutoReplyService更新电话状态");
        final long id = PhoneUserHelper.selectPhoneByName(wechatNick);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonRequest = new StringRequest(Request.Method.POST, HttpUtils.ADD_SUCCESS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("更新电话状态", "response -> " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ffff", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("wxName", wechatNick);
                map.put("ids", id + "#" + wechatNick);
                return map;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonRequest);
    }

    /**
     * 将JSON对象写入存储卡
     *
     * @param person
     * @throws FileNotFoundException
     */
    private void writeJSONObjectToSdCard(JSONArray person) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "json"
                + File.separator + "json.txt");
        // 文件夹不存在的话，就创建文件夹
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        // 写入内存卡
        PrintStream outputStream = null;
        try {
            outputStream = new PrintStream(new FileOutputStream(file));
            outputStream.print(person.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    boolean isSendFriend = false;
    boolean addressShow = false;

    private void sendFriendCircle(String className) {                         //6.5.6,页面变成这鬼样了
        Log.e("111",className+"8888888888888888888888888888888888888888888");
        if ((className.equals("com.tencent.mm.plugin.sns.ui.SnsUploadUI") || className.equals("com.tencent.mm.plugin.sns.ui.En_c4f742e5")) && !isSendFriend) {
            StaticData.isFriendCircleSuccess = true;
            Log.e("111",className+"999999999999999999999999999999999999999");
            completeMessage();
        }
    }

    int friendCircleStep = 0;

    private void completeMessage() {
        LogToFile.i("发朋友圈", "1、跳转到发送编辑界面");
        Log.e("111","1、跳转到发送编辑界面");
        wakeAndUnlock(true);
        friendCircleStep = 0;
        isSendFriend = true;

        //这里获取到后台传回来的朋友圈编辑内容然后取出来再用粘贴板展示出来
        SharedPreferences sharedPreferences = getSharedPreferences("zjl", Context.MODE_PRIVATE);
        //getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
        String friendCircleContent = sharedPreferences.getString("friendCircleContent", "");
        if (friendCircleContent!=null){
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null) {
                return;
            }
            List<AccessibilityNodeInfo> commentEdit = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.friendCircleEdit);//寻找评论编辑框
            AccessibilityNodeInfo edit;
            if (!commentEdit.isEmpty()) {
                edit = commentEdit.get(0);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("message", friendCircleContent);
                clipboard.setPrimaryClip(clip);
                boolean b = edit.performAction(AccessibilityNodeInfo.ACTION_FOCUS);  //使输入焦点指向节点的动作。
                boolean b1 = edit.performAction(AccessibilityNodeInfo.ACTION_PASTE);  //粘贴当前剪贴板内容的操作。
            } else {
                edit = findNodeInfosByName(nodeInfo, "android.widget.EditText");
                if (edit != null) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("message",friendCircleContent);
                    clipboard.setPrimaryClip(clip);
                    boolean b = edit.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    boolean b1 = edit.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                }
            }
            sleepTime(500);
        }

        if (!StaticData.friendCircleAddress.isEmpty()) {//判断是否要选择地址
            LogToFile.i("发朋友圈", "2、需要选择地址");
            Log.e("111","2、需要选择地址");
            setLocation();
        }
        sleepTime(1000);
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            LogToFile.e("发朋友圈", "3.4.1、获取不到界面节点");
            Log.e("111","3.4.1、获取不到界面节点");
            return;
        }
        AccessibilityNodeInfo targetNode = null;
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.friendCircleEditCompleted);
        if (list.isEmpty()) {
            list = nodeInfo.findAccessibilityNodeInfosByText("发表");
            LogToFile.e("发朋友圈", "3.4.2、Id找不到发送按钮，通过搜索‘发表’找到按钮");
            Log.e("111","3.4.2、Id找不到发送按钮，通过搜索‘发表’找到按钮");
            sleepTime(500);
        }
        if (!list.isEmpty()) {






            Log.d("isDoJob>>>", "点击发表按钮");
            LogToFile.i("发朋友圈", "3.5、点击发表按钮");
            Log.e("111","3.5、点击发表按钮");
            friendCircleStep = 9;//成功
            targetNode = list.get(0);
            performClick(targetNode);
            Log.e("111",StaticData.friendCircleCommentContent+"00000000000000000000000000");
        }
        wakeAndUnlock(true);
        List<AccessibilityNodeInfo> progressFriendCircle = null;
        int i3 = 0;
        do {
            i3++;
            if (i3++ >= 5) {
                break;
            }
            sleepTime(1000);
            nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null) {
                LogToFile.e("发朋友圈", "3.5.1、获取不到界面节点");
                Log.e("111","3.5.1、获取不到界面节点");
                return;
            }
            progressFriendCircle = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.progressFriendCircle);
        } while (!progressFriendCircle.isEmpty());
        wakeAndUnlock(true);
        Log.e("111",StaticData.friendCircleCommentContent+"111111111111111111111111111111111");
        if (!TextUtils.isEmpty(StaticData.friendCircleCommentContent)) {

            String[] split = StaticData.friendCircleCommentContent.split("#");
            nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null) {
                LogToFile.e("发朋友圈", "3.5.2、获取不到界面节点");
                Log.e("111","3.5.2、获取不到界面节点");
                return;
            }
            Log.e("111",StaticData.friendCircleCommentContent+"333333333333333333333333333333333333333333");
            searchComment(split, 0);
            sleepTime(500);
        }
        Log.e("111",StaticData.friendCircleCommentContent+"222222222222222222222222222222222222");
        isSendFriend = false;
        performBack(this);
        StaticData.isSendFriendCircle = false;
        StaticData.friendCircleCommentContent = "";
        if (friendCircleStep == 9) {
            Log.d("发朋友圈完成》》》", "成功" + friendCircleStep);
            Log.e("111","成功" + friendCircleStep);
            LogToFile.i("发朋友圈", "发朋友圈成功" + friendCircleStep);
            Log.e("111","发朋友圈成功" + friendCircleStep);
            doOneTaskStatus(1);//执行成功
        } else {
            Log.d("发朋友圈完成》》》", "失败" + friendCircleStep);
            Log.e("111","失败" + friendCircleStep);
            LogToFile.i("发朋友圈", "发朋友圈失败" + friendCircleStep);
            Log.e("111","发朋友圈失败" + friendCircleStep);
            doOneTaskStatus(-1);//执行失败
        }
    }

    private void setLocation() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            LogToFile.e("发朋友圈", "2.1、获取界面节点失败！");
            return;
        }
        //查找“所在位置”的按钮
        List<AccessibilityNodeInfo> friendCircleLocation = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.friendCircleLocation);
        if (!friendCircleLocation.isEmpty()) {
            LogToFile.i("发朋友圈", "2.2、点击所在位置按钮");
            friendCircleStep = 1;
            //点击“所在位置”
            performClick(friendCircleLocation.get(0));
        } else {
            friendCircleLocation = nodeInfo.findAccessibilityNodeInfosByText("所在位置");
            if (!friendCircleLocation.isEmpty()) {
                //点击“所在位置”
                LogToFile.i("发朋友圈", "2.2、点击所在位置按钮");
                performClick(friendCircleLocation.get(0));
                friendCircleStep = 1;
            } else {
                Toast.makeText(AutoReplyService.this, "找不到位置按钮", Toast.LENGTH_SHORT).show();
                LogToFile.e("发朋友圈", "2.3、找不到所在位置按钮");
                return;
            }
        }
        List<AccessibilityNodeInfo> locationItems = null;
        wakeAndUnlock(true);
        int i1 = 0;
        //判断选择地址界面是否刷新
        do {
            i1++;
            if (i1 >= 10) {//10秒后刷新定位不成功，直接不选择位置
                if (locationItems != null && !locationItems.isEmpty()) {
                    LogToFile.i("发朋友圈", "2.4、超时不选择地址");
                    performClick(locationItems.get(0));
                    friendCircleStep = 2;//不选择地址成功
                } else {
                    LogToFile.i("发朋友圈", "2.4、超时不选择地址");
                    performBack(this);//选择失败
                }
                return;
            }
            sleepTime(1000);
            nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null) {
                LogToFile.e("发朋友圈", "2.4.1、找不到界面节点");
                return;
            }
            Log.d("WxId.locationItem", "setLocation: WxId.locationItem>>>" + WxId.locationItem);
            locationItems = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.locationItem);
        } while (locationItems.isEmpty() || locationItems.size() <= 1);

        if (!locationItems.isEmpty()) {//遍历定位地址页面是否有需要的地址，如果有则直接返回
            for (int i = 0; i < locationItems.size(); i++) {
                if (locationItems.get(i).getText().toString().equals(StaticData.friendCircleAddress)) {//首页列表搜索到地址，则直接选择该地址
                    LogToFile.i("发朋友圈", "2.5、首页列表搜索到地址，选择返回");
                    performClick(locationItems.get(i));
                    friendCircleStep = 3;//选择地址成功
                    return;
                }
            }
        }
        //选择地址页没有所需要的地址，则点击搜索按钮
        List<AccessibilityNodeInfo> search = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.friendCricleLocationSearch);
        if (!search.isEmpty()) {
            LogToFile.i("发朋友圈", "2.6、首页列表没有该地址，点击搜索按钮");
            performClick(search.get(0));
            friendCircleStep = 4;
        } else {
            Toast.makeText(AutoReplyService.this, "找不到搜索按钮", Toast.LENGTH_SHORT).show();
            LogToFile.e("发朋友圈", "2.6、找不到搜索按钮");
            return;
        }
        sleepTime(1000);
        nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            LogToFile.e("发朋友圈", "2.6.1、获取界面节点失败！");
            return;
        }
        //粘贴地址搜索
        List<AccessibilityNodeInfo> searchLocationEditId = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.searchLocationEditId);
        if (!searchLocationEditId.isEmpty()) {
            LogToFile.i("发朋友圈", "2.7、粘贴地址");
            AccessibilityNodeInfo targetNode = searchLocationEditId.get(0);
            targetNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            //使用剪切板
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("message", StaticData.friendCircleAddress);
            clipboard.setPrimaryClip(clip);
            //粘贴进入内容
            targetNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            friendCircleStep = 5;
        } else {
//            searchLocationEditId = nodeInfo.findAccessibilityNodeInfosByText("搜索附近位置");
//            if (!searchLocationEditId.isEmpty()) {
//                LogToFile.i("发朋友圈", "2.7、粘贴地址");
//                AccessibilityNodeInfo targetNode = searchLocationEditId.get(0);
//                targetNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
//                //使用剪切板
//                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                ClipData clip = ClipData.newPlainText("message", StaticData.friendCircleAddress);
//                clipboard.setPrimaryClip(clip);
//                //粘贴进入内容
//                targetNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
//                friendCircleStep = 5;
//            } else {
            LogToFile.e("发朋友圈", "2.7、找不到编辑框！");
            Toast.makeText(AutoReplyService.this, "找不到编辑框", Toast.LENGTH_SHORT).show();
            return;
//            }
        }
        sleepTime(1000);
        //点击搜索按钮
        //6.6.6这页面根本就没有搜索按钮....by temper
//        //遍历一次,没有结果就返回,有则点击
        List<AccessibilityNodeInfo> nearLocationItems = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.locationItem);
        if (!nearLocationItems.isEmpty()) {//遍历定位地址页面是否有需要的地址，如果有则直接返回
            for (int i = 0; i < nearLocationItems.size(); i++) {
                if (nearLocationItems.get(i).getText().toString().equals(StaticData.friendCircleAddress)) {//首页列表搜索到地址，则直接选择该地址
                    LogToFile.i("发朋友圈", "2.8、首页列表搜索到地址，选择返回");
                    performClick(nearLocationItems.get(i));
                    friendCircleStep = 8;//选择地址成功
                    return;
                }
            }
        }
        //还是找不到地址
        LogToFile.e("发朋友圈", "2.8、没有该地址！");
        Toast.makeText(AutoReplyService.this, "没有该地址,选择不显示", Toast.LENGTH_SHORT).show();
        performBack(this);//关闭软键盘
        sleepTime(1000);
        performBack(this);//返回上一页
        nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            LogToFile.e("发朋友圈", "2.8.1、获取界面节点失败！");
            return;
        }
        List<AccessibilityNodeInfo> noShowAddress = nodeInfo.findAccessibilityNodeInfosByText("不显示位置");
        if (!noShowAddress.isEmpty()) {
            performClick(noShowAddress.get(0));
            LogToFile.e("发朋友圈", "2.8.2、选择不显示地址");
            friendCircleStep = 8;//选择不显示地址
        } else {
            LogToFile.e("发朋友圈", "2.8.2、没返回到'所在位置'！");
            Toast.makeText(AutoReplyService.this, "没返回到'所在位置'", Toast.LENGTH_SHORT).show();
            performBack(this);
            return;
        }
        return;
//        List<AccessibilityNodeInfo> searchId = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.searchId);
//        if (!searchId.isEmpty()) {
//            LogToFile.i("发朋友圈", "2.8、点击搜索按钮");
//            performClick(searchId.get(0));
//            friendCircleStep = 6;
//        } else {
//            searchId = nodeInfo.findAccessibilityNodeInfosByText("搜索");
//            if (!searchId.isEmpty()) {
//                LogToFile.i("发朋友圈", "2.8、点击搜索按钮");
//                performClick(searchId.get(0));
//                friendCircleStep = 6;
//            } else {
//                LogToFile.e("发朋友圈", "2.8、找不到搜索按钮！");
//                Toast.makeText(AutoReplyService.this, "找不到搜索按钮", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//        List<AccessibilityNodeInfo> createNewLocation = null;
//        int i2 = 0;
//        //等待搜索内容界面刷新
//        do {
//            i2++;
//            if (i2 >= 10) {
//                LogToFile.i("发朋友圈", "2.9、搜索不到内容，也没有创建按钮，直接返回");
//                performBack(this);
//                sleepTime(500);
//                performBack(this);
//                return;
//            }
//            sleepTime(1000);
//            nodeInfo = getRootInActiveWindow();
//            if (nodeInfo == null) {
//                LogToFile.e("发朋友圈", "2.9.1、获取界面节点失败！");
//                return;
//            }
//            createNewLocation = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.createNewLocation);
//            if (createNewLocation.isEmpty()) {
//                createNewLocation = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.locationItem);
//            }
//        } while (createNewLocation.isEmpty());
//        //搜索结果是否匹配需要的地址
//        repeatLocation();
//        /**
//         * 已经选择地址了，则不用新建地址，直接返回
//         */
//        if (friendCircleStep == 8) {
//            return;
//        }
//        List<AccessibilityNodeInfo> createNewLocationComplete = null;
//        int i3 = 0;
//        do {
//            i3++;
//            sleepTime(1000);
//            nodeInfo = getRootInActiveWindow();
//            if (nodeInfo == null) {
//                return;
//            }
//            if (i3 >= 10) {
//                LogToFile.i("发朋友圈", "3.3、10秒后还未定位到地址，则新建地址");
//                List<AccessibilityNodeInfo> locationDetail = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.createLocationDetail);
//                if (!locationDetail.isEmpty()) {
//                    LogToFile.i("发朋友圈", "3.3.1、10秒后还未定位到地址，则新建地址，粘贴地址");
//                    AccessibilityNodeInfo info = locationDetail.get(0);
//                    info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
//                    //使用剪切板
//                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                    ClipData clip = ClipData.newPlainText("message", StaticData.friendCircleAddress);
//                    clipboard.setPrimaryClip(clip);
//                    //粘贴进入内容
//                    info.performAction(AccessibilityNodeInfo.ACTION_PASTE);
//                } else {
//                    LogToFile.e("发朋友圈", "3.3.1、10秒后还未定位到地址，则新建地址,找不到地址的控件Id");
//                }
//            }
//            createNewLocationComplete = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.createNewLocationComplete);
//        }
//        while (!createNewLocationComplete.isEmpty() && !createNewLocationComplete.get(0).isEnabled());
//
//        if (!createNewLocationComplete.isEmpty()) {
//            LogToFile.i("发朋友圈", "3.4、新建地址完成，点击完成按钮");
//            performClick(createNewLocationComplete.get(0));
//            friendCircleStep = 8;
//        } else {
//            createNewLocationComplete = nodeInfo.findAccessibilityNodeInfosByText("完成");
//            if (!createNewLocationComplete.isEmpty()) {
//                LogToFile.i("发朋友圈", "3.4、新建地址完成，点击完成按钮");
//                performClick(createNewLocationComplete.get(0));
//                friendCircleStep = 8;
//            } else {
//                LogToFile.e("发朋友圈", "3.4、新建地址完成，找不到完成按钮");
//            }
//        }
    }

    private void repeatLocation() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> createNewLocation = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.createNewLocation);
        //有创建地址的按钮，则直接点击新建地址
        if (!createNewLocation.isEmpty()) {
            LogToFile.i("发朋友圈", "3.0、新建地址");
            performClick(createNewLocation.get(0));
            friendCircleStep = 7;
        } else {//如果地址匹配，则直接选择返回
            List<AccessibilityNodeInfo> locationItem = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.locationItem);
            if (!locationItem.isEmpty()) {
                for (int i = 0; i < locationItem.size(); i++) {
                    if (locationItem.get(i).getText().toString().equals(StaticData.friendCircleAddress)) {
                        LogToFile.i("发朋友圈", "3.1、搜索界面有该地址，直接选择返回");
                        performClick(locationItem.get(i));
                        friendCircleStep = 8;//选择地址成功
                        return;
                    }
                }
                List<AccessibilityNodeInfo> locationList = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.locationList);
                if (!locationList.isEmpty()) {
                    boolean b = locationList.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    if (!b) {
                        LogToFile.i("发朋友圈", "3.2、没有该地址，也没有新建地址按钮，直接返回！");
                        performBack(this);
                        sleepTime(1000);
                        performBack(this);
                        return;
                    }
                    repeatLocation();
                }
            }
        }
    }

    private void searchComment(String[] content, int i) {
        Log.e("111",StaticData.friendCircleCommentContent+"pppppppppppppppppppppppppppppp");
        wakeAndUnlock(true);
        int step = 0;
        sleepTime(1000);
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> commentLists = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.friendCircleComment);//寻找朋友圈右下角评论按钮
        if (commentLists.isEmpty())
            commentLists = nodeInfo.findAccessibilityNodeInfosByText("评论");
        Log.d("FFFFF>>", "searchComment: 寻找朋友圈右下角评论按钮");
        if (!commentLists.isEmpty()) {
            LogToFile.i("发朋友圈", "3.6、点击评论按钮" + i);
            step = 1;
            Log.d("FFFFF>>", "searchComment: 点击评论按钮");
            performClick(commentLists.get(0));
        } else {
            List<AccessibilityNodeInfo> friednList = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.friendCircleListId);//寻找朋友圈List
            if (!friednList.isEmpty()) {
                friednList.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
            LogToFile.i("发朋友圈", "3.6、找不到评论按钮" + i);
            Log.d("FFFFF>>", "searchComment: 找不到评论按钮");
        }
        sleepTime(1000);
        nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> comment = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.friendCircleComment1);//寻找点击评论后弹出框里的评论按钮
        if (comment.isEmpty()) {
            comment = nodeInfo.findAccessibilityNodeInfosByText("评论");
        }
        if (!comment.isEmpty()) {
            LogToFile.i("发朋友圈", "3.7、点击评论后弹出框里的评论按钮" + i);
            step = 2;
            Log.d("FFFFF>>", "searchComment: 点击评论后弹出框里的评论按钮");
            performClick(comment.get(0));
        } else {
            LogToFile.e("发朋友圈", "3.7、找不到评论后弹出框里的评论按钮" + i);
        }
        sleepTime(1000);
        nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> commentEdit = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.friendCircleCommentEditId);//寻找评论编辑框
        AccessibilityNodeInfo edit;
        if (!commentEdit.isEmpty()) {
            Log.e("111",content[i]+"0.0.0.0.0.0.0");
            LogToFile.i("发朋友圈", "3.7、粘贴评论内容" + i);
            step = 3;
            Log.d("FFFFF>>", "searchComment: 粘贴内容");
            edit = commentEdit.get(0);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("message", content[i]);
            clipboard.setPrimaryClip(clip);
            //Log.i("demo", "设置粘贴板");ff
            //焦点 （n是AccessibilityNodeInfo对象）
            boolean b = edit.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            //Log.i("demo", "获取焦点");
            //粘贴进入内容
            boolean b1 = edit.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        } else {
            Log.e("111",content[i]+"1.1.1.1.1.1.1.");
            edit = findNodeInfosByName(nodeInfo, "android.widget.EditText");
            if (edit != null) {
                LogToFile.i("发朋友圈", "3.7、粘贴评论内容" + i);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("message", content[i]);
                clipboard.setPrimaryClip(clip);
                //Log.i("demo", "设置粘贴板");ff
                //焦点 （n是AccessibilityNodeInfo对象）
                boolean b = edit.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                //Log.i("demo", "获取焦点");
                //粘贴进入内容
                boolean b1 = edit.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            }
        }
        sleepTime(1000);
        List<AccessibilityNodeInfo> commentSend = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.friendCircleCommentSendId);//寻找发送按钮
        if (commentSend.isEmpty()) {
            Log.e("111",StaticData.friendCircleCommentContent+"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            commentSend = nodeInfo.findAccessibilityNodeInfosByText("发送");
        }
        if (!commentSend.isEmpty()) {
            Log.e("111",StaticData.friendCircleCommentContent+"bbbbbbbbbbbbbbbbbbbbbbbbbbb");
            LogToFile.i("发朋友圈", "3.8、点击发送按钮" + i);
            step = 4;
            Log.d("FFFFF>>", "searchComment: 点击发送按钮");
            performClick(commentSend.get(0));
        } else {
            LogToFile.e("发朋友圈", "3.8、找不到发送按钮" + i);
        }
        if (step == 4) {
            Log.e("111",StaticData.friendCircleCommentContent+"ccccccccccccccccccccccccccccc");
            if (i < content.length - 1) {
                searchComment(content, i + 1);
                Log.e("111",StaticData.friendCircleCommentContent+"ddddddddddddddddddddddddddddddddddd");
            }
        } else {
            Log.e("111",StaticData.friendCircleCommentContent+"eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            wakeAndUnlock(true);
            sleepTime(1000);
            searchComment(content, 0);
        }
    }

    private void addFriendControl2(String className) {
        if (className.equals("com.tencent.mm.ui.LauncherUI")) {
            jumpToAddrassBook();//1打开通讯录
            LogToFile.i("加好友", "1、打开通讯录");
            addFriendStep = 1;
            sleepTime(500);
            clickNewFriend();//2点击新的朋友
            LogToFile.i("加好友", "2、点击新的朋友");
            sleepTime(500);
            junpToWritePhone();//3点击“微信号/QQ号/手机号”
            LogToFile.i("加好友", "3、点击“微信号/QQ号/手机号”");
            sleepTime(500);
            addFriendRepeat();
        }
    }

    private void addFriendRepeat() {
        AccessibilityNodeInfo info = getRootInActiveWindow();
        List<AccessibilityNodeInfo> clearId = info.findAccessibilityNodeInfosByViewId(WxId.clearEditId);//如果编辑框里面还有内容
        if (!clearId.isEmpty()) {//则 清空编辑框，重新输入
            performClick(clearId.get(0));
            LogToFile.i("加好友", "清空编辑框");
            sleepTime(500);
        }
        searchPhone();//4粘贴手机号码
        sleepTime(500);
        clickSearch();//5点击搜索用户
        sleepTime(500);
        addFriend();//6点击添加通讯录
        sleepTime(500);
        writePermission();//7粘贴验证信息内容
        sleepTime(500);
        sendHiPermission();//8发送验证消息
        sleepTime(1000);
        performBack(this);
        sleepTime(1000);
        if (friendIndex < StaticData.thePhoneUsers.size()) {
            LogToFile.e("加好友", "继续添加好友");
            addFriendRepeat();
        } else {
            LogToFile.e("加好友", "添加好友结束");
            StaticData.isDoJob = false;
            friendIndex = 0;
            phoneId = 0l;
            StaticData.isAddFriend = false;
            performBack(this);
            sleepTime(500);
            performBack(this);
            jumpToIndex();
        }
    }

    private void sendHiPermission() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }

        AccessibilityNodeInfo targetNode = null;

        //找到控件
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.sendPermissionId);
        if (!list.isEmpty()) {
            LogToFile.i("加好友", "点击发送验证消息按钮");
            if (phoneId != 0) {
                PhoneUserHelper.updateStatus(phoneId, 1);
                PhoneUserHelper.updateTime(phoneId);
            }
            targetNode = list.get(0);
            performClick(targetNode);
            changePhoneListStatus();
        } else
            LogToFile.i("加好友", "找不到发送验证消息按钮");
        addFriendStep = 0;
    }

    private void writePermission() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            LogToFile.e("加好友", "写验证消息，未找到界面节点1");
            return;
        }

        AccessibilityNodeInfo targetNode = null;

        //找到控件
        List<AccessibilityNodeInfo> list = null;
        do {
            sleepTime(1000);
            nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null) {
                LogToFile.e("加好友", "写验证消息，未找到界面节点2");
                return;
            }
            list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.permissionEtId);
        } while (list.isEmpty());
        if (!list.isEmpty()) {
            LogToFile.i("加好友", "粘贴验证消息");
            targetNode = list.get(0);
            //使用剪切板
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("message", "我是Song啊");
            clipboard.setPrimaryClip(clip);
            //Log.i("demo", "设置粘贴板");
            //焦点 （n是AccessibilityNodeInfo对象）
            targetNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            //Log.i("demo", "获取焦点");
            //粘贴进入内容
            targetNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            addFriendStep = 7;
        } else
            LogToFile.i("加好友", "找不到粘贴编辑框！");
    }

    int isenable = 0;

    private void addFriend() {
        AccessibilityNodeInfo nodeInfo = null;
        List<AccessibilityNodeInfo> noUser = null;
        List<AccessibilityNodeInfo> list = null;
        sleepTime(1000);
        do {
            sleepTime(1000);
            nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null) {
                LogToFile.i("加好友", "点击添加通讯录，找不到界面节点返回");
                return;
            }
            noUser = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.noUserId);
            list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.addfriendId);
        } while (noUser.isEmpty() && list.isEmpty());
        List<AccessibilityNodeInfo> wechatRemark = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.wechatRemark);
        if (!wechatRemark.isEmpty()) {
            if (phoneId != 0)
                PhoneUserHelper.updateWechatName(phoneId, wechatRemark.get(0).getText().toString());//保存电话号码对应的微信昵称
            LogToFile.i("加好友", "保存此用户的微信昵称");
        } else {
            LogToFile.i("加好友", "找不到此用户的微信昵称的控件Id");
        }
        sleepTime(1000);
        nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            LogToFile.i("加好友", "找不到界面节点返回。。");
            return;
        }
        noUser = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.noUserId);
        list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.addfriendId);
        if (noUser.isEmpty()) {
            AccessibilityNodeInfo targetNode = null;
            //找到控件
            if (!list.isEmpty()) {
                targetNode = list.get(0);
                performClick(targetNode);
                LogToFile.i("加好友", "点击添加通讯录");
                isenable = 1;
            } else {
                //对方已经是好友
                addFriendStep = 0;
                LogToFile.i("加好友", "对方已经是好友");
                isenable = -1;
                changePhoneListStatus();
                PhoneUserHelper.updateStatus(phoneId, 2);
                PhoneUserHelper.updateTime(phoneId);
            }
        } else {
            //没有此用户
            LogToFile.i("加好友", "没有此用户哦");
            isenable = -1;
            PhoneUserHelper.updateStatus(phoneId, 4);
            PhoneUserHelper.updateTime(phoneId);
            changePhoneListStatus();
        }
        Log.d("加好友", "addFriend: addFriendStep = 6;");
        addFriendStep = 6;
    }

    int addType = 0;

    private void clickSearch() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            LogToFile.e("加好友", "点击搜索，找不到界面节点");
            return;
        }

        AccessibilityNodeInfo targetNode = null;

        //找到控件
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.searchfriendId);
        List<AccessibilityNodeInfo> phoneAddressBook = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.phoneAddressBook);//判断此号码是否在手机通讯录里面
        if (list.isEmpty()) {
            list = nodeInfo.findAccessibilityNodeInfosByText("搜索");
            LogToFile.i("加好友", "id找不到，通过‘搜索’来查找搜索按钮");
        }
        if (!list.isEmpty()) {
            if (phoneAddressBook.isEmpty()) {
                targetNode = list.get(0);
                LogToFile.i("加好友", "直接搜索加好友");
                addType = 1;
            } else if (list.size() >= 3) {
                targetNode = list.get(2);
                addType = 2;
                LogToFile.i("加好友", "点击通讯录加好友");
            }
            performClick(targetNode);
        } else {
            LogToFile.i("加好友", "找不到搜索的Item");
        }
        addFriendStep = 5;
    }

    Long phoneId = 0l;

    private void searchPhone() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            LogToFile.e("加好友", "搜索号码，找不到界面节点");
            return;
        }
        AccessibilityNodeInfo targetNode = null;
        //找到控件
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.searchEtId);
        if (!list.isEmpty()) {
            targetNode = list.get(0);
            //使用剪切板
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            String phone = String.valueOf(StaticData.thePhoneUsers.get(friendIndex).getPhoneNum());
            phoneId = StaticData.thePhoneUsers.get(friendIndex).getPhoneId();
            phone = phone.substring(0, 3) + " " + phone.substring(3, 7) + " " + phone.substring(7, phone.length());//电话号码加空格
            LogToFile.i("加好友", "粘贴手机号码" + phone);
            ClipData clip = ClipData.newPlainText("message", phone);
            ++friendIndex;
            clipboard.setPrimaryClip(clip);
            //Log.i("demo", "设置粘贴板");
            //焦点 （n是AccessibilityNodeInfo对象）
            targetNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            //Log.i("demo", "获取焦点");
            //粘贴进入内容
            targetNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            //Log.i("demo", "粘贴内容");
            addFriendStep = 4;
        } else {
            LogToFile.i("加好友", "找不到编辑框~~");
        }
    }

    private void junpToWritePhone() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }

        AccessibilityNodeInfo targetNode = null;

        //找到控件
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.searchListId);
        if (!list.isEmpty())
            targetNode = list.get(0);
        performClick(targetNode);
        addFriendStep = 3;
    }

    private void clickNewFriend() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }

        AccessibilityNodeInfo targetNode = null;

        //找到控件
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.newfriendId);
        if (!list.isEmpty())
            targetNode = list.get(0);
        else {
            list = nodeInfo.findAccessibilityNodeInfosByText("新的朋友");
            if (!list.isEmpty()) {
                targetNode = list.get(0);
            }
        }
        performClick(targetNode);
        addFriendStep = 2;
    }

    private void jumpToAddrassBook() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        AccessibilityNodeInfo targetNode = null;
        targetNode = findNodeInfosByText(nodeInfo, "通讯录");
        performClick(targetNode);
    }

    //常见的微信内部通知，可自行测试并修改
    private boolean isInside(String msg) {
        boolean result = false;
        if (msg.equals("已复制") || msg.equals("已分享") || msg.equals("已下载"))
            result = true;
        if (msg.length() > 6 && (msg.substring(0, 6).equals("当前处于移动") || msg.substring(0, 6).equals("无法连接到服") || msg.substring(0, 6).equals("图片已保存至") || msg.substring(0, 6).equals("网络连接不可")))
            result = true;
        return result;
    }

    @SuppressWarnings("static-access")
    private void setData(String data) {
        Time time = new Time();
        time.setToNow();
        int hour = time.hour;
        int minute = time.minute;
        if (StaticData.showall) {

        } else {
            if (!data.equals("微信：你收到了一条消息。")) {
                data = data.split(":")[0];
                //Log.i("demo", "showall=" + StaticData.showall+" data=" + data);
                data += " 发来一条消息。";
            }
        }
        data = data.format("%s     %02d:%02d", data, hour, minute);
        StaticData.data.add(data);
    }

    /**
     * 自动回复
     */
    @SuppressLint("NewApi")
    private void reply(String req) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }

        AccessibilityNodeInfo targetNode = null;

        //查找文本编辑框
        if (editText == null) {
            Log.i("demo", "正在查找编辑框...");
            //第一种查找方法
            List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.editId);
            if (!list1.isEmpty())
                editText = list1.get(0);
            //第二种查找方法
            if (editText == null)
                editText = findNodeInfosByName(nodeInfo, "android.widget.EditText");
        }
        targetNode = editText;
        //粘贴回复信息
        if (targetNode != null) {
            //android >= 21=5.0时可以用ACTION_SET_TEXT
            //android >= 18=4.3时可以通过复制粘贴的方法,先确定焦点，再粘贴ACTION_PASTE
            //使用剪切板
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            Iterator<Map.Entry<String, String>> iterator = StaticData.autoReplyMap.entrySet().iterator();
            String message = "";
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                if (req.contains(next.getKey())) {
                    message = next.getValue();
                    Log.d("isDoJob>>>", "reply: " + req + ">>>" + message);
                    break;
                }
            }
            if (TextUtils.isEmpty(message)) {
                return;
            }
//            String message = isResponse ? "我现在自动打招呼啊！" : StaticData.message;
            replyMsgList.add(message);
            isResponse = false;
            ClipData clip = ClipData.newPlainText("message", message);
            clipboard.setPrimaryClip(clip);
            //Log.i("demo", "设置粘贴板");
            //焦点 （n是AccessibilityNodeInfo对象）
            boolean b = targetNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            sleepTime(500);
            //Log.i("demo", "获取焦点");
            //粘贴进入内容
            boolean b1 = targetNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            sleepTime(500);
            //Log.i("demo", "粘贴内容");
        }

        //查找发送按钮
        if (targetNode != null) { //通过组件查找
            Log.i("demo", "查找发送按钮...");
            targetNode = null;
            List<AccessibilityNodeInfo> list2 = nodeInfo.findAccessibilityNodeInfosByViewId(WxId.sendId);
            if (!list2.isEmpty())
                targetNode = list2.get(0);
            //第二种查找方法
            if (targetNode == null)
                targetNode = findNodeInfosByText(nodeInfo, "发送");
        }

        //点击发送按钮
        if (targetNode != null) {
            Log.i("demo", "点击发送按钮中...");
            final AccessibilityNodeInfo n = targetNode;
            performClick(n);
            StaticData.replaied++;
        }
        //恢复锁屏状态
//        wakeAndUnlock(false);
    }

    private void sleepTime(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "微信助手服务被中断啦~", Toast.LENGTH_LONG).show();
    }

    //服务开启时初始化
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i("demo", "开启");
        //获取电源管理器对象
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        packageManager = getPackageManager();
        //得到键盘锁管理器对象
        km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        //得到键盘锁管理器对象
        kl = km.newKeyguardLock("unLock");

        editText = null;

        //注册广播接收器
        sreceiver = new ScreenOffReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(sreceiver, filter);

        preceiver = new PhoneReceiver();
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(preceiver, filter);

        tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        //设置一个监听器
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        manager = new CookieManager(
                new PersistentCookieStore(this),
                CookiePolicy.ACCEPT_ALL);
        Toast.makeText(this, "_已开启微信助手服务_", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("demo", "关闭");
        wakeAndUnlock(false);

        editText = null;

        //注销广播接收器
        unregisterReceiver(sreceiver);
        unregisterReceiver(preceiver);

        StaticData.total = 0;
        StaticData.replaied = 0;
        Toast.makeText(this, "_已关闭微信助手服务_", Toast.LENGTH_LONG).show();
    }

    //屏幕状态变化广播接收器，黑屏或亮屏显示锁屏界面
    class ScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //若在通话则不显示锁屏界面
            if (StaticData.iscalling)
                return;
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                if (StaticData.isStartTask)
                    wakeAndUnlock(true);
                Log.i("demo", "screen off");
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                Log.i("demo", "screen on");
                if (canReply)
                    return;
            }
        }
    }

    //通话状态变化广播接收器，通话期间不弹出锁屏活动
    public class PhoneReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                StaticData.iscalling = true;
                Log.i("demo", "去电");
            } else {
                StaticData.iscalling = true;
                Log.i("demo", "来电");
            }
        }
    }

    //通话状态变化广播接收器，通话期间不弹出锁屏活动
    private PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    StaticData.iscalling = false;
                    Log.i("demo", "挂断");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    StaticData.iscalling = true;
                    Log.i("demo", "接听");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    StaticData.iscalling = true;
                    Log.i("demo", "来电");
                    break;
            }
        }
    };
    private TelephonyManager tm;

    public interface ControlFinish {
        void onCompleted();
    }

    private void getNum() {
        StaticData.isGetNum = false;
        jumpToAddrassBook();
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        AccessibilityNodeInfo nodeInfosByName = findNodeInfosByName(nodeInfo, "android.widget.ListView");
        if (nodeInfosByName != null) {
            boolean b = true;
            do {
                b = nodeInfosByName.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            } while (b);
        }
    }




    /**
     * 获取当前微信号
     */
    private void getCurrentWxNum() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        AccessibilityNodeInfo targetNode;

        targetNode = findNodeInfoByText(nodeInfo, "我");
        performClick(targetNode);
        sleepTime(1500);
        AccessibilityNodeInfo nodeInfoMe = getRootInActiveWindow();
        //com.tencent.mm:id/cby 微信号的textview, 原来是com.tencent.mm:id/cdn
        List<AccessibilityNodeInfo> nodeInfos = nodeInfoMe.findAccessibilityNodeInfosByViewId(UI.MINE_WX_TEXTVIEW_ID);
        if (nodeInfos != null) {
            if (nodeInfos.size() > 0) {
                String text = (String) nodeInfos.get(0).getText();
                LogUtils.d(TAG, "text:" + text);
                if (!text.isEmpty()) {
                    Constants.wxno = text.substring(4);
                }
            } else {
                LogUtils.w(TAG, "size为0,找不到:" + UI.MINE_WX_TEXTVIEW_ID + " version:" + UI.WX_VERSION);
                ToastUtil.showLongToast("找不到微信号的id,请检查版本号是否" + UI.WX_VERSION);
            }
        } else {
            LogUtils.w(TAG, "nodeInfos为空");
            ToastUtil.showLongToast("找不到微信号的id,请检查版本号是否" + UI.WX_VERSION);
        }


        //这里是为了获取通讯录信息所以添加模拟点击通讯录新的朋友触发点击作用
        nodeInfo.refresh();
        nodeInfo = getRootInActiveWindow();
        targetNode = findNodeInfoByText(nodeInfo, "通讯录");
        if (targetNode == null) {
            Log.e(TAG, "找不到'通讯录'");
            return ;
        }
        performClick(targetNode);
        sleepTime(1000);


        nodeInfo.refresh();
        nodeInfo = getRootInActiveWindow();
        targetNode = findNodeInfoByText(nodeInfo, "群聊");
        if (targetNode == null) {
            Log.e(TAG, "找不到'群聊'");
            return ;
        }
        performClick(targetNode);
        sleepTime(1000);
        performBack(this);



        if (Constants.token==null){
            jumpToIndex();
            Toast.makeText(this,"请再次点击开启微信辅助按钮",Toast.LENGTH_LONG).show();
        }

        EventBusUtil.sendEvent(new Event(C.EventCode.B));
        Constants.isGetWxInfo = false;
    }


    /**
     * 通过文本查找
     */
    public AccessibilityNodeInfo findNodeInfoByText(AccessibilityNodeInfo nodeInfo, String text) {
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}