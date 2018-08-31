package com.mikuwxc.autoreply.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;


import com.mikuwxc.autoreply.AlarmReceiver;
import com.mikuwxc.autoreply.R;
import com.mikuwxc.autoreply.StaticData;
import com.mikuwxc.autoreply.bean.TaskJob;
import com.mikuwxc.autoreply.utils.LogToFile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/1/6.
 */
public class HuliaoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.huliao_layout);
        if (StaticData.isDoJob) {//如果在执行任务，则存进任务队列
            TaskJob taskJob = new TaskJob();
            taskJob.setType(600);
            StaticData.jobList.add(taskJob);
        } else {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH-mm-ss");
            String format = simpleDateFormat.format(date);
            String[] split = format.split("-");
            int hh = Integer.valueOf(split[0]);
            int mm = Integer.valueOf(split[1]);
//        int max = ((mm + 2)>=60)?60:(mm+2);
            int min;
            if ((mm + 3) >= 60) {
                hh = hh + 1;
                mm = 1;
//            max = 2;
            } else {
                mm = mm + 2;
            }
            String timeStr = "";
            if (hh < 10) {
                timeStr = "0";
            }
            timeStr = timeStr + hh + ":";
            if (mm < 10) {
                timeStr = timeStr + "0";
            }
            timeStr = timeStr + mm;
            LogToFile.d("互聊中", timeStr + "将会进行聊天,人数：" + StaticData.friendBeanList.size());
            setAlarmTime(0, mm, hh);

            StaticData.isDoJob = true;
            StaticData.isChatOthers = true;
            String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis()));
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            Log.d("打招呼时间", "onCreate: " + hour);
            if (hour <= 10) {
                StaticData.chatContent = "早上好啊！现在时间是：" + time;
            } else if (hour <= 2) {
                StaticData.chatContent = "中午好啊！现在时间是：" + time;
            } else if (hour <= 18) {
                StaticData.chatContent = "下午好啊！现在时间是：" + time;
            } else if (hour <= 24) {
                StaticData.chatContent = "晚上好啊！现在时间是：" + time;
            }
            Intent mIntent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
            startActivity(mIntent);
        }
        finish();
    }

    /**
     * 设置闹钟
     *
     * @param alarmId 闹钟Id
     * @param minute  分钟
     * @param hour    小时
     */
    public void setAlarmTime(int alarmId, int minute, int hour) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, alarmId, intent, 0);

        long firstTime = SystemClock.elapsedRealtime();    // 开机之后到现在的运行时间(包括睡眠时间)
        long systemTime = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8")); // 这里时区需要设置一下，不然会有8个小时的时间差
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 选择的每天定时时间
        long selectTime = calendar.getTimeInMillis();

        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if (systemTime > selectTime) {
            //设置的时间小于当前时间
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            selectTime = calendar.getTimeInMillis();
        }

        // 计算现在时间到设定时间的时间差
        long time = selectTime - systemTime;
        firstTime += time;

        // 进行闹铃注册
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                firstTime, RunningActivity.DAY, sender);
    }

}
