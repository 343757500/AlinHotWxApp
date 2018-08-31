package com.mikuwxc.autoreply.wcoksocket.towx.isClient;

import android.app.Activity;
import android.content.Context;

import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wclocalsocket.LocalSocketClient;
import com.mikuwxc.autoreply.wcloop.ClearBlackFriendBasket;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadThread implements Runnable {
    private Activity activity;
    private BufferedReader br;
    private ClassLoader classLoader;
    private ClearBlackFriendBasket clearBlackFriendBasket;
    private Context context;
    private LocalSocketClient localSocketClient;
    private ExecutorService service = Executors.newCachedThreadPool();
    private WechatEntity wechatEntity;

    class HandleMiddleRunnable implements Runnable {
        private String info;

        public HandleMiddleRunnable(String str) {
            this.info = str;
        }

        public void run() {
            try {
                ReceiveFromMiddle.handle(ReadThread.this.context, ReadThread.this.classLoader, ReadThread.this.activity, ReadThread.this.wechatEntity, this.info, ReadThread.this.localSocketClient, ReadThread.this.clearBlackFriendBasket);
            } catch (Throwable e) {
                Logger.e(e, "===== HANDLE MIDDLERUNNABLE ===== ERROR!!!", new Object[0]);
            }
        }
    }

    public ReadThread(Context context, ClassLoader classLoader, Activity activity, WechatEntity wechatEntity, BufferedReader bufferedReader, LocalSocketClient localSocketClient, ClearBlackFriendBasket clearBlackFriendBasket) {
        this.context = context;
        this.classLoader = classLoader;
        this.wechatEntity = wechatEntity;
        this.activity = activity;
        this.br = bufferedReader;
        this.localSocketClient = localSocketClient;
        this.clearBlackFriendBasket = clearBlackFriendBasket;
    }

    public void run() {
        try {
            Logger.i("===== WX THREAD START ", new Object[0]);
            while (true) {
                String readLine = this.br.readLine();
                if (readLine != null) {
                    Logger.i("===== READ FROM APP [%s]", new Object[]{readLine});
                    this.service.submit(new HandleMiddleRunnable(readLine));
                } else {
                    return;
                }
            }
        } catch (Throwable e) {
            Logger.e(e, "===== READ FROM APP ===== ERROR!!!", new Object[0]);
        }
    }
}