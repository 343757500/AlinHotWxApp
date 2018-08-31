package com.mikuwxc.autoreply.modle;

public class ImMessageBean {
    /**
     * wxno : Lyh_HoiRab
     * fromAccount : 70faf75c6b174d32ba258686dfd8ffb7
     * toAccount : 9d1b86f5fdaf459b9dc773d8ef4663f1
     * content : hello
     * conversationTime : 1531897001302
     * type : 1
     */

    private String wxno;
    private String fromAccount;
    private String toAccount;
    private String content;
    private long conversationTime;
    private String type;
    private String wxid;

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

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getConversationTime() {
        return conversationTime;
    }

    public void setConversationTime(long conversationTime) {
        this.conversationTime = conversationTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
