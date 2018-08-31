package com.mikuwxc.autoreply.wcreceiver;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.mikuwxc.autoreply.bean.ImLoginBean;
import com.mikuwxc.autoreply.common.Config;
import com.mikuwxc.autoreply.common.MyApp;
import com.mikuwxc.autoreply.common.net.NetApi;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.common.util.Constants;
import com.mikuwxc.autoreply.common.util.EventBusUtil;
import com.mikuwxc.autoreply.common.util.LogUtils;
import com.mikuwxc.autoreply.common.util.Logger;
import com.mikuwxc.autoreply.common.util.SPHelper;
import com.mikuwxc.autoreply.common.util.SharedPrefsUtils;
import com.mikuwxc.autoreply.common.util.ToastUtil;
import com.mikuwxc.autoreply.modle.C;
import com.mikuwxc.autoreply.modle.Event;
import com.mikuwxc.autoreply.modle.FriendBean;
import com.mikuwxc.autoreply.modle.HttpImeiBean;
import com.mikuwxc.autoreply.modle.ImMessageBean;
import com.mikuwxc.autoreply.presenter.tasks.AsyncFriendTask;
import com.mikuwxc.autoreply.receiver.Constance;
import com.mikuwxc.autoreply.wcentity.CircleFriendEntity;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMLogListener;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUser;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import org.apache.commons.lang3.StringUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;

import static com.mikuwxc.autoreply.activity.RunningActivity.wxState;


public class MsgReceiver extends BroadcastReceiver {
    private static final String KEY_REPLY = "reply";
    private static final String KEY_LUCKY_MONEY = "lucky_money";
    private static final String KEY_SELECT_ID = "select_id";
    private static final String KEY_AUTO_REPLY_TEXT = "reply_text";
    private final List<String> mData = new ArrayList<>();
    private String sig;
    private String id;
    private String sdkAppId;


    private final String SDcardPath = "/storage/emulated/0/JCM/";
    ArrayList<FriendBean> beanArrayList=new ArrayList<>();
    FriendBean friendBean;



    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            sendMsg();
            handler.postDelayed(this, 20000);
        }
    };

    Handler handlerAlive=new Handler();
    Runnable runnableAlive=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            permissionAlive(wxno,context1);
            handlerAlive.postDelayed(this, 60000);
        }
    };



    private String[] search = {
            //  "input keyevent 3",// 返回到主界面，数值与按键的对应关系可查阅KeyEvent
            // "sleep 1",// 等待1秒
            // 打开微信的启动界面，am命令的用法可自行百度、Google// 等待3秒
            "am force-stop com.tencent.mm",
            "am start -a com.tencent.mm.action.BIZSHORTCUT -f 67108864"
            // "am  start  service  com.mikuwxc.autoreply.AutoReplyService"// 打开微信的搜索
            // 像搜索框中输入123，但是input不支持中文，蛋疼，而且这边没做输入法处理，默认会自动弹出输入法
    };
    private String wxno;
    private Context context1;


    @Override
    public void onReceive(Context context, Intent intent) {
     String action= intent.getAction();
        context1 = context;
     if(TextUtils.isEmpty(action))
     {
        return;
     }

     if(action.equals(Constance.action_getWechatFriends))
     {

       //  action_getWechatFriends(context,intent);
     }else if (action.equals(Constance.action_returnRoom)){
         String momyType = intent.getStringExtra("momyType");

         Toast.makeText(context, "是否开启自动抢红包"+momyType, Toast.LENGTH_LONG).show();
         action_returnRooms(context,momyType);
     }else if (action.equals(Constance.action_getcpWechatDb)){
         action_getWechatDB(context,intent);
     }
    }

    private void action_returnRooms(Context context,String momyType) {
        if ("true".equals(momyType)){
            //重连微信并且更改红包是否能自动获取
            SharedPreferences sp = context.getSharedPreferences("moneyStaus", Activity.MODE_WORLD_READABLE);
            SharedPreferences.Editor ditor = sp.edit();
            ditor.putBoolean("moneyStaus_put",true).commit();
            ToastUtil.showLongToast("开启微信自动抢红包权限");

            // 获取Runtime对象  获取root权限
            Runtime runtime = Runtime.getRuntime();
            try {
                Process process = runtime.exec("su");
            } catch (IOException e) {
                e.printStackTrace();
            }

            search[1] = chineseToUnicode(search[1]);
            execShell(search);

        }else{
            SharedPreferences sp = context.getSharedPreferences("moneyStaus", Activity.MODE_WORLD_READABLE);
            SharedPreferences.Editor ditor = sp.edit();
            ditor.putBoolean("moneyStaus_put",false).commit();
            ToastUtil.showLongToast("关闭微信自动抢红包权限");

            // 获取Runtime对象  获取root权限
            Runtime runtime = Runtime.getRuntime();
            try {
                Process process = runtime.exec("su");
            } catch (IOException e) {
                e.printStackTrace();
            }

            search[1] = chineseToUnicode(search[1]);
            execShell(search);
        }


    }


    //获取数据库的路径并复制去指定文件夹，和密码
    private void action_getWechatDB(Context context, Intent intent) {
        String dabase_Route=intent.getStringExtra(Constance.dabase_cpRoute);
        String dabase_Password=intent.getStringExtra(Constance.dabase_cpPassword);
        wxno = intent.getStringExtra("wxno");
        String wxid = intent.getStringExtra("wxid");
        if (StringUtils.isBlank(wxno)){
            wxno=wxid;
        }
        String headImgUrl = intent.getStringExtra("headImgUrl");
        String userName = intent.getStringExtra("userName");
        Toast.makeText(context,"连接中",Toast.LENGTH_LONG).show();

        String fileName = getFileName(dabase_Route);


        SQLiteDatabase.loadLibs(context);//引用SQLiteDatabase的方法之前必须先添加这句代码

        File file=new File(SDcardPath+"EnMicroMsglyNew"+dabase_Password+".db");
        if (file.exists()){
            file.delete();
        }

        //解密复制出来的微信数据库得到没密码的数据库方便操作
        String friendList=decrypt(fileName,"EnMicroMsglyNew"+dabase_Password+".db",dabase_Password,context);
        if (friendList!=null){
            sendWXFriendList(friendList,context, wxno,wxid,headImgUrl,userName);
        }

    }


    private void sendWXFriendList(String WXFriendList,Context context,String wxno,String wxid,String headImgUrl,String userName) {
         List<FriendBean> friendBean = new Gson().fromJson(WXFriendList, new TypeToken<List<FriendBean>>() {
        }.getType());

         //登录IM
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        String DEVICE_ID = tm.getDeviceId();
        Log.e("111",DEVICE_ID);
        volleyGet(context, wxno, wxid, headImgUrl,friendBean,userName,DEVICE_ID);


    }


    private void volleyGet(final Context context, final String wxno, final String wxid, String headImgUrl, final List<FriendBean> friendBean,String userName,String DEVICE_ID) {
        String url = AppConfig.OUT_NETWORK + NetApi.imLogin+"?wxno="+wxno+"&"+"headImgUrl="+headImgUrl+"&"+"wxid="+wxid+"&"+"nickname="+userName+"&"+"jpush="+DEVICE_ID;
        Log.e("111",url);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {//s为请求返回的字符串数据
                        ImLoginBean imLoginBean = new Gson().fromJson(s, ImLoginBean.class);
                        if(imLoginBean!=null&&imLoginBean.getCode().equals("200")&&imLoginBean.isSuccess()==true) {
                            sig = imLoginBean.getResult().getSig();
                            id = imLoginBean.getResult().getRelationId();
                            sdkAppId = imLoginBean.getResult().getSdkAppId();

                            initDataLogin(imLoginBean);
                            initTMConfig(context);
                            loginIM(context);
                            //登陆IM成功再同步好友
                            AsyncFriendTask.sendFriendList(wxno, friendBean, false);

                            SharedPreferences sp = context.getSharedPreferences("test", Activity.MODE_WORLD_READABLE);
                            SharedPreferences.Editor ditor = sp.edit();
                            ditor.putBoolean("test_put",true).commit();
                            ToastUtil.showLongToast("开启所有权限");

                           // handlerAlive.postDelayed(runnableAlive, 10000);//每两秒执行一次runnable.



                        }else {
                            //ToastUtil.showShortToast("登录IM此帐号不能授权");

                            SharedPreferences sp = context.getSharedPreferences("test", Activity.MODE_WORLD_READABLE);
                            SharedPreferences.Editor ditor = sp.edit();
                            ditor.putBoolean("test_put",false).commit();
                            ToastUtil.showLongToast("关闭所有权限");

                           //移除定时保活功能
                           // handlerAlive.removeCallbacks(runnableAlive);
                           // handlerAlive.removeMessages(0);
                            search[1] = chineseToUnicode(search[1]);
                            execShell(search);

                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(context,volleyError.toString(),Toast.LENGTH_LONG).show();
                    }
                });
        //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
        request.setTag("testGet");
        //将请求加入全局队列中
        MyApp.getHttpQueues().add(request);
    }




    private void initDataLogin(ImLoginBean imLoginBean) {
        Constants.token = imLoginBean.getResult().getRelationId();
        LogUtils.e("111", "登录成功");
        if (!TextUtils.isEmpty(Constants.token)) {
            //头尾添加1位随机数作加密
            Random rand = new Random();
            int start = rand.nextInt(10);
            int end = rand.nextInt(10);
            try {
                File tokenFile = new File(AppConfig.APP_FOLDER, "/token");
                tokenFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(tokenFile);
                OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
                osw.write(start + Constants.token + end);
                osw.flush();
                fos.flush();
                osw.close();
                fos.close();
                LogUtils.e("111", "保存token:" + Constants.token);
            } catch (Exception e) {
                LogUtils.e("111", "保存token出错");
                e.printStackTrace();
            }
            ToastUtil.showLongToast("登录成功");
        } else {
            ToastUtil.showLongToast("token为空");
        }
    }


    /**
     * 初始化腾讯云IM 配置
     */
    private void initTMConfig(Context context) {
        //初始化SDK基本配置
        TIMSdkConfig config = new TIMSdkConfig(Integer.parseInt(sdkAppId))
                .enableCrashReport(false)
                .enableLogPrint(true)
                .setLogLevel(TIMLogLevel.DEBUG)
                .setLogPath(Environment.getExternalStorageDirectory().getParent() + "/justfortest/");
        //初始化SDK
        TIMManager.getInstance().init(context, config);
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
     * 登录腾讯云 IM
     */
    private void loginIM(final Context context) {
        TIMUser user = new TIMUser();
        user.setIdentifier(AppConfig.getIdentifier());
        //发起登录请求
        TIMManager.getInstance().login(
                id,//sdkAppId，由腾讯分配
                sig,//用户帐号签名，由私钥加密获得，具体请参考文档
                new TIMCallBack() {//回调接口

                    @Override
                    public void onSuccess() {//登录成功
                        ToastUtil.showLongToast("腾讯IM登录成功" + AppConfig.getIdentifier());
                        if (wxState!=null) {
                            wxState.setText("微信连接状态：true");
                        }
                        NewMessageListener(context);
                        handler.postDelayed(runnable, 20000);//每两秒执行一次runnable.
                        sendMsg();

                    }

                    @Override
                    public void onError(int code, String desc) {//登录失败
                        if (wxState!=null) {
                        wxState.setText("微信连接状态：true");
                        }
                        //错误码code和错误描述desc，可用于定位请求失败原因
                        //错误码code含义请参见错误码表
                        ToastUtil.showLongToast("腾讯IM登录失败");
                        Log.e("111",code+"_____"+desc);
                    }
                });

    }



    private void NewMessageListener(final Context context) {
        //设置消息监听器，收到新消息时，通过此监听器回调
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {//消息监听器
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {//收到新消息

                TIMMessage msg = list.get(0);
                for (int i = 0; i < msg.getElementCount(); ++i) {
                    TIMElem elem = msg.getElement(i);

                    //获取当前元素的类型
                    TIMElemType elemType = elem.getType();
                    Log.d("111", "elem type: " + elemType.name());
                    if (elemType == TIMElemType.Text) {
                        //处理文本消息
                        TIMUserProfile sendUser = msg.getSenderProfile();
                        String sender = msg.getSender();
                        final TIMTextElem textElem = (TIMTextElem) elem;
                        if (!msg.isSelf()) {
                            if (!TextUtils.isEmpty(textElem.getText().replaceAll("&quot;", "\""))) {
                                Logger.d("收到消息了。。。。。" + textElem.getText().replaceAll("&quot;", "\""));

                                if (textElem.getText().contains("wxno")) {
                                    try {
                                        ImMessageBean messageBean = new Gson().fromJson(textElem.getText().replaceAll("&quot;", "\""), ImMessageBean.class);

                                        //ToastUtil.showLongToast("消息发送人: " + messageBean.getWxid() + "  消息内容: " + messageBean.getContent());
                                        EventBusUtil.sendEvent(new Event(C.EventCode.A, messageBean));


                                        Intent intent=new Intent();
                                        String type = messageBean.getType();
                                        if (type.equals("200")){
                                            CircleFriendEntity circleFriendEntity = new Gson().fromJson(messageBean.getContent(), CircleFriendEntity.class);
                                            String circleType = circleFriendEntity.getType();
                                            Toast.makeText(context,circleType+"343757500",Toast.LENGTH_LONG).show();
                                            if ("0".equals(circleType)){  //图文
                                                String circleText = circleFriendEntity.getContent();
                                                String fodderUrl = circleFriendEntity.getFodderUrl();
                                                intent.putExtra("name",messageBean.getWxid());
                                                intent.putExtra("content",messageBean.getContent());
                                                intent.putExtra("type",type);
                                                intent.putExtra("circleText",circleText);
                                                intent.putExtra("fodderUrl",fodderUrl);
                                                intent.putExtra("circleType",circleType);
                                            }else if("1".equals(circleType)){ //视频

                                            }else if ("2".equals(circleType)){//文本
                                                String circleText = circleFriendEntity.getContent();
                                                String fodderUrl = circleFriendEntity.getFodderUrl();
                                                intent.putExtra("name",messageBean.getWxid());
                                                intent.putExtra("content",messageBean.getContent());
                                                intent.putExtra("type",type);
                                                intent.putExtra("circleText",circleText);
                                                intent.putExtra("fodderUrl",fodderUrl);
                                                intent.putExtra("circleType",circleType);
                                            }else if("3".equals(circleType)){ //链接

                                            }
                                            intent.setAction(Constance.action_getWechatFriends);
                                            intent.setClassName(Constance.packageName_wechat,Constance.receiver_wechat);
                                            context.sendBroadcast(intent);
                                          //  Toast.makeText(context,"发送广播朋友圈:"+Constance.action_getWechatFriends,Toast.LENGTH_LONG).show();
                                        }else{
                                            intent.putExtra("name",messageBean.getWxid());
                                            intent.putExtra("content",messageBean.getContent());
                                            intent.putExtra("type",type);
                                            intent.setAction(Constance.action_getWechatFriends);
                                            intent.setClassName(Constance.packageName_wechat,Constance.receiver_wechat);
                                            context.sendBroadcast(intent);
                                          //  Toast.makeText(context,"发送广播聊天:"+Constance.action_getWechatFriends,Toast.LENGTH_LONG).show();
                                        }


                                    }catch (Exception e){
                                        ToastUtil.showLongToast("IM发过来的数据格式有错"+e.toString());
                                    }



                                   // openWeiXinApp(context);
                                }else{
                                    ToastUtil.showLongToast("IM发过来的数据格式有错");
                                }
                            }
                        }
                    } else if (elemType == TIMElemType.Image) {
                        //处理图片消息
                    }//...处理更多消息
                }

                //消息的内容解析请参考消息收发文档中的消息解析说明
                return true;//返回true将终止回调链，不再调用下一个新消息监听器
            }
        });
    }

    /**
     * 打开微信
     */
    private void openWeiXinApp(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            ToastUtil.showLongToast("检查到您手机没有安装微信，请安装后使用该功能");
        }

    }

    private void sendMsg() {
        //获取单聊会话
//        String peer = "0911d2b559d04fb5b011dc64a6a25235";  //获取与用户 "sample_user_1" 的会话
        String peer = "9d1b86f5fdaf459b9dc773d8ef4663f1";  //获取与用户 "sample_user_1" 的会话   //621c62f470e94160a4f9417fe82966b2
        TIMConversation conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C,    //会话类型：单聊
                peer);                      //会话对方用户帐号//对方id

        //构造一条消息
        TIMMessage msg = new TIMMessage();

        //添加文本内容
        TIMTextElem elem = new TIMTextElem();
        elem.setText("大雄 发的第2条消息");

        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            Log.d("111", "addElement failed");
            return;
        }

        //发送消息
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                Log.e("111", "send message failed. code: " + code + " errmsg: " + desc);
             //   ToastUtil.showShortToast("发送消息失败"+code+"--"+desc);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
              //  ToastUtil.showShortToast("发送消息成功");
            }
        });
    }



    //截取文件名
    public String getFileName(String pathandname){

        int start=pathandname.lastIndexOf("/");
        if(start!=-1){
            return pathandname.substring(start+1,pathandname.length());
        }else{
            return null;
        }

    }



    /**
     * 解密数据库
     * @param encryptedName 要解密的数据库名称
     * @param decryptedName 解密后的数据库名称
     * @param key 密码
     */
    private String decrypt(String encryptedName,String decryptedName,String key,Context context) {
        try {
            File databaseFile = context.getDatabasePath(SDcardPath + encryptedName);



            SQLiteDatabaseHook hook = new SQLiteDatabaseHook(){
                public void preKey(SQLiteDatabase database){
                }
                public void postKey(SQLiteDatabase database){
                    database.rawExecSQL("PRAGMA cipher_migrate;");  //最关键的一句！！！  因为微信的版本较低，不加会兼容不了微信数据库
                }
            };

            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, key, null,hook);
            Cursor c = database.query("rcontact", null, null, null, null, null, null);
            while (c.moveToNext()) {
                String nickname = c.getString(c.getColumnIndex("nickname"));
                String alias = c.getString(c.getColumnIndex("alias"));
                String username = c.getString(c.getColumnIndex("username"));
                String conRemark = c.getString(c.getColumnIndex("conRemark"));

                friendBean=new FriendBean();
                Cursor s = database.query("img_flag", new String[]{"reserved1"}, "username=?",  new String[]{username}, null, null, null);
                while (s.moveToNext()) {
                    if (s!=null&&s.getCount()>0) {
                        String HeadImgUrl = s.getString(s.getColumnIndex("reserved1"));
                        friendBean.setHeadImgUrl(HeadImgUrl);
                    }
                }


                friendBean.setNickname(nickname);
                friendBean.setWxid(username);
                friendBean.setRemarkname(conRemark);
                //friendBean.setWxno(alias);
                if (StringUtils.isBlank(alias)){
                    friendBean.setWxno(username);
                }else{
                    friendBean.setWxno(alias);
                }

                beanArrayList.add(friendBean);

            }
            c.close();

            String s = JSON.toJSONString(beanArrayList);
            Log.e("111",s);

            File decrypteddatabaseFile = context.getDatabasePath(SDcardPath + decryptedName);

            //deleteDatabase(SDcardPath + decryptedName);

            //连接到解密后的数据库，并设置密码为空
            database.rawExecSQL(String.format("ATTACH DATABASE '%s' as "+ decryptedName.split("\\.")[0] +" KEY '';", decrypteddatabaseFile.getAbsolutePath()));
            database.rawExecSQL("SELECT sqlcipher_export('"+ decryptedName.split("\\.")[0] +"');");
            database.rawExecSQL("DETACH DATABASE "+ decryptedName.split("\\.")[0] +";");
            SQLiteDatabase decrypteddatabase = SQLiteDatabase.openOrCreateDatabase(decrypteddatabaseFile, "", null);
            //decrypteddatabase.setVersion(database.getVersion());
            decrypteddatabase.close();
            database.close();
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }




    }

    public String chineseToUnicode(String str){
        String result="";
        for (int i = 0; i < str.length(); i++){
            int chr1 = (char) str.charAt(i);
            if(chr1>=19968&&chr1<=171941){//汉字范围 \u4e00-\u9fa5 (中文)
                result+="\\u" + Integer.toHexString(chr1);
            }else{
                result+=str.charAt(i);
            }
        }
        return result;
    }


    /**
     30      * 执行Shell命令
     31      *
     32      * @param commands
     33      *            要执行的命令数组
     34      */
    public void execShell(String[] commands) {
        // 获取Runtime对象
        Runtime runtime = Runtime.getRuntime();

        DataOutputStream os = null;
        try {
            // 获取root权限
            Process process = runtime.exec("su");
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                os.write(command.getBytes());
                os.writeBytes("\n");
                os.flush();
            }
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //是否保活权限
    private void permissionAlive(String wxno, final Context context) {
        OkGo.post(AppConfig.OUT_NETWORK + NetApi.loginAlive + "?wxno=" + wxno).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, okhttp3.Response response) {
                Log.e("111", "result:" + s);

            }

            @Override
            public void onError(Call call, okhttp3.Response response, Exception e) {
                super.onError(call, response, e);
                Log.e("111", "保活信息失败:");
            }
        });
    }

}