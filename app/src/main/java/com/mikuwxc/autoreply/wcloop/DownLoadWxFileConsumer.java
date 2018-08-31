package com.mikuwxc.autoreply.wcloop;

import android.content.Context;

import com.mikuwxc.autoreply.wcentity.MessageEntity;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wclocalsocket.LocalSocketClient;
import com.mikuwxc.autoreply.wcutil.DownLoadWxResFromWxUtil;
import com.mikuwxc.autoreply.wcutil.WxSwitchControlUtil;
import com.orhanobut.logger.Logger;

public class DownLoadWxFileConsumer implements Runnable {
    private DownLoadWxFileBasket basket;
    private Context context;
    private LocalSocketClient localSocketClient;
    private WechatEntity wechatEntity;
    private ClassLoader wxClassloader;

    public DownLoadWxFileConsumer(Context context, ClassLoader classLoader, LocalSocketClient localSocketClient, WechatEntity wechatEntity, DownLoadWxFileBasket downLoadWxFileBasket) {
        this.context = context;
        this.wxClassloader = classLoader;
        this.localSocketClient = localSocketClient;
        this.wechatEntity = wechatEntity;
        this.basket = downLoadWxFileBasket;
    }

    public void run() {
        while (true) {
            try {
                Logger.i("===== FILE LOOP ", new Object[0]);
                if (WxSwitchControlUtil.getDownLoadWxFileState()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                MessageEntity consume = this.basket.consume();
                Logger.i("===== FILE COMSUME ", new Object[0]);
                WxSwitchControlUtil.setDownLoadWxFileState(true);
                long fileSize = consume.getFileSize();
                long msgId = consume.getMsgId();
                if (fileSize > 26214400) {
                    DownLoadWxResFromWxUtil.downloadWxFileRes(this.wxClassloader, this.wechatEntity, consume.getMsgId(), true);
                } else {
                    DownLoadWxResFromWxUtil.downloadWxFileRes(this.wxClassloader, this.wechatEntity, msgId);
                }
                Logger.i("===== FILE FINISH ", new Object[0]);
            } catch (Throwable e2) {
                Logger.e(e2, "===== WX DOWNLOAD FILE COMSUMER ===== ERROR!!!", new Object[0]);
                WxSwitchControlUtil.setDownLoadWxFileState(false);
            }
        }
    }
}