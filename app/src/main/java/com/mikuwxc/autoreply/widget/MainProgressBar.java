package com.mikuwxc.autoreply.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikuwxc.autoreply.R;


/**
 * Created by Administrator on 2016/8/19.
 */
public class MainProgressBar extends RelativeLayout {

    private ImageView progress;
    private RotateAnimation mRotateAnimation;
    public TextView statusTv;

    public MainProgressBar(Context context) {
        this(context, null);
    }

    public MainProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.main_progress_bar, this);
        progress = (ImageView) view.findViewById(R.id.main_progress);
        statusTv = (TextView) view.findViewById(R.id.status_tv);
        mRotateAnimation = new RotateAnimation(0.0f, 720.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setFillAfter(true);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(2200);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);

    }

    public void startThisAnimation() {
        progress.setAnimation(mRotateAnimation);
    }

    public void stopThisAnimation() {
        progress.clearAnimation();
    }
}
