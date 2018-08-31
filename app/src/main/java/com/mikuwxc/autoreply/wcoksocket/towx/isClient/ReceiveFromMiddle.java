package com.mikuwxc.autoreply.wcoksocket.towx.isClient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mikuwxc.autoreply.wcentity.AutoVerifyEntity;
import com.mikuwxc.autoreply.wcentity.UserEntity;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wclocalsocket.LocalSocketClient;
import com.mikuwxc.autoreply.wclocalsocket.WxCmdType;
import com.mikuwxc.autoreply.wcloop.ClearBlackFriendBasket;
import com.mikuwxc.autoreply.wcutil.ChatroomUtil;
import com.mikuwxc.autoreply.wcutil.DownLoadWxResFromWxUtil;
import com.mikuwxc.autoreply.wcutil.FriendUtil;
import com.mikuwxc.autoreply.wcutil.KillSlientUpdateUtil;
import com.mikuwxc.autoreply.wcutil.LabelUtil;
import com.mikuwxc.autoreply.wcutil.MomentUtil;
import com.mikuwxc.autoreply.wcutil.QrcodeUtil;
import com.mikuwxc.autoreply.wcutil.RemarkUtil;
import com.mikuwxc.autoreply.wcutil.SendComment;
import com.mikuwxc.autoreply.wcutil.SendMesUtil;
import com.mikuwxc.autoreply.wcutil.WalletUtil;
import com.mikuwxc.autoreply.wcutil.WxSwitchControlUtil;
import com.mikuwxc.autoreply.wx.WechatDb;

import java.util.HashMap;

public class ReceiveFromMiddle {
    public static void handle(Context context, ClassLoader classLoader, Activity activity, WechatEntity wechatEntity, String str, LocalSocketClient localSocketClient, ClearBlackFriendBasket clearBlackFriendBasket) throws Exception {
        JSONObject parseObject = JSONObject.parseObject(str);
        Intent intent;
        switch (parseObject.getIntValue("wxType")) {
            case WxCmdType.REPORT_CONNECT_REMOTE /*1002*/:
                localSocketClient.syncUserInfo();
                return;
            case WxCmdType.REPORT_SYNC_SELF /*1003*/:
                localSocketClient.syncUserInfos();
                return;
            case WxCmdType.SEND_MESSAGE /*2002*/:
                sendMessage(classLoader, wechatEntity, activity, parseObject);
                return;
            case WxCmdType.UPDATE_CONTACT_REMARK /*2003*/:
                RemarkUtil.updateContactRemark(classLoader, wechatEntity, parseObject.getString("wechatId"), parseObject.getString("newRemark"));
                return;
            case WxCmdType.UPDATE_CONTACT_LABEL /*2007*/:
                LabelUtil.updateContactLabel(classLoader, wechatEntity, parseObject.getString("wechatId"), JSONArray.parseArray(parseObject.getString("labelList"), String.class), true);
                return;
            case WxCmdType.AUTO_VERIFY /*2008*/:
                FriendUtil.autoVerifyUser(classLoader, wechatEntity, parseObject.getString("wechatId"), parseObject.getString("stranger"), parseObject.getIntValue("scene"));
                return;
            case WxCmdType.GET_ONE_CONTACT /*2009*/:
                String userTalker = WechatDb.getInstance().selectSelf().getUserTalker();
                localSocketClient.reportOneContact(WechatDb.getInstance().selectContact(WechatDb.getInstance().selectHeadPics(), userTalker, parseObject.getString("wechatId"), WechatDb.getInstance().selectLabel()));
                return;
            case WxCmdType.GET_ONE_CHATROOM /*2010*/:
                UserEntity selectSelf = WechatDb.getInstance().selectSelf();
                HashMap selectHeadPics = WechatDb.getInstance().selectHeadPics();
                localSocketClient.reportOneChatroom(WechatDb.getInstance().selectChatRoomContact(selectHeadPics, WechatDb.getInstance().selectContactTree(selectHeadPics, selectSelf.getUserTalker(), WechatDb.getInstance().selectLabel()), selectSelf, parseObject.getString("chatroomId")));
                return;
            case WxCmdType.GET_ADD_FRIEND_RESULT /*2013*/:
                FriendUtil.searchFriend(classLoader, wechatEntity, parseObject.getLong("taskId").longValue(), parseObject.getString("remark"), parseObject.getString("searchValue"), parseObject.getString("sendWord"), parseObject.getIntValue("scene"));
                return;
            case WxCmdType.REPORT_VERIFY_FRIEND /*2017*/:
                for (AutoVerifyEntity autoVerifyEntity : WechatDb.getInstance().selectFromFmessage(parseObject.getIntValue("friendCountLimit"))) {
                    FriendUtil.autoVerifyUser(classLoader, wechatEntity, autoVerifyEntity.getTalker(), autoVerifyEntity.getEncryptTalker(), autoVerifyEntity.getScene());
                    localSocketClient.reportVerifyFriend();
                }
                return;
            case WxCmdType.GET_LUCKY_MONEY /*2018*/:
                WalletUtil.openLuckyMoney(classLoader, wechatEntity, parseObject.getString("payMsgId"), parseObject.getString("nativeurl"));
                return;
            case WxCmdType.GET_TRANS /*2019*/:
                WalletUtil.confirmTransferAccounts(classLoader, wechatEntity, parseObject.getString("transactionId"), parseObject.getString("transferId"), parseObject.getString("sendWechatId"), parseObject.getIntValue("invalidTime"));
                return;
            case WxCmdType.GET_OTHER_FRIENDS_MOMENT /*2020*/:
                MomentUtil.collectOtherMoment(classLoader, wechatEntity, parseObject.getString("wechatId"), parseObject.getLong("snsId").longValue(), parseObject.getIntValue("selectTime"), parseObject.getIntValue("selectLimit"));
                return;
            case WxCmdType.CLEAR_BLACK_FRIENDS /*2021*/:
                if (clearBlackFriendBasket.getBasket().size() > 0) {
                    localSocketClient.reportClearBlackFriend(parseObject.getLong("taskId").longValue(), false, "任务已经在执行中了");
                    return;
                } else {
                    FriendUtil.clearBlackFriend(clearBlackFriendBasket, parseObject.getLong("taskId").longValue(), JSONObject.parseArray(parseObject.getString("wechatIdList"), String.class));
                    return;
                }
            case WxCmdType.REVIOKE_MESSAGE /*2022*/:
                SendMesUtil.revokeMessage(classLoader, wechatEntity, parseObject.getLong("msgId").longValue());
                return;
            case 3001:
                ChatroomUtil.createChatroom(classLoader, wechatEntity, parseObject.getString("chatroomName"), JSONArray.parseArray(parseObject.getString("memberList"), String.class));
                return;
            case 3002:
                ChatroomUtil.inventFriendInChatroom(classLoader, wechatEntity, parseObject.getString("chatroomId"), JSONArray.parseArray(parseObject.getString("friendIdList"), String.class));
                return;
            case 3003:
                ChatroomUtil.addChatroomMember(classLoader, wechatEntity, parseObject.getString("chatroomId"), parseObject.getString("wechatId"), parseObject.getString("sendWord"));
                return;
            case 3006:
                QrcodeUtil.createReceiveMallBm(classLoader, wechatEntity, context, parseObject.getDouble("money").doubleValue());
                return;
            case 3007:
                DownLoadWxResFromWxUtil.downloadWxFileRes(classLoader, wechatEntity, parseObject.getLongValue("msgId"));
                return;
            case 3008:
                WalletUtil.confirmTransferAccounts(classLoader, wechatEntity, parseObject.getString("transactionId"), parseObject.getString("transferId"), parseObject.getString("sendWechatId"), parseObject.getIntValue("invalidTime"));
                return;
            case 3009:
                WalletUtil.openLuckyMoney(classLoader, wechatEntity, parseObject.getString("payMsgId"), parseObject.getString("nativeurl"));
                return;
            case 3010:
                RemarkUtil.updateContactPhone(classLoader, wechatEntity, parseObject.getString("wechatId"), parseObject.getString("phones"));
                return;
            case 3011:
                RemarkUtil.updateChatroomName(classLoader, wechatEntity, parseObject.getString("chatroomId"), parseObject.getString("chatroomName"));
                return;
            case 3012:
                SendMesUtil.sendFile(classLoader, wechatEntity, parseObject.getString("wechatId"), parseObject.getString("content"), parseObject.getLong("localMsgId").longValue());
                return;
            case 3013:
                LabelUtil.updateContactLabel(classLoader, wechatEntity, parseObject.getString("wechatId"), JSONArray.parseArray(parseObject.getString("labelList"), String.class), true);
                return;
            case 3014:
                ChatroomUtil.removeFriendInChatroom(classLoader, wechatEntity, parseObject.getString("chatroomId"), JSONArray.parseArray(parseObject.getString("friendIdList"), String.class));
                return;
            case 3015:
               // SendMesUtil.sendAtInChatroom(classLoader, wechatEntity, parseObject.getString("chatroomId"), parseObject.getString("atId"), parseObject.getString("content"), parseObject.getLong("localMsgId").longValue());
                return;
            case 3016:
                intent = new Intent();
                intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.chatting.gallery.ImageGalleryUI"));
                intent.putExtra("img_gallery_talker", parseObject.getString("talker"));
                intent.putExtra("img_gallery_msg_svr_id", parseObject.getLongValue("msgSvrId"));
                activity.startActivity(intent);
                return;
            case 3018:
                String string = parseObject.getString("url");
                intent = new Intent();
                intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.plugin.webview.ui.tools.WebViewUI"));
                intent.putExtra("rawUrl", string);
                activity.startActivity(intent);
                return;
            case 3020:
                MomentUtil.collectCurrentMoment(classLoader, wechatEntity, parseObject.getLong("snsId").longValue());
                return;
            case 3022:
                DownLoadWxResFromWxUtil.downloadMomentPic(classLoader, wechatEntity, parseObject.getString("snsLocalId"), parseObject.getIntValue("createTime"));
                return;
            case 3023:
                FriendUtil.deleteFriend(classLoader, wechatEntity, parseObject.getString("wechatId"));
                return;
            case 3024:
                SendComment.sendComment(classLoader, wechatEntity, parseObject.getLong("snsId").longValue(), parseObject.getString("wechatId"), parseObject.getString("sendWord"));
                return;
            case 3025:
                SendComment.sendCommentToOther(classLoader, wechatEntity, parseObject.getLong("snsId").longValue(), parseObject.getString("wechatId"), parseObject.getString("otherWechatId"), parseObject.getIntValue("commentId"), parseObject.getString("sendWord"));
                return;
            case 3026:
                SendComment.sendLikeToMoment(classLoader, wechatEntity, parseObject.getLong("snsId").longValue(), parseObject.getString("wechatId"));
                return;
            case 3027:
                KillSlientUpdateUtil.killSlientInstall(classLoader, wechatEntity);
                return;
            case WxCmdType.INNER_HANDLE_HEART /*9000*/:
                JSON.toJSONString(WechatDb.getInstance().selectSelf());
                localSocketClient.reportHeart();
                return;
            case WxCmdType.INNER_DOWNLOAD_FILE /*9001*/:
                WxSwitchControlUtil.setDownLoadWxFileState(false);
                return;
            case WxCmdType.INNER_HANDLE_MOMENT_SUCCESS /*9002*/:
                localSocketClient.reportHandleMomentSuccess(parseObject.getString("talker"), MomentUtil.parseMomentDetail(classLoader, wechatEntity, parseObject.getString("momentListResult")));
                return;
            case WxCmdType.INNER_SELECT_BATCH_MESSAGE /*9003*/:
                localSocketClient.reportBatchMessage(WechatDb.getInstance().selectAllOldMessage());
                return;
            default:
                return;
        }
    }

    public static void sendMessage(ClassLoader classLoader, WechatEntity wechatEntity, Activity activity, JSONObject jSONObject) throws Exception {
        long j = 0;
        if (jSONObject.containsKey("localMsgId")) {
            j = jSONObject.getLong("localMsgId").longValue();
        }
        int intValue = jSONObject.getIntValue("type");
        ClassLoader classLoader2;
        WechatEntity wechatEntity2;
        if (intValue == 1 || intValue == 42 || intValue == 48 || intValue == 10000) {
            classLoader2 = classLoader;
            wechatEntity2 = wechatEntity;
            SendMesUtil.sendTxt(classLoader2, wechatEntity2, jSONObject.getString("wechatId"), jSONObject.getString("content"), intValue, j);
        } else if (intValue == 3) {
            classLoader2 = classLoader;
            wechatEntity2 = wechatEntity;
           // SendMesUtil.sendPic(classLoader2, wechatEntity2, WechatDb.getInstance().selectSelf().getUserTalker(), jSONObject.getString("wechatId"), jSONObject.getString("content"), j);
        } else if (intValue == 43) {
            ClassLoader r4 = classLoader;
            WechatEntity r5 = wechatEntity;
           // SendMesUtil.sendVideo(r4, r5, jSONObject.getString("wechatId"), jSONObject.getString("content"), j);
        } else if (intValue == 34) {
          //  SendMesUtil.sendAmr(classLoader, wechatEntity, activity, jSONObject.getString("wechatId"), jSONObject.getString("content"), jSONObject.getInteger("duration").intValue(), j);
        } else if (intValue == 49) {
            JSONObject jSONObject2 = jSONObject.getJSONObject("content");
            String string = jSONObject2.getString("type");
            if ("file".equals(string)) {
                ClassLoader r4 = classLoader;
                WechatEntity    r5 = wechatEntity;
                SendMesUtil.sendFile(r4, r5, jSONObject.getString("wechatId"), jSONObject2.getString("filePath"), j);
            } else if ("link".equals(string)) {
                ClassLoader classLoader3 = classLoader;
                WechatEntity wechatEntity3 = wechatEntity;
               // SendMesUtil.sendLink(classLoader3, wechatEntity3, jSONObject.getString("wechatId"), jSONObject2.getString("url"), jSONObject2.getString("title"), jSONObject2.getString("desc"), jSONObject2.getString("thumbPath"), j);
            }
        }
    }
}