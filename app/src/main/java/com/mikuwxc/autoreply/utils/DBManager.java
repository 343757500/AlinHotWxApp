package com.mikuwxc.autoreply.utils;

import android.content.Context;

import com.mikuwxc.autoreply.greendao.gen.DaoMaster;
import com.mikuwxc.autoreply.greendao.gen.DaoSession;
import com.mikuwxc.autoreply.greendao.gen.MessageFailBeanDao;
import com.mikuwxc.autoreply.modle.MessageFailBean;
import com.tencent.imsdk.protocol.im_common;

import java.util.List;

public class DBManager {
    private Context mContext;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private MessageFailBeanDao userDao;
 
    private DBManager(Context context) {
        mContext = context;
    }
 
    private static volatile DBManager instance = null;
    public static DBManager getInstance(Context context){
        if (instance==null){
            synchronized (DBManager.class){
                if (instance==null){
                    instance = new DBManager(context);
                }
            }
        }
        return instance;
    }
 
    public void init() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, "user.db");
        mDaoMaster = new DaoMaster(helper.getWritableDb());
        mDaoSession = mDaoMaster.newSession();
        userDao = mDaoSession.getMessageFailBeanDao();
    }
 
    public void insertTopicMo(MessageFailBean user) {
        userDao.insertOrReplace(user);
    }
 
    public void insertTopicMo(List<MessageFailBean> user) {
        userDao.insertOrReplaceInTx(user);
    }
    //单个删除
    public void deleteTopicMo(MessageFailBean user) {
        userDao.delete(user);
    }
    //删除所有
    public void deleteTopicMo() {
        userDao.deleteAll();
    }
    //更新
    public void updateTopicMo(MessageFailBean user) {
        userDao.update(user);
    }
    //查询所有
    public List<MessageFailBean> queryAllTopicMo() {
        return userDao.queryBuilder().build().list();
    }
    //where查询
    public List<MessageFailBean> query(String name) {
        return userDao.queryBuilder().where(MessageFailBeanDao.Properties.Username.eq(name)).list();
    }
    //between
    public List<MessageFailBean> querContent(int a,int b) {
        return userDao.queryBuilder().where(MessageFailBeanDao.Properties.Content.between(a,b)).list();
    }
}
 