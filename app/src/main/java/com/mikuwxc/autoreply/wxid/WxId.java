package com.mikuwxc.autoreply.wxid;

/**
 * Created by Administrator on 2017/2/14.
 */
public class WxId {

    //微信6.3.30相关组件的id，微信版本更新后随之修改即可
    public static String jiahaoId = "com.tencent.mm:id/dq";

    public static String titleBarId = "com.tencent.mm:id/ec";//getChild(1).getChild(0)
    public static String addressBookLastId = "com.tencent.mm:id/a8m";//通讯录最后一行，”几位联系人“
    public static String friendId = "com.tencent.mm:id/gz";//通讯录的listview的Id
    public static String currUserNick = "com.tencent.mm:id/bs6";//当前用户的微信昵称
    public static String currUserNum = "com.tencent.mm:id/bs7";//当前用户的微信号
    public static String permissionInfo = "com.tencent.mm:id/aoz";//加好友打招呼的验证消息
    public static String agreeFriend = "com.tencent.mm:id/a8p";//同意添加好友
    public static String editFriendInfo = "com.tencent.mm:id/a9e";//修改备注名
    public static String editFriendName = "com.tencent.mm:id/a_r";//备注名
    public static String editFriendPhone = "com.tencent.mm:id/bub";//备注电话号码
    public static String editFriendInfoFinish = "com.tencent.mm:id/eu";//修改备注完成
    public static String editMessageId = "com.tencent.mm:id/z4";
    public static String searchFriendId = "com.tencent.mm:id/bnc";//好友搜索框id
    public static String friendLineId = "com.tencent.mm:id/bmc";//好友栏id
    public static String groupNameId = "android:id/summary";//更改群命名id
    public static String groupInfoListId = "android:id/list";//群命名id
    public static String etgroupNameId = "com.tencent.mm:id/bm5";//群命名id
    public static String sendGroupId = "com.tencent.mm:id/bif";//转发群Id

    /**
     * 添加好友操作
     */
    public static String newfriendId = "com.tencent.mm:id/asb";//新的朋友Id
    public static String searchListId = "com.tencent.mm:id/at2";//微信号/QQ号/手机号 Id
    public static String searchEtId = "com.tencent.mm:id/g_";//搜索用户编辑框
    public static String searchfriendId = "com.tencent.mm:id/av7";//搜索输入内容的按钮
    public static String addfriendId = "com.tencent.mm:id/abv";//添加到通讯录
    //    public static String permissionEtId = "com.tencent.mm:id/c8w";//验证消息编辑框
//    public static String sendPermissionId = "com.tencent.mm:id/fx";//发送加好友申请
    public static String addFriendRightTop = "com.tencent.mm:id/fw";//右上角的添加好友按钮Id
    public static String clearEditId = "com.tencent.mm:id/ga";//输入微信号/QQ号/手机号框的清空按钮
    public static String noUserId = "com.tencent.mm:id/auz";//该用户不存在
    public static String phoneAddressBook = "com.tencent.mm:id/a9q";//手机通讯录

    /**
     * 发朋友圈需要操作的控件
     */
    public static String friendCircleEditCompleted = "com.tencent.mm:id/fx";//右上角发送按钮
    public static String friendCircleComment = "com.tencent.mm:id/chi";//朋友圈右下角评论按钮
    public static String friendCircleComment1 = "com.tencent.mm:id/chh";//点击评论后弹出的评论按钮
    public static String friendCirclePraise = "com.tencent.mm:id/chc";//点击评论后弹出的点赞按钮
    public static String friendCircleCommentEditId = "com.tencent.mm:id/chw";//评论编辑框Id
    public static String friendCircleCommentSendId = "com.tencent.mm:id/chy";//评论发送ID
    public static String friendCircleListId = "com.tencent.mm:id/ci2";//朋友圈listviewId
    public static String friendCircleLocation = "com.tencent.mm:id/cl5";//所在位置
    public static String friendCricleLocationSearch = "com.tencent.mm:id/ap";//位置搜索右上角搜索按钮
    public static String locationItem = "com.tencent.mm:id/btd";
    public static String searchLocationEditId = "com.tencent.mm:id/g_";//输入位置编辑框
    public static String searchId = "com.tencent.mm:id/gb";//搜索  按钮
    public static String locationList = "com.tencent.mm:id/btt";//搜索到的地址的ListView Id
    public static String createNewLocation = "com.tencent.mm:id/aem";//没有找到你的位置？
    public static String createLocationDetail = "com.tencent.mm:id/bth";//详细地址
    public static String createNewLocationComplete = "com.tencent.mm:id/fx";//创建新位置  完成  按钮
    public static String progressFriendCircle = "com.tencent.mm:id/cmd";//彩色进度条加载

