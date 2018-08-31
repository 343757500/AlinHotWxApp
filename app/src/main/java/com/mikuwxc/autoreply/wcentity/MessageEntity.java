package com.mikuwxc.autoreply.wcentity;


import com.mikuwxc.autoreply.wcutil.OtherUtils;

public class MessageEntity {
    private String content;
    private long createTime = 0;
    private String customMsgId;
    private long duration = 0;
    private long fileSize = 0;
    private String imgPath;
    private boolean isSend = false;
    private long msgId;
    private String msgSvrId;
    private String self;
    private String talker;
    private int type = 0;

    public String getContent() {
        return OtherUtils.isEmpty(this.content) ? "" : this.content;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public String getCustomMsgId() {
        return this.customMsgId;
    }

    public long getDuration() {
        return this.duration;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public String getImgPath() {
        return OtherUtils.isEmpty(this.imgPath) ? "" : this.imgPath;
    }

    public long getMsgId() {
        return this.msgId;
    }

    public String getMsgSvrId() {
        return OtherUtils.isEmpty(this.msgSvrId) ? "" : this.msgSvrId;
    }

    public String getSelf() {
        return OtherUtils.isEmpty(this.self) ? "" : this.self;
    }

    public String getTalker() {
        return OtherUtils.isEmpty(this.talker) ? "" : this.talker;
    }

    public int getType() {
        return this.type;
    }

    public boolean isSend() {
        return this.isSend;
    }

    public void setContent(String str) {
        this.content = str;
    }

    public void setCreateTime(long j) {
        this.createTime = j;
    }

    public void setCustomMsgId(String str) {
        this.customMsgId = str;
    }

    public void setDuration(long j) {
        this.duration = j;
    }

    public void setFileSize(long j) {
        this.fileSize = j;
    }

    public void setImgPath(String str) {
        this.imgPath = str;
    }

    public void setMsgId(long j) {
        this.msgId = j;
    }

    public void setMsgSvrId(String str) {
        this.msgSvrId = str;
    }

    public void setSelf(String str) {
        this.self = str;
    }

    public void setSend(boolean z) {
        this.isSend = z;
    }

    public void setTalker(String str) {
        this.talker = str;
    }

    public void setType(int i) {
        this.type = i;
    }
}