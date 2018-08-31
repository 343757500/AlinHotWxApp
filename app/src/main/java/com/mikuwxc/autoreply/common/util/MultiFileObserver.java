package com.mikuwxc.autoreply.common.util;

import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Date: 2018/5/18 14:38.
 * Desc:
 */
@SuppressWarnings(value = {"rawtypes", "unchecked"})
public class MultiFileObserver extends FileObserver {
    private static final String TAG = MultiFileObserver.class.getSimpleName();
    public static int CHANGES_ONLY = CREATE | MODIFY | DELETE | CLOSE_WRITE
            | DELETE_SELF | MOVE_SELF | MOVED_FROM | MOVED_TO;

    private List<SingleFileObserver> mObservers;
    private String mPath;
    private int mMask;
    private MessagePathListener mPathListener;
    private List<String> defaultVoiceList = new ArrayList<>();
    private List<String> defaultImgList = new ArrayList<>();
    private List<String> nowriteImgList = new ArrayList<>();
    private List<String> nowriteVideoList = new ArrayList<>();

    public interface MessagePathListener {
        /**
         * 经过文件监听过滤路径
         *
         * @param type   1:voice, 2:send picture, 3:receive picture, 4:send video, 5:receive video
         * @param option
         * @param path
         */
        void addPathToList(int type, int option, String path);
    }

    public MultiFileObserver(String path) {
        this(path, ALL_EVENTS);
    }

    public MultiFileObserver(String path, int mask) {
        super(path, mask);
        mPath = path;
        mMask = mask;
    }

    public void setPathListener(MessagePathListener listener) {
        this.mPathListener = listener;
    }

    @Override
    public void startWatching() {
        if (mObservers != null)
            return;

        mObservers = new ArrayList<SingleFileObserver>();
        Stack<String> stack = new Stack<String>();
        stack.push(mPath);

        while (!stack.isEmpty()) {
            String parent = stack.pop();
            mObservers.add(new SingleFileObserver(parent, mMask));
            File path = new File(parent);
            File[] files = path.listFiles();
            if (null == files)
                continue;
            for (File f : files) {
                if (f.isDirectory() && !f.getName().equals(".")
                        && !f.getName().equals("..")) {
                    stack.push(f.getPath());
                }
            }
        }

        for (int i = 0; i < mObservers.size(); i++) {
            SingleFileObserver sfo = mObservers.get(i);
            sfo.startWatching();
        }
    }

    ;

    @Override
    public void stopWatching() {
        if (mObservers == null)
            return;

        for (int i = 0; i < mObservers.size(); i++) {
            SingleFileObserver sfo = mObservers.get(i);
            sfo.stopWatching();
        }

        mObservers.clear();
        mObservers = null;
    }

    ;


    @Override
    public void onEvent(int event, String path) {
        if (path.endsWith("/null")) return;
        if (path.endsWith("/record")) return;
        if (path.endsWith("/wenote")) return;
        if (path.endsWith("/package")) return;
        if (path.contains("/favorite")) return;
        if (path.contains("/logcat")) return;
        if (path.contains("/locallog")) return;
        if (path.contains("/sns")) return;
        if (path.contains("/emoji")) return;
        if (path.contains("/avatar")) return;
        if (path.contains("/com.tencent.xin.emoticon.person.stiker")) return;
        String checkresupdate = AppConfig.ROOT + "/tencent/MicroMsg/CheckResUpdate";
        if (path.startsWith(checkresupdate))return;
        switch (event) {
            case FileObserver.ACCESS:
//                Log.i(TAG, FileObserver.ACCESS + "-ACCESS: " + path);
                break;
            case FileObserver.ATTRIB:
                Log.i(TAG, FileObserver.ATTRIB + "-ATTRIB: " + path);
                break;
            case FileObserver.CLOSE_NOWRITE:
                if (path.contains("/openapi/") && path.endsWith(".png"))return;
                Log.v(TAG, FileObserver.CLOSE_NOWRITE + "-CLOSE_NOWRITE: " + path);
//                if(path.contains("/WeiXin/")){ //自己发的,即拍即发
//
//                }
                if (path.contains("/image2/")) { //有可能是发送现有图片,th开头的
                    if (!nowriteImgList.contains(path)) {
                        nowriteImgList.add(path);
                        mPathListener.addPathToList(FileObserver.CLOSE_NOWRITE, 2, path);
                    }
                }
                if (path.contains("/video/") && path.endsWith(".mp4")) {//发出的视频
                    if (!nowriteVideoList.contains(path)) {
                        nowriteVideoList.add(path);
                        mPathListener.addPathToList(FileObserver.CLOSE_NOWRITE, 4, path);
                    }
                }
                if (path.contains("/video/") && path.endsWith(".jpg.tmp")) {//收到的视频
                    if (!nowriteVideoList.contains(path)) {
                        nowriteVideoList.add(path);
                        mPathListener.addPathToList(FileObserver.CLOSE_NOWRITE, 5, path);
                    }
                }
                break;
            case FileObserver.CLOSE_WRITE:
                Log.i(TAG, FileObserver.CLOSE_WRITE + "-CLOSE_WRITE: " + path);
                break;
            case FileObserver.CREATE:
                Log.i(TAG, FileObserver.CREATE + "-CREATE: " + path);
                break;
            case FileObserver.DELETE:
                Log.i(TAG, FileObserver.DELETE + "-DELETE: " + path);
                break;
            case FileObserver.DELETE_SELF:
                Log.i(TAG, FileObserver.DELETE_SELF + "-DELETE_SELF: " + path);
                break;
            case FileObserver.MODIFY:
//                Log.i(TAG, FileObserver.MODIFY + "-MODIFY: " + path);
                break;
            case FileObserver.MOVE_SELF:
                Log.i(TAG, FileObserver.MOVE_SELF + "-MOVE_SELF: " + path);
                break;
            case FileObserver.MOVED_FROM:
                Log.i(TAG, FileObserver.MOVED_FROM + "-MOVED_FROM: " + path);
                break;
            case FileObserver.MOVED_TO:
                Log.i(TAG, FileObserver.MOVED_TO + "-MOVED_TO: " + path);
                break;
            case FileObserver.OPEN:
                Log.i(TAG, FileObserver.OPEN + "-OPEN: " + path);
                break;
            default:
                Log.d(TAG, "DEFAULT(" + event + " : " + path);
                if (event == 1073742080 || event == 1073741840 || event == 1073741825) {
                    if (path.contains("/voice2/")) {//收发的语音
                        if (!defaultVoiceList.contains(path)) {
                            defaultVoiceList.add(path);
                            LogUtils.v(TAG, "default path:" + path);
                            mPathListener.addPathToList(event, 1, path);
                        }
                    } else if (path.contains("/image2/") && event == 1073742080) { //收到的图片
                        if (!defaultImgList.contains(path)) {
                            defaultImgList.add(path);
                            mPathListener.addPathToList(1073742080, 3, path);
                        }
                    }
                }
                break;
        }
    }

    public void resetVoiceList() {
        defaultVoiceList = new ArrayList<>();
    }

    class SingleFileObserver extends FileObserver {

        String mPath;

        public SingleFileObserver(String path) {
            this(path, ALL_EVENTS);
            mPath = path;
        }

        public SingleFileObserver(String path, int mask) {
            super(path, mask);
            mPath = path;
        }

        @Override
        public void onEvent(int event, @Nullable String path) {
            String newPath = mPath + "/" + path;
            MultiFileObserver.this.onEvent(event, newPath);
        }
    }
}
