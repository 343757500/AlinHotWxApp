package com.mikuwxc.autoreply.wcentity;


import com.mikuwxc.autoreply.wcutil.OtherUtils;

public class MemberEntity {
    private String headPic = "";
    private String nickName = "";
    private int opType;
    private int status;
    private String wechatId = "";

    public String getHeadPic() {
        return OtherUtils.isEmpty(this.headPic) ? "" : this.headPic;
    }

    public String getNickName() {
        return OtherUtils.isEmpty(this.nickName) ? "" : this.nickName;
    }

    public int getOpType() {
        return this.opType;
    }

    public int getStatus() {
        return this.status;
    }

    public String getWechatId() {
        return OtherUtils.isEmpty(this.wechatId) ? "" : this.wechatId;
    }

    public void setHeadPic(String str) {
        this.headPic = str;
    }

    public void setNickName(String str) {
        this.nickName = str;
    }

    public void setOpType(int i) {
        this.opType = i;
    }

    public void setStatus(int i) {
        this.status = i;
    }

    public void setWechatId(String str) {
        this.wechatId = str;
    }

    public String toString() {
        return "[wechatId=" + this.wechatId + ", nickName=" + this.nickName + ", headPic=" + this.headPic + ", status=" + this.status + "]";
    }
}