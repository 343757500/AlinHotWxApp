package com.mikuwxc.autoreply.bean;

import android.text.TextUtils;

/**
 * Created by Administrator on 2016/9/20.
 */
public class FriendBean {
    private String wechatNum = "";
    private String wechatNick = "";
    private String wechatRemark = "";
    private String wechatPhone = "";

    public String getWechatNum() {
        return wechatNum;
    }

    public void setWechatNum(String wechatNum) {
        this.wechatNum = wechatNum;
    }

    public String getWechatNick() {
        return wechatNick;
    }

    public void setWechatNick(String wechatNick) {
        this.wechatNick = wechatNick;
    }

    public String getWechatRemark() {
        return wechatRemark;
    }

    public void setWechatRemark(String wechatRemark) {
        this.wechatRemark = wechatRemark;
    }

    public String getWechatPhone() {
        return wechatPhone;
    }

    public void setWechatPhone(String wechatPhone) {
        this.wechatPhone = wechatPhone;
    }

    @Override
    public boolean equals(Object o) {
        FriendBean fb = (FriendBean) o;
        if (TextUtils.isEmpty(this.wechatNum)) {
            if (this.wechatNick.equals(fb.getWechatNick()))
                return true;
            else
                return false;
        } else {
            if (this.wechatNum.equals(fb.getWechatNum()))
                return true;
            else
                return false;
        }

    }
}
