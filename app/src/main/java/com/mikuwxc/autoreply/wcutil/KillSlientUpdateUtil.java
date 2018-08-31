package com.mikuwxc.autoreply.wcutil;

import com.mikuwxc.autoreply.wcentity.WechatEntity;

import de.robv.android.xposed.XposedHelpers;

public class KillSlientUpdateUtil {
    public static void killSlientInstall(ClassLoader paramClassLoader, WechatEntity paramWechatEntity) {

        Object localObject1 = XposedHelpers.findClass(paramWechatEntity.kill_slient_install_class1, paramClassLoader);
        Object localObject2 = XposedHelpers.findClass(paramWechatEntity.kill_slient_install_class2, paramClassLoader);
        Class localClass = XposedHelpers.findClass(paramWechatEntity.kill_slient_install_class3, paramClassLoader);
        Class paramClassLoader1 = XposedHelpers.findClass(paramWechatEntity.kill_slient_install_class4, paramClassLoader);
        localObject2 = XposedHelpers.newInstance((Class)localObject2, new Object[0]);
        XposedHelpers.setObjectField(localObject2, paramWechatEntity.kill_slient_install_field1, Integer.valueOf(35));
        XposedHelpers.setObjectField(localObject2, paramWechatEntity.kill_slient_install_field2, Integer.valueOf(1));
        localObject1 = XposedHelpers.callMethod(XposedHelpers.callStaticMethod((Class)localObject1, paramWechatEntity.kill_slient_install_method4, new Object[] { localClass }), paramWechatEntity.kill_slient_install_method5, new Object[0]);
        Class paramClassLoader2 = (Class) XposedHelpers.newInstance(paramClassLoader1, new Object[] { Integer.valueOf(23), localObject2 });
        XposedHelpers.callMethod(localObject1, paramWechatEntity.kill_slient_install_method6, new Object[] { paramClassLoader2 });
    }
}