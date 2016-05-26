package layout.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by lw on 2016/4/25.
 * 为了实现放大缩小等功能而自定义的VideoView
 */
public class VideoView extends android.widget.VideoView {
    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 传入视频的宽和高
     * @param videoWidth
     * @param VideoHeight
     */
    public void setVideoSize(int videoWidth,int VideoHeight){
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = videoWidth;
        params.height = VideoHeight;
        setLayoutParams(params);
    }
}
