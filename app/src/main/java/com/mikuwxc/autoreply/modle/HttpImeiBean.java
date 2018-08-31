package com.mikuwxc.autoreply.modle;

public class HttpImeiBean<T> {
    /**
     * msg : 登录成功
     * code : 200
     * success : true
     * result : true
     */

    private String msg;
    private String code;
    private boolean success;
    private T result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
