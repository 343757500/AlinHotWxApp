package com.mikuwxc.autoreply.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Date: 2018/5/10 10:38.
 * Desc:
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final String SYNC_FRIEND = "com.hsl.helper.sync_friend";
    public static final String MESSAGE_WARN = "com.hsl.helper.message_warn";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SYNC_FRIEND)) {
            Intent i = new Intent(context, HSLAlarmService.class);
            context.startService(i);
        }

    }
}
