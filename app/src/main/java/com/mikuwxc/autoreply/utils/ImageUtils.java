package com.mikuwxc.autoreply.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;


import com.mikuwxc.autoreply.R;
import com.mikuwxc.autoreply.StaticData;
import com.mikuwxc.autoreply.common.MyApp;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2016/4/22.
 */
public class ImageUtils {

    public static Map<String, String> fileCache = new HashMap<>();

    //创建图片保存地址
    public static File createImageSavePath(Context context) {
//        String fileName = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss_SSS").format(new Date(System.currentTimeMillis()));
        String fileName = "IMG_share_" + StaticData.IMGID++;
        fileName += ".jpg";
        File file = null;
        try {
            file = new FileUtil(context).createSDDir(MyApp.FILE_URL + "/image/");
        } catch (Exception e) {
            file = new File(Environment.getExternalStorageDirectory() + "");
        }
        file = new File(file, fileName);
        return file;
    }

    //保存到相册
    public static boolean saveImageToGallery(Context context, Bitmap bmp, File file) {
        boolean isSave = true;
        // 首先保存图片
        if (bmp == null || file == null) {
            return isSave = false;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            isSave = false;
            e.printStackTrace();
        } catch (IOException e) {
            isSave = false;
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e) {
            isSave = false;
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
        return isSave;
    }


    public static boolean isImage(String url) {
        boolean image = false;
        if (url.endsWith(".jpg")) {
            image = true;
        } else if (url.endsWith(".png")) {
            image = true;
        } else if (url.endsWith(".jpeg")) {
            image = true;
        }
        return image;
    }


    public static DisplayImageOptions getDisplayImageOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.app_icon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .resetViewBeforeLoading(true)
                .build();
    }

    /* 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片
     * 这里的path是图片的地址
     */
    public static void getImageForUrl(final String path, final Context context, final OnLoadListener onLoadListener) {
        final Activity activity = (Activity) context;
        if (fileCache.get(path) != null && onLoadListener != null) {
            Log.e("111","本地拿+777777777777777777777777777777777777");
            final File cacheFile = new File(fileCache.get(path));
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onLoadListener.onSuccess(cacheFile);
                }
            });
        } else { // 从网络上获取图片
            Log.e("111","从网络上获取图片+777777777777777777777777777777777777");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final File file = createImageSavePath(context);// 如果图
                    try {
                        URL url = new URL(path);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(5000);
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);
                        if (conn.getResponseCode() == 200) {
                            InputStream is = conn.getInputStream();
                            FileOutputStream fos = new FileOutputStream(file);
                            byte[] buffer = new byte[1024];
                            int len = 0;
                            while ((len = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                            }
                            if (onLoadListener != null) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        onLoadListener.onSuccess(file);
                                    }
                                });
                            }
                            fileCache.put(path, file.getAbsolutePath());
                            is.close();
                            fos.close();
                            // 返回一个URI对象
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public interface OnLoadListener {
        void onSuccess(File file);
    }

}
