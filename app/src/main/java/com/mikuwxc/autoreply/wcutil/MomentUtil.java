package com.mikuwxc.autoreply.wcutil;

import com.alibaba.fastjson.JSONArray;
import com.mikuwxc.autoreply.wcentity.CircleCommentEntity;
import com.mikuwxc.autoreply.wcentity.CircleLikeEntity;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wclocalsocket.LocalSocketClient;
import com.mikuwxc.autoreply.wclocalsocket.WxCmdType;
import com.mikuwxc.autoreply.wx.WechatDb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class MomentUtil {
    public static void collectCurrentMoment(ClassLoader classLoader, WechatEntity wechatEntity, long j) {
        XposedBridge.log("eeeeeeeeeeeeeeeeeee");
        //FileIoUtil.setValueToPath(String.valueOf(j), false, "/mnt/sdcard/snsId.txt");
        XposedBridge.log("rrrrrrrrrrrrrrr");
        Object newInstance = XposedHelpers.newInstance(XposedHelpers.findClass(wechatEntity.collect_current_sns_class1, classLoader), new Object[]{Long.valueOf(j)});
        XposedBridge.log("rrrrrrrrrrrrrrrrrrrr");
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.collect_current_sns_class2, classLoader), wechatEntity.collect_current_sns_method1, new Object[0]), wechatEntity.collect_current_sns_method2, new Object[]{newInstance, Integer.valueOf(0)});
        XposedBridge.log("tttttttttttttttttttttt");
    }

    public static void collectOtherMoment(ClassLoader classLoader, WechatEntity wechatEntity, String str, long j, int i, int i2) {
       /* FileIoUtil.setValueToPath(str, false, "/mnt/sdcard/userName.txt");
        FileIoUtil.setValueToPath(String.valueOf(j), false, "/mnt/sdcard/snsId.txt");
        FileIoUtil.setValueToPath(String.valueOf(i), false, "/mnt/sdcard/selectTime.txt");
        FileIoUtil.setValueToPath(String.valueOf(i2), false, "/mnt/sdcard/selectLimit.txt");*/
        Object newInstance = XposedHelpers.newInstance(XposedHelpers.findClass(wechatEntity.collect_other_sns_class1, classLoader), new Object[]{str, Long.valueOf(j), Boolean.valueOf(false), Integer.valueOf(0)});
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.collect_other_sns_class2, classLoader), wechatEntity.collect_other_sns_method1, new Object[0]), wechatEntity.collect_other_sns_method2, new Object[]{newInstance, Integer.valueOf(0)});
    }

    public static String oz(String str) {
        return str != null ? str.replace("\\[", "[[]").replace("%", "").replace("\\^", "").replace("'", "").replace("\\{", "").replace("\\}", "").replace("\"", "") : str;
    }

    public static void parseCurrentMoment(ClassLoader classLoader, WechatEntity wechatEntity, Object obj, Object obj2) {
        XposedBridge.log("11111111111111111111");
        boolean booleanValue = ((Boolean) XposedHelpers.getObjectField(obj, wechatEntity.parse_current_sns_field1)).booleanValue();
        XposedBridge.log("22222222222222222222"+booleanValue);
        String str = (String) XposedHelpers.getObjectField(obj, wechatEntity.parse_current_sns_field2);
        XposedBridge.log("33333333333333333333"+str);
        Object objectField = XposedHelpers.getObjectField(XposedHelpers.getObjectField(obj2, wechatEntity.parse_current_sns_field3), wechatEntity.parse_current_sns_field4);
        XposedBridge.log("4444444444444444444444"+str);
        int intValue = ((Integer) XposedHelpers.getObjectField(XposedHelpers.callMethod(obj2, wechatEntity.parse_current_sns_method1, new Object[0]), wechatEntity.parse_current_sns_field5)).intValue();
        XposedBridge.log("5555555555555555555"+intValue);
        if (intValue == 0 || intValue == 207) {
            Object objectField2 = XposedHelpers.getObjectField(objectField, wechatEntity.parse_current_sns_field6);
            XposedBridge.log("66666666666666"+objectField2.toString());
            if (objectField2 != null) {
                intValue = ((Integer) XposedHelpers.getObjectField(objectField2, wechatEntity.parse_current_sns_field7)).intValue();
                XposedBridge.log("777777777777777"+intValue);
                Class findClass = XposedHelpers.findClass(wechatEntity.parse_current_sns_class1, classLoader);
                XposedBridge.log("88888888888888"+findClass.toString());
                XposedHelpers.setStaticObjectField(findClass, wechatEntity.parse_current_sns_field8, Integer.valueOf(intValue));
                if (intValue <= 0) {
                    XposedHelpers.setStaticObjectField(findClass, wechatEntity.parse_current_sns_field9, Integer.valueOf(Integer.MAX_VALUE));
                }
                XposedHelpers.setStaticObjectField(XposedHelpers.findClass(wechatEntity.parse_current_sns_class2, classLoader), wechatEntity.parse_current_sns_field11, XposedHelpers.getObjectField(objectField2, wechatEntity.parse_current_sns_field10));
                Class findClass2 = XposedHelpers.findClass(wechatEntity.parse_current_sns_class3, classLoader);
                XposedBridge.log("9999999999999"+findClass2.toString());
                String valueFromPath = FileIoUtil.getValueFromPath("/mnt/sdcard/snsId.txt");
                if (!OtherUtils.isEmpty(valueFromPath)) {
                    long parseLong = Long.parseLong(valueFromPath);
                    XposedHelpers.callStaticMethod(findClass2, wechatEntity.parse_current_sns_method2, new Object[]{Long.valueOf(parseLong)});
                    if (!booleanValue || !str.equals((String) XposedHelpers.getObjectField(objectField, wechatEntity.parse_current_sns_field12))) {
                    }
                }
            }
        }
    }

    public static String parseMomentDetail(ClassLoader classLoader, WechatEntity wechatEntity, String str) {
        //List<CircleLocalEntity> parseArray = JSONArray.parseArray(str, CircleLocalEntity.class);
       // if (!OtherUtils.isEmpty(parseArray)) {
           // for (CircleLocalEntity circleLocalEntity : parseArray) {
               // long snsId = circleLocalEntity.getSnsId();
                Class findClass = XposedHelpers.findClass(wechatEntity.parse_moment_detail_class1, classLoader);
                XposedBridge.log("!!!!!!!!"+findClass.toString());
                Class findClass2 = XposedHelpers.findClass(wechatEntity.parse_moment_detail_class2, classLoader);
        XposedBridge.log("@@@@@@@@"+findClass2.toString());
                String str2 = "sns_table_" + "06327b091f484a22";
                Object callStaticMethod = XposedHelpers.callStaticMethod(findClass, wechatEntity.parse_moment_detail_method1, new Object[]{str2});
        XposedBridge.log("########"+callStaticMethod.toString());
                Object callStaticMethod2 = XposedHelpers.callStaticMethod(findClass2, wechatEntity.parse_moment_detail_method2, new Object[]{callStaticMethod});
        XposedBridge.log("$$$$$$$$$"+callStaticMethod2.toString());
                LinkedList linkedList = (LinkedList) XposedHelpers.getObjectField(callStaticMethod2, wechatEntity.parse_moment_detail_field1);
        XposedBridge.log("%%%%%%%%"+linkedList.toString());
                Iterator it = ((LinkedList) XposedHelpers.getObjectField(callStaticMethod2, wechatEntity.parse_moment_detail_field2)).iterator();
        XposedBridge.log("^^^^^^^^^^"+it.toString());
                ArrayList arrayList = new ArrayList();
                while (it.hasNext()) {
                    Object next = it.next();
                    XposedBridge.log("&&&&&&&&&"+next.toString());
                    str2 = (String) XposedHelpers.getObjectField(next, wechatEntity.parse_moment_detail_field3);
                    String str3 = (String) XposedHelpers.getObjectField(next, wechatEntity.parse_moment_detail_field4);
                    String str4 = (String) XposedHelpers.getObjectField(next, wechatEntity.parse_moment_detail_field5);
                    String str5 = (String) XposedHelpers.getObjectField(next, wechatEntity.parse_moment_detail_field6);
                    int intValue = ((Integer) XposedHelpers.getObjectField(next, wechatEntity.parse_moment_detail_field7)).intValue();
                    int intValue2 = ((Integer) XposedHelpers.getObjectField(next, wechatEntity.parse_moment_detail_field8)).intValue();
                    int intValue3 = ((Integer) XposedHelpers.getObjectField(next, wechatEntity.parse_moment_detail_field9)).intValue();
                    int intValue4 = ((Integer) XposedHelpers.getObjectField(next, wechatEntity.parse_moment_detail_field10)).intValue();
                    CircleCommentEntity circleCommentEntity = new CircleCommentEntity();
                    circleCommentEntity.setCommentId1(intValue2);
                    circleCommentEntity.setCommentId2(intValue3);
                    circleCommentEntity.setCommentArg(intValue4);
                    circleCommentEntity.setCommentTime(intValue);
                    circleCommentEntity.setContent(str2);
                    circleCommentEntity.setNickName(str3);
                    circleCommentEntity.setWechatId(str4);
                    circleCommentEntity.setOtherWechatId(str5);
                    arrayList.add(circleCommentEntity);
                }
                //circleLocalEntity.setCommentList(arrayList);
                ArrayList arrayList2 = new ArrayList();
                Iterator it2 = linkedList.iterator();
                while (it2.hasNext()) {
                    Object next2 = it2.next();
                    str2 = (String) XposedHelpers.getObjectField(next2, wechatEntity.parse_moment_detail_field11);
                    String str6 = (String) XposedHelpers.getObjectField(next2, wechatEntity.parse_moment_detail_field12);
                    CircleLikeEntity circleLikeEntity = new CircleLikeEntity();
                    circleLikeEntity.setWechatId(str6);
                    circleLikeEntity.setNickName(str2);
                    arrayList2.add(circleLikeEntity);
                }
              //  circleLocalEntity.setLikeList(arrayList2);
        //    }
      //  }
        return JSONArray.toJSONString(arrayList2);
    }

    public static void parseOtherMoment(ClassLoader classLoader, WechatEntity wechatEntity, Object obj, Object obj2) {
        boolean booleanValue = ((Boolean) XposedHelpers.getObjectField(obj, wechatEntity.parse_other_sns_field1)).booleanValue();
        String str = (String) XposedHelpers.getObjectField(obj, wechatEntity.parse_other_sns_field2);
        Object objectField = XposedHelpers.getObjectField(XposedHelpers.getObjectField(obj2, wechatEntity.parse_other_sns_field3), wechatEntity.parse_other_sns_field4);
        int intValue = ((Integer) XposedHelpers.getObjectField(XposedHelpers.callMethod(obj2, wechatEntity.parse_other_sns_method1, new Object[0]), wechatEntity.parse_other_sns_field5)).intValue();
        String valueFromPath = FileIoUtil.getValueFromPath("/mnt/sdcard/userName.txt");
        String valueFromPath2 = FileIoUtil.getValueFromPath("/mnt/sdcard/snsId.txt");
        if (!OtherUtils.isEmpty(valueFromPath2)) {
            long parseLong = Long.parseLong(valueFromPath2);
            Class findClass = XposedHelpers.findClass(wechatEntity.parse_other_sns_class1, classLoader);
            valueFromPath2 = (String) XposedHelpers.getObjectField(objectField, wechatEntity.parse_other_sns_field6);
            if (intValue == 207 || intValue == 203 || intValue == 0 || intValue == 2001 || intValue == WxCmdType.UPDATE_CONTACT_REMARK || intValue == 2004) {
                if (intValue == WxCmdType.UPDATE_CONTACT_REMARK) {
                    XposedHelpers.callMethod(XposedHelpers.getObjectField(XposedHelpers.callStaticMethod(findClass, wechatEntity.parse_other_sns_method2, new Object[0]), wechatEntity.parse_other_sns_field7), wechatEntity.parse_other_sns_method3, new Object[]{"SnsInfo", "DELETE FROM SnsInfo where SnsInfo.userName=\"" + oz(valueFromPath) + "\""});
                }
                int intValue2 = ((Integer) XposedHelpers.getObjectField(objectField, wechatEntity.parse_other_sns_field8)).intValue();
                String str2 = (String) XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.parse_other_sns_class2, classLoader), wechatEntity.parse_other_sns_method4, new Object[]{Long.valueOf(parseLong)});
                if (!booleanValue) {
                    writeWxSns(classLoader, wechatEntity, parseLong, objectField, valueFromPath, str2);
                } else if (str.equals(valueFromPath2)) {
                    XposedHelpers.callMethod(XposedHelpers.callStaticMethod(findClass, wechatEntity.parse_other_sns_method5, new Object[0]), wechatEntity.parse_other_sns_method6, new Object[]{Integer.valueOf(0), Integer.valueOf(intValue2), valueFromPath, Boolean.valueOf(false)});
                    XposedHelpers.callMethod(XposedHelpers.callStaticMethod(findClass, wechatEntity.parse_other_sns_method7, new Object[0]), wechatEntity.parse_other_sns_method8, new Object[]{valueFromPath});
                    LocalSocketClient.getInstance().reportCollectOtherMoment(valueFromPath, -9999, -9999);
                } else if (booleanValue && !str.equals(valueFromPath2)) {
                    writeWxSns(classLoader, wechatEntity, parseLong, objectField, valueFromPath, str2);
                }
            }
        }
    }

    private static void writeWxSns(ClassLoader classLoader, WechatEntity wechatEntity, long j, Object obj, String str, String str2) {
        long j2 = 0;
        long j3 = 0;
        LinkedList linkedList = (LinkedList) XposedHelpers.getObjectField(obj, wechatEntity.write_into_wx_sns_db_field1);
        if (!linkedList.isEmpty()) {
            XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.write_into_wx_sns_db_class1, classLoader), wechatEntity.write_into_wx_sns_db_method1, new Object[]{str, Integer.valueOf(8), linkedList, str2});
            Class findClass = XposedHelpers.findClass(wechatEntity.write_into_wx_sns_db_class2, classLoader);
            if (j == 0) {
                j2 = ((Long) XposedHelpers.getObjectField(linkedList.getFirst(), wechatEntity.write_into_wx_sns_db_field2)).longValue();
            } else {
                j2 = ((Long) XposedHelpers.callStaticMethod(findClass, wechatEntity.write_into_wx_sns_db_method2, new Object[]{Long.valueOf(j)})).longValue();
            }
            Object first = linkedList.getFirst();
            Object last = linkedList.getLast();
            long longValue = ((Long) XposedHelpers.getObjectField(first, wechatEntity.write_into_wx_sns_db_field3)).longValue();
            j3 = ((Long) XposedHelpers.getObjectField(last, wechatEntity.write_into_wx_sns_db_field3)).longValue();
            Object objectField = XposedHelpers.getObjectField(obj, wechatEntity.write_into_wx_sns_db_field4);
            XposedHelpers.callStaticMethod(findClass, wechatEntity.write_into_wx_sns_db_method3, new Object[]{str, Long.valueOf(j2), Long.valueOf(j3), objectField});
            XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.write_into_wx_sns_db_class3, classLoader), wechatEntity.write_into_wx_sns_db_method4, new Object[]{str});
            j2 = longValue;
        }
        LocalSocketClient.getInstance().reportCollectOtherMoment(str, j2, j3);
    }




    public static int sendTxtMoment(ClassLoader paramClassLoader, WechatEntity paramWechatEntity, String paramString, int paramInt, Set<String> paramSet)
    {
        int i = 0;
        if (paramInt == 1) {
            i = 1;
        }
        Object localObject2 = new LinkedList();
        Object localObject1 = new LinkedList();
        if ((paramInt == 2) || (paramInt == 3)) {
            localObject1 = WechatDb.getInstance().selectContactByLabelName(paramSet);
        }
        int j = 0;
        if (paramInt == 3) {
            j = 1;
        }
        Object localObject4 = XposedHelpers.findClass(paramWechatEntity.commit_text_moment_class1, paramClassLoader);
        Object localObject6 = XposedHelpers.findClass(paramWechatEntity.commit_text_moment_class2, paramClassLoader);

        Class<?> paramSet1 = XposedHelpers.findClass(paramWechatEntity.commot_text_moment_class3, paramClassLoader);
        Object localObject3 = XposedHelpers.findClass(paramWechatEntity.commit_text_moment_class4, paramClassLoader);
        Object localObject5 = XposedHelpers.findClass(paramWechatEntity.commit_text_moment_class5, paramClassLoader);
        localObject3 = initArj(paramWechatEntity, (Class)localObject3);
        localObject4 = XposedHelpers.newInstance((Class)localObject4, new Object[] { Integer.valueOf(2) });
        localObject6 = XposedHelpers.newInstance((Class)localObject6, new Object[0]);
        Object localObject7 = XposedHelpers.getObjectField(localObject4, paramWechatEntity.commit_text_moment_field1);
        XposedHelpers.setObjectField(localObject6, paramWechatEntity.commit_text_moment_field2, localObject7);
        if (Integer.MAX_VALUE > 0) {
            XposedHelpers.callMethod(localObject4, paramWechatEntity.commit_text_moment_method1, new Object[] { Integer.valueOf(2) });
        }
        localObject6 = new LinkedList();
        if (localObject2 != null)
        {
            localObject5 = (List) XposedHelpers.callStaticMethod((Class)localObject5, paramWechatEntity.commit_text_moment_method2, new Object[0]);
            localObject7 = ((List)localObject2).iterator();
            while (((Iterator)localObject7).hasNext())
            {
                String str = (String)((Iterator)localObject7).next();
                if (!((List)localObject5).contains(str))
                {
                    localObject2 = XposedHelpers.newInstance(paramSet1, new Object[0]);
                    XposedHelpers.setObjectField(localObject2, paramWechatEntity.commit_text_moment_field3, str);
                    ((LinkedList)localObject6).add(localObject2);
                }
            }
        }
        if (j != 0) {
            XposedHelpers.callMethod(localObject4, paramWechatEntity.commit_text_moment_method3, new Object[] { Integer.valueOf(1) });
        }
        for (;;)
        {
            XposedHelpers.callMethod(localObject4, paramWechatEntity.commit_text_moment_method4, new Object[] { Integer.valueOf(0) });
            if (0 != 0) {
                XposedHelpers.callMethod(localObject4, paramWechatEntity.commit_text_moment_method4, new Object[] { Integer.valueOf(5) });
            }
            XposedHelpers.callMethod(localObject4, paramWechatEntity.commit_text_moment_method5, new Object[] { null, null, null, Integer.valueOf(0), Integer.valueOf(0) });
            XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(localObject4, paramWechatEntity.commit_text_moment_method6, new Object[] { paramString }), paramWechatEntity.commit_text_moment_method7, new Object[] { localObject3 }), paramWechatEntity.commit_text_moment_method8, new Object[] { localObject6 }), paramWechatEntity.commit_text_moment_method9, new Object[] { Integer.valueOf(i) }), paramWechatEntity.commit_text_moment_method10, new Object[] { Integer.valueOf(0) }), paramWechatEntity.commit_text_moment_method11, new Object[] { localObject1 });
            paramInt = ((Integer) XposedHelpers.callMethod(localObject4, paramWechatEntity.commit_text_moment_method12, new Object[0])).intValue();
            XposedBridge.log("===== commit " + paramInt+ new Object[0]);
            sendCommitMoment(paramClassLoader, paramWechatEntity);

            XposedHelpers.callMethod(localObject4, paramWechatEntity.commit_text_moment_method3, new Object[] { Integer.valueOf(0) });

            return paramInt;

        }




    }


    public static int sendPicMoment(ClassLoader paramClassLoader, WechatEntity paramWechatEntity, String paramString, int paramInt, Set<String> paramSet, JSONArray paramJSONArray)
    {
        int i = 0;
        if (paramInt == 1) {
            i = 1;
        }
        Object localObject7 = new LinkedList();
        Object localObject1 = new LinkedList();
        if ((paramInt == 2) || (paramInt == 3)) {
            localObject1 = WechatDb.getInstance().selectContactByLabelName(paramSet);
        }
        int j = 0;
        if (paramInt == 3) {
            j = 1;
        }
        Object localObject9 = XposedHelpers.findClass(paramWechatEntity.commit_pic_moment_class1, paramClassLoader);
        Object localObject2 = XposedHelpers.findClass(paramWechatEntity.commit_pic_moment_class2, paramClassLoader);
        Object localObject8 = XposedHelpers.findClass(paramWechatEntity.commot_pic_moment_class3, paramClassLoader);
        Object localObject6 = XposedHelpers.findClass(paramWechatEntity.commit_pic_moment_class4, paramClassLoader);
        Class localClass1 = XposedHelpers.findClass(paramWechatEntity.commit_pic_moment_class5, paramClassLoader);
        Object localObject5 = XposedHelpers.findClass(paramWechatEntity.commit_pic_moment_class6, paramClassLoader);
        Class localClass2 = XposedHelpers.findClass(paramWechatEntity.commit_pic_moment_class7, paramClassLoader);
        Class paramSet1 = XposedHelpers.findClass(paramWechatEntity.commit_pic_moment_class8, paramClassLoader);
        Object localObject3 = XposedHelpers.newInstance(XposedHelpers.findClass(paramWechatEntity.commit_pic_moment_class9, paramClassLoader), new Object[0]);
        Object localObject4 = initArj(paramWechatEntity, (Class)localObject2);
        localObject2 = new LinkedList();
        Object localObject10 = new ArrayList();
        XposedBridge.log(paramJSONArray.size()+"@@@@@@@@@"+paramInt);
        for (paramInt = 0; paramInt < paramJSONArray.size(); paramInt++) {
            ((ArrayList)localObject10).add(paramJSONArray.getString(paramInt));
        }
        Iterator paramJSONArray1 = ((ArrayList)localObject10).iterator();
        paramInt = 0;
        if (paramJSONArray1.hasNext())
        {
            localObject10 = XposedHelpers.newInstance((Class)localObject9, new Object[] { (String)paramJSONArray1.next(), Integer.valueOf(2) });
            XposedHelpers.setObjectField(localObject10, paramWechatEntity.commit_pic_moment_field1, Integer.valueOf(2));
            XposedHelpers.setObjectField(localObject10, paramWechatEntity.commit_pic_moment_field2, Integer.valueOf(i));
            if (paramInt == 0) {
                XposedHelpers.setObjectField(localObject10, paramWechatEntity.commit_pic_moment_field3, Integer.valueOf(0));
            }
            for (;;)
            {
                XposedBridge.log("第一");
                XposedHelpers.setObjectField(localObject10, paramWechatEntity.commit_pic_moment_field4, Integer.valueOf(0));
                XposedBridge.log("第二");
                XposedHelpers.setObjectField(localObject10, paramWechatEntity.commit_pic_moment_field5, paramString);
                XposedHelpers.setObjectField(localObject10, paramWechatEntity.commit_pic_moment_field6, Boolean.valueOf(false));
                ((List)localObject2).add(localObject10);
                paramInt++;
                XposedBridge.log("第三");
                XposedHelpers.setObjectField(localObject10, paramWechatEntity.commit_pic_moment_field3, Integer.valueOf(0));
                XposedBridge.log("第四");
                break;

            }
        }
        LinkedList paramJSONArray2 = new LinkedList();
        XposedBridge.log("第w");
        if (localObject7 != null)
        {
            localObject8 = (List) XposedHelpers.callStaticMethod((Class)localObject8, paramWechatEntity.commit_pic_moment_method1, new Object[0]);
            XposedBridge.log("第l");
            localObject9 = ((List)localObject7).iterator();
            while (((Iterator)localObject9).hasNext())
            {
                localObject7 = (String)((Iterator)localObject9).next();
                if (!((List)localObject8).contains(localObject7))
                {
                    XposedBridge.log("第四");
                    localObject10 = XposedHelpers.newInstance((Class)localObject6, new Object[0]);
                    XposedHelpers.setObjectField(localObject10, paramWechatEntity.commit_pic_moment_field7, localObject7);
                    paramJSONArray2.add(localObject10);
                }
            }
        }
        localObject5 = XposedHelpers.newInstance((Class)localObject5, new Object[] { Integer.valueOf(1) });
        localObject6 = XposedHelpers.getObjectField(localObject5, paramWechatEntity.commit_pic_moment_field8);
        XposedHelpers.setObjectField(localObject3, paramWechatEntity.commit_pic_moment_field9, localObject6);
        if (((Integer) XposedHelpers.getStaticObjectField(localClass2, paramWechatEntity.commit_pic_moment_field10)).intValue() > 0) {
            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method2, new Object[] { Integer.valueOf(3) });
        }
        XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method3, new Object[] { paramString }), paramWechatEntity.commit_pic_moment_method4, new Object[] { localObject4 }), paramWechatEntity.commit_pic_moment_method5, new Object[] { paramJSONArray2 }), paramWechatEntity.commit_pic_moment_method6, new Object[] { Integer.valueOf(i) }), paramWechatEntity.commit_pic_moment_method7, new Object[] { Integer.valueOf(0) }), paramWechatEntity.commit_pic_moment_method8, new Object[] { localObject1 });
        if (j != 0) {
            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method9, new Object[] { Integer.valueOf(1) });
        }
        for (;;) {
            XposedBridge.log("第q");
            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method10, new Object[]{Integer.valueOf(0)});
            if (0 != 0) {
                XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method10, new Object[]{Integer.valueOf(5)});
            }
            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method11, new Object[]{null, null, null, Integer.valueOf(0), Integer.valueOf(0)});
            paramInt = ((Integer) XposedHelpers.getObjectField(localObject4, paramWechatEntity.commit_pic_moment_field11)).intValue();
            if ((localObject4 != null) && (paramInt != 0)) {
                Object paramJSONArray3 = (String) XposedHelpers.getObjectField(localObject4, paramWechatEntity.commit_pic_moment_field12);
                localObject1 = XposedHelpers.getObjectField(localObject5, paramWechatEntity.commit_pic_moment_field13);
                Object paramString4 = XposedHelpers.getObjectField(localObject1, paramWechatEntity.commit_pic_moment_field14);
                XposedHelpers.setObjectField(localObject1, paramWechatEntity.commit_pic_moment_field15, XposedHelpers.newInstance(localClass1, new Object[0]));
                XposedHelpers.setObjectField(paramString4, paramWechatEntity.commit_pic_moment_field16, Integer.valueOf(paramInt));
                XposedHelpers.setObjectField(paramString4, paramWechatEntity.commit_pic_moment_field17, paramJSONArray3);
            }
            for (paramInt = 0; paramInt < ((List) localObject2).size(); paramInt++) {
                XposedBridge.log("第b");
                Object paramString1 = XposedHelpers.newInstance(paramSet1, new Object[0]);
                XposedHelpers.setObjectField(paramString1, paramWechatEntity.commit_pic_moment_field18, Float.valueOf(-1000.0F));
                XposedHelpers.setObjectField(paramString1, paramWechatEntity.commit_pic_moment_field19, Float.valueOf(-1000.0F));
                XposedHelpers.setObjectField(paramString1, paramWechatEntity.commit_pic_moment_field20, Float.valueOf(-1000.0F));
                XposedHelpers.setObjectField(paramString1, paramWechatEntity.commit_pic_moment_field21, Float.valueOf(-1000.0F));
                XposedHelpers.callMethod(XposedHelpers.getObjectField(XposedHelpers.getObjectField(localObject5, paramWechatEntity.commit_pic_moment_field22), paramWechatEntity.commit_pic_moment_field23), paramWechatEntity.commit_pic_moment_method12, new Object[]{paramString1});
            }
            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method9, new Object[]{Integer.valueOf(0)});
            XposedBridge.log("第j");
            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method13, new Object[]{localObject2});
            paramInt = ((Integer) XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method14, new Object[0])).intValue();
            sendCommitMoment(paramClassLoader, paramWechatEntity);
            return paramInt;
        }

    }




    public static int sendPicMoment1(ClassLoader paramClassLoader, WechatEntity paramWechatEntity, String paramString, int paramInt, Set<String> paramSet, JSONArray paramJSONArray)
    {
        int i = 0;
        if (paramInt == 1) {
            i = 1;
        }
        Object localObject7 = new LinkedList();
        Object localObject1 = new LinkedList();
        if ((paramInt == 2) || (paramInt == 3)) {
            localObject1 = WechatDb.getInstance().selectContactByLabelName(paramSet);
        }
        int j = 0;
        if (paramInt == 3) {
            j = 1;
        }
        Object localObject9 = XposedHelpers.findClass(paramWechatEntity.commit_pic_moment_class1, paramClassLoader);
        Object localObject2 = XposedHelpers.findClass(paramWechatEntity.commit_pic_moment_class2, paramClassLoader);
        Object localObject8 = XposedHelpers.findClass(paramWechatEntity.commot_pic_moment_class3, paramClassLoader);
        Object localObject6 = XposedHelpers.findClass(paramWechatEntity.commit_pic_moment_class4, paramClassLoader);
        Class localClass1 = XposedHelpers.findClass(paramWechatEntity.commit_pic_moment_class5, paramClassLoader);
        Object localObject5 = XposedHelpers.findClass(paramWechatEntity.commit_pic_moment_class6, paramClassLoader);
        Class localClass2 = XposedHelpers.findClass(paramWechatEntity.commit_pic_moment_class7, paramClassLoader);
        Class<?> paramSet1 = XposedHelpers.findClass(paramWechatEntity.commit_pic_moment_class8, paramClassLoader);
        Object localObject3 = XposedHelpers.newInstance(XposedHelpers.findClass(paramWechatEntity.commit_pic_moment_class9, paramClassLoader), new Object[0]);
        Object localObject4 = initArj(paramWechatEntity, (Class)localObject2);
        localObject2 = new LinkedList();
        Object localObject10 = new ArrayList();
        for (paramInt = 0; paramInt < paramJSONArray.size(); paramInt++) {
            ((ArrayList)localObject10).add(paramJSONArray.getString(paramInt));
        }
        Iterator paramJSONArray1 = ((ArrayList)localObject10).iterator();
        paramInt = 0;
        if (paramJSONArray1.hasNext())
        {
            localObject10 = XposedHelpers.newInstance((Class)localObject9, new Object[] { (String)paramJSONArray1.next(), Integer.valueOf(2) });
            XposedHelpers.setObjectField(localObject10, paramWechatEntity.commit_pic_moment_field1, Integer.valueOf(2));
            XposedHelpers.setObjectField(localObject10, paramWechatEntity.commit_pic_moment_field2, Integer.valueOf(i));
            if (paramInt == 0) {
                XposedHelpers.setObjectField(localObject10, paramWechatEntity.commit_pic_moment_field3, Integer.valueOf(0));
            }
            for (;;)
            {
                XposedHelpers.setObjectField(localObject10, paramWechatEntity.commit_pic_moment_field4, Integer.valueOf(0));
                XposedHelpers.setObjectField(localObject10, paramWechatEntity.commit_pic_moment_field5, paramString);
                XposedHelpers.setObjectField(localObject10, paramWechatEntity.commit_pic_moment_field6, Boolean.valueOf(false));
                ((List)localObject2).add(localObject10);
                paramInt++;
                XposedHelpers.setObjectField(localObject10, paramWechatEntity.commit_pic_moment_field3, Integer.valueOf(0));
                break;

            }
        }
        LinkedList paramJSONArray4 = new LinkedList();
        if (localObject7 != null)
        {
            localObject8 = (List) XposedHelpers.callStaticMethod((Class)localObject8, paramWechatEntity.commit_pic_moment_method1, new Object[0]);
            localObject9 = ((List)localObject7).iterator();
            while (((Iterator)localObject9).hasNext())
            {
                localObject7 = (String)((Iterator)localObject9).next();
                if (!((List)localObject8).contains(localObject7))
                {
                    localObject10 = XposedHelpers.newInstance((Class)localObject6, new Object[0]);
                    XposedHelpers.setObjectField(localObject10, paramWechatEntity.commit_pic_moment_field7, localObject7);
                    paramJSONArray4.add(localObject10);
                }
            }
        }
        localObject5 = XposedHelpers.newInstance((Class)localObject5, new Object[] { Integer.valueOf(1) });
        localObject6 = XposedHelpers.getObjectField(localObject5, paramWechatEntity.commit_pic_moment_field8);
        XposedHelpers.setObjectField(localObject3, paramWechatEntity.commit_pic_moment_field9, localObject6);
        if (((Integer) XposedHelpers.getStaticObjectField(localClass2, paramWechatEntity.commit_pic_moment_field10)).intValue() > 0) {
            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method2, new Object[] { Integer.valueOf(3) });
        }
        XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method3, new Object[] { paramString }), paramWechatEntity.commit_pic_moment_method4, new Object[] { localObject4 }), paramWechatEntity.commit_pic_moment_method5, new Object[] { paramJSONArray4 }), paramWechatEntity.commit_pic_moment_method6, new Object[] { Integer.valueOf(i) }), paramWechatEntity.commit_pic_moment_method7, new Object[] { Integer.valueOf(0) }), paramWechatEntity.commit_pic_moment_method8, new Object[] { localObject1 });
        if (j != 0) {
            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method9, new Object[] { Integer.valueOf(1) });
        }
        for (;;)
        {
            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method10, new Object[] { Integer.valueOf(0) });
            if (0 != 0) {
                XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method10, new Object[] { Integer.valueOf(5) });
            }
            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method11, new Object[] { null, null, null, Integer.valueOf(0), Integer.valueOf(0) });
            paramInt = ((Integer) XposedHelpers.getObjectField(localObject4, paramWechatEntity.commit_pic_moment_field11)).intValue();
            if ((localObject4 != null) && (paramInt != 0))
            {
                Object paramJSONArray3 = (String) XposedHelpers.getObjectField(localObject4, paramWechatEntity.commit_pic_moment_field12);
                localObject1 = XposedHelpers.getObjectField(localObject5, paramWechatEntity.commit_pic_moment_field13);
                Object paramString3 = XposedHelpers.getObjectField(localObject1, paramWechatEntity.commit_pic_moment_field14);
                XposedHelpers.setObjectField(localObject1, paramWechatEntity.commit_pic_moment_field15, XposedHelpers.newInstance(localClass1, new Object[0]));
                XposedHelpers.setObjectField(paramString3, paramWechatEntity.commit_pic_moment_field16, Integer.valueOf(paramInt));
                XposedHelpers.setObjectField(paramString3, paramWechatEntity.commit_pic_moment_field17, paramJSONArray3);
            }
            for (paramInt = 0; paramInt < ((List)localObject2).size(); paramInt++)
            {
                Object paramString1  = XposedHelpers.newInstance(paramSet1, new Object[0]);
                XposedHelpers.setObjectField(paramString1, paramWechatEntity.commit_pic_moment_field18, Float.valueOf(-1000.0F));
                XposedHelpers.setObjectField(paramString1, paramWechatEntity.commit_pic_moment_field19, Float.valueOf(-1000.0F));
                XposedHelpers.setObjectField(paramString1, paramWechatEntity.commit_pic_moment_field20, Float.valueOf(-1000.0F));
                XposedHelpers.setObjectField(paramString1, paramWechatEntity.commit_pic_moment_field21, Float.valueOf(-1000.0F));
                XposedHelpers.callMethod(XposedHelpers.getObjectField(XposedHelpers.getObjectField(localObject5, paramWechatEntity.commit_pic_moment_field22), paramWechatEntity.commit_pic_moment_field23), paramWechatEntity.commit_pic_moment_method12, new Object[] { paramString1 });
            }
            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method9, new Object[] { Integer.valueOf(0) });
            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method13, new Object[] { localObject2 });
            paramInt = ((Integer) XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_pic_moment_method14, new Object[0])).intValue();
            sendCommitMoment(paramClassLoader, paramWechatEntity);
            return paramInt;
        }

    }




    public static int sendVideoMoment(ClassLoader paramClassLoader, WechatEntity paramWechatEntity, String paramString1, int paramInt, Set<String> paramSet, String paramString2, String paramString3)
    {
        int i = 0;
        if (paramInt == 1) {
            i = 1;
        }
        Object localObject6 = new LinkedList();
        Object localObject1 = new LinkedList();
        if ((paramInt == 2) || (paramInt == 3)) {
            localObject1 = WechatDb.getInstance().selectContactByLabelName(paramSet);
        }
        int j = 0;
        if (paramInt == 3) {
            j = 1;
        }
        Object localObject5 = XposedHelpers.findClass(paramWechatEntity.commit_video_moment_class1, paramClassLoader);
        Object localObject4 = XposedHelpers.findClass(paramWechatEntity.commit_video_moment_class2, paramClassLoader);
        Class localClass2 = XposedHelpers.findClass(paramWechatEntity.commit_video_moment_class3, paramClassLoader);
        Object localObject2 = XposedHelpers.findClass(paramWechatEntity.commit_video_moment_class4, paramClassLoader);
        Object localObject7 = XposedHelpers.findClass(paramWechatEntity.commit_video_moment_class5, paramClassLoader);
        Class paramSet1 = XposedHelpers.findClass(paramWechatEntity.commit_video_moment_class6, paramClassLoader);
        Class localClass1 = XposedHelpers.findClass(paramWechatEntity.commit_video_moment_class7, paramClassLoader);
        Object localObject3 = initArj(paramWechatEntity, (Class)localObject2);
        localObject2 = Md5Util.getFileMD5(paramString3);
        LinkedList localLinkedList = new LinkedList();
        XposedBridge.log("aaaaaaaaaaaaaaaa");
        if (localObject6 != null)
        {
            localObject7 = (List) XposedHelpers.callStaticMethod((Class)localObject7, paramWechatEntity.commit_video_moment_method1, new Object[0]);
            Iterator localIterator = ((List)localObject6).iterator();
            while (localIterator.hasNext())
            {
                String str = (String)localIterator.next();
                if (!((List)localObject7).contains(str))
                {
                    localObject6 = XposedHelpers.newInstance(localClass2, new Object[0]);
                    XposedHelpers.setObjectField(localObject6, paramWechatEntity.commit_video_moment_field1, str);
                    localLinkedList.add(localObject6);
                }
            }
        }
        XposedBridge.log("bbbbbbbbbbbbbbbbb");
        localObject5 = XposedHelpers.newInstance((Class)localObject5, new Object[] { Integer.valueOf(15) });
        localObject4 = XposedHelpers.newInstance((Class)localObject4, new Object[0]);
        localObject6 = XposedHelpers.getObjectField(localObject5, paramWechatEntity.commit_video_moment_field2);
        XposedHelpers.setObjectField(localObject4, paramWechatEntity.commit_video_moment_field3, localObject6);
        if (Integer.MAX_VALUE > 0) {
            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_video_moment_method2, new Object[] { Integer.valueOf(3) });
        }
        localObject1 = XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_video_moment_method3, new Object[] { paramString1 }), paramWechatEntity.commit_video_moment_method4, new Object[] { localObject3 }), paramWechatEntity.commit_video_moment_method5, new Object[] { localLinkedList }), paramWechatEntity.commit_video_moment_method6, new Object[] { Integer.valueOf(i) }), paramWechatEntity.commit_video_moment_method7, new Object[] { Integer.valueOf(0) }), paramWechatEntity.commit_video_moment_method8, new Object[] { localObject1 });
        XposedBridge.log("ccccccccccccccccccc");
        if (j != 0)
        {
            XposedBridge.log("dddddddddddddddddddd");
            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_video_moment_method9, new Object[] { Integer.valueOf(1) });
            XposedHelpers.callMethod(localObject1, paramWechatEntity.commit_video_moment_method10, new Object[] { Integer.valueOf(i) });
            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_video_moment_method11, new Object[] { Integer.valueOf(0) });
            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_video_moment_method12, new Object[] { null, null, null, Integer.valueOf(0), Integer.valueOf(0) });
            if (!((Boolean) XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_video_moment_method13, new Object[] { paramString3, paramString2, paramString1, localObject2 })).booleanValue()) {
                //break label708;
            }
            XposedBridge.log("eeeeeeeeeeeeeeeee");
           String paramString4 = (String) XposedHelpers.newInstance(localClass1, new Object[0]);
           String paramString5 = (String) XposedHelpers.getObjectField(paramString4, paramWechatEntity.commit_video_moment_field4);
            XposedHelpers.setObjectField(paramString5, paramWechatEntity.commit_video_moment_field5, Integer.valueOf(0));
            XposedHelpers.setObjectField(paramString5, paramWechatEntity.commit_video_moment_field6, Boolean.valueOf(true));
            XposedHelpers.callMethod(XposedHelpers.getStaticObjectField(paramSet1, paramWechatEntity.commit_video_moment_field7), paramWechatEntity.commit_video_moment_method14, new Object[] { paramString1 });
            paramInt = ((Integer) XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_video_moment_method15, new Object[0])).intValue();
            sendCommitMoment(paramClassLoader, paramWechatEntity);
        }
      /*  for (;;)
        {


            XposedHelpers.callMethod(localObject5, paramWechatEntity.commit_video_moment_method9, new Object[] { Integer.valueOf(0) });
            break;
            label708:
            paramInt = 0;
            return paramInt;
        }*/

      return paramInt;

    }













    private static Object initArj(WechatEntity paramWechatEntity, Class paramClass1)
    {
        Object paramClass = XposedHelpers.newInstance(paramClass1, new Object[0]);
        XposedHelpers.setObjectField(paramClass, paramWechatEntity.init_commit_moment_field1, Float.valueOf(0.0F));
        XposedHelpers.setObjectField(paramClass, paramWechatEntity.init_commit_moment_field2, null);
        XposedHelpers.setObjectField(paramClass, paramWechatEntity.init_commit_moment_field3, null);
        XposedHelpers.setObjectField(paramClass, paramWechatEntity.init_commit_moment_field4, null);
        XposedHelpers.setObjectField(paramClass, paramWechatEntity.init_commit_moment_field5, Integer.valueOf(0));
        XposedHelpers.setObjectField(paramClass, paramWechatEntity.init_commit_moment_field6, Integer.valueOf(0));
        XposedHelpers.setObjectField(paramClass, paramWechatEntity.init_commit_moment_field7, null);
        XposedHelpers.setObjectField(paramClass, paramWechatEntity.init_commit_moment_field8, null);
        XposedHelpers.setObjectField(paramClass, paramWechatEntity.init_commit_moment_field9, null);
        XposedHelpers.setObjectField(paramClass, paramWechatEntity.init_commit_moment_field10, Integer.valueOf(0));
        XposedHelpers.setObjectField(paramClass, paramWechatEntity.init_commit_moment_field11, Integer.valueOf(0));
        XposedHelpers.setObjectField(paramClass, paramWechatEntity.init_commit_moment_field12, null);
        XposedHelpers.setObjectField(paramClass, paramWechatEntity.init_commit_moment_field13, null);
        XposedHelpers.setObjectField(paramClass, paramWechatEntity.init_commit_moment_field14, Float.valueOf(-1000.0F));
        XposedHelpers.setObjectField(paramClass, paramWechatEntity.init_commit_moment_field15, Float.valueOf(-1000.0F));
        XposedHelpers.setObjectField(paramClass, paramWechatEntity.init_commit_moment_field16, Integer.valueOf(0));
        return paramClass;
    }



    public static int sendPicMoment9(ClassLoader classLoader, WechatEntity wechatEntity, String content, int publicMode, Set<String> labelNames, JSONArray momentPicArr) {
        int i = 0;
        if (publicMode == 1) {
            i = 1;
        }
        List<String> list = new LinkedList();
        List<String> list2 = new LinkedList();
        if (publicMode == 2 || publicMode == 3) {
            list2 = WechatDb.getInstance().selectContactByLabelName(labelNames);
        }
        boolean z = false;
        if (publicMode == 3) {
            z = true;
        }
        Class class_data_h = XposedHelpers.findClass(wechatEntity.commit_pic_moment_class1, classLoader);
        Class class_arj = XposedHelpers.findClass(wechatEntity.commit_pic_moment_class2, classLoader);
        Class class_s = XposedHelpers.findClass(wechatEntity.commot_pic_moment_class3, classLoader);
        Class class_bqg = XposedHelpers.findClass(wechatEntity.commit_pic_moment_class4, classLoader);
        Class class_boj = XposedHelpers.findClass(wechatEntity.commit_pic_moment_class5, classLoader);
        Class class_ax = XposedHelpers.findClass(wechatEntity.commit_pic_moment_class6, classLoader);
        Class class_c_a = XposedHelpers.findClass(wechatEntity.commit_pic_moment_class7, classLoader);
        Class class_bpo = XposedHelpers.findClass(wechatEntity.commit_pic_moment_class8, classLoader);
        Object object_pInt = XposedHelpers.newInstance(XposedHelpers.findClass(wechatEntity.commit_pic_moment_class9, classLoader), new Object[0]);
        Object object_arj = initArj(wechatEntity, class_arj);
        List linkedList = new LinkedList();
        ArrayList<String> nQj = new ArrayList();
        for (int m = 0; m < momentPicArr.size(); m++) {
            nQj.add(momentPicArr.getString(m));
        }
        Iterator it = nQj.iterator();
        int i6 = 0;
        while (it.hasNext()) {
            Object object_data_h = XposedHelpers.newInstance(class_data_h, new Object[]{(String) it.next(), Integer.valueOf(2)});
            XposedHelpers.setObjectField(object_data_h, wechatEntity.commit_pic_moment_field1, Integer.valueOf(2));
            XposedHelpers.setObjectField(object_data_h, wechatEntity.commit_pic_moment_field2, Integer.valueOf(i));
            if (i6 == 0) {
                XposedHelpers.setObjectField(object_data_h, wechatEntity.commit_pic_moment_field3, Integer.valueOf(0));
            } else {
                XposedHelpers.setObjectField(object_data_h, wechatEntity.commit_pic_moment_field3, Integer.valueOf(0));
            }
            int i7 = i6 + 1;
            XposedHelpers.setObjectField(object_data_h, wechatEntity.commit_pic_moment_field4, Integer.valueOf(0));
            XposedHelpers.setObjectField(object_data_h, wechatEntity.commit_pic_moment_field5, content);
            XposedHelpers.setObjectField(object_data_h, wechatEntity.commit_pic_moment_field6, Boolean.valueOf(false));
            linkedList.add(object_data_h);
            i6 = i7;
        }
        LinkedList linkedList2 = new LinkedList();
        if (list != null) {
            List Hv = (List) XposedHelpers.callStaticMethod(class_s, wechatEntity.commit_pic_moment_method1, new Object[0]);
            for (String str32 : list) {
                if (!Hv.contains(str32)) {
                    Object object_bqg = XposedHelpers.newInstance(class_bqg, new Object[0]);
                    XposedHelpers.setObjectField(object_bqg, wechatEntity.commit_pic_moment_field7, str32);
                    linkedList2.add(object_bqg);
                }
            }
        }
        Object object_ax = XposedHelpers.newInstance(class_ax, new Object[]{Integer.valueOf(1)});
        Object object_afv = XposedHelpers.getObjectField(object_ax, wechatEntity.commit_pic_moment_field8);
        XposedHelpers.setObjectField(object_pInt, wechatEntity.commit_pic_moment_field9, object_afv);
        if (0 > ((Integer) XposedHelpers.getStaticObjectField(class_c_a, wechatEntity.commit_pic_moment_field10)).intValue()) {
            XposedHelpers.callMethod(object_ax, wechatEntity.commit_pic_moment_method2, new Object[]{Integer.valueOf(3)});
        }
        XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(object_ax, wechatEntity.commit_pic_moment_method3, new Object[]{content}), wechatEntity.commit_pic_moment_method4, new Object[]{object_arj}), wechatEntity.commit_pic_moment_method5, new Object[]{linkedList2}), wechatEntity.commit_pic_moment_method6, new Object[]{Integer.valueOf(i)}), wechatEntity.commit_pic_moment_method7, new Object[]{Integer.valueOf(0)}), wechatEntity.commit_pic_moment_method8, new Object[]{list2});
        if (z) {
            XposedHelpers.callMethod(object_ax, wechatEntity.commit_pic_moment_method9, new Object[]{Integer.valueOf(1)});
        } else {
            XposedHelpers.callMethod(object_ax, wechatEntity.commit_pic_moment_method9, new Object[]{Integer.valueOf(0)});
        }
        XposedHelpers.callMethod(object_ax, wechatEntity.commit_pic_moment_method10, new Object[]{Integer.valueOf(0)});
        if (false) {
            XposedHelpers.callMethod(object_ax, wechatEntity.commit_pic_moment_method10, new Object[]{Integer.valueOf(5)});
        }
        XposedHelpers.callMethod(object_ax, wechatEntity.commit_pic_moment_method11, new Object[]{null, null, null, Integer.valueOf(0), Integer.valueOf(0)});
        int score = ((Integer) XposedHelpers.getObjectField(object_arj, wechatEntity.commit_pic_moment_field11)).intValue();
        if (!(object_arj == null || score == 0)) {
            i6 = score;
            String str4 = (String) XposedHelpers.getObjectField(object_arj, wechatEntity.commit_pic_moment_field12);
            Object axVar_nsy = XposedHelpers.getObjectField(object_ax, wechatEntity.commit_pic_moment_field13);
            Object axVar_rWt = XposedHelpers.getObjectField(axVar_nsy, wechatEntity.commit_pic_moment_field14);
            XposedHelpers.setObjectField(axVar_nsy, wechatEntity.commit_pic_moment_field15, XposedHelpers.newInstance(class_boj, new Object[0]));
            XposedHelpers.setObjectField(axVar_rWt, wechatEntity.commit_pic_moment_field16, Integer.valueOf(i6));
            XposedHelpers.setObjectField(axVar_rWt, wechatEntity.commit_pic_moment_field17, str4);
        }
        for (int index = 0; index < linkedList.size(); index++) {
            Object com_tencent_mm_protocal_c_bpo = XposedHelpers.newInstance(class_bpo, new Object[0]);
            XposedHelpers.setObjectField(com_tencent_mm_protocal_c_bpo, wechatEntity.commit_pic_moment_field18, Float.valueOf(-1000.0f));
            XposedHelpers.setObjectField(com_tencent_mm_protocal_c_bpo, wechatEntity.commit_pic_moment_field19, Float.valueOf(-1000.0f));
            XposedHelpers.setObjectField(com_tencent_mm_protocal_c_bpo, wechatEntity.commit_pic_moment_field20, Float.valueOf(-1000.0f));
            XposedHelpers.setObjectField(com_tencent_mm_protocal_c_bpo, wechatEntity.commit_pic_moment_field21, Float.valueOf(-1000.0f));
            XposedHelpers.callMethod(XposedHelpers.getObjectField(XposedHelpers.getObjectField(object_ax, wechatEntity.commit_pic_moment_field22), wechatEntity.commit_pic_moment_field23), wechatEntity.commit_pic_moment_method12, new Object[]{com_tencent_mm_protocal_c_bpo});
        }
        XposedHelpers.callMethod(object_ax, wechatEntity.commit_pic_moment_method13, new Object[]{linkedList});
        int commit = ((Integer) XposedHelpers.callMethod(object_ax, wechatEntity.commit_pic_moment_method14, new Object[0])).intValue();
        sendCommitMoment(classLoader, wechatEntity);
        return commit;
    }


    private static void sendCommitMoment(ClassLoader paramClassLoader, WechatEntity paramWechatEntity)
    {
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass(paramWechatEntity.send_commit_moment_class1, paramClassLoader), paramWechatEntity.send_commit_moment_method1, new Object[0]), paramWechatEntity.send_commit_moment_method2, new Object[0]);
    }
}