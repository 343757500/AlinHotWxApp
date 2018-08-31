package com.mikuwxc.autoreply.wcentity;

public class AutoVerifyEntity {
    private String encryptTalker;
    private int scene;
    private String talker;

    public String getEncryptTalker() {
        return this.encryptTalker;
    }

    public int getScene() {
        return this.scene;
    }

    public String getTalker() {
        return this.talker;
    }

    public void setEncryptTalker(String str) {
        this.encryptTalker = str;
    }

    public void setScene(int i) {
        this.scene = i;
    }

    public void setTalker(String str) {
        this.talker = str;
    }

    public String toString() {
        return "AutoVerifyEntity{talker='" + this.talker + '\'' + ", encryptTalker='" + this.encryptTalker + '\'' + ", scene=" + this.scene + '}';
    }
}