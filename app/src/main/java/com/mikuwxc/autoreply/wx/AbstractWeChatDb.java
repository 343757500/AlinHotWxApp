package com.mikuwxc.autoreply.wx;

import android.database.Cursor;
import android.support.v4.view.InputDeviceCompat;

import com.alibaba.fastjson.JSONArray;
import com.mikuwxc.autoreply.wcentity.ChatroomEntity;
import com.mikuwxc.autoreply.wcentity.MemberEntity;
import com.mikuwxc.autoreply.wcentity.MessageEntity;
import com.mikuwxc.autoreply.wcentity.UserEntity;
import com.mikuwxc.autoreply.wcentity.WxEntity;
import com.mikuwxc.autoreply.wcutil.ArrayUtil;
import com.mikuwxc.autoreply.wcutil.OtherUtils;
import com.mikuwxc.autoreply.wcutil.Rcontactlvbuff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XposedHelpers;

public abstract class AbstractWeChatDb {
    protected static final String SELECT_BIGIMAGE_SQL = "select bigImgPath from ImgInfo2 where msglocalid == ";
    protected static final String SELECT_CHATROOM_SQL = "select c.chatroomname,c.memberlist,c.displayname,c.roomowner,r.conRemark,r.nickname,r.pyInitial,r.quanPin,c.roomdata,i.reserved1,i.reserved2 from chatroom c join rcontact r on c.chatroomname = r.username join img_flag i on c.chatroomname = i.username where c.roomowner is not null";
    protected static final String SELECT_CONTACT_SQL = "select r.username,r.alias,r.conRemark,r.nickname,r.pyInitial,r.quanPin,r.lvbuff,r.encryptUsername,r.contactLabelIds,i.reserved1,i.reserved2 from rcontact r left join img_flag i on r.username = i.username  where (r.type & 1 != 0 and r.type & 8 = 0 and r.type & 32 = 0 and r.verifyFlag & 8 = 0 and r.username not like '%@%' and r.username != 'filehelper' ) ";
    protected static final String SELECT_HEADPIC_SQL = "select username,reserved1,reserved2 from img_flag";
    protected static final String SELECT_LABEL_SQL = "select labelID,labelName from ContactLabel";
    protected static final String SELECT_MSG_ID_SQL = "select * from message where msgId == ";
    protected static final String SELECT_MSG_SVR_ID_SQL = "select * from message where msgSvrId == ";
    protected static final String SELECT_ONE_CHATROOM_SQL = "select c.chatroomname,c.memberlist,c.displayname,c.roomowner,r.conRemark,r.nickname,r.pyInitial,r.quanPin,c.roomdata,i.reserved1,i.reserved2 from chatroom c join rcontact r on c.chatroomname = r.username join img_flag i on c.chatroomname = i.username where c.roomowner is not null and c.chatroomname={0}";
    protected static final String SELECT_ONE_CONTACT_SQL = "select rcontact.username,rcontact.alias,rcontact.conRemark,rcontact.nickname,rcontact.pyInitial,rcontact.quanPin,rcontact.lvbuff,rcontact.encryptUsername,rcontact.contactLabelIds from rcontact where rcontact.username = '{0}' and rcontact.username not like '%@%' and rcontact.username != 'filehelper' order by rcontact.pyInitial limit 1";
    protected static final String SELECT_SELF_ENTITY_SQL = "select id,value from userinfo where id in (2,4,5,6,29,42,12290,12291,12293)";
    protected static final String SELECT_SELF_HEAD_PIC_SQL = "select sid,value from userinfo2 where sid == 'USERINFO_SELFINFO_SMALLIMGURL_STRING'";
    protected static final String SELECT_SELF_ID_SQL = "select id,value from userinfo where id = 2";
    protected static final String SELECT_TEMP_CONTACT_SQL = "select r.username,r.alias,r.conRemark,r.nickname,r.pyInitial,r.quanPin,r.lvbuff,r.encryptUsername,r.contactLabelIds,i.reserved1,i.reserved2 from rcontact r left join img_flag i on r.username = i.username where r.type & 1 =0 and r.type & 2 !=0 and r.username not like '%@%' and r.username != 'filehelper'  and r.username not like 'gh_%' ";
    protected static final String SELECT_VOICE_LENGTH_SQL = "select VoiceLength from voiceinfo where MsgLocalId == ";
    public static Object sqliteDatabase;

