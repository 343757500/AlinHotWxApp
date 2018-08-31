package com.mikuwxc.autoreply.presenter;

import com.mikuwxc.autoreply.modle.LoginBean;
import com.mikuwxc.autoreply.modle.WXUserBean;
import com.mikuwxc.autoreply.presenter.interfas.ILoginPresenter;
import com.mikuwxc.autoreply.presenter.interfas.OnLoginFinishedListener;
import com.mikuwxc.autoreply.presenter.tasks.AsyncLoginTask;
import com.mikuwxc.autoreply.view.interfas.ILoginView;

/**
 * Package_name:com.hero.zhaoq.mymvpdemo.presenter
 * Author:zhaoqiang
 * Email:zhaoq_hero@163.com
 * Date:2017/03/24   18/56
 *
 * 登录的  数据控制器  实现登录数据控制器接口
 * 实现逻辑业务
 *
 * 去登陆
 * 登录成功    失败回调   实现数据回调   并将回调数据返回  View
 */
public class LoginPresenter implements ILoginPresenter,OnLoginFinishedListener {

    //present 持有  view 和 modle 的 引用   modle 和View  通过p  交互
    private ILoginView view;
    private AsyncLoginTask task;  //处理  异步任务

    /**
     * 将  当前的需要处理的View 传进来   这边用于回调处理数据
     * @param loginView  需要处理的View    实现 ILoginView 接口
     */
    public LoginPresenter(ILoginView loginView) {
        this.view = loginView;
        this.task = new AsyncLoginTask(); //创建  异步任务类
    }

    /**
     * 去登陆  传入 微信号,手机号码,设备IMEI   将当前类作为 回调类
     * @param wxno 微信号
     * @param phone 手机号
     * @param imei 设备IMEI
     */
    @Override
    public void iLoginPrToLogin(String wxno, String phone, String imei, String regId) {
        //执行 异步任务：  请求数据   将this  作为监听器
        task.validateCredentAsync(this, new WXUserBean(wxno, phone, imei, regId));
    }

    @Override
    public void onLoginFishedLisSuccess(LoginBean loginBean) {
        view.iLoginViewSuccess(loginBean);
    }

    @Override
    public void onLoginFishedLisError(Exception e) {
        view.iLoginViewFailed(e);
    }

}
