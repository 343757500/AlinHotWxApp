package com.mikuwxc.autoreply.service;

import android.accessibilityservice.AccessibilityService;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.Gson;
import com.mikuwxc.autoreply.common.UI;
import com.mikuwxc.autoreply.common.net.NetApi;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.common.util.Constants;
import com.mikuwxc.autoreply.common.util.EventBusUtil;
import com.mikuwxc.autoreply.common.util.LogUtils;
import com.mikuwxc.autoreply.common.util.Logger;
import com.mikuwxc.autoreply.common.util.ObjectToJson;
import com.mikuwxc.autoreply.common.util.SharedPrefsUtils;
import com.mikuwxc.autoreply.common.util.ToastUtil;
import com.mikuwxc.autoreply.modle.AmountBean;
import com.mikuwxc.autoreply.modle.C;
import com.mikuwxc.autoreply.modle.Event;
import com.mikuwxc.autoreply.modle.ImMessageBean;
import com.mikuwxc.autoreply.modle.LocalMessageBean;
import com.mikuwxc.autoreply.modle.MessageBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by leo on 2016/8/4.
 * 自动回复、收取红包与转账
 */
public class AutoReplyService extends AccessibilityService {

    private static final String TAG = AutoReplyService.class.getSimpleName();
    private static boolean sIsBound = false;
    private ImMessageBean moneyBean;
    private static ArrayList<LocalMessageBean> msgList;
    private static long startTime = 0;
    private static String receiver;
    private static String currPage;
    private static String moneyType;

