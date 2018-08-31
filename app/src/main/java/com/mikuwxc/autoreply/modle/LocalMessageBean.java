package com.mikuwxc.autoreply.modle;

/**
 * Date: 2018/4/8 11:55.
 * Desc:
 */
public class LocalMessageBean {

    private String receiver;
    private String msg;
    private long createTime;
    private boolean sent;

    public LocalMessageBean(String rcv, String message, long time) {
        this.receiver = rcv;
        this.msg = message;
        this.createTime = time;
        this.sent = false;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "rcv:" + receiver + " msg:" + msg + " isSent:" + sent;
    }
}
