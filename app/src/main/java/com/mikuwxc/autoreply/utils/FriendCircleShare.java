package com.mikuwxc.autoreply.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;


import com.mikuwxc.autoreply.StaticData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/26.
 */
public class FriendCircleShare {
    private static final String TAG = FriendCircleShare.class.getSimpleName();
    private final Context context;
    private ProgressDialog myProgressDialog;
    private List<File> shareFileList = new ArrayList<>();

    public FriendCircleShare(Context context) {
        this.context = context;
    }

    /**
     * 分享（支持多图片分享)
     *
     * @param imageUrlList
     */
    public void newStartShare(String content, String[] imageUrlList) {
        if (imageUrlList == null) {
            return;
        }
//        if (myProgressDialog == null) {
//            myProgressDialog = new ProgressDialog(context);
//        }
//        myProgressDialog.show();
//        myProgressDialog.setMessage(context.getResources().getString(R.string.imageDownwaiting));

        shareFileList.clear();
        ImageUtils.fileCache.clear();
        downPic(imageUrlList[0], content, imageUrlList);
//        for (String path : imageUrlList) {
//        }
    }

    private void downPic(String path, final String content, final String[] imageUrlList) {
        try {
            ImageUtils.getImageForUrl(path, context, new ImageUtils.OnLoadListener() {
                @Override
                public void onSuccess(File file) {
                    shareFileList.add(file);
                    int i = shareFileList.size() - 1;
                    if (shareFileList.size() == imageUrlList.length) {
                        if (StaticData.isFriendCircleSuccess) {//10秒 图片未下完，则任务失败
                            return;
                        }
                        Log.e("111","分享");
                        startShare(content, shareFileList, context);
                    } else {
                        Log.e("111","下载");
                        downPic(imageUrlList[i + 1], content, imageUrlList);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param fileList
     * @param context
     */
    private void startShare(String content, List<File> fileList, Context context) {
        String mPackageName = "";
        String mActivityName = "";
        mPackageName = "com.tencent.mm";
        mActivityName = "com.tencent.mm.ui.tools.ShareToTimeLineUI";

        ArrayList<Uri> uriList = new ArrayList<>();
        for (File file : fileList) {
            uriList.add(Uri.fromFile(file));
        }
        try {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.setClassName(mPackageName, mActivityName);
            ArrayList<CharSequence> contentList = new ArrayList<>();
            Log.e("111",content);
            contentList.add(content);
            shareIntent.putExtra(Intent.EXTRA_TEXT, contentList);
            //shareIntent.putExtra("Kdescription", content);因为没root的手机好像重启后会获取不到，所以采取粘贴剪切板的方式在AutoReplyService.completeMessage方法中实现
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriList);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
            shareIntent.setType("image/*");
            context.startActivity(Intent.createChooser(shareIntent, "Share images to.."));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public final static String WEIXIN_CHATTING_MIMETYPE = "vnd.android.cursor.item/vnd.com.tencent.mm.chatting.profile";//微信聊天
    public final static String WEIXIN_SNS_MIMETYPE = "vnd.android.cursor.item/vnd.com.tencent.mm.plugin.sns.timeline";//微信朋友圈
    public final static String WEIXIN_VIDIO_MIMETYPE = "vnd.android.cursor.item/vnd.com.tencent.mm.chatting.voip.video";//微信视频

    /**
     * 进去聊天界面
     *
     * @param context
     * @param phone   手机号码
     */
    public static void shareToFriend(Context context, String phone) {
        int id = getChattingID(context, phone, WEIXIN_CHATTING_MIMETYPE);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.withAppendedPath(
                ContactsContract.Data.CONTENT_URI, String.valueOf(id)),
                WEIXIN_CHATTING_MIMETYPE);
        context.startActivity(intent);
    }

    /**
     * 朋友圈
     *
     * @param context
     * @param id
     */
    public static void shareToTimeLine(Context context, int id) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.withAppendedPath(
                ContactsContract.Data.CONTENT_URI, String.valueOf(id)),
                WEIXIN_SNS_MIMETYPE);
        context.startActivity(intent);
    }


    /**
     * 根据电话号码查询微信id
     **/
    public static int getChattingID(Context context, String querymobile, String mimeType) {
        if (context == null || querymobile == null || querymobile.equals("")) {
            return 0;
        }
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://com.android.contacts/data");
        StringBuilder sb = new StringBuilder();
        sb.append(ContactsContract.Contacts.Data.MIMETYPE).append(" = ").append("'");
        sb.append(mimeType).append("'");
        sb.append(" AND ").append("replace(data1,' ','')").append(" = ").append("'").append(querymobile).append("'");
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Contacts.Data._ID}, sb.toString(), null, null);
        while (cursor.moveToNext()) {
            int wexin_id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.Data._ID));
            return wexin_id;
        }
        cursor.close();
        return 0;
    }
}
