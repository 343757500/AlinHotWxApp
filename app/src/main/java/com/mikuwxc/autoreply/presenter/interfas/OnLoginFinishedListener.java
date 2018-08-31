package com.mikuwxc.autoreply.presenter.interfas;

import com.mikuwxc.autoreply.modle.LoginBean;

/**
 * Package_name:com.hero.zhaoq.mymvpdemo.presenter
 * Author:zhaoqiang
 * Email:zhaoq_hero@163.com
 * Date:2017/03/24   19/02
 *
 * 登录  成功或失败的  回调逻辑
 */
public interface OnLoginFinishedListener {
    void onLoginFishedLisSuccess(LoginBean loginBean); //监听
    void onLoginFishedLisError(Exception e);  //监听
}
