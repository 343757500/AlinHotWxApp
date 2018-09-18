package com.mikuwxc.autoreply.xposed;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.mikuwxc.autoreply.bean.DeleteFriendBean;
import com.mikuwxc.autoreply.common.BaseHook;
import com.mikuwxc.autoreply.common.net.NetApi;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.common.util.LogUtils;
import com.mikuwxc.autoreply.common.util.MultiFileObserver;
import com.mikuwxc.autoreply.common.util.MyFileUtil;
import com.mikuwxc.autoreply.common.util.RegularUtils;
import com.mikuwxc.autoreply.common.util.Utils;
import com.mikuwxc.autoreply.modle.FriendBean;
import com.mikuwxc.autoreply.modle.HookMessageBean;
import com.mikuwxc.autoreply.modle.HttpBean;
import com.mikuwxc.autoreply.receiver.Constance;
import com.mikuwxc.autoreply.utils.DBManager;
import com.mikuwxc.autoreply.wcapi.WechatEntityFactory;
import com.mikuwxc.autoreply.wcentity.AutoVerifyAllEntity;
import com.mikuwxc.autoreply.wcentity.FileEntity;
import com.mikuwxc.autoreply.wcentity.LuckyMoneyMessage;
import com.mikuwxc.autoreply.wcentity.UserEntity;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wcutil.AuthUtil;
import com.mikuwxc.autoreply.wcutil.DownLoadWxResFromWxUtil;
import com.mikuwxc.autoreply.wcutil.FriendUtil;
import com.mikuwxc.autoreply.wcutil.XmlToJson;
import com.mikuwxc.autoreply.wx.WechatDb;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.upyun.library.common.Params;
import com.upyun.library.common.UploadEngine;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;
import com.upyun.library.utils.UpYunUtils;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import okhttp3.Call;
import okhttp3.Response;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Created by Dusan (duqian) on 2017/5/11 - 11:46.
 * E-mail: duqian2010@gmail.com
 * Description:微信消息注入
 * remarks:
 */

public class HookMessage extends BaseHook implements MultiFileObserver.MessagePathListener {

    private static final String TAG = HookMessage.class.getSimpleName();
    private static Handler handler;
    private static Object requestCaller;
    private static List<HookMessageBean> list_msg = new ArrayList<>();
    public static List<HookMessageBean> list_msgFail = new ArrayList<>();
    private boolean logined = false;
    private TIMConversation conversation;
    private String token;
    private Context mContext;
    private boolean counting = false;
    private CountDownTimer cdt;
    private int errorStartIndex = -1;
    private int errorEndIndex = -1;
    private MultiFileObserver mFileObserver;
    private List<String> voiceList = new ArrayList<>(); //含有voice/一级/二级的路径
    private List<String> amrList = new ArrayList<>();//存放已找到的amr路径,用于筛选.
    private List<String> imageSentList = new ArrayList<>();
    private List<String> imageRcvList = new ArrayList<>();
    private List<String> videoList = new ArrayList<>();
    private int fix=0;

    //监听微信聊天图片传送到又拍云文件夹的这个文件夹下
    private String savePath = "/imaginforapp/{year}{mon}{day}/{random32}{.suffix}";

    private String imgPath;
    private String rcyimgPath;
    /** 存放当前文件夹下所有文件的路径的集合 **/
    private static ArrayList<String> paths = new ArrayList<String>();
    //为了做一个标志，防止在聊天页面hook到图片后频繁上传服务器
    private String oneImagin;
    //为了做一个标志，防止在聊天页面hook到图片后频繁上传服务器
    private String rcyImagin;
    //为了做一个标志，防止在聊天页面hook到发送视频后频繁上传服务器
    private String sedImagin;
    //为了做一个标志，防止在聊天页面hook到接受的视频后频繁上传服务器
    private String reyImagin;
    //为了做一个标志，防止在聊天页面hook到接受的语音文件后频繁上传服务器
    private String newVoiseNull;
    //为了做一个标志，防止在聊天页面hook到接受的图片文件后频繁上传服务器
    private String newImaginNull;
    //为了做一个标志，防止在聊天页面hook到接受的File文件后频繁上传服务器
    private String newFileNull;
    //为了做一个标志，防止在聊天页面hook到接受的自己发送的File文件后频繁上传服务器
    private String sendFileNull;
    private String mainImagin;
    private String autoVerifyNull;

    //自动领红包后提示已经领取到后台  此时type为10000
    private String momyType;
    private String momyTypeNull;
    private String momyContent;

    //转账相关
    private String accountsType;
    private String accountsIsSend;
    private String  accountsContent;
    private String accountsNull;


    //红包相关
    private String accountsMonyType;
    private String accountsMonyContent;
    private String  accountsMonyNull;

    private String sedVideoPath;
    private String reyVideoPath;


    private static String str;
    private static String paramAnonymousMethodHookParam1;
    private static ContentValues localContentValues;
    private String newVoisePath;
    private String status;
    private String filestatus;
    private String monystatus;
    private String picstatus;
    private String voisestatus;
    private int  videoState;  //视频的发送或者接受状态
    private String newImaginPath;
    private String newFilePath;
    private String sendFilePath;
    private String autoVerifyUser;



    //自动收红包相关
    private static String wechatVersion = "";
    private static List<LuckyMoneyMessage> luckyMoneyMessages = new ArrayList<>();
    private static Object requestCallerMony;
   private XC_LoadPackage.LoadPackageParam loadPackageParam;
    private int field_status;


    //    private  SettingsHelper mSettings;
    public HookMessage(ClassLoader classLoader, Context context,final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        super(classLoader, context);
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        if (mFileObserver == null) {
            mFileObserver = new MultiFileObserver(AppConfig.ROOT + "/tencent/MicroMsg");
            mFileObserver.startWatching();
            mFileObserver.setPathListener(this);
        }
        this.mContext = context;
        this.loadPackageParam=loadPackageParam;
//        mContext = (Context) AndroidAppHelper.currentApplication();
        conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, "2fd3f9ee9ea446f2a3ea98cc8d4051f7");
        if (mContext == null) {
            final Class<?> aClass = findClass("android.app.ActivityThread", null);
            Object[] object = new Object[0];
            final Object currentActivityThread = callStaticMethod(aClass, "currentActivityThread", object);
            mContext = (Context) callMethod(currentActivityThread, "getSystemContext", object);
        }
        Utils.init(mContext);
    }

    //hook聊天列表里面的消息
    public void hookConversationItem() {
        WechatEntity create = WechatEntityFactory.create(CommonHook.wechatVersionName);
        getVoicePathHook(create);


        findAndHookMethod(VersionParam.conversationClass, classLoader,
                VersionParam.con_GetCursorMethod, Cursor.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        synchronized (HookMessage.class) {
                           /* if (RegularUtils.SENSITIVE_WORD == null) {
                                RegularUtils.SENSITIVE_WORD = MyFileUtil.readFromFile(AppConfig.APP_FOLDER + "/sensitive");
                            } else {
                                File updateFile = new File(AppConfig.APP_FOLDER + "/update");
                                if (updateFile.exists()) {//需要更新敏感词
                                    RegularUtils.SENSITIVE_WORD = MyFileUtil.readFromFile(AppConfig.APP_FOLDER + "/sensitive");
                                    updateFile.delete();
                                }
                            }*/
                            String field_content = (String) getObjectField(param.thisObject, "field_content");
                            String field_username = (String) getObjectField(param.thisObject, "field_username");
                            String field_msgType = (String) getObjectField(param.thisObject, "field_msgType");//1:文字(status=6的时候是新的朋友); 3:图片 34:语音 42:个人名片 43:小视频 47:动图表情 48:发送位置 49:文章/文件 50:视频/语音聊天 10000:系统消息(被添加好友) -1879048186(负数):位置共享 436207665:红包  419430449:转账
                            int field_unReadCount = (int) getObjectField(param.thisObject, "field_unReadCount"); //对方发送的,第一次拦截到,unreadCount不为0,自己发送的一直都是0
                            //0:剪子石头布或骰子准备出的状态 1:自己正在发送的;2:自己已发送成功的,3:对方发来的 4:系统消息(被添加好友) 6:对方发起视频聊天(语音的聊天,3代表未接,6代表接通. )
                            field_status = (int) getObjectField(param.thisObject, "field_status");
                            long field_conversationTime = (long) getObjectField(param.thisObject, "field_conversationTime");//13位数的代表成功收到的时间,19位的是开始发送的时间,只有status为1的时候会有



                            //领取红包相关信息上传
                            if (momyType!=null&&"10000".equals(momyType)&&momyTypeNull==null){
                                handleMessage(field_unReadCount, Integer.parseInt("3"), field_username, momyContent, momyType, field_conversationTime);
                                momyTypeNull=momyType;
                            }/*else if (momyType!=null&&"10000".equals(momyType)&&momyTypeNull==null&&"3".equals(monystatus)){
                                XposedBridge.log("接收红包");
                                handleMessage(field_unReadCount,  Integer.parseInt(monystatus), field_username, momyContent, momyType, field_conversationTime);
                                momyTypeNull=momyType;
                            }*/

                            if (momyType!=null&&"436207665".equals(momyType)&&momyTypeNull==null){
                                handleMessage(field_unReadCount, Integer.parseInt(monystatus), field_username, momyContent, momyType, field_conversationTime);
                                momyTypeNull=momyType;
                            }




                            //转账相关信息上传
                            if (accountsType!=null&&"419430449".equals(accountsType)&&accountsNull==null&&"0".equals(accountsIsSend)){  //收到转账
                                handleMessage(field_unReadCount, field_status, field_username, accountsContent, accountsType, field_conversationTime);
                                accountsNull=accountsType;
                                XposedBridge.log("aaaaaaaaaaaaaaa同步收到的转账信息"+accountsContent);
                            }else if (accountsType!=null&&"419430449".equals(accountsType)&&accountsNull==null&&"1".equals(accountsIsSend)){//点击确定收款成功
                                handleMessage(field_unReadCount, field_status, field_username, accountsContent, "2000", field_conversationTime);
                                accountsNull=accountsType;
                                XposedBridge.log("bbbbbbbbbbbbbbbbb同步点击收款转账信息"+accountsContent);
                            }



                            if (sedVideoPath!=null&&field_msgType.equals("43")&& videoState ==1&&sedImagin==null&&(field_conversationTime + "").length() != 19 ){
                                sedImagin =sedVideoPath;
                                XposedBridge.log("上传自己发送的视频"+sedVideoPath);

                                File file= new File(sedVideoPath);
                                boolean exists = file.exists();
                                if(exists){
                                    XposedBridge.log("上传自己发送的视频"+exists);

                                  String uploadVideoUrl = uploadVideo(sedVideoPath,field_username,"Send", field_unReadCount, videoState, field_username, field_msgType, field_conversationTime);
//                                    XposedBridge.log("$$$$$$$$$"+uploadVideoUrl);
//                                    handleMessage(field_unReadCount, field_status, field_username, uploadVideoUrl, field_msgType, field_conversationTime);
//                                    XposedBridge.log("$$$$$$$$$同步上传自己发送的视频成功");
                                }else {
                                    XposedBridge.log("不存在不上上传对方发送的视频456"+exists);
                                    //因为视频还没下载成功，所以文件会为空，此时就需要重新进这个判断方法，知道文件下载成功
                                    sedImagin=null;
                                }

                            }

                            if (reyVideoPath!=null&&field_msgType.equals("43")&& videoState ==3&&reyImagin==null&&(field_conversationTime + "").length() != 19 ){
                                reyImagin =reyVideoPath;
                                XposedBridge.log("上传对方发送的视频"+reyVideoPath);


                               String str=reyVideoPath.substring(0,reyVideoPath.indexOf("."));
                               str=str+".mp4";
                                XposedBridge.log("上传对方发送的视频"+str);
                                File file= new File(str);
                                boolean exists = file.exists();
                                if (exists){
                                    XposedBridge.log("存在上传对方发送的视频123"+exists);
                                    String uploadVideoUrl =  uploadVideo(str,field_username,"Receive", field_unReadCount, videoState, field_username, field_msgType, field_conversationTime);
//                                    XposedBridge.log("$$$$$$$$$"+uploadVideoUrl);
//                                     handleMessage(field_unReadCount, field_status, field_username, uploadVideoUrl, field_msgType, field_conversationTime);
//                                    XposedBridge.log("$$$$$$$$$同步上传对方发送的视频成功");
                                }else{
                                    XposedBridge.log("不存在不上上传对方发送的视频456"+exists);
                                    //因为视频还没下载成功，所以文件会为空，此时就需要重新进这个判断方法，知道文件下载成功
                                    reyImagin=null;
                                }
                            }


                            if (newVoisePath!=null&&field_msgType.equals("34")&&newVoiseNull==null&&"1".equals(voisestatus)&&(field_conversationTime + "").length() != 19 ){
                                XposedBridge.log("上传自己发送的语音文件");

                                String newVoisePathUrl = uploadAmr(newVoisePath,field_username,"Send",field_unReadCount, Integer.parseInt(voisestatus), field_username, field_msgType, field_conversationTime);
                                XposedBridge.log("$$$$$$$$$"+newVoisePathUrl);
                                //handleMessage(field_unReadCount, field_status, field_username, newVoisePathUrl, field_msgType, field_conversationTime);
                                XposedBridge.log("$$$$$$$$$同步自己发送的语音文件成功");
                                newVoiseNull=newVoisePath;
                            }else if(newVoisePath!=null&&field_msgType.equals("34")&&newVoiseNull==null&&"3".equals(voisestatus)&&(field_conversationTime + "").length() != 19 ){
                                XposedBridge.log("上传接收到的语音文件");
                               String newVoisePathUrl= uploadAmr(newVoisePath,field_username,"Receive",field_unReadCount, Integer.parseInt(voisestatus), field_username, field_msgType, field_conversationTime);
                               XposedBridge.log("$$$$$$$$$"+newVoisePathUrl);
                                //handleMessage(field_unReadCount, field_status, field_username, newVoisePathUrl, field_msgType, field_conversationTime);
                                XposedBridge.log("$$$$$$$$$同步接收到的语音文件成功");
                                newVoiseNull=newVoisePath;
                            }


                            if (newImaginPath!=null&&field_msgType.equals("3")&&newImaginNull==null&&"1".equals(picstatus)&&(field_conversationTime + "").length() != 19 /*&& field_status != 3*/){
                                newImaginNull=newImaginPath;
                                File file=new File(newImaginPath);
                                if (file.exists()){
                                    XposedBridge.log("上传自己发送的图片文件");
                                    String newPicPathUrl= uploadPic(newImaginPath,field_username,"Send");
                                    XposedBridge.log("$$$$$$$$$"+newPicPathUrl);
                                    handleMessage(field_unReadCount, Integer.parseInt(picstatus), field_username, newPicPathUrl, field_msgType, field_conversationTime);
                                    XposedBridge.log("$$$$$$$$$同步接发送的图片成功");
                                }else{
                                    XposedBridge.log("newImaginPath::::::"+"发送图片文件为空");
                                    newImaginNull=null;
                                }


                            }else if(newImaginPath!=null&&field_msgType.equals("3")&&newImaginNull==null&&"3".equals(picstatus)&&(field_conversationTime + "").length() != 19 /*&& field_status != 1*/){
                                newImaginNull=newImaginPath;
                                File file=new File(newImaginPath);
                                if (file.exists()){
                                    XposedBridge.log("上传接收到的图片文件");
                                    String newPicPathUrl= uploadPic(newImaginPath,field_username,"Receive");
                                    XposedBridge.log("$$$$$$$$$"+newPicPathUrl);

                                    handleMessage(field_unReadCount, Integer.parseInt(picstatus), field_username, newPicPathUrl, field_msgType, field_conversationTime);
                                    XposedBridge.log("$$$$$$$$$同步接收到的图片成功");

                                }else{
                                    XposedBridge.log("接收到的图片文件为空");
                                    //因为图片还没下载成功，所以文件会为空，此时就需要重新进这个判断方法，知道文件下载成功
                                    newImaginNull=null;
                                }

                            }



                            if (sendFilePath!=null&&field_msgType.equals("49")&&sendFileNull==null&&"1".equals(filestatus)&&(field_conversationTime + "").length() != 19 ){
                                sendFileNull=sendFilePath;
                                XposedBridge.log("707120170"+sendFilePath);
                                File file=new File(sendFilePath);
                                if (file.exists()){
                                    XposedBridge.log("上传自己发送的File文件");
                                    String newFilePathUrl= uploadFile(sendFilePath,field_username,"Send",field_unReadCount, field_status, field_username, field_msgType, field_conversationTime);
                                    XposedBridge.log("$$$$$$$$$"+newFilePathUrl);

                                    //handleMessage(field_unReadCount, field_status, field_username, newFilePathUrl, field_msgType, field_conversationTime);
                                    XposedBridge.log("$$$$$$$$$同步接收到的File成功");
                                }else{
                                    XposedBridge.log("接收到的自己发送的File文件为空");
                                    //因为图片还没下载成功，所以文件会为空，此时就需要重新进这个判断方法，知道文件下载成功
                                    sendFileNull=null;
                                }

                            }else if(newFilePath!=null&&field_msgType.equals("49")&&newFileNull==null&&"3".equals(filestatus)&&(field_conversationTime + "").length() != 19 ){
                                newFileNull=newFilePath;
                                File file=new File(newFilePath);
                                if (file.exists()){
                                    XposedBridge.log("上传接收到的File文件");
                                    String newFilePathUrl= uploadFile(newFilePath,field_username,"Receive",field_unReadCount, field_status, field_username, field_msgType, field_conversationTime);
                                    XposedBridge.log("$$$$$$$$$"+newFilePathUrl);

                                   // handleMessage(field_unReadCount, field_status, field_username, newFilePathUrl, field_msgType, field_conversationTime);
                                    XposedBridge.log("$$$$$$$$$同步接收到的File成功");

                                }else{
                                    XposedBridge.log("接收到的File文件为空");
                                    //因为图片还没下载成功，所以文件会为空，此时就需要重新进这个判断方法，知道文件下载成功
                                    newFileNull=null;
                                }

                            }




                            if ((field_unReadCount == 0 && field_status == 3) || (field_unReadCount == 0 && field_status == 0) || (TextUtils.isEmpty(field_content) && field_status != 2)) {
                                return;
                            }
                            if (field_username.contains("微信团队") || field_username.toLowerCase().contains("wechat")
                                    || field_username.contains("@chatroom")) {
                                return;
                            }
                            if ((field_conversationTime + "").length() == 19 || field_status == 1) {
                                return;
                            }
                            if (list_msg.size() == 0&&!field_msgType.equals("34")&&!field_msgType.equals("43")&&!field_msgType.equals("3")&&!field_msgType.equals("49")&&!field_msgType.equals("10000")&&!field_msgType.equals("419430449")&&!field_msgType.equals("1")&&!"436207665".equals(field_msgType)) {
                                handleMessage(field_unReadCount, field_status, field_username, field_content, field_msgType, field_conversationTime);
                            } else {
                                //对比之前的信息
                                boolean isSaved = false;
                                for (int i = 0; i < list_msg.size(); i++) {
                                    HookMessageBean msgBean = list_msg.get(i);
                                    if (msgBean.getStatus() == field_status && msgBean.getContent().equals(field_content) && msgBean.getConversationTime() == field_conversationTime && msgBean.getUsername().equals(field_username) && msgBean.getMsgType().equals(field_msgType)) {
                                        return;
                                    }
                                }
                                if (!isSaved&&!field_msgType.equals("34")&&!field_msgType.equals("43")&&!field_msgType.equals("3")&&!field_msgType.equals("49")&&!field_msgType.equals("10000")&&!field_msgType.equals("419430449")&&!field_msgType.equals("1")&&!"436207665".equals(field_msgType)) {
                                    if (list_msg.size() > 2000 && errorStartIndex == -1) {//以防信息太多导致遍历需时长,在没error的时候清零
                                        list_msg = new ArrayList<>();
                                    }
                                    handleMessage(field_unReadCount, field_status, field_username, field_content, field_msgType, field_conversationTime);
                                }
                            }
                        }
                    }
                });
    }




    private String uploadPic(String newPicPath, String username, String sign) {
        File temp = null;
        temp = new File(newPicPath);
        final Map<String, Object> paramsMap = new HashMap<>();
        //上传又拍云的命名空间
        paramsMap.put(Params.BUCKET, "cloned");


        String newPicLast = newPicPath.substring(newPicPath.lastIndexOf("/")+1);
        //又拍云的保存路径，任选其中一个
        savePath="/picforapp/"+username+"/"+sign+"/{year}{mon}{day}/"+newPicLast;

        paramsMap.put(Params.SAVE_KEY, savePath);
        //时间戳加上15秒
        paramsMap.put(Params.EXPIRATION, System.currentTimeMillis()+15);
        //进度回调，可为空
        UpProgressListener progressListener = new UpProgressListener() {
            @Override
            public void onRequestProgress(final long bytesWrite, final long contentLength) {
                Log.e(TAG, (100 * bytesWrite) / contentLength + "%");
                Log.e(TAG, bytesWrite + "::" + contentLength);
                XposedBridge.log((100 * bytesWrite) / contentLength + "%");
            }
        };
        //结束回调，不可为空
        UpCompleteListener completeListener = new UpCompleteListener() {
            @Override
            public void onComplete(boolean isSuccess, String result) {
                // textView.setText(isSuccess + ":" + result);
                Log.e(TAG, isSuccess + ":" + result);
                XposedBridge.log(isSuccess + ":" + result);
            }
        };

        //表单上传（本地签名方式）
        UploadEngine.getInstance().formUpload(temp,paramsMap , "unesmall",UpYunUtils.md5("unesmall123456"), completeListener, progressListener);

        SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        String newSavePath=AppConfig.YOUPAIYUN+"/picforapp/"+username+"/"+sign+"/"+str+"/"+newPicLast;
        return newSavePath;
    }


    private String uploadFile(String newFilePath, final String username, final String sign, final int field_unReadCount,
                              final int field_status, final String field_username, final String field_msgType, final long field_conversationTime) {
        File temp = null;
        temp = new File(newFilePath);
        final Map<String, Object> paramsMap = new HashMap<>();
        //上传又拍云的命名空间
        paramsMap.put(Params.BUCKET, "cloned");

        final String newFileLast = newFilePath.substring(newFilePath.lastIndexOf("/")+1);
        //又拍云的保存路径，任选其中一个
        savePath="/fileforapp/"+username+"/"+sign+"/{year}{mon}{day}/"+newFileLast;

        paramsMap.put(Params.SAVE_KEY, savePath);
        //时间戳加上15秒
        paramsMap.put(Params.EXPIRATION, System.currentTimeMillis()+15);
        //进度回调，可为空
        UpProgressListener progressListener = new UpProgressListener() {
            @Override
            public void onRequestProgress(final long bytesWrite, final long contentLength) {
                Log.e(TAG, (100 * bytesWrite) / contentLength + "%");
                Log.e(TAG, bytesWrite + "::" + contentLength);
                XposedBridge.log((100 * bytesWrite) / contentLength + "%");
            }
        };
        //结束回调，不可为空
        UpCompleteListener completeListener = new UpCompleteListener() {
            @Override
            public void onComplete(boolean isSuccess, String result) {
                // textView.setText(isSuccess + ":" + result);
                Log.e(TAG, isSuccess + ":" + result);
                XposedBridge.log(isSuccess + ":" + result);


                SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String newSavePath=AppConfig.YOUPAIYUN+"/fileforapp/"+username+"/"+sign+"/"+str+"/"+newFileLast;
                handleMessage(field_unReadCount, field_status, field_username, newSavePath, field_msgType, field_conversationTime);
            }
        };

        //表单上传（本地签名方式）
        UploadEngine.getInstance().formUpload(temp,paramsMap , "unesmall",UpYunUtils.md5("unesmall123456"), completeListener, progressListener);


        return "";
    }






    //上传又拍云语音文件
    private String uploadAmr(String amrPath, final String username, final String sign, final int field_unReadCount,
                             final int field_status, final String field_username, final String field_msgType, final long field_conversationTime) {
        File temp = null;
        temp = new File(amrPath);
        final Map<String, Object> paramsMap = new HashMap<>();
        //上传又拍云的命名空间
        paramsMap.put(Params.BUCKET, "cloned");


        final String newArmLast = amrPath.substring(amrPath.lastIndexOf("/")+1);
        //又拍云的保存路径，任选其中一个
        savePath="/amrforapp/"+username+"/"+sign+"/{year}{mon}{day}/"+newArmLast;




        paramsMap.put(Params.SAVE_KEY, savePath);
        //时间戳加上15秒
        paramsMap.put(Params.EXPIRATION, System.currentTimeMillis()+15);
        //进度回调，可为空
        UpProgressListener progressListener = new UpProgressListener() {
            @Override
            public void onRequestProgress(final long bytesWrite, final long contentLength) {
                Log.e(TAG, (100 * bytesWrite) / contentLength + "%");
                Log.e(TAG, bytesWrite + "::" + contentLength);
                XposedBridge.log((100 * bytesWrite) / contentLength + "%");
            }
        };
        //结束回调，不可为空
        UpCompleteListener completeListener = new UpCompleteListener() {
            @Override
            public void onComplete(boolean isSuccess, String result) {
                // textView.setText(isSuccess + ":" + result);
                Log.e(TAG, isSuccess + ":" + result);
                XposedBridge.log(isSuccess + ":" + result);

                SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String newSavePath=AppConfig.YOUPAIYUN+"/amrforapp/"+username+"/"+sign+"/"+str+"/"+newArmLast;

                XposedBridge.log("$$$$$$$$$"+newSavePath);
                handleMessage(field_unReadCount, field_status, field_username, newSavePath, field_msgType, field_conversationTime);
                XposedBridge.log("$$$$$$$$$同步上传自己发送的成功");
            }
        };

        //表单上传（本地签名方式）
        UploadEngine.getInstance().formUpload(temp,paramsMap , "unesmall",UpYunUtils.md5("unesmall123456"), completeListener, progressListener);



        return "";
    }

    //上传自己发送的视频
    private String uploadVideo(String sendVideoPath, final String username, final String sign, final int field_unReadCount,
                               final int field_status, final String field_username, final String field_msgType, final long field_conversationTime ) {
        File temp = null;
        temp = new File(sendVideoPath);
        final Map<String, Object> paramsMap = new HashMap<>();
        //上传又拍云的命名空间
        paramsMap.put(Params.BUCKET, "cloned");

        final String newVideoLast = sendVideoPath.substring(sendVideoPath.lastIndexOf("/")+1);
        //又拍云的保存路径，任选其中一个
        savePath="/videoforapp/"+username+"/"+sign+"/{year}{mon}{day}/"+newVideoLast;



        paramsMap.put(Params.SAVE_KEY, savePath);
        //时间戳加上15秒
        paramsMap.put(Params.EXPIRATION, System.currentTimeMillis()+15);

        final boolean[] flag = {false};

        //进度回调，可为空
        UpProgressListener progressListener = new UpProgressListener() {
            @Override
            public void onRequestProgress(final long bytesWrite, final long contentLength) {
                Log.e(TAG, (100 * bytesWrite) / contentLength + "%");
                Log.e(TAG, bytesWrite + "::" + contentLength);
                XposedBridge.log((100 * bytesWrite) / contentLength + "%");
            }
        };
        //结束回调，不可为空
        UpCompleteListener completeListener = new UpCompleteListener() {
            @Override
            public void onComplete(boolean isSuccess, String result) {
                // textView.setText(isSuccess + ":" + result);
                Log.e(TAG, isSuccess + ":" + result);
                XposedBridge.log(isSuccess + ":" + result);

                SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String newSavePath=AppConfig.YOUPAIYUN+"/videoforapp/"+username+"/"+sign+"/"+str+"/"+newVideoLast;

                XposedBridge.log("$$$$$$$$$"+newSavePath);
                handleMessage(field_unReadCount, field_status, field_username, newSavePath, field_msgType, field_conversationTime);
                XposedBridge.log("$$$$$$$$$同步上传自己发送的视频成功");
            }


        };

        //表单上传（本地签名方式）
        UploadEngine.getInstance().formUpload(temp,paramsMap , "unesmall",UpYunUtils.md5("unesmall123456"), completeListener, progressListener);


        return "";
    }


    private void getVoicePathHook(final WechatEntity paramWechatEntity) {

        XposedHelpers.findAndHookMethod(paramWechatEntity.sqlitedatabase_class_name, classLoader, "insertWithOnConflict", new Object[] { String.class, String.class, ContentValues.class, Integer.TYPE, new XC_MethodHook()
        {
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
                    throws Throwable
            {
                ContentValues localContentValues;
                int i;
                try
                {
                    str = (String)paramAnonymousMethodHookParam.args[0];
                    localContentValues = (ContentValues)paramAnonymousMethodHookParam.args[2];
                    XposedBridge.log("QWEREEEEEEEEEEEEEEEEE"+localContentValues.toString());


                    if (localContentValues.getAsString("encryptTalker")!=null&&localContentValues.getAsString("msgContent")!=null/*&& "1".equals(SharedPrefsUtils.getString("AntoVerity"))*/){
                        autoVerifyUser=localContentValues.toString();


                       String msgContent =localContentValues.getAsString("msgContent");
                        JSONObject xmlToJson=new XmlToJson.Builder(msgContent).build();

                        AutoVerifyAllEntity autoVerifyAllEntity=new Gson().fromJson(xmlToJson.toString(), AutoVerifyAllEntity.class);


                        handleMessage(0, 0, "", autoVerifyUser, "103", 0);


                        XSharedPreferences moneyStaus = new XSharedPreferences("com.mikuwxc.autoreply", "verifyStaus");
                        boolean verifyStaus_put = moneyStaus.getBoolean("verifyStaus_put", true);
                        if (verifyStaus_put){
                            //自动抢红包
                             FriendUtil.autoVerifyUser3(classLoader, paramWechatEntity, localContentValues.getAsString("talker"), autoVerifyAllEntity.getMsg().getTicket(), Integer.parseInt(autoVerifyAllEntity.getMsg().getScene()));
                            XposedBridge.log("自动通过好友开启");
                        }else{
                            XposedBridge.log("自动通过好友关闭");
                        }


                        UserEntity userEntity = WechatDb.getInstance().selectSelf();
                        String userName = userEntity.getUserName();
                        String userTalker = userEntity.getUserTalker();
                        String headPic = userEntity.getHeadPic();
                        String alias = userEntity.getAlias();  //微信号
                        FriendBean friendBean =friendAdviceParse(autoVerifyUser);
                        addNewFriend(alias, friendBean); //新的好友,通知后台

                    }


                    if ("20".equals(localContentValues.getAsString("msgSubType"))&&localContentValues.getAsString("path")!=null){
                        String sendImaginPath=localContentValues.getAsString("path");
                        String newsendImaginPath="/storage/emulated/0/tencent/MicroMsg/"+sendImaginPath;
                        File file=new File(newsendImaginPath);
                       boolean b= file.exists();
                       XposedBridge.log("QWER"+newsendImaginPath+"___"+b);
                       if (b){
                           newImaginPath=newsendImaginPath;
                           newImaginNull=null;
                       }

                    }else if("true".equals(localContentValues.getAsString("isUpload"))&&localContentValues.getAsString("fileFullPath")!=null){
                        XposedBridge.log(localContentValues.getAsString("fileFullPath")+localContentValues.getAsString("isUpload"));
                        String filePath=localContentValues.getAsString("fileFullPath");
                        sendFilePath=filePath;
                        sendFileNull=null;
                    }


                   /* //上传删除好友的统计
                    if ("0".equals(localContentValues.getAsString("lastSeq"))&&StringUtils.isNotBlank(localContentValues.getAsString("userName"))&&"0".equals(localContentValues.getAsString("reserved2"))){
                        UserEntity userEntity = WechatDb.getInstance().selectSelf();
                        String userName = userEntity.getUserName();
                        String userTalker = userEntity.getUserTalker();
                        String headPic = userEntity.getHeadPic();
                        String alias = userEntity.getAlias();  //微信号
                        XposedBridge.log("aliasaliasaliasalias::"+alias);
                        handleMessageDeleteFriend(alias, localContentValues.getAsString("userName"), localContentValues.getAsString("reserved2"));
                    }*/



                    i = -1;
                    switch (str.hashCode())
                    {
                        case 954925063:
                            if (!str.equals("message")) {
                                //break label355;
                            }
                            i = 0;
                    }
                }
                catch (Exception e)
                {
                    String str;
                    long l1;
                    long l2;
                    return;
                }
                if (str.equals("fmessage_msginfo"))
                {
                    i = 1;
                    //break label355;
                    ContentValues contentValues = (ContentValues)paramAnonymousMethodHookParam.args[2];
                    long l1 = contentValues.getAsLong("createTime").longValue();
                    long l2 = System.currentTimeMillis() - 300000L;
                    XposedBridge.log("===== after hook message %s %s"+ new Object[] { Long.valueOf(l1), Long.valueOf(l2) });
                    if (l1 < l2)
                    {
                        XposedBridge.log("===== after forget[%s]"+ new Object[] { contentValues.toString() });
                        return;
                    }
                    XposedBridge.log("===== after handle[%s]"+ new Object[] { contentValues.toString() });
                    if (localContentValues.getAsString("talker").startsWith("gh_")) {
                        //break label380;
                    }
                    i = localContentValues.getAsInteger("type").intValue();
                    paramAnonymousMethodHookParam1 = "";
                    if (localContentValues.containsKey("msgSvrId")) {
                        paramAnonymousMethodHookParam1 = localContentValues.getAsString("msgSvrId");
                    }
                    if ((i == 43) && (paramAnonymousMethodHookParam1 != null) && (paramAnonymousMethodHookParam1.length() > 0)) {
                        if (!localContentValues.containsKey("imgPath")) {
                        }
                    }
                }
                label355:
                label380:
                label381:
                for (paramAnonymousMethodHookParam1 = localContentValues.getAsString("imgPath");; paramAnonymousMethodHookParam1 = "")
                {
                    String type=localContentValues.getAsString("type");
                    String isSend=localContentValues.getAsString("isSend");
                    String talker=localContentValues.getAsString("talker");
                    String s=localContentValues.getAsString("createTime");



                    if ("43".equals(type)){
                        DownLoadWxResFromWxUtil.downloadWxVideoRes(classLoader, paramWechatEntity, paramAnonymousMethodHookParam1);
                    }else if("34".equals(type)) {

                        XposedBridge.log("AAAAAAAAAAAAAAAAAAAAA" + type);
                        XposedBridge.log("QQQQQQQQQQQQQQQQQQQQQ" + DownLoadWxResFromWxUtil.downloadWxVoiceRes(classLoader, paramWechatEntity, paramAnonymousMethodHookParam1));

                        String voisePath = DownLoadWxResFromWxUtil.downloadWxVoiceRes(classLoader, paramWechatEntity, paramAnonymousMethodHookParam1);

                        File file = new File(voisePath);
                        boolean b = file.exists();
                        XposedBridge.log("voisePath:::"+b);
                        if (b) {
                            newVoisePath = voisePath;
                            newVoiseNull = null;

                            if ("1".equals(isSend)){
                                voisestatus="1";
                            }else{
                                voisestatus="3";
                            }
                        }
                    }else if ("3".equals(type)){
                        try{
                            if(localContentValues.getAsString("msgSvrId")==null){
                                XposedBridge.log("msgSvrId:"+localContentValues.getAsString("msgSvrId")+"---msgId:"+String.valueOf(localContentValues.get("msgId")+"paramAnonymousMethodHookParam1"+paramAnonymousMethodHookParam1));
                                String picPath= DownLoadWxResFromWxUtil.downloadWxPicRes(classLoader, paramWechatEntity, "100", String.valueOf(localContentValues.get("msgId")), paramAnonymousMethodHookParam1);
                                XposedBridge.log("ssssssssssssssss"+picPath);
                                String imageLast = picPath.substring(picPath.lastIndexOf("/")+1);
                                XposedBridge.log("dddddddddddddd"+imageLast);
                                String  imageLast2=imageLast.substring(3,imageLast.length());
                                XposedBridge.log("dddddddddddddd"+imageLast2);
                                String newPicPath=picPath.substring(0,picPath.length()-imageLast.length())+imageLast2+".jpg";
                                XposedBridge.log("dddddddddddddd"+newPicPath);
                                File file1=new File(newPicPath);
                                boolean picBoole=file1.exists();
                                XposedBridge.log("ffffffffffffffff"+picBoole);


                                newImaginPath=newPicPath;
                                newImaginNull=null;

                                if ("1".equals(isSend)){
                                    picstatus="1";
                                }else{
                                    picstatus="3";
                                }
                                //status = localContentValues.getAsString("status");
                                XposedBridge.log("sssssssssssssss"+status);


                               /* if ("1".equals(isSend)){
                                    field_status=1;
                                    File file=new File(newImaginPath);
                                    if (file.exists()) {
                                        String newPicPathUrl = uploadPic(newImaginPath, talker, "Send");
                                        XposedBridge.log("$$$$$$$$$" + newPicPathUrl);
                                        if (s != null) {
                                            handleMessage(0, field_status, talker, newPicPathUrl, type, Long.parseLong(s));
                                        }
                                    }

                                }else {
                                    File file=new File(newImaginPath);
                                    if (file.exists()) {
                                        String newPicPathUrl = uploadPic(newImaginPath, talker, "Receive");
                                        XposedBridge.log("$$$$$$$$$" + newPicPathUrl);
                                        handleMessage(0, field_status, talker, newPicPathUrl, type, Long.parseLong(s));
                                    }
                                }*/
                            }else {
                                /*if ("1".equals(isSend)){
                                    field_status=1;
                                }*/
                                XposedBridge.log("msgSvrId:"+localContentValues.getAsString("msgSvrId")+"---msgId:"+String.valueOf(localContentValues.get("msgId")+"paramAnonymousMethodHookParam1"+paramAnonymousMethodHookParam1));
                                String picPath= DownLoadWxResFromWxUtil.downloadWxPicRes(classLoader, paramWechatEntity, localContentValues.getAsString("msgSvrId"), String.valueOf(localContentValues.get("msgId")), paramAnonymousMethodHookParam1);
                                XposedBridge.log("ssssssssssssssss"+picPath);
                                String imageLast = picPath.substring(picPath.lastIndexOf("/")+1);
                                XposedBridge.log("dddddddddddddd"+imageLast);
                                String  imageLast2=imageLast.substring(3,imageLast.length());
                                XposedBridge.log("dddddddddddddd"+imageLast2);
                                String newPicPath=picPath.substring(0,picPath.length()-imageLast.length())+imageLast2+".jpg";
                                XposedBridge.log("dddddddddddddd"+newPicPath);
                                File file1=new File(newPicPath);
                                boolean picBoole=file1.exists();
                                XposedBridge.log("ffffffffffffffff222"+picBoole);


                                newImaginPath=newPicPath;
                                newImaginNull=null;

                                if ("1".equals(isSend)){
                                    picstatus="1";
                                }else{
                                    picstatus="3";
                                }
                                //status = localContentValues.getAsString("status");
                                XposedBridge.log("sssssssssssssss"+status);

                              /*  if ("1".equals(isSend)){
                                    field_status=1;
                                    File file=new File(newImaginPath);
                                    if (file.exists()){
                                        XposedBridge.log("上传自己发送的图片文件");
                                        XposedBridge.log("$$$$$$$$$同步接发送的图片成功");
                                        String newPicPathUrl= uploadPic(newImaginPath,talker,"Send");
                                        XposedBridge.log("$$$$$$$$$"+newPicPathUrl);
                                        handleMessage(0, field_status, talker, newPicPathUrl, type, Long.parseLong(s));
                                    }


                                }else {
                                    File file=new File(newImaginPath);
                                    if (file.exists()) {
                                        String newPicPathUrl = uploadPic(newImaginPath, talker, "Receive");
                                        XposedBridge.log("$$$$$$$$$" + newPicPathUrl);
                                        handleMessage(0, field_status, talker, newPicPathUrl, type, Long.parseLong(s));
                                    }
                                }*/

                            }

                        }catch (Exception e){
                            XposedBridge.log(e.toString()+"图片代码引发的错误");
                        }
                    }else if ("49".equals(type)){
                                XposedBridge.log("kkkkkkkkkkkkk"+(Long) localContentValues.get("msgId"));
                                FileEntity fileEntity= DownLoadWxResFromWxUtil.downloadWxFileRes(classLoader, paramWechatEntity, (Long) localContentValues.get("msgId"));
                                String filePath="/storage/emulated/0/tencent/MicroMsg/Download/"+fileEntity.getTitle();
                                XposedBridge.log(filePath);
                                File file=new File(filePath);
                                boolean b=file.exists();
                                XposedBridge.log(b+"343757500");

                                newFilePath=filePath;
                                newFileNull=null;

                                sendFilePath=filePath;
                                sendFileNull=null;


                        if ("1".equals(isSend)){
                            filestatus="1";
                           // XposedBridge.log("上传自己发送的File文件");
                           // String newFilePathUrl= uploadFile(sendFilePath,talker,"Send",0, 1, talker, type, Long.parseLong(s));
                        }else{
                            filestatus="3";
                        }

                    }else if ("10000".equals(type)){//领取红包信息
                        String content=localContentValues.getAsString("content");


                         momyType=type;
                         momyContent=content;
                         momyTypeNull=null;

                    }else if ("419430449".equals(type)){//收到转账信息
                        XposedBridge.log("CCCCCCCCCCCCCCCCCCCCCCC"+"收到转账"+localContentValues);
                        String content=localContentValues.getAsString("content");
                        if ("0".equals(isSend)){  //收到转账
                            accountsType=type;
                            accountsIsSend=isSend;
                            accountsContent=content;
                            accountsNull=null;
                        }else if ("1".equals(isSend)){ //点击收转账
                            accountsType=type;
                            accountsIsSend=isSend;
                            accountsContent=content;
                            accountsNull=null;
                        }

                    }else if ("2000".equals(type)){  //领取转账信息
                        XposedBridge.log("DDDDDDDDDDDDDDDDDDDDDDDDD"+"接收转账"+localContentValues);
                    }else if("436207665".equals(type)){
                        String content=localContentValues.getAsString("content");


                        momyType=type;
                        momyContent=content;
                        momyTypeNull=null;
                        if ("1".equals(isSend)){
                            monystatus="1";
                        }else{
                            monystatus="3";
                        }
                    }


                    return;
                }
            }

            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
            {
                try
                {
                    if (((String)paramAnonymousMethodHookParam.args[0]).equals("message"))
                    {
                        localContentValues = (ContentValues)paramAnonymousMethodHookParam.args[2];
                        XposedBridge.log("111111111111111111111111"+ localContentValues.toString());

                        String type=localContentValues.getAsString("type");
                        XposedBridge.log("type:"+type);
                        long field_conversationTime =Long.parseLong(localContentValues.getAsString("createTime"));
                        XposedBridge.log("field_conversationTime:"+field_conversationTime);
                        String content =localContentValues.getAsString("content");
                        XposedBridge.log("content:"+content);
                        String talker =localContentValues.getAsString("talker");
                        XposedBridge.log("talker:"+talker);
                        int statuss =Integer.parseInt(localContentValues.getAsString("status"));
                        String isSend =localContentValues.getAsString("isSend");
                        XposedBridge.log("statuss:"+statuss);
                        if ("1".equals(type)){

                            if ("1".equals(isSend)){
                                statuss=1;
                            }
                            handleMessage(0, statuss, talker, content, type, field_conversationTime);
                        }




                        XposedBridge.log("111111111111111111111111"+ localContentValues.toString());
                        String paramAnonymousMethodHookParam1 = localContentValues.getAsString("type");
                        long l1 = localContentValues.getAsLong("createTime").longValue();
                        long l2 = System.currentTimeMillis() - 300000L;
                        XposedBridge.log("===== hook message %s %s"+ new Object[] { Long.valueOf(l1), Long.valueOf(l2) });
                        XposedBridge.log("222222222222222222222222"+ Long.valueOf(l1)+"+"+ Long.valueOf(l2));
                        if (l1 < l2)
                        {
                            XposedBridge.log("=====forget[%s]"+ new Object[] { localContentValues.toString() });
                            XposedBridge.log("3333333333333333333"+  localContentValues.toString() );
                            return;
                        }
                        XposedBridge.log("=====handle[%s]"+ new Object[] { localContentValues.toString() });
                        XposedBridge.log("44444444444444444444444"+  localContentValues.toString() );
                      //  status = localContentValues.getAsString("status");
                        XposedBridge.log("5555555555555555555555"+ status);

                        if ((AuthUtil.isForbiddenAuth(8)) && ("1".equals(paramAnonymousMethodHookParam1)))
                        {
                            paramAnonymousMethodHookParam1 = localContentValues.getAsString("content");
                            Matcher localMatcher = Pattern.compile("(\\d{7,14})").matcher(paramAnonymousMethodHookParam1);
                            while (localMatcher.find())
                            {
                                String str = localMatcher.group(1);
                                paramAnonymousMethodHookParam1 = paramAnonymousMethodHookParam1.replace(str, "[��������{" + Base64.encodeToString(str.getBytes(), 0) + "}]");
                            }
                            localContentValues.put("content", paramAnonymousMethodHookParam1);
                            return;
                        }
                    }
                }
                catch (Exception e)
                {
                    XposedBridge.log("ee:"+e.toString());
                }
            }
        } });
    }

    public void handleMessage(int unreadCount, int status, String username, String content, String msgType, long conversationTime) {
        list_msg.add(new HookMessageBean(status, username, content, msgType, conversationTime));
        XposedBridge.log("111"+ "unReadCount:" + unreadCount + " status:" + status + " username:" + username + " msgType:" + msgType + ",content=" + content + " conversationTime:" + conversationTime);
        getToken();
        /*if (msgType.equals("34") && voiceList.size() != 0) {
            getVoiceMsg(status, content);
        }*/
        if (msgType.equals("3") && status == 3 && imageRcvList.size() != 0) {
            LogUtils.w(TAG, "收到的照片:" + imageRcvList.get(imageRcvList.size() - 1));
            XposedBridge.log("1111111111111111111111111111111111111111111111111111111111"+"收到的照片:" + imageRcvList.get(imageRcvList.size() - 1)+"____");
        }
        saveMessage(status, msgType, username, content, conversationTime, 0);
       /* if (RegularUtils.SENSITIVE_WORD != null && status == 2) {
//                                    LogUtils.i(TAG,"敏感词过滤:"+RegularUtils.SENSITIVE_WORD);
            boolean hasSensitive = RegularUtils.isMatchRegular(content, RegularUtils.SENSITIVE_WORD);
            if (hasSensitive) {
                LogUtils.w(TAG, "含有敏感词:" + content);
                sendNotice("信息含有敏感词,请留意");
            }
        }*/
    }

    private void getVoiceMsg(int status, String content) {
        if (status == 3) {
            getLastVoiceMessageFile();
        } else {
            String[] contents = content.split(":");
//            LogUtils.w(TAG,"***"+contents[0]+" *** "+contents[1]+" *** "+contents[2]);
            long pressTime = Long.parseLong(contents[1]);
            getMyVoiceMessageFile(System.currentTimeMillis() - pressTime);
        }
    }

    private void getLastVoiceMessageFile() {
        String path = MyFileUtil.getLastModifiedFile(voiceList);
        findAmr(path);
    }

    public String findAmr(String path) {
        File subFolder = new File(path);//获取最新的目录,可能不是次级路径
        int last = path.lastIndexOf("/voice2/");
        if (path.length() - last <= 11) { //未找到次级(放amr)的目录了,如:/voice2/c0
            File file = new File(path);
            String folder = MyFileUtil.getLastModifiedFile(file);
            if (folder != null) {
                subFolder = new File(folder);
            } else {
                //该目录下依然没有次级目录.(改动过但依然没有次级目录)
                LogUtils.e(TAG, path + "目录下依然没有次级目录.(改动过但依然没有次级目录)");
                return null;
            }
        }
        String amrPath = MyFileUtil.getLastModifiedFile(subFolder, "amr");
        if (amrPath != null) {
            mFileObserver.resetVoiceList();
            amrList.add(amrPath);
            LogUtils.v(TAG, "找到语音:" + amrPath + "  modified:" + new File(amrPath).lastModified());
        } else {
            LogUtils.e(TAG, "在" + subFolder + "找不到语音");
            return null;
        }
        return amrPath;
    }

    private void getMyVoiceMessageFile(long time) {
        LogUtils.i(TAG, "寻找" + time + "附近的amr");
        List<String> spFileList = MyFileUtil.getFileListOnSpecifiedTime(voiceList, time, 2000l, 1000l);//找出特定时间内操作过的路径
        LogUtils.i(TAG, "spFileList size:" + spFileList.size());
        if (spFileList.size() == 1) {//就只有一个的话那就是他了
            findAmr(spFileList.get(0));
        } else {//需要靠amrList过滤
            for (int i = spFileList.size() - 1; i > 0; i--) {
                boolean gotAmr = false;
                for (int j = 0; j < amrList.size(); j++) {
                    if (amrList.get(j).startsWith(spFileList.get(i))) {
                        gotAmr = true;
                        continue;
                    }
                }
                if (gotAmr) {
                    spFileList.remove(i);
                }
            }
            //可能还有多个目录,但应该只有一个目录有amr了
            LogUtils.i(TAG, "可能还有多个目录,但应该只有一个目录有amr");
            int count = 0;
            for (int i = 0; i < spFileList.size(); i++) {
                String found = findAmr(spFileList.get(i));
                if (found != null) {
                    count++;
                }
            }
            if (count == 1) {
                LogUtils.w(TAG, "找到唯一的语音了");
                amrList = new ArrayList<>();
                voiceList = new ArrayList<>();
            } else {
                //这种情况下拿第一个语音
                LogUtils.w(TAG, "有" + count + "个目录有语音");
            }
        }
    }

    private void sendNotice(String notice) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.hsl.helper", "com.hsl.helper.service.SensitiveService"));
        intent.putExtra("notice", notice);
        mContext.startService(intent);
    }

    private void getToken() {
        if (token == null) {
            token = MyFileUtil.readFromFile(AppConfig.APP_FOLDER + "/token");
            token = token.substring(1, token.length() - 1);
//            LogUtils.w(TAG, "token:" + token);
        }
    }

    /**
     * 有新的好友,通知后台
     *
     * @param wxToken
     * @param friend
     */
    private void addNewFriend(String wxToken, FriendBean friend) {
        Gson gson = new Gson();
        String friendStr = gson.toJson(friend);
        LogUtils.v(TAG, "有新好友:" + friendStr);
        if (AppConfig.getSelectHost() == null) {
            AppConfig.setHost(AppConfig.OUT_NETWORK);
        }
        OkGo.post(AppConfig.getSelectHost() + NetApi.addFriend + "/" + wxToken).headers("Content-Type", "application/json").upJson(friendStr).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                LogUtils.i(TAG, "result:" + s);
                try {
                    HttpBean bean = new Gson().fromJson(s, HttpBean.class);
                    if (bean.isSuccess()) {
                        sendNotice("添加好友成功");
                    } else {
                        sendNotice(bean.getCode() + " " + bean.getMsg());
                    }
                } catch (Exception e) {
                    sendNotice(e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                sendNotice(e.getMessage());
            }
        });
    }


    /**
     * 保存信息到后台
     * @param status
     * @param msgType
     * @param username
     * @param content
     * @param conversationTime
     * @param index
     */
    private void saveMessage(final int status, final String msgType, final String username, final String content, final long conversationTime, final int index) {
        if (token == null) return;
        if ((content.contains("现在可以开始聊天了") || content.contains("accepted your friend request")) && msgType.equals("10000") && status == 4) {
            //刚刚把你添加到通讯录，现在可以开始聊天了
            //你已添加了*******，现在可以开始聊天了
            String nickname = "";
            if (content.contains("刚刚把你添加到通讯录，现在可以开始聊天了")) {
                nickname = content.substring(0, content.length() - 21);
            } else if (content.contains("你已添加了") && content.contains("，现在可以开始聊天了")) {
                nickname = content.substring(0, content.length() - 11);
                nickname = nickname.substring(5, nickname.length());
            } else {
                nickname = content;
            }


            UserEntity userEntity = WechatDb.getInstance().selectSelf();
            String userName = userEntity.getUserName();
            String userTalker = userEntity.getUserTalker();
            String headPic = userEntity.getHeadPic();
            String alias = userEntity.getAlias();  //微信号
            FriendBean friendBean = new FriendBean();
            friendBean.setNickname(nickname);
            friendBean.setWxid(username);
            addNewFriend(alias, friendBean); //新的好友,通知后台
        }
        Gson gson = new Gson();
        XposedBridge.log(token+"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        HookMessageBean msg = new HookMessageBean(token, status, username, content, msgType, conversationTime);
        String msgStr = gson.toJson(msg);
        if (AppConfig.getSelectHost() == null) {
            AppConfig.setHost(AppConfig.OUT_NETWORK);
        }
        Log.e("111","实时发送信息---ip"+AppConfig.getSelectHost());
        XposedBridge.log("实时发送信息---ip"+AppConfig.getSelectHost());
        //实时发送信息
        OkGo.post(AppConfig.getSelectHost() + NetApi.sendMessage).headers("Content-Type", "application/json").upJson(msgStr).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                LogUtils.i(TAG, "result:" + s);
                XposedBridge.log("result:" + s);
                try {
                    HttpBean bean = new Gson().fromJson(s, HttpBean.class);
                    if (bean.isSuccess()) {
                        LogUtils.i(TAG, "保存信息成功:" + conversationTime);
                        XposedBridge.log("保存信息成功:" + conversationTime);
                        if (errorStartIndex != -1) {//有失败的信息,但现在终于成功了
                            errorEndIndex = index;
                           // handleFailedMessage(errorStartIndex, errorEndIndex);
                        }
                    } else {
                        LogUtils.w(TAG, "保存第" + index + "条信息失败:" + conversationTime);
                        XposedBridge.log("保存第" + index + "条信息失败:" + conversationTime);
                        if (errorStartIndex == -1) {//记录了哪一条开始失败的
                            errorStartIndex = index;
                        }


                        //保存失败时候发起广播
                        Intent in=new Intent();
                        in.setClassName(Constance.packageName_me,Constance.receiver_my);
                        in.setAction(Constance.action_hookmessagefail);
                        in.putExtra("status",status+"");
                        in.putExtra("username",username);
                        in.putExtra("content",content);
                        in.putExtra("msgType",msgType);
                        in.putExtra("conversationTime",conversationTime+"");


                        XposedBridge.log("保存第" + index + "条信息失败:" );
                    }
                } catch (Exception e) {
                    if (errorStartIndex == -1) {
                        errorStartIndex = index;
                    }
                    //保存失败时候发起广播
                    Intent in=new Intent();
                    in.setClassName(Constance.packageName_me,Constance.receiver_my);
                    in.setAction(Constance.action_hookmessagefail);
                    in.putExtra("status",status+"");
                    in.putExtra("username",username);
                    in.putExtra("content",content);
                    in.putExtra("msgType",msgType);
                    in.putExtra("conversationTime",conversationTime+"");

                    XposedBridge.log("保存第" + index + "条信息失败:" + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                if (errorStartIndex == -1) {
                    errorStartIndex = index;
                }
                XposedBridge.log("保存第" + index + "条信息失败:" + e.getMessage());
                LogUtils.e(TAG, "保存第" + index + "条信息失败:" + e.getMessage());
              //  list_msgFail.add(new HookMessageBean(status, username, content, msgType, conversationTime));


                //保存失败时候发起广播
                Intent in=new Intent();
                in.setClassName(Constance.packageName_me,Constance.receiver_my);
                in.setAction(Constance.action_hookmessagefail);
                in.putExtra("status",status+"");
                in.putExtra("username",username);
                in.putExtra("content",content);
                in.putExtra("msgType",msgType);
                in.putExtra("conversationTime",conversationTime+"");

                context.sendBroadcast(in);

//                ToastUtil.showLongToast("保存第"+index+"条信息失败:" + e.getMessage());
                //setAlarmToSyncMessage();
            }
        });
    }

    /**
     * 设置定时任务同步未保存的信息
     */
    private void setAlarmToSyncMessage() {
        if (!counting) {
            counting = true;
            Calendar today = Calendar.getInstance();
            Random rdn = new Random();
            int second = rdn.nextInt(10);
            String alarmTime = today.get(Calendar.YEAR) + "-" + (today.get(Calendar.MONTH) + 1) + "-" + today.get(Calendar.DAY_OF_MONTH) + " 23:58:0" + second;
            cdt = new CountDownTimer(Utils.getTimeDiff(alarmTime), 5000) {
                @Override
                public void onTick(long millisUntilFinished) {
//                    LogUtils.d(TAG,"未够钟! list size:"+list_msg.size()+" errorStartIndex:"+errorStartIndex+" 还有:"+(millisUntilFinished/1000));
                }

                @Override
                public void onFinish() {
//                    LogUtils.w(TAG,"够钟! list size:"+list_msg.size()+" errorStartIndex:"+errorStartIndex);
                    if (list_msg.size() != 0 && errorStartIndex != -1) {
                        handleFailedMessage(errorStartIndex, list_msg.size());
                    }
                    counting = false;
                }
            }.start();
        }
    }


    /**
     * 定时发送实时同步失败的信息
     *
     * @param startIndex
     * @param endIndex
     */
    private void handleFailedMessage(int startIndex, int endIndex) {
        Gson gson = new Gson();
        List<HookMessageBean> errorMsg = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            HookMessageBean bean = list_msg.get(i);
            bean.setToken(token);
            errorMsg.add(bean);
        }
        String msgListStr = gson.toJson(errorMsg);
        LogUtils.w(TAG, "msgListStr:" + msgListStr);
        if (AppConfig.getSelectHost() == null) {
            AppConfig.setHost(AppConfig.OUT_NETWORK);
        }
        OkGo.post(AppConfig.getSelectHost() + NetApi.syncMessage).headers("Content-Type", "application/json").upJson(msgListStr).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                LogUtils.i(TAG, "result:" + s);
                try {
                    HttpBean bean = new Gson().fromJson(s, HttpBean.class);
                    if (bean.isSuccess()) {
                        errorStartIndex = -1;
                        errorEndIndex = -1;
                        list_msg = new ArrayList<>();
                        LogUtils.e(TAG, "同步信息成功");
                        sendNotice("同步信息成功");
                    } else {
                        LogUtils.e(TAG, "同步信息失败:" + bean.getMsg());
                        sendNotice("同步信息失败:" + bean.getMsg());
                    }
                } catch (Exception e) {
                    LogUtils.e(TAG, "同步信息失败:" + e.getMessage());
                    sendNotice("同步信息失败:" + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                LogUtils.e(TAG, "同步信息失败:" + e.getMessage());
                sendNotice("同步信息失败:" + e.getMessage());
            }
        });
    }

    private boolean checkLogin() {
        if (!TextUtils.isEmpty(TIMManager.getInstance().getLoginUser())) {
            return true;
        }
        return false;
    }

    @Override
    public void addPathToList(int type, int option, String path) {
        LogUtils.e(TAG, option + " addPathToList:" + path);
        switch (option) {
            case 1:
                int last = path.lastIndexOf("/voice2/");
                if (path.length() - last > 11) { //已经找到次级(放amr)的目录了:/voice2/c0/b3
                    voiceList.add(path);
                } else {
                    File file = new File(path);
                    String folder = MyFileUtil.getLastModifiedFile(file);
                    if (folder != null) {
                        voiceList.add(folder);
                    } else {
                        voiceList.add(path);
                    }
                }
                break;
            case 2: //发出的照片

                break;
            case 3:

                break;
            case 4:  //发出的视频
                sedImagin=null;
                XposedBridge.log("……"+ path);

                boolean b = fileIsExists(path);
                if (b){
                    XposedBridge.log("存在"+path);
                    videoList.add(path) ;
                    sedVideoPath=path;
                    videoState=1;

                }else{
                    XposedBridge.log("不存在"+path);
                }

                break;
            case 5:  //收到的视频
                XposedBridge.log("&&……"+ path);
                reyImagin=null;

                boolean b1 = fileIsExists(path);
                if (b1){
                    XposedBridge.log("存在"+path);
                    videoList.add(path) ;
                    reyVideoPath=path;
                    videoState=3;
                }else{
                    XposedBridge.log("不存在"+path);
                }

                break;
        }
    }




    /**
     * 检查扩展名，得到图片格式的文件
     * @param fName  文件名
     * @return
     */
    @SuppressLint("DefaultLocale")
    private boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg")|| FileEnd.equals("bmp") ) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }



    //判断文件是否存在
    public boolean fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }else{
                return true;
            }

        }
        catch (Exception e)
        {
            return false;
        }

    }



    public  FriendBean friendAdviceParse(String content) throws Exception {
        if(StringUtils.isBlank(content)){
            return null;
        }

        try{
            Pattern pattern = Pattern.compile("<msg[\\s\\S]*</msg>");
            Matcher matcher = pattern.matcher(content);

            if(matcher.find()){
                //匹配需要的内容
                content = matcher.group();
                //解释xml
                Document document = DocumentHelper.parseText(content);
                //根节点
                Element root = document.getRootElement();

                //昵称
                String nickname = root.attributeValue("fromnickname");
                //头像
                String bigheadimgurl = root.attributeValue("bigheadimgurl");
                String country = root.attributeValue("country");
                String province = root.attributeValue("province");
                String city = root.attributeValue("city");
                //微信wxid
                String wxid = root.attributeValue("fromusername");

                FriendBean friend = new FriendBean();
                friend.setNickname(nickname);
                friend.setWxid(wxid);
                friend.setHeadImgUrl(bigheadimgurl);
                friend.setCountry(country);
                friend.setProvince(province);
                friend.setCity(city);
                return friend;
            }

            return null;
        }catch(Exception e){
            throw new Exception(e.getMessage(), e);
        }
    }


    public void handleMessageDeleteFriend(String wxno,String wxid,String createtime){
        Gson gson = new Gson();
        DeleteFriendBean deleteFriendBean=new DeleteFriendBean();
        deleteFriendBean.setWxno(wxno);
        deleteFriendBean.setFriendWxid(wxid);
        deleteFriendBean.setConversationTime(createtime);

        String friendStr = gson.toJson(deleteFriendBean);

        OkGo.delete(AppConfig.OUT_NETWORK+ NetApi.deletefriend+ "?" + "wxno=" +wxno+ "&friendWxid=" + wxid+"&conversationTime="+createtime).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                XposedBridge.log("sssssss"+s);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                XposedBridge.log("sssssss"+e.toString());
            }
        });
    }


}

