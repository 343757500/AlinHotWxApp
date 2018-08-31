package com.mikuwxc.autoreply.modle;

/**
 * Package_name:com.tencent.mobileqq.modle
 * Author:CYX
 * Email:android_cyx@163.com
 * Date:2017/03/13  19:01
 * 用户  实体类
 */
public class WXUserBean {

    private String wxno;//微信号
    private String phone;//手机号码
    private String imei;//手机IMEI
    private String registrationId; //极光推送的id

    public WXUserBean(String wxno, String phone, String imei, String regId) {
        this.wxno = wxno;
        this.phone = phone;
        this.imei = imei;
        this.registrationId = regId;
    }

    public String getWxno() {
        return wxno;
    }

    public void setWxno(String wxno) {
        this.wxno = wxno;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    @Override
    public String toString() {
        return "WXUserBean{" +
                "wxno='" + wxno + '\'' +
                ", phone='" + phone + '\'' +
                ", imei='" + imei + '\'' +
                '}';
    }
}
