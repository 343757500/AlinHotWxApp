package com.mikuwxc.autoreply;

import android.app.PendingIntent;

import com.mikuwxc.autoreply.activity.RunningActivity;
import com.mikuwxc.autoreply.bean.FriendBean;
import com.mikuwxc.autoreply.bean.TaskJob;
import com.mikuwxc.autoreply.db.PhoneUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticData {
    //默认自动回复的消息内容
    static String message = "您好本人在忙待会回您请稍等  【自动回复】";

    //是否指定好友
    static boolean isfriend = true;
    //默认指定的好友昵称
    static String friend = "在此指定一位好友";
    //是否开启自动回复
    public static boolean autoReply = false;
    //是否开启首页自动回复》》状态
    public static boolean wechatAuto = false;
    //从通知栏点击进入需要自动回复
    public static boolean autoReplyFromNotification = false;
    //正在回复内容
    public static boolean autoIng = false;
    //是否正在执行任务
    public static boolean isDoJob = false;
    //是否添加好友
    public static boolean isAddFriend = false;
    //是否添加好友
    static boolean isAddLastFriend = false;
    //是否验证好友添加
    public static boolean isCheckFriend = false;
    //是否发朋友圈
    public static boolean isSendFriendCircle = false;
    //是否获取好友信息
    public static boolean isGetFriendInfo = false;
    //是否获取好友信息
    public static boolean isGetFriendInfoStart = false;
    //是否创建群
    static boolean isCreateGroup = false;
    //是否监听消息转发
    static boolean isTransmitGroup = false;
    static List<FriendBean> fbList = new ArrayList<>();
    //互聊好友列表
    public static List<String> huliaoList = new ArrayList<>();

    static AutoReplyService.ControlFinish controlFinish = new RunningActivity();

    //锁屏界面是否显示消息详细内容
    static boolean showall = true;
    //是否来电或正在通话，用于是否显示锁屏界面
    static boolean iscalling = false;
    //是否轮询请求任务
    public static boolean isStartTask = false;
    //消息总数
    public static int total = 0;
    //已自动回复的消息总数
    public static int replaied = 0;
    //照片Id
    public static int IMGID = 0;
    //是否自动回复中
    public static boolean isReply = false;
    //是否朋友圈发送成功
    public static boolean isFriendCircleSuccess = false;
    //是否可加好友
    public static boolean isCanAddFriend = false;
    //是否开启互聊功能
    public static boolean isChatOthers = false;
    //互聊语句
    public static String chatContent = "今天天气真不错！";
    //收到的微信消息列表
    public static List<String> data = new ArrayList<String>();
    //任务列表
    public static List<TaskJob> jobList = new ArrayList<>();
    //任务列表
    public static List<PendingIntent> intentList = new ArrayList<>();
    //要操作的电话联系人列表
    public static List<PhoneUser> thePhoneUsers = new ArrayList<>();
    //自动回复规则
    public static Map<String, String> autoReplyMap = new HashMap<>();
    public static String currentUser = "";
    public static String currentUserId = "";
    public static String currentWxId = "";
    public static String currentTaskId = "";
    public static String friendCircleCommentContent = "";//朋友圈评论内容
    public static String friendCircleAddress = "";//朋友圈位置
    public static boolean isUpdateFriend = false;
    public static List<FriendBean> friendBeanList = new ArrayList<>();
    public static boolean isGetWxInfo = false;

    public static boolean isGetNum = false;
}
