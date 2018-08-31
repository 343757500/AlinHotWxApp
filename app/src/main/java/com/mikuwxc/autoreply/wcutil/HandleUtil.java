package com.mikuwxc.autoreply.wcutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HandleUtil {
    public static void m19(String str, String str2) {
        FileOutputStream fileOutputStream;
        Throwable e;
        FileOutputStream fileOutputStream2 = null;
        int i = 0;
        try {
            if (new File(str).exists()) {
                InputStream fileInputStream = new FileInputStream(str);
                fileOutputStream = new FileOutputStream(str2);
                try {
                    byte[] bArr = new byte[1444];
                    while (true) {
                        int read = fileInputStream.read(bArr);
                        if (read == -1) {
                            break;
                        }
                        i += read;
                        fileOutputStream.write(bArr, 0, read);
                    }
                    fileInputStream.close();
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable e2) {
                           // ThrowableExtension.printStackTrace(e2);
                        }
                    }
                } catch (Exception e3) {
                 //   e2 = e3;
                    try {
                       // ThrowableExtension.printStackTrace(e2);
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (Throwable e22) {
                              //  ThrowableExtension.printStackTrace(e22);
                            }
                        }
                    } catch (Throwable th) {
                   //     e22 = th;
                        fileOutputStream2 = fileOutputStream;
                        fileOutputStream = fileOutputStream2;
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (Throwable e4) {
                               // ThrowableExtension.printStackTrace(e4);
                            }
                        }
                     //   throw e22;
                    }
                } catch (Throwable th2) {
                   // e22 = th2;
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                   // throw e22;
                }
            }
        } catch (Exception e5) {
            //e22 = e5;
            fileOutputStream = null;
          //  ThrowableExtension.printStackTrace(e22);
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (Throwable th3) {
          //  e22 = th3;
            fileOutputStream = fileOutputStream2;
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
          //  throw e22;
        }
    }
}