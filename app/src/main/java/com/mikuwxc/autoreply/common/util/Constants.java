package com.mikuwxc.autoreply.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miku01 on 2018-03-16.
 */
//常量
public class Constants {
    public static boolean isGetWxInfo = false;//获取微信号
    public static String token;
    public static String wxno;//微信号
    public static String phone;//获取手机号码
    public static String imei;//设备IMEI
    public static boolean serviceIsRunning = false;//辅助服务是否在运行了
    public static boolean wechatSvcIsRunning = false;
    public static boolean startSendMsg = false;
    public static int sendMsgStatus = 0;
    public static int getMoneyStatus = 0;
    public static boolean startGetMoney = false;
    public static boolean isLookingFriend = false;
    public static final String LAST_MSG_KEY = "lastMsg";
    public static List<String> wxidList = new ArrayList<>();
//    public static boolean fromChatInfo = false;

    public static final int DO_NOTHING = 0;
    public static final int START_SEND_MSG = 1;//开始发送信息
    public static final int PASTE_ON_SEARCH = 2;//去搜索页面黏贴
    public static final int SEND_ON_CHATTING = 3;//在聊天页面发送信息
    public static final int SEARCH_MORE = 4;//搜索其他人
    public static final int FINISH_SEND = 5;//发送完所有信息,返回主页面

    public static final int START_GET_MONEY = 11;
    public static final int GOTO_CHAT_INFO = 12; //紧接2,进入聊天页面,点击右上角
    public static final int FIND_CHAT_RECORD = 13; //点击"查找聊天记录
    public static final int PASTE_ON_SEARCH_RECORD = 14;//去搜索页面黏贴
    public static final int FOUND_MONEY_RECORD = 15;
    public static final int CLICK_MONEY = 16; //点击红包或转账
    public static final int OPEN_MONEY = 17; //打开红包或确认转账
    public static final int FINISH_GET_MONEY = 19;
}
