package com.mikuwxc.autoreply.wcutil;


import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.mikuwxc.autoreply.wcentity.FileEntity;
import com.mikuwxc.autoreply.wcentity.WechatEntity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class DownLoadWxResFromWxUtil {
    public static void downloadMomentPic(ClassLoader classLoader, WechatEntity wechatEntity, String str, int i) {
        Class findClass = XposedHelpers.findClass(wechatEntity.download_sns_res_pic_class1, classLoader);
        Class findClass2 = XposedHelpers.findClass(wechatEntity.download_sns_res_pic_class2, classLoader);
        Class findClass3 = XposedHelpers.findClass(wechatEntity.download_sns_res_pic_class3, classLoader);
        Class findClass4 = XposedHelpers.findClass(wechatEntity.download_sns_res_pic_class4, classLoader);
        Class findClass5 = XposedHelpers.findClass(wechatEntity.download_sns_res_pic_class5, classLoader);
        Class findClass6 = XposedHelpers.findClass(wechatEntity.download_sns_res_pic_class6, classLoader);
        Class findClass7 = XposedHelpers.findClass(wechatEntity.download_sns_res_pic_class7, classLoader);
        Object callStaticMethod = XposedHelpers.callStaticMethod(findClass, wechatEntity.download_sns_res_pic_method1, new Object[0]);
        Object callStaticMethod2 = XposedHelpers.callStaticMethod(findClass, wechatEntity.download_sns_res_pic_method2, new Object[0]);
        XposedBridge.log("===== READ FROM APP downloadMomentPic [%s]"+ new Object[]{"111"});
        callStaticMethod2 = XposedHelpers.callMethod(callStaticMethod2, wechatEntity.download_sns_res_pic_method3, new Object[]{str});
        XposedBridge.log("===== READ FROM APP downloadMomentPic [%s]"+ new Object[]{"33"});
        callStaticMethod2 = XposedHelpers.callMethod(callStaticMethod2, wechatEntity.download_sns_res_pic_method4, new Object[0]);
        XposedBridge.log("===== READ FROM APP downloadMomentPic [%s]"+ new Object[]{"222"});
        Iterator it = (Iterator) XposedHelpers.callMethod(XposedHelpers.getObjectField(XposedHelpers.getObjectField(callStaticMethod2, wechatEntity.download_sns_res_pic_field1), wechatEntity.download_sns_res_pic_field2), wechatEntity.download_sns_res_pic_method5, new Object[0]);
        while (it.hasNext()) {
            Object next = it.next();
            XposedBridge.log("===== READ FROM APP downloadMomentPic [%s]"+ new Object[]{"333"});
            String str2 = (String) XposedHelpers.getObjectField(next, wechatEntity.download_sns_res_pic_field3);
            str2 = (String) XposedHelpers.callStaticMethod(findClass4, wechatEntity.download_sns_res_pic_method6, new Object[]{Integer.valueOf(2), str2});
            Object callMethod = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(findClass3, wechatEntity.download_sns_res_pic_method7, new Object[0]), wechatEntity.download_sns_res_pic_method8, new Object[]{Integer.valueOf(i)});
            Map map = (Map) XposedHelpers.getObjectField(callStaticMethod, wechatEntity.download_sns_res_pic_field4);
            Object[] r14 = new Object[2];
            r14[0] = XposedHelpers.newInstance(findClass5, new Object[]{next});
            r14[1] = Integer.valueOf(2);
            map.put(str2, XposedHelpers.newInstance(findClass6, r14));
            ((LinkedList) XposedHelpers.getObjectField(callStaticMethod, wechatEntity.download_sns_res_pic_field5)).add(XposedHelpers.newInstance(findClass2, new Object[]{next, Integer.valueOf(2), str2, callMethod, null, null}));
        }
        Object objectField = XposedHelpers.getObjectField(callStaticMethod, "handler");
        XposedBridge.log("===== READ FROM APP downloadMomentPic [%s]"+new Object[]{"444"});
        Runnable runnable = (Runnable) XposedHelpers.newInstance(findClass7, new Object[]{callStaticMethod});
        XposedBridge.log("===== READ FROM APP downloadMomentPic [%s]"+ new Object[]{"555"});
        XposedHelpers.callMethod(objectField, "postDelayed", new Object[]{runnable, Long.valueOf(500)});
        XposedBridge.log("===== READ FROM APP downloadMomentPic [%s]"+new Object[]{"666"});
    }

    public static FileEntity downloadWxFileRes(ClassLoader classLoader, WechatEntity wechatEntity, long j) throws Exception {
        Object callMethod = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_file_class1, classLoader), wechatEntity.download_file_method1, new Object[0]), wechatEntity.download_file_method2, new Object[]{Long.valueOf(j)});
       XposedBridge.log("''''''''''''''''''''''''"+callMethod.toString());
        boolean isChatroom = isChatroom(XposedHelpers.getObjectField(callMethod, wechatEntity.download_file_field1).toString());
        XposedBridge.log("''''''''''''''''''''''''"+isChatroom);
        Object objectField = XposedHelpers.getObjectField(callMethod, wechatEntity.download_file_field2);
        XposedBridge.log("''''''''''''''''''''''''"+objectField.toString());
        JSONObject jsonObject = XmlParseUtil.parseFile(objectField.toString());
        XposedBridge.log("''''''''''''''''''''''''"+jsonObject.toJSONString());
        FileEntity fileEntity = new Gson().fromJson(jsonObject.toJSONString(), FileEntity.class);


        if (isChatroom && XposedHelpers.getIntField(callMethod, wechatEntity.download_file_field3) == 0) {
            objectField = XposedHelpers.getObjectField(callMethod, wechatEntity.download_file_field2);
           // XposedBridge.log("...................."+objectField.toString());
            if (isChatroom && objectField != null) {
                objectField = XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_file_class2, classLoader), wechatEntity.download_file_method3, new Object[]{objectField});
             //   XposedBridge.log("[[[[[[[[[[[["+objectField.toString());
            }
        }
        String obj = XposedHelpers.getObjectField(XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_file_class3, classLoader), wechatEntity.download_file_method4, new Object[]{objectField}), wechatEntity.download_file_field5).toString();
      //  XposedBridge.log("]]]]]]]]]"+obj.toString());
        XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_file_class4, classLoader), wechatEntity.download_file_method5, new Object[]{Long.valueOf(j), objectField, null}).toString();
        objectField = XposedHelpers.newInstance(XposedHelpers.findClass(wechatEntity.download_file_class5, classLoader), new Object[]{obj});
      //  XposedBridge.log("/////////"+objectField.toString());
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_file_class6, classLoader), wechatEntity.download_file_method6, new Object[0]), wechatEntity.download_file_method7, new Object[]{objectField, Integer.valueOf(0)});
        XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_file_class7, classLoader), wechatEntity.download_file_method8, new Object[]{callMethod});
        return fileEntity;
    }

    public static void downloadWxFileRes(ClassLoader classLoader, WechatEntity wechatEntity, long j, boolean z) throws Exception {
        Object callMethod = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_file_class1, classLoader), wechatEntity.download_file_method1, new Object[0]), wechatEntity.download_file_method2, new Object[]{Long.valueOf(j)});
        boolean isChatroom = isChatroom(XposedHelpers.getObjectField(callMethod, wechatEntity.download_file_field1).toString());
        Object objectField = XposedHelpers.getObjectField(callMethod, wechatEntity.download_file_field2);
        if (isChatroom && XposedHelpers.getIntField(callMethod, wechatEntity.download_file_field3) == 0) {
            objectField = XposedHelpers.getObjectField(callMethod, wechatEntity.download_file_field2);
            if (isChatroom && objectField != null) {
                objectField = XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_file_class2, classLoader), wechatEntity.download_file_method3, new Object[]{objectField});
            }
        }
        callMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_file_class3, classLoader), wechatEntity.download_file_method4, new Object[]{objectField});
        String handleNull = handleNull(XposedHelpers.getObjectField(callMethod, wechatEntity.download_bigfile_field8).toString());
        String handleNull2 = handleNull(XposedHelpers.getObjectField(callMethod, wechatEntity.download_bigfile_field9).toString());
        String toLowerCase = handleNull(XposedHelpers.getObjectField(callMethod, wechatEntity.download_bigfile_field10).toString()).toLowerCase();
        String handleNull3 = handleNull(XposedHelpers.getObjectField(callMethod, wechatEntity.download_bigfile_field11).toString());
        String handleNull4 = handleNull(XposedHelpers.getObjectField(callMethod, wechatEntity.download_bigfile_field12).toString());
        String handleNull5 = handleNull(XposedHelpers.getObjectField(callMethod, wechatEntity.download_bigfile_field13).toString());
        XposedBridge.log(handleNull+"--"+handleNull2+"--"+toLowerCase+"--"+handleNull3+"--"+handleNull4+"--"+handleNull5);
        FileIoUtil.setValueToPath(handleNull, false, "/mnt/sdcard/mediaId.txt");
        FileIoUtil.setValueToPath(String.valueOf(j), false, "/mnt/sdcard/msgId.txt");
        Class findClass = XposedHelpers.findClass(wechatEntity.download_bigfile_class8, classLoader);
        callMethod = getCsE(classLoader, wechatEntity, findClass, j, handleNull);
        if (OtherUtils.isEmpty(callMethod)) {
            XposedHelpers.callStaticMethod(findClass, wechatEntity.download_bigfile_method9, new Object[]{Long.valueOf(j), objectField, null});
            objectField = getCsE(classLoader, wechatEntity, findClass, j, handleNull);
        } else {
            objectField = callMethod;
        }
        objectField = XposedHelpers.newInstance(XposedHelpers.findClass(wechatEntity.download_bigfile_class9, classLoader), new Object[]{objectField, handleNull5, handleNull3, handleNull2, toLowerCase, handleNull4});
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_bigfile_class10, classLoader), wechatEntity.download_bigfile_method10, new Object[0]), wechatEntity.download_bigfile_method11, new Object[]{objectField, Integer.valueOf(0)});
    }

    public static String downloadWxPicRes(ClassLoader classLoader, WechatEntity wechatEntity, String str, String str2, String str3) throws Throwable {
        Object callStaticMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_pic_class1, classLoader), wechatEntity.download_pic_method1, new Object[0]);
        Object callStaticMethod2 = XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_pic_class2, classLoader), wechatEntity.download_pic_method2, new Object[0]);
        long longField = XposedHelpers.getLongField(XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_pic_class2, classLoader), wechatEntity.download_pic_method3, new Object[0]), wechatEntity.download_pic_method4, new Object[]{Long.valueOf(Long.parseLong(str))}), wechatEntity.download_pic_field2);
        long parseLong = Long.parseLong(str2);
        XposedHelpers.callMethod(callStaticMethod2, wechatEntity.download_pic_method5, new Object[]{Long.valueOf(longField), Long.valueOf(parseLong), Integer.valueOf(0), Integer.valueOf(10000), Integer.valueOf(2130837945), callStaticMethod, Integer.valueOf(0)});
        return XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_pic_class2, classLoader), wechatEntity.download_pic_method3, new Object[0]), wechatEntity.download_pic_method6, new Object[]{str3, Boolean.valueOf(true)}).toString();
    }

    public static String downloadWxVideoRes(ClassLoader classLoader, WechatEntity wechatEntity, String str) throws Throwable {
        Object newInstance = XposedHelpers.newInstance(XposedHelpers.findClass(wechatEntity.download_video_class1, classLoader), new Object[]{str, Boolean.valueOf(true)});
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_video_class2, classLoader), wechatEntity.download_video_method1, new Object[0]), wechatEntity.download_video_method2, new Object[]{newInstance, Integer.valueOf(0)});
        XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_video_class3, classLoader), wechatEntity.download_video_method3, new Object[]{str});
        return XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_video_class4, classLoader), wechatEntity.download_video_method4, new Object[]{str}).toString();
    }

    public static String downloadWxVoiceRes(ClassLoader classLoader, WechatEntity wechatEntity, String str) throws Throwable {
        return XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_voice_class1, classLoader), wechatEntity.download_voice_method1, new Object[]{str, Boolean.valueOf(false)}).toString();
    }

    public static Object getCsE(ClassLoader classLoader, WechatEntity wechatEntity, Class cls, long j, String str) {
        Object callMethod = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.download_bigfile_class7, classLoader), wechatEntity.download_bigfile_method6, new Object[0]), wechatEntity.download_bigfile_method7, new Object[]{Long.valueOf(j)});
        if (callMethod != null) {
            return callMethod;
        }
        return XposedHelpers.callStaticMethod(cls, wechatEntity.download_bigfile_method8, new Object[]{str});
    }

    public static String handleNull(String str) {
        return str == null ? "" : str;
    }

    public static boolean isChatroom(String str) {
        return (str == null || str.length() <= 0) ? false : str.endsWith("@chatroom");
    }





}