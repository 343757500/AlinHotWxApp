package com.mikuwxc.autoreply.utils;

/**
 * Created by Administrator on 2016/11/29.
 */
public class HttpUtils {
    static String HOST = "http://120.24.102.187:8070/hiden/api/m/1.0/";
    /**
     * 测试环境
     */
//    static String HOST = "http://192.168.1.247:8080/hiden/api/m/1.0/";
    public static String LOGIN_OUT_URL = HOST + "devicelogOut.json";
    public static String JOB_RECORD_URL = HOST + "getJobListDataRecord.json";
    public static String JOB_LIST_URL = HOST + "MemgetJobListData.json";
    public static String CANCEL_JOB_LIST_URL = HOST + "ChangeMemcacheStatus.json";
    public static String ADD_DEVICE_INFO_URL = HOST + "addDeviceInfoContent.json";
    public static String ADD_FRIEND_INFO_URL = HOST + "addFriendConnData.json";
    public static String UPDATE_TASK_STATUS_URL = HOST + "doOneTaskStatus.json";
    public static String LOGIN_URL = HOST + "deviceLogin.json";
    public static String REGISTER_URL = HOST + "deviceRegister.json";
    public static String CHANGE_PHONE_STATUS_URL = HOST + "changePhoneListStatus.json";
    public static String ADD_SUCCESS_URL = HOST + "changePhonetoSuccess.json";


}
