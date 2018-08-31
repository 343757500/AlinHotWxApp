package com.mikuwxc.autoreply.db;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.mikuwxc.autoreply.bean.Phone;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/12/9.
 */
public class PhoneUserHelper {
    public static void savePhoneList(List<Phone> phoneUserList) {
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < phoneUserList.size(); i++) {
                PhoneUser phoneUser = new PhoneUser();
                phoneUser.setPhoneId(phoneUserList.get(i).getId());
                phoneUser.setPhoneNum(phoneUserList.get(i).getPhoneNum());
                phoneUser.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static void updateWechatName(long phoneId, String weChatNick) {
        List<PhoneUser> execute = new Select().from(PhoneUser.class).where("phoneId = ?", phoneId).execute();
        PhoneUser phoneUser = execute.get(0);
        phoneUser.setNickName(weChatNick.trim());
        phoneUser.save();
    }

    public static void updatePhoneName(int phoneId, String phoneName) {
        List<PhoneUser> execute = new Select().from(PhoneUser.class).where("phoneId = ?", phoneId).execute();
        PhoneUser phoneUser = execute.get(0);
        phoneUser.settName(phoneName);
        phoneUser.save();
    }

    public static void updateType(Long phoneId, int type) {
        List<PhoneUser> execute = new Select().from(PhoneUser.class).where("phoneId = ?", phoneId).execute();
        PhoneUser phoneUser = execute.get(0);
        phoneUser.setAddType(type);
        phoneUser.save();
    }

    public static void updateStatus(Long phoneId, int status) {
        List<PhoneUser> execute = new Select().from(PhoneUser.class).where("phoneId = ?", phoneId).execute();
        PhoneUser phoneUser = execute.get(0);
        phoneUser.setStatus(status);
        phoneUser.save();
    }

    public static void updateTime(Long phoneId) {
        List<PhoneUser> execute = new Select().from(PhoneUser.class).where("phoneId = ?", phoneId).execute();
        PhoneUser phoneUser = execute.get(0);
        phoneUser.setTime(Long.decode(new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis()))));
        phoneUser.save();
    }

    public static long selectPhoneByName(String name) {
        List<PhoneUser> execute = new Select().from(PhoneUser.class).where("nickName = ?", name).execute();
        if (!execute.isEmpty())
            return execute.get(0).getPhoneId();
        else
            return 0;
    }

    /**
     * @param type -1 查询全部 0 查询未添加的用户  1 添加过的用户 2 回应通过的用户 3 添加失败  4没有此用户
     * @return
     */
    public static List<PhoneUser> selectPhoneByStatus(int type) {
        if (type == -1) {
            return new Select().from(PhoneUser.class).execute();
        } else {
            return new Select().from(PhoneUser.class).where("status=?", type).execute();
        }
    }

    /**
     * 查询今天操作的数据
     *
     * @return
     */
    public static List<PhoneUser> selectPhoneByTime() {
        return new Select().from(PhoneUser.class).where("time = ?", Long.decode(new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis())))).execute();
    }
}
