package com.mikuwxc.autoreply.wcloop;

import com.mikuwxc.autoreply.wcentity.MessageEntity;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DownLoadWxFileBasket {
    private BlockingQueue<MessageEntity> basket = new LinkedBlockingQueue();

    public MessageEntity consume() throws InterruptedException {
        return (MessageEntity) this.basket.take();
    }

    public BlockingQueue<MessageEntity> getBasket() {
        return this.basket;
    }

    public void produce(MessageEntity messageEntity) {
        try {
            this.basket.put(messageEntity);
        } catch (Throwable e) {
            //ThrowableExtension.printStackTrace(e);
        }
    }
}