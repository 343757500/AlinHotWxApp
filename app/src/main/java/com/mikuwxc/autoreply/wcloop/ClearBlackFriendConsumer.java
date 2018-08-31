package com.mikuwxc.autoreply.wcloop;

import android.content.Context;

import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wcentity.WxEntity;
import com.mikuwxc.autoreply.wclocalsocket.LocalSocketClient;
import com.mikuwxc.autoreply.wcutil.FileIoUtil;
import com.mikuwxc.autoreply.wcutil.FriendUtil;
import com.mikuwxc.autoreply.wcutil.WxSwitchControlUtil;
import com.orhanobut.logger.Logger;

public class ClearBlackFriendConsumer implements Runnable {
    private ClearBlackFriendBasket basket;
    private ClassLoader classLoader;
    private Context context;
    private LocalSocketClient localSocketClient;
    private WechatEntity wechatEntity;

    public ClearBlackFriendConsumer(Context context, ClassLoader classLoader, LocalSocketClient localSocketClient, WechatEntity wechatEntity, ClearBlackFriendBasket clearBlackFriendBasket) {
        this.context = context;
        this.classLoader = classLoader;
        this.localSocketClient = localSocketClient;
        this.wechatEntity = wechatEntity;
        this.basket = clearBlackFriendBasket;
    }

    public void run() {
        while (true) {
            try {
                if (WxSwitchControlUtil.getClearBlackFriendState()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                WxEntity consume = this.basket.consume();
                WxSwitchControlUtil.setClearBlackFriendState(true);
                FriendUtil.clearBlackFriend(this.classLoader, this.wechatEntity, consume.getUserName(), consume.getNickName());
                if (this.basket.getBasket().size() == 0) {
                    this.localSocketClient.reportClearBlackFriend(Long.parseLong(FileIoUtil.getValueFromPath("/mnt/sdcard/blackFriendsTaskId.txt")), true, "");
                }
                Thread.sleep(10000);
            } catch (Throwable e2) {
                Logger.e(e2, "===== CLEAR BLACK COMSUMER ===== ERROR!!!", new Object[0]);
                WxSwitchControlUtil.setClearBlackFriendState(false);
            }
        }
    }
}