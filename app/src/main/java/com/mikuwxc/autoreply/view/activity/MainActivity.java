package com.mikuwxc.autoreply.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.mikuwxc.autoreply.R;
import com.mikuwxc.autoreply.bean.ImLoginBean;
import com.mikuwxc.autoreply.common.Config;
import com.mikuwxc.autoreply.common.MyApp;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.common.util.DensityUtils;
import com.mikuwxc.autoreply.common.util.EventBusUtil;
import com.mikuwxc.autoreply.common.util.Logger;
import com.mikuwxc.autoreply.common.util.PollingUtils;
import com.mikuwxc.autoreply.common.util.SPHelper;
import com.mikuwxc.autoreply.common.util.ToastUtil;
import com.mikuwxc.autoreply.modle.C;
import com.mikuwxc.autoreply.modle.Event;
import com.mikuwxc.autoreply.modle.MessageBean;
import com.mikuwxc.autoreply.service.AutoOpenLuckyMoneyService;
import com.mikuwxc.autoreply.service.AutoReplyService;
import com.mikuwxc.autoreply.service.PollingService;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String KEY_REPLY = "reply";
    private static final String KEY_LUCKY_MONEY = "lucky_money";
    private static final String KEY_SELECT_ID = "select_id";
    private static final String KEY_AUTO_REPLY_TEXT = "reply_text";

    private MyAdapter mAdapter;

    private final List<String> mData = new ArrayList<>();
    private static final String TAG = MainActivity.class.getSimpleName();
    private String sig;
    private String id;
    private String sdkAppId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SPHelper.getInstance().init(this);
        initData();
        initView();
       // loginIM();

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
            Log.d(TAG, "addElement failed");
            return;
        }

        //发送消息
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                Log.e(TAG, "send message failed. code: " + code + " errmsg: " + desc);
                ToastUtil.showShortToast("发送消息失败"+code+"--"+desc);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                Log.e(TAG, "SendMsg ok");
                ToastUtil.showShortToast("发送消息成功");
            }
        });
    }

    @Override
    public boolean isRegisterEventBus() {
        return true;
    }

    private void initData() {
        Config.isOpenAutoReply = SPHelper.getInstance().getBoolean(KEY_REPLY);
        Config.isOpenAutoOpenLuckyMoney = SPHelper.getInstance().getBoolean(KEY_LUCKY_MONEY);
        int selectId = SPHelper.getInstance().getInt(KEY_SELECT_ID);
        if (selectId < 0 || selectId > 2) {
            selectId = 0;
        }
        Config.SelectId = selectId;
        Config.AutoReplyText = SPHelper.getInstance().getString(KEY_AUTO_REPLY_TEXT);

        // 自动回复默认文本
        mData.add("Hey~");
        mData.add("你已被拉进黑名单");
        mData.add("忙碌.jpg");

        volleyGet();
    }


    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddReplyView(MainActivity.this);
            }
        });

        SwitchCompat swReply = (SwitchCompat) findViewById(R.id.sw_auto_reply);
        swReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkAutoReplyService(MainActivity.this);
                    Config.isOpenAutoReply = true;
                    SPHelper.getInstance().putBoolean(KEY_REPLY, true);
                } else {
                    Config.isOpenAutoReply = false;
                    SPHelper.getInstance().putBoolean(KEY_REPLY, false);
                }
            }
        });
        swReply.setChecked(Config.isOpenAutoReply);

        SwitchCompat swLuckyMoney = (SwitchCompat) findViewById(R.id.sw_auto_open_lucky_money);
        swLuckyMoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkAutoOpenLuckyMoneyService(MainActivity.this);
                    Config.isOpenAutoOpenLuckyMoney = true;
                    SPHelper.getInstance().putBoolean(KEY_LUCKY_MONEY, true);
                } else {
                    Config.isOpenAutoOpenLuckyMoney = false;
                    SPHelper.getInstance().putBoolean(KEY_LUCKY_MONEY, false);
                }
            }
        });
        swLuckyMoney.setChecked(Config.isOpenAutoOpenLuckyMoney);


        Button messageBtn = (Button)findViewById(R.id.btn_message);
        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.finish();
            }
        });


        mAdapter = new MyAdapter(this, mData, Config.SelectId);
        ListView lvContents = (ListView) findViewById(R.id.lv_reply_content);
        lvContents.setAdapter(mAdapter);
    }

    /**
     * 登录腾讯云 IM
     */
    private void loginIM() {
        TIMUser user = new TIMUser();
        user.setIdentifier(AppConfig.getIdentifier());
        //发起登录请求
        TIMManager.getInstance().login(
                id,//sdkAppId，由腾讯分配
                sig,//用户帐号签名，由私钥加密获得，具体请参考文档
                new TIMCallBack() {//回调接口

                    @Override
                    public void onSuccess() {//登录成功
                        Log.d(TAG, "login succ");
                        ToastUtil.showLongToast("腾讯IM登录成功:" + AppConfig.getIdentifier());
                        NewMessageListener();
                        sendMsg();
                        //PollingUtils.startPollingService(MainActivity.this, 1, PollingService.class, PollingService.ACTION);
                    }

                    @Override
                    public void onError(int code, String desc) {//登录失败

                        //错误码code和错误描述desc，可用于定位请求失败原因
                        //错误码code含义请参见错误码表
                        Log.d(TAG, "login failed. code: " + code + " errmsg: " + desc);
                        ToastUtil.showLongToast("腾讯IM登录失败111");
                        Log.e("111",code+"_____"+desc);
                    }
                });
    }



    private void NewMessageListener() {
        //设置消息监听器，收到新消息时，通过此监听器回调
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {//消息监听器
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {//收到新消息

                TIMMessage msg = list.get(0);
                for (int i = 0; i < msg.getElementCount(); ++i) {
                    TIMElem elem = msg.getElement(i);

                    //获取当前元素的类型
                    TIMElemType elemType = elem.getType();
                    Log.d(TAG, "elem type: " + elemType.name());
                    if (elemType == TIMElemType.Text) {
                        //处理文本消息
                        TIMUserProfile sendUser = msg.getSenderProfile();
                        String sender = msg.getSender();
                        final TIMTextElem textElem = (TIMTextElem) elem;
                        if (!msg.isSelf()) {
                            if (!TextUtils.isEmpty(textElem.getText().replaceAll("&quot;", "\""))) {
                                Logger.d("收到消息了。。。。。" + textElem.getText().replaceAll("&quot;", "\""));

                                ToastUtil.showLongToast(textElem.getText().replaceAll("&quot;", "\""));
                                if (textElem.getText().contains("remarkname")) {

                                    MessageBean messageBean = new Gson().fromJson(textElem.getText().replaceAll("&quot;", "\""), MessageBean.class);

                                    ToastUtil.showLongToast("消息发送人: " + messageBean.getRemarkname() + "  消息内容: " + messageBean.getText());
                                    EventBusUtil.sendEvent(new Event(C.EventCode.A, messageBean));
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


    private final int ADD_TEXT_LIMIT = 8;

    private void showAddReplyView(final Context context) {
        final RelativeLayout contentView = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params.setMargins(DensityUtils.dp2px(context, 30), 0,
                DensityUtils.dp2px(context, 20), 0);
        final EditText editText = new EditText(context);
        contentView.addView(editText, params);

        final Dialog dialog = new AlertDialog.Builder(context)
                .setTitle("添加自动回复文本")
                .setView(contentView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        if (text.length() > 0) {
                            if (mData.size() < ADD_TEXT_LIMIT) {
                                addText(text);
                                Snackbar.make(contentView, "添加文本成功", Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                            } else {
                                Snackbar.make(contentView, "添加文本已到上限", Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        dialog.setCancelable(true);
        dialog.show();
    }

    private void addText(String text) {
        mData.add(text);
        mAdapter.notifyDataSetChanged();
    }

    private void checkAutoReplyService(Context context) {
        if (!AutoReplyService.isConnected()) {
            showAccessibilityServiceSettings(context);
        }
    }

    private void checkAutoOpenLuckyMoneyService(Context context) {
        if (!AutoOpenLuckyMoneyService.isConnected()) {
            showAccessibilityServiceSettings(context);
        }
    }

    private Dialog mDialog = null;

    private void showAccessibilityServiceSettings(Context context) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = new AlertDialog.Builder(context)
                .setMessage("使用微信自动服务需要打开辅助服务, 去设置?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        mDialog.show();
    }

    /* ==== Adapter ==== */
    private class MyAdapter extends BaseAdapter {

        List<String> mData;
        LayoutInflater mInflater;
        int mSelectId;

        MyAdapter(Context context, List<String> data, int selectId) {
            mData = data;
            mInflater = LayoutInflater.from(context);
            mSelectId = selectId;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.listview_item, parent, false);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_content);
                viewHolder.checkBox = (AppCompatCheckBox) convertView.findViewById(R.id.cb_select);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.textView.setText(mData.get(position));
            viewHolder.checkBox.setChecked((mSelectId == position));
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mSelectId = position;
                        notifyDataSetChanged();
                        // 保存数据
                        SPHelper.getInstance().putInt(KEY_SELECT_ID, mSelectId);
                        String text = mData.get(position);
                        Config.AutoReplyText = text;
                        SPHelper.getInstance().putString(KEY_AUTO_REPLY_TEXT, text);
                    }
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView textView;
            AppCompatCheckBox checkBox;
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);
        //登出
    /*    TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {

                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                Log.d(TAG, "logout failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess() {
                ToastUtil.showLongToast("登出成功");
            }
        });*/
    }



    private void volleyGet() {
        String url = "http://120.24.102.187:8066/api/1.0/im/wxno?wxno=hsl_test";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {//s为请求返回的字符串数据
                        ImLoginBean imLoginBean = new Gson().fromJson(s, ImLoginBean.class);
                        sig = imLoginBean.getResult().getSig();
                        id = imLoginBean.getResult().getRelationId();
                        sdkAppId = imLoginBean.getResult().getSdkAppId();

                        initTMConfig();
                        loginIM();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MainActivity.this,volleyError.toString(),Toast.LENGTH_LONG).show();
                    }
                });
        //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
        request.setTag("testGet");
        //将请求加入全局队列中
        MyApp.getHttpQueues().add(request);
    }



    /**
     * 初始化腾讯云IM 配置
     */
    private void initTMConfig() {
        //初始化SDK基本配置
        TIMSdkConfig config = new TIMSdkConfig(Integer.parseInt(sdkAppId))
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

}