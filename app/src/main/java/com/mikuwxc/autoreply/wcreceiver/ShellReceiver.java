/*
package com.mikuwxc.autoreply.wcreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.DataOutputStream;

public class ShellReceiver extends BroadcastReceiver {

    private String[] search = {
            "input keyevent 3",// 返回到主界面，数值与按键的对应关系可查阅KeyEvent
            "sleep 1",// 等待1秒
            // 打开微信的启动界面，am命令的用法可自行百度、Google// 等待3秒
            //"am start -n com.tencent.mm/com.tencent.mm.ui.chatting.ChattingUI -e \"Chat_User\" zsp343757500",
            "am start -a com.tencent.mm.action.BIZSHORTCUT -f 67108864"      //  启动微信"// 打开微信的搜索
            // 像搜索框中输入123，但是input不支持中文，蛋疼，而且这边没做输入法处理，默认会自动弹出输入法
    };


    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.zhongshao".equals(intent.getAction())){
            Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show();
            search[2] = chineseToUnicode(search[2]);
            execShell(search);
        }
    }



    */
/**
     64      * 把中文转成Unicode码
     65      * @param str
     66      * @return
     67      *//*

    public String chineseToUnicode(String str){
        String result="";
        for (int i = 0; i < str.length(); i++){
            int chr1 = (char) str.charAt(i);
            if(chr1>=19968&&chr1<=171941){//汉字范围 \u4e00-\u9fa5 (中文)
                result+="\\u" + Integer.toHexString(chr1);
            }else{
                result+=str.charAt(i);
            }
        }
        return result;
    }


    */
/**
     30      * 执行Shell命令
     31      *
     32      * @param commands
     33      *            要执行的命令数组
     34      *//*

    public void execShell(String[] commands) {
        // 获取Runtime对象
        Runtime runtime = Runtime.getRuntime();

        DataOutputStream os = null;
        try {
            // 获取root权限
            Process process = runtime.exec("su");
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                os.write(command.getBytes());
                os.writeBytes("\n");
                os.flush();
            }
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */
/**
     64      * 把中文转成Unicode码
     65      * @param str
     66      * @return
     67      *//*

}
*/
