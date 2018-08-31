package com.mikuwxc.autoreply.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.mikuwxc.autoreply.common.net.NetApi;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.common.util.Constants;
import com.mikuwxc.autoreply.common.util.ToastUtil;
import com.mikuwxc.autoreply.modle.AliveBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

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
        OkGo.post(AppConfig.getSelectHost() + NetApi.alive).params(paramMap).execute(new StringCallback() {
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
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("PollingService:onDestroy");
    }
}
