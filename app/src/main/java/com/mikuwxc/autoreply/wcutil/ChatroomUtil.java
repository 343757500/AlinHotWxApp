package com.mikuwxc.autoreply.wcutil;


import com.mikuwxc.autoreply.wcentity.WechatEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class ChatroomUtil {
    public static void addChatroomMember(ClassLoader classLoader, WechatEntity wechatEntity, String str, String str2, String str3) throws Exception {
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(classLoader.loadClass(wechatEntity.add_friend_in_chatroom_class1), wechatEntity.add_friend_in_chatroom_method1, new Object[0]), wechatEntity.add_friend_in_chatroom_method2, new Object[0]);
        XposedHelpers.callStaticMethod(classLoader.loadClass(wechatEntity.add_friend_in_chatroom_class2), wechatEntity.add_friend_in_chatroom_method3, new Object[0]);
        Object callStaticMethod = XposedHelpers.callStaticMethod(classLoader.loadClass(wechatEntity.add_friend_in_chatroom_class2), wechatEntity.add_friend_in_chatroom_method4, new Object[0]);
        List arrayList = new ArrayList();
        List arrayList2 = new ArrayList();
        Map hashMap = new HashMap();
        arrayList.add(str2);
        arrayList2.add(Integer.valueOf(14));
        hashMap.put(str2, Integer.valueOf(0));
        Class loadClass = classLoader.loadClass(wechatEntity.add_friend_in_chatroom_class4);
        String str4 = wechatEntity.add_friend_in_chatroom_method5;
        Object[] r6 = new Object[2];
        r6[0] = XposedHelpers.newInstance(loadClass, new Object[]{Integer.valueOf(2), arrayList, arrayList2, null, str3, "", hashMap, str, ""});
        r6[1] = Integer.valueOf(0);
        XposedHelpers.callMethod(callStaticMethod, str4, r6);
    }

    public static void createChatroom(ClassLoader classLoader, WechatEntity wechatEntity, String str, List<String> list) throws Exception {
        Object callStaticMethod = XposedHelpers.callStaticMethod(classLoader.loadClass(wechatEntity.create_chatroom_class1), wechatEntity.create_chatroom_method1, new Object[0]);
        Object newInstance = XposedHelpers.newInstance(classLoader.loadClass(wechatEntity.create_chatroom_class2), new Object[]{str, list});
        XposedHelpers.callMethod(callStaticMethod, wechatEntity.create_chatroom_method2, new Object[]{newInstance, Integer.valueOf(0)});
    }

    public static void inventFriendInChatroom(ClassLoader classLoader, WechatEntity wechatEntity, String str, List<String> list) throws Exception {
        Object callStaticMethod = XposedHelpers.callStaticMethod(classLoader.loadClass(wechatEntity.invent_friend_in_chatroom_class1), wechatEntity.invent_friend_in_chatroom_method1, new Object[0]);
        Object newInstance = XposedHelpers.newInstance(classLoader.loadClass(wechatEntity.invent_friend_in_chatroom_class2), new Object[]{str, list, null});
        XposedHelpers.callMethod(callStaticMethod, wechatEntity.invent_friend_in_chatroom_method2, new Object[]{newInstance, Integer.valueOf(0)});
    }

    public static void removeFriendInChatroom(ClassLoader classLoader, WechatEntity wechatEntity, String str, List<String> list) throws Exception {
        Object callStaticMethod = XposedHelpers.callStaticMethod(classLoader.loadClass(wechatEntity.remove_friend_in_chatroom_class1), wechatEntity.remove_friend_in_chatroom_method1, new Object[0]);
        Object newInstance = XposedHelpers.newInstance(classLoader.loadClass(wechatEntity.remove_friend_in_chatroom_class2), new Object[]{str, list, Integer.valueOf(1)});
        XposedHelpers.callMethod(callStaticMethod, wechatEntity.remove_friend_in_chatroom_method2, new Object[]{newInstance, Integer.valueOf(0)});
    }

    public static ArrayList refreashCreateChatroom(ClassLoader paramClassLoader, WechatEntity paramWechatEntity, Object paramObject)
    {
        Object localObject = XposedHelpers.getObjectField(XposedHelpers.getObjectField(XposedHelpers.getObjectField(paramObject, paramWechatEntity.refreash_create_chatroom_field1), paramWechatEntity.refreash_create_chatroom_field2), paramWechatEntity.refreash_create_chatroom_field3);
        paramObject = (String)XposedHelpers.getObjectField(XposedHelpers.getObjectField(localObject, paramWechatEntity.refreash_create_chatroom_field4), paramWechatEntity.refreash_create_chatroom_field5);
        Object localObject2 = (LinkedList)XposedHelpers.getObjectField(localObject, paramWechatEntity.refreash_create_chatroom_field6);
        ArrayList localObject1 = new ArrayList();
        localObject2 = ((LinkedList)localObject2).iterator();
        while (((Iterator)localObject2).hasNext()) {
            ((List)localObject1).add((String)XposedHelpers.getObjectField(XposedHelpers.getObjectField(((Iterator)localObject2).next(), paramWechatEntity.refreash_create_chatroom_field7), paramWechatEntity.refreash_create_chatroom_field8));
        }
        //加了这句会显示两句拉人进群的信息
        // XposedHelpers.callStaticMethod(XposedHelpers.findClass(paramWechatEntity.refreash_create_chatroom_class1, paramClassLoader), paramWechatEntity.refreash_create_chatroom_method1, new Object[] { paramObject, localObject1, "你邀请%s加入了群聊", Boolean.valueOf(false), "" });
        XposedBridge.log(localObject1.toString());
        return localObject1;
    }
}