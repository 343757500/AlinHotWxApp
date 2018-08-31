package com.mikuwxc.autoreply.common.util;

import com.mikuwxc.autoreply.modle.Event;

import de.greenrobot.event.EventBus;

/**
 * Created by yongxiong on 2017/9/18.
 */

public class EventBusUtil {
    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    public static void sendEvent(Event event) {
        EventBus.getDefault().post(event);
    }

    public static void sendStickyEvent(Event event) {
        EventBus.getDefault().postSticky(event);
    }

    // 其他
}
