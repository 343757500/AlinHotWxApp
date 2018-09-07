package com.mikuwxc.autoreply.wcutil;

import com.alibaba.fastjson.JSONObject;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.common.util.MyFileUtil;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wcentity.WxEntity;
import com.mikuwxc.autoreply.wcloop.ClearBlackFriendBasket;
import com.mikuwxc.autoreply.wx.WechatDb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class FriendUtil {
    public static void addFriend1(ClassLoader classLoader, WechatEntity wechatEntity, String str, String str2, int i) {
        List arrayList = new ArrayList();
        List arrayList2 = new ArrayList();
        List arrayList3 = new ArrayList();
        arrayList.add(str);
        arrayList2.add(str2);
        arrayList3.add(Integer.valueOf(i));
        Object callStaticMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.add_search_friend_class1, classLoader), wechatEntity.add_search_friend_method1, new Object[0]);
        Class findClass = XposedHelpers.findClass(wechatEntity.add_search_friend_class4, classLoader);
        String str3 = wechatEntity.add_search_friend_method5;
        Object[] r6 = new Object[2];
        r6[0] = XposedHelpers.newInstance(findClass, new Object[]{Integer.valueOf(1), arrayList, arrayList3, arrayList2, "", "", null, "", ""});
        r6[1] = Integer.valueOf(0);
        XposedHelpers.callMethod(callStaticMethod, str3, r6);
    }

    public static void addFriend2(ClassLoader classLoader, WechatEntity wechatEntity, String str, String str2, int i) {
        List arrayList = new ArrayList();
        List arrayList2 = new ArrayList();
        HashMap hashMap = new HashMap();
        arrayList.add(str);
        arrayList2.add(Integer.valueOf(i));
        Object callStaticMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.add_search_friend_class1, classLoader), wechatEntity.add_search_friend_method1, new Object[0]);
        Class findClass = XposedHelpers.findClass(wechatEntity.add_search_friend_class4, classLoader);
        String str3 = wechatEntity.add_search_friend_method5;
        Object[] r6 = new Object[2];
        r6[0] = XposedHelpers.newInstance(findClass, new Object[]{Integer.valueOf(2), arrayList, arrayList2, null, str2, "", hashMap, null, ""});
        r6[1] = Integer.valueOf(0);
        XposedHelpers.callMethod(callStaticMethod, str3, r6);
    }

    public static void addFriendWithUpdateRemark(ClassLoader classLoader, WechatEntity wechatEntity, String str, String str2, String str3, int i) {
        Object newInstance = XposedHelpers.newInstance(XposedHelpers.findClass(wechatEntity.add_friend_with_update_remark_class1, classLoader), new Object[]{str});
        XposedHelpers.setObjectField(newInstance, wechatEntity.add_friend_with_update_remark_field1, str2);
        XposedHelpers.setObjectField(newInstance, wechatEntity.add_friend_with_update_remark_field2, "");
        XposedHelpers.setObjectField(newInstance, wechatEntity.add_friend_with_update_remark_field3, "");
        if (i == 15) {
            XposedHelpers.setObjectField(newInstance, wechatEntity.add_friend_with_update_remark_field4, str3);
        }
        Class findClass = XposedHelpers.findClass(wechatEntity.add_friend_with_update_remark_class2, classLoader);
        Class findClass2 = XposedHelpers.findClass(wechatEntity.add_friend_with_update_remark_class3, classLoader);
        XposedHelpers.callStaticMethod(findClass, wechatEntity.add_friend_with_update_remark_method2, new Object[0]);
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(findClass2, wechatEntity.add_friend_with_update_remark_method1, new Object[0]), wechatEntity.add_friend_with_update_remark_method3, new Object[]{newInstance});
    }

    public static void autoVerifyUser(ClassLoader paramClassLoader, WechatEntity wechatEntity, String str, String str2, int i) throws Exception {
        XposedBridge.log("000000000000000000");
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(paramClassLoader.loadClass(wechatEntity.auto_verify_user_class1), wechatEntity.auto_verify_user_method1, new Object[0]), wechatEntity.auto_verify_user_method2, new Object[0]);
        XposedBridge.log("11111111");
        XposedHelpers.callStaticMethod(paramClassLoader.loadClass(wechatEntity.auto_verify_user_class2), wechatEntity.auto_verify_user_method3, new Object[0]);
        XposedBridge.log("122222222222");

        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(paramClassLoader.loadClass(wechatEntity.auto_verify_user_class3), wechatEntity.auto_verify_user_method4, new Object[0]), wechatEntity.auto_verify_user_method5, new Object[] { XposedHelpers.newInstance(paramClassLoader.loadClass(wechatEntity.auto_verify_user_class4), new Object[] { str, str2, Integer.valueOf(i) }), Integer.valueOf(0) });
        XposedBridge.log("133333333333");
    }

    public static void clearBlackFriend(ClearBlackFriendBasket clearBlackFriendBasket, long j, List<String> list) {
        FileIoUtil.setValueToPath("", false, "/mnt/sdcard/blackFriends.txt");
        FileIoUtil.setValueToPath(String.valueOf(j), false, "/mnt/sdcard/blackFriendsTaskId.txt");
        String userTalker = WechatDb.getInstance().selectSelf().getUserTalker();
        Set set = null;
        if (!OtherUtils.isEmpty(list) && list.size() > 0) {
            Set hashSet = new HashSet();
            for (String add : list) {
                hashSet.add(add);
            }
            set = hashSet;
        }
        Set<String> r0 = null;
        for (WxEntity wxEntity : WechatDb.getInstance().selectContacts(r0)) {
            if (!wxEntity.getUserName().equals(userTalker)) {
                clearBlackFriendBasket.produce(wxEntity);
            }
        }
    }

    public static void clearBlackFriend(ClassLoader classLoader, WechatEntity wechatEntity) {
        String valueFromPath = FileIoUtil.getValueFromPath("/mnt/sdcard/wxid.txt");
        XposedBridge.log("成功hook微信4444444444444444444444444444444444"+valueFromPath);
        String valueFromPath2 = FileIoUtil.getValueFromPath("/mnt/sdcard/nickname.txt");
        Object newInstance = XposedHelpers.newInstance(XposedHelpers.findClass(wechatEntity.clear_black_friend_class1, classLoader), new Object[0]);
        XposedHelpers.setObjectField(XposedHelpers.newInstance(XposedHelpers.findClass(wechatEntity.clear_black_friend_class2, classLoader), new Object[]{Double.valueOf(1.0d), "1", valueFromPath, valueFromPath2, Integer.valueOf(31), Integer.valueOf(2), null, null, null, Integer.valueOf(11), "", "", null, valueFromPath2, valueFromPath2, newInstance}), wechatEntity.clear_black_friend_field1, "RemittanceProcess");
        Class findClass = XposedHelpers.findClass(wechatEntity.clear_black_friend_class3, classLoader);
        XposedHelpers.callStaticMethod(findClass, wechatEntity.clear_black_friend_method1, new Object[0]);
        XposedHelpers.callMethod(XposedHelpers.getObjectField(XposedHelpers.callStaticMethod(findClass, wechatEntity.clear_black_friend_method2, new Object[0]), wechatEntity.clear_black_friend_field2), wechatEntity.clear_black_friend_method3, new Object[]{valueFromPath, Integer.valueOf(0)});
        FileIoUtil.setValueToPath(valueFromPath, false, "/mnt/sdcard/blackFriend.txt");
    }

    public static void clearBlackFriend(ClassLoader classLoader, WechatEntity wechatEntity, String str, String str2) throws Exception {
        Object newInstance = XposedHelpers.newInstance(XposedHelpers.findClass(wechatEntity.clear_black_friend_class0, classLoader), new Object[]{str});
        XposedHelpers.callMethod(XposedHelpers.getObjectField(XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.clear_black_friend_class3, classLoader), wechatEntity.clear_black_friend_method2, new Object[0]), wechatEntity.clear_black_friend_field2), wechatEntity.clear_black_friend_method3, new Object[]{newInstance, Integer.valueOf(0)});
        FileIoUtil.setValueToPath(str, false, "/mnt/sdcard/wxid.txt");
        FileIoUtil.setValueToPath(str2, false, "/mnt/sdcard/nickname.txt");
    }

    public static void deleteFriend(ClassLoader classLoader, WechatEntity wechatEntity, String str) {
        Class findClass = XposedHelpers.findClass(wechatEntity.delete_contact_class1, classLoader);
        Class findClass2 = XposedHelpers.findClass(wechatEntity.delete_contact_class2, classLoader);
        Class findClass3 = XposedHelpers.findClass(wechatEntity.delete_contact_class3, classLoader);
        XposedHelpers.callStaticMethod(findClass, wechatEntity.delete_contact_method1, new Object[0]);
        Object callStaticMethod = XposedHelpers.callStaticMethod(findClass2, wechatEntity.delete_contact_method2, new Object[0]);
        Object newInstance = XposedHelpers.newInstance(findClass3, new Object[]{str, Integer.valueOf(0)});
        XposedHelpers.callMethod(callStaticMethod, wechatEntity.delete_contact_method3, new Object[]{newInstance});
    }

    public static void searchFriend(ClassLoader classLoader, WechatEntity wechatEntity, long j, String str, String str2, String str3, int i) throws Exception {
        MyFileUtil.writeToNewFile(AppConfig.APP_ADD , str2);
        Object callStaticMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.add_search_friend_class1, classLoader), wechatEntity.add_search_friend_method1, new Object[0]);
        Object newInstance = XposedHelpers.newInstance(classLoader.loadClass(wechatEntity.add_search_friend_class2), new Object[]{str2, Integer.valueOf(i)});
        XposedHelpers.callMethod(callStaticMethod, wechatEntity.add_search_friend_method2, new Object[]{newInstance, Integer.valueOf(0)});
    }


    public static void autoVerifyUser3(ClassLoader pluginLoader, WechatEntity wechatEntity, String wxid, String stranger, int scene) throws Exception {
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(pluginLoader.loadClass(wechatEntity.auto_verify_user_class1), wechatEntity.auto_verify_user_method1, new Object[0]), wechatEntity.auto_verify_user_method2, new Object[0]);
        XposedHelpers.callStaticMethod(pluginLoader.loadClass(wechatEntity.auto_verify_user_class2), wechatEntity.auto_verify_user_method3, new Object[0]);
        Object object2 = XposedHelpers.callStaticMethod(pluginLoader.loadClass(wechatEntity.auto_verify_user_class3), wechatEntity.auto_verify_user_method4, new Object[0]);
        String str = wechatEntity.auto_verify_user_method5;
        Object[] objArr = new Object[2];
        objArr[0] = XposedHelpers.newInstance(pluginLoader.loadClass(wechatEntity.auto_verify_user_class4), new Object[]{wxid, stranger, Integer.valueOf(scene)});
        objArr[1] = Integer.valueOf(0);
        XposedHelpers.callMethod(object2, str, objArr);
    }





    public static void addFriend11(ClassLoader classLoader, WechatEntity wechatEntity, String stranger1, String stranger2, int scene) {
        Object []args = new Object[9];
        List<String> strangerList1 = new ArrayList();
        List<String> strangerList2 = new ArrayList();
        List<Integer> wechatSceneList = new ArrayList();
        strangerList1.add(stranger1);
        strangerList2.add(stranger2);
        wechatSceneList.add(Integer.valueOf(scene));
        args[0] = Integer.valueOf(1);
        args[1] = strangerList1;
        args[2] = wechatSceneList;
        args[3] = strangerList2;
        args[4] = "";
        args[5] = "";
        args[6] = null;
        args[7] = "";
        args[8] = "";
        Object object2 = XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.add_search_friend_class1, classLoader), wechatEntity.add_search_friend_method1, new Object[0]);
        Class class_o = XposedHelpers.findClass(wechatEntity.add_search_friend_class4, classLoader);
        XposedHelpers.callMethod(object2, wechatEntity.add_search_friend_method5, new Object[]{XposedHelpers.newInstance(class_o, args), Integer.valueOf(0)});
    }

    //微信号加好友
    public static void addFriend12(ClassLoader classLoader, WechatEntity wechatEntity, String stranger1, String sendWord, int scene) {
        Object[] args = new Object[9];
        List<String> strangerList1 = new ArrayList();
        List<Integer> wechatSceneList = new ArrayList();
        Map<String, Integer> wechatTree = new HashMap();
        strangerList1.add(stranger1);
        wechatSceneList.add(Integer.valueOf(scene));
        args[0] = Integer.valueOf(2);
        args[1] = strangerList1;
        args[2] = wechatSceneList;
        args[3] = null;
        args[4] = sendWord;
        args[5] = "";
        args[6] = wechatTree;
        args[7] = null;
        args[8] = "";
        Object object2 = XposedHelpers.callStaticMethod(XposedHelpers.findClass(wechatEntity.add_search_friend_class1, classLoader), wechatEntity.add_search_friend_method1, new Object[0]);
        Class class_o = XposedHelpers.findClass(wechatEntity.add_search_friend_class4, classLoader);
        XposedHelpers.callMethod(object2, wechatEntity.add_search_friend_method5, new Object[]{XposedHelpers.newInstance(class_o, args), Integer.valueOf(0)});
    }
}