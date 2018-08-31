package com.mikuwxc.autoreply.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mikuwxc.autoreply.activity.RunningActivity;

public class laucherReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("111","onReceiver...");

        try {
            Intent mBootIntent = new Intent(context, RunningActivity.class);
            // 下面这句话必须加上才能开机自动运行app的界面
            mBootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mBootIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
