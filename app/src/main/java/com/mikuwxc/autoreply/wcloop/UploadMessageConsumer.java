package com.mikuwxc.autoreply.wcloop;

import android.content.Context;

import com.mikuwxc.autoreply.wcentity.MessageEntity;
import com.mikuwxc.autoreply.wclocalsocket.LocalSocketClient;
import com.orhanobut.logger.Logger;

public class UploadMessageConsumer implements Runnable {
    private UploadMessageBasket basket;
    private Context context;
    private LocalSocketClient localSocketClient;

    public UploadMessageConsumer(Context context, LocalSocketClient localSocketClient, UploadMessageBasket uploadMessageBasket) {
        this.context = context;
        this.localSocketClient = localSocketClient;
        this.basket = uploadMessageBasket;
    }

    public void run() {
        while (true) {
            try {
                MessageEntity consume = this.basket.consume();
                Logger.i("===== after hook message %s %s", new Object[]{Long.valueOf(consume.getCreateTime()), Long.valueOf(System.currentTimeMillis() - 300000)});
                if (consume.getCreateTime() < System.currentTimeMillis() - 300000) {
                    Logger.i("===== UploadMessageConsumer is Forget", new Object[0]);
                } else {
                    this.localSocketClient.uploadMessage(consume);
                }
            } catch (Throwable e) {
                Logger.e(e, "===== UPLOAD MESSAGE CONSUMER ===== ERROR!!!", new Object[0]);
            }
        }
    }
}