package com.mikuwxc.autoreply.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.mikuwxc.autoreply.R;
import com.mikuwxc.autoreply.common.net.NetApi;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.common.util.MyFileUtil;
import com.mikuwxc.autoreply.common.util.RegularUtils;
import com.mikuwxc.autoreply.common.util.SharedPrefsUtils;
import com.mikuwxc.autoreply.receiver.AlarmReceiver;


import okhttp3.Call;


public class LoopService extends Service{
    Handler handlerAlive=new Handler();
    Runnable runnableAlive=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            permissionAlive();
            handlerAlive.postDelayed(this, 30000);
        }
    };
    private String wxno;

    @Override
    public void onCreate() {

    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;

    }

    @SuppressLint("WrongConstant")
    public int onStartCommand(Intent intent, int flags, int startId) {
       String wxno1 = intent.getStringExtra("wxno");
        if (wxno1!=null){
            wxno=wxno1;
            Log.e("222","wxno为::"+wxno);
            handlerAlive.postDelayed(runnableAlive, 10000);//每两秒执行一次runnable.
        }else {
            Log.e("222","wxno为空");
        }


        ((AlarmManager) getSystemService(NotificationCompat.CATEGORY_ALARM)).setExact(2, SystemClock.elapsedRealtime() + ((long)5000), PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmReceiver.class), 0));

        NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("请不要锁屏")
                .setContentText("")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);
        Notification notification = builder.build();
        startForeground(1, notification);
        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }


    //是否保活权限
    private void permissionAlive() {
        Log.e("222","123456789");
        String aliveStaue = MyFileUtil.readFromFile(AppConfig.APP_FOLDER + "/updateAlive");
        if ("true".equals(aliveStaue)){
            aliveStaue="0";
        }else{
            aliveStaue="1";
        }
        if (wxno!=null) {
            Log.e("222","aliveStaue"+aliveStaue);
                OkGo.post(AppConfig.OUT_NETWORK + NetApi.loginAlive + "?wxno=" + wxno+"&usingState="+aliveStaue).execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        Log.e("222", "result:" + s);
                        try {

                            Log.e("222", "保活成功");


                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("222", "保活信息失败:" + e.toString());
                        }
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.e("222", "保活信息失败:" + e.toString());

                    }
                });

            }else {
                Log.e("222","没有权限关闭保活");
            }
    }


}

