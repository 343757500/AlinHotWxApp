package com.mikuwxc.autoreply;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.mikuwxc.autoreply.activity.AuthorityActivity;
import com.mikuwxc.autoreply.activity.HuliaoActivity;
import com.mikuwxc.autoreply.activity.RunningActivity;


/**
 * @ClassName: AlarmReceiver
 * @Description: 闹铃时间到了会进入这个广播，这个时候可以做一些该做的业务。
 */
public class AlarmReceiver extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        //Toast.makeText(context, "闹铃响了, 可以做点事情了~~", Toast.LENGTH_LONG).show();
        Log.d("AlarmReceiver", "闹铃响了, 可以做点事情了~~");
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unlock");
        kl.disableKeyguard();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "bright");
        wl.acquire();
        //wl.release();
     /*   intent = new Intent(context, AuthorityActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);*/
//        Intent mIntent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
//        context.startActivity(mIntent);

    }
}
