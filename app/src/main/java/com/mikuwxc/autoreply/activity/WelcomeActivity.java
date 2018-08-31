package com.mikuwxc.autoreply.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mikuwxc.autoreply.R;
import com.mikuwxc.autoreply.common.MyApp;
import com.mikuwxc.autoreply.utils.PreferenceUtil;


/**
 * Created by Administrator on 2016/11/21.
 */
public class WelcomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Log.d("welcome", "onCreate: welcome");
        if (MyApp.isWelcom) {
            if (PreferenceUtil.isLogin(WelcomeActivity.this)) {
                startActivity(new Intent(WelcomeActivity.this, RunningActivity.class));
            } else {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
            finish();
            return;
        }
        MyApp.isWelcom = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 1000);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("welcome", "onCreate: handleMessage");
            if (PreferenceUtil.isLogin(WelcomeActivity.this)) {
                startActivity(new Intent(WelcomeActivity.this, RunningActivity.class));
            } else {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
            WelcomeActivity.this.finish();
        }
    };

}
