package com.mikuwxc.autoreply.modle;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class MessageFailBean {
    @Id(autoincrement = true)
    private Long id;
    private String token;
    private int status;//1:自己正在发送的;2:自己已发送成功的,3:对方发来的
    private String username;
    private String content;
    private String msgType;//1:文字; 3:图片 34:语音
    private long conversationTime;//13位数的代表成功收到的时间,19位的是开始发送的时间,只有status为1的时候会有
    @Generated(hash = 401664738)
    public MessageFailBean(Long id, String token, int status, String username,
            String content, String msgType, long conversationTime) {
        this.id = id;
        this.token = token;
        this.status = status;
        this.username = username;
        this.content = content;
        this.msgType = msgType;
        this.conversationTime = conversationTime;
    }
    @Generated(hash = 1515198563)
    public MessageFailBean() {
    }
    public String getToken() {
        return this.token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public int getStatus() {
        return this.status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getMsgType() {
        return this.msgType;
    }
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
    public long getConversationTime() {
        return this.conversationTime;
    }
    public void setConversationTime(long conversationTime) {
        this.conversationTime = conversationTime;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }


}
