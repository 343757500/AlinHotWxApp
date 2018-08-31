package com.mikuwxc.autoreply.wcloop;

import com.mikuwxc.autoreply.wcentity.WxEntity;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClearBlackFriendBasket {
    private BlockingQueue<WxEntity> basket = new LinkedBlockingQueue();

    public WxEntity consume() throws InterruptedException {
        return (WxEntity) this.basket.take();
    }

    public BlockingQueue<WxEntity> getBasket() {
        return this.basket;
    }

    public void produce(WxEntity wxEntity) {
        try {
            this.basket.put(wxEntity);
        } catch (Throwable e) {
            //ThrowableExtension.printStackTrace(e);
        }
    }
}