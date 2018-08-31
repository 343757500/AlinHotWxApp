package com.mikuwxc.autoreply.wcutil;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class FileIoUtil {
    public static void copyDir(String str, String str2) {
        File file = new File(str);
        createFile(file, false);
        File file2 = new File(str2);
        if (file.isDirectory() && file2.isDirectory()) {
            copyFileToDir(file.getAbsolutePath() + "/" + file2.getName(), listFile(file2));
        }
    }

    public static void copyFile(File file, File file2) {
        if (file.exists()) {
            file.delete();
        }
        createFile(file, true);
        try {
            InputStream fileInputStream = new FileInputStream(file2);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bArr = new byte[1024];
            while (fileInputStream.read(bArr) != -1) {
                fileOutputStream.write(bArr);
            }
            fileInputStream.close();
            fileOutputStream.close();
        } catch (Throwable e) {
           // ThrowableExtension.printStackTrace(e);
        }
    }

    public static void copyFile(File file, InputStream inputStream) {
        if (!file.exists()) {
            createFile(file, true);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bArr = new byte[1024];
                while (inputStream.read(bArr) != -1) {
                    fileOutputStream.write(bArr);
                }
                inputStream.close();
                fileOutputStream.close();
            } catch (Throwable e) {
             //   ThrowableExtension.printStackTrace(e);
            }
        }
    }

    public static void copyFileFromDir(String str, String str2) {
        File file = new File(str2);
        createFile(str, false);
        if (file.isDirectory()) {
            copyFileToDir(str, listFile(file));
        }
    }

    public static void copyFileToDir(String str, File file, String str2) {
        String str3 = (str2 == null || "".equals(str2)) ? str + "/" + file.getName() : str + "/" + str2;
        copyFile(new File(str3), file);
    }

    public static void copyFileToDir(String str, String[] strArr) {
        if (str != null && !"".equals(str)) {
            File file = new File(str);
            if (!file.exists()) {
                file.mkdir();
            } else if (!file.isDirectory()) {
                return;
            }
            for (String file2 : strArr) {
                File file3 = new File(file2);
                if (file3.isDirectory()) {
                    copyFileToDir(str + "/" + file3.getName(), listFile(file3));
                } else {
                    copyFileToDir(str, file3, "");
                }
            }
        }
    }

    public static void createFile(File file, boolean z) {
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                createFile(file.getParentFile(), false);
            } else if (z) {
                try {
                    file.createNewFile();
                } catch (Throwable e) {
                 //   ThrowableExtension.printStackTrace(e);
                }
            } else {
                file.mkdir();
            }
        }
    }

    public static void createFile(String str, boolean z) {
        createFile(new File(str), z);
    }

    public static void fileMove(String str, String str2) throws Exception {
        try {
            File[] listFiles = new File(str).listFiles();
            if (listFiles != null) {
                File file = new File(str2);
                if (!file.exists()) {
                    file.mkdirs();
                }
                for (int i = 0; i < listFiles.length; i++) {
                    if (listFiles[i].isDirectory()) {
                        fileMove(listFiles[i].getPath(), str2 + "\\" + listFiles[i].getName());
                        listFiles[i].delete();
                    }
                    File file2 = new File(file.getPath() + "\\" + listFiles[i].getName());
                    if (file2.exists()) {
                        file2.delete();
                    }
                    listFiles[i].renameTo(file2);
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static List<File> getFileSort(String str) {
        List<File> files = getFiles(str, new ArrayList());
        if (files != null && files.size() > 0) {
            Collections.sort(files, new Comparator<File>() {
                public int compare(File file, File file2) {
                    return file.lastModified() < file2.lastModified() ? 1 : file.lastModified() == file2.lastModified() ? 0 : -1;
                }
            });
        }
        return files;
    }

    public static List<File> getFiles(String str, List<File> list) {
        File file = new File(str);
        if (file.isDirectory()) {
            for (File file2 : file.listFiles()) {
                if (file2.isDirectory()) {
                    getFiles(file2.getAbsolutePath(), list);
                } else {
                    list.add(file2);
                }
            }
        }
        return list;
    }

    public static String getValueFromPath(String... strArr) {
        String str = "";
        for (String file : strArr) {
            File file2 = new File(file);
            if (file2.exists()) {
                str = readContentFromFile(file2);
                if (!OtherUtils.isEmpty(str)) {
                    break;
                }
            }
        }
        return str;
    }

    public static String[] listFile(File file) {
        String absolutePath = file.getAbsolutePath();
        String[] list = file.list();
        String[] strArr = new String[list.length];
        for (int i = 0; i < list.length; i++) {
            strArr[i] = absolutePath + "/" + list[i];
        }
        return strArr;
    }

    public static String parseChineseToUtf8(String str) throws Exception {
        return URLEncoder.encode(str, "UTF-8");
    }

    public static String parseUtf8ToAscII(String str) throws Exception {
        return URLDecoder.decode(str, "UTF-8");
    }

    private static byte[] readByteFromFile(File file) {
        byte[] bArr = null;
        try {
            bArr = new byte[Long.valueOf(file.length()).intValue()];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(bArr);
            fileInputStream.close();
            return bArr;
        } catch (Exception e) {
            return bArr;
        }
    }

    private static String readContentFromFile(File file) {
        try {
            byte[] bArr = new byte[Long.valueOf(file.length()).intValue()];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(bArr);
            fileInputStream.close();
            String str = new String(bArr);
            try {
                return Pattern.compile("\\s*|\t|\r|\n").matcher(str).replaceAll("");
            } catch (Exception e) {
                return str;
            }
        } catch (Exception e2) {
            return "";
        }
    }

    private static void saveByteToConfig(String str, String str2) throws Exception {
        File file = new File(str);
        if (!(file.exists() || file.getParentFile().exists())) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(str2.getBytes("UTF-8"));
        fileOutputStream.close();
    }

    private static void saveByteToConfig(String str, byte[] bArr) throws Exception {
        File file = new File(str);
        if (!(file.exists() || file.getParentFile().exists())) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStream fileOutputStream = new FileOutputStream(file);
        new BufferedOutputStream(fileOutputStream).write(bArr);
        fileOutputStream.close();
    }

    private static void saveStringToConfig(String str, String str2) throws Exception {
        File file = new File(str);
        if (!(file.exists() || file.getParentFile().exists())) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(str2);
        bufferedWriter.close();
    }

    private static void saveStringToConfig(String str, String str2, boolean z) {
        try {
            File file = new File(str);
            if (!(file.exists() || file.getParentFile().exists())) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, z));
            bufferedWriter.write(str2);
            bufferedWriter.close();
        } catch (Throwable e) {
           // ThrowableExtension.printStackTrace(e);
        }
    }

    public static void setValueToPath(String str, boolean z, String... strArr) {
        String str2 = str;
        for (String str3 : strArr) {
            if (!str2.endsWith("\n")) {
                str2 = str2 + "\n";
            }
            saveStringToConfig(str3, str2, z);
        }
    }
}