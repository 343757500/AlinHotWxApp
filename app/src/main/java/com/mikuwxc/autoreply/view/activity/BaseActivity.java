package com.mikuwxc.autoreply.view.activity;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.mikuwxc.autoreply.common.util.EventBusUtil;
import com.mikuwxc.autoreply.modle.Event;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Package_name:com.tencent.mobileqq
 * Author:CYX
 * Email:android_cyx@163.com
 * Date:2018/03/13   18/29
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static String TAG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (isRegisterEventBus()) {
            EventBusUtil.register(this);
        }
        TAG = getClass().getSimpleName();
    }


    ProgressDialog progressDialog ;
    /**
     *
     * @Title: showProcessDialoig
     * @Description:
     * @param msg void
     *
     */
    public void showProcessDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        if (!TextUtils.isEmpty(msg)){
//            progressDialog.setMessage(msg);
            progressDialog.setTitle(msg);
        }else{
            progressDialog.setMessage("正在加载...");
        }

        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    public void dismissProcessDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        dismissProcessDialog();
        super.onDestroy();
        if (isRegisterEventBus()) {
            EventBusUtil.unregister(this);
        }
    }

    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，默认不绑定，子类需要绑定的话复写此方法返回true.
     */
    public boolean isRegisterEventBus() {
        return false;
    }
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEventBusCome(Event event) {
        if (event != null) {
            receiveEvent(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread, sticky = true)
    public void onStickyEventBusCome(Event event) {
        if (event != null) {
            receiveStickyEvent(event);
        }
    }
    /**
     * 接收到分发到事件
     *
     * @param event 事件
     */
    protected void receiveEvent(Event event) {

    }

    /**
     * 接受到分发的粘性事件
     *
     * @param event 粘性事件
     */
    protected void receiveStickyEvent(Event event) {

    }
}
