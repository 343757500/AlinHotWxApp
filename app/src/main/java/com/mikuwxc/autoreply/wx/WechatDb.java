package com.mikuwxc.autoreply.wx;

import android.database.Cursor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mikuwxc.autoreply.wcentity.AutoVerifyEntity;
import com.mikuwxc.autoreply.wcentity.ChatroomEntity;
import com.mikuwxc.autoreply.wcentity.MemberEntity;
import com.mikuwxc.autoreply.wcentity.MessageEntity;
import com.mikuwxc.autoreply.wcentity.RequestParameters;
import com.mikuwxc.autoreply.wcentity.UserEntity;
import com.mikuwxc.autoreply.wcentity.WxEntity;
import com.mikuwxc.autoreply.wcutil.MatchUtil;
import com.mikuwxc.autoreply.wcutil.OtherUtils;
import com.orhanobut.logger.Logger;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class WechatDb extends AbstractWeChatDb {
    private static WechatDb wechatDb;
    private String str2;

    private WechatDb() {
    }

    public static WechatDb getInstance() {
        synchronized (WechatDb.class) {
            try {
                if (wechatDb == null) {
                    wechatDb = new WechatDb();
                }
                WechatDb wechatDb = new WechatDb();
                return wechatDb;
            } finally {
                Object obj = WechatDb.class;
            }
        }
    }

    private boolean includeChatroom(String str, List<ChatroomEntity> list) {
        for (ChatroomEntity chatroomId : list) {
            if (str.equals(chatroomId.getChatroomId())) {
                return true;
            }
        }
        return false;
    }

    private boolean includeContact(String str, List<WxEntity> list) {
        for (WxEntity userName : list) {
            if (str.equals(userName.getUserName())) {
                return true;
            }
        }
        return false;
    }

    private AutoVerifyEntity parseFmessage(AutoVerifyEntity autoVerifyEntity, String str) throws Exception {
        Element rootElement = new SAXReader().read(new ByteArrayInputStream(str.getBytes())).getRootElement();
        String attributeValue = rootElement.attributeValue("ticket");
        int parseInt = Integer.parseInt(rootElement.attributeValue("scene"));
        autoVerifyEntity.setEncryptTalker(attributeValue);
        autoVerifyEntity.setScene(parseInt);
        return autoVerifyEntity;
    }

    private MemberEntity parseMember(Cursor cursor) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setWechatId(cursor.getString(0));
        memberEntity.setNickName(cursor.getString(2));
        String string = cursor.getString(5);
        if (StringUtils.isBlank(string)) {
            memberEntity.setHeadPic(cursor.getString(6));
        } else {
            memberEntity.setHeadPic(string);
        }
        return memberEntity;
    }

    private MessageEntity parseMessage(Cursor cursor) throws Exception {
        long j = cursor.getLong(cursor.getColumnIndex("msgId"));
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setTalker(cursor.getString(cursor.getColumnIndex("talker")));
        messageEntity.setSelf(selectSelf().getUserTalker());
        messageEntity.setMsgSvrId(cursor.getString(cursor.getColumnIndex("msgSvrId")));
        messageEntity.setType(cursor.getInt(cursor.getColumnIndex("type")));
        messageEntity.setCreateTime(cursor.getLong(cursor.getColumnIndex("createTime")));
        messageEntity.setContent(cursor.getString(cursor.getColumnIndex("content")));
        messageEntity.setMsgId(j);
        messageEntity.setSend(cursor.getInt(cursor.getColumnIndex("isSend")) != 0);
        messageEntity.setImgPath(cursor.getString(cursor.getColumnIndex("imgPath")));
        return messageEntity;
    }

    private List<MemberEntity> selectAllContactsForChatroomMember() {
        Throwable th;
        List<MemberEntity> arrayList = new ArrayList();
        Cursor query = query("select c.username,c.alias,c.nickname,c.pyInitial,c.quanPin,i.reserved1,i.reserved2 from rcontact c join img_flag i on c.username = i.username");
        while (query.moveToNext()) {
            try {
                arrayList.add(parseMember(query));
            } catch (Throwable th2) {
                th = th2;
            }
        }
        if (query != null) {
            query.close();
        }
        return arrayList;
       // throw th;
   /*     if (query != null) {
            if (th != null) {
                try {
                    query.close();
                } catch (Throwable th3) {
                    ThrowableExtension.addSuppressed(th, th3);
                }
            } else {
                query.close();
            }
        }*/
       // throw th;
    }

    private List<MemberEntity> selectChatroomMembers(String[] strArr) {
        Throwable th;
        List<MemberEntity> arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList(strArr.length);
        for (String str : strArr) {
            arrayList2.add("\"" + str + "\"");
        }
        Cursor query = query("select c.username,c.alias,c.nickname,c.pyInitial,c.quanPin,i.reserved1,i.reserved2 from rcontact c join img_flag i on c.username = i.username where c.username in (" + StringUtils.join(arrayList2, ',') + ")");
        while (query.moveToNext()) {
            try {
                arrayList.add(parseMember(query));
            } catch (Throwable th2) {
                th = th2;
            }
        }
        if (query != null) {
            query.close();
        }
        return arrayList;
  /*      if (query != null) {
            if (th != null) {
                try {
                    query.close();
                } catch (Throwable th3) {
                    ThrowableExtension.addSuppressed(th, th3);
                }
            } else {
                query.close();
            }
        }
        throw th;
        throw th;*/
    }

    private List<MemberEntity> selectChatroomMembers(String[] strArr, List<MemberEntity> list) {
        List<MemberEntity> arrayList = new ArrayList();
        for (Object obj : strArr) {
            for (MemberEntity memberEntity : list) {
                if (memberEntity.getWechatId().equals(obj)) {
                    arrayList.add(memberEntity);
                }
            }
        }
        return arrayList;
    }

    public void deleteAppattachOldFile(long j) {
        String str = "delete from appattach where totalLen = " + j;
        XposedHelpers.callMethod(sqliteDatabase, "execSQL", new Object[]{str, null});
    }

    public void deleteOldFmessage(String str) {
        Object obj = sqliteDatabase;
        String str2 = RequestParameters.SUBRESOURCE_DELETE;
        Object[] objArr = new Object[3];
        objArr[0] = "fmessage_conversation";
        objArr[1] = "talker = ?";
        objArr[2] = new String[]{str};
        ((Integer) XposedHelpers.callMethod(obj, str2, objArr)).intValue();
    }

    public String getImgMsgSvrId(String str) {
        String str2 = null;
        Cursor query = query("select * from ImgInfo2 where id = '" + str + "'");
        if (query != null) {
            while (query.moveToNext()) {
                str2 = query.getString(query.getColumnIndex("msgSvrId"));
            }
            query.close();
        }
        return str2;
    }

    public boolean isImageDownloadComplete(String str) {
        Cursor query = query("select * from ImgInfo2 where id = '" + str + "'");
        if (query == null) {
            return false;
        }
        boolean z = false;
        while (query.moveToNext()) {
            z = query.getInt(query.getColumnIndex("iscomplete")) == 1;
        }
        query.close();
        return z;
    }

    public List<MessageEntity> selectAllOldMessage() throws Exception {
        List<MessageEntity> linkedList = new LinkedList();
        XposedBridge.log("222222222222222222");
        Cursor query = query("select * from message");
        XposedBridge.log("3333333333333333333333");
        if (query != null) {
            while (query.moveToNext()) {
                linkedList.add(parseMessage(query));
            }
            query.close();
        }
        return linkedList;
    }

    public String selectAppattachFilePath(long j) {
        String str = "";
        Cursor query = query("select fileFullPath from appattach where msgInfoId = '" + j + "'");
        if (query != null) {
            while (query.moveToNext()) {
                str = query.getString(query.getColumnIndex("fileFullPath"));
            }
            query.close();
        }
        return str;
    }

    public String selectAppattachMsgInfoIdFromRowId(String str) {
        String str2 = null;
        Cursor query = query("select rowid,* from appattach where rowid = '" + str + "'");
        if (query != null) {
            while (query.moveToNext()) {
                str2 = query.getString(query.getColumnIndex("msgInfoId"));
            }
            query.close();
        }
        return str2;
    }

    public long selectAppattachTotalLengthFromRowId(String str) {
        long j = 0;
        Cursor query = query("select rowid,* from appattach where rowid = '" + str + "'");
        if (query != null) {
            while (query.moveToNext()) {
                j = query.getLong(query.getColumnIndex("totalLen"));
            }
            query.close();
        }
        return j;
    }

    public String selectBigImagePath(String str) {
        String str2 = null;
        Cursor query = query("select * from ImgInfo2 where id = '" + str + "'");
        if (query != null) {
            while (query.moveToNext()) {
                str2 = query.getString(query.getColumnIndex("bigImgPath"));
            }
            query.close();
        }
        return str2;
    }

    public ChatroomEntity selectChatRoomContact(HashMap<String, String> hashMap, HashMap<String, WxEntity> hashMap2, UserEntity userEntity, String str) throws Exception {
        ChatroomEntity chatroomEntity = new ChatroomEntity();
        for (int i = 0; i < 30; i++) {
            Cursor query = query(MessageFormat.format("select c.chatroomname,c.memberlist,c.displayname,c.roomowner,r.conRemark,r.nickname,r.pyInitial,r.quanPin,c.roomdata,i.reserved1,i.reserved2 from chatroom c join rcontact r on c.chatroomname = r.username join img_flag i on c.chatroomname = i.username where c.roomowner is not null and c.chatroomname={0}", new Object[]{str}));
            if (query != null) {
                while (query.moveToNext()) {
                    chatroomEntity = AbstractWeChatDb.parseChatroom(query, userEntity, hashMap, hashMap2);
                    if (chatroomEntity != null) {
                        break;
                    }
                }
                query.close();
                if (chatroomEntity != null) {
                    break;
                }
            }
            Thread.sleep(1000);
        }
        return chatroomEntity;
    }

    public JSONArray selectChatRoomContactJa(HashMap<String, String> hashMap, HashMap<String, WxEntity> hashMap2, UserEntity userEntity) throws Exception {
        JSONArray jSONArray = new JSONArray();
        Cursor query = query("select c.chatroomname,c.memberlist,c.displayname,c.roomowner,r.conRemark,r.nickname,r.pyInitial,r.quanPin,c.roomdata,i.reserved1,i.reserved2 from chatroom c join rcontact r on c.chatroomname = r.username join img_flag i on c.chatroomname = i.username where c.roomowner is not null");
        if (query != null) {
            while (query.moveToNext()) {
                ChatroomEntity parseChatroom = AbstractWeChatDb.parseChatroom(query, userEntity, hashMap, hashMap2);
                if (parseChatroom != null) {
                    jSONArray.add(JSON.toJSON(parseChatroom));
                }
            }
            query.close();
        }
        return jSONArray;
    }

    public List<ChatroomEntity> selectChatrooms(Set<String> set) {
        Throwable th;
        List<ChatroomEntity> arrayList = new ArrayList();
        String str = "select c.chatroomname,c.memberlist,c.displayname,c.roomowner,r.conRemark,r.nickname,r.pyInitial,r.quanPin,c.roomdata,i.reserved1,i.reserved2 from chatroom c join rcontact r on c.chatroomname = r.username join img_flag i on c.chatroomname = i.username where c.roomowner is not null";
        if (set != null && set.size() > 0) {
            ArrayList arrayList2 = new ArrayList(set.size());
            for (String str2 : set) {
                arrayList2.add("\"" + str2 + "\"");
            }
            str2 = "select c.chatroomname,c.memberlist,c.displayname,c.roomowner,r.conRemark,r.nickname,r.pyInitial,r.quanPin,c.roomdata,i.reserved1,i.reserved2 from chatroom c join rcontact r on c.chatroomname = r.username join img_flag i on c.chatroomname = i.username where c.roomowner is not null" + " and c.chatroomname in (" + StringUtils.join(arrayList2, ',') + ")";
        }
        List selectAllContactsForChatroomMember = selectAllContactsForChatroomMember();
        Cursor query = query(str2);
        while (query.moveToNext()) {
            try {
                ChatroomEntity parseChatroom = parseChatroom(query);
                parseChatroom.setMembers(selectChatroomMembers(StringUtils.split(query.getString(1), ';'), selectAllContactsForChatroomMember));
                arrayList.add(parseChatroom);
            } catch (Throwable th2) {
                th = th2;
            }
        }
        if (query != null) {
            query.close();
        }
        if (set != null) {
            for (String str22 : set) {
                if (!includeChatroom(str22, arrayList)) {
                    ChatroomEntity chatroomEntity = new ChatroomEntity();
                    chatroomEntity.setChatroomId(str22);
                    chatroomEntity.setOpType(3);
                    arrayList.add(chatroomEntity);
                }
            }
        }
        return arrayList;
      /*  throw th;
        if (query != null) {
            if (th != null) {
                try {
                    query.close();
                } catch (Throwable th3) {
                    ThrowableExtension.addSuppressed(th, th3);
                }
            } else {
                query.close();
            }
        }
        throw th;*/
    }

    public WxEntity selectContact(HashMap<String, String> hashMap, String str, String str2, HashMap<String, String> hashMap2) throws Exception {
        WxEntity wxEntity = null;
        for (int i = 0; i < 30; i++) {
            Cursor query = query(MessageFormat.format("select rcontact.username,rcontact.alias,rcontact.conRemark,rcontact.nickname,rcontact.pyInitial,rcontact.quanPin,rcontact.lvbuff,rcontact.encryptUsername,rcontact.contactLabelIds from rcontact where rcontact.username = '{0}' and rcontact.username not like '%@%' and rcontact.username != 'filehelper' order by rcontact.pyInitial limit 1", new Object[]{str}));
            if (query != null) {
                while (query.moveToNext()) {
                    wxEntity = AbstractWeChatDb.parseFriend(query, str2, hashMap, hashMap2);
                    if (wxEntity != null) {
                        break;
                    }
                }
                query.close();
                if (wxEntity != null) {
                    break;
                }
            }
            Thread.sleep(1000);
        }
        return wxEntity;
    }

    public HashMap<String, WxEntity> selectContactTree(HashMap<String, String> hashMap, String str, HashMap<String, String> hashMap2) {
        HashMap<String, WxEntity> hashMap3 = new HashMap();
        Cursor query = query("select r.username,r.alias,r.conRemark,r.nickname,r.pyInitial,r.quanPin,r.lvbuff,r.encryptUsername,r.contactLabelIds,i.reserved1,i.reserved2 from rcontact r left join img_flag i on r.username = i.username  where (r.type & 1 != 0 and r.type & 8 = 0 and r.type & 32 = 0 and r.verifyFlag & 8 = 0 and r.username not like '%@%' and r.username != 'filehelper' ) ");
        if (query != null) {
            while (query.moveToNext()) {
                WxEntity parseFriend = AbstractWeChatDb.parseFriend(query, str, hashMap, hashMap2);
                if (parseFriend != null) {
                    hashMap3.put(parseFriend.getUserName(), parseFriend);
                }
            }
            query.close();
        }
        return hashMap3;
    }

    public List<WxEntity> selectContacts(Set<String> set) {
        Throwable th;
        List<WxEntity> arrayList = new ArrayList();
        String str = "select r.username,r.alias,r.conRemark,r.nickname,r.pyInitial,r.quanPin,r.lvbuff,r.encryptUsername,r.contactLabelIds,i.reserved1,i.reserved2 from rcontact r left join img_flag i on r.username = i.username  where (r.type & 1 != 0 and r.type & 8 = 0 and r.type & 32 = 0 and r.verifyFlag & 8 = 0 and r.username not like '%@%' and r.username != 'filehelper' ) ";
        if (set != null && set.size() > 0) {
            ArrayList arrayList2 = new ArrayList(set.size());
            for (String str2 : set) {
                arrayList2.add("\"" + str2 + "\"");
            }
            str2 = "select r.username,r.alias,r.conRemark,r.nickname,r.pyInitial,r.quanPin,r.lvbuff,r.encryptUsername,r.contactLabelIds,i.reserved1,i.reserved2 from rcontact r left join img_flag i on r.username = i.username  where (r.type & 1 != 0 and r.type & 8 = 0 and r.type & 32 = 0 and r.verifyFlag & 8 = 0 and r.username not like '%@%' and r.username != 'filehelper' ) " + " and r.username in (" + StringUtils.join(arrayList2, ',') + ")";
        }
        HashMap selectLabel = selectLabel();
        Cursor query = query(str2);
        while (query.moveToNext()) {
            try {
                arrayList.add(parseFriend(query, selectLabel));
            } catch (Throwable th2) {
                th = th2;
            }
        }
        if (query != null) {
            query.close();
        }
        if (set != null) {
            for (String str22 : set) {
                if (!includeContact(str22, arrayList)) {
                    WxEntity wxEntity = new WxEntity();
                    wxEntity.setUserName(str22);
                    wxEntity.setOpType(3);
                    arrayList.add(wxEntity);
                }
            }
        }
        return arrayList;
      /*  throw th;
        if (query != null) {
            if (th != null) {
                try {
                    query.close();
                } catch (Throwable th3) {
                    ThrowableExtension.addSuppressed(th, th3);
                }
            } else {
                query.close();
            }
        }
        throw th;*/
    }

    public String selectEmojiInfo(String str) {
        String str2 = "";
        Cursor query = query("select * from EmojiInfo where md5 = '" + str + "'");
        if (query != null) {
            while (query.moveToNext()) {
                str2 = query.getString(query.getColumnIndex("cdnUrl"));
                if (OtherUtils.isEmpty(str2)) {
                    str2 = query.getString(query.getColumnIndex("groupId")) + "/" + str + "_cover";
                }
            }
            query.close();
        }
        return str2;
    }

    public LinkedList<AutoVerifyEntity> selectFromFmessage(int i) throws Exception {
        LinkedList<AutoVerifyEntity> linkedList = new LinkedList();
        Cursor query = query("select talker,fmsgContent,lastModifiedTime from fmessage_conversation where state = 0 and fmsgType = 1 and lastModifiedTime > " + (System.currentTimeMillis() - 604800000) + " limit " + i);
        if (query != null) {
            while (query.moveToNext()) {
                AutoVerifyEntity autoVerifyEntity = new AutoVerifyEntity();
                autoVerifyEntity.setTalker(query.getString(0));
                linkedList.add(parseFmessage(autoVerifyEntity, query.getString(1)));
            }
            query.close();
        }
        return linkedList;
    }

    public HashMap<String, String> selectHeadPics() {
        HashMap<String, String> hashMap = new HashMap();
        Cursor query = query("select username,reserved1,reserved2 from img_flag");
        if (query != null) {
            while (query.moveToNext()) {
                String string = query.getString(0);
                if (!OtherUtils.isEmpty(string)) {
                    String string2 = query.getString(1);
                    Object string3 = query.getString(2);
                    if (!OtherUtils.isEmpty(string2)) {
                        String str = string2;
                    }
                    if (OtherUtils.isEmpty(string3)) {
                        string3 = "";
                    }
                    hashMap.put(string, (String) string3);
                }
            }
            query.close();
        }
        return hashMap;
    }

    public HashMap<String, String> selectLabel() {
        HashMap<String, String> hashMap = new HashMap();
        Cursor query = query("select labelID,labelName from ContactLabel");
        if (query != null) {
            while (query.moveToNext()) {
                String string = query.getString(0);
                String string2 = query.getString(1);
                if (!OtherUtils.isEmpty(string)) {
                    hashMap.put(string, string2);
                }
            }
            query.close();
        }
        return hashMap;
    }

    public ArrayList<String> selectLabels() {
        ArrayList<String> arrayList = new ArrayList();
        Cursor query = query("select labelID,labelName from ContactLabel");
        if (query != null) {
            while (query.moveToNext()) {
                arrayList.add(query.getString(1));
            }
            query.close();
        }
        return arrayList;
    }

    public MessageEntity selectMessageFromMsgLocalId(String str) throws Exception {
        MessageEntity messageEntity = null;
        Cursor query = query("select * from message where msgId = '" + str + "'");
        if (query != null) {
            while (query.moveToNext()) {
                messageEntity = parseMessage(query);
            }
            query.close();
        }
        return messageEntity;
    }

    public MessageEntity selectMessageFromMsgSvrId(String str) throws Exception {
        MessageEntity messageEntity = null;
        Cursor query = query("select * from message where msgSvrId = '" + str + "'");
        if (query != null) {
            while (query.moveToNext()) {
                messageEntity = parseMessage(query);
            }
            query.close();
        }
        return messageEntity;
    }

    public MessageEntity selectMsgByMsgId(long j) {
        MessageEntity messageEntity = null;
        Cursor query = query("select * from message where msgId == " + j);
        if (query != null) {
            while (query.moveToNext()) {
                MessageEntity messageEntity2 = new MessageEntity();
                messageEntity2.setTalker(query.getString(query.getColumnIndex("talker")));
                messageEntity2.setSelf(getInstance().selectSelf().getUserTalker());
                messageEntity2.setMsgSvrId(query.getString(query.getColumnIndex("msgSvrId")));
                messageEntity2.setType(query.getInt(query.getColumnIndex("type")));
                messageEntity2.setCreateTime((long) query.getInt(query.getColumnIndex("createTime")));
                messageEntity2.setContent(query.getString(query.getColumnIndex("content")));
                messageEntity2.setMsgId(j);
                messageEntity2.setSend(query.getInt(query.getColumnIndex("isSend")) == 1);
                messageEntity = messageEntity2;
            }
            query.close();
        }
        return messageEntity;
    }

    public MessageEntity selectMsgByMsgSvrId(long j) {
        MessageEntity messageEntity = null;
        Cursor query = query("select * from message where msgSvrId == " + j);
        if (query != null) {
            while (query.moveToNext()) {
                messageEntity = new MessageEntity();
                messageEntity.setTalker(query.getString(query.getColumnIndex("talker")));
                messageEntity.setSelf(getInstance().selectSelf().getUserTalker());
                messageEntity.setMsgId(query.getLong(query.getColumnIndex("msgId")));
                messageEntity.setMsgSvrId(query.getString(query.getColumnIndex("msgSvrId")));
                messageEntity.setType(query.getInt(query.getColumnIndex("type")));
                messageEntity.setCreateTime(query.getLong(query.getColumnIndex("createTime")));
                messageEntity.setContent(query.getString(query.getColumnIndex("content")));
                messageEntity.setImgPath(query.getString(query.getColumnIndex("imgPath")));
            }
            query.close();
        }
        return messageEntity;
    }

    public UserEntity selectSelf() {
        int i;
        UserEntity userEntity;
        Cursor query = query("select id,value from userinfo where id in (2,4,5,6,29,42,12290,12291,12293)");
        UserEntity userEntity2 = new UserEntity();
        if (query != null) {
            i = 0;
            while (query.moveToNext()) {
                String string = query.getString(1);
                switch (query.getInt(0)) {
                    case 2:
                        if (!string.startsWith("+86")) {
                            userEntity2.setUserTalker(string);
                            break;
                        }
                        Logger.i("SELECT +86 IS COME", new Object[0]);
                        i = 1;
                        break;
                    case 4:
                        userEntity2.setUserName(string);
                        break;
                    case 5:
                        userEntity2.setBindEmail(string);
                        break;
                    case 6:
                        userEntity2.setBindMobile(string);
                        break;
                    case 29:
                        userEntity2.setBindQQ(MatchUtil.getValue(string, "uin=(.*)&key="));
                        break;
                    case 42:
                        userEntity2.setAlias(string);
                        break;
                    case UserInfoId.GENDER /*12290*/:
                        userEntity2.setGender(StringUtils.isNumeric(string) ? Integer.parseInt(string) : 0);
                        break;
                    case UserInfoId.SIGNTURE /*12291*/:
                        userEntity2.setSignature(string);
                        break;
                    case UserInfoId.REGION /*12293*/:
                        userEntity2.setRegion(string);
                        break;
                    default:
                        break;
                }
            }
        }
        i = 0;
        if (i != 0) {
            userEntity = null;
        } else {
            Cursor query2 = query("select sid,value from userinfo2 where sid == 'USERINFO_SELFINFO_SMALLIMGURL_STRING'");
            if (query2 != null) {
                while (query2.moveToNext()) {
                    userEntity2.setHeadPic(query2.getString(1));
                }
            }
            if (StringUtils.isBlank(userEntity2.getHeadPic())) {
                return userEntity2;
            }
            userEntity = userEntity2;
        }
        return userEntity;
    }

    public String selectSendPicPathByMsgId(String str) {
        String str2 = "";
        Cursor query = query("select bigImgPath from ImgInfo2 where msglocalid == " + str);
        if (query != null) {
            while (query.moveToNext()) {
                str2 = query.getString(0);
            }
            query.close();
        }
        return str2;
    }

    public String selectVoiceMsgLocalId(String str) {
        String str2 = null;
        Cursor query = query("select * from voiceinfo where FileName = '" + str + "'");
        if (query != null) {
            while (query.moveToNext()) {
                str2 = query.getString(query.getColumnIndex("MsgLocalId"));
            }
            query.close();
        }
        return str2;
    }

    public MessageEntity selectVoideFromImgPath(String str) throws Exception {
        MessageEntity messageEntity = null;
        Cursor query = query("select * from message where imgPath = '" + str + "'");
        if (query != null) {
            while (query.moveToNext()) {
                messageEntity = parseMessage(query);
            }
            query.close();
        }
        return messageEntity;
    }

    public long selectWxVoiceDuration(long j) throws Exception {
        long j2 = 0;
        Cursor query = query("select VoiceLength from voiceinfo where MsgLocalId == " + j);
        if (query != null) {
            while (query.moveToNext()) {
                j2 = query.getLong(0);
            }
            query.close();
        }
        return j2;
    }


    public long getVideoThumbSizeFromVideoInfo(String paramString)
            throws Exception
    {
        long l1 = 0L;
        paramString = "select msglocalid,reserved4 from videoinfo2 where msglocalid = '" + paramString + "'";
        Logger.i("===== videoThumbSize:" + paramString, new Object[0]);
        Cursor query   = query(paramString);
        long l2 = l1;
        if (query != null)
        {
            while (query.moveToNext()) {
                //l1 = XmlParseUtil.parseVideoThumbSize(query.getString(1));
            }
            query.close();
            l2 = l1;
        }
        return l2;
    }


    public List<String> selectContactByLabelName(java.util.Set<String> paramSet) {
        // Byte code:
        //   0: new 307	java/util/LinkedList
        //   3: dup
        //   4: invokespecial 308	java/util/LinkedList:<init>	()V
        //   7: astore_2
        //   8: aload_0
        //   9: aload_1
        //   10: invokevirtual 416	com/ac/wechat/wx/WechatDb:selectLabelIdFromLabelName	(Ljava/util/Set;)Ljava/util/List;
        //   13: invokeinterface 24 1 0
        //   18: astore 4
        //   20: aload 4
        //   22: invokeinterface 30 1 0
        //   27: ifeq +203 -> 230
        //   30: aload 4
        //   32: invokeinterface 34 1 0
        //   37: checkcast 275	java/lang/Integer
        //   40: astore_1
        //   41: new 222	java/lang/StringBuilder
        //   44: dup
        //   45: invokespecial 223	java/lang/StringBuilder:<init>	()V
        //   48: ldc_w 418
        //   51: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   54: aload_1
        //   55: invokevirtual 421	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   58: ldc_w 423
        //   61: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   64: aload_1
        //   65: invokevirtual 421	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   68: ldc_w 425
        //   71: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   74: aload_1
        //   75: invokevirtual 421	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   78: ldc_w 427
        //   81: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   84: aload_1
        //   85: invokevirtual 421	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   88: ldc_w 284
        //   91: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   94: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   97: astore_1
        //   98: new 222	java/lang/StringBuilder
        //   101: dup
        //   102: invokespecial 223	java/lang/StringBuilder:<init>	()V
        //   105: ldc_w 429
        //   108: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   111: aload_1
        //   112: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   115: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   118: iconst_0
        //   119: anewarray 260	java/lang/Object
        //   122: invokestatic 296	com/orhanobut/logger/Logger:i	(Ljava/lang/String;[Ljava/lang/Object;)V
        //   125: aload_0
        //   126: aload_1
        //   127: invokevirtual 197	com/ac/wechat/wx/WechatDb:query	(Ljava/lang/String;)Landroid/database/Cursor;
        //   130: astore_3
        //   131: aconst_null
        //   132: astore_1
        //   133: aload_3
        //   134: invokeinterface 200 1 0
        //   139: ifeq +38 -> 177
        //   142: aload_2
        //   143: aload_3
        //   144: iconst_0
        //   145: invokeinterface 99 2 0
        //   150: invokevirtual 430	java/util/LinkedList:add	(Ljava/lang/Object;)Z
        //   153: pop
        //   154: goto -21 -> 133
        //   157: astore_1
        //   158: aload_1
        //   159: athrow
        //   160: astore_2
        //   161: aload_3
        //   162: ifnull +13 -> 175
        //   165: aload_1
        //   166: ifnull +55 -> 221
        //   169: aload_3
        //   170: invokeinterface 208 1 0
        //   175: aload_2
        //   176: athrow
        //   177: aload_3
        //   178: ifnull -158 -> 20
        //   181: iconst_0
        //   182: ifeq +21 -> 203
        //   185: aload_3
        //   186: invokeinterface 208 1 0
        //   191: goto -171 -> 20
        //   194: astore_1
        //   195: new 210	java/lang/NullPointerException
        //   198: dup
        //   199: invokespecial 211	java/lang/NullPointerException:<init>	()V
        //   202: athrow
        //   203: aload_3
        //   204: invokeinterface 208 1 0
        //   209: goto -189 -> 20
        //   212: astore_3
        //   213: aload_1
        //   214: aload_3
        //   215: invokevirtual 215	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
        //   218: goto -43 -> 175
        //   221: aload_3
        //   222: invokeinterface 208 1 0
        //   227: goto -52 -> 175
        //   230: aload_2
        //   231: areturn
        //   232: astore_2
        //   233: goto -72 -> 161
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	236	0	this	WechatDb
        //   0	236	1	paramSet	java.util.Set<String>
        //   7	136	2	localLinkedList	java.util.LinkedList
        //   160	71	2	localList	List<String>
        //   232	1	2	localObject	Object
        //   130	74	3	localCursor	Cursor
        //   212	10	3	localThrowable	Throwable
        //   18	13	4	localIterator	Iterator
        // Exception table:
        //   from	to	target	type
        //   133	154	157	java/lang/Throwable
        //   158	160	160	finally
        //   185	191	194	java/lang/Throwable
        //   169	175	212	java/lang/Throwable
        //   133	154	232	finally
        return null;
    }
}