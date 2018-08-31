package com.mikuwxc.autoreply.bean;

/**
 * Created by Administrator on 2016/11/22.
 */
public class TaskRecord {
    private boolean expand;
    private int status;
    private String msg;
    private int code;
    private TRResult result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public TRResult getResult() {
        return result;
    }

    public void setResult(TRResult result) {
        this.result = result;
    }


    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }
}
