package com.mikuwxc.autoreply.view.interfas;

import com.mikuwxc.autoreply.modle.LoginBean;

/**
 * Package_name:com.tencent.mobileqq.view.interfas
 * Author:CYX
 * Email:android_cyx@163.com
 * Date:2017/03/13   19/00
 *
 * 登录  view   接口实现   用于回调  处理UI
 */
public interface ILoginView {
    void iLoginViewSuccess(LoginBean loginBean); //登录成功
    void iLoginViewFailed(Exception e); //登录失败
}
