package com.mikuwxc.autoreply.common.net;


/**
 * Created by miku01 on 2018-03-14.
 */

public class NetApi {

//    public final static String host = "http://120.24.102.187:8066/";     //外网测试服务器
//    public final static String host = "http://192.168.5.87:8080/";      //局域网测试服务器

//    public static String login = host + "api/1.0/im/user/login/wechat"; //登录

    //    public static String login = "api/1.0/im/user/login/wechat"; //登录
    public static String login = "api/1.0/wechat"; //登录
    public static String alive = "api/1.0/wechat/app/alive"; //心跳
    public static String amount = "api/1.0/wechatBot/amount/callback"; //金额回调
    public static final String syncFriend = "api/1.0/wechatFriend/synchronize";

    public static final String addFriend = "api/1.0/wechatFriend";
    public static final String sendMessage = "api/1.0/wechatMessage";
    public static final String loginImeiLogin = "api/1.0/device/login";
    public static final String loginImeiAlive = "api/1.0/device/alive";
    public static final String syncMessage = "api/1.0/wechatMessage/synchronize";

    public static final String loginAlive = "api/1.0/wechat/alive";
    public static final String luckyMessage = "api/1.0/wechatMessage/ask/luckyPackage";
    public static final String imLogin = "api/1.0/im/wxno";

    public static final String deletefriend = "/api/1.0/logger/wechatFriend/delete";
    public static final String loginweb = "api/1.0/log/login";
    public static final String retract = "api/1.0/wechatMessage/retract";  //撤回消息

    public static final String chatRecord = "api/1.0/log/chatRecord";  //撤回和删除消息

    public static final String upDateHot = "/api/1.0/logger/version/code";
}
