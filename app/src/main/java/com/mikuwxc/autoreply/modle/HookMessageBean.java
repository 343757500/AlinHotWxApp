package com.mikuwxc.autoreply.modle;

/**
 * Date: 2018/4/28 14:32.
 * Desc:
 */
public class HookMessageBean {
    private String token;
    private int status;//1:自己正在发送的;2:自己已发送成功的,3:对方发来的
    private String username;
    private String content;
    private String msgType;//1:文字; 3:图片 34:语音
    private long conversationTime;//13位数的代表成功收到的时间,19位的是开始发送的时间,只有status为1的时候会有
    private String msgId;

    public HookMessageBean() {

    }

    public HookMessageBean(int status, String username, String content, String msgType, long conversationTime,String msgId) {
        this.status = status;
        this.username = username;
        this.content = content;
        this.msgType = msgType;
        this.conversationTime = conversationTime;
        this.msgId=msgId;
    }

    public HookMessageBean(String token, int status, String username, String content, String msgType, long conversationTime,String msgId) {
        this.token = token;
        this.status = status;
        this.username = username;
        this.content = content;
        this.msgType = msgType;
        this.conversationTime = conversationTime;
        this.msgId=msgId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public long getConversationTime() {
        return conversationTime;
    }

    public void setConversationTime(long conversationTime) {
        this.conversationTime = conversationTime;
    }
}
