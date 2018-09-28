package com.mikuwxc.autoreply.wcutil;


public class RiskActionUtil {
    private static long LAST_SEND_RISK_TIME = 0;

    public static void send(int riskType, boolean isIgnoreWait) {
        if (isIgnoreWait) {
            send(riskType);
        } else if (System.currentTimeMillis() - LAST_SEND_RISK_TIME > 5000) {
            LAST_SEND_RISK_TIME = System.currentTimeMillis();
            send(riskType);
        }
    }

    private static void send(int riskType) {
      /*  LocalSocketClient client = LocalSocketClient.getInstance();
        if (client != null) {
            client.reportPhoneRiskOperate(riskType);
        }*/
    }
}
