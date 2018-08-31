package com.mikuwxc.autoreply.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Administrator on 2016/5/17.
 */
@Table(name = "PhoneUser")
public class PhoneUser extends Model {

    @Column(name = "phoneId")
    private long phoneId;//号码Id

    @Column(name = "phoneNum")
    private String phoneNum;//号码

    @Column(name = "addType")
    private int addType = 0;//添加类型  1：搜索加好友  2：通讯录加好友

    @Column(name = "nickName")
    private String nickName;//昵称

    @Column(name = "tName")
    private String tName;//通讯录备注名

    @Column(name = "status")
    private int status;//状态 0未添加  1已添加 2通过添加 3失败 4没有此用户

    @Column(name = "time")
    private long time;//操作时间   20161214

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(long phoneId) {
        this.phoneId = phoneId;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public int getAddType() {
        return addType;
    }

    public void setAddType(int addType) {
        this.addType = addType;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
