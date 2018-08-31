package com.mikuwxc.autoreply.bean;

/**
 * Created by Administrator on 2016/11/23.
 */
public class WxInfo {
    private String wxLemonName;
    private long id;
    private String wxOpendId;
    private long userId;
    private long lastUpdated;
    private long dateCreated;
    private String wxNum;
    private String wxPic;

    public String getWxLemonName() {
        return wxLemonName;
    }

    public void setWxLemonName(String wxLemonName) {
        this.wxLemonName = wxLemonName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWxOpendId() {
        return wxOpendId;
    }

    public void setWxOpendId(String wxOpendId) {
        this.wxOpendId = wxOpendId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getWxNum() {
        return wxNum;
    }

    public void setWxNum(String wxNum) {
        this.wxNum = wxNum;
    }

    public String getWxPic() {
        return wxPic;
    }

    public void setWxPic(String wxPic) {
        this.wxPic = wxPic;
    }
}
