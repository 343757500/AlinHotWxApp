package com.mikuwxc.autoreply.bean;

public class DeleteFriendBean {
    private String wxno;   //所属微信号
    private String friendWxid;  //好友wxid
    private String conversationTime;  //删除时间

    public String getWxno() {
        return wxno;
    }

    public void setWxno(String wxno) {
        this.wxno = wxno;
    }

    public String getFriendWxid() {
        return friendWxid;
    }

    public void setFriendWxid(String friendWxid) {
        this.friendWxid = friendWxid;
    }

    public String getConversationTime() {
        return conversationTime;
    }

    public void setConversationTime(String conversationTime) {
        this.conversationTime = conversationTime;
    }
}
