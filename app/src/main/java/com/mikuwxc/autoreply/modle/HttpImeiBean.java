package com.mikuwxc.autoreply.modle;

public class HttpImeiBean {
    /**
     * msg : 登录成功
     * code : 200
     * success : true
     * result : true
     */

    private String msg;
    private String code;
    private boolean success;
    private boolean result;

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

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