    /**
     * 必须重写的方法，响应各种事件。
     */
    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        int eventType = event.getEventType(); // 事件类型
        String className;
        switch (eventType) {
            //是否跳转页面
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                if (Constants.isGetWxInfo) {
                    getCurrentWxNum();
                }
                className = event.getClassName().toString();
                LogUtils.i("className:" + className + " getMoney:" + Constants.startGetMoney + " moneyStatus:" + Constants.getMoneyStatus + " getMes:" + Constants.startSendMsg + " msgStatus:" + Constants.sendMsgStatus);
                currPage = className;
//*****************  聊天页面  ****************************************
                if (className.endsWith(UI.CHATTING_UI)) {//从搜索进入聊天页面
                    if (Constants.startSendMsg && Constants.sendMsgStatus == Constants.SEND_ON_CHATTING) {
                        if (findUnreadMessage()) {//查找是否还有信息未发送
                            EventBusUtil.sendEvent(new Event(C.EventCode.D, msgList));
                        } else {
                            ToastUtil.showShortToast("所有信息都已发送");
                            finishSendMsgFromChat(true);
                        }
                    }
                    if (Constants.startGetMoney) { //抢红包或者获取转账
                        if (Constants.getMoneyStatus == Constants.GOTO_CHAT_INFO) {
                            // TODO: 2018/4/4 有可能进入了聊天页面但没点击右上角聊天信息按钮
                            boolean action = findAndPerformAction(UI.IMAGEBUTTON, "聊天信息");
                            if (action) {
                                Constants.getMoneyStatus = Constants.FIND_CHAT_RECORD;
                            } else {
                                ToastUtil.showShortToast("无法点击右上角聊天信息按钮");
                                Constants.startGetMoney = false;
                                Constants.getMoneyStatus = Constants.DO_NOTHING;
                            }
                        } else if (Constants.getMoneyStatus == Constants.FOUND_MONEY_RECORD) {
                            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                            clickLuckyMoneyOrWireTransfer(moneyType, rootNode); //点击红包view或转账view
                            Constants.getMoneyStatus = Constants.CLICK_MONEY;
//                            sendAmount(moneyType); //发送金额给后台接口
//                            sleepTime(500);
//                            manyClickBack(); //第一次收红包或收款会额外弹出确认窗问是否存入银行卡,要多两次点返回.以后就是需要点5次返回
//                            Constants.startGetMoney=false;
//                            Constants.getMoneyStatus=0;
                        } else if (Constants.getMoneyStatus == Constants.FINISH_GET_MONEY) {
                            finishSendMsgFromChat(false);
                        }
                    }
//******************   搜索页面   *****************************************
                } else if (className.endsWith(UI.SEARCH_UI)) { //搜索页面
                    if ((Constants.startSendMsg && Constants.sendMsgStatus == Constants.PASTE_ON_SEARCH) || (Constants.startGetMoney && Constants.getMoneyStatus == Constants.PASTE_ON_SEARCH)) {
                        startTime = System.currentTimeMillis();
                        if (receiver != null) {
                            ToastUtil.showShortToast("输入接受者的名字");
                            fillInputBar(receiver); //输入接受者的名字

                            sleepTime(400);
                            if (!clickSearchName()) { //点击第一个搜索结果,如果失败则重试
                                sleepTime(1000); //重试
                                if (!clickSearchName()) {
                                    // TODO: 2018/4/18 要处理未能成功发送的信息,记录下来

                                    //------------------------
                                    ToastUtil.showShortToast("没能点击到或者没有这个人:" + receiver);
                                    setMessageSent(receiver);
                                    removeSentMessage();
                                    finishSendMsgFromSearch();
                                    if (msgList != null) {
                                        receiver = getNextReceiver();
                                        if (receiver != null) { //如果还有其他人的信息,重新进来发送
                                            searchSender(receiver);
                                        }
                                    } else {
                                        ToastUtil.showShortToast("信息列表已空,返回首页");
                                        //返回
                                        finishSendMsgFromSearch();
                                    }
                                }
                            }
                        }
                    } else if (Constants.startSendMsg && Constants.sendMsgStatus == Constants.SEARCH_MORE) { //黏贴其他人的名字去发送信息
                        long endTime = System.currentTimeMillis() - startTime;
                        LogUtils.w("----单次计时结束,耗时:" + endTime);
                        startTime = System.currentTimeMillis();
                        removeSentMessage();
                        if (msgList.size() > 0) {
                            receiver = msgList.get(0).getReceiver(); //获取信息列表中第一位的名字
                            fillInputBar(receiver); //输入发送人的名字
                            sleepTime(400);
                            findAndPerformParentAction("id", UI.SEARCH_RESULT_CONTACTS_TEXTVIEW_ID, UI.TEXTVIEW, UI.RelativeLayout);
                            Constants.sendMsgStatus = Constants.SEND_ON_CHATTING;
                        } else {
                            finishSendMsgFromSearch();
                        }
                    } else if (Constants.startSendMsg && Constants.sendMsgStatus == Constants.FINISH_SEND) {
                        finishSendMsgFromSearch();
                    } else if (Constants.startGetMoney && Constants.getMoneyStatus == Constants.FINISH_GET_MONEY) {
                        Constants.startGetMoney = false;
                        Constants.getMoneyStatus = Constants.DO_NOTHING;
                        finishSendMsgFromSearch();
                    } else if (Constants.sendMsgStatus == Constants.DO_NOTHING) {
                        //什么也不做,用户自己操作
                    }
//*****************   聊天资料页面(聊天页面右上角)  ****************************
                } else if (className.endsWith(UI.CHAT_INFO_UI)) {
                    if (Constants.startGetMoney) {
                        if (Constants.getMoneyStatus == Constants.FIND_CHAT_RECORD) {
                            findAndPerformParentAction("text", "查找聊天记录", UI.TEXTVIEW, UI.LinearLayout);
                            Constants.getMoneyStatus = Constants.PASTE_ON_SEARCH_RECORD;
                        } else if (Constants.getMoneyStatus == Constants.FINISH_GET_MONEY) {
                            //聊天信息左上角的返回
                            if (!findAndPerformParentAction("id", UI.LUCKY_MONEY_DETAIL_BACK_IMAGEVIEW_ID, UI.IMAGEVIEW, UI.LinearLayout)) {
                                clickImageBack();
                            }
                        }
                    }
//*******************  查找聊天记录页面  *****************************************
                } else if (className.endsWith(UI.SEARCH_CHAT_INFO_UI)) { //查找聊天记录页面
                    if (Constants.startGetMoney) {
                        if (Constants.getMoneyStatus == Constants.PASTE_ON_SEARCH_RECORD) {
                            fillInputBar(moneyType);//输入红包或转账
                        } else if (Constants.getMoneyStatus == Constants.FINISH_GET_MONEY) {
                            findAndPerformParentAction("id", UI.SEARCH_CHAT_INFO_BACK_IMAGEVIEW_ID, UI.IMAGEVIEW, UI.LinearLayout); //搜索聊天记录左上角的返回
                        }
                    }
//*******************  弹出红包页面 *****************************************************
                } else if (className.endsWith(UI.LUCKY_MONEY_RECEIVE_UI)) {
                    if (Constants.startGetMoney && Constants.getMoneyStatus == Constants.CLICK_MONEY) {
                        openLuckyMoneyOrWireTransfer(moneyType);//打开红包或确认收款
                        Constants.getMoneyStatus = Constants.OPEN_MONEY;
                    }
//********************  红包详情页面 ******************************************************
                } else if (className.endsWith(UI.LUCKY_MONEY_DETAIL_UI)) {
                    if (Constants.startGetMoney) {
                        if (Constants.getMoneyStatus == Constants.OPEN_MONEY) {
                            sendAmount(moneyType); //发送金额给后台接口
                            sleepTime(200);
                            if (!findAndPerformParentAction("id", UI.LUCKY_MONEY_DETAIL_BACK_IMAGEVIEW_ID, UI.IMAGEVIEW, UI.LinearLayout)) { //红包详情或交易详情左上角的返回
                                clickImageBack();
                            }
                        } else { //如果是CLICK_MONEY直接进来的,代表已经提取了红包了
                            if (!findAndPerformParentAction("id", UI.LUCKY_MONEY_DETAIL_BACK_IMAGEVIEW_ID, UI.IMAGEVIEW, UI.LinearLayout)) {//红包详情左上角的返回
                                clickImageBack();
                            }
                        }
                    }
//*******************  转账详情页面  **************************************************************
                } else if (className.endsWith(UI.TRANSFER_DETAIL_UI)) {
                    if (Constants.startGetMoney) {
                        if (Constants.getMoneyStatus == Constants.CLICK_MONEY) {
                            Constants.getMoneyStatus = Constants.OPEN_MONEY;
                            sendAmount(moneyType);
                            sleepTime(200);
                            openLuckyMoneyOrWireTransfer(moneyType);//打开红包或确认收款
                            LogUtils.i("----收到钱了----");
                        } else if (Constants.getMoneyStatus == Constants.FINISH_GET_MONEY) {
                            //红包详情或交易详情左上角的返回
                            if (!findAndPerformParentAction("id", UI.LUCKY_MONEY_DETAIL_BACK_IMAGEVIEW_ID, UI.IMAGEVIEW, UI.LinearLayout)) {
                                clickImageBack();
                            }
                        }
                    }
                } else {
                    LogUtils.d(TAG, "去了没监听的页面:" + className);
                    ToastUtil.showShortToast("去了没监听的页面");
                }
                break;
        }
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "onInterrupt");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(TAG, "onCreate svc");
        EventBusUtil.register(this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // 再次动态注册广播
        IntentFilter localIntentFilter = new IntentFilter("android.intent.action.USER_PRESENT");
        localIntentFilter.setPriority(Integer.MAX_VALUE);// 整形最大值
        myReceiver searchReceiver = new myReceiver();
        registerReceiver(searchReceiver, localIntentFilter);
        super.onStart(intent, startId);
    }

    /**
     * 2.在Service的onDestroy()中重启Service.
     */
    @Override
    public void onDestroy() {
        Intent localIntent = new Intent();
        localIntent.setClass(this, AutoReplyService.class); // 销毁时重新启动Service
        this.startService(localIntent);
        ToastUtil.showLongToast("已关闭分流助手服务");
        Constants.serviceIsRunning = false;
        EventBusUtil.unregister(this);
    }

    /**
     * 3.创建一个广播
     */
    public static class myReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.startService(new Intent(context, AutoReplyService.class));
        }
    }

    @Override
    protected void onServiceConnected() {
        Log.i(TAG, "connect auto reply service");
        sIsBound = true;
        super.onServiceConnected();
        ToastUtil.showLongToast("已开启分流助手服务");
        Constants.serviceIsRunning = true;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "disconnect auto reply service");
        sIsBound = false;
        return super.onUnbind(intent);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        LogUtils.v(TAG, "+++++  bindService ++++");
        return super.bindService(service, conn, flags);
    }

    public static boolean isConnected() {
        return sIsBound;
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
        sleepTime(500);
        nodeInfo.refresh();
        sleepTime(500);
        nodeInfo = getRootInActiveWindow();
        //com.tencent.mm:id/cby 微信号的textview, 原来是com.tencent.mm:id/cdn
        List<AccessibilityNodeInfo> nodeInfos = nodeInfo.findAccessibilityNodeInfosByViewId(UI.MINE_WX_TEXTVIEW_ID);
        if (nodeInfos.size() > 0) {
            String text = (String) nodeInfos.get(0).getText();
            if (!text.isEmpty()) {
                Constants.wxno = text.substring(4);
            }
        } else {
            ToastUtil.showLongToast("找不到微信号的id,请检查版本号是否" + UI.WX_VERSION);
        }
        jumpToIndex();
        EventBusUtil.sendEvent(new Event(C.EventCode.B));
        Constants.isGetWxInfo = false;
    }

    /**
     * 跳转回app首页
     */
    public void jumpToIndex() {
        Log.e("111","...................................");
        Intent intent=new Intent();
        intent = getPackageManager().getLaunchIntentForPackage("com.mikuwxc.autoreply");
        startActivity(intent);
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
     * 返回事件
     */
    public void performBack(AccessibilityService service) {
        if (service == null) {
            return;
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    /**
     * 根据子控件id或者text查找父控件并且点击
     */
    private boolean findAndPerformParentAction(String findIdOrText, String widget, String widgetClassName, String parentWidgetClassName) {
        // 取得当前激活窗体的根节点
        if (getRootInActiveWindow() == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodes = null;
        // 通过id或者text找到当前的节点
        if (findIdOrText.equals("id")) {
            nodes = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(widget);
        } else if (findIdOrText.equals("text")) {
            nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(widget);
        }
        if (nodes != null) {
            for (AccessibilityNodeInfo node : nodes) {
                if (node.getClassName().equals(widgetClassName) && node.isEnabled()) {
                    AccessibilityNodeInfo parent = node.getParent();
                    if (parent.getClassName().equals(parentWidgetClassName) && node.isEnabled()) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK); // 执行点击
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 打开微信
     */
    private void openWeiXinApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            ToastUtil.showLongToast("检查到您手机没有安装微信，请安装后使用该功能");
        }

    }

    /**
     * 查找UI控件并点击
     *
     * @param widget 控件完整名称, 如android.widget.Button, android.widget.TextView
     * @param text   控件文本
     */
    private boolean findAndPerformAction(String widget, String text) {
        // 取得当前激活窗体的根节点
        if (getRootInActiveWindow() == null) {
            return false;
        }

        // 通过文本找到当前的节点
        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);
        if (nodes != null) {
            for (AccessibilityNodeInfo node : nodes) {
                if (node.getClassName().equals(widget) && node.isEnabled()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK); // 执行点击
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * 填充输入框
     */
    private boolean fillInputBar(String reply) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            return findInputBar(rootNode, reply);
        }
        return false;
    }

    /**
     * 填充输入框
     */
    private boolean fillInputBar(ArrayList<LocalMessageBean> replys) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            return findInputBar(rootNode, replys);
        }
        return false;
    }

    /**
     * 查找EditText控件
     *
     * @param rootNode 根结点
     * @param reply    回复内容
     * @return 找到返回true, 否则返回false
     */
    private boolean findInputBar(AccessibilityNodeInfo rootNode, String reply) {
        int count = rootNode.getChildCount();
//        Log.i(TAG, "root class=" + rootNode.getClassName() + ", " + rootNode.getText() + ", child: " + count);
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo node = rootNode.getChild(i);
            if (UI.EDITTEXT.equals(node.getClassName())) {   // 找到输入框并输入文本
                Log.i(TAG, "****found the EditText");
                fillText(node, reply);
                return true;
            }

            if (findInputBar(node, reply)) {    // 递归查找
                return true;
            }
        }
        return false;
    }

    /**
     * 查找EditText控件
     *
     * @param rootNode 根结点
     * @param replys   回复内容的列表
     * @return 找到返回true, 否则返回false
     */
    private boolean findInputBar(AccessibilityNodeInfo rootNode, ArrayList<LocalMessageBean> replys) {
        int count = rootNode.getChildCount();
//        Log.i(TAG, "root class=" + rootNode.getClassName() + ", " + rootNode.getText() + ", child: " + count);
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo node = rootNode.getChild(i);
            if (UI.EDITTEXT.equals(node.getClassName())) {   // 找到输入框并输入文本
                Log.i(TAG, "****found the EditText");
                fillText(node, replys);
                return true;
            }

            if (findInputBar(node, replys)) {    // 递归查找
                return true;
            }
        }
        return false;
    }

    /**
     * 填充文本
     */
    private void fillText(AccessibilityNodeInfo node, String reply) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.i(TAG, "set text");
            Bundle args = new Bundle();
            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, reply);
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
        } else {
            ClipData data = ClipData.newPlainText("reply", reply);
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(data);
            node.performAction(AccessibilityNodeInfo.ACTION_FOCUS); // 获取焦点
            node.performAction(AccessibilityNodeInfo.ACTION_PASTE); // 执行粘贴
        }
        EventBusUtil.sendEvent(new Event(C.EventCode.E, null));//点击发送
    }

    private void fillText(AccessibilityNodeInfo node, ArrayList<LocalMessageBean> replys) {
        String msg = "";
        Gson gson = new Gson();
        for (int i = 0; i < replys.size(); i++) {
            LocalMessageBean msgBean = replys.get(i);
            if (msgBean.getReceiver().equals(receiver) && !msgBean.isSent()) {
                msg = msgBean.getMsg();
                SharedPrefsUtils.putString(Constants.LAST_MSG_KEY, gson.toJson(msgBean)); //把最新发送的信息保存起来以便之后对比
                replys.get(i).setSent(true);
                break;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.i(TAG, "set texts:" + msg);
            Bundle args = new Bundle();
            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, msg);
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
        } else {
            ClipData data = ClipData.newPlainText("reply", msg);
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(data);
            node.performAction(AccessibilityNodeInfo.ACTION_FOCUS); // 获取焦点
            node.performAction(AccessibilityNodeInfo.ACTION_PASTE); // 执行粘贴
        }
        EventBusUtil.sendEvent(new Event(C.EventCode.E, replys));
    }

    /**
     * 点击搜索结果第一位
     *
     * @return
     */
    private boolean clickSearchName() {
        boolean clickName = findAndPerformParentAction("id", "com.tencent.mm:id/l7", UI.TEXTVIEW, UI.RelativeLayout);
        if (clickName) {
            if (Constants.startSendMsg) {
                Constants.sendMsgStatus = Constants.SEND_ON_CHATTING;
            }
            if (Constants.startGetMoney) {
                Constants.getMoneyStatus = Constants.GOTO_CHAT_INFO;
            }
        }
        return clickName;
    }

    /**
     * 找出所有被标记为已发送的信息,并移除
     */
    private void removeSentMessage() {
        for (int i = msgList.size() - 1; i >= 0; i--) {//找出所有被标记为已发送的信息,并移除
            LocalMessageBean bean = msgList.get(i);
            if (bean.isSent()) {
                msgList.remove(i);
            }
        }
    }

    /**
     * 查找是否还有信息未发送
     *
     * @return
     */
    private boolean findUnreadMessage() {
        for (int i = 0; i < msgList.size(); i++) {
            if (!msgList.get(i).isSent()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置该接受者所有信息为已发送
     *
     * @param rcv
     */
    private void setMessageSent(String rcv) {
        for (int i = 0; i < msgList.size(); i++) {
            if (msgList.get(i).getReceiver().equals(rcv)) {
                msgList.get(i).setSent(true);
            }
        }
    }

    /**
     * 返回下一个有未发送信息的接受者名字
     *
     * @return 返回null的话代表没有其他人的信息要发送了
     */
    private String getNextReceiver() {
        for (int i = 0; i < msgList.size(); i++) {
            if (!msgList.get(i).isSent() && !msgList.get(i).getReceiver().equals(receiver)) {
                return msgList.get(i).getReceiver();
            }
        }
        return null;
    }

    /**
     * 设置线程睡眠时间
     *
     * @param time 睡眠时间（单位：毫秒）
     */
    private void sleepTime(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听并处理EventBus事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEventBusCome(Event event) {
        if (event != null) {
            // 接受到Event后的相关逻辑
            switch (event.getCode()) {
                case C.EventCode.A:
                    /*
                     * 打开微信主页
                     */
                    //openWeiXinApp();
                    ImMessageBean messageBean = (ImMessageBean) event.getData();
                    int type = Integer.parseInt(messageBean.getType());
//                    String message = messageBean.getText();
                    /*
                      0:文本; 1:图片; 2:语音; 3:红包; 4:表情; 5:系统; 6:转账 ; 7:获取好友微信号
                     */
                    switch (type) {
                        case 0:
                        case 1:
                        case 2:
                        case 4:
                        case 5:
                            sendMessageToCustomer(messageBean);
                            break;
                        case 3:
                            findLuckMoneyOrWireTransfer("微信红包", messageBean);
                            break;
                        case 6:
                            findLuckMoneyOrWireTransfer("微信转账", messageBean);
                            break;
                        case 7:
                            findFriend(messageBean);
                            break;
                        default:
                            break;
                    }
                    break;
//                case C.EventCode.C:
//
//
//                    break;
                case C.EventCode.D:  //开始黏贴字符
                    if (event.getData() != null) {
                        ArrayList<LocalMessageBean> list = (ArrayList<LocalMessageBean>) event.getData();
                        if (list != null) {
                            fillInputBar(list);
                        } else {
                            fillInputBar("test:" + event.getData().toString());
                        }
                    }
                    break;
                case C.EventCode.E: //填充完文本(包括输入接受者名字和信息. 点击聊天页面的发送,如果全部信息已发送就点击返回,还有信息就继续发送
                    LogUtils.i("-----填充完毕!-----");
//                    ToastUtil.showShortToast(event.getData().toString());
                    if (currPage.endsWith(UI.CHATTING_UI)) {
                        if (Constants.sendMsgStatus == Constants.SEND_ON_CHATTING) {
                            findAndPerformAction(UI.BUTTON, "发送");
                            if (event.getData() != null) {
                                ArrayList<LocalMessageBean> list = (ArrayList<LocalMessageBean>) event.getData();
                                if (list != null) {
                                    if (list.size() > 1) { //包含那条已黏贴并点击发送的信息,但还没移除
//                                        int index = -1;
                                        int count = 0;
                                        int unread = 0;
                                        for (int i = 0; i < list.size(); i++) {
                                            LocalMessageBean msgBean = list.get(i);
                                            LogUtils.i("list", "**** " + msgBean.toString());
                                            if (!msgBean.isSent()) {
                                                if (msgBean.getReceiver().equals(receiver)) {
                                                    count++;//找出这个接受者还有多少条未发送的信息
                                                }
                                                unread++;
                                            }
                                        }
                                        if (unread > 0) { //还有信息未发送
                                            if (count > 0) {//此接受者还有信息
                                                EventBusUtil.sendEvent(new Event(C.EventCode.D, list));
                                            } else {//此接受者没信息了,有其他接受者的信息,返回搜索
                                                finishSendMsgFromChat(false);
                                            }
                                        } else {
                                            //所有用户的信息已发送完
                                            finishSendMsgFromChat(true);
                                        }
                                    } else { //所有用户的信息已发送完
                                        finishSendMsgFromChat(true);
                                    }
                                } else {
                                    finishSendMsgFromChat(true);
                                }
                            } else { //可以点击'返回'了
                                finishSendMsgFromChat(true);
                            }
                        }
                    } else if (currPage.endsWith(UI.SEARCH_CHAT_INFO_UI)) {//聊天记录搜索页面
                        sleepTime(400);
                        if (!findAndPerformParentAction("text", moneyType, UI.TEXTVIEW, UI.LinearLayout)) {//点击对应的聊天记录,跳转到聊天页面相应的红包或转账记录
                            LogUtils.v("没点击到记录,等400");
                            sleepTime(400);
                            if (!findAndPerformParentAction("text", moneyType, UI.TEXTVIEW, UI.LinearLayout)) {
                                ToastUtil.showShortToast("没找到相应记录,请安排人手处理");
                                LogUtils.v("还没找到相应记录");
                                // TODO: 2018/4/18 要记录未能收到的钱,要人手处理

                                //*****************************************
                                Constants.getMoneyStatus = Constants.FINISH_GET_MONEY;
                                findAndPerformParentAction("id", UI.SEARCH_CHAT_INFO_BACK_IMAGEVIEW_ID, UI.IMAGEVIEW, UI.LinearLayout); //搜索聊天记录左上角的返回
                            } else {//第二次才点到
                                Constants.getMoneyStatus = Constants.FOUND_MONEY_RECORD;
                            }
                        } else {//第一次就点到了
                            Constants.getMoneyStatus = Constants.FOUND_MONEY_RECORD;
                        }
                    }
                    break;
                case C.EventCode.I:
                    //找图片
                    LogUtils.i(TAG, "找图片");
                    // 可以不用在 Activity 中增加任何处理，各 Activity 都可以响应
//                    Instrumentation inst = new Instrumentation();
//                    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
//                            MotionEvent.ACTION_DOWN, 200, 500, 0));
//                    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
//                            MotionEvent.ACTION_UP, 200, 500, 0));
                    break;
            }
        }
    }

    /**
     * 根据备注发送信息
     * @param bean
     */
    private void sendMessageToCustomer(ImMessageBean bean) {
        Gson gson = new Gson();
        String last = SharedPrefsUtils.getString(Constants.LAST_MSG_KEY);
        LocalMessageBean localMsg = new LocalMessageBean(bean.getWxno(), bean.getContent(), bean.getConversationTime());
        String newBean = gson.toJson(localMsg);
        if (last.equals(newBean)) { //先对比是否因登录重新发送了一样的信息
            ToastUtil.showShortToast("与上一条信息一样,不需要重发");
            LogUtils.v("与上一条信息一样,不需要重发");
            return;
        }
        LogUtils.i("sendMessageToCustomer", "startSendMsg:" + Constants.startSendMsg + " *** status:" + Constants.sendMsgStatus);
        if (!Constants.startSendMsg) {
            Constants.sendMsgStatus = Constants.START_SEND_MSG;
            Constants.startSendMsg = true;
            receiver=null;
        }
        if (msgList == null) {
            msgList = new ArrayList<>();
        }
        msgList.add(localMsg);
        if (Constants.sendMsgStatus == Constants.START_SEND_MSG) {
            for (int i = 0; i < msgList.size(); i++) {
                if (!msgList.get(i).isSent()) {
                    searchSender(msgList.get(i).getReceiver()); //拿出信息列表里面未读信息第一位的接受者,先去发送
                    break;
                }
            }

        }
    }

    private void findFriend(ImMessageBean bean) {
        Constants.isLookingFriend = true;
        searchSender(bean.getWxno());
    }

    /**
     * 发送信息
     *
     * @param message
     */
    private void sendMessage(String message) {
        //粘贴信息内容
        fillInputBar(message);
        sleepTime(500);
        //点击发送按钮
        findAndPerformAction(UI.BUTTON, "发送");
        performBack(this);
        sleepTime(100);
        performBack(this);
        sleepTime(200);
        performBack(this);
        sleepTime(100);
    }

    /**
     * 通过备注查找发送人
     *
     * @param sender
     */
    private void searchSender(String sender) {
        sleepTime(400);
        receiver = sender;
        if (Constants.startSendMsg && Constants.sendMsgStatus == Constants.START_SEND_MSG) {
            Constants.sendMsgStatus = Constants.PASTE_ON_SEARCH;
        }
        if (Constants.startGetMoney && Constants.getMoneyStatus == Constants.START_GET_MONEY) {
            Constants.getMoneyStatus = Constants.PASTE_ON_SEARCH;
        }
        boolean search = findAndPerformAction(UI.TEXTVIEW, "搜索");//可判断是否点击到搜索按钮
        if (!search) {
            sleepTime(1000);//等待并尝试一次
            boolean tryAgain = findAndPerformAction(UI.TEXTVIEW, "搜索");//可判断是否点击到搜索按钮
            if (!tryAgain) {
                if (Constants.startSendMsg) {
                    //无法点击搜索按钮,信息留待下次发送
                    LogUtils.v("有信息未发送,无法点击搜索按钮");
                 //   ToastUtil.showShortToast("无法点击搜索按钮,请切换到微信首页,信息将留待下次发送");
                    Constants.sendMsgStatus = Constants.DO_NOTHING;
                    Constants.startSendMsg = false;
                }
                if (Constants.startGetMoney) {
                    LogUtils.v("有钱未提取,无法点击搜索按钮");
                  //  ToastUtil.showShortToast("无法点击搜索按钮,请切换到微信首页,需手动打开红包或转账");
                    Constants.startGetMoney = false;
                    Constants.getMoneyStatus = Constants.DO_NOTHING;
                }
            }
        } else {
            startTime = System.currentTimeMillis();
        }
//        EventBusUtil.sendEvent(new Event(C.EventCode.C, sender));
//        sleepTime(500);
//        fillInputBar(sender); //输入发送人的名字

    }

    /**
     * 点击聊天页面左上角的返回
     */
    private void finishSendMsgFromChat(boolean reset) {
//        Constants.startSendMsg=false;
        boolean back = findAndPerformParentAction("id", UI.CHATUI_BACK_IMAGEVIEW_ID, UI.IMAGEVIEW, UI.LinearLayout);//点击'返回'
        if (back) {
            if (Constants.startSendMsg) {
                Constants.sendMsgStatus = Constants.SEARCH_MORE;
            }
            if (reset) {
                if (Constants.startSendMsg) {
                    Constants.sendMsgStatus = Constants.FINISH_SEND;
                }
            }
        } else {
            LogUtils.v("finishSendMsgFromChat", "点击不到返回");
            sleepTime(1000);
            clickImageBack();//点击'返回'
//            findAndPerformParentAction("id", UI.CHATUI_BACK_IMAGEVIEW_ID, UI.IMAGEVIEW, UI.LinearLayout);//点击'返回'
        }

    }

    /**
     * 点击搜索页面左上角的返回
     */
    private void finishSendMsgFromSearch() {
        if (msgList != null) {
            if (msgList.size() == 0) {
                msgList = null;
                receiver = null;
            } else {
                removeSentMessage();
            }
        }
        if (!findAndPerformParentAction("id", UI.SEARCH_BACK_IMAGEVIEW_ID, UI.IMAGEVIEW, UI.LinearLayout)) {
            clickImageBack();//点击'返回'
        }
        long endTime = System.currentTimeMillis() - startTime;
        LogUtils.w("----结束计时----返回到主界面,耗时:" + endTime);
        if (Constants.startSendMsg) {
            Constants.sendMsgStatus = Constants.DO_NOTHING;
            Constants.startSendMsg = false;
        }

    }

    /**
     * 点击父控件为LinearLayout的返回键
     *
     * @return
     */
    private boolean clickImageBack() {
        return findAndPerformParentAction("text", "返回", UI.IMAGEVIEW, UI.LinearLayout);
    }

    /**
     * 多次点击返回键
     */
    private void manyClickBack() {
        sleepTime(500);
        performBack(AutoReplyService.this);
        sleepTime(500);
        performBack(AutoReplyService.this);
        sleepTime(500);
        performBack(AutoReplyService.this);
        sleepTime(500);
        performBack(AutoReplyService.this);
        sleepTime(500);
        performBack(AutoReplyService.this);
        sleepTime(500);
        performBack(AutoReplyService.this);
//        sleepTime(500);
//        performBack(AutoReplyService.this);
    }

    /**
     * 查找红包、转账 ①
     * 微信切换到后台的时候,收到红包或转账,有可能来不及切换回前台而导致没有点击搜索按钮
     * @param bean
     */
    private void findLuckMoneyOrWireTransfer(String type, ImMessageBean bean) {
        Gson gson = new Gson();
        String last = SharedPrefsUtils.getString(Constants.LAST_MSG_KEY); //有可能是红包信息或者文本信息
        String newBean = gson.toJson(bean);
        if (last.equals(newBean)) { //先对比是否因登录重新发送了一样的信息
            ToastUtil.showShortToast("与上一条信息一样,不需要重发");
            LogUtils.v("与上一条信息一样,不需要重发");
            return;
        }
        Constants.startGetMoney = true;
        Constants.getMoneyStatus=Constants.START_GET_MONEY;
        moneyType = type;
        moneyBean = bean;
        searchSender(bean.getWxno());
    }

    /**
     * 查找红包、转账 ②
     */
    private void WXLuckMoneyOrWireTransfer(String type) {
//        sleepTime(500);
//        fillInputBar(type);//输入红包或转账
//        sleepTime(500);
//        findAndPerformParentAction("text", type, UI.TEXTVIEW, UI.LinearLayout); //点击对应的聊天记录,跳转到相应的聊天记录
//        sleepTime(500);
//        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
//        clickLuckyMoneyOrWireTransfer(type, rootNode); //点击红包view
//        sleepTime(500);
//        openLuckyMoneyOrWireTransfer(type); //打开红包或确认收款
//        sleepTime(2000);
//
//        sendAmount(type); //发送金额给后台接口
//
//        sleepTime(500);

//        manyClickBack(); //第一次收红包或收款会额外弹出确认窗问是否存入银行卡,要多两次点返回.以后就是需要点5次返回
//        Constants.startGetMoney=false;
//        Constants.getMoneyStatus=Constants.FINISH_GET_MONEY;
    }

    /**
     * 发送金额
     * @param type
     */
    //TODO 等待接口
    private void sendAmount(String type) {
        List<AccessibilityNodeInfo> nodes = null;
        if (type.equals("微信红包")) {
            nodes = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(UI.LUCKY_MONEY_AMOUNT_TEXTVIEW_ID);
        } else if (type.equals("微信转账")) {
            nodes = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(UI.WIRE_TRANSFER_AMOUNT_TEXTVIEW_ID);
        }
        if (nodes != null) {
            if (nodes.size()!=0){
                sendAmountToAPI(nodes.get(0));
            } else {
                LogUtils.d("nodes size=0");//再尝试
                sleepTime(800);
                if (type.equals("微信红包")) {
                    nodes = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(UI.LUCKY_MONEY_AMOUNT_TEXTVIEW_ID);
                } else if (type.equals("微信转账")) {
                    nodes = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(UI.WIRE_TRANSFER_AMOUNT_TEXTVIEW_ID);
                }
                if (nodes != null) {
                    if (nodes.size() != 0) {
                        sendAmountToAPI(nodes.get(0));
                    } else {
                        LogUtils.d("nodes size依然=0");
                        ToastUtil.showShortToast("无法发送请求到接口");
                    }
                } else {
                    LogUtils.d("nodes依然为空");
                    ToastUtil.showShortToast("无法发送请求到接口");
                }
            }
        } else {
            LogUtils.d("nodes为空");
        }
//        findAndPerformParentAction("id", UI.LUCKY_MONEY_DETAIL_BACK_IMAGEVIEW_ID, UI.IMAGEVIEW, UI.LinearLayout); //红包详情或交易详情左上角的返回
        Constants.getMoneyStatus = Constants.FINISH_GET_MONEY;
        Gson gson = new Gson();
        SharedPrefsUtils.putString(Constants.LAST_MSG_KEY, gson.toJson(moneyBean)); //把最新发送的信息保存起来以便之后对比
    }

    private void sendAmountToAPI(AccessibilityNodeInfo node) {
        String text = (String) (node.getText());
        Logger.d("金额是： " + text);
        moneyBean.setContent(text);
        OkGo.post(AppConfig.getSelectHost() + NetApi.amount).upJson(ObjectToJson.javabeanToJson(moneyBean)).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                AmountBean amountBean = new Gson().fromJson(s, AmountBean.class);
                if (amountBean.isSuccess()) {
                    Logger.d("金额回调成功");
                } else {
                    Logger.d("金额回调失败");

                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                e.printStackTrace();
            }
        });
    }

    /**
     * 点击领取红包、转账
     *
     * @param type     类型
     * @param rootNode
     */
    private void clickLuckyMoneyOrWireTransfer(String type, AccessibilityNodeInfo rootNode) {
        if (rootNode != null) {
            int count = rootNode.getChildCount();
            for (int i = count - 1; i >= 0; i--) {  // 倒序查找最新的红包、转账
                AccessibilityNodeInfo node = rootNode.getChild(i);
                if (node == null)
                    continue;
                String keyWord = "";
                if (type.equals("微信红包")) {
                    keyWord = "领取红包";
                } else if (type.equals("微信转账")) {
                    keyWord = "￥";
                }
                CharSequence text = node.getText();
                if (text != null && text.toString().contains(keyWord)) {
                    AccessibilityNodeInfo parent = node.getParent();
                    while (parent != null) {
                        if (parent.isClickable()) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break;
                        }
                        parent = parent.getParent();
                    }
                }
                clickLuckyMoneyOrWireTransfer(type, node);
            }
        }
    }

    /**
     * 打开红包、转账
     */
    private boolean openLuckyMoneyOrWireTransfer(String type) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            String widgetId = "";
            if (type.equals("微信红包")) {
                widgetId = UI.OPEN_LUCKY_MONEY_BUTTON_ID;
            } else if (type.equals("微信转账")) {
                widgetId = UI.OPEN_WIRE_TRANSFER_BUTTON_ID;
            }
            List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(widgetId);
            for (AccessibilityNodeInfo node : nodes) {
                if (node.isClickable()) {
                    LogUtils.i(TAG, "open LuckyMoneyOrWireTransfer:" + type);
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            }
        }
        return false;
    }
}
