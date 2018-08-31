package com.mikuwxc.autoreply.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Date: 2018/5/15 14:14.
 * Desc: 只用于出toast提示
 */
public class SensitiveService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String notice = intent.getStringExtra("notice");
            Toast.makeText(this, notice, Toast.LENGTH_SHORT).show();
        }
        return START_STICKY;
    }
}