    public static String getRegion(byte[] bArr) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = bArr[48];
        if (i < 0) {
            i &= InputDeviceCompat.SOURCE_CLASS_MASK;
        }
        byte b = bArr[(i + 48) + 2];
        if (b == (byte) 0) {
            try {
                return stringBuilder.toString();
            } catch (Throwable e) {
                //ThrowableExtension.printStackTrace(e);
            }
        } else {
            i = ((i + 48) + 2) + 1;
            int i2 = b + i;
            stringBuilder.append(new String(ArrayUtil.copyOfRange(bArr, i, i2)));
            byte b2 = bArr[i2 + 1];
            i2 += 2;
            stringBuilder.append(new String(ArrayUtil.copyOfRange(bArr, i2, b2 + i2)));
            return stringBuilder.toString();
        }
        return stringBuilder.toString();
    }


    public static String getUniqueNameFromPath(String str) {
        return str.substring(str.indexOf("MicroMsg/") + "MicroMsg/".length(), str.indexOf("/EnMicroMsg.db"));
    }

    public static void initWechatDb(Object obj) {
        synchronized (AbstractWeChatDb.class) {
            try {
                sqliteDatabase = obj;
            } finally {
                Class cls = AbstractWeChatDb.class;
            }
        }
    }

    public static ChatroomEntity parseChatroom(Cursor cursor, UserEntity userEntity, HashMap<String, String> hashMap, HashMap<String, WxEntity> hashMap2) throws Exception {
        String string = cursor.getString(0);
        String string2 = cursor.getString(1);
        String string3 = cursor.getString(2);
        String string4 = cursor.getString(3);
        String string5 = cursor.getString(4);
        String string6 = cursor.getString(5);
        String string7 = cursor.getString(6);
        String string8 = cursor.getString(7);
        ChatroomEntity chatroomEntity = new ChatroomEntity();
        chatroomEntity.setChatroomId(string);
        String[] split = string2.split(";");
        String[] split2 = string3.split("、");
        String[] split3 = string3.split(",");
        LinkedList linkedList = new LinkedList();
        for (int i = 0; i < split.length; i++) {
            String str = split[i];
            String headPic = str.equals(userEntity.getUserTalker()) ? userEntity.getHeadPic() : (String) hashMap.get(str);
            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setWechatId(str);
            WxEntity wxEntity;
            if (split2.length > split3.length) {
                if (split2[i].equals(str)) {
                    wxEntity = (WxEntity) hashMap2.get(str);
                    if (wxEntity != null) {
                        memberEntity.setNickName(wxEntity.getNickName());
                    } else {
                        memberEntity.setNickName(split2[i]);
                    }
                } else {
                    memberEntity.setNickName(split2[i]);
                }
            } else if (split3[i].equals(str)) {
                wxEntity = (WxEntity) hashMap2.get(str);
                if (wxEntity != null) {
                    memberEntity.setNickName(wxEntity.getNickName());
                } else {
                    memberEntity.setNickName(split3[i]);
                }
            } else {
                memberEntity.setNickName(split3[i]);
            }
            if (OtherUtils.isEmpty(headPic)) {
                headPic = "";
            }
            memberEntity.setHeadPic(headPic);
            memberEntity.setStatus(1);
            linkedList.add(memberEntity);
        }
        chatroomEntity.setMembers(linkedList);
        chatroomEntity.setChatroomOwner(string4);
        chatroomEntity.setConRemark(string5);
        chatroomEntity.setNickName(OtherUtils.isEmpty(string6) ? string3 : string6);
        chatroomEntity.setPyInitial(string7);
        chatroomEntity.setQuanPin(string8);
        chatroomEntity.setChatroomHeadPic((String) hashMap.get(string));
        return chatroomEntity;
    }

    public static WxEntity parseFriend(Cursor cursor, String str, HashMap<String, String> hashMap, HashMap<String, String> hashMap2) {
        int i = 0;
        String string = cursor.getString(0);
        String string2 = cursor.getString(1);
        if (str.equals(string) || str.equals(string2)) {
            return null;
        }
        String string3 = cursor.getString(2);
        String string4 = cursor.getString(3);
        String string5 = cursor.getString(4);
        String string6 = cursor.getString(5);
        byte[] blob = cursor.getBlob(6);
        String string7 = cursor.getString(7);
        String string8 = cursor.getString(8);
        WxEntity wxEntity = new WxEntity();
        wxEntity.setUserName(string);
        wxEntity.setAlias(string2);
        wxEntity.setConRemark(string3);
        wxEntity.setNickName(string4);
        wxEntity.setPyInitial(string5);
        wxEntity.setQuanPin(string6);
        string = (String) hashMap.get(string);
        if (OtherUtils.isEmpty(string)) {
            string = "";
        }
        wxEntity.setHeadPic(string);
        if (blob != null) {
            byte b = blob[8];
            Rcontactlvbuff rcontactlvbuff = new Rcontactlvbuff();
            rcontactlvbuff.field_lvbuff = blob;
            rcontactlvbuff.pI();
            int i2 = rcontactlvbuff.bbt;
            string = OtherUtils.isEmpty(rcontactlvbuff.bAX) ? "" : rcontactlvbuff.bAX;
            wxEntity.setGender(b);
            wxEntity.setAddFrom(i2);
            wxEntity.setPhone(string);
        }
        LinkedList linkedList = new LinkedList();
        if (string8 != null) {
            String[] split = string8.split(",");
            int length = split.length;
            while (i < length) {
                string = (String) hashMap2.get(split[i]);
                if (string != null) {
                    linkedList.add(string);
                }
                i++;
            }
        }
        wxEntity.setRegion(getRegion(blob));
        wxEntity.setEntryptUsername(string7);
        wxEntity.setLabelIds(string8);
        wxEntity.setLabelNames(JSONArray.toJSONString(linkedList));
        wxEntity.setSelfWxId(str);
        return wxEntity;
    }

    protected ChatroomEntity parseChatroom(Cursor cursor) {
        ChatroomEntity chatroomEntity = new ChatroomEntity();
        chatroomEntity.setChatroomId(cursor.getString(0));
        String string = cursor.getString(2);
        String string2 = cursor.getString(5);
        chatroomEntity.setChatroomOwner(cursor.getString(3));
        chatroomEntity.setConRemark(cursor.getString(4));
        chatroomEntity.setPyInitial(cursor.getString(6));
        chatroomEntity.setQuanPin(cursor.getString(7));
        if (OtherUtils.isEmpty(string2)) {
            string2 = string;
        }
        chatroomEntity.setNickName(string2);
        string2 = cursor.getString(9);
        string = cursor.getString(10);
        if (OtherUtils.isEmpty(string2)) {
            string2 = string;
        }
        if (OtherUtils.isEmpty(string2)) {
            string2 = "";
        }
        chatroomEntity.setChatroomHeadPic(string2);
        chatroomEntity.setOpType(2);
        return chatroomEntity;
    }

    protected WxEntity parseFriend(Cursor cursor, HashMap<String, String> hashMap) {
        String string = cursor.getString(0);
        String string2 = cursor.getString(1);
        String string3 = cursor.getString(2);
        String string4 = cursor.getString(3);
        String string5 = cursor.getString(4);
        String string6 = cursor.getString(5);
        byte[] blob = cursor.getBlob(6);
        String string7 = cursor.getString(7);
        String string8 = cursor.getString(8);
        String string9 = cursor.getString(9);
        String string10 = cursor.getString(10);
        if (OtherUtils.isEmpty(string9)) {
            string9 = string10;
        }
        string10 = OtherUtils.isEmpty(string9) ? "" : string9;
        WxEntity wxEntity = new WxEntity();
        wxEntity.setUserName(string);
        wxEntity.setAlias(string2);
        wxEntity.setConRemark(string3);
        wxEntity.setNickName(string4);
        wxEntity.setPyInitial(string5);
        wxEntity.setQuanPin(string6);
        wxEntity.setOpType(2);
        if (blob != null) {
            byte b = blob[8];
            Rcontactlvbuff rcontactlvbuff = new Rcontactlvbuff();
            rcontactlvbuff.field_lvbuff = blob;
            rcontactlvbuff.pI();
            int i = rcontactlvbuff.bbt;
            string9 = OtherUtils.isEmpty(rcontactlvbuff.bAX) ? "" : rcontactlvbuff.bAX;
            wxEntity.setGender(b);
            wxEntity.setAddFrom(i);
            wxEntity.setPhone(string9);
        }
        LinkedList linkedList = new LinkedList();
        if (string8 != null) {
            for (Object obj : string8.split(",")) {
                string9 = (String) hashMap.get(obj);
                if (string9 != null) {
                    linkedList.add(string9);
                }
            }
        }
        wxEntity.setRegion(getRegion(blob));
        wxEntity.setEntryptUsername(string7);
        wxEntity.setHeadPic(string10);
        wxEntity.setLabelIds(string8);
        wxEntity.setLabelNames(JSONArray.toJSONString(linkedList));
        return wxEntity;
    }

    protected Cursor query(String str) {
        return (Cursor) XposedHelpers.callMethod(sqliteDatabase, "rawQuery", new Object[]{str, null});
    }

    public abstract ChatroomEntity selectChatRoomContact(HashMap<String, String> hashMap, HashMap<String, WxEntity> hashMap2, UserEntity userEntity, String str) throws Exception;

    public abstract JSONArray selectChatRoomContactJa(HashMap<String, String> hashMap, HashMap<String, WxEntity> hashMap2, UserEntity userEntity) throws Exception;

    public abstract List<ChatroomEntity> selectChatrooms(Set<String> set);

    public abstract WxEntity selectContact(HashMap<String, String> hashMap, String str, String str2, HashMap<String, String> hashMap2) throws Exception;

    public abstract HashMap<String, WxEntity> selectContactTree(HashMap<String, String> hashMap, String str, HashMap<String, String> hashMap2) throws Exception;

    public abstract List<WxEntity> selectContacts(Set<String> set);

    public abstract HashMap<String, String> selectHeadPics() throws Exception;

    public abstract HashMap<String, String> selectLabel() throws Exception;

    public abstract ArrayList<String> selectLabels() throws Exception;

    public abstract MessageEntity selectMsgByMsgId(long j) throws Exception;

    public abstract UserEntity selectSelf() throws Exception;

    public abstract String selectSendPicPathByMsgId(String str) throws Exception;

    public abstract long selectWxVoiceDuration(long j) throws Exception;

    public void updateVoiceInfo(int i, int i2, String str) {
        String str2 = "update voiceinfo set Status=3,netoffset=0,totallen=" + i + ",voicelength=" + i2 + " where filename='" + str + "'";
        XposedHelpers.callMethod(sqliteDatabase, "execSQL", new Object[]{str2});
    }
}