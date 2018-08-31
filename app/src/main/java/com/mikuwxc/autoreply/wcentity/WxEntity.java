package com.mikuwxc.autoreply.wcentity;


import com.mikuwxc.autoreply.wcutil.OtherUtils;

public class WxEntity {
    private int addFrom = 0;
    private String alias = "";
    private String conRemark = "";
    private String entryptUsername = "";
    private int gender = 0;
    private String headPic = "";
    private int isForceUpdate = 0;
    private String labelIds = "";
    private String labelNames;
    private String nickName = "";
    private int opType;
    private String phone = "";
    private String pyInitial = "";
    private String quanPin = "";
    private String region = "";
    private String selfWxId = "";
    private String userName = "";

    public int getAddFrom() {
        return this.addFrom;
    }

    public String getAlias() {
        return OtherUtils.isEmpty(this.alias) ? "" : this.alias;
    }

    public String getConRemark() {
        return OtherUtils.isEmpty(this.conRemark) ? "" : this.conRemark;
    }

    public String getEntryptUsername() {
        return OtherUtils.isEmpty(this.entryptUsername) ? "" : this.entryptUsername;
    }

    public int getGender() {
        return this.gender;
    }

    public String getHeadPic() {
        return OtherUtils.isEmpty(this.headPic) ? "" : this.headPic;
    }

    public int getIsForceUpdate() {
        return this.isForceUpdate;
    }

    public String getLabelIds() {
        return this.labelIds;
    }

    public String getLabelNames() {
        return this.labelNames;
    }

    public String getNickName() {
        return OtherUtils.isEmpty(this.nickName) ? "" : this.nickName;
    }

    public int getOpType() {
        return this.opType;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getPyInitial() {
        return OtherUtils.isEmpty(this.pyInitial) ? "" : this.pyInitial;
    }

    public String getQuanPin() {
        return OtherUtils.isEmpty(this.quanPin) ? "" : this.quanPin;
    }

    public String getRegion() {
        return OtherUtils.isEmpty(this.region) ? "" : this.region;
    }

    public String getSelfWxId() {
        return OtherUtils.isEmpty(this.selfWxId) ? "" : this.selfWxId;
    }

    public String getUserName() {
        return OtherUtils.isEmpty(this.userName) ? "" : this.userName;
    }

    public void setAddFrom(int i) {
        this.addFrom = i;
    }

    public void setAlias(String str) {
        this.alias = str;
    }

    public void setConRemark(String str) {
        this.conRemark = str;
    }

    public void setEntryptUsername(String str) {
        this.entryptUsername = str;
    }

    public void setGender(int i) {
        this.gender = i;
    }

    public void setHeadPic(String str) {
        this.headPic = str;
    }

    public void setIsForceUpdate(int i) {
        this.isForceUpdate = i;
    }

    public void setLabelIds(String str) {
        this.labelIds = str;
    }

    public void setLabelNames(String str) {
        this.labelNames = str;
    }

    public void setNickName(String str) {
        this.nickName = str;
    }

    public void setOpType(int i) {
        this.opType = i;
    }

    public void setPhone(String str) {
        this.phone = str;
    }

    public void setPyInitial(String str) {
        this.pyInitial = str;
    }

    public void setQuanPin(String str) {
        this.quanPin = str;
    }

    public void setRegion(String str) {
        this.region = str;
    }

    public void setSelfWxId(String str) {
        this.selfWxId = str;
    }

    public void setUserName(String str) {
        this.userName = str;
    }

    public String toString() {
        return "WxEntity{opType=" + this.opType + ", selfWxId='" + this.selfWxId + '\'' + ", userName='" + this.userName + '\'' + ", alias='" + this.alias + '\'' + ", conRemark='" + this.conRemark + '\'' + ", nickName='" + this.nickName + '\'' + ", pyInitial='" + this.pyInitial + '\'' + ", quanPin='" + this.quanPin + '\'' + ", headPic='" + this.headPic + '\'' + ", gender=" + this.gender + ", region='" + this.region + '\'' + ", entryptUsername='" + this.entryptUsername + '\'' + ", addFrom=" + this.addFrom + ", labelIds='" + this.labelIds + '\'' + ", labelNames='" + this.labelNames + '\'' + ", phone='" + this.phone + '\'' + ", isForceUpdate=" + this.isForceUpdate + '}';
    }
}