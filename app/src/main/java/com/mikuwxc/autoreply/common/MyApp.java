package com.mikuwxc.autoreply.common;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.callback.StringCallback;
import com.mikuwxc.autoreply.common.net.NetApi;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.common.util.LogUtils;
import com.mikuwxc.autoreply.common.util.SharedPrefsUtils;
import com.mikuwxc.autoreply.common.util.ToastUtil;
import com.mikuwxc.autoreply.common.util.Utils;
import com.lzy.okgo.OkGo;
import com.mikuwxc.autoreply.modle.HttpImeiBean;
import com.mikuwxc.autoreply.service.ContextHolder;
import com.mikuwxc.autoreply.utils.LogToFile;
import com.mikuwxc.autoreply.utils.PersistentCookieStore;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMFriendshipSettings;
import com.tencent.imsdk.TIMGroupEventListener;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupSettings;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMLogListener;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSNSChangeInfo;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.ext.group.TIMGroupAssistantListener;
import com.tencent.imsdk.ext.group.TIMGroupCacheInfo;
import com.tencent.imsdk.ext.group.TIMUserConfigGroupExt;
import com.tencent.imsdk.ext.message.TIMUserConfigMsgExt;
import com.tencent.imsdk.ext.sns.TIMFriendshipProxyListener;
import com.tencent.imsdk.ext.sns.TIMUserConfigSnsExt;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.PublicKey;
import java.util.List;
import java.util.logging.Level;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

/**
 * Created by miku01 on 2018-02-12.
 */

public class MyApp extends Application {
    private static final String TAG = MyApp.class.getSimpleName();




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

        ContextHolder.initial(this);
        // 获取Runtime对象  获取root权限
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }


        File appPath = new File(AppConfig.APP_FOLDER);
        if (!appPath.exists()) {
            appPath.mkdirs();
        }
        SharedPrefsUtils.init(getApplicationContext(), "config");
        AppConfig.init();
        Utils.init(this);
        OkGo.init(this);
        OkGo.getInstance().debug("OKGo", Level.INFO, true);//打包去掉log
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        AppConfig.Registration_Id = SharedPrefsUtils.getString("regId");
        if (AppConfig.Registration_Id == null) {
            AppConfig.Registration_Id = JPushInterface.getRegistrationID(this);
        }
        LogUtils.w(TAG, "init regId:" + AppConfig.Registration_Id);
        Log.e("222",AppConfig.Registration_Id);
        //initTMConfig();
        initTIMUserConfig();






        //init for api client sync cookie
        manager = new CookieManager(
                new PersistentCookieStore(this),
                CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        bd = new Build();
        Log.i("info", bd.MODEL + ":" + bd.DEVICE + ":" + bd.PRODUCT);
        //ActiveAndroid.initialize(this); //数据库初始化
        initTencent();
        initImageLoader(this);
        LogToFile.init(this);//初始化Log工具



        //Bugly.init(this, "9bcbc1d675", true);


        hotUpdate();
    }


    //初始化Bugly
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
       // MultiDex.install(base);

        initSophix();



        // 安装tinker
        //Beta.installTinker();
    }

    private void hotUpdate() {
        OkGo.get(AppConfig.OUT_NETWORK + NetApi.upDateHot).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, okhttp3.Response response) {
                Log.e("111","result:" + s);
                try {
                    HttpImeiBean<Integer> bean = new Gson().fromJson(s, new TypeToken<HttpImeiBean<Integer>>(){}.getType());
                    if (bean.isSuccess()) {

                        //获取当前app版本号与后台对比后台版本号大于本地app版本号时进行更新
                     /*   PackageInfo packageInfo = null;
                        packageInfo = getApplicationContext()
                                      .getPackageManager()
                                      .getPackageInfo(getPackageName(), 0);*/
                        //Integer version = Integer.valueOf(packageInfo.versionCode);
                        if(bean.getResult() > VersionInfo.versionCode){
                         /*queryAndLoadNewPatch不可放在attachBaseContext 中，
                           否则无网络权限，建议放在后面任意时刻，如onCreate中*/
                         //去阿里看是否有补丁包
                            SophixManager.getInstance().queryAndLoadNewPatch();
                        }

                        Log.e("111", "获取App版本信息成功:"+bean.getResult());
                    }else {
                        Log.e("111", "获取App版本信息失败:");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("111", "获取App版本信息失败:"+e.toString());
                }
            }
            @Override
            public void onError(Call call, okhttp3.Response response, Exception e) {
                super.onError(call, response, e);
                Log.e("111", "获取App版本信息失败:");
            }
        });
    }


    /**
     * 初始化腾讯云IM 配置
     */
    private void initTMConfig() {
        //初始化SDK基本配置
        TIMSdkConfig config = new TIMSdkConfig(Config.IM_SDKAPPID_INT)
                .enableCrashReport(false)
                .enableLogPrint(true)
                .setLogLevel(TIMLogLevel.DEBUG)
                .setLogPath(Environment.getExternalStorageDirectory().getParent() + "/justfortest/");
        //初始化SDK
        TIMManager.getInstance().init(this, config);
        //2.初始化SDK配置
        TIMSdkConfig sdkConfig = TIMManager.getInstance().getSdkConfig();
        sdkConfig.setLogListener(new TIMLogListener() {
            @Override
            public void log(int i, String s, String s1) {

            }
        });
//2.初始化SDK配置
    }

    /**
     * 初始化腾讯云 基本用户配置
     */
    private void initTIMUserConfig() {
        //基本用户配置
        TIMUserConfig userConfig = new TIMUserConfig()
                //设置群组资料拉取字段
                .setGroupSettings(new TIMGroupSettings())
                //设置资料关系链拉取字段
                .setFriendshipSettings(new TIMFriendshipSettings())
                //设置用户状态变更事件监听器
                .setUserStatusListener(new TIMUserStatusListener() {
                    @Override
                    public void onForceOffline() {
                        //被其他终端踢下线
                        Log.i(TAG, "onForceOffline");
                    }

                    @Override
                    public void onUserSigExpired() {
                        //用户签名过期了，需要刷新userSig重新登录SDK
                        Log.i(TAG, "onUserSigExpired");
                    }
                })
                //设置连接状态事件监听器
                .setConnectionListener(new TIMConnListener() {
                    @Override
                    public void onConnected() {
                        Log.i(TAG, "onConnected");
                    }

                    @Override
                    public void onDisconnected(int code, String desc) {
                        Log.i(TAG, "onDisconnected");
                    }

                    @Override
                    public void onWifiNeedAuth(String name) {
                        Log.i(TAG, "onWifiNeedAuth");
                    }
                })
                //设置群组事件监听器
                .setGroupEventListener(new TIMGroupEventListener() {
                    @Override
                    public void onGroupTipsEvent(TIMGroupTipsElem elem) {
                        Log.i(TAG, "onGroupTipsEvent, type: " + elem.getTipsType());
                    }
                })
                //设置会话刷新监听器
                .setRefreshListener(new TIMRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh");
                    }

                    @Override
                    public void onRefreshConversation(List<TIMConversation> conversations) {
                        Log.i(TAG, "onRefreshConversation, conversation size: " + conversations.size());
                    }
                });

        //消息扩展用户配置
        userConfig = new TIMUserConfigMsgExt(userConfig)
                //禁用消息存储
                .enableStorage(false)
                //开启消息已读回执
                .enableReadReceipt(true);
        //资料关系链扩展用户配置
        userConfig = new TIMUserConfigSnsExt(userConfig)
                //开启资料关系链本地存储
                .enableFriendshipStorage(true)
                //设置关系链变更事件监听器
                .setFriendshipProxyListener(new TIMFriendshipProxyListener() {
                    @Override
                    public void OnAddFriends(List<TIMUserProfile> users) {
                        Log.i(TAG, "OnAddFriends");
                    }

                    @Override
                    public void OnDelFriends(List<String> identifiers) {
                        Log.i(TAG, "OnDelFriends");
                    }

                    @Override
                    public void OnFriendProfileUpdate(List<TIMUserProfile> profiles) {
                        Log.i(TAG, "OnFriendProfileUpdate");
                    }

                    @Override
                    public void OnAddFriendReqs(List<TIMSNSChangeInfo> reqs) {
                        Log.i(TAG, "OnAddFriendReqs");
                    }
                });

        //群组管理扩展用户配置
        userConfig = new TIMUserConfigGroupExt(userConfig)
                //开启群组资料本地存储
                .enableGroupStorage(true)
                //设置群组资料变更事件监听器
                .setGroupAssistantListener(new TIMGroupAssistantListener() {
                    @Override
                    public void onMemberJoin(String groupId, List<TIMGroupMemberInfo> memberInfos) {
                        Log.i(TAG, "onMemberJoin");
                    }

                    @Override
                    public void onMemberQuit(String groupId, List<String> members) {
                        Log.i(TAG, "onMemberQuit");
                    }

                    @Override
                    public void onMemberUpdate(String groupId, List<TIMGroupMemberInfo> memberInfos) {
                        Log.i(TAG, "onMemberUpdate");
                    }

                    @Override
                    public void onGroupAdd(TIMGroupCacheInfo groupCacheInfo) {
                        Log.i(TAG, "onGroupAdd");
                    }

                    @Override
                    public void onGroupDelete(String groupId) {
                        Log.i(TAG, "onGroupDelete");
                    }

                    @Override
                    public void onGroupUpdate(TIMGroupCacheInfo groupCacheInfo) {
                        Log.i(TAG, "onGroupUpdate");
                    }
                });

        //将用户配置与通讯管理器进行绑定
        TIMManager.getInstance().setUserConfig(userConfig);
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



    public static RequestQueue getHttpQueues() {
        return requestQueue;
    }



    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }



    /**
     * 初始化Sophix
     * 需要在attachBaseContext方法里面调用
     * 并且要在super.attachBaseContext(base);和Multidex.install方法之后调用
     * 且在其他方法之前
     */
    private void initSophix() {
        String appVersion = "4.0";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        SophixManager.getInstance().setContext(this)
                /*
                    设置版本号，版本号与控制台的版本号统一，才可以更新
                    这里我踩的坑，控制台上添加版本，是添加需要更新的版本，与版本升级没有关系
                 */
                .setAppVersion(appVersion)
                //<可选>用户自定义aes秘钥, 会对补丁包采用对称加密
                .setAesKey(null)
                /*
                    <可选> isEnabled默认为false, 是否调试模式, 调试模式下会输出日志以及不进行补丁签名校验.
                    线下调试此参数可以设置为true, 查看日志过滤TAG
                    正式发布必须改为false，否则存在安全风险
                 */
                .setEnableDebug(true)
                /*
                     <可选，推荐使用> 三个Secret分别对应AndroidManifest里面的三个，
                     可以不在AndroidManifest设置而是用此函数来设置Secret
                 */
                .setSecretMetaData(null, null, null)
                /*
                     <可选> 设置patch加载状态监听器,
                    该方法参数需要实现PatchLoadStatusListener接口
                 */
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(int mode, int code, String info, int handlePatchVersion) {
                        // 补丁加载回调通知
                        Log.e("sophix", "onLoad: 补丁加载回调通知  code = " + code);
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            // 表明补丁加载成功
                            Log.e("sophix", "onLoad: 表明补丁加载成功");
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
                            Log.e("sophix", "onLoad: 表明新补丁生效需要重启. 开发者可提示用户或者强制重启");
                            //SophixManager.getInstance().killProcessSafely();

                            //热更新成功后重启代码
                            Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                            PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                            AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 50, restartIntent);
                            android.os.Process.killProcess(android.os.Process.myPid());
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
                            Log.e("sophix", "onLoad: 其它错误信息, 查看PatchStatus类说明"+code);
                        }
                    }
                }).initialize();
    }

}
