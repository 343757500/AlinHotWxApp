package com.mikuwxc.autoreply.wcentity;


import com.mikuwxc.autoreply.wcutil.OtherUtils;

import java.util.ArrayList;
import java.util.List;

public class ChatroomEntity {
    private String chatroomHeadPic = "";
    private String chatroomId = "";
    private String chatroomOwner = "";
    private String conRemark = "";
    private List<MemberEntity> members = new ArrayList();
    private String nickName = "";
    private int opType = 0;
    private String pyInitial = "";
    private String quanPin = "";

    public String getChatroomHeadPic() {
        return OtherUtils.isEmpty(this.chatroomHeadPic) ? "" : this.chatroomHeadPic;
    }

    public String getChatroomId() {
        return OtherUtils.isEmpty(this.chatroomId) ? "" : this.chatroomId;
    }

    public String getChatroomOwner() {
        return OtherUtils.isEmpty(this.chatroomOwner) ? "" : this.chatroomOwner;
    }

    public String getConRemark() {
        return OtherUtils.isEmpty(this.conRemark) ? "" : this.conRemark;
    }

    public List<MemberEntity> getMembers() {
        return this.members;
    }

    public String getNickName() {
        return OtherUtils.isEmpty(this.nickName) ? "" : this.nickName;
    }

    public int getOpType() {
        return this.opType;
    }

    public String getPyInitial() {
        return OtherUtils.isEmpty(this.pyInitial) ? "" : this.pyInitial;
    }

    public String getQuanPin() {
        return OtherUtils.isEmpty(this.quanPin) ? "" : this.quanPin;
    }

    public void setChatroomHeadPic(String str) {
        this.chatroomHeadPic = str;
    }

    public void setChatroomId(String str) {
        this.chatroomId = str;
    }

    public void setChatroomOwner(String str) {
        this.chatroomOwner = str;
    }

    public void setConRemark(String str) {
        this.conRemark = str;
    }

    public void setMembers(List<MemberEntity> list) {
        this.members = list;
    }

    public void setNickName(String str) {
        this.nickName = str;
    }

    public void setOpType(int i) {
        this.opType = i;
    }

    public void setPyInitial(String str) {
        this.pyInitial = str;
    }

    public void setQuanPin(String str) {
        this.quanPin = str;
    }

    public String toString() {
        return "[chatroomId=" + this.chatroomId + ", members=" + this.members + ", chatroomOwner=" + this.chatroomOwner + ", conRemark=" + this.conRemark + ", nickName=" + this.nickName + ", pyInitial=" + this.pyInitial + ", quanPin=" + this.quanPin + ", chatroomHeadPic=" + this.chatroomHeadPic + "]";
    }
}