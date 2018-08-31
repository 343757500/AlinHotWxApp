package com.mikuwxc.autoreply.wcloop;

import android.content.ContentValues;

import com.mikuwxc.autoreply.wcentity.MessageEntity;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wcutil.AuthUtil;
import com.mikuwxc.autoreply.wcutil.OtherUtils;
import com.mikuwxc.autoreply.wx.WechatDb;
import com.orhanobut.logger.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.XposedBridge;

public class MessageConsumer
{

    private static MessageEntity localMessageEntity1;
    private static MessageEntity localObject;
    private static String localObject1;

    public static void handleMessageInfo(final ClassLoader paramClassLoader, final ContentValues paramContentValues, final WechatEntity paramWechatEntity)
  {
    new Thread()
    {
      public void run()
      {
        int i;
        int j;
        String str2;
        long l2;
        String str4;
        String str1;
        String str3;
        long l1;
        MessageEntity localMessageEntity2;
        boolean bool;
        label316:
      //  Object localObject;
        for (;;)
        {
          try
          {
            i = paramContentValues.getAsInteger("type").intValue();
            j = paramContentValues.getAsInteger("isSend").intValue();
            str2 = paramContentValues.getAsString("msgSvrId");
            l2 = paramContentValues.getAsLong("msgId").longValue();
            str3 = paramContentValues.getAsString("talker");
            str4 = paramContentValues.getAsString("content");
            str1 = paramContentValues.getAsString("imgPath");
            l1 = paramContentValues.getAsLong("createTime").longValue();
            localMessageEntity2 = new MessageEntity();
            localMessageEntity2.setTalker(str3);
            localMessageEntity2.setSelf(WechatDb.getInstance().selectSelf().getUserTalker());
            localMessageEntity2.setMsgSvrId(str2);
            localMessageEntity2.setType(i);
            localMessageEntity2.setCreateTime(l1);
            localMessageEntity2.setContent(str4);
            localMessageEntity2.setMsgId(l2);
            if (j == 0) {
              //break label734;
            }
            bool = true;
            localMessageEntity2.setSend(bool);
            localMessageEntity2.setImgPath(str1);
              localMessageEntity1 = localMessageEntity2;
              XposedBridge.log(localMessageEntity1.getImgPath().toString());
            switch (i)
            {
              case 419430449:
              case 436207665:
                if (localMessageEntity1 == null) {
                 // break label733;
                }
                //LocalSocketClient.getInstance().uploadMessage(localMessageEntity1);
                return;
            }
          }
          catch (Throwable localThrowable)
          {
           // String str3;
            MessageEntity localMessageEntity1;
            Logger.e(localThrowable, "===== HANDLE WX MESSAGE ===== ERROR!!!", new Object[0]);
            return;
          }
          localMessageEntity1 = localMessageEntity2;
          if (j == 1)
          {
            localMessageEntity1 = localMessageEntity2;
            if (OtherUtils.isEmpty(str2))
            {
              if ((j != 1) || (!OtherUtils.isEmpty(str2))) {
                if (str3.contains("@chatroom"))
                {
                  if (AuthUtil.isForbiddenAuth(100001))
                  {
                      try {
                          HandleMessageConsumer.handlePic(paramClassLoader, paramWechatEntity, localMessageEntity2);
                      } catch (Throwable throwable) {
                          throwable.printStackTrace();
                      }
                      localMessageEntity1 = null;
                    Logger.i("===== downloadimage true", new Object[0]);
                  }
                  else
                  {
                    Logger.i("===== downloadimage false", new Object[0]);
                      localObject = localMessageEntity2;
                  }
                }

                else
                {
                    try {
                        HandleMessageConsumer.handlePic(paramClassLoader, paramWechatEntity, localMessageEntity2);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    localObject = null;
                  Logger.i("===== downloadimage true", new Object[0]);


                }
              }
            }
          }
        }
          // break label316;
          //break label316;
          // break label316;
          // break label316;
          // break label316;
          //     DownLoadWxFileConsumer.handle(paramClassLoader, paramWechatEntity, localMessageEntity2);
          // break label316;
          //  break label316;
          // break label316;
      }
    }.start();
  }

  public static void parseFmessageInfo(ContentValues paramContentValues)
  {
    Logger.i("===== HANDLE NEW FRIEND REQUEST", new Object[0]);
    int i = paramContentValues.getAsInteger("isSend").intValue();
    if ((i == 0) || (i == 2))
    {
     String msgContent = paramContentValues.getAsString("msgContent");
      Object localObject2 = Pattern.compile("ticket=\"(.*?)\"");
      Object localObject1 = Pattern.compile("scene=\"(.*?)\"");
      localObject2 = ((Pattern)localObject2).matcher(msgContent);
      localObject1 = ((Pattern)localObject1).matcher(msgContent);
      Logger.i("===== PREPARE FIND NEW FRIEND REQUEST", new Object[0]);
      if ((((Matcher)localObject2).find()) && (((Matcher)localObject1).find()))
      {
        Logger.i("===== FIND NEW FRIEND REQUEST", new Object[0]);
        localObject1 = WechatDb.getInstance().selectSelf().getUserTalker();
        Logger.i("===== SEND NEW FRIEND REQUEST", new Object[0]);
        //LocalSocketClient.getInstance().uploadNewFriendRequest((String)localObject1, msgContent);
      }
    }
  }

  public static void parseVideoInfo(ContentValues paramContentValues)
          throws Exception
  {
   String msglocalid = paramContentValues.getAsString("msglocalid");
    MessageEntity localMessageEntity = WechatDb.getInstance().selectMessageFromMsgLocalId(msglocalid);
    long l = WechatDb.getInstance().getVideoThumbSizeFromVideoInfo(msglocalid);
    Logger.i("===== videoThumbSize:" + l, new Object[0]);
    localMessageEntity.setFileSize(l);
    if ((localMessageEntity != null) && (!localMessageEntity.isSend())) {
      //LocalSocketClient.getInstance().uploadMessage(localMessageEntity);
    }
  }
}
