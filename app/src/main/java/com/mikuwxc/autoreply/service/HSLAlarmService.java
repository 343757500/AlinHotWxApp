package com.mikuwxc.autoreply.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.easy.wtool.sdk.WToolSDK;
import com.mikuwxc.autoreply.common.util.Constants;
import com.mikuwxc.autoreply.common.util.LogUtils;
import com.mikuwxc.autoreply.common.util.SharedPrefsUtils;
import com.mikuwxc.autoreply.presenter.tasks.AsyncFriendTask;

/**
 * Date: 2018/5/10 10:36.
 * Desc:
 */
public class HSLAlarmService extends Service {
    private static final String TAG = HSLAlarmService.class.getSimpleName();
    private static boolean running = false;
    private CountDownTimer cdt;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long syncFriendTime = SharedPrefsUtils.getLong("syncFriendTime");
        long currTime = System.currentTimeMillis();
        LogUtils.w(TAG, "currTime:" + currTime + " syncFriendTime:" + syncFriendTime + " =" + (currTime - syncFriendTime) + " running:" + running);
//        if (currTime - syncFriendTime > 120 * 60 * 1000 && syncFriendTime != 0) {
//            PhoneController.wakeAndUnlockScreen(this);

//        }
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (!running) {
            running = true;
            if (Constants.token != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WToolSDK sdk = new WToolSDK();
                        String initResult = sdk.init("9999", "757533D0860F8CC0590B510BE2374F48C5750673");
                        LogUtils.i(TAG, initResult);
                        AsyncFriendTask.getFriendList(sdk, true);
                    }
                }).start();
            }
            cdt = new CountDownTimer(24 * 60 * 60 * 1000, 10 * 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Intent i = new Intent(HSLAlarmService.this, AlarmReceiver.class);
                    i.setAction(AlarmReceiver.SYNC_FRIEND);
                    sendBroadcast(i);
                    running = false;
                }
            };
            cdt.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        LogUtils.d(TAG, "onDestroy");
        Intent i = new Intent(HSLAlarmService.this, AlarmReceiver.class);
        i.setAction(AlarmReceiver.SYNC_FRIEND);
        sendBroadcast(i);
        super.onDestroy();
    }
}
