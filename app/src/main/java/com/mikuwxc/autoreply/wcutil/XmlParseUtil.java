package com.mikuwxc.autoreply.wcutil;

import com.alibaba.fastjson.JSONObject;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XmlParseUtil
{
/*  public static String buildCardXml(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9)
  {
    Document localDocument = DocumentHelper.createDocument();
    Element localElement = localDocument.addElement("msg");
    localElement.addAttribute("username", paramString1);
    localElement.addAttribute("nickname", paramString2);
    localElement.addAttribute("alias", paramString3);
    localElement.addAttribute("fullpy", paramString4);
    localElement.addAttribute("shortpy", paramString5);
    localElement.addAttribute("scene", "17");
    localElement.addAttribute("province", paramString6);
    localElement.addAttribute("city", paramString7);
    localElement.addAttribute("sign", paramString8);
    localElement.addAttribute("sex", paramString9);
    return localDocument.toString();
  }
  
  public static String handle(Element paramElement)
  {
    String str = paramElement.getTextTrim();
    paramElement = str;
    if (str.contains("["))
    {
      paramElement = str;
      if (str.contains("]")) {
        paramElement = str.substring(str.lastIndexOf("[") + 1, str.indexOf("]"));
      }
    }
    return paramElement;
  }
  
  public static String parseCard(String paramString)
    throws Exception
  {
    String str1 = paramString;
    if (paramString.contains("<msg")) {
      if (!paramString.contains("</msg>")) {
        break label318;
      }
    }
    label318:
    for (str1 = paramString.substring(paramString.indexOf("<msg"), paramString.lastIndexOf("</msg>") + "</msg>".length());; str1 = paramString.substring(paramString.indexOf("<msg")))
    {
      paramString = new JSONObject();
      Object localObject = new SAXReader().read(new ByteArrayInputStream(str1.getBytes())).getRootElement();
      str1 = ((Element)localObject).attributeValue("nickname");
      String str2 = ((Element)localObject).attributeValue("username");
      String str3 = ((Element)localObject).attributeValue("alias");
      String str4 = ((Element)localObject).attributeValue("fullpy");
      String str5 = ((Element)localObject).attributeValue("shortpy");
      String str6 = ((Element)localObject).attributeValue("scene");
      String str7 = ((Element)localObject).attributeValue("province");
      String str8 = ((Element)localObject).attributeValue("city");
      String str9 = ((Element)localObject).attributeValue("sign");
      String str10 = ((Element)localObject).attributeValue("sex");
      String str11 = ((Element)localObject).attributeValue("bigheadimgurl");
      localObject = ((Element)localObject).attributeValue("antispamticket");
      paramString.put("username", str2);
      paramString.put("nickname", str1);
      paramString.put("alias", str3);
      paramString.put("fullpy", str4);
      paramString.put("shortpy", str5);
      paramString.put("scene", str6);
      paramString.put("province", str7);
      paramString.put("city", str8);
      paramString.put("sign", str9);
      paramString.put("sex", str10);
      paramString.put("bigheadimgurl", str11);
      paramString.put("antispamticket", localObject);
      return paramString.toString();
    }
  }
  
  public static String parseChatroomSystemMes(String paramString)
    throws Exception
  {
    String str = paramString;
    if (paramString.contains("<sysmsg")) {
      str = paramString.substring(paramString.indexOf("<sysmsg"), paramString.lastIndexOf("</sysmsg>") + "</sysmsg>".length());
    }
    paramString = new SAXReader();
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(str.getBytes());
    paramString = paramString.read(localByteArrayInputStream).getRootElement().element("sysmsgtemplate").element("content_template");
    str = paramString.element("template").getText().toString();
    paramString = paramString.element("link_list").elements("link");
    ArrayList localArrayList = new ArrayList();
    paramString = paramString.iterator();
    while (paramString.hasNext())
    {
      Object localObject1 = (Element)paramString.next();
      if (!((Element)localObject1).attributeValue("name").equals("revoke"))
      {
        Object localObject2 = ((Element)localObject1).element("memberlist").elements("member");
        if ((!OtherUtils.isEmpty(localObject2)) && (((List)localObject2).size() > 0))
        {
          localObject1 = new StringBuilder();
          localObject2 = ((List)localObject2).iterator();
          while (((Iterator)localObject2).hasNext()) {
            ((StringBuilder)localObject1).append(((Element)((Iterator)localObject2).next()).element("nickname").getText().toString()).append("��");
          }
          localObject1 = ((StringBuilder)localObject1).toString();
          localArrayList.add(((String)localObject1).substring(0, ((String)localObject1).lastIndexOf("��")));
        }
      }
    }
    if (str.contains("$username$"))
    {
      str = str.replace("$username$", (CharSequence)localArrayList.get(0));
      paramString = str;
      if (str.contains("$names$")) {
        paramString = str.replace("$names$", (CharSequence)localArrayList.get(1));
      }
    }
    for (;;)
    {
      str = paramString;
      if (paramString.contains("$revoke$")) {
        str = paramString.replace("$revoke$", "");
      }
      localByteArrayInputStream.close();
      return str;
      if (str.contains("$names$"))
      {
        paramString = str.replace("$names$", (CharSequence)localArrayList.get(0));
      }
      else
      {
        paramString = str;
        if (str.contains("$kickoutname$")) {
          paramString = str.replace("$kickoutname$", (CharSequence)localArrayList.get(0));
        }
      }
    }
  }
  
  public static String parseEmojiMessageCdnUrl(String paramString)
    throws Exception
  {
    Object localObject = paramString;
    if (paramString.contains("<msg")) {
      localObject = paramString.substring(paramString.indexOf("<msg"), paramString.lastIndexOf("</msg>") + "</msg>".length());
    }
    paramString = new SAXReader();
    localObject = new ByteArrayInputStream(((String)localObject).getBytes());
    paramString = paramString.read((InputStream)localObject).getRootElement().element("emoji").attributeValue("cdnurl");
    ((InputStream)localObject).close();
    return paramString;
  }*/
  
  public static JSONObject parseFile(String paramString)
    throws Exception
  {
    Object localObject1 = paramString;
    if (paramString.contains("<msg")) {
      localObject1 = paramString.substring(paramString.indexOf("<msg"), paramString.lastIndexOf("</msg>") + "</msg>".length());
    }
    Object localObject2 = new SAXReader();
    ByteArrayInputStream paramString1 = new ByteArrayInputStream(((String)localObject1).getBytes());
    Object localObject3 = ((SAXReader)localObject2).read(paramString1).getRootElement().element("appmsg");
    localObject1 = ((Element)localObject3).element("title");
    Object localObject4 = ((Element)localObject3).element("type");
    localObject2 = ((Element)localObject3).element("des");
    localObject3 = ((Element)localObject3).element("appattach").element("totallen");
    localObject1 = ((Element)localObject1).getTextTrim();
    localObject4 = ((Element)localObject4).getTextTrim();
    localObject2 = ((Element)localObject2).getTextTrim();
    localObject3 = ((Element)localObject3).getTextTrim();
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("title", localObject1);
    localJSONObject.put("type", Integer.valueOf(Integer.parseInt((String)localObject4)));
    localJSONObject.put("des", localObject2);
    localJSONObject.put("totalLen", Long.valueOf(Long.parseLong((String)localObject3)));
    paramString1.close();
    return localJSONObject;
  }
  
  /*public static JSONObject parseLink(String paramString)
    throws Exception
  {
    Object localObject1 = paramString;
    if (paramString.contains("<msg")) {
      localObject1 = paramString.substring(paramString.indexOf("<msg"), paramString.lastIndexOf("</msg>") + "</msg>".length());
    }
    Object localObject2 = new SAXReader();
    paramString = new ByteArrayInputStream(((String)localObject1).getBytes());
    Object localObject3 = ((SAXReader)localObject2).read(paramString).getRootElement().element("appmsg");
    localObject1 = ((Element)localObject3).element("title");
    localObject2 = ((Element)localObject3).element("des");
    localObject3 = ((Element)localObject3).element("url");
    localObject1 = ((Element)localObject1).getTextTrim();
    localObject2 = ((Element)localObject2).getTextTrim();
    localObject3 = ((Element)localObject3).getTextTrim();
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("title", localObject1);
    localJSONObject.put("type", "link");
    localJSONObject.put("desc", localObject2);
    localJSONObject.put("url", localObject3);
    paramString.close();
    return localJSONObject;
  }
  
  public static String parseLuckyMoney(String paramString)
    throws Exception
  {
    Object localObject1 = paramString;
    if (paramString.contains("<msg")) {
      localObject1 = paramString.substring(paramString.indexOf("<msg"), paramString.lastIndexOf("</msg>") + "</msg>".length());
    }
    paramString = new JSONObject();
    Object localObject2 = new SAXReader().read(new ByteArrayInputStream(((String)localObject1).getBytes())).getRootElement().element("appmsg").element("wcpayinfo");
    localObject1 = ((Element)localObject2).element("sendertitle");
    Object localObject3 = ((Element)localObject2).element("paymsgid");
    localObject2 = ((Element)localObject2).element("nativeurl");
    localObject3 = handle((Element)localObject3);
    localObject1 = handle((Element)localObject1);
    localObject2 = handle((Element)localObject2);
    paramString.put("sendertitle", localObject1);
    paramString.put("paymsgid", localObject3);
    paramString.put("nativeurl", localObject2);
    return paramString.toString();
  }
  
  public static String parseMpHelper(String paramString)
    throws Exception
  {
    String str = paramString;
    if (paramString.contains("<msg")) {
      str = paramString.substring(paramString.indexOf("<msg"), paramString.lastIndexOf("</msg>") + "</msg>".length());
    }
    str = new SAXReader().read(new ByteArrayInputStream(str.getBytes())).getRootElement().element("appmsg").element("url").getText();
    paramString = str;
    if (str.contains("["))
    {
      paramString = str;
      if (str.contains("]")) {
        paramString = str.substring(str.lastIndexOf("[") + 1, str.indexOf("]"));
      }
    }
    return paramString;
  }
  
  public static long parsePicLength(String paramString)
    throws Exception
  {
    Object localObject = paramString;
    if (paramString.contains("<msg")) {
      localObject = paramString.substring(paramString.indexOf("<msg"), paramString.lastIndexOf("</msg>") + "</msg>".length());
    }
    paramString = new SAXReader();
    localObject = new ByteArrayInputStream(((String)localObject).getBytes());
    long l = Long.parseLong(paramString.read((InputStream)localObject).getRootElement().element("img").attributeValue("length"));
    ((InputStream)localObject).close();
    return l;
  }
  
  public static String parseTransMoney(String paramString)
    throws Exception
  {
    Object localObject1 = paramString;
    if (paramString.contains("<msg")) {
      localObject1 = paramString.substring(paramString.indexOf("<msg"), paramString.lastIndexOf("</msg>") + "</msg>".length());
    }
    paramString = new JSONObject();
    localObject1 = new SAXReader().read(new ByteArrayInputStream(((String)localObject1).getBytes())).getRootElement().element("appmsg");
    Object localObject3 = ((Element)localObject1).element("title");
    Element localElement = ((Element)localObject1).element("wcpayinfo");
    Object localObject6 = localElement.element("paysubtype");
    Object localObject5 = localElement.element("feedesc");
    Object localObject4 = localElement.element("transcationid");
    Object localObject2 = localElement.element("transferid");
    localObject1 = localElement.element("invalidtime");
    localElement = localElement.element("pay_memo");
    localObject3 = handle((Element)localObject3);
    int i = Integer.parseInt(((Element)localObject6).getTextTrim());
    localObject6 = handle(localElement);
    localObject5 = handle((Element)localObject5);
    localObject4 = handle((Element)localObject4);
    localObject2 = handle((Element)localObject2);
    localObject1 = handle((Element)localObject1);
    paramString.put("title", localObject3);
    paramString.put("paysubtype", Integer.valueOf(i));
    paramString.put("payMemo", localObject6);
    paramString.put("feedesc", localObject5);
    paramString.put("transcationid", localObject4);
    paramString.put("transferid", localObject2);
    paramString.put("invalidtime", localObject1);
    return paramString.toString();
  }
  
  private static void saveByteToConfig(String paramString, byte[] paramArrayOfByte)
    throws Exception
  {
    paramString = new File(paramString);
    if ((!paramString.exists()) && (!paramString.getParentFile().exists())) {
      paramString.getParentFile().mkdirs();
    }
    if (!paramString.exists()) {
      paramString.createNewFile();
    }
    paramString = new FileOutputStream(paramString);
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(paramString);
    localBufferedOutputStream.write(paramArrayOfByte);
    localBufferedOutputStream.flush();
    paramString.close();
  }*/
}
