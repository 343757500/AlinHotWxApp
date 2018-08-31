/*
package com.hsl.helper.autoreply;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mikuwxc.autoreply.utils.LogToFile;
import com.mikuwxc.autoreply.utils.PersistentCookieStore;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.PublicKey;

*/
/**
 * Created by Administrator on 2016/11/19.
 *//*

public class WxCApplication extends Application {
    public static final String WX_APP_ID = "wxe7aa599f2f5f44f9";
    public static final String WX_APP_SECRET = "ea2b4d8e7733959d98b41df3254c9346";
    public static final String FILE_URL = "DuoQun";
    public static IWXAPI api;
    public static PublicKey publicKey = null;
    public static RequestQueue requestQueue;
    public static boolean isWelcom = true;
    public static ImageLoader loader;
    public static ImageLoaderConfiguration.Builder builder;
    public static CookieManager manager;
    public static TelephonyManager tm;
    public static Build bd;

    @Override
    public void onCreate() {
        super.onCreate();
        //init for api client sync cookie
        manager = new CookieManager(
                new PersistentCookieStore(this),
                CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        bd = new Build();
        Log.i("info", bd.MODEL + ":" + bd.DEVICE + ":" + bd.PRODUCT);
        ActiveAndroid.initialize(this); //数据库初始化
        initTencent();
        initImageLoader(this);
        LogToFile.init(this);//初始化Log工具

    }

    private void initImageLoader(Context context) {
        loader = ImageLoader.getInstance();
        builder = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(5)
                .memoryCache(new LRULimitedMemoryCache(10 * 1024 * 1024))
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSize(50 * 1024 * 1024)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCache(new UnlimitedDiskCache(StorageUtils.getCacheDirectory(context)))
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.FIFO);
        ImageLoaderConfiguration config = builder.build();
        loader.init(config);
    }

    private void initTencent() {
        api = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
        api.registerApp(WX_APP_ID);
    }
}
*/
