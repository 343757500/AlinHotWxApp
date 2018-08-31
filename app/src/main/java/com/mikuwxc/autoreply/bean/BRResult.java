package com.mikuwxc.autoreply.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/11/22.
 */
public class BRResult {
    private List<TaskJob> job;
    private List<AutoReplyStateMent> statement;
    private DeviceInfo deviceInfo;
    private int userStatus;//1自动回复  0不自动回复
    private WxInfo wxInfo;
    private long nowTime;
    private String defaultName;
    private List<Phone> phoneList;
    private int contactFlag;
    private String wxListstr;
    private int talkFlag;

    public int getTalkFlag() {
        return talkFlag;
    }

    public void setTalkFlag(int talkFlag) {
        this.talkFlag = talkFlag;
    }

    public int getContactFlag() {
        return contactFlag;
    }

    public void setContactFlag(int contactFlag) {
        this.contactFlag = contactFlag;
    }

    public String getWxListstr() {
        return wxListstr;
    }

    public void setWxListstr(String wxListstr) {
        this.wxListstr = wxListstr;
    }

    public List<Phone> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(List<Phone> phoneList) {
        this.phoneList = phoneList;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public long getNowTime() {
        return nowTime;
    }

    public void setNowTime(long nowTime) {
        this.nowTime = nowTime;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public WxInfo getWxInfo() {
        return wxInfo;
    }

    public void setWxInfo(WxInfo wxInfo) {
        this.wxInfo = wxInfo;
    }

    public List<TaskJob> getJob() {
        return job;
    }

    public void setJob(List<TaskJob> job) {
        this.job = job;
    }

    public List<AutoReplyStateMent> getStatement() {
        return statement;
    }

    public void setStatement(List<AutoReplyStateMent> statement) {
        this.statement = statement;
    }
}
