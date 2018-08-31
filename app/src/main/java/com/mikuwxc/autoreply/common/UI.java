package com.mikuwxc.autoreply.common;

/**
 * Created by lenovo on 2016/8/7.
 * UI控件
 */
public class UI {

    public final static String BUTTON = "android.widget.Button";
    public final static String EDITTEXT = "android.widget.EditText";
    public final static String TEXTVIEW = "android.widget.TextView";
    public final static String IMAGEVIEW = "android.widget.ImageView";
    public final static String IMAGEBUTTON = "android.widget.ImageButton";
    public final static String LinearLayout = "android.widget.LinearLayout";
    public final static String RelativeLayout = "android.widget.RelativeLayout";

    public static String WX_VERSION = "6.5.10";

    //这是6.6.6 (04/10)版本的ID.微信会动态改控件ID和页面className
    public static String MINE_WX_TEXTVIEW_ID = "com.tencent.mm:id/cby"; //"我"页面里微信号的textview,之前是com.tencent.mm:id/cdn
    public static String OPEN_LUCKY_MONEY_BUTTON_ID = "com.tencent.mm:id/c31";//红包“開”按钮  之前是com.tencent.mm:id/c4q
    public static String LUCKY_MONEY_AMOUNT_TEXTVIEW_ID = "com.tencent.mm:id/bzd";//红包金额  之前是com.tencent.mm:id/c13
    public static String OPEN_WIRE_TRANSFER_BUTTON_ID = "com.tencent.mm:id/cwk";//“确认收款”按钮,之前是com.tencent.mm:id/cy8
    public static String WIRE_TRANSFER_AMOUNT_TEXTVIEW_ID = "com.tencent.mm:id/cwj";//转账金额  之前是com.tencent.mm:id/cy7
    public static String SEARCH_RESULT_CONTACTS_TEXTVIEW_ID = "com.tencent.mm:id/l7";//显示联系人搜索结果的控件ID  //
    public static String CHATUI_BACK_IMAGEVIEW_ID = "com.tencent.mm:id/hm";//聊天页返回按钮的控件ID  之前是com.tencent.mm:id/hi
    public static String SEARCH_BACK_IMAGEVIEW_ID = "com.tencent.mm:id/hu";//搜索页返回按钮的控件ID  之前是com.tencent.mm:id/hq
    public static String LUCKY_MONEY_DETAIL_BACK_IMAGEVIEW_ID = "com.tencent.mm:id/hy";//红包详情/交易详情/聊天信息页返回按钮的控件ID
    public static String SEARCH_CHAT_INFO_BACK_IMAGEVIEW_ID = "com.tencent.mm:id/hu";//搜索聊天记录页面返回按钮的控件ID 之前是com.tencent.mm:id/hq
    public static String WECHAT_HOME_LINEARLAYOUT_ID = "com.tencent.mm:id/ca3";//微信首页"微信"的控件ID

    public static String CHATUI_PERSON_IMAGEBUTTON_ID = "com.tencent.mm:id/hi";  //聊天页面右上角人头按钮
    public static String CHATMSG_AVATAR_IMAGEVIEW_ID = "com.tencent.mm:id/cz1";  //聊天信息页面左上头像
    public static String DETAIL_NICKNAME_TEXTVIEW_ID = "com.tencent.mm:id/q0";  //详细资料页面的昵称
    public static String DETAIL_WXNO_TEXTVIEW_ID = "com.tencent.mm:id/ang";  //详细资料页面的微信号

    public final static String CHATTING_UI = "ChattingUI"; //聊天页面
    public final static String SEARCH_UI = "FTSMainUI"; //搜索页面
    public final static String CHAT_INFO_UI = "SingleChatInfoUI"; //聊天信息页面
    public final static String SEARCH_CHAT_INFO_UI = "FTSChattingConvUI"; //搜索聊天记录页面  //之前是com.tencent.mm.plugin.search.ui.FTSChattingConvUI
    public final static String LUCKY_MONEY_RECEIVE_UI = "LuckyMoneyReceiveUI"; //打开红包页面
    public final static String LUCKY_MONEY_DETAIL_UI = "LuckyMoneyDetailUI"; //红包详情页面
    public final static String TRANSFER_DETAIL_UI = "RemittanceDetailUI"; //红包详情页面

