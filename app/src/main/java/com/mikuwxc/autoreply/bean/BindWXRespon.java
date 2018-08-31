package com.mikuwxc.autoreply.bean;

/**
 * Created by Administrator on 2016/11/22.
 */
public class BindWXRespon {
    private BRResult result;
    private int status;
    private String msg;
    private int code;

    public BRResult getResult() {
        return result;
    }

    public void setResult(BRResult result) {
        this.result = result;
    }

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
}
