package com.mikuwxc.autoreply.wcutil;


import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wx.WechatDb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.robv.android.xposed.XposedHelpers;

public class LabelUtil {
    public static void addLabel(ClassLoader classLoader, WechatEntity wechatEntity, String str) throws Exception {
        Class loadClass = classLoader.loadClass(wechatEntity.manager_add_label_class1);
        Object callStaticMethod = XposedHelpers.callStaticMethod(loadClass, wechatEntity.manager_add_label_method1, new Object[0]);
        Object newInstance = XposedHelpers.newInstance(classLoader.loadClass(wechatEntity.manager_add_label_class2), new Object[]{str});
        XposedHelpers.callMethod(callStaticMethod, wechatEntity.manager_add_label_method2, new Object[]{newInstance, Integer.valueOf(0)});
        refreashLabel(classLoader, wechatEntity, loadClass, callStaticMethod);
    }

    public static void delLabel(ClassLoader classLoader, WechatEntity wechatEntity, String str) throws Exception {
        Class loadClass = classLoader.loadClass(wechatEntity.manager_del_label_class1);
        Object callStaticMethod = XposedHelpers.callStaticMethod(loadClass, wechatEntity.manager_del_label_method1, new Object[0]);
        Object newInstance = XposedHelpers.newInstance(classLoader.loadClass(wechatEntity.manager_del_label_class2), new Object[]{str});
        XposedHelpers.callMethod(callStaticMethod, wechatEntity.manager_del_label_method2, new Object[]{newInstance, Integer.valueOf(0)});
        refreashLabel(classLoader, wechatEntity, loadClass, callStaticMethod);
    }

    private static void refreashLabel(ClassLoader classLoader, final WechatEntity wechatEntity, Class<?> cls, final Object obj) throws Exception {
        Class loadClass = classLoader.loadClass(wechatEntity.manager_refreash_label_class1);
        Object callStaticMethod = XposedHelpers.callStaticMethod(cls, wechatEntity.manager_refreash_label_method1, new Object[0]);
        final Object newInstance = XposedHelpers.newInstance(loadClass, new Object[0]);
        Runnable runnable = new Runnable() {
            public void run() {
                XposedHelpers.callMethod(obj, wechatEntity.manager_refreash_label_method2, new Object[]{newInstance, Integer.valueOf(0)});
            }
        };
        XposedHelpers.callMethod(callStaticMethod, wechatEntity.manager_refreash_label_method3, new Object[]{runnable});
    }

    private static void updateContactLabel(ClassLoader classLoader, WechatEntity wechatEntity, String str, List<String> list) throws Exception {
        new ArrayList().addAll(list);
        Class loadClass = classLoader.loadClass(wechatEntity.manager_update_contact_label_class1);
        ArrayList arrayList = (ArrayList) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(classLoader.loadClass(wechatEntity.manager_update_contact_label_class2), wechatEntity.manager_update_contact_label_method1, new Object[0]), wechatEntity.manager_update_contact_label_method2, new Object[]{loadClass});
        String obj = XposedHelpers.callStaticMethod(loadClass, wechatEntity.manager_update_contact_label_method3, new Object[]{arrayList}).toString();
        Object newInstance = classLoader.loadClass(wechatEntity.manager_update_contact_label_class3).newInstance();
        XposedHelpers.setObjectField(newInstance, wechatEntity.manager_update_contact_label_field1, obj);
        XposedHelpers.setObjectField(newInstance, wechatEntity.manager_update_contact_label_field2, str);
        Class loadClass2 = classLoader.loadClass(wechatEntity.manager_update_contact_label_class4);
        new LinkedList().add(newInstance);
        Object newInstance2 = XposedHelpers.newInstance(loadClass2, new Object[]{loadClass2});
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(classLoader.loadClass(wechatEntity.manager_update_contact_label_class5), wechatEntity.manager_update_contact_label_method6, new Object[0]), wechatEntity.manager_update_contact_label_method7, new Object[]{newInstance2, Integer.valueOf(0)});
    }

    public static void updateContactLabel(ClassLoader classLoader, WechatEntity wechatEntity, String str, List<String> list, boolean z) throws Exception {
        for (int i = 0; i < 30; i++) {
            HashMap selectLabel = WechatDb.getInstance().selectLabel();
            LinkedList linkedList = new LinkedList();
         /*   for (Entry value : selectLabel.entrySet()) {
                linkedList.add((String) value.getValue());
            }*/
            int i2 = 0;
            for (String str2 : list) {
                int i3;
                Iterator it = linkedList.iterator();
                while (it.hasNext()) {
                    if (((String) it.next()).equals(str2)) {
                        i3 = 1;
                        break;
                    }
                }
                i3 = 0;
                if (i3 == 0) {
                    addLabel(classLoader, wechatEntity, str2);
                    i2 = 1;
                }
            }
            Thread.sleep(1000);
            if (i2 == 0) {
                break;
            }
        }
        boolean b = new ArrayList().addAll(list);
        Class loadClass = classLoader.loadClass(wechatEntity.manager_update_contact_label_class1);
        ArrayList arrayList = (ArrayList) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(classLoader.loadClass(wechatEntity.manager_update_contact_label_class2), wechatEntity.manager_update_contact_label_method1, new Object[0]), wechatEntity.manager_update_contact_label_method2, new Object[]{b});
        String str22 = XposedHelpers.callStaticMethod(loadClass, wechatEntity.manager_update_contact_label_method3, new Object[]{arrayList}).toString();
        Object newInstance = classLoader.loadClass(wechatEntity.manager_update_contact_label_class3).newInstance();
        XposedHelpers.setObjectField(newInstance, wechatEntity.manager_update_contact_label_field1, str22);
        XposedHelpers.setObjectField(newInstance, wechatEntity.manager_update_contact_label_field2, str);
        Class loadClass2 = classLoader.loadClass(wechatEntity.manager_update_contact_label_class4);
        new LinkedList().add(newInstance);
        Object newInstance2 = XposedHelpers.newInstance(loadClass2, new Object[]{str22});
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(classLoader.loadClass(wechatEntity.manager_update_contact_label_class5), wechatEntity.manager_update_contact_label_method6, new Object[0]), wechatEntity.manager_update_contact_label_method7, new Object[]{newInstance2, Integer.valueOf(0)});
    }

    public static void updateLabel(ClassLoader classLoader, WechatEntity wechatEntity, int i, String str) throws Exception {
        Class loadClass = classLoader.loadClass(wechatEntity.manager_update_label_class1);
        Object callStaticMethod = XposedHelpers.callStaticMethod(loadClass, wechatEntity.manager_update_label_method1, new Object[0]);
        Object newInstance = XposedHelpers.newInstance(classLoader.loadClass(wechatEntity.manager_update_label_class2), new Object[]{Integer.valueOf(i), str});
        XposedHelpers.callMethod(callStaticMethod, wechatEntity.manager_update_label_method2, new Object[]{newInstance, Integer.valueOf(0)});
        refreashLabel(classLoader, wechatEntity, loadClass, callStaticMethod);
    }
}