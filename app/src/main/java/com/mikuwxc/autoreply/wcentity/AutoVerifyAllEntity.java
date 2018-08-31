package com.mikuwxc.autoreply.wcentity;

public class AutoVerifyAllEntity {
    /**
     * msg : {"alias":"hsl_test","snsbgobjectid":"0","googlecontact":"","smallheadimgurl":"http://wx.qlogo.cn/mmhead/ver_1/5bWKuibPZnlxdeLu6RicX5tl4nKxnEh7qvysTkCnRNRx1YPYNZZhO4ScwaWlW7EKDiacG1xEtB7EP5rBnYIDcdegTURakjSehWNdMAF3H7L7nI/96","fromnickname":"NA","city":"Shenzhen","brandlist":{"count":"0","ver":"675952847"},"ticket":"v2_a8549863467b58299d3a420564c1bd98bc7af832f2bb127ff2369996863b2872ab5af8bd44ccede655b1778a6600799564ea9b38f9606629d63e4c538f1a3573@stranger","province":"Guangdong","weibo":"","content":"","mfullhash":"aece066d068ccd75f98b95763179a75c","snsflag":"1","sourcenickname":"","bigheadimgurl":"http://wx.qlogo.cn/mmhead/ver_1/5bWKuibPZnlxdeLu6RicX5tl4nKxnEh7qvysTkCnRNRx1YPYNZZhO4ScwaWlW7EKDiacG1xEtB7EP5rBnYIDcdegTURakjSehWNdMAF3H7L7nI/0","fromusername":"wxid_98m3ys9svjnu22","sourceusername":"","albumbgimgid":"","mhash":"aece066d068ccd75f98b95763179a75c","fullpy":"NA","albumstyle":"0","percard":"1","sign":"","chatroomusername":"","imagestatus":"3","albumflag":"0","encryptusername":"v1_d88ac7755c8d38fc05fa9d62f3559f5526e1de2a5920206d40d5f529ec0afe2379310826cc4477e34757bc13e111a690@stranger","country":"CN","opcode":"2","qrticket":"","snsbgimgid":"","shortpy":"NA","sex":"1","scene":"6"}
     */

    private MsgBean msg;

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        /**
         * alias : hsl_test
         * snsbgobjectid : 0
         * googlecontact :
         * smallheadimgurl : http://wx.qlogo.cn/mmhead/ver_1/5bWKuibPZnlxdeLu6RicX5tl4nKxnEh7qvysTkCnRNRx1YPYNZZhO4ScwaWlW7EKDiacG1xEtB7EP5rBnYIDcdegTURakjSehWNdMAF3H7L7nI/96
         * fromnickname : NA
         * city : Shenzhen
         * brandlist : {"count":"0","ver":"675952847"}
         * ticket : v2_a8549863467b58299d3a420564c1bd98bc7af832f2bb127ff2369996863b2872ab5af8bd44ccede655b1778a6600799564ea9b38f9606629d63e4c538f1a3573@stranger
         * province : Guangdong
         * weibo :
         * content :
         * mfullhash : aece066d068ccd75f98b95763179a75c
         * snsflag : 1
         * sourcenickname :
         * bigheadimgurl : http://wx.qlogo.cn/mmhead/ver_1/5bWKuibPZnlxdeLu6RicX5tl4nKxnEh7qvysTkCnRNRx1YPYNZZhO4ScwaWlW7EKDiacG1xEtB7EP5rBnYIDcdegTURakjSehWNdMAF3H7L7nI/0
         * fromusername : wxid_98m3ys9svjnu22
         * sourceusername :
         * albumbgimgid :
         * mhash : aece066d068ccd75f98b95763179a75c
         * fullpy : NA
         * albumstyle : 0
         * percard : 1
         * sign :
         * chatroomusername :
         * imagestatus : 3
         * albumflag : 0
         * encryptusername : v1_d88ac7755c8d38fc05fa9d62f3559f5526e1de2a5920206d40d5f529ec0afe2379310826cc4477e34757bc13e111a690@stranger
         * country : CN
         * opcode : 2
         * qrticket :
         * snsbgimgid :
         * shortpy : NA
         * sex : 1
         * scene : 6
         */

        private String alias;
        private String snsbgobjectid;
        private String googlecontact;
        private String smallheadimgurl;
        private String fromnickname;
        private String city;
        private BrandlistBean brandlist;
        private String ticket;
        private String province;
        private String weibo;
        private String content;
        private String mfullhash;
        private String snsflag;
        private String sourcenickname;
        private String bigheadimgurl;
        private String fromusername;
        private String sourceusername;
        private String albumbgimgid;
        private String mhash;
        private String fullpy;
        private String albumstyle;
        private String percard;
        private String sign;
        private String chatroomusername;
        private String imagestatus;
        private String albumflag;
        private String encryptusername;
        private String country;
        private String opcode;
        private String qrticket;
        private String snsbgimgid;
        private String shortpy;
        private String sex;
        private String scene;

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getSnsbgobjectid() {
            return snsbgobjectid;
        }

        public void setSnsbgobjectid(String snsbgobjectid) {
            this.snsbgobjectid = snsbgobjectid;
        }

        public String getGooglecontact() {
            return googlecontact;
        }

        public void setGooglecontact(String googlecontact) {
            this.googlecontact = googlecontact;
        }

        public String getSmallheadimgurl() {
            return smallheadimgurl;
        }

        public void setSmallheadimgurl(String smallheadimgurl) {
            this.smallheadimgurl = smallheadimgurl;
        }

        public String getFromnickname() {
            return fromnickname;
        }

        public void setFromnickname(String fromnickname) {
            this.fromnickname = fromnickname;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public BrandlistBean getBrandlist() {
            return brandlist;
        }

        public void setBrandlist(BrandlistBean brandlist) {
            this.brandlist = brandlist;
        }

        public String getTicket() {
            return ticket;
        }

        public void setTicket(String ticket) {
            this.ticket = ticket;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getWeibo() {
            return weibo;
        }

        public void setWeibo(String weibo) {
            this.weibo = weibo;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getMfullhash() {
            return mfullhash;
        }

        public void setMfullhash(String mfullhash) {
            this.mfullhash = mfullhash;
        }

        public String getSnsflag() {
            return snsflag;
        }

        public void setSnsflag(String snsflag) {
            this.snsflag = snsflag;
        }

        public String getSourcenickname() {
            return sourcenickname;
        }

        public void setSourcenickname(String sourcenickname) {
            this.sourcenickname = sourcenickname;
        }

        public String getBigheadimgurl() {
            return bigheadimgurl;
        }

        public void setBigheadimgurl(String bigheadimgurl) {
            this.bigheadimgurl = bigheadimgurl;
        }

        public String getFromusername() {
            return fromusername;
        }

        public void setFromusername(String fromusername) {
            this.fromusername = fromusername;
        }

        public String getSourceusername() {
            return sourceusername;
        }

        public void setSourceusername(String sourceusername) {
            this.sourceusername = sourceusername;
        }

        public String getAlbumbgimgid() {
            return albumbgimgid;
        }

        public void setAlbumbgimgid(String albumbgimgid) {
            this.albumbgimgid = albumbgimgid;
        }

        public String getMhash() {
            return mhash;
        }

        public void setMhash(String mhash) {
            this.mhash = mhash;
        }

        public String getFullpy() {
            return fullpy;
        }

        public void setFullpy(String fullpy) {
            this.fullpy = fullpy;
        }

        public String getAlbumstyle() {
            return albumstyle;
        }

        public void setAlbumstyle(String albumstyle) {
            this.albumstyle = albumstyle;
        }

        public String getPercard() {
            return percard;
        }

        public void setPercard(String percard) {
            this.percard = percard;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getChatroomusername() {
            return chatroomusername;
        }

        public void setChatroomusername(String chatroomusername) {
            this.chatroomusername = chatroomusername;
        }

        public String getImagestatus() {
            return imagestatus;
        }

        public void setImagestatus(String imagestatus) {
            this.imagestatus = imagestatus;
        }

        public String getAlbumflag() {
            return albumflag;
        }

        public void setAlbumflag(String albumflag) {
            this.albumflag = albumflag;
        }

        public String getEncryptusername() {
            return encryptusername;
        }

        public void setEncryptusername(String encryptusername) {
            this.encryptusername = encryptusername;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getOpcode() {
            return opcode;
        }

        public void setOpcode(String opcode) {
            this.opcode = opcode;
        }

        public String getQrticket() {
            return qrticket;
        }

        public void setQrticket(String qrticket) {
            this.qrticket = qrticket;
        }

        public String getSnsbgimgid() {
            return snsbgimgid;
        }

        public void setSnsbgimgid(String snsbgimgid) {
            this.snsbgimgid = snsbgimgid;
        }

        public String getShortpy() {
            return shortpy;
        }

        public void setShortpy(String shortpy) {
            this.shortpy = shortpy;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getScene() {
            return scene;
        }

        public void setScene(String scene) {
            this.scene = scene;
        }

        public static class BrandlistBean {
            /**
             * count : 0
             * ver : 675952847
             */

            private String count;
            private String ver;

            public String getCount() {
                return count;
            }

            public void setCount(String count) {
                this.count = count;
            }

            public String getVer() {
                return ver;
            }

            public void setVer(String ver) {
                this.ver = ver;
            }
        }
    }
}
