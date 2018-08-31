package com.mikuwxc.autoreply.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.mikuwxc.autoreply.R;
import com.mikuwxc.autoreply.StaticData;


public class LockScreenActivity extends Activity {

    private Button closebtn;
    private Button detailbtn;
    private TextView text;
    private TextView caltext;
    private TextView timetext;
    private showReceiver receiver;
    private ViewFlipper viewFlipper;
    private ListView listView;
    private Button Levelbtn;

    //电量相关
    private int batteryLevel;
    private int batteryScale;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取当前电量，如未获取具体数值，则默认为0
            batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            //获取最大电量，如未获取到具体数值，则默认为100
            batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            //显示电量
            Levelbtn.setText((batteryLevel * 100 / batteryScale) + "%");
        }
    };

    //30秒更新一次锁屏时间信息
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        public void run() {
            showInfo();
            handler.postDelayed(this, 30000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.activity_screen);

        Log.i("demo", "Create...");
        closebtn = (Button) this.findViewById(R.id.closebtn);
        text = (TextView) this.findViewById(R.id.screentext);
        caltext = (TextView) findViewById(R.id.caltext);
        timetext = (TextView) findViewById(R.id.timetext);
        viewFlipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);
        detailbtn = (Button) this.findViewById(R.id.showDetails);
        listView = (ListView) this.findViewById(R.id.listview);
        Levelbtn = (Button) findViewById(R.id.progbLevel);

        //设置切换动画
        viewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);

        //定时更新界面
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 30000);

        //监听按钮点击事件
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticData.total = 0;
                StaticData.replaied = 0;
                StaticData.data.clear();
                finish();
            }
        });

        detailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
            }
        });

        //注册广播接收器
        receiver = new showReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.autoreply.SHOW_ACTION");//自定义广播，用于实时更新锁屏显示信息
        registerReceiver(receiver, filter);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        //注册接收器以获取电量信息
        registerReceiver(broadcastReceiver, intentFilter);

        //显示信息
        showInfo();
        showDetails();

        Log.i("demo", "Create");
    }

    public void showInfo() {
        Time time = new Time();
        time.setToNow();
        int year = time.year;
        int month = time.month;
        int day = time.monthDay;
        int minute = time.minute;
        int hour = time.hour;
        caltext.setText(year + "-" + (month + 1) + "-" + day);
        timetext.setText((hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute));
        text.setText("共收到 " + StaticData.total + " 条微信消息\n\n" + "自动回复 " + StaticData.replaied + " 条\n");
        if (StaticData.total == 0) {
            detailbtn.setEnabled(false);
            detailbtn.setText("暂无消息");
        } else {
            detailbtn.setEnabled(true);
            detailbtn.setText("消息详情");
        }
        Log.i("demo", "showInfo");
    }

    public void showDetails() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LockScreenActivity.this, android.R.layout.simple_list_item_1, StaticData.data);
        listView.setAdapter(adapter);
        Log.i("demo", "showDetails");
    }

    //屏蔽返回事件
    @Override
    public void onBackPressed() {

    }

    @Override
    public void finish() {
        super.finish();
        //activity的消失动画效果
        //overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);//系统自带动画
        overridePendingTransition(R.anim.activity_close, R.anim.activity_start);//自定义动画
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
        unregisterReceiver(broadcastReceiver);

        Log.i("demo", "Destroy");
    }

    //自定义广播接收器，实时更新锁屏信息
    class showReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.example.autoreply.SHOW_ACTION")) {
                showInfo();
                showDetails();
            }
        }
    }
}
