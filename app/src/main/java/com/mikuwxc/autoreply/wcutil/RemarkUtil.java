package com.mikuwxc.autoreply.wcutil;

import com.mikuwxc.autoreply.wcentity.WechatEntity;

import de.robv.android.xposed.XposedHelpers;

public class RemarkUtil {
    public static void updateChatroomName(ClassLoader classLoader, WechatEntity wechatEntity, String str, String str2) {
        Object newInstance = XposedHelpers.newInstance(XposedHelpers.findClass(wechatEntity.update_chatroom_name_class1, classLoader), new Object[0]);
        Class findClass = XposedHelpers.findClass(wechatEntity.update_chatroom_name_class2, classLoader);
        XposedHelpers.setObjectField(newInstance, wechatEntity.update_chatroom_name_field1, XposedHelpers.callMethod(XposedHelpers.newInstance(findClass, new Object[0]), wechatEntity.update_chatroom_name_method1, new Object[]{str}));
        XposedHelpers.setObjectField(newInstance, wechatEntity.update_chatroom_name_field2, XposedHelpers.callMethod(XposedHelpers.newInstance(findClass, new Object[0]), wechatEntity.update_chatroom_name_method1, new Object[]{str2}));
        for (Class cls : XposedHelpers.findClass(wechatEntity.update_chatroom_name_class3, classLoader).getDeclaredClasses()) {
            if (cls.getSimpleName().equals(wechatEntity.update_chatroom_name_classname_1)) {
                Object newInstance2 = XposedHelpers.newInstance(cls, new Object[]{Integer.valueOf(27), newInstance});
                XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.update_chatroom_name_class4, classLoader), wechatEntity.update_chatroom_name_method2, new Object[0]);
                XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.update_chatroom_name_class5, classLoader), wechatEntity.update_chatroom_name_method3, new Object[0]), wechatEntity.update_chatroom_name_method4, new Object[]{newInstance2});
                return;
            }
        }
    }

    public static void updateContactPhone(ClassLoader classLoader, WechatEntity wechatEntity, String str, String str2) {
        Object callStaticMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.update_phone_class1, classLoader), wechatEntity.update_phone_method1, new Object[0]);
        Object obj = null;
        if (!wechatEntity.update_phone_method2.equals("")) {
            obj = XposedHelpers.callMethod(callStaticMethod, wechatEntity.update_phone_method2, new Object[0]);
        }
        if (obj == null) {
            obj = XposedHelpers.callMethod(callStaticMethod, wechatEntity.update_phone_method3, new Object[]{str});
        } else {
            obj = XposedHelpers.callMethod(obj, wechatEntity.update_phone_method3, new Object[]{str});
        }
        XposedHelpers.callMethod(obj, wechatEntity.update_phone_method4, new Object[]{str2});
        XposedHelpers.callMethod(callStaticMethod, wechatEntity.update_phone_method5, new Object[]{obj});
    }

    public static void updateContactRemark(ClassLoader classLoader, WechatEntity wechatEntity, String str, String str2) throws Exception {
        Object callStaticMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.update_remark_class1, classLoader), wechatEntity.update_remark_method1, new Object[0]);
        Object obj = null;
        if (!wechatEntity.update_remark_method2.equals("")) {
            obj = XposedHelpers.callMethod(callStaticMethod, wechatEntity.update_remark_method2, new Object[0]);
        }
        if (obj == null) {
            obj = XposedHelpers.callMethod(callStaticMethod, wechatEntity.update_remark_method3, new Object[]{str});
        } else {
            obj = XposedHelpers.callMethod(obj, wechatEntity.update_remark_method3, new Object[]{str});
        }
        XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.update_remark_class2, classLoader), wechatEntity.update_remark_method4, new Object[]{obj, str2});
    }
}