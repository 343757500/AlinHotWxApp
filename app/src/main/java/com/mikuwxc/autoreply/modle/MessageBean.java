package com.mikuwxc.autoreply.modle;

/**
 * Package_name:com.tencent.mobileqq.modle
 * Author:CYX
 * Email:android_cyx@163.com
 * Date:2017/03/13  19:01
 * 消息  实体类
 */

public class MessageBean {
    /**
     * remarkname : 8x6b0zjgl9ifw152xo4ttl6yw
     * nickname : CYxiong
     * headImgUrl : /cgi-bin/mmwebwx-bin/webwxgeticon?seq=674714292&username=@e23622d7ca348644070fffea16de549a&skey=@crypt_c2d5fc5e_437f142a5f0dd9d276cb7e338b16ace7
     * text : 你好吗
     * type : 0
     * createTime : 1521084106892
     */
    private String remarkname;   //备注名
    private String nickname;     //昵称
    private String headImgUrl;   //头像
    private int type;            //消息类型(0:文本; 1:图片; 2:语音; 3:红包; 4:表情; 5:系统; 6.转账)
    private String text;         //消息内容(当type为1/2时,text为对应的文件URL)
    private long createTime;     //发送时间

    public String getRemarkname() {
        return remarkname;
    }

    public void setRemarkname(String remarkname) {
        this.remarkname = remarkname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


    @Override
    public String toString() {
        return "消息详情{" +
                "备注='" + remarkname + '\'' +
                ", 昵称='" + nickname + '\'' +
                ", 头像='" + headImgUrl + '\'' +
                ", 类型=" + type +
                ", 消息='" + text + '\'' +
                ", 创建时间=" + createTime +
                '}';
    }
}
