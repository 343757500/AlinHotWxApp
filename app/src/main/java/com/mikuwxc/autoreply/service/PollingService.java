package com.mikuwxc.autoreply.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.mikuwxc.autoreply.common.net.NetApi;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.common.util.Constants;
import com.mikuwxc.autoreply.common.util.ToastUtil;
import com.mikuwxc.autoreply.modle.AliveBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class PollingService extends Service {
    public static final String ACTION = "PollingService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("wxno", Constants.wxno);
   /*     OkGo.post(AppConfig.getSelectHost() + NetApi.alive).params(paramMap).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                AliveBean aliveBean = new Gson().fromJson(s, AliveBean.class);
                if (aliveBean.isSuccess() == true) {
                    ToastUtil.showLongToast(aliveBean.getMsg());
                } else {
                    ToastUtil.showLongToast("保活失败");
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                e.printStackTrace();
            }
        });*/
     /*   OkGo.post(AppConfig.OUT_NETWORK + NetApi.loginAlive + "?wxno=" + "hsl_test").execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, okhttp3.Response response) {
                Log.e("111", "result:" + s);
                try {
                    Date date = new Date();

                    Log.e("111","保活成功343757500"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));


                   *//* if (list_msgFail.size()>0){
                        handleFailedMessage(list_msgFail);
                    }else {
                        Log.e("111","保活成功::没有缓存失败的信息");
                    }

                    HttpImeiBean<Boolean> bean = new Gson().fromJson(s, new TypeToken<HttpImeiBean<Boolean>>(){}.getType());
                    if (bean.getResult()) {
                        Log.e("111", "保活信息成功:");
                    } else {
                        Log.e("111", "保活信息失败:");
                        SharedPreferences sp = context.getSharedPreferences("test", Activity.MODE_WORLD_READABLE);
                        SharedPreferences.Editor ditor = sp.edit();
                        ditor.putBoolean("test_put",false).commit();
                        ToastUtil.showLongToast("关闭所有权限");
                        //移除定时保活功能
                        handlerAlive.removeCallbacks(runnableAlive);
                        search[1] = chineseToUnicode(search[1]);
                        execShell(search);
                    }*//*
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("111", "保活信息失败:" + e.toString());
                }
            }
            @Override
            public void onError(Call call, okhttp3.Response response, Exception e) {
                super.onError(call, response, e);
                Log.e("111", "保活信息失败:");

            }
        });*/


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("PollingService:onDestroy");
    }
}
