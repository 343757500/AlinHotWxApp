package com.mikuwxc.autoreply.presenter.tasks;

import android.util.Log;
import android.widget.Toast;

import com.easy.wtool.sdk.WToolSDK;
import com.google.gson.Gson;
import com.mikuwxc.autoreply.common.net.NetApi;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.common.util.Constants;
import com.mikuwxc.autoreply.common.util.LogUtils;
import com.mikuwxc.autoreply.common.util.SharedPrefsUtils;
import com.mikuwxc.autoreply.common.util.ToastUtil;
import com.mikuwxc.autoreply.modle.FriendBean;
import com.mikuwxc.autoreply.modle.HttpBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedBridge;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Date: 2018/5/10 10:41.
 * Desc:
 */
public class AsyncFriendTask {
    private static final String TAG = AsyncFriendTask.class.getSimpleName();

    public static List<FriendBean> getFriendList(WToolSDK sdk, boolean onSvc) {
        List<FriendBean> mFriendBeans = new ArrayList<>();
        Constants.wxidList = new ArrayList<>();
        try {
            String text = "";
            JSONObject jsonTask = new JSONObject();
            jsonTask.put("type", 5);
            jsonTask.put("taskid", System.currentTimeMillis());
            jsonTask.put("content", new JSONObject());
            jsonTask.getJSONObject("content").put("pageindex", 0);
            jsonTask.getJSONObject("content").put("pagecount", 0);
            String content = sdk.sendTask(jsonTask.toString());
            LogUtils.d(TAG, content);
            final JSONObject jsonObject = new JSONObject(content);
            if (jsonObject.getInt("result") == 0) {
                JSONArray jsonArray1 = jsonObject.getJSONArray("content");
                if (jsonArray1.length() > 0) {
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject friendJson = jsonArray1.getJSONObject(i);
                        String nickname = sdk.decodeValue(friendJson.getString("nickname"));
                        String wxid = sdk.decodeValue(friendJson.getString("wxid"));
                        String wxno = sdk.decodeValue(friendJson.getString("wxno"));
                        String remarkname = sdk.decodeValue(friendJson.getString("remark"));
                        LogUtils.v(TAG, "nickname:" + nickname + " wxid:" + wxid + " wxno:" + wxno + " remarkname:" + remarkname);
                        FriendBean bean = new FriendBean();
                        bean.setNickname(nickname);
                        bean.setWxid(wxid);
                        bean.setWxno(wxno);//先用来存放微信号
                        bean.setRemarkname(remarkname);
                        mFriendBeans.add(bean);
                        Constants.wxidList.add(wxid);
                    }
                    if (Constants.token != null) {
                        sendFriendList(Constants.token, mFriendBeans, false);
                    } else {
                        showNotice(onSvc, "请先打开微信辅助");
                        return mFriendBeans;
                    }
                }
            } else {
                text = jsonObject.getString("errmsg");
                showNotice(onSvc, text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mFriendBeans;
    }

    public static void sendFriendList(String wxToken, List<FriendBean> friendList, final boolean onSvc) {
        Gson gson = new Gson();
        String listStr = gson.toJson(friendList);
        Log.e("111","sendFriendList---ip"+AppConfig.OUT_NETWORK);
        OkGo.post(AppConfig.OUT_NETWORK + NetApi.syncFriend + "/" + wxToken).headers("Content-Type", "application/json").upJson(listStr).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.e("111","777777777777777777777777777777777777777777777777777777777");
                LogUtils.i(TAG, "result:" + s);
                LogUtils.e("111", "result:" + s);
                try {
                    HttpBean bean = new Gson().fromJson(s, HttpBean.class);
                    if (bean.isSuccess()) {
                        //showNotice(onSvc, "同步成功");
                        SharedPrefsUtils.putLong("syncFriendTime", System.currentTimeMillis());
                    } else {
                        showNotice(onSvc, bean.getCode() + " " + bean.getMsg());
                    }
                } catch (Exception e) {
                    showNotice(onSvc, e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Log.e("111","888888888888888888888888888888888888888888888888888888888888888888");
                showNotice(onSvc, e.getMessage());
                LogUtils.e("111", e.toString());
            }
        });
    }

    private static void showNotice(boolean onSvc, String msg) {
        if (onSvc) {
            LogUtils.i(TAG, msg);
        } else {
            ToastUtil.showLongToast(msg);
        }
    }
}
