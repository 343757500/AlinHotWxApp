package com.mikuwxc.autoreply;

import android.database.Cursor;

import com.mikuwxc.autoreply.modle.FriendBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by Administrator on 2018-06-04.
 */

public class Tools {

    public static HashMap getAllContactWithoutChatroom(ClassLoader classLoader)
    {
        Cursor cursor= getCursor(classLoader,"@all.contact.android");

        HashMap hashMap=new HashMap();
        if(cursor==null)
        {
            return null;
        }

        for (cursor.moveToFirst();!cursor.isLast();cursor.moveToNext())
        {
            int index_username=cursor.getColumnIndex("username");
            int index_nickname=cursor.getColumnIndex("nickname");
            String username=cursor.getString(index_username);
            String nickname=cursor.getString(index_nickname);

            hashMap.put(username,nickname);
            //XposedBridge.log(username+"="+nickname);
        }

        int index_username=cursor.getColumnIndex("username");
        int index_nickname=cursor.getColumnIndex("nickname");
        String username=cursor.getString(index_username);
        String nickname=cursor.getString(index_nickname);

        hashMap.put(username,nickname);
        cursor.close();
        return  hashMap;
    }

    public static ArrayList getAllContact(ClassLoader classLoader)
    {
        Cursor cursor= getCursor(classLoader,"@all.contact.android");

        HashMap hashMap=new HashMap();

        FriendBean friendBean;
        ArrayList<FriendBean> beanArrayList=new ArrayList<>();
        if(cursor==null)
        {
            return null;
        }

        for (cursor.moveToFirst();!cursor.isLast();cursor.moveToNext())
        {
            int index_username=cursor.getColumnIndex("username");
            int index_nickname=cursor.getColumnIndex("nickname");
            int index_alias=cursor.getColumnIndex("alias");//微信号
            int index_conRemark=cursor.getColumnIndex("conRemark");//测试




            String username=cursor.getString(index_username);
            String nickname=cursor.getString(index_nickname);
            String alias=cursor.getString(index_alias);
            String conRemark=cursor.getString(index_conRemark);





             //XposedBridge.log(index_conRemark+"="+conRemark);


            hashMap.put(username,nickname);




            friendBean=new FriendBean();
            friendBean.setNickname(nickname);
            friendBean.setWxid(username);
            //friendBean.setWxno(alias);
            if (alias.equals("")){
                friendBean.setWxno(username);
            }else{
                friendBean.setWxno(alias);
            }

            friendBean.setRemarkname(conRemark);
            beanArrayList.add(friendBean);
        }

        int index_username=cursor.getColumnIndex("username");
        int index_nickname=cursor.getColumnIndex("nickname");
        String username=cursor.getString(index_username);
        String nickname=cursor.getString(index_nickname);

        hashMap.put(username,nickname);
        cursor.close();
        return  beanArrayList;
    }

    public  static Cursor getCursor(ClassLoader classLoader, String arg)
    {
        try {
            Class cc=  classLoader.loadClass("com.tencent.mm.z.c");
            Object FO= XposedHelpers.callStaticMethod(cc,"FO");
            Cursor cursor=(Cursor) XposedHelpers.callMethod(FO,"c",arg,"",(List)null);
            return cursor;
        }catch (Exception e)
        {
           /* com.example.administrator.myhook.Tools.printException(e);*/
        }

        return null;
    }

}
