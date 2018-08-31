package com.mikuwxc.autoreply.wcutil;


public class WxSwitchControlUtil {
    private static volatile boolean clearBlackFriendState = false;
    private static volatile boolean downLoadWxFileState = false;

    public static boolean getClearBlackFriendState() {
        return clearBlackFriendState;
    }

    public static boolean getDownLoadWxFileState() {
        return downLoadWxFileState;
    }

    public static void setClearBlackFriendState(boolean z) {
        clearBlackFriendState = z;
    }

    public static void setDownLoadWxFileState(boolean z) {
        FileIoUtil.setValueToPath("newDownLoadWxFileState[" + z + "]\n", false, "/mnt/sdcard/newDownLoadWxFileState.txt");
        downLoadWxFileState = z;
    }
}