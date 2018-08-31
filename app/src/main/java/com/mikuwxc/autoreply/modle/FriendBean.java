package com.mikuwxc.autoreply.modle;

import com.google.gson.annotations.SerializedName;

/**
 * Date: 2018/4/13 14:33.
 * Desc: 好友bean
 */
public class FriendBean {


    /**
     * wechatId : 2fd3f9ee9ea446f2a3ea98cc8d4051f7
     * nickname : Temper 司徒
     * remarkname : bd7bd9dff6ee4da2b8e71aa574473f8a
     * headImgUrl : http://cloned.test.upcdn.net/head/6b2d753dd768498b88a02b20caf03d63.jpg
     * serviceId : f218dd10b4244ea798995c72856387bc
     * sex : 1
     * createTime : 1522738162000
     * id : 928204f3fc9d4234a32abc7adc3f5d49
     */

    private String wechatId;
    private String nickname;
    private String remarkname;
    private String headImgUrl;
    private String serviceId;
    private String sex;
    private String wxno;
    private long createTime;
    private String wxid;
    @SerializedName("id")
    private String friendId; //这个不是数据库id
    private String country;
    private String province;
    private String city;

    public String getWxid() {
        return wxid;
    }

    public void setWxid(String wxid) {
        this.wxid = wxid;
    }

    public String getWxno() {
        return wxno;
    }

    public void setWxno(String wxno) {
        this.wxno = wxno;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRemarkname() {
        return remarkname;
    }

    public void setRemarkname(String remarkname) {
        this.remarkname = remarkname;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    @Override
    public String toString() {
        return "Friend--nickname:" + nickname + " remarkname:" + remarkname + " headImgUrl:" + headImgUrl;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
