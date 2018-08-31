package com.mikuwxc.autoreply.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easy.wtool.sdk.WToolSDK;
import com.google.gson.Gson;
import com.mikuwxc.autoreply.R;
import com.mikuwxc.autoreply.common.UI;
import com.mikuwxc.autoreply.common.util.AppConfig;
import com.mikuwxc.autoreply.common.util.Constants;
import com.mikuwxc.autoreply.common.util.LogUtils;
import com.mikuwxc.autoreply.common.util.MyFileUtil;
import com.mikuwxc.autoreply.common.util.ObjectToJson;
import com.mikuwxc.autoreply.common.util.ServiceUtil;
import com.mikuwxc.autoreply.common.util.SharedPrefsUtils;
import com.mikuwxc.autoreply.common.util.ToastUtil;
import com.mikuwxc.autoreply.common.util.Utils;
import com.mikuwxc.autoreply.modle.C;
import com.mikuwxc.autoreply.modle.Event;
import com.mikuwxc.autoreply.modle.FriendBean;
import com.mikuwxc.autoreply.modle.LoginBean;
import com.mikuwxc.autoreply.presenter.LoginPresenter;
import com.mikuwxc.autoreply.presenter.tasks.AsyncFriendTask;
import com.mikuwxc.autoreply.service.AlarmReceiver;
import com.mikuwxc.autoreply.service.WechatService;
import com.mikuwxc.autoreply.view.interfas.ILoginView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * 登录界面   view
 * 实现  ILoginView Presenter 和 LoginActivity 通过接口交互。  并回调数据。
 */
public class LoginActivity extends BaseActivity implements ILoginView {

    @Bind(R.id.account)
    TextInputEditText account;
    private final int REQUEST_PHONE_PERMISSIONS = 0;
    @Bind(R.id.rvContent)
    RecyclerView mRvContent;
    @Bind(R.id.edtContent)
    EditText mEdtContent;
    @Bind(R.id.ivPic)
    ImageView mIvPic;
    private boolean permission_grant = false;
    private WToolSDK sdk;
    private List<FriendBean> mFriendBeans;
    private FriendAdapter mAdapter;
    private String wxid = null;
    private static final int IMAGE_REQUEST_CODE = 101;
    private String imgPath;
//    private boolean firstTime=true;
//    private long lastTaskId;
//    private int lastType;

    public static boolean isForeground = false;

    // 初始化   登录数据控制器
    private LoginPresenter loginPresenter;
    private String maillist;
    //    LoginReceiver mLoginReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);



        //接收hook成功时通讯录信息方法  在对应的Activity的文件清单中设置这个属性，才能进行数据的接收
        //    android:exported="true"
        Intent mIntent = this.getIntent();


        Bundle bundle = mIntent.getBundleExtra("bundle");
        if (bundle!=null) {
            maillist = bundle.getString("我是key");
            if(maillist!=null){
                Toast.makeText(this,maillist,Toast.LENGTH_SHORT).show();
            }
        }



        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionsList.size() == 0) {
                permission_grant = true;
                initVersion();
            } else {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
        } else {
            permission_grant = true;
            initVersion();
        }
        //初始化  控制器
        loginPresenter = new LoginPresenter(this);
        sdk = new WToolSDK();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_SCREEN_ON);
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_USER_PRESENT);
//        registerReceiver(mLoginReceiver, filter);
    }

    private void initVersion() {
        String version = MyFileUtil.readFromFile(AppConfig.APP_FOLDER + "/version");
        LogUtils.w(TAG, "version:" + version + " regId:" + AppConfig.Registration_Id);
        if (version == null) {
            version = UI.WX_VERSION;
        }
        UI.initUIVersion(version);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permission_grant = true;
                    initVersion();
                } else {
                    Toast.makeText(this, "你需要开启权限才能使用", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean isRegisterEventBus() {
        return true;
    }


    /**
     * 该方法  由  ILoginview 回调
     */
    @Override
    public void iLoginViewSuccess(LoginBean loginBean) {
        Log.e("111","ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt");
        dismissProcessDialog();
        //登录  成功  处理ui数据
        if (loginBean != null) {
            if (loginBean.getResult() != null) {
//                AppConfig.setIdentifier(loginBean.getResult().getName());
//                AppConfig.setUserSig(loginBean.getResult().getSig());
                Constants.token = loginBean.getResult().getToken();
                Log.e("111","uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu");
                LogUtils.d(TAG, "登录成功");
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
                        LogUtils.e(TAG, "保存token:" + Constants.token);
                    } catch (Exception e) {
                        LogUtils.e(TAG, "保存token出错");
                        e.printStackTrace();
                    }
                    ToastUtil.showLongToast("登录成功");
                } else {
                    ToastUtil.showLongToast("token为空");
                }
            } else {
                ToastUtil.showLongToast("result为空");
            }
        } else {
            ToastUtil.showLongToast("LoginBean为空");
        }
    }

    /**
     * 该方法  由   ILoginview  回调
     */
    @Override
    public void iLoginViewFailed(Exception e) {
        //登录 失败 处理UI数据
        Log.e("111","yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
        dismissProcessDialog();
        ToastUtil.showLongToast(e.getMessage());
    }

    /**
     * 获取手机号码
     */
    private void getPhoneAndIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        /**
         * 获取手机号码
         */
        @SuppressLint("MissingPermission") String phoneNumber1 = telephonyManager.getLine1Number();
        /**
         * 获取手机IMEI号
         */
        @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
        Constants.phone = phoneNumber1;
        Constants.imei = imei;
        getWxInfo();
    }

    private void getWxInfo() {
        LogUtils.d(TAG, "跳转去微信");
        Constants.isGetWxInfo = true;
        try {
            Intent intent;
            intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastUtil.showLongToast("检查到您手机没有安装微信，请安装后使用该功能");
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
        dismissProcessDialog();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEventBusCome(Event event) {
        if (event != null) {
            // 接受到Event后的相关逻辑
            switch (event.getCode()) {
                case C.EventCode.A:
                    break;
                case C.EventCode.B:
                    LogUtils.w(TAG, "wxno:" + Constants.wxno);
                    Log.e("111","qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
                    loginPresenter.iLoginPrToLogin(Constants.wxno, Constants.phone, Constants.imei, AppConfig.Registration_Id);
                    dismissProcessDialog();
                    break;
                case C.EventCode.H:
                    moveTaskToBack(true);
                    break;
            }
        }
    }


    @OnClick(R.id.btnFriend)
    public void onMBtnFriendClicked() {


        long syncFriendTime = SharedPrefsUtils.getLong("syncFriendTime");
        long currTime = System.currentTimeMillis();
        LogUtils.w(TAG, "currTime:" + currTime + " syncFriendTime:" + syncFriendTime + " =" + (currTime - syncFriendTime));
        if (currTime - syncFriendTime < 5 * 60 * 1000 && syncFriendTime != 0) {
            ToastUtil.showShortToast("请勿频繁刷新好友列表,请稍后几分钟");
            return;
        }
        showProcessDialog("加载中");
        mFriendBeans = AsyncFriendTask.getFriendList(sdk, false);
        dismissProcessDialog();
        if (mFriendBeans.size() != 0) {
            mRvContent.setVisibility(View.VISIBLE);
        } else {
            mRvContent.setVisibility(View.GONE);
        }
        mAdapter = new FriendAdapter(mFriendBeans);
        mRvContent.setAdapter(mAdapter);
        mRvContent.setLayoutManager(new LinearLayoutManager(this));
        Calendar today = Calendar.getInstance();
        Random random = new Random();
        int second = random.nextInt(10);
        String alarmTime = today.get(Calendar.YEAR) + "-" + (today.get(Calendar.MONTH) + 1) + "-" + today.get(Calendar.DAY_OF_MONTH) + " 23:59:0" + second;
        CountDownTimer cdt = new CountDownTimer(Utils.getTimeDiff(alarmTime), 30 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent i = new Intent(LoginActivity.this, AlarmReceiver.class);
                i.setAction(AlarmReceiver.SYNC_FRIEND);
                sendBroadcast(i);
            }
        }.start();
    }

//    @OnClick(R.id.btnSend)
//    public void onMBtnSendClicked() {
//        if (wxid == null) {
//            ToastUtil.showShortToast("请先选择好友");
//            return;
//        }
//        if (mEdtContent.getText() == null && imgPath == null) {
//            ToastUtil.showShortToast("请输入内容");
//            return;
//        }
//        int type = 0;
//        if (imgPath != null) {
//            type = 2;
//        } else if (mEdtContent.getText() != null && !TextUtils.isEmpty(mEdtContent.getText().toString())) {
//            type = 1;
//        }
//        showProcessDialog("发送中");
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("type", type);
//            jsonObject.put("taskid", System.currentTimeMillis());
//            jsonObject.put("content", new JSONObject());
//            jsonObject.getJSONObject("content").put("talker", wxid);
//            jsonObject.getJSONObject("content").put("timeout", 60);
//            if (type == 1) {
//                jsonObject.getJSONObject("content").put("text", sdk.encodeValue(mEdtContent.getText().toString()));
//            } else if (type == 2) {
//                jsonObject.getJSONObject("content").put("imagefile", imgPath);
//            }
//            jsonObject.getJSONObject("content").put("timeout", -1);
//            String result = sdk.sendTask(jsonObject.toString());
//            JSONObject jsonObject1 = new JSONObject(result);
//            if (jsonObject1.getInt("result") == 0) {
//                ToastUtil.showShortToast("发送成功");
//            } else {
//                ToastUtil.showShortToast(jsonObject1.getString("errmsg"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            ToastUtil.showShortToast("出错了");
//        }
//        dismissProcessDialog();
//        mEdtContent.setText("");
//        imgPath = null;
//        mIvPic.setVisibility(View.GONE);
//        KeyboardUtils.closeKeyboard(this);
//    }

    @OnClick(R.id.btnInit)
    public void onMBtnInitClicked() {
        initSDK();
        Intent intent = new Intent(this, LoginActivity.class);
        this.startActivity(intent);
    }

    private void initSDK() {
        String initResult = sdk.init("9999", "757533D0860F8CC0590B510BE2374F48C5750673");
        LogUtils.i(TAG, initResult);
        ToastUtil.showShortToast(ObjectToJson.parseResult(initResult));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_REQUEST_CODE && data != null) {
                try {
                    imgPath = Utils.getPath(this, data.getData());
                    Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
                    mIvPic.setVisibility(View.VISIBLE);
                    mIvPic.setImageBitmap(bitmap);
                } catch (Exception e) {
                    // TODO Auto-generatedcatch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (sdk != null) {
            sdk.unload();
        }
        super.onDestroy();
    }

    @OnClick(R.id.btnWechat)
    public void onMBtnWechatClicked() {
        AppConfig.setSelectHost(AppConfig.OUT_NETWORK);
        if (ServiceUtil.isAccessibilitySettingsOn(this, "com.mikuwxc.autoreply/com.mikuwxc.autoreply.service.WechatService")) {
            if (!Constants.wechatSvcIsRunning) {
                Intent intent = new Intent(this, WechatService.class);
                startService(intent);
                ToastUtil.showLongToast("请等待辅助服务进行连接,稍后再试");
            } else {
                //获取IMEI和手机号,然后打开微信
                if (permission_grant) {
                    Constants.startSendMsg = false;
                    Constants.sendMsgStatus = 0;
                    Constants.startGetMoney = false;
                    Constants.getMoneyStatus = 0;
                    showProcessDialog("正在一键登录");
                    getPhoneAndIMEI(Utils.getContext());
                    finish();
                } else {
                    ToastUtil.showLongToast("未开启所需的权限,请检查权限管理");
                }
            }
        } else {
            ToastUtil.showLongToast("辅助服务还未开启！请打开'微信辅助'");
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 不退出程序，进入后台
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class FriendAdapter extends BaseQuickAdapter<FriendBean, BaseViewHolder> {

        public FriendAdapter(@Nullable List<FriendBean> data) {
            super(R.layout.item_login_friend, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final FriendBean item) {
            TextView tvName = helper.getView(R.id.tvName);
            tvName.setText(item.getNickname());
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = helper.getAdapterPosition();
                    wxid = mFriendBeans.get(position).getWechatId();
                    ToastUtil.showShortToast("发送给:" + mFriendBeans.get(position).getNickname() + " wxid:" + wxid);
                }
            });
        }
    }
}


