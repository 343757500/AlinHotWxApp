package com.mikuwxc.autoreply.wcutil;


import com.mikuwxc.autoreply.wcentity.WechatEntity;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class SendComment {
    public static void sendComment(ClassLoader classLoader, WechatEntity wechatEntity, long j, String str, String str2) {
        Class findClass = XposedHelpers.findClass(wechatEntity.send_comment_class1, classLoader);
        Class findClass2 = XposedHelpers.findClass(wechatEntity.send_comment_class2, classLoader);
        Class findClass3 = XposedHelpers.findClass(wechatEntity.send_comment_class3, classLoader);
        Class findClass4 = XposedHelpers.findClass(wechatEntity.send_comment_class4, classLoader);
        Class findClass5 = XposedHelpers.findClass(wechatEntity.send_comment_class5, classLoader);
        Class findClass6 = XposedHelpers.findClass(wechatEntity.send_comment_class6, classLoader);
        Class findClass7 = XposedHelpers.findClass(wechatEntity.send_comment_class7, classLoader);
        Class findClass8 = XposedHelpers.findClass(wechatEntity.send_comment_class8, classLoader);
        Object callMethod = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(findClass8, wechatEntity.send_comment_method1, new Object[]{findClass7}), wechatEntity.send_comment_method2, new Object[]{str});
        Object newInstance = XposedHelpers.newInstance(findClass, new Object[0]);
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field1, str2);
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field2, Integer.valueOf((int) (((Long) XposedHelpers.callStaticMethod(findClass4, wechatEntity.send_comment_method3, new Object[0])).longValue() / 1000)));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field3, XposedHelpers.callStaticMethod(findClass5, wechatEntity.send_comment_method4, new Object[0]));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field4, XposedHelpers.callStaticMethod(findClass5, wechatEntity.send_comment_method5, new Object[0]));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field5, Integer.valueOf(0));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field6, callMethod);
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field7, str);
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field8, Integer.valueOf(2));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field9, Integer.valueOf(0));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field10, Integer.valueOf(0));
        Object newInstance2 = XposedHelpers.newInstance(findClass, new Object[0]);
        XposedHelpers.setObjectField(newInstance2, wechatEntity.send_comment_field11, null);
        Object newInstance3 = XposedHelpers.newInstance(findClass2, new Object[0]);
        XposedHelpers.setObjectField(newInstance3, wechatEntity.send_comment_field12, Long.valueOf(j));
        XposedHelpers.setObjectField(newInstance3, wechatEntity.send_comment_field13, newInstance);
        XposedHelpers.setObjectField(newInstance3, wechatEntity.send_comment_field14, newInstance2);
        newInstance2 = XposedHelpers.callStaticMethod(findClass4, wechatEntity.send_comment_method6, new Object[0]);
        new StringBuilder().append(newInstance2);
        Object[] objArr = new Object[2];
        objArr[0] = newInstance3;
        objArr[1] = (String) XposedHelpers.callStaticMethod(findClass6, wechatEntity.send_comment_method7, new Object[]{str2.toString().getBytes()});
        newInstance2 = XposedHelpers.newInstance(findClass3, objArr);
        XposedHelpers.callMethod(XposedHelpers.getObjectField(XposedHelpers.callStaticMethod(findClass8, wechatEntity.send_comment_method8, new Object[0]), wechatEntity.send_comment_field15), wechatEntity.send_comment_method9, new Object[]{newInstance2, Integer.valueOf(0)});
    }

    public static void sendCommentToOther(ClassLoader classLoader, WechatEntity wechatEntity, long j, String str, String str2, int i, String str3) {
        Class findClass = XposedHelpers.findClass(wechatEntity.send_comment_class1, classLoader);
        Class findClass2 = XposedHelpers.findClass(wechatEntity.send_comment_class2, classLoader);
        Class findClass3 = XposedHelpers.findClass(wechatEntity.send_comment_class3, classLoader);
        Class findClass4 = XposedHelpers.findClass(wechatEntity.send_comment_class4, classLoader);
        Class findClass5 = XposedHelpers.findClass(wechatEntity.send_comment_class5, classLoader);
        Class findClass6 = XposedHelpers.findClass(wechatEntity.send_comment_class6, classLoader);
        Class findClass7 = XposedHelpers.findClass(wechatEntity.send_comment_class7, classLoader);
        Class findClass8 = XposedHelpers.findClass(wechatEntity.send_comment_class8, classLoader);
        Object callMethod = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(findClass8, wechatEntity.send_comment_method1, new Object[]{findClass7}), wechatEntity.send_comment_method2, new Object[]{str});
        Object newInstance = XposedHelpers.newInstance(findClass, new Object[0]);
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field1, str3);
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field2, Integer.valueOf((int) (((Long) XposedHelpers.callStaticMethod(findClass4, wechatEntity.send_comment_method3, new Object[0])).longValue() / 1000)));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field3, XposedHelpers.callStaticMethod(findClass5, wechatEntity.send_comment_method4, new Object[0]));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field4, XposedHelpers.callStaticMethod(findClass5, wechatEntity.send_comment_method5, new Object[0]));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field5, Integer.valueOf(0));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field6, callMethod);
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field7, str);
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field8, Integer.valueOf(2));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field9, Integer.valueOf(0));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field10, Integer.valueOf(i));
        Object newInstance2 = XposedHelpers.newInstance(findClass, new Object[0]);
        XposedHelpers.setObjectField(newInstance2, wechatEntity.send_comment_field11, str2);
        Object newInstance3 = XposedHelpers.newInstance(findClass2, new Object[0]);
        XposedHelpers.setObjectField(newInstance3, wechatEntity.send_comment_field12, Long.valueOf(j));
        XposedHelpers.setObjectField(newInstance3, wechatEntity.send_comment_field13, newInstance);
        XposedHelpers.setObjectField(newInstance3, wechatEntity.send_comment_field14, newInstance2);
        newInstance2 = XposedHelpers.callStaticMethod(findClass4, wechatEntity.send_comment_method6, new Object[0]);
        new StringBuilder().append(newInstance2);
        Object[] objArr = new Object[2];
        objArr[0] = newInstance3;
        objArr[1] = (String) XposedHelpers.callStaticMethod(findClass6, wechatEntity.send_comment_method7, new Object[]{str2.toString().getBytes()});
        newInstance2 = XposedHelpers.newInstance(findClass3, objArr);
        XposedHelpers.callMethod(XposedHelpers.getObjectField(XposedHelpers.callStaticMethod(findClass8, wechatEntity.send_comment_method8, new Object[0]), wechatEntity.send_comment_field15), wechatEntity.send_comment_method9, new Object[]{newInstance2, Integer.valueOf(0)});
    }

    public static void sendLikeToMoment(ClassLoader classLoader, WechatEntity wechatEntity, long j, String str) {
        XposedBridge.log("TTTTTTTTTTTTTTTTT");
        Class findClass = XposedHelpers.findClass(wechatEntity.send_comment_class1, classLoader);
        XposedBridge.log("TTTTTTTTTTTTTTTTT"+findClass);
        Class findClass2 = XposedHelpers.findClass(wechatEntity.send_comment_class2, classLoader);
        XposedBridge.log("TTTTTTTTTTTTTTTTT"+findClass2);
        Class findClass3 = XposedHelpers.findClass(wechatEntity.send_comment_class3, classLoader);
        XposedBridge.log("TTTTTTTTTTTTTTTTT"+findClass3);
        Class findClass4 = XposedHelpers.findClass(wechatEntity.send_comment_class4, classLoader);
        XposedBridge.log("TTTTTTTTTTTTTTTTT"+findClass4);
        Class findClass5 = XposedHelpers.findClass(wechatEntity.send_comment_class5, classLoader);
        XposedBridge.log("TTTTTTTTTTTTTTTTT"+findClass5);
        Class findClass6 = XposedHelpers.findClass(wechatEntity.send_comment_class6, classLoader);
        Class findClass7 = XposedHelpers.findClass(wechatEntity.send_comment_class7, classLoader);
        Class findClass8 = XposedHelpers.findClass(wechatEntity.send_comment_class8, classLoader);
        Object callMethod = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(findClass8, wechatEntity.send_comment_method1, new Object[]{findClass7}), wechatEntity.send_comment_method2, new Object[]{str});
        Object newInstance = XposedHelpers.newInstance(findClass, new Object[0]);
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field1, "");
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field2, Integer.valueOf((int) (((Long) XposedHelpers.callStaticMethod(findClass4, wechatEntity.send_comment_method3, new Object[0])).longValue() / 1000)));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field3, XposedHelpers.callStaticMethod(findClass5, wechatEntity.send_comment_method4, new Object[0]));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field4, XposedHelpers.callStaticMethod(findClass5, wechatEntity.send_comment_method5, new Object[0]));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field5, Integer.valueOf(0));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field6, callMethod);
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field7, str);
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field8, Integer.valueOf(1));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field9, Integer.valueOf(0));
        XposedHelpers.setObjectField(newInstance, wechatEntity.send_comment_field10, Integer.valueOf(0));
        Object newInstance2 = XposedHelpers.newInstance(findClass, new Object[0]);
        XposedHelpers.setObjectField(newInstance2, wechatEntity.send_comment_field11, null);
        Object newInstance3 = XposedHelpers.newInstance(findClass2, new Object[0]);
        XposedHelpers.setObjectField(newInstance3, wechatEntity.send_comment_field12, Long.valueOf(j));
        XposedHelpers.setObjectField(newInstance3, wechatEntity.send_comment_field13, newInstance);
        XposedHelpers.setObjectField(newInstance3, wechatEntity.send_comment_field14, newInstance2);
        newInstance2 = XposedHelpers.callStaticMethod(findClass4, wechatEntity.send_comment_method6, new Object[0]);
        new StringBuilder().append(newInstance2);
        Object[] objArr = new Object[2];
        objArr[0] = newInstance3;
        objArr[1] = (String) XposedHelpers.callStaticMethod(findClass6, wechatEntity.send_comment_method7, new Object[]{str.toString().getBytes()});
        newInstance2 = XposedHelpers.newInstance(findClass3, objArr);
        XposedHelpers.callMethod(XposedHelpers.getObjectField(XposedHelpers.callStaticMethod(findClass8, wechatEntity.send_comment_method8, new Object[0]), wechatEntity.send_comment_field15), wechatEntity.send_comment_method9, new Object[]{newInstance2, Integer.valueOf(0)});
    }
}