package com.mikuwxc.autoreply.xposed;

/**
 * Created by duqian on 2017/5/10.
 */

public class VersionParam {

    public static final String PACKAGE_NAME = "com.tencent.mm";

    //conversation
    public static String conversationClass = PACKAGE_NAME + ".e.b.ag";
    public static String con_GetCursorMethod = "b";
    public static String con_MessageClass1 = PACKAGE_NAME + ".ab.a";//message
    public static String con_NetworkMethod = "a";//a
    public static String con_MessageClass = PACKAGE_NAME + ".modelmulti.i";
    public static String con_ImageClass = PACKAGE_NAME + "af.k"; //NetSceneUploadMsgImg
    public static String activity_chatingui = PACKAGE_NAME + ".ui.chatting.ChattingUI";
    public static String activity_chatingui_a = activity_chatingui + ".a";
    public static String networkRequest = PACKAGE_NAME + ".model.ak";
    public static String requestMethod = "vw";
    public static String con_MediaClass = PACKAGE_NAME + ".pluginsdk.ui.tools.SightCaptureResult";
    public static String unknownString = PACKAGE_NAME + ".model.k.xD"; //未知的方法,返回String,需要hook看结果
    public static String con_UploadVoiceClass = PACKAGE_NAME + ".modelvoice.f"; //NetSceneUploadVoice, asL是文件名

    //nearby friends
    public static String NearbyFriendsUI = PACKAGE_NAME + ".plugin.nearby.ui.NearbyFriendsUI";
    public static String ContactInfoUI = PACKAGE_NAME + ".plugin.profile.ui.ContactInfoUI";
    public static String SayHiEditUI = PACKAGE_NAME + ".ui.contact.SayHiEditUI";
    public static String SayHiModel = PACKAGE_NAME + ".pluginsdk.model.m";//打招呼
    public static String BaseRequestBean = PACKAGE_NAME + ".v.k";
    public static String NEARBY_CALLBACK_METHOD = "a";
    public static String NEARBY_PROTOCAL = ".protocal.c.afk";
    public static String NEARBY_PROTOCAL_POSITION = ".mm.plugin.nearby.a.d";
    public static String NEARBY_PROTOCAL_POSITION2 = ".mm.plugin.nearby.a.c";

    public static void init(String version) {
        switch (version) {
            case "6.3.32":
                //message
                conversationClass = PACKAGE_NAME + ".e.b.ag";
                con_GetCursorMethod = "b";
                con_MessageClass1 = PACKAGE_NAME + ".ab.a";
                con_NetworkMethod = "a";//a
                con_MessageClass = PACKAGE_NAME + ".modelmulti.i";
                activity_chatingui = PACKAGE_NAME + ".ui.chatting.ChattingUI";
                activity_chatingui_a = activity_chatingui + ".a";
                networkRequest = PACKAGE_NAME + ".model.ak";
                requestMethod = "vw";

                SayHiModel = PACKAGE_NAME + ".pluginsdk.model.m";
                BaseRequestBean = PACKAGE_NAME + ".v.k";
                NEARBY_PROTOCAL = ".protocal.c.afk";
                NEARBY_PROTOCAL_POSITION = ".mm.plugin.nearby.a.d";
                NEARBY_PROTOCAL_POSITION2 = ".mm.plugin.nearby.a.c";

                con_ImageClass = PACKAGE_NAME + "af.k"; //NetSceneUploadMsgImg
                break;
            case "6.5.7":
                //message
                conversationClass = PACKAGE_NAME + ".e.b.ag";
                con_GetCursorMethod = "b";
                con_MessageClass1 = PACKAGE_NAME + ".ab.a";
                con_NetworkMethod = "a";//a
                con_MessageClass = PACKAGE_NAME + ".modelmulti.i";
                activity_chatingui = PACKAGE_NAME + ".ui.chatting.ChattingUI";
                activity_chatingui_a = activity_chatingui + ".a";
                networkRequest = PACKAGE_NAME + ".model.ak";
                requestMethod = "vw";
                break;
            case "6.5.10": //Chatting UI是com.tencent.mm.ui.chatting.En_5b8fbb1e
                conversationClass = PACKAGE_NAME + ".e.b.ai";
                con_GetCursorMethod = "b";
                con_ImageClass = PACKAGE_NAME + "af.k"; //NetSceneUploadMsgImg
                con_MessageClass = PACKAGE_NAME + ".modelmulti.j";

                con_MessageClass1 = PACKAGE_NAME + ".ab.a";
                con_NetworkMethod = "a";//a
                activity_chatingui = PACKAGE_NAME + ".ui.chatting.En_5b8fbb1e";
                activity_chatingui_a = activity_chatingui + ".a";
                networkRequest = PACKAGE_NAME + ".model.ak";
                requestMethod = "vw";
                break;
            case "6.6.6":
                conversationClass = PACKAGE_NAME + ".g.c.ak";
                con_GetCursorMethod = "c";
                con_ImageClass = PACKAGE_NAME + "am.l";
                break;

            case "6.6.7":
                conversationClass = PACKAGE_NAME + ".g.c.am";
                con_GetCursorMethod = "d";
               // con_ImageClass = PACKAGE_NAME + "am.l";
                break;

            case "6.7.2":
                conversationClass = PACKAGE_NAME + ".h.c.ap";
                con_GetCursorMethod = "d";
                // con_ImageClass = PACKAGE_NAME + "am.l";
                break;
        }
    }
}
