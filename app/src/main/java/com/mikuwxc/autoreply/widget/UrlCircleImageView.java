package com.mikuwxc.autoreply.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.mikuwxc.autoreply.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by ccheng on 1/18/15.
 */
public class UrlCircleImageView extends CircleImageView {
    private int mScreenWidth;
    String picUrl;

    public UrlCircleImageView(Context context) {
        super(context);
        if (!isInEditMode()) {
            init();
        }
    }

    public UrlCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init();
        }
    }

    private void init() {
        mScreenWidth = ((Activity) getContext()).getResources().getDisplayMetrics().widthPixels;
    }

    public void loadUrl(String url) {
        url = extractUrl(url);
        if (picUrl == null || picUrl != url) {
            DisplayImageOptions options = getDisplayImageOptions();
            ImageLoader.getInstance().displayImage(url, this, options);
        }
    }

    private String extractUrl(String ur) {
        if (ur == null) {
            return null;
        }
        return ur.split(";")[0];
    }

    private DisplayImageOptions getDisplayImageOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.pic_rotating)
                .showImageOnFail(R.drawable.pic_rotating)
                .showImageForEmptyUri(R.drawable.pic_rotating)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .resetViewBeforeLoading(true)
                .build();
    }

    public void loadUrlAndResize(String pic) {
        int screenWidth = mScreenWidth;
        loadUrlAndResize(pic, screenWidth);
    }

    public void loadUrlAndResize(String pic, final int width) {
        pic = extractUrl(pic);
        DisplayImageOptions options = getDisplayImageOptions();
        ImageLoader.getInstance().displayImage(pic, this, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap != null) {
                    float height = 0;
                    height = ((float) bitmap.getHeight()) / bitmap.getWidth() * width;
                    view.getLayoutParams().width = width;
                    view.getLayoutParams().height = (int) height;
                    view.requestLayout();
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });
    }

    public void loadUrlAndResize(String pic, final int width, final int height) {
        pic = extractUrl(pic);
        DisplayImageOptions options = getDisplayImageOptions();
        ImageLoader.getInstance().displayImage(pic, this, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap != null) {
                    // 计算比例
                    float scaleX = (float) width / bitmap.getWidth();// 宽的比例
                    float scaleY = (float) height / bitmap.getHeight();// 高的比例
                    //新的宽高
                    int newW = 0;
                    int newH = 0;
                    if (scaleX > scaleY) {
                        newW = (int) (bitmap.getWidth() * scaleX);
                        newH = (int) (bitmap.getHeight() * scaleX);
                    } else if (scaleX <= scaleY) {
                        newW = (int) (bitmap.getWidth() * scaleY);
                        newH = (int) (bitmap.getHeight() * scaleY);
                    }
                    ((ImageView) view).setImageBitmap(Bitmap.createScaledBitmap(bitmap, newW, newH, true));
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });
    }
}
