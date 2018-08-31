package com.mikuwxc.autoreply.modle;

/**
 * Date: 2018/4/13 17:38.
 * Desc:
 */
public class CommonMessageBean {

    /**
     * wechatId : 2fd3f9ee9ea446f2a3ea98cc8d4051f7
     * service : {"username":"bacon","password":"e10adc3949ba59abbe56e057f20f883e","wechatId":"2fd3f9ee9ea446f2a3ea98cc8d4051f7","name":"培根","headImgUrl":"","createTime":1522229499000,"id":"f218dd10b4244ea798995c72856387bc"}
     * friend : {"wechatId":"2fd3f9ee9ea446f2a3ea98cc8d4051f7","nickname":"Bacon Chen","remarkname":"65fkjmzoc2pyxtp6bo3cls3xd","headImgUrl":"http://cloned.test.upcdn.net/head/eb7e1fcf800a4a8880c3cf7a867ac69e.jpg","serviceId":"f218dd10b4244ea798995c72856387bc","sex":"1","createTime":1523606798000,"id":"de3fef6c6c90408ca752ca2d8bba51c9"}
     * remarkname : 65fkjmzoc2pyxtp6bo3cls3xd
     * headImgUrl : http://cloned.test.upcdn.net/head/eb7e1fcf800a4a8880c3cf7a867ac69e.jpg
     * text : 4
     * type : 0
     * sendByService : false
     */

    private String wechatId;
    private ServiceBean service;
    private FriendBean friend;
    private String remarkname;
    private String headImgUrl;
    private String text;
    private String type;
    private boolean sendByService;

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public ServiceBean getService() {
        return service;
    }

    public void setService(ServiceBean service) {
        this.service = service;
    }

    public FriendBean getFriend() {
        return friend;
    }

    public void setFriend(FriendBean friend) {
        this.friend = friend;
    }

    public String getRemarkname() {
        return remarkname;
    }

    public void setRemarkname(String remarkname) {
        this.remarkname = remarkname;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSendByService() {
        return sendByService;
    }

    public void setSendByService(boolean sendByService) {
        this.sendByService = sendByService;
    }

    public static class ServiceBean {
        /**
         * username : bacon
         * password : e10adc3949ba59abbe56e057f20f883e
         * wechatId : 2fd3f9ee9ea446f2a3ea98cc8d4051f7
         * name : 培根
         * headImgUrl :
         * createTime : 1522229499000
         * id : f218dd10b4244ea798995c72856387bc
         */

        private String username;
        private String password;
        private String wechatId;
        private String name;
        private String headImgUrl;
        private long createTime;
        private String id;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getWechatId() {
            return wechatId;
        }

        public void setWechatId(String wechatId) {
            this.wechatId = wechatId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHeadImgUrl() {
            return headImgUrl;
        }

        public void setHeadImgUrl(String headImgUrl) {
            this.headImgUrl = headImgUrl;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

}
