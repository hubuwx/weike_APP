package com.atguigu.mobileplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * 作者：杨光福 on 2016/4/22 15:24
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：自定义Videoview
 */
public class VideoView extends android.widget.VideoView {
    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 传入视频的宽和高
     * @param videoWidth
     * @param videoHeight
     */
    public void setVideoSize(int videoWidth,int videoHeight){
        ViewGroup.LayoutParams l=  getLayoutParams();
        l.width = videoWidth;
        l.height = videoHeight;
        setLayoutParams(l);
    }
}
