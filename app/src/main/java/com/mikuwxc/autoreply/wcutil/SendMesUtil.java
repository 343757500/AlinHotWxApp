package com.mikuwxc.autoreply.wcutil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mikuwxc.autoreply.wcentity.MessageEntity;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wx.WechatDb;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class SendMesUtil
{
    public static boolean revokeMessage(ClassLoader paramClassLoader, WechatEntity paramWechatEntity, long paramLong)
    {
        try
        {
            Object localObject1 = WechatDb.getInstance().selectMsgByMsgId(paramLong);
            Object localObject2 = XposedHelpers.findClass(paramWechatEntity.revoke_message_class1, paramClassLoader);
            Class localClass = XposedHelpers.findClass(paramWechatEntity.revoke_message_class2, paramClassLoader);
            localObject2 = XposedHelpers.newInstance((Class)localObject2, new Object[] { ((MessageEntity)localObject1).getTalker() });
            XposedHelpers.setObjectField(localObject2, "field_msgId", Long.valueOf(((MessageEntity)localObject1).getMsgId()));
            XposedHelpers.setObjectField(localObject2, "field_createTime", Long.valueOf(((MessageEntity)localObject1).getCreateTime()));
            XposedHelpers.setObjectField(localObject2, "field_msgSvrId", Long.valueOf(Long.parseLong(((MessageEntity)localObject1).getMsgSvrId())));
            XposedHelpers.setObjectField(localObject2, "field_talker", ((MessageEntity)localObject1).getTalker());
            XposedHelpers.setObjectField(localObject2, paramWechatEntity.revoke_message_field1, Integer.valueOf(0));
            XposedHelpers.setObjectField(localObject2, "field_type", Integer.valueOf(((MessageEntity)localObject1).getType()));
            XposedHelpers.setObjectField(localObject2, "field_imgPath", ((MessageEntity)localObject1).getImgPath());
            XposedHelpers.setObjectField(localObject2, "field_content", ((MessageEntity)localObject1).getContent());
            localObject1 = XposedHelpers.newInstance(localClass, new Object[] { localObject2, "����������������" });
            XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(paramWechatEntity.revoke_message_class3, paramClassLoader), paramWechatEntity.revoke_message_method1, new Object[0]), paramWechatEntity.revoke_message_method2, new Object[] { localObject1, Integer.valueOf(0) });
            return true;
        }
        catch (Exception e)
        {
            Logger.e(e, "===== REVOKE MESSAGE ===== ERROR!!!", new Object[0]);
        }
        return false;
    }

    public static void saveMsgId(String paramString, Long paramLong)
    {
        MsgTreeUtil.getMsgTree().put(paramString, paramLong);
    }

    public static boolean sendAmr(ClassLoader paramClassLoader, WechatEntity paramWechatEntity, Activity paramActivity, String paramString1, String paramString2, int paramInt, long paramLong)
            throws Exception
    {
        try
        {
            XposedBridge.log("aaaaaaaaaaaaaaaaaaaaa");
            int i = (int)new File(paramString2).length();
            XposedBridge.log("bbbbbbbbbbbbbbbbbbbbbb");
            Class localClass = XposedHelpers.findClass(paramWechatEntity.sendAmr_class_1, paramClassLoader);
            XposedBridge.log("cccccccccccccccccccccc");
            paramString1 = XposedHelpers.callStaticMethod(localClass, paramWechatEntity.sendAmr_method_1, new Object[] { paramString1 }).toString();
            XposedBridge.log("ddddddddddddddddddddddddddddd");
            HandleUtil.m19(paramString2, XposedHelpers.callStaticMethod(localClass, paramWechatEntity.sendAmr_method_2, new Object[] { paramString1, Boolean.valueOf(false) }).toString());
            XposedBridge.log("eeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            XposedHelpers.callStaticMethod(localClass, paramWechatEntity.sendAmr_method_3, new Object[] { paramString1, Integer.valueOf(i), Integer.valueOf(0) });
            XposedBridge.log("ffffffffffffffffffffffffffffff");
            WechatDb.getInstance().updateVoiceInfo(i, paramInt, paramString1);
            XposedBridge.log("ggggggggggggggggggggggggggggg");
            paramActivity.runOnUiThread(new AmrRun(paramClassLoader, paramWechatEntity, paramString1));
            saveMsgId(paramString1, Long.valueOf(paramLong));
            return true;
        }
        catch (Throwable e)
        {
            XposedBridge.log("===== SEND AMR ===== ERROR!!!" +e.toString());

        }
        return false;
    }

/*    public static boolean sendAtInChatroom(ClassLoader paramClassLoader, WechatEntity paramWechatEntity, String paramString1, String paramString2, String paramString3, long paramLong)
    {
        try
        {
            HashMap localHashMap = new HashMap();
            localHashMap.put("atuserlist", "<![CDATA[" + paramString2 + "]]>");
            paramString1 = XposedHelpers.newInstance(XposedHelpers.findClass(paramWechatEntity.sendTxt_class_2, paramClassLoader), new Object[] { paramString1, paramString3, Integer.valueOf(1), Integer.valueOf(291), localHashMap });
            long l = XposedHelpers.getLongField(paramString1, paramWechatEntity.sendTxt_filed_msgId);
            XposedHelpers.callMethod(XposedHelpers.getStaticObjectField(XposedHelpers.findClass(paramWechatEntity.sendTxt_class_1, paramClassLoader), paramWechatEntity.sendTxt_field_1), paramWechatEntity.sendTxt_method_1, new Object[] { paramString1 });
            saveMsgId(String.valueOf(l), Long.valueOf(paramLong));
            return true;
        }
        catch (Exception paramClassLoader)
        {
            Logger.e(paramClassLoader, "===== SEND AT ===== ERROR!!!", new Object[0]);
        }
        return false;
    }*/

    public static void sendFile(ClassLoader paramClassLoader, WechatEntity paramWechatEntity, String paramString1, String paramString2, long paramLong)
    {
        Object localObject2 = XposedHelpers.newInstance(XposedHelpers.findClass(paramWechatEntity.sendFile_class1, paramClassLoader), new Object[0]);
        XposedHelpers.callMethod(localObject2, paramWechatEntity.sendFile_method1, new Object[] { paramString2 });
        Object localObject1 = XposedHelpers.newInstance(XposedHelpers.findClass(paramWechatEntity.sendFile_class2, paramClassLoader), new Object[0]);
        XposedHelpers.setObjectField(localObject1, paramWechatEntity.sendFile_field1, localObject2);
        localObject2 = new File(paramString2);
        XposedHelpers.setObjectField(localObject1, paramWechatEntity.sendFile_field2, ((File)localObject2).getName());
        Class localClass = XposedHelpers.findClass(paramWechatEntity.sendFile_class3, paramClassLoader);
        XposedHelpers.setObjectField(localObject1, paramWechatEntity.sendFile_field3, XposedHelpers.callStaticMethod(localClass, paramWechatEntity.sendFile_method2, new Object[] { Long.valueOf(((File)localObject2).length()) }));
        XposedHelpers.callStaticMethod(XposedHelpers.findClass(paramWechatEntity.sendFile_class4, paramClassLoader), paramWechatEntity.sendFile_method3, new Object[] { localObject1, "", "", paramString1, Integer.valueOf(4), null });
        saveMsgId(paramString2, Long.valueOf(paramLong));
    }

    public static void sendLink(ClassLoader paramClassLoader, WechatEntity paramWechatEntity, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, long paramLong)
            throws Exception
    {
        Object localObject = XposedHelpers.newInstance(XposedHelpers.findClass(paramWechatEntity.send_link_class1, paramClassLoader), new Object[] { paramString2 });
        XposedHelpers.setObjectField(localObject, paramWechatEntity.send_link_field0, String.valueOf(paramLong));
        paramString2 = (String) XposedHelpers.newInstance(XposedHelpers.findClass(paramWechatEntity.send_link_class2, paramClassLoader), new Object[0]);
        XposedHelpers.setObjectField(paramString2, paramWechatEntity.send_link_field1, localObject);
        XposedHelpers.setObjectField(paramString2, paramWechatEntity.send_link_field2, paramString3);
        XposedHelpers.setObjectField(paramString2, paramWechatEntity.send_link_field3, paramString4);
        Bitmap paramString6 = BitmapFactory.decodeFile(paramString5);
        ByteArrayOutputStream paramString7 = new ByteArrayOutputStream();
        paramString6.compress(Bitmap.CompressFormat.JPEG, 85, paramString7);
        XposedHelpers.setObjectField(paramString2, paramWechatEntity.send_link_field4, paramString6);
        XposedHelpers.callStaticMethod(XposedHelpers.findClass(paramWechatEntity.send_link_class3, paramClassLoader), paramWechatEntity.send_link_method1, new Object[] { paramString2, "", "", paramString1, Integer.valueOf(4), null });
        saveMsgId(String.valueOf(paramLong), Long.valueOf(paramLong));
    }

    public static boolean sendPic(ClassLoader paramClassLoader, WechatEntity paramWechatEntity, String paramString1, String paramString2, String paramString3, long paramLong)
            throws Exception
    {
        try
        {
            XposedBridge.log("zzzzzzzzzzzzzzzzzzzzz");
            Object[] arrayOfObject = new Object[2];
            XposedBridge.log("xxxxxxxxxxxxxxxxxxxxxxxx");
            arrayOfObject[0] = XposedHelpers.newInstance(XposedHelpers.findClass(paramWechatEntity.sendPic_class_1, paramClassLoader), new Object[] { Integer.valueOf(3), paramString1, paramString2, paramString3, Integer.valueOf(1), null, Integer.valueOf(1), "", "", Boolean.valueOf(true), Integer.valueOf(2130837923) });
            XposedBridge.log("cccccccccccccccccccccccccccc"+arrayOfObject[0]);
            arrayOfObject[1] = Integer.valueOf(1);
            long l = XposedHelpers.getLongField(arrayOfObject[0], paramWechatEntity.sendPic_filed_msgId);
            XposedHelpers.callMethod(XposedHelpers.getStaticObjectField(XposedHelpers.findClass(paramWechatEntity.sendPic_class_2, paramClassLoader), paramWechatEntity.sendPic_field_1), paramWechatEntity.sendPic_method_1, arrayOfObject);
            saveMsgId(String.valueOf(l), Long.valueOf(paramLong));
            return true;
        }
        catch (Exception e)
        {
            //ThrowableExtension.printStackTrace(paramClassLoader);
        }
        return false;
    }

    public static boolean sendTxt(ClassLoader paramClassLoader, WechatEntity paramWechatEntity, String paramString1, String paramString2, int paramInt, long paramLong)
            throws Exception
    {
        try
        {

           /* //加入这几句解决文本延时发送问题
            Class<?> gClass = XposedHelpers.findClass(paramWechatEntity.confirm_transfer_accounts_class1, paramClassLoader);
            Object g = XposedHelpers.callStaticMethod(gClass, paramWechatEntity.clear_black_friend_method1);
            Object filedA = XposedHelpers.getObjectField(g, "fVR");
            Class<?> oClass = XposedHelpers.findClass("com.tencent.mm.ac.o", paramClassLoader);
            Class<?> lClass = XposedHelpers.findClass("com.tencent.mm.ac.l", paramClassLoader);
            Method methodA = XposedHelpers.findMethodExact(oClass, "a", lClass, int.class);
            Object o = XposedHelpers.callStaticMethod(oClass, "a", filedA);*/



            Class<?> gClass = XposedHelpers.findClass(paramWechatEntity.confirm_transfer_accounts_class1, paramClassLoader);
            Object g = XposedHelpers.callStaticMethod(gClass, paramWechatEntity.clear_black_friend_method1);
            Object filedA = XposedHelpers.getObjectField(g, paramWechatEntity.clear_black_friend_methoddemo);
            Class<?> oClass = XposedHelpers.findClass(paramWechatEntity.sendTxt_class_1, paramClassLoader);
            Class<?> lClass = XposedHelpers.findClass(paramWechatEntity.clear_black_friend_methoddemo1, paramClassLoader);
            Method methodA = XposedHelpers.findMethodExact(oClass, "a", lClass, int.class);
            Object o = XposedHelpers.callStaticMethod(oClass, "a", filedA);



            XposedBridge.log("1111111111111111111111");
           Object paramString3 =  XposedHelpers.newInstance(XposedHelpers.findClass(paramWechatEntity.sendTxt_class_2, paramClassLoader), new Object[] { paramString1, paramString2, Integer.valueOf(paramInt) });
            XposedBridge.log("2222222222222222222222222"+paramString1);
            long l = XposedHelpers.getLongField(paramString3, paramWechatEntity.sendTxt_filed_msgId);
            XposedBridge.log("3333333333333333333333333333"+l);
           // XposedHelpers.callMethod(XposedHelpers.getStaticObjectField(XposedHelpers.findClass(paramWechatEntity.sendTxt_class_1, paramClassLoader), paramWechatEntity.sendTxt_field_1), paramWechatEntity.sendTxt_method_1, new Object[] { paramString1 });
            XposedBridge.log("444444444444444444444444444");
            saveMsgId(String.valueOf(l), Long.valueOf(paramLong));
            XposedBridge.log("55555555555555555555555555555");

            //加入这几句解决文本延时发送问题
            Object[] pp = new Object[]{paramString3, 0};
            try {
                XposedBridge.invokeOriginalMethod(methodA, o, pp);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return true;
        }
        catch (Exception e)
        {

            Logger.e(e, "===== SEND TEXT ===== ERROR!!!", new Object[0]+e.toString());
            XposedBridge.log("===== SEND TEXT ===== ERROR!!!"+e.toString());
            return false;
        }
    }

    public static void sendVideo(final ClassLoader paramClassLoader, final WechatEntity paramWechatEntity, final String paramString1, final String paramString2, final long paramLong)
            throws Exception
    {
        new Thread()
        {
            public void run()
            {
                try
                {
                    String str1 = paramString1;
                    Object localObject = JSON.parseObject(paramString2);
                    String str2 = ((JSONObject)localObject).getString("videoPath");
                    localObject = ((JSONObject)localObject).getString("videoImgPath");
                    String str3 = XposedHelpers.callStaticMethod(XposedHelpers.findClass(paramWechatEntity.sendVideo_class_1, paramClassLoader), paramWechatEntity.sendVideo_method_1, new Object[] { WechatDb.getInstance().selectSelf().getUserTalker() }).toString();
                    HandleUtil.m19(str2, XposedHelpers.callStaticMethod(XposedHelpers.findClass(paramWechatEntity.sendVideo_class_2, paramClassLoader), paramWechatEntity.sendVideo_method_2, new Object[] { str3 }).toString());
                    HandleUtil.m19((String)localObject, XposedHelpers.callStaticMethod(XposedHelpers.findClass(paramWechatEntity.sendVideo_class_3, paramClassLoader), paramWechatEntity.sendVideo_method_3, new Object[] { str3 }).toString());
                    XposedHelpers.callStaticMethod(XposedHelpers.findClass(paramWechatEntity.sendVideo_class_4, paramClassLoader), paramWechatEntity.sendVideo_method_4, new Object[] { str3, Integer.valueOf(2), str1, str2 }).toString();
                    XposedHelpers.callStaticMethod(XposedHelpers.findClass(paramWechatEntity.sendVideo_class_5, paramClassLoader), paramWechatEntity.sendVideo_method_5, new Object[] { str3 });
                    SendMesUtil.saveMsgId(str3, Long.valueOf(paramLong));
                    return;
                }
                catch (Throwable localThrowable)
                {
                    //ThrowableExtension.printStackTrace(localThrowable);
                }
            }
        }.start();
    }

    static class AmrRun
            implements Runnable
    {
        private ClassLoader classLoader;
        private String str;
        private WechatEntity wechatEntity;

        public AmrRun(ClassLoader paramClassLoader, WechatEntity paramWechatEntity, String paramString)
        {
            this.classLoader = paramClassLoader;
            this.wechatEntity = paramWechatEntity;
            this.str = paramString;
        }

        public void run()
        {
            Object localObject1 = XposedHelpers.getStaticObjectField(XposedHelpers.findClass(this.wechatEntity.sendAmr_class_2, this.classLoader), this.wechatEntity.sendAmr_field_1);
            Object localObject2 = XposedHelpers.newInstance(XposedHelpers.findClass(this.wechatEntity.sendAmr_class_3, this.classLoader), new Object[] { this.str });
            XposedHelpers.callMethod(localObject1, this.wechatEntity.sendAmr_method_4, new Object[] { localObject2 });
        }
    }


    public static boolean sendAmr9(ClassLoader classLoader, WechatEntity wechatEntity, Activity activity, String talker, String content, int duration, long localMsgId) throws Exception {
        String r3 = content;
        XposedBridge.log("abcabc1");
        try {
            int r5 = (int) new File(r3).length();
            XposedBridge.log("abcabc2");
            Class<?> r6 = XposedHelpers.findClass(wechatEntity.sendAmr_class_1, classLoader);
            XposedBridge.log("abcabc3");
            String r4 = XposedHelpers.callStaticMethod(r6, wechatEntity.sendAmr_method_1, new Object[]{talker}).toString();
            XposedBridge.log("abcabc4");
            HandleUtil.m19(r3, XposedHelpers.callStaticMethod(r6, wechatEntity.sendAmr_method_2, new Object[]{r4, Boolean.valueOf(false)}).toString());
            XposedBridge.log("abcabc5");
            XposedHelpers.callStaticMethod(r6, wechatEntity.sendAmr_method_3, new Object[]{r4, Integer.valueOf(r5), Integer.valueOf(0)});
            XposedBridge.log("abcabc6");
            // WechatDb.getInstance().updateVoiceInfo(r5, duration, r4);
            XposedBridge.log("abcabc7");
            activity.runOnUiThread(new AmrRun(classLoader, wechatEntity, r4));
            XposedBridge.log("abcabc8");
            saveMsgId(r4, Long.valueOf(localMsgId));
            return true;
        } catch (Throwable e) {
            XposedBridge.log("===== SEND AMR ===== ERROR!!!"+new Object[0]);
            return false;
        }
    }
}
