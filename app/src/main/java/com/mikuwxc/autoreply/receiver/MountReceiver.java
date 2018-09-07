package com.mikuwxc.autoreply.receiver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.mikuwxc.autoreply.Tools;
import com.mikuwxc.autoreply.modle.FriendBean;
import com.mikuwxc.autoreply.wcapi.WechatEntityFactory;
import com.mikuwxc.autoreply.wcentity.UserEntity;
import com.mikuwxc.autoreply.wcentity.WechatEntity;
import com.mikuwxc.autoreply.wcutil.FriendUtil;
import com.mikuwxc.autoreply.wcutil.MomentUtil;
import com.mikuwxc.autoreply.wcutil.SendMesUtil;
import com.mikuwxc.autoreply.wx.WechatDb;
import com.mikuwxc.autoreply.xposed.CommonHook;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class MountReceiver extends XC_MethodHook {

    private Activity activity;
    private String JcmDbPath="/storage/emulated/0/JCM/";

    private String substring;

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Context context=(Context) param.args[0];
        Intent intent=(Intent) param.args[1];
        String action=intent.getAction();
        if(TextUtils.isEmpty(action))
        {
            return;
        }

        if(action.equals(Constance.action_getWechatFriends))
        {
            action_getWechatFriends(context,intent);
        }else if (action.equals(Constance.action_getWechatDb)){
            action_getWechatDb(context,intent);
        }else if (action.equals(Constance.action_toast)){
            action_toast(context,intent);
        }
    }


    public void action_toast(Context context,Intent intent){
        String str_toast = intent.getStringExtra(Constance.str_toast);
        Toast.makeText(context,str_toast,Toast.LENGTH_SHORT).show();
    }



    public void action_getWechatDb(Context context, Intent intent){
        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                activity = (Activity) param.thisObject;
                int versionCode= activity.getPackageManager().getPackageInfo(activity.getPackageName(),0).versionCode;
                XposedBridge.log("weixin123 versionCode="+versionCode);
            }
        });

        Toast.makeText(context,"正在连接微信", Toast.LENGTH_LONG).show();
        ClassLoader classLoader=context.getClassLoader();

        String dabase_Route = intent.getStringExtra(Constance.dabase_Route);
        String dabase_Password = intent.getStringExtra(Constance.dabase_Password);

        //判断路径是否存在，不存在则创建
        File JcmDb=new File(JcmDbPath);
        if (!JcmDb.exists()){
            JcmDb.mkdir();
        }

        String copyRoute=JcmDbPath+"EnMicroMsg"+dabase_Password+".db";

        File file=new File(copyRoute);
        boolean exists = file.exists();
        if (exists){
            file.delete();
        }

        boolean b=copyFile(dabase_Route,copyRoute);
        XposedBridge.log("343433"+b);

        Intent in=new Intent();
        in.setClassName(Constance.packageName_me,Constance.receiver_my);
        in.setAction(Constance.action_getcpWechatDb);
        in.putExtra(Constance.dabase_cpRoute,copyRoute);
        in.putExtra(Constance.dabase_cpPassword,dabase_Password);

        UserEntity userEntity = WechatDb.getInstance().selectSelf();
        String userName = userEntity.getUserName();
        String userTalker = userEntity.getUserTalker();
        String headPic = userEntity.getHeadPic();
        String alias = userEntity.getAlias();  //微信号
        XposedBridge.log(alias+userName+userTalker+headPic);     //获取数据库里面的历史数据
        in.putExtra("wxno",alias);
        in.putExtra("wxid",userTalker);
        in.putExtra("headImgUrl",headPic);
        in.putExtra("userName",userName);

        context.sendBroadcast(in);


    }




    public void action_getWechatFriends(final Context context, Intent intent)
    {


       // Toast.makeText(context,"微信收到广播:"+Constance.action_getWechatFriends,Toast.LENGTH_LONG).show();
        final ClassLoader classLoader=context.getClassLoader();
        final WechatEntity create = WechatEntityFactory.create(CommonHook.wechatVersionName);
        String name = intent.getStringExtra("name");
        String content = intent.getStringExtra("content");
        final String type = intent.getStringExtra("type");
        final String circleText = intent.getStringExtra("circleText");
        final String fodderUrl = intent.getStringExtra("fodderUrl");
        String circleType = intent.getStringExtra("circleType");
        String addWxid = intent.getStringExtra("addWxid");
        String addMsg = intent.getStringExtra("addMsg");
        String addType = intent.getStringExtra("addType");


        XposedBridge.log("circleText:"+circleText+"fodderUrl"+fodderUrl+"circleType"+circleType);
        XposedBridge.log(name+content+type+"--"+circleText+fodderUrl+circleType);
        if (name!=null) {
            try {
                 final String path = Environment.getExternalStorageDirectory().toString() + "/shidoe/";

                if (type.equals("1")) {

                    SendMesUtil.sendTxt(classLoader, create, name, content, 1, 1);

                }else if (type.equals("3")){
                    downLoad(content, "",classLoader,create,name,path,type,context);

                }else if (type.equals("34")){   //发送语音
                    downLoad(content, "",classLoader,create,name,path,type,context);

                }else if (type.equals("43")){  //发送视频
                    downLoad(content, "",classLoader,create,name,path,type,context);
                }else if(type.equals("49")){   //文章 文件
                    downLoad(content, "",classLoader,create,name,path,type,context);
                }else if (type.equals("200")){
                    //发朋友圈
                    if ("0".equals(circleType)){
                        XposedBridge.log("path:"+path);
                        XposedBridge.log("fodderUrl:"+fodderUrl);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<String> listMonentPic = downLoadPicMonet(fodderUrl, circleText, classLoader, create, "", path, type, context);
                                XposedBridge.log("listMonentPic+"+listMonentPic.toString());
                                JSONArray array= JSONArray.parseArray(JSON.toJSONString(listMonentPic));
                                MomentUtil.sendPicMoment9(classLoader,create,circleText,0,null,array);
                            }
                        }).start();

                    }else if ("2".equals(circleType)){
                        MomentUtil.sendTxtMoment(classLoader,create,circleText,0,null);
                    }

                }else if (type.equals("119")){
                    Intent in=new Intent();
                    in.setClassName(Constance.packageName_me,Constance.receiver_my);
                    in.setAction(Constance.action_returnRoom);
                    in.putExtra("momyType",content);
                    context.sendBroadcast(in);

                }else if (type.equals("201")){   //201代表加好友
                    if ("1".equals(addType)){   //1代表微信号加好友
                        XposedBridge.log("addWxid"+addWxid+"addMsg"+addMsg);
                        FriendUtil.searchFriend(classLoader,create,0,"",addWxid,"",15);
          /*              FriendUtil.addFriendWithUpdateRemark(classLoader, create, "", "测试电话", "", 15);
                        FriendUtil.addFriend12(classLoader,create,addWxid,addMsg,15);   //15 是通过手机号途径加好友  content代表微信号，circleText代表打招呼信息*/
                        XposedBridge.log("addWxiddd"+addWxid+"addMsggg"+addMsg);

                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Intent in=new Intent();
            in.setClassName(Constance.packageName_me,Constance.receiver_my);
            in.setAction(Constance.action_getWechatFriends);


            UserEntity userEntity = WechatDb.getInstance().selectSelf();
            String userName = userEntity.getUserName();
            String userTalker = userEntity.getUserTalker();
            String headPic = userEntity.getHeadPic();
            String alias = userEntity.getAlias();  //微信号
            XposedBridge.log(alias+userName+userTalker+headPic);     //获取数据库里面的历史数据
            in.putExtra("wxno",alias);
            in.putExtra("wxid",userTalker);
            in.putExtra("headImgUrl",headPic);
            context.sendBroadcast(in);
        }

    }

    /**
     * 从服务器下载文件
     * @param path 下载文件的地址
     * @param
     */
    public void downLoad(final String path, final String cirletext, final ClassLoader classLoader, final WechatEntity create, final String name, final String picpach, final String type, final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    String[] split = path.split(",");

                    String suffixes="avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|txt|html|htm|java|doc|amr";
                    String file=path.substring(path.lastIndexOf('/')+1);//截取url最后的数据
                    Pattern pat=Pattern.compile("[\\w]+[\\.]("+suffixes+")");//正则判断
                    Matcher mc=pat.matcher(file);
                    while(mc.find()){
                        //截取文件名后缀名
                        substring = mc.group();
                        XposedBridge.log("123456789"+ substring);
                    }

                    URL url = new URL(path);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(5000);
                    con.setConnectTimeout(5000);
                    con.setRequestProperty("Charset", "UTF-8");
                    con.setRequestMethod("GET");
                    if (con.getResponseCode() == 200) {
                        InputStream is = con.getInputStream();//获取输入流
                        FileOutputStream fileOutputStream = null;//文件输出流
                        if (is != null) {
                            FileUtils fileUtils = new FileUtils();
                            fileOutputStream = new FileOutputStream(fileUtils.createFile(substring));//指定文件保存路径，代码看下一步
                            byte[] buf = new byte[1024];
                            int ch;
                            while ((ch = is.read(buf)) != -1) {
                                fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
                            }
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.flush();
                            fileOutputStream.close();

                            if (type.equals("3")){
                                File file1=new File(picpach+substring);
                                boolean a = file1.exists();
                                if (a){
                                    boolean b = SendMesUtil.sendPic(classLoader, create, "", name, picpach+substring, 1);
                                }else {
                                    XposedBridge.log("992746034"+"图片还没下载好");
                                }

                            }else if (type.equals("34")){
                                if (activity!=null){
                                    SendMesUtil.sendAmr9(classLoader,create, activity,name,picpach+substring,1,1);
                                }else {
                                    Toast.makeText(context,"发送语音文件失败，请重连微信后再发送",Toast.LENGTH_SHORT).show();
                                    XposedBridge.log("activity为空");
                                }

                            }else if (type.equals("49")){
                                SendMesUtil.sendFile(classLoader, create, name, picpach+substring, 1);
                            }else if (type.equals("200")){
                                List<String> list = new ArrayList<String>();
                                list.add(picpach+substring);
                                JSONArray array= JSONArray.parseArray(JSON.toJSONString(list));
                                XposedBridge.log("array:"+array);
                                XposedBridge.log("cirletext:"+cirletext);
                                MomentUtil.sendPicMoment9(classLoader,create,cirletext,0,null,array);
                            }


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();



    }


    /**
     * 从服务器下载文件
     * @param path 下载文件的地址
     * @param
     */
    public  List<String> downLoadPicMonet(final String path, final String cirletext, final ClassLoader classLoader, final WechatEntity create, final String name, final String picpach, final String type, final Context context) {
        final List<String> listPicMonet = new ArrayList<String>();
                try {

                    String[] split = path.split(",");

                    for (int i = 0; i < split.length; i++) {
                        String suffixes = "avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|txt|html|htm|java|doc|amr";
                        String file = split[i].substring(split[i].lastIndexOf('/') + 1);//截取url最后的数据
                        Pattern pat = Pattern.compile("[\\w]+[\\.](" + suffixes + ")");//正则判断
                        Matcher mc = pat.matcher(file);
                        while (mc.find()) {
                            //截取文件名后缀名
                            substring = mc.group();
                            XposedBridge.log("123456789" + substring);
                        }

                        URL url = new URL(split[i]);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setReadTimeout(5000);
                        con.setConnectTimeout(5000);
                        con.setRequestProperty("Charset", "UTF-8");
                        con.setRequestMethod("GET");
                        if (con.getResponseCode() == 200) {
                            InputStream is = con.getInputStream();//获取输入流
                            FileOutputStream fileOutputStream = null;//文件输出流
                            if (is != null) {
                                FileUtils fileUtils = new FileUtils();
                                fileOutputStream = new FileOutputStream(fileUtils.createFile(substring));//指定文件保存路径，代码看下一步
                                byte[] buf = new byte[1024];
                                int ch;
                                while ((ch = is.read(buf)) != -1) {
                                    fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
                                }
                            }
                            if (fileOutputStream != null) {
                                fileOutputStream.flush();
                                fileOutputStream.close();

                                listPicMonet.add(picpach + substring);

                            }
                        }
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }

        return listPicMonet;

    }


    public class FileUtils {
        private String path = Environment.getExternalStorageDirectory().toString() + "/shidoe";

        public FileUtils() {
            File file = new File(path);
            /**
             *如果文件夹不存在就创建
             */
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        /**
         * 创建一个文件
         *
         * @param FileName 文件名
         * @return
         */
        public File createFile(String FileName) {
            return new File(path, FileName);
        }
    }




    /**
     * 复制单个文件
     *
     * @param oldPath$Name String 原文件路径+文件名 如：data/user/0/com.test/files/abc.txt
     * @param newPath$Name String 复制后路径+文件名 如：data/user/0/com.test/cache/abc.txt
     * @return <code>true</code> if and only if the file was copied;
     *         <code>false</code> otherwise
     */
    public boolean copyFile(String oldPath$Name, String newPath$Name) {
        try {
            File oldFile = new File(oldPath$Name);
            if (!oldFile.exists()) {
                XposedBridge.log("--Method--"+"copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                XposedBridge.log("--Method--\", \"copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                XposedBridge.log("--Method--\", \"copyFile:  oldFile cannot read.");
                return false;
            }


            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);
            FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
