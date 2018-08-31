package com.mikuwxc.autoreply.modle;

/**
 * Date: 2018/5/9 15:03.
 * Desc:
 */
public class HttpBean {


    /**
     * msg : 同步成功
     * code : 200
     * success : true
     */

    private String msg;
    private String code;
    private boolean success;

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
}
