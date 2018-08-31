package com.mikuwxc.autoreply.wcentity;

import java.util.List;

public class CircleFriendEntity {
    /**
     * wechatId : e067e3933e64489386f51ec5db1522f4
     * content : 内11容11331
     * operateTime : Aug 13, 2018 11:08:33 AM
     * operator : 超级管理员
     * type : 1
     * state : 0
     * createTime : Aug 13, 2018 11:08:33 AM
     * wechatIds : ["5906d10a119d409c8930dfef1bc375c4","e067e3933e64489386f51ec5db1522f4"]
     * id : e9e31ef46e24424f9649c7b9e4d0674e
     */

    private String wechatId;
    private String content;
    private String operateTime;
    private String fodderUrl;
    private String location;
    private String operator;
    private String type;
    private String state;
    private String createTime;
    private String id;
    private List<String> wechatIds;


    public String getFodderUrl() {
        return fodderUrl;
    }

    public void setFodderUrl(String fodderUrl) {
        this.fodderUrl = fodderUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getWechatIds() {
        return wechatIds;
    }

    public void setWechatIds(List<String> wechatIds) {
        this.wechatIds = wechatIds;
    }
}
