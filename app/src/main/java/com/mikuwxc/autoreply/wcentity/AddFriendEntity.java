package com.mikuwxc.autoreply.wcentity;

public class AddFriendEntity {
    private String   addNo;   //添加号
    private String   msg;	//添加的消息
    private String   remark;  //备注
    private String   type;    //加好友形式

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public String getAddNo() {
        return addNo;
    }

    public void setAddNo(String addNo) {
        this.addNo = addNo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
