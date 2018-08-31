package com.mikuwxc.autoreply.presenter.tasks;


import com.mikuwxc.autoreply.modle.WXUserBean;
import com.mikuwxc.autoreply.presenter.interfas.OnLoginFinishedListener;

/**
 * Package_name:com.hero.zhaoq.mymvpdemo.presenter
 * Author:zhaoqiang
 * Email:zhaoq_hero@163.com
 * Date:2017/03/24   19/16
 *
 * 异步任务的  处理接口
 */
public interface IAsyncLoginTask {
    /**
     * 回调  方法
     * @param listener  监听器
     * @param bean  登陆数据的  封装类
     */
    void validateCredentAsync(OnLoginFinishedListener listener, WXUserBean bean);
}
