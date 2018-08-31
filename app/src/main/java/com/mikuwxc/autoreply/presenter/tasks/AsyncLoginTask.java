package com.mikuwxc.autoreply.presenter.tasks;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.mikuwxc.autoreply.common.net.NetApi;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.common.util.LogUtils;
import com.mikuwxc.autoreply.common.util.ToastUtil;
import com.mikuwxc.autoreply.modle.LoginBean;
import com.mikuwxc.autoreply.modle.WXUserBean;
import com.mikuwxc.autoreply.presenter.interfas.OnLoginFinishedListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Package_name:com.hero.zhaoq.mymvpdemo.presenter
 * Author:zhaoqiang
 * Email:zhaoq_hero@163.com
 * Date:2017/03/24   19/15
 */
public class AsyncLoginTask implements IAsyncLoginTask {
    /**
     * 异步任务  处理  登录数据信息
     *
     * @param listener 回调处理类
     * @param bean     登陆数据对象的  封装类
     */

    @Override
    public void validateCredentAsync(final OnLoginFinishedListener listener, final WXUserBean bean) {

//        Map<String, String> paramMap = new HashMap<>();
        LogUtils.w("AsyncLoginTask", "=== wxno:" + bean.getWxno() + " phone:" + bean.getPhone() + " imei:" + bean.getImei());
//        paramMap.put("wxno", bean.getWxno());
//        paramMap.put("phone", bean.getPhone());
//        paramMap.put("imei", bean.getImei());
        ToastUtil.showLongToast(AppConfig.getSelectHost() + NetApi.login + "/" + bean.getWxno());
        Log.e("111","wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
        OkGo.get(AppConfig.getSelectHost() + NetApi.login + "/" + bean.getWxno() + "?registrationId=" + bean.getRegistrationId()).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.e("111","eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
                LoginBean loginBean = new Gson().fromJson(s, LoginBean.class);
                if (loginBean.isSuccess()) {
                    listener.onLoginFishedLisSuccess(loginBean);
                } else {
                    LogUtils.w("validateCredentAsync", "result:" + s);
                    if (!TextUtils.isEmpty(loginBean.getMsg())) {
                        ToastUtil.showLongToast("登录失败:" + loginBean.getMsg());
                    } else {
                        ToastUtil.showLongToast("登录失败:" + loginBean.getCode());
                    }
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Log.e("111","rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
                e.printStackTrace();
                listener.onLoginFishedLisError(e);
            }
        });
//        OkGo.post(AppConfig.getSelectHost()+ NetApi.login+""+bean.getWxno()).params(paramMap).execute(new StringCallback() {
//            @Override
//            public void onSuccess(String s, Call call, Response response) {
//                LoginBean loginBean = new Gson().fromJson(s, LoginBean.class);
//                if (loginBean.isSuccess()) {
//                    listener.onLoginFishedLisSuccess(loginBean);
//                } else {
//                    LogUtils.w("validateCredentAsync", "result:" + s);
//                    if (!TextUtils.isEmpty(loginBean.getMsg())) {
//                        ToastUtil.showLongToast("登录失败:" + loginBean.getMsg());
//                    } else {
//                        ToastUtil.showLongToast("登录失败:" + loginBean.getCode());
//                    }
//                }
//            }
//
//            @Override
//            public void onError(Call call, Response response, Exception e) {
//                super.onError(call, response, e);
//                e.printStackTrace();
//                listener.onLoginFishedLisError(e);
//            }
//        });

    }

}
