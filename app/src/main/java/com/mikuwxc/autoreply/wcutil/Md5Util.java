package com.mikuwxc.autoreply.wcutil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util
{
  protected static char[] hexDigits = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
  protected static MessageDigest messageDigest = null;
  
  static
  {
    try
    {
      messageDigest = MessageDigest.getInstance("MD5");
     // return;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      for (;;)
      {
        localNoSuchAlgorithmException.printStackTrace();
      }
    }
  }
  
  /*private static String MD5(ByteBuffer paramByteBuffer)
  {
    str = "";
    try
    {
      Object localObject = MessageDigest.getInstance("MD5");
      ((MessageDigest)localObject).update(paramByteBuffer);
      paramByteBuffer = ((MessageDigest)localObject).digest();
      localObject = new char[32];
      int j = 0;
      int i = 0;
      while (j < 16)
      {
        int m = paramByteBuffer[j];
        int k = i + 1;
        localObject[i] = hexDigits[(m >>> 4 & 0xF)];
        i = k + 1;
        localObject[k] = hexDigits[(m & 0xF)];
        j++;
      }
      paramByteBuffer = new String((char[])localObject);
    }
    catch (NoSuchAlgorithmException paramByteBuffer)
    {
      for (;;)
      {
        paramByteBuffer.printStackTrace();
        paramByteBuffer = str;
      }
    }
    return paramByteBuffer;
  }*/
  
 /* public static String getByteMd5(byte[] paramArrayOfByte)
  {
    try
    {
      byte[] arrayOfByte = MessageDigest.getInstance("MD5").digest(paramArrayOfByte);
      paramArrayOfByte = new StringBuilder(arrayOfByte.length * 2);
      int j = arrayOfByte.length;
      int i = 0;
      while (i < j)
      {
        int k = arrayOfByte[i];
        if ((k & 0xFF) < 16) {
          paramArrayOfByte.append("0");
        }
        paramArrayOfByte.append(Integer.toHexString(k & 0xFF));
        i++;
        continue;
        return paramArrayOfByte;
      }
    }
    catch (NoSuchAlgorithmException paramArrayOfByte)
    {
      paramArrayOfByte.printStackTrace();
      paramArrayOfByte = null;
    }
    for (;;)
    {
      paramArrayOfByte = paramArrayOfByte.toString();
    }
  }*/
  
  /* Error */
  public static String getFileMD5(java.io.File paramFile)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore 5
    //   6: aconst_null
    //   7: astore_3
    //   8: aload 5
    //   10: astore_2
    //   11: new 97	java/io/FileInputStream
    //   14: astore_1
    //   15: aload 5
    //   17: astore_2
    //   18: aload_1
    //   19: aload_0
    //   20: invokespecial 100	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   23: aload_1
    //   24: invokevirtual 104	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   27: getstatic 110	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
    //   30: lconst_0
    //   31: aload_0
    //   32: invokevirtual 116	java/io/File:length	()J
    //   35: invokevirtual 122	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
    //   38: invokestatic 124	com/ac/wechat/util/Md5Util:MD5	(Ljava/nio/ByteBuffer;)Ljava/lang/String;
    //   41: astore_0
    //   42: aload_1
    //   43: ifnull +7 -> 50
    //   46: aload_1
    //   47: invokevirtual 127	java/io/FileInputStream:close	()V
    //   50: aload_0
    //   51: areturn
    //   52: astore_0
    //   53: aload_3
    //   54: astore_1
    //   55: ldc 50
    //   57: astore_2
    //   58: aload_2
    //   59: astore_0
    //   60: aload_1
    //   61: ifnull -11 -> 50
    //   64: aload_1
    //   65: invokevirtual 127	java/io/FileInputStream:close	()V
    //   68: aload_2
    //   69: astore_0
    //   70: goto -20 -> 50
    //   73: astore_0
    //   74: aload_2
    //   75: astore_0
    //   76: goto -26 -> 50
    //   79: astore_0
    //   80: aload 4
    //   82: astore_1
    //   83: ldc 50
    //   85: astore_2
    //   86: aload_2
    //   87: astore_0
    //   88: aload_1
    //   89: ifnull -39 -> 50
    //   92: aload_1
    //   93: invokevirtual 127	java/io/FileInputStream:close	()V
    //   96: aload_2
    //   97: astore_0
    //   98: goto -48 -> 50
    //   101: astore_0
    //   102: aload_2
    //   103: astore_0
    //   104: goto -54 -> 50
    //   107: astore_0
    //   108: aload_2
    //   109: astore_1
    //   110: aload_1
    //   111: ifnull +7 -> 118
    //   114: aload_1
    //   115: invokevirtual 127	java/io/FileInputStream:close	()V
    //   118: aload_0
    //   119: athrow
    //   120: astore_1
    //   121: goto -71 -> 50
    //   124: astore_1
    //   125: goto -7 -> 118
    //   128: astore_0
    //   129: goto -19 -> 110
    //   132: astore_0
    //   133: goto -50 -> 83
    //   136: astore_0
    //   137: goto -82 -> 55
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	140	0	paramFile	java.io.File
    //   14	101	1	localObject1	Object
    //   120	1	1	localIOException1	java.io.IOException
    //   124	1	1	localIOException2	java.io.IOException
    //   10	99	2	localObject2	Object
    //   7	47	3	localObject3	Object
    //   1	80	4	localObject4	Object
    //   4	12	5	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   11	15	52	java/io/FileNotFoundException
    //   18	23	52	java/io/FileNotFoundException
    //   64	68	73	java/io/IOException
    //   11	15	79	java/io/IOException
    //   18	23	79	java/io/IOException
    //   92	96	101	java/io/IOException
    //   11	15	107	finally
    //   18	23	107	finally
    //   46	50	120	java/io/IOException
    //   114	118	124	java/io/IOException
    //   23	42	128	finally
    //   23	42	132	java/io/IOException
    //   23	42	136	java/io/FileNotFoundException
    return null;
  }
  
  /* Error */
  public static String getFileMD5(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aconst_null
    //   6: astore_2
    //   7: aload 4
    //   9: astore_1
    //   10: new 112	java/io/File
    //   13: astore 5
    //   15: aload 4
    //   17: astore_1
    //   18: aload 5
    //   20: aload_0
    //   21: invokespecial 131	java/io/File:<init>	(Ljava/lang/String;)V
    //   24: aload 4
    //   26: astore_1
    //   27: new 97	java/io/FileInputStream
    //   30: astore_0
    //   31: aload 4
    //   33: astore_1
    //   34: aload_0
    //   35: aload 5
    //   37: invokespecial 100	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   40: aload_0
    //   41: invokevirtual 104	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   44: getstatic 110	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
    //   47: lconst_0
    //   48: aload 5
    //   50: invokevirtual 116	java/io/File:length	()J
    //   53: invokevirtual 122	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
    //   56: invokestatic 124	com/ac/wechat/util/Md5Util:MD5	(Ljava/nio/ByteBuffer;)Ljava/lang/String;
    //   59: astore_1
    //   60: aload_0
    //   61: ifnull +7 -> 68
    //   64: aload_0
    //   65: invokevirtual 127	java/io/FileInputStream:close	()V
    //   68: aload_1
    //   69: astore_0
    //   70: aload_0
    //   71: areturn
    //   72: astore_0
    //   73: aload_2
    //   74: astore_1
    //   75: ldc 50
    //   77: astore_2
    //   78: aload_2
    //   79: astore_0
    //   80: aload_1
    //   81: ifnull -11 -> 70
    //   84: aload_1
    //   85: invokevirtual 127	java/io/FileInputStream:close	()V
    //   88: aload_2
    //   89: astore_0
    //   90: goto -20 -> 70
    //   93: astore_0
    //   94: aload_2
    //   95: astore_0
    //   96: goto -26 -> 70
    //   99: astore_0
    //   100: aload_3
    //   101: astore_1
    //   102: ldc 50
    //   104: astore_2
    //   105: aload_2
    //   106: astore_0
    //   107: aload_1
    //   108: ifnull -38 -> 70
    //   111: aload_1
    //   112: invokevirtual 127	java/io/FileInputStream:close	()V
    //   115: aload_2
    //   116: astore_0
    //   117: goto -47 -> 70
    //   120: astore_0
    //   121: aload_2
    //   122: astore_0
    //   123: goto -53 -> 70
    //   126: astore_0
    //   127: aload_1
    //   128: ifnull +7 -> 135
    //   131: aload_1
    //   132: invokevirtual 127	java/io/FileInputStream:close	()V
    //   135: aload_0
    //   136: athrow
    //   137: astore_0
    //   138: goto -70 -> 68
    //   141: astore_1
    //   142: goto -7 -> 135
    //   145: astore_2
    //   146: aload_0
    //   147: astore_1
    //   148: aload_2
    //   149: astore_0
    //   150: goto -23 -> 127
    //   153: astore_1
    //   154: aload_0
    //   155: astore_1
    //   156: goto -54 -> 102
    //   159: astore_1
    //   160: aload_0
    //   161: astore_1
    //   162: goto -87 -> 75
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	165	0	paramString	String
    //   9	123	1	localObject1	Object
    //   141	1	1	localIOException1	java.io.IOException
    //   147	1	1	str1	String
    //   153	1	1	localIOException2	java.io.IOException
    //   155	1	1	str2	String
    //   159	1	1	localFileNotFoundException	java.io.FileNotFoundException
    //   161	1	1	str3	String
    //   6	116	2	str4	String
    //   145	4	2	localObject2	Object
    //   1	100	3	localObject3	Object
    //   3	29	4	localObject4	Object
    //   13	36	5	localFile	java.io.File
    // Exception table:
    //   from	to	target	type
    //   10	15	72	java/io/FileNotFoundException
    //   18	24	72	java/io/FileNotFoundException
    //   27	31	72	java/io/FileNotFoundException
    //   34	40	72	java/io/FileNotFoundException
    //   84	88	93	java/io/IOException
    //   10	15	99	java/io/IOException
    //   18	24	99	java/io/IOException
    //   27	31	99	java/io/IOException
    //   34	40	99	java/io/IOException
    //   111	115	120	java/io/IOException
    //   10	15	126	finally
    //   18	24	126	finally
    //   27	31	126	finally
    //   34	40	126	finally
    //   64	68	137	java/io/IOException
    //   131	135	141	java/io/IOException
    //   40	60	145	finally
    //   40	60	153	java/io/IOException
    //   40	60	159	java/io/FileNotFoundException
    return null;
  }
  
 /* public static String getMd5Value(String paramString)
  {
    try
    {
      Object localObject = MessageDigest.getInstance("MD5");
      ((MessageDigest)localObject).update(paramString.getBytes());
      paramString = new java/lang/StringBuffer;
      paramString.<init>();
      localObject = ((MessageDigest)localObject).digest();
      for (int i = 0; i < localObject.length; i++)
      {
        int k = localObject[i];
        int j = k;
        if (k < 0) {
          j = k + 256;
        }
        if (j < 16) {
          paramString.append("0");
        }
        paramString.append(Integer.toHexString(j));
      }
      paramString = paramString.toString();
    }
    catch (Exception paramString)
    {
      for (;;)
      {
        paramString.printStackTrace();
        paramString = "";
      }
    }
    return paramString;
  }*/
  
 /* public static String sha1(String paramString)
  {
    try
    {
      paramString = sha1(paramString.getBytes("utf-8"));
      return paramString;
    }
    catch (UnsupportedEncodingException paramString)
    {
      for (;;)
      {
        paramString.printStackTrace();
        paramString = "";
      }
    }
  }*/
  
 /* public static String sha1(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {}
    for (paramArrayOfByte = "";; paramArrayOfByte = paramArrayOfByte.toString()) {
      for (;;)
      {
        return paramArrayOfByte;
        try
        {
          Object localObject = MessageDigest.getInstance("sha1");
          localObject = ((MessageDigest)localObject).digest(paramArrayOfByte);
          paramArrayOfByte = new StringBuilder();
          int j = localObject.length;
          for (int i = 0; i < j; i++)
          {
            int k = localObject[i] & 0xFF;
            if (k < 16) {
              paramArrayOfByte.append("0");
            }
            paramArrayOfByte.append(Integer.toHexString(k));
          }
        }
        catch (Exception paramArrayOfByte)
        {
          System.out.println(paramArrayOfByte.toString());
          paramArrayOfByte.printStackTrace();
          paramArrayOfByte = "";
        }
      }
    }
  }*/
  
  /* Error */
  public static String stringToMD5(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: ldc 34
    //   5: invokestatic 40	java/security/MessageDigest:getInstance	(Ljava/lang/String;)Ljava/security/MessageDigest;
    //   8: aload_0
    //   9: ldc -80
    //   11: invokevirtual 156	java/lang/String:getBytes	(Ljava/lang/String;)[B
    //   14: invokevirtual 68	java/security/MessageDigest:digest	([B)[B
    //   17: astore_0
    //   18: new 70	java/lang/StringBuilder
    //   21: dup
    //   22: aload_0
    //   23: arraylength
    //   24: iconst_2
    //   25: imul
    //   26: invokespecial 73	java/lang/StringBuilder:<init>	(I)V
    //   29: astore 4
    //   31: aload_0
    //   32: arraylength
    //   33: istore_2
    //   34: iconst_0
    //   35: istore_1
    //   36: iload_1
    //   37: iload_2
    //   38: if_icmpge +66 -> 104
    //   41: aload_0
    //   42: iload_1
    //   43: baload
    //   44: istore_3
    //   45: iload_3
    //   46: sipush 255
    //   49: iand
    //   50: bipush 16
    //   52: if_icmpge +11 -> 63
    //   55: aload 4
    //   57: ldc 75
    //   59: invokevirtual 79	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   62: pop
    //   63: aload 4
    //   65: iload_3
    //   66: sipush 255
    //   69: iand
    //   70: invokestatic 85	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   73: invokevirtual 79	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   76: pop
    //   77: iinc 1 1
    //   80: goto -44 -> 36
    //   83: astore_0
    //   84: aload_0
    //   85: invokevirtual 43	java/security/NoSuchAlgorithmException:printStackTrace	()V
    //   88: aload 4
    //   90: astore_0
    //   91: aload_0
    //   92: areturn
    //   93: astore_0
    //   94: aload_0
    //   95: invokevirtual 159	java/io/UnsupportedEncodingException:printStackTrace	()V
    //   98: aload 4
    //   100: astore_0
    //   101: goto -10 -> 91
    //   104: aload 4
    //   106: invokevirtual 89	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   109: astore_0
    //   110: goto -19 -> 91
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	113	0	paramString	String
    //   35	43	1	i	int
    //   33	6	2	j	int
    //   44	26	3	k	int
    //   1	104	4	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   3	18	83	java/security/NoSuchAlgorithmException
    //   3	18	93	java/io/UnsupportedEncodingException
    return null;
  }
}
