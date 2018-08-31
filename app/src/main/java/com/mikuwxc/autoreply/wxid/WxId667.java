package com.mikuwxc.autoreply.wxid;

/**
 * Date: 2018/5/22 11:07.
 * Desc: 由于之前的注释与命名太不规范,所以不保证该版本的id与之前的是同一个控件或同一类型.by Temper
 */
public class WxId667 extends WxId {
    public static void init() {
        /**
         * 添加好友操作
         */
        newfriendId = "com.tencent.mm:id/b5t";//通讯录页面,"新的朋友"Id,类型linearLayout
        searchListId = "com.tencent.mm:id/c9y";//"添加朋友"页面的"微信号/QQ号/手机号" Id,类型textview
        searchEtId = "com.tencent.mm:id/hx";//点击"微信号/QQ号/手机号"之后跳转到的搜索页的搜索用户编辑框,类型edittext
        searchfriendId = "com.tencent.mm:id/l4";//搜索页输入内容后的搜索item,包含绿色头像+,类型LinearLayout
        addfriendId = "com.tencent.mm:id/an_";//新好友的"详细资料"页面的"添加到通讯录", 类型button
        permissionEtId = "com.tencent.mm:id/cz9";//"验证申请"页面的"验证消息编辑框",类型edittext
        sendPermissionId = "com.tencent.mm:id/hh";//"验证申请"页面的"发送"加好友申请,类型textview
        addFriendRightTop = "com.tencent.mm:id/gc";//右上角的添加好友按钮Id----------没用途,不知道哪里的控件
        clearEditId = "com.tencent.mm:id/bap";//点击"微信号/QQ号/手机号"之后跳转到的搜索页,输入微信号/QQ号/手机号框的清空按钮,类型imageButton
        noUserId = "com.tencent.mm:id/ayf";//该用户不存在
        phoneAddressBook = "com.tencent.mm:id/a_c";//手机通讯录

        /**
         * 发朋友圈需要操作的控件
         */
        friendCircleEditCompleted = "com.tencent.mm:id/hh";//"朋友圈编辑页"右上角"发表"按钮,类型textview
        friendCircleComment = "com.tencent.mm:id/dao";//"朋友圈"item右下角评论按钮,类型imageview
        friendCircleComment1 = "com.tencent.mm:id/d_r";//"朋友圈"item,点击评论后弹出的"评论"按钮,类型textview
        friendCirclePraise = "com.tencent.mm:id/d_o";//"朋友圈"item,点击评论后弹出的点"赞"按钮,类型textview
        friendCircleCommentEditId = "com.tencent.mm:id/dax";//"朋友圈"item,点"评论"后出现的评论编辑框Id,类型edittext
        friendCircleCommentSendId = "com.tencent.mm:id/db1";//朋友圈"item,评论"发送"按钮ID,类型button
        friendCircleListId = "com.tencent.mm:id/ddn";//"朋友圈"的listview Id, 类型listview
        friendCircleLocation = "com.tencent.mm:id/bw8";//"朋友圈编辑页"的"所在位置" id,类型textview
        friendCricleLocationSearch = "com.tencent.mm:id/bc";//"所在位置"页面的右上角搜索按钮id,类型textview
        locationItem = "com.tencent.mm:id/cdq";//"所在位置"页面的地址ITEM Id,类型textview
        searchLocationEditId = "com.tencent.mm:id/hx";//点了搜索按钮进入搜索页面,输入位置的编辑框id,类型edittext
        searchId = "com.tencent.mm:id/gt";//搜索  按钮
        locationList = "com.tencent.mm:id/cdo";//搜索到的地址的ListView item Id,类型linearLayout
        createNewLocation = "com.tencent.mm:id/ag5";//没有找到你的位置？
        createLocationDetail = "com.tencent.mm:id/bw9";//详细地址
        createNewLocationComplete = "com.tencent.mm:id/gd";//创建新位置  完成  按钮
        progressFriendCircle = "com.tencent.mm:id/dfb";//"朋友圈"页面,发送完或下拉刷新会出现的彩色加载进度圈,类型View

        /**
         * 创建群聊
         */
        createGroupChatConfirm = "com.tencent.mm:id/hh"; //"发起群聊"页面右上角的"确定"按钮
        friendLineId = "com.tencent.mm:id/cc6";

        /**
         * 获取通讯录列表的控件
         */
        addressBookListId = "com.tencent.mm:id/jq";//"通讯录"联系人listview的item(不包括上面4个item)的Id,类型view
        wechatNum = "com.tencent.mm:id/ang";//"详细资料"页面的微信号,类型textview
        wechatNick = "com.tencent.mm:id/ant";//"详细资料"页面的微信昵称,类型textview
        wechatRemark = "com.tencent.mm:id/q0";//"详细资料"页面的备注,类型textview
        wechatPhone = "com.tencent.mm:id/ae6";//手机号 FrameLayout ----没用途的

        /**
         * 自动回复的相关控件
         */
        chatListId = "com.tencent.mm:id/big";//聊天联系人列表 listview ---- 没用途的
        chatUserItemId = "com.tencent.mm:id/apt";//每个联系人的item的id (首页首个联系人item的id.),类型linearLayout
        chatContentListId = "com.tencent.mm:id/c48";//聊天页面 聊天内容 类型listview
        chatContentTextId = "com.tencent.mm:id/jz";//聊天页面 文本信息的 item 的id,全部文本信息都是这个id,类型view
        qunId = "com.tencent.mm:id/hn";//"聊天页面"左上角聊天对象名称 id,类型textview
        editId = "com.tencent.mm:id/aaa";//"聊天页面" 输入框id,类型 edittext
        sendId = "com.tencent.mm:id/aag";//"聊天页面"发送按钮 id 类型button
        gongzhonghaoId = "com.tencent.mm:id/o";//公众号的消息 item---暂时没用途
        tengxunxinwenId = "android:id/text1";//腾讯新闻的消息 item

        /**
         * 互聊功能的相关控件
         */
        huliao_searchId = "com.tencent.mm:id/hx";//首页右上角搜索按钮跳去的搜索页面, 搜索框Id, 类型edittext
        huliao_searchItemId = "com.tencent.mm:id/l4";//搜索页输入内容后的搜索item,包含绿色头像+,类型LinearLayout
        huliao_searchIv = "com.tencent.mm:id/bay";//搜索页面输入手机后,"查找手机/微信号"的item, 类型RelativeLayout
        huliao_addFriend = "com.tencent.mm:id/an_";//新好友的"详细资料"页面的"添加到通讯录", 类型button
        huliao_sendMessage = "com.tencent.mm:id/ana";//已有好友的"详细资料"页面的"发消息"Id,类型button
        huliao_noUser = "com.tencent.mm:id/c92";//搜索好友时出的弹窗,"用户不存在" id, 类型textview

        permissionEtId = "com.tencent.mm:id/cz9";//"验证申请"页面的"验证消息编辑框",类型edittext
        sendPermissionId = "com.tencent.mm:id/hh";//"验证申请"页面的"发送"加好友申请,类型textview
        /**
         * 通过好友验证
         */
        receiveFriend = "com.tencent.mm:id/b8p";//"新的朋友"页面,收到新的朋友验证,绿色的"接受"按钮,类型button
        permissionTv = "com.tencent.mm:id/cze";//"朋友验证"页面,"对方发来的验证消息为“我是Temper 司徒” 填入",类型textview
        completeAdd = "com.tencent.mm:id/hh";//"朋友验证"页面右上角的绿色"完成"按钮,类型textview
        communicateId = "com.tencent.mm:id/ana";//"详细资料"页面,同意加好友后打招呼按钮id   '发消息', 类型button
    }
}
