package com.mikuwxc.autoreply.modle;

/**
 * Package_name:com.tencent.mobileqq.modle
 * Author:CYX
 * Email:android_cyx@163.com
 * Date:2017/03/13  19:01
 * 用户登录  实体类
 */
public class LoginBean {


    /**
     * msg : 查询成功
     * code : 200
     * success : true
     * result : {"wxno":"hsl_test","nickname":"测试账号","createTime":1525766734000,"token":"3a96fff95e6c4f41b930ffb48b688a75"}
     */

    private String msg;
    private String code;
    private boolean success;
    private ResultBean result;

    @Override
    public String toString() {
        return "LoginBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", success=" + success +
                ", result=" + result +
                '}';
    }

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

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * wxno : hsl_test
         * nickname : 测试账号
         * createTime : 1525766734000
         * token : 3a96fff95e6c4f41b930ffb48b688a75
         */

        private String wxno;
        private String nickname;
        private long createTime;
        private String token;

        public String getWxno() {
            return wxno;
        }

        public void setWxno(String wxno) {
            this.wxno = wxno;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
