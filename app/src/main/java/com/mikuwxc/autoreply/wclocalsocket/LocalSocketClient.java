package com.mikuwxc.autoreply.wclocalsocket;

import android.app.Activity;
import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mikuwxc.autoreply.wcentity.ChatroomEntity;
import com.mikuwxc.autoreply.wcentity.MessageEntity;
import com.mikuwxc.autoreply.wcentity.UserEntity;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wcentity.WxEntity;
import com.mikuwxc.autoreply.wcloop.ClearBlackFriendBasket;
import com.mikuwxc.autoreply.wcloop.ClearBlackFriendConsumer;
import com.mikuwxc.autoreply.wcloop.DownLoadWxFileBasket;
import com.mikuwxc.autoreply.wcloop.DownLoadWxFileConsumer;
import com.mikuwxc.autoreply.wcloop.UploadMessageBasket;
import com.mikuwxc.autoreply.wcloop.UploadMessageConsumer;
import com.mikuwxc.autoreply.wcoksocket.towx.isClient.ReadThread;
import com.mikuwxc.autoreply.wcutil.KillSlientUpdateUtil;
import com.mikuwxc.autoreply.wcutil.OtherUtils;
import com.mikuwxc.autoreply.wx.WechatDb;
import com.orhanobut.logger.Logger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.robv.android.xposed.XposedBridge;

public class LocalSocketClient {
    private static LocalSocketClient instance;
    private Context applicationContext;
    private BufferedReader br;
    public ClearBlackFriendBasket clearBlackFriendBasket;
    public DownLoadWxFileBasket downLoadWxFileBasket;
    private LocalSocket localSocket = new LocalSocket();
    private PrintWriter printWriter;
    private ExecutorService service = Executors.newCachedThreadPool();
    public UploadMessageBasket uploadMessageBasket;
    private WechatDb wechatDb = WechatDb.getInstance();
    private WechatEntity wechatEntity;
    private Activity wxActivity;
    private ClassLoader wxClassLoader;

    public LocalSocketClient(Context context, ClassLoader classLoader, Activity activity, WechatEntity wechatEntity) {
        this.applicationContext = context;
        this.wxClassLoader = classLoader;
        this.wxActivity = activity;
        this.wechatEntity = wechatEntity;
        instance = this;
    }

    private JSONObject build(int i) {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("packageId", UUID.randomUUID().toString());
        jSONObject.put("isFromWx", Boolean.valueOf(true));
        jSONObject.put("wxType", Integer.valueOf(i));
        return jSONObject;
    }

    public static LocalSocketClient getInstance() {
        return instance;
    }

    private void sendToServer(String str) {
        synchronized (this.printWriter) {
            this.printWriter.write(str);
            this.printWriter.write("\n");
            this.printWriter.flush();
        }
    }

    public void connectToServer() {
        new Thread() {
            public void run() {
                try {
                    Logger.i("===== START ONNECT APP", new Object[0]);
                    LocalSocketClient.this.localSocket.connect(new LocalSocketAddress(Const.LOCAL_SOCKET_NAME));
                    LocalSocketClient.this.br = new BufferedReader(new InputStreamReader(LocalSocketClient.this.localSocket.getInputStream()));
                    LocalSocketClient.this.printWriter = new PrintWriter(LocalSocketClient.this.localSocket.getOutputStream());
                    LocalSocketClient.this.clearBlackFriendBasket = new ClearBlackFriendBasket();
                    LocalSocketClient.this.service.submit(new ClearBlackFriendConsumer(LocalSocketClient.this.applicationContext, LocalSocketClient.this.wxClassLoader, LocalSocketClient.this, LocalSocketClient.this.wechatEntity, LocalSocketClient.this.clearBlackFriendBasket));
                    LocalSocketClient.this.uploadMessageBasket = new UploadMessageBasket();
                    LocalSocketClient.this.service.submit(new UploadMessageConsumer(LocalSocketClient.this.applicationContext, LocalSocketClient.this, LocalSocketClient.this.uploadMessageBasket));
                    LocalSocketClient.this.downLoadWxFileBasket = new DownLoadWxFileBasket();
                    LocalSocketClient.this.service.submit(new DownLoadWxFileConsumer(LocalSocketClient.this.applicationContext, LocalSocketClient.this.wxClassLoader, LocalSocketClient.this, LocalSocketClient.this.wechatEntity, LocalSocketClient.this.downLoadWxFileBasket));
                    LocalSocketClient.this.service.submit(new ReadThread(LocalSocketClient.this.applicationContext, LocalSocketClient.this.wxClassLoader, LocalSocketClient.this.wxActivity, LocalSocketClient.this.wechatEntity, LocalSocketClient.this.br, LocalSocketClient.this, LocalSocketClient.this.clearBlackFriendBasket));
                    LocalSocketClient.this.syncUserInfo();
                } catch (Throwable e) {
                    Logger.e(e, "===== WX CONNECT ===== ERROR!!!", new Object[0]);
                }
            }
        }.start();
    }

