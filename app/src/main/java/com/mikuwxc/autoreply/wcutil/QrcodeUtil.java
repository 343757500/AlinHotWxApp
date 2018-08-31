package com.mikuwxc.autoreply.wcutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


import com.mikuwxc.autoreply.wcentity.WechatEntity;

import de.robv.android.xposed.XposedHelpers;

public class QrcodeUtil {
    public static void createReceiveMallBm(ClassLoader classLoader, WechatEntity wechatEntity, Context context, double d) throws Exception {
        Object newInstance = XposedHelpers.newInstance(classLoader.loadClass(wechatEntity.create_receive_money_class2), new Object[]{Double.valueOf(d), "1", ""});
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(classLoader.loadClass(wechatEntity.create_receive_money_class3), wechatEntity.create_receive_money_method2, new Object[0]), wechatEntity.create_receive_money_method3, new Object[]{newInstance, Integer.valueOf(0)});
    }

    public static Bitmap drawBm(ClassLoader classLoader, WechatEntity wechatEntity, Context context, String str, String str2, int i) throws Exception {
        Bitmap bitmap;
        int i2 = (int) (((float) i) * 0.26f);
        int i3 = (int) (((float) i) * 0.22f);
        int i4 = (int) (((float) i) * 0.076f);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap createBitmap = Bitmap.createBitmap(i2, i2, Config.ARGB_8888);
        i3 = (i2 - i3) / 2;
        new Canvas(createBitmap).drawBitmap((Bitmap) XposedHelpers.callStaticMethod(classLoader.loadClass(wechatEntity.download_receive_money_class1), wechatEntity.download_receive_money_method1, new Object[]{str2, Integer.valueOf(i3), Integer.valueOf(i3), Integer.valueOf((int) (((float) i3) * 0.06f))}), null, new Rect(i3, i3, i2 - i3, i2 - i3), paint);
        byte[] bArr = new byte[40000];
        int[] iArr = new int[2];
        Class loadClass = classLoader.loadClass(wechatEntity.download_receive_money_class2);
        i4 = ((Integer) XposedHelpers.callStaticMethod(loadClass, wechatEntity.download_receive_money_method2, new Object[]{bArr, iArr, str, Integer.valueOf(0), Integer.valueOf(3), "UTF-8"})).intValue();
        XposedHelpers.callStaticMethod(loadClass, wechatEntity.download_receive_money_method3, new Object[0]);
        if (i4 > 0) {
            bitmap = (Bitmap) XposedHelpers.callStaticMethod(classLoader.loadClass(wechatEntity.download_receive_money_class3), wechatEntity.download_receive_money_method4, new Object[]{context, createBitmap, bArr, iArr});
        } else {
            bitmap = null;
        }
        createBitmap.recycle();
        return bitmap;
    }
}