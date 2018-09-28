package com.mikuwxc.autoreply.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.mikuwxc.autoreply.common.net.NetApi;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.service.LoopService;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;

public class AlarmReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        //context.getApplicationContext().startService(new Intent(context.getApplicationContext(), LoopUploadService.class));
        context.getApplicationContext().startService(new Intent(context.getApplicationContext(), LoopService.class));


    }
}