    public static void initUIVersion(String version) {
        WX_VERSION = version;
        switch (version) {
            case "6.5.10":
                MINE_WX_TEXTVIEW_ID = "com.tencent.mm:id/bzs"; //"我"页面里微信号的textview
                OPEN_LUCKY_MONEY_BUTTON_ID = "com.tencent.mm:id/c31";//红包“開”按钮  之前是com.tencent.mm:id/c4q
                LUCKY_MONEY_AMOUNT_TEXTVIEW_ID = "com.tencent.mm:id/bzd";//红包金额  之前是com.tencent.mm:id/c13
                OPEN_WIRE_TRANSFER_BUTTON_ID = "com.tencent.mm:id/cwk";//“确认收款”按钮,之前是com.tencent.mm:id/cy8
                WIRE_TRANSFER_AMOUNT_TEXTVIEW_ID = "com.tencent.mm:id/cwj";//转账金额  之前是com.tencent.mm:id/cy7
                SEARCH_RESULT_CONTACTS_TEXTVIEW_ID = "com.tencent.mm:id/kr";//显示联系人搜索结果的控件ID  //
                CHATUI_BACK_IMAGEVIEW_ID = "com.tencent.mm:id/hm";//聊天页返回按钮的控件ID  之前是com.tencent.mm:id/hi
                SEARCH_BACK_IMAGEVIEW_ID = "com.tencent.mm:id/hu";//搜索页返回按钮的控件ID  之前是com.tencent.mm:id/hq
                LUCKY_MONEY_DETAIL_BACK_IMAGEVIEW_ID = "com.tencent.mm:id/hy";//红包详情/交易详情/聊天信息页返回按钮的控件ID
                SEARCH_CHAT_INFO_BACK_IMAGEVIEW_ID = "com.tencent.mm:id/hu";//搜索聊天记录页面返回按钮的控件ID 之前是com.tencent.mm:id/hq
                break;
            case "6.6.6":
                MINE_WX_TEXTVIEW_ID = "com.tencent.mm:id/cby"; //"我"页面里微信号的textview,之前是com.tencent.mm:id/cdn
                OPEN_LUCKY_MONEY_BUTTON_ID = "com.tencent.mm:id/c31";//红包“開”按钮  之前是com.tencent.mm:id/c4q
                LUCKY_MONEY_AMOUNT_TEXTVIEW_ID = "com.tencent.mm:id/bzd";//红包金额  之前是com.tencent.mm:id/c13
                OPEN_WIRE_TRANSFER_BUTTON_ID = "com.tencent.mm:id/cwk";//“确认收款”按钮,之前是com.tencent.mm:id/cy8
                WIRE_TRANSFER_AMOUNT_TEXTVIEW_ID = "com.tencent.mm:id/cwj";//转账金额  之前是com.tencent.mm:id/cy7
                SEARCH_RESULT_CONTACTS_TEXTVIEW_ID = "com.tencent.mm:id/kr";//显示联系人搜索结果的控件ID  //
                CHATUI_BACK_IMAGEVIEW_ID = "com.tencent.mm:id/hm";//聊天页返回按钮的控件ID  之前是com.tencent.mm:id/hi
                SEARCH_BACK_IMAGEVIEW_ID = "com.tencent.mm:id/hu";//搜索页返回按钮的控件ID  之前是com.tencent.mm:id/hq
                LUCKY_MONEY_DETAIL_BACK_IMAGEVIEW_ID = "com.tencent.mm:id/hy";//红包详情/交易详情/聊天信息页返回按钮的控件ID
                SEARCH_CHAT_INFO_BACK_IMAGEVIEW_ID = "com.tencent.mm:id/hu";//搜索聊天记录页面返回按钮的控件ID 之前是com.tencent.mm:id/hq

                CHATUI_PERSON_IMAGEBUTTON_ID = "com.tencent.mm:id/hi";  //聊天页面右上角人头按钮
                break;
        }
    }
}
