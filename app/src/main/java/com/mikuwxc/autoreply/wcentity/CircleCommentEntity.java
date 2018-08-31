package com.mikuwxc.autoreply.wcentity;

public class CircleCommentEntity {
    private int commentArg;
    private int commentId1;
    private int commentId2;
    private int commentTime;
    private String content;
    private String nickName;
    private String otherWechatId;
    private String wechatId;

    public int getCommentArg() {
        return this.commentArg;
    }

    public int getCommentId1() {
        return this.commentId1;
    }

    public int getCommentId2() {
        return this.commentId2;
    }

    public int getCommentTime() {
        return this.commentTime;
    }

    public String getContent() {
        return this.content;
    }

    public String getNickName() {
        return this.nickName;
    }

    public String getOtherWechatId() {
        return this.otherWechatId;
    }

    public String getWechatId() {
        return this.wechatId;
    }

    public void setCommentArg(int i) {
        this.commentArg = i;
    }

    public void setCommentId1(int i) {
        this.commentId1 = i;
    }

    public void setCommentId2(int i) {
        this.commentId2 = i;
    }

    public void setCommentTime(int i) {
        this.commentTime = i;
    }

    public void setContent(String str) {
        this.content = str;
    }

    public void setNickName(String str) {
        this.nickName = str;
    }

    public void setOtherWechatId(String str) {
        this.otherWechatId = str;
    }

    public void setWechatId(String str) {
        this.wechatId = str;
    }
}