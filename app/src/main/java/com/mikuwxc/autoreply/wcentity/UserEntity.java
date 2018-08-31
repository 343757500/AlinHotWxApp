package com.mikuwxc.autoreply.wcentity;


import com.google.gson.annotations.SerializedName;
import com.mikuwxc.autoreply.wcutil.OtherUtils;

public class UserEntity {
    @SerializedName("wxno")
    private String alias = "";
    private String bindEmail = "";
    private String bindMobile = "";
    private String bindQQ = "";
    private int gender = 0;
    @SerializedName("headImgUrl")
    private String headPic = "";
    private String region = "";
    private String signature = "";
    private String userName = "";
    @SerializedName("wxid")
    private String userTalker = "";

    public String getAlias() {
        return OtherUtils.isEmpty(this.alias) ? "" : this.alias;
    }

    public String getBindEmail() {
        return OtherUtils.isEmpty(this.bindEmail) ? "" : this.bindEmail;
    }

    public String getBindMobile() {
        return OtherUtils.isEmpty(this.bindMobile) ? "" : this.bindMobile;
    }

    public String getBindQQ() {
        return OtherUtils.isEmpty(this.bindQQ) ? "" : this.bindQQ;
    }

    public int getGender() {
        return this.gender;
    }

    public String getHeadPic() {
        return OtherUtils.isEmpty(this.headPic) ? "" : this.headPic;
    }

    public String getRegion() {
        return OtherUtils.isEmpty(this.region) ? "" : this.region;
    }

    public String getSignature() {
        return this.signature;
    }

    public String getUserName() {
        return OtherUtils.isEmpty(this.userName) ? "" : this.userName;
    }

    public String getUserTalker() {
        return OtherUtils.isEmpty(this.userTalker) ? "" : this.userTalker;
    }

    public void setAlias(String str) {
        this.alias = str;
    }

    public void setBindEmail(String str) {
        this.bindEmail = str;
    }

    public void setBindMobile(String str) {
        this.bindMobile = str;
    }

    public void setBindQQ(String str) {
        this.bindQQ = str;
    }

    public void setGender(int i) {
        this.gender = i;
    }

    public void setHeadPic(String str) {
        this.headPic = str;
    }

    public void setRegion(String str) {
        this.region = str;
    }

    public void setSignature(String str) {
        this.signature = str;
    }

    public void setUserName(String str) {
        this.userName = str;
    }

    public void setUserTalker(String str) {
        this.userTalker = str;
    }

    public String toString() {
        return "UserEntity{userTalker='" + this.userTalker + '\'' + ", userName='" + this.userName + '\'' + ", bindQQ='" + this.bindQQ + '\'' + ", bindEmail='" + this.bindEmail + '\'' + ", bindMobile='" + this.bindMobile + '\'' + ", alias='" + this.alias + '\'' + ", headPic='" + this.headPic + '\'' + ", gender=" + this.gender + ", region='" + this.region + '\'' + '}';
    }
}