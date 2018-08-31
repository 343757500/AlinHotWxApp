package com.mikuwxc.autoreply.common.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Date: 2018/5/14 11:11.
 * Desc:
 */
public class MyFileUtil {
    private static final String TAG = MyFileUtil.class.getSimpleName();

    public static void writeToNewFile(String filePath, String content) {
        try {
            File file = new File(filePath);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            osw.write(content);
            osw.flush();
            fos.flush();
            osw.close();
            fos.close();
            LogUtils.e(TAG, "成功保存到文件:" + filePath);
        } catch (Exception e) {
            LogUtils.e(TAG, "保存出错");
            e.printStackTrace();
        }
    }

    public static String readFromFile(String filePath) {
        String content = null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis, "utf-8");
                char input[] = new char[fis.available()];
                isr.read(input);
                content = new String(input);
                LogUtils.w(TAG, "读取content:" + content);
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "读取文件出错");
            e.printStackTrace();
        }
        return content;
    }

    public static List<String> getFileListOnSpecifiedTime(List<String> fileList, long spTime, long before, long after) {
        List<String> specifiedList = new ArrayList<>();
        for (int i = 0; i < fileList.size(); i++) {
            File file = new File(fileList.get(i));
            if (file.lastModified() > spTime - before && file.lastModified() < spTime + after) {
                specifiedList.add(fileList.get(i));
            }
        }
        return specifiedList;
    }

    /**
     * 比较指定的文件列表里最新的目录或文件
     *
     * @param fileList
     * @return
     */
    public static String getLastModifiedFile(List<String> fileList) {
        ArrayList<FileInfo> fileInfos = new ArrayList<FileInfo>();//将需要的子文件信息存入到FileInfo里面
        for (int i = 0; i < fileList.size(); i++) {
            File file = new File(fileList.get(i));
            FileInfo fileInfo = new FileInfo();
            fileInfo.name = file.getName();
            fileInfo.path = file.getPath();
            fileInfo.lastModified = file.lastModified();
            fileInfos.add(fileInfo);
        }
        Collections.sort(fileInfos, new FileComparator());
        return fileInfos.get(fileInfos.size() - 1).path;
    }

    /**
     * 获取该目录下最新修改的文件.
     *
     * @param parentFiles
     * @return
     */
    public static String getLastModifiedFile(File parentFiles) {
        return getLastModifiedFile(parentFiles, null);
    }

    /**
     * 获取该目录下最新修改的文件.
     * @param parentFiles
     * @param format      amr,jpg,mp4
     * @return 返回最新修改的文件的路径. 如果路径为文件或该目录下没文件则返回null.
     */
    public static String getLastModifiedFile(File parentFiles, String format) {
        File[] files;
        if (format == null) {
            files = parentFiles.listFiles();
        } else {
            if (format.equals("amr")) {
                files = parentFiles.listFiles(voiceFilter);
            } else if (format.equals("jpg")) {
                files = parentFiles.listFiles(imgFilter);
            } else if (format.equals("mp4")) {
                files = parentFiles.listFiles(videoFilter);
            } else {
                files = parentFiles.listFiles();
            }
        }
        if (files == null || files.length == 0) {
            LogUtils.e(TAG, "******  " + parentFiles + "  目录下没" + format + "文件");
            return null;
        }
        ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();//将需要的子文件信息存入到FileInfo里面
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            FileInfo fileInfo = new FileInfo();
            fileInfo.name = file.getName();
            fileInfo.path = file.getPath();
            fileInfo.lastModified = file.lastModified();
            fileList.add(fileInfo);
        }
        Collections.sort(fileList, new FileComparator());
        return fileList.get(fileList.size()-1).path;
    }

    public static FileFilter imgFilter = new FileFilter() {
        public boolean accept(File file) {
            String tmp = file.getName().toLowerCase();
            if (tmp.endsWith(".jpg")) {
                return true;
            }
            return false;
        }
    };

    public static FileFilter voiceFilter = new FileFilter() {
        public boolean accept(File file) {
            String tmp = file.getName().toLowerCase();
            if (tmp.endsWith(".amr")) {
                return true;
            }
            return false;
        }
    };

    public static FileFilter videoFilter = new FileFilter() {
        public boolean accept(File file) {
            String tmp = file.getName().toLowerCase();
            if (tmp.endsWith(".mp4")) {
                return true;
            }
            return false;
        }
    };

    public static class FileInfo {
        public String name;
        public String path;
        public long lastModified;
    }

    public static class FileComparator implements Comparator<FileInfo> {
        public int compare(FileInfo file1, FileInfo file2) {
            if (file1.lastModified < file2.lastModified) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