    public DownLoadWxFileBasket getDownLoadWxFileBasket() {
        return this.downLoadWxFileBasket;
    }

    public UploadMessageBasket getUploadMessageBasket() {
        return this.uploadMessageBasket;
    }

    public void reportAddFriendResult(long j, String str, boolean z, String str2) {
        JSONObject build = build(WxCmdType.REPORT_ADD_FRIEND_RESULT);
        build.put("taskId", Long.valueOf(j));
        build.put("phone", (Object) str);
        build.put("isSuccess", Boolean.valueOf(z));
        build.put("extra", (Object) str2);
        sendToServer(build.toString());
    }

    public void reportBatchMessage(List<MessageEntity> list) {
        JSONObject build = build(WxCmdType.REPORT_BATCH_MESSAGE_RESULT);
        build.put("wechatId", WechatDb.getInstance().selectSelf().getUserTalker());
        build.put("messageList", JSON.toJSONString(list));
        sendToServer(build.toString());
    }

    public void reportClearBlackFriend(long j, boolean z, String str) {
        JSONObject build = build(1025);
        build.put("taskId", Long.valueOf(j));
        build.put("isSuccess", Boolean.valueOf(z));
        build.put("wechatId", WechatDb.getInstance().selectSelf().getUserTalker());
        build.put("extra", (Object) str);
        sendToServer(build.toString());
    }

    public void reportCollectOtherMoment(String str, long j, long j2) {
        JSONObject build = build(1024);
        build.put("talker", (Object) str);
        build.put("wechatId", WechatDb.getInstance().selectSelf().getUserTalker());
        build.put("firstSnsId", Long.valueOf(j));
        build.put("lastSnsId", Long.valueOf(j2));
        sendToServer(build.toString());
    }

    public void reportHandleMomentSuccess(String str, String str2) {
        JSONObject build = build(WxCmdType.INNER_HANDLE_MOMENT_SUCCESS);
        build.put("talker", (Object) str);
        build.put("wechatId", WechatDb.getInstance().selectSelf().getUserTalker());
        build.put("momentResult", (Object) str2);
        sendToServer(build.toString());
    }

    public void reportHeart() {
        sendToServer(build(WxCmdType.INNER_HANDLE_HEART).toString());
    }

    public void reportInvokeMessage(String str, long j) {
        JSONObject build = build(WxCmdType.REPORT_INVOKE_MESSAGE);
        build.put("wechatId", WechatDb.getInstance().selectSelf().getUserTalker());
        build.put("talker", (Object) str);
        build.put("msgId", Long.valueOf(j));
        sendToServer(build.toString());
    }

    public void reportMessageSendResult(long j, long j2, long j3, String str) {
        JSONObject build = build(WxCmdType.REPORT_MESSAGE_SEND_RESULT);
        build.put("msgId", Long.valueOf(j));
        build.put("localMsgId", Long.valueOf(j2));
        build.put("createTime", Long.valueOf(j3));
        build.put("talker", (Object) str);
        String jSONObject = build.toString();
        Logger.i("=====SEND 1009 TO MIDDLE [%s]", new Object[]{String.valueOf(j2)});
        sendToServer(jSONObject);
    }

    public void reportOneChatroom(ChatroomEntity chatroomEntity) throws Exception {
        JSONObject build = build(WxCmdType.REPORT_ONE_CHATROOM);
        build.put("chatroomEntity", JSON.toJSONString(chatroomEntity));
        sendToServer(build.toString());
    }

    public void reportOneContact(WxEntity wxEntity) throws Exception {
        JSONObject build = build(WxCmdType.REPORT_ONE_CONTACT);
        build.put("weEntity", JSON.toJSONString(wxEntity));
        sendToServer(build.toString());
    }

    public void reportPhoneRiskOperate(int i) {
        JSONObject build = build(WxCmdType.REPORT_RISK_OPERATE);
        build.put("riskOperateType", Integer.valueOf(i));
        build.put("operateTime", Long.valueOf(System.currentTimeMillis()));
        sendToServer(build.toString());
    }

    public void reportRiskOperate(int i, String str, String str2) {
        JSONObject build = build(WxCmdType.REPORT_RISK_OPERATE);
        build.put("riskOperateType", Integer.valueOf(i));
        build.put("talker", (Object) str);
        build.put("extra", (Object) str2);
        build.put("operateTime", Long.valueOf(System.currentTimeMillis()));
        sendToServer(build.toString());
    }

    public void reportVerifyCallback(WxEntity wxEntity) {
        JSONObject build = build(WxCmdType.REPORT_VERIFY_FRIEND_CALLBACK);
        build.put("wechatId", WechatDb.getInstance().selectSelf().getUserTalker());
        build.put("friend", JSONObject.toJSONString(wxEntity));
        sendToServer(build.toString());
    }

    public void reportVerifyFriend() {
        sendToServer(build(WxCmdType.REPORT_VERIFY_FRIEND).toString());
    }

    public void syncChatroom(Set<String> set) throws Exception {
        XposedBridge.log("77777777777777777777777");
        UserEntity selectSelf = this.wechatDb.selectSelf();
        XposedBridge.log("8888888888888888888888888");
        JSONObject build = build(WxCmdType.REPORT_SYNC_CHAT_ROOM);
        XposedBridge.log("99999999999999999999999999");
        build.put("wechatId", selectSelf.getUserTalker());
        build.put("isAll", Boolean.valueOf(set == null));
        build.put("chatroomContacts", this.wechatDb.selectChatrooms(set));
        sendToServer(build.toString());
    }

    public void syncContactLabel() {
        UserEntity selectSelf = this.wechatDb.selectSelf();
        JSONObject build = build(WxCmdType.REPORT_SYNC_LABEL);
        build.put("wechatId", selectSelf.getUserTalker());
        build.put("labels", this.wechatDb.selectLabels());
        sendToServer(build.toString());
    }

    public void syncContacts(Set<String> set) {
        UserEntity selectSelf = this.wechatDb.selectSelf();
        JSONObject build = build(WxCmdType.REPORT_SYNC_CONTACTS);
        build.put("wechatId", selectSelf.getUserTalker());
        build.put("isAll", Boolean.valueOf(set == null));
        build.put("contacts", this.wechatDb.selectContacts(set));
        sendToServer(build.toString());
    }

    public void syncUserInfo() throws Exception {
        while (true) {
            UserEntity selectSelf = this.wechatDb.selectSelf();
            if (OtherUtils.isEmpty(selectSelf)) {
                Thread.sleep(5000);
            } else {
                Object toJSONString = JSON.toJSONString(selectSelf);
                JSONObject build = build(WxCmdType.REPORT_SYNC_SELF);
                build.put("wechat", toJSONString);
                sendToServer(build.toString());
                KillSlientUpdateUtil.killSlientInstall(this.wxClassLoader, this.wechatEntity);
                return;
            }
        }
    }

    public void syncUserInfos() throws Exception {
        syncContactLabel();
        syncContacts(null);
        syncChatroom(null);
    }

    public void uploadMessage(MessageEntity messageEntity) {
        JSONObject build = build(WxCmdType.REPORT_MESSAGE);
        build.put("message", JSONObject.toJSONString(messageEntity));
        String jSONObject = build.toString();
        Logger.i("=====SEND 1007 TO MIDDLE [%s]", new Object[]{JSONObject.toJSON(messageEntity)});
        sendToServer(jSONObject);
    }

    public void uploadNewFriendRequest(String str, String str2) {
        JSONObject build = build(WxCmdType.REPORT_NEW_FRIEND_REQUEST);
        build.put("wechatId", (Object) str);
        build.put("msgContent", (Object) str2);
        sendToServer(build.toString());
    }
}