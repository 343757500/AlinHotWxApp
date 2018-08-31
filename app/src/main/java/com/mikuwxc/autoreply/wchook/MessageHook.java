package com.mikuwxc.autoreply.wchook;

import android.content.ContentValues;
import android.util.Base64;

import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wcloop.MessageConsumer;
import com.mikuwxc.autoreply.wcutil.AuthUtil;
import com.mikuwxc.autoreply.wcutil.DownLoadWxResFromWxUtil;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MessageHook
{

    private static String str;
    private static String paramAnonymousMethodHookParam1;
    private static ContentValues localContentValues;

    public static void hook(final WechatEntity paramWechatEntity, XC_LoadPackage.LoadPackageParam paramLoadPackageParam)
    {
       final ClassLoader classLoader = paramLoadPackageParam.classLoader;
        XposedHelpers.findAndHookMethod(paramWechatEntity.sqlitedatabase_class_name, classLoader, "insertWithOnConflict", new Object[] { String.class, String.class, ContentValues.class, Integer.TYPE, new XC_MethodHook()
        {
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
                    throws Throwable
            {
                ContentValues localContentValues;
                int i;
                try
                {
                    str = (String)paramAnonymousMethodHookParam.args[0];
                    localContentValues = (ContentValues)paramAnonymousMethodHookParam.args[2];
                    i = -1;
                    switch (str.hashCode())
                    {
                        case 954925063:
                            if (!str.equals("message")) {
                                //break label355;
                            }
                            i = 0;
                    }
                }
                catch (Exception e)
                {
                    String str;
                    long l1;
                    long l2;
                   // Logger.e(paramAnonymousMethodHookParam, "===== INSERT AFTER ===== ERROR!!!", new Object[0]);
                    return;
                }
                if (str.equals("fmessage_msginfo"))
                {
                    i = 1;
                    //break label355;
                    ContentValues contentValues = (ContentValues)paramAnonymousMethodHookParam.args[2];
                   long l1 = contentValues.getAsLong("createTime").longValue();
                   long l2 = System.currentTimeMillis() - 300000L;
                    XposedBridge.log("===== after hook message %s %s"+ new Object[] { Long.valueOf(l1), Long.valueOf(l2) });
                    if (l1 < l2)
                    {
                        XposedBridge.log("===== after forget[%s]"+ new Object[] { contentValues.toString() });
                        return;
                    }
                    XposedBridge.log("===== after handle[%s]"+ new Object[] { contentValues.toString() });
                    if (localContentValues.getAsString("talker").startsWith("gh_")) {
                        //break label380;
                    }
                    i = localContentValues.getAsInteger("type").intValue();
                    paramAnonymousMethodHookParam1 = "";
                    if (localContentValues.containsKey("msgSvrId")) {
                        paramAnonymousMethodHookParam1 = localContentValues.getAsString("msgSvrId");
                    }
                    if ((i == 43) && (paramAnonymousMethodHookParam1 != null) && (paramAnonymousMethodHookParam1.length() > 0)) {
                        if (!localContentValues.containsKey("imgPath")) {
                           // break label381;
                        }
                    }
                }
                label355:
                label380:
                label381:
                for (paramAnonymousMethodHookParam1 = localContentValues.getAsString("imgPath");; paramAnonymousMethodHookParam1 = "")
                {
                    XposedBridge.log("AAAAAAAAAAAAAAAAAAAAA"+ paramAnonymousMethodHookParam1);
                    XposedBridge.log("QQQQQQQQQQQQQQQQQQQQQ"+  DownLoadWxResFromWxUtil.downloadWxVideoRes(classLoader, paramWechatEntity, paramAnonymousMethodHookParam1) );
                    DownLoadWxResFromWxUtil.downloadWxVideoRes(classLoader, paramWechatEntity, paramAnonymousMethodHookParam1);
                   // DownLoadWxResFromWxUtil.downloadMomentPic(classLoader, paramWechatEntity,"", (int) localContentValues.getAsLong("createTime").longValue());
                   // MessageConsumer.handleMessageInfo(classLoader, localContentValues, paramWechatEntity);
                    String path=DownLoadWxResFromWxUtil.downloadWxVideoRes(classLoader, paramWechatEntity, paramAnonymousMethodHookParam1);
                   String voisePath= DownLoadWxResFromWxUtil.downloadWxVoiceRes(classLoader,paramWechatEntity,paramAnonymousMethodHookParam1);
                   String pathnew =path.substring(0,path.length()-3)+"mp4";
                    XposedBridge.log("805806858"+voisePath);




                   XposedBridge.log("343757500"+pathnew);

                   File file=new File(voisePath);
                   boolean b=file.exists();
                    XposedBridge.log("343757500"+b);

                    XposedBridge.log("=====insertWithOnConflict afterHookedMethod[%s]"+ new Object[] { localContentValues.toString() });
                    XposedBridge.log("===== HOOK NEW FRIEND REQUEST"+ new Object[0]);
                    localContentValues.put("isFmessageMsgInfo", Boolean.valueOf(true));
                    MessageConsumer.parseFmessageInfo(localContentValues);
                    return;
                }
            }

            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
            {
                try
                {
                    if (((String)paramAnonymousMethodHookParam.args[0]).equals("message"))
                    {
                        localContentValues = (ContentValues)paramAnonymousMethodHookParam.args[2];
                        XposedBridge.log("=====insertWithOnConflict beforeHookedMethod[%s]"+ new Object[] { localContentValues.toString() });
                        XposedBridge.log("111111111111111111111111"+ localContentValues.toString());
                       String paramAnonymousMethodHookParam1 = localContentValues.getAsString("type");
                        long l1 = localContentValues.getAsLong("createTime").longValue();
                        long l2 = System.currentTimeMillis() - 300000L;
                        XposedBridge.log("===== hook message %s %s"+ new Object[] { Long.valueOf(l1), Long.valueOf(l2) });
                        XposedBridge.log("222222222222222222222222"+ Long.valueOf(l1)+"+"+ Long.valueOf(l2));
                        if (l1 < l2)
                        {
                            XposedBridge.log("=====forget[%s]"+ new Object[] { localContentValues.toString() });
                            XposedBridge.log("3333333333333333333"+  localContentValues.toString() );
                            return;
                        }
                        XposedBridge.log("=====handle[%s]"+ new Object[] { localContentValues.toString() });
                        XposedBridge.log("44444444444444444444444"+  localContentValues.toString() );
                        if ((AuthUtil.isForbiddenAuth(8)) && ("1".equals(paramAnonymousMethodHookParam1)))
                        {
                            paramAnonymousMethodHookParam1 = localContentValues.getAsString("content");
                            Matcher localMatcher = Pattern.compile("(\\d{7,14})").matcher(paramAnonymousMethodHookParam1);
                            while (localMatcher.find())
                            {
                                String str = localMatcher.group(1);
                                paramAnonymousMethodHookParam1 = paramAnonymousMethodHookParam1.replace(str, "[��������{" + Base64.encodeToString(str.getBytes(), 0) + "}]");
                            }
                            localContentValues.put("content", paramAnonymousMethodHookParam1);
                            return;
                        }
                    }
                }
                catch (Exception e)
                {
                   // Logger.e(paramAnonymousMethodHookParam, "===== INSERT BEFORE ===== ERROR!!!", new Object[0]);
                }
            }
        } });
    }
}