    public static String friendCircleEdit="com.tencent.mm:id/ddw";//发表朋友圈时内容编辑框
    /**
     * 创建群聊
     */
    public static String createGroupChatConfirm = "com.tencent.mm:id/hh"; //"发起群聊"页面右上角的"确定"按钮

    /**
     * 获取通讯录列表的控件
     */
    public static String addressBookListId = "com.tencent.mm:id/hn";//通讯录的listview的Id
    public static String wechatNum = "com.tencent.mm:id/abn";//微信号
    public static String wechatNick = "com.tencent.mm:id/abx";//微信昵称
    public static String wechatRemark = "com.tencent.mm:id/l5";//备注
    public static String wechatPhone = "com.tencent.mm:id/ac4";//手机号 FrameLayout


    /**
     * 自动回复的相关控件
     */
    public static String chatListId = "com.tencent.mm:id/big";//聊天联系人列表 listview
    public static String chatUserItemId = "com.tencent.mm:id/aeb";//每个联系人的item的id
    public static String chatContentListId = "com.tencent.mm:id/a25";//聊天页面 聊天内容 listview
    public static String chatContentTextId = "com.tencent.mm:id/ib";//聊天页面 文本信息的 item 的id
    public static String qunId = "com.tencent.mm:id/g1";//当前左上角聊天对象名称 id
    public static String editId = "com.tencent.mm:id/a27";//聊天页面 输入框id
    public static String sendId = "com.tencent.mm:id/a2c";//发送按钮 id
    public static String gongzhonghaoId = "com.tencent.mm:id/o";//公众号的消息 item
    public static String tengxunxinwenId = "android:id/text1";//腾讯新闻的消息 item

    /**
     * 互聊功能的相关控件
     */
    public static String huliao_searchId = "com.tencent.mm:id/gn";//搜索框Id
    public static String huliao_searchItemId = "com.tencent.mm:id/auh";
    public static String huliao_searchIv = "com.tencent.mm:id/av1";//查找手机/QQ号/微信号
    public static String huliao_sendMessage = "com.tencent.mm:id/abh";//发消息Id
    public static String huliao_noUser = "com.tencent.mm:id/blw";//用户不存在
    public static String huliao_addFriend = "com.tencent.mm:id/abg";//添加到通讯录

    public static String permissionEtId = "com.tencent.mm:id/c8w";//验证消息编辑框
    public static String sendPermissionId = "com.tencent.mm:id/fx";//发送加好友申请

    /**
     * 通过好友验证
     */
    public static String receiveFriend = "com.tencent.mm:id/aso";//接受
    public static String permissionTv = "com.tencent.mm:id/c9v";
    public static String completeAdd = "com.tencent.mm:id/gd";//完成
    public static String communicateId = "com.tencent.mm:id/abw";//同意加好友后打招呼按钮id

    public final static String CHATTING_UI = "ChattingUI"; //聊天页面
    public final static String SEARCH_UI = "FTSMainUI"; //搜索页面
    public final static String CHAT_INFO_UI = "SingleChatInfoUI"; //聊天信息页面
    public final static String SEARCH_CHAT_INFO_UI = "FTSChattingConvUI"; //搜索聊天记录页面  //之前是com.tencent.mm.plugin.search.ui.FTSChattingConvUI
    public final static String LUCKY_MONEY_RECEIVE_UI = "LuckyMoneyReceiveUI"; //打开红包页面
    public final static String LUCKY_MONEY_DETAIL_UI = "LuckyMoneyDetailUI"; //红包详情页面
    public final static String TRANSFER_DETAIL_UI = "RemittanceDetailUI"; //红包详情页面
}
