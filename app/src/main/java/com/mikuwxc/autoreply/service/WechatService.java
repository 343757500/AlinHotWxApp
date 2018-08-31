package com.mikuwxc.autoreply.service;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.mikuwxc.autoreply.common.UI;
import com.mikuwxc.autoreply.common.util.Constants;
import com.mikuwxc.autoreply.common.util.EventBusUtil;
import com.mikuwxc.autoreply.common.util.LogUtils;
import com.mikuwxc.autoreply.common.util.ToastUtil;
import com.mikuwxc.autoreply.modle.C;
import com.mikuwxc.autoreply.modle.Event;
import com.mikuwxc.autoreply.modle.LocalMessageBean;
import com.mikuwxc.autoreply.modle.MessageBean;
import com.mikuwxc.autoreply.view.activity.SecondActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2018/5/7 13:48.
 * Desc:
 */
public class WechatService extends AccessibilityService {
    private static final String TAG = WechatService.class.getSimpleName();

    private MessageBean moneyBean;
    private static ArrayList<LocalMessageBean> msgList;
    private static long startTime = 0;
    private static String receiver;
    private static String currPage;
    private static String moneyType;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
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
                break;
        }
    }

    @Override
    public void onInterrupt() {
        LogUtils.v(TAG, "______ onInterrupt _________");
    }

    @Override
    public void onDestroy() {
        LogUtils.v(TAG, "+++++++ onDestroy +++++++");
        super.onDestroy();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LogUtils.i(TAG, " **** onServiceConnected *****");
        Constants.wechatSvcIsRunning = true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(TAG, "---- onCreate----");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
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
                    Log.e("111",Constants.wxno);
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



     /*   if (Constants.token==null){
            jumpToIndex();
            Toast.makeText(this,"请再次点击开启微信辅助按钮",Toast.LENGTH_LONG).show();
        }*/

        EventBusUtil.sendEvent(new Event(C.EventCode.B));
        Log.e("111","pppppppppppppppppppp");
        Constants.isGetWxInfo = false;
    }

    /**
     * 跳转回app首页
     */
    public void jumpToIndex() {
        Intent intent;
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
}
