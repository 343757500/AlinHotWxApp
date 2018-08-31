package com.mikuwxc.autoreply.common.util;

import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

/**
 * @author CenYongxiong
 * @version V1.0
 * @ClassName: [com.cncbk.groupbuy.util.ToastUtils]
 * @Description: TODO (用一句话描述该文件做什么)
 * @Date 2017/11/15 14:55
 */

public class ToastUtil {

    private static Toast toast;

    private static View view;

    private ToastUtil() {
    }

    private static void getToast() {
        if (toast == null) {
            toast = new Toast(Utils.getContext());
        }
        if (view == null) {
            view = Toast.makeText(Utils.getContext(), "", Toast.LENGTH_SHORT).getView();
        }
        toast.setView(view);
    }

    public static void showShortToast(CharSequence msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(int resId) {
        showToast(resId, Toast.LENGTH_SHORT);
    }

    public static void showLongToast( CharSequence msg) {
        showToast(msg, Toast.LENGTH_LONG);
    }

    public static void showLongToast(int resId) {
        showToast(resId, Toast.LENGTH_LONG);
    }

    private static void showToast(CharSequence msg, int duration) {
        try {
            getToast();
            toast.setText(msg);
            toast.setDuration(duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    private static void showToast(int resId, int duration) {
        try {
            if (resId == 0) {
                return;
            }
            getToast();
            toast.setText(resId);
            toast.setDuration(duration);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
