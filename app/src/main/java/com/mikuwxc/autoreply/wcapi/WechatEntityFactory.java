package com.mikuwxc.autoreply.wcapi;


import com.mikuwxc.autoreply.wcentity.WechatEntity;

public class WechatEntityFactory {
    private static WechatEntity current;

    public static WechatEntity create(android.content.Context r3) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:com.ac.wechat.api.WechatEntityFactory.create(android.content.Context):com.ac.wechat.entity.WechatEntity. bs: [B:3:0x0005, B:13:0x0016]
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:86)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
*/
        /*
        r0 = com.ac.wechat.api.WechatEntityFactory.class;
        monitor-enter(r0);
        if (r3 != 0) goto L_0x0012;
    L_0x0005:
        r0 = new java.lang.IllegalArgumentException;	 Catch:{ all -> 0x000d }
        r1 = "context can not be null";	 Catch:{ all -> 0x000d }
        r0.<init>(r1);	 Catch:{ all -> 0x000d }
        throw r0;	 Catch:{ all -> 0x000d }
    L_0x000d:
        r0 = move-exception;
        r1 = com.ac.wechat.api.WechatEntityFactory.class;
        monitor-exit(r1);
        throw r0;
    L_0x0012:
        r0 = current;	 Catch:{ all -> 0x000d }
        if (r0 != 0) goto L_0x0022;
    L_0x0016:
        r0 = com.ac.wechat.util.GlobalUtil.WX_PM;	 Catch:{ Throwable -> 0x0028 }
        r0 = com.ac.wechat.util.AppUtil.getAppVersion(r3, r0);	 Catch:{ Throwable -> 0x0028 }
        r0 = create(r0);	 Catch:{ Throwable -> 0x0028 }
        current = r0;	 Catch:{ Throwable -> 0x0028 }
    L_0x0022:
        r0 = current;	 Catch:{ all -> 0x000d }
        r1 = com.ac.wechat.api.WechatEntityFactory.class;
        monitor-exit(r1);
        return r0;
    L_0x0028:
        r0 = move-exception;
        r1 = new java.lang.RuntimeException;	 Catch:{ all -> 0x000d }
        r2 = r0.getMessage();	 Catch:{ all -> 0x000d }
        r1.<init>(r2, r0);	 Catch:{ all -> 0x000d }
        throw r1;	 Catch:{ all -> 0x000d }
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ac.wechat.api.WechatEntityFactory.create(android.content.Context):com.ac.wechat.entity.WechatEntity");
    }

    public static WechatEntity create(String str) {
        WechatEntity wechatEntity6513;
        Object obj = -1;
        switch (str.hashCode()) {
            case 51293888:
                if (str.equals("6.6.0")) {
                    obj = 7;
                    break;
                }
                break;
            case 51293889:
                if (str.equals("6.6.1")) {
                    obj = 8;
                    break;
                }
                break;
            case 51293890:
                if (str.equals("6.6.2")) {
                    obj = 9;
                    break;
                }
                break;
            case 51293891:
                if (str.equals("6.6.3")) {
                    obj = 10;
                    break;
                }
                break;
            case 51293893:
                if (str.equals("6.6.5")) {
                    obj = 11;
                    break;
                }
                break;
            case 51293894:
                if (str.equals("6.6.6")) {
                    obj = 12;
                    break;
                }
                break;
            case 51293895:
                if (str.equals("6.6.7")) {
                    obj = 13;
                    break;
                }
                break;
            case 51294849:
                if (str.equals("6.7.0")) {
                    obj = 14;
                    break;
                }
                break;
            case 1590021297:
                if (str.equals("6.3.31")) {
                    obj = null;
                    break;
                }
                break;
            case 1590080819:
                if (str.equals("6.5.13")) {
                    obj = 1;
                    break;
                }
                break;
            case 1590080820:
                if (str.equals("6.5.14")) {
                    obj = 2;
                    break;
                }
                break;
            case 1590080822:
                if (str.equals("6.5.16")) {
                    obj = 3;
                    break;
                }
                break;
            case 1590080825:
                if (str.equals("6.5.19")) {
                    obj = 4;
                    break;
                }
                break;
            case 1590080849:
                if (str.equals("6.5.22")) {
                    obj = 5;
                    break;
                }
                break;
            case 1590080850:
                if (str.equals("6.5.23")) {
                    obj = 6;
                    break;
                }
                break;
        }
        switch (Integer.parseInt(obj.toString())) {
            case 0:
                wechatEntity6513 = new WechatEntity6513();
                break;
            case 1:
                wechatEntity6513 = new WechatEntity6513();
                break;
            case 2:
                wechatEntity6513 = new WechatEntity6514();
                break;
            case 3:
                wechatEntity6513 = new WechatEntity6516();
                break;
            case 4:
                wechatEntity6513 = new WechatEntity6519();
                break;
            case 5:
                wechatEntity6513 = new WechatEntity6522();
                break;
            case 6:
                wechatEntity6513 = new WechatEntity6523();
                break;
            case 7:
                wechatEntity6513 = new WechatEntity6600();
                break;
            case 8:
                wechatEntity6513 = new WechatEntity6601();
                break;
            case 9:
                wechatEntity6513 = new WechatEntity6602();
                break;
            case 10:
                wechatEntity6513 = new WechatEntity6603();
                break;
            case 11:
                wechatEntity6513 = new WechatEntity6605();
                break;
            case 12:
                wechatEntity6513 = new WechatEntity6606();
                break;
            case 13:
                wechatEntity6513 = new WechatEntity6607();
                break;
            case 14:
                wechatEntity6513 = new WechatEntity6700();
                break;
            default:
                wechatEntity6513 = new WechatEntityDefault();
                break;
        }
        if (str.startsWith("6.5.7")) {
            wechatEntity6513.sqlitedatabase_class_name = "com.tencent.mmdb.database.SQLiteDatabase";
            wechatEntity6513.cancellationsignal_class_name = "com.tencent.mmdb.support.CancellationSignal";
            wechatEntity6513.sqlitecipherspec_class_name = "com.tencent.mmdb.database.SQLiteCipherSpec";
            wechatEntity6513.sqlitedatabase$cursorfactory_class_name = "com.tencent.mmdb.database.SQLiteDatabase$CursorFactory";
            wechatEntity6513.databaseerrorhandler_class_name = "com.tencent.mmdb.DatabaseErrorHandler";
        }
        return wechatEntity6513;
    }
}