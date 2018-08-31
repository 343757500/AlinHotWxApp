package com.mikuwxc.autoreply.wcutil;

import java.util.concurrent.ConcurrentHashMap;

public class MsgTreeUtil {
    private static ConcurrentHashMap<String, Long> msgTree;

    public static ConcurrentHashMap<String, Long> getMsgTree() {
        if (msgTree == null) {
            msgTree = new ConcurrentHashMap();
        }
        return msgTree;
    }
}