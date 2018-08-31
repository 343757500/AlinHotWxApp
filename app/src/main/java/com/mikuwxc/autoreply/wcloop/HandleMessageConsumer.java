package com.mikuwxc.autoreply.wcloop;

import com.mikuwxc.autoreply.wcentity.MessageEntity;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wcutil.DownLoadWxResFromWxUtil;

public class HandleMessageConsumer
{
  public static MessageEntity handlePic(ClassLoader paramClassLoader, WechatEntity paramWechatEntity, MessageEntity paramMessageEntity)
    throws Throwable
  {
    String str1 = paramMessageEntity.getImgPath();
    long l = paramMessageEntity.getMsgId();
    String str2 = paramMessageEntity.getMsgSvrId();
    if (paramMessageEntity.isSend())
    {
      DownLoadWxResFromWxUtil.downloadWxPicRes(paramClassLoader, paramWechatEntity, "0", String.valueOf(l), str1);
      return paramMessageEntity;
    }
    DownLoadWxResFromWxUtil.downloadWxPicRes(paramClassLoader, paramWechatEntity, str2, String.valueOf(l), str1);
    return paramMessageEntity;
  }
}
