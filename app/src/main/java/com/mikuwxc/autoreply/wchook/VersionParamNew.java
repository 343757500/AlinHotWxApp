package com.mikuwxc.autoreply.wchook;


public class VersionParamNew {

    public static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";

    public static String receiveUIFunctionName = "d";

    /**
     * last param of receiveUIFunctionName for class luckyMoneyReceiveUI
     */
    public static String receiveUIParamName = "com.tencent.mm.ab.l";

    /**
     * Search MMCore has not been initialize ?
     */
    public static String networkRequest = "com.tencent.mm.model.au";

    /**
     * Search MMCore has not been initialize ? next function of networkRequest
     */
    public static String getNetworkByModelMethod = "DF";


    public static String luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";

    /**
     * Search jSONObject.optString("timingIdentifier")
     */
    public static String receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ag";


    /**
     * Search hashMap.put("timingIdentifier", str
     */
    public static String luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ad";

    /**
     * search /cgi-bin/mmpay-bin/transferoperation
     */
    public static String getTransferRequest = WECHAT_PACKAGE_NAME + ".plugin.remittance.model.u";


    public static boolean hasTimingIdentifier = false;


    public static void init(String version) {
        switch (version) {
            case "6.3.22":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.t.j";
                networkRequest = "com.tencent.mm.model.ah";
                getNetworkByModelMethod = "tF";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plxugin.luckymoney.c.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ab";
                break;
            case "6.3.23":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.t.j";
                networkRequest = "com.tencent.mm.model.ah";
                getNetworkByModelMethod = "vE";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ab";
                break;
            case "6.3.25":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.t.j";
                networkRequest = "com.tencent.mm.model.ah";
                getNetworkByModelMethod = "vF";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ab";
                break;
            case "6.3.27":
                receiveUIFunctionName = "e";
                receiveUIParamName = "com.tencent.mm.u.k";
                networkRequest = "com.tencent.mm.model.ah";
                getNetworkByModelMethod = "yj";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ab";
                break;
            case "6.3.28":
                receiveUIFunctionName = "c";
                receiveUIParamName = "com.tencent.mm.v.k";
                networkRequest = "com.tencent.mm.model.ah";
                getNetworkByModelMethod = "vP";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ab";
                break;
            case "6.3.30":
            case "6.3.31":
                receiveUIFunctionName = "c";
                receiveUIParamName = "com.tencent.mm.v.k";
                networkRequest = "com.tencent.mm.model.ah";
                getNetworkByModelMethod = "vS";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ab";
                break;
            case "6.3.32":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.v.k";
                networkRequest = "com.tencent.mm.model.ak";
                getNetworkByModelMethod = "vw";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ab";
                break;
            case "6.5.3":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.v.k";
                networkRequest = "com.tencent.mm.model.ak";
                getNetworkByModelMethod = "vy";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                break;
            case "6.5.4":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.u.k";
                networkRequest = "com.tencent.mm.model.ak";
                getNetworkByModelMethod = "vy";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ab";
                hasTimingIdentifier = true;
                break;
            case "6.5.6":
            case "6.5.7":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.u.k";
                networkRequest = "com.tencent.mm.model.al";
                getNetworkByModelMethod = "vM";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.En_fba4b94f";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ab";
                hasTimingIdentifier = true;
                break;
            case "6.5.8":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.w.k";
                networkRequest = "com.tencent.mm.model.an";
                getNetworkByModelMethod = "uC";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.En_fba4b94f";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ab";
                hasTimingIdentifier = true;
                break;
            case "6.5.10":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.w.k";
                networkRequest = "com.tencent.mm.s.ao";
                getNetworkByModelMethod = "uH";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.En_fba4b94f";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.c.ab";
                hasTimingIdentifier = true;
                break;
            case "6.5.13":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.y.k";
                networkRequest = "com.tencent.mm.u.ap";
                getNetworkByModelMethod = "vd";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.En_fba4b94f";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ab";
                hasTimingIdentifier = true;
                break;
            case "6.5.16":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.ac.k";
                networkRequest = "com.tencent.mm.x.ap";
                getNetworkByModelMethod = "wT";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.En_fba4b94f";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ab";
                hasTimingIdentifier = true;
                break;
            case "6.5.19":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.ad.k";
                networkRequest = "com.tencent.mm.y.at";
                getNetworkByModelMethod = "wS";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.En_fba4b94f";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ab";
                hasTimingIdentifier = true;
                break;
            case "6.5.22":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.ad.k";
                networkRequest = "com.tencent.mm.y.at";
                getNetworkByModelMethod = "wW";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.En_fba4b94f";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ab";
                hasTimingIdentifier = true;
                break;
            case "6.5.23":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.ad.k";
                networkRequest = "com.tencent.mm.y.at";
                getNetworkByModelMethod = "wY";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.En_fba4b94f";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ab";
                hasTimingIdentifier = true;
                break;
            case "6.6.0":
            case "6.6.1":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.ad.k";
                networkRequest = "com.tencent.mm.y.as";
                getNetworkByModelMethod = "ys";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ae";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ab";
                getTransferRequest = WECHAT_PACKAGE_NAME + ".plugin.remittance.c.l";
                hasTimingIdentifier = true;
                break;
            case "6.6.2":
            case "6.6.3":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.ae.k";
                networkRequest = "com.tencent.mm.z.ar";
                getNetworkByModelMethod = "CG";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.af";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ac";
                getTransferRequest = WECHAT_PACKAGE_NAME + ".plugin.remittance.model.s";
                hasTimingIdentifier = true;
                break;
            case "6.6.5":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.ac.l";
                networkRequest = "com.tencent.mm.y.as";
                getNetworkByModelMethod = "CN";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.af";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ac";
                getTransferRequest = WECHAT_PACKAGE_NAME + ".plugin.remittance.model.t";
                hasTimingIdentifier = true;
                break;
            case "6.6.6":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.ac.l";
                networkRequest = "com.tencent.mm.z.au";
                getNetworkByModelMethod = "Dv";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.af";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ac";
                getTransferRequest = WECHAT_PACKAGE_NAME + ".plugin.remittance.model.u";
                hasTimingIdentifier = true;
                break;
            case "6.6.7":
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.ab.l";
                networkRequest = "com.tencent.mm.model.au";
                getNetworkByModelMethod = "DF";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ag";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ad";
                getTransferRequest = WECHAT_PACKAGE_NAME + ".plugin.remittance.model.u";
                hasTimingIdentifier = true;
                break;
            case "6.7.2":
                receiveUIFunctionName = "c";
                receiveUIParamName = "com.tencent.mm.af.m";   //微信源码能找到
                networkRequest = "com.tencent.mm.model.av";
                getNetworkByModelMethod = "CB";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ag";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ad";
                getTransferRequest = WECHAT_PACKAGE_NAME + ".plugin.remittance.model.u";
                hasTimingIdentifier = true;
                break;
            default:
                receiveUIFunctionName = "d";
                receiveUIParamName = "com.tencent.mm.ab.l";
                networkRequest = "com.tencent.mm.model.au";
                getNetworkByModelMethod = "DF";
                receiveLuckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ag";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                luckyMoneyRequest = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.b.ad";
                getTransferRequest = WECHAT_PACKAGE_NAME + ".plugin.remittance.model.u";
                hasTimingIdentifier = true;
        }
    }
}
