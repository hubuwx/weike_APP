package layout.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * 作者：杨光福 on 2016/4/15 15:30
 * 微信：yangguangfu520
 * QQ号：541433511
 * item的正常显示
 * 1.得到两个子View的实例（conentView,menuView） --onFinishInflate();
 * 2.得到两个子View的宽和高(contentWidth,menuWidth,viewHeight) - onMeasure();
 * 3.指定menuView的位置-onLayout
 *
 * 解决item滑动后不能自动打开和关闭
 * 原因是：ListView拦截item(SlideLayout)的事件，父层View拦截子View的事件
 * 1.计算滑动的方向，如果是水平方向滑动就 ，反拦截 把事件给当前控件
 *  DX > DY > 8
 *   反拦截
 * 内容视图设置点击事件时不能滑动item
 * 原因：子View把事件给消费了，拦截
 *问题2.点击事件消费了事件
 * 解决：拦截
 */
public class SlideLayout extends FrameLayout {

    private View conentView;
    private View menuView;

    private int contentWidth,menuWidth,viewHeight;

    private Scroller scroller;

    public SlideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context);
    }

    /**
     * 1.得到两个子View的实例（conentView,menuView） --onFinishInflate();
     * 当加载布局文件完成的时候回调这个方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        conentView = getChildAt(0);
        menuView = getChildAt(1);
    }

    /**
     * 2.得到两个子View的宽和高(contentWidth,menuWidth,viewHeight) - onMeasure();
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        contentWidth = getMeasuredWidth();//可以写
        contentWidth = conentView.getMeasuredWidth();

        menuWidth = menuView.getMeasuredWidth();
//        menuWidth = getMeasuredWidth();//不可以写

        viewHeight = getMeasuredHeight();


    }

    /**
     * 3.指定menuView的位置-onLayout
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        menuView.layout(contentWidth,0,contentWidth+menuWidth,viewHeight);
    }

    private float startX;
    private float startY;
    private float donwX;
    private float downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //1.记录按下的坐标
                donwX =startX = event.getX();
                downY =startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //2.来到新的坐标
                float endX = event.getX();
                float endY = event.getY();
                //3.计算偏移量
                float distanceX = endX - startX;
                //4.转换为scrollTo识别的坐标
                int toScrollX = (int) (getScrollX() - distanceX);

                //屏蔽非法值[0,menuWidth]
                if(toScrollX < 0){
                    toScrollX = 0;
                }else if(toScrollX > menuWidth){
                    toScrollX = menuWidth;
                }
                scrollTo(toScrollX,getScrollY());

                //5.重新赋值
                startX = event.getX();

                //计算在水平方向和竖直方向移动的距离
                float DX = Math.abs(endX - donwX);
                float DY = Math.abs(endY - downY);
                if(DX > DY && DX >8){
                    //把事件传递给itemt
                    getParent().requestDisallowInterceptTouchEvent(true);
                }

                break;
            case MotionEvent.ACTION_UP:

                int totalScrollX =  getScrollX();

                if(totalScrollX < menuWidth/2){
                    //关闭菜单-平滑的关闭
                    closeMenu();
                }else{
                    //打开菜单--平滑的打开
                    openMenu();
                }


                break;
        }
        return true;
    }

    private void openMenu() {
        //
        int distanceX = (menuWidth-getScrollX());
        Log.e("distanceX","distanceX=="+distanceX);
        scroller.startScroll(getScrollX(), getScrollY(), menuWidth-getScrollX(),0);
        invalidate();//computeScroll
        if(onStateChangeListener != null){
            onStateChangeListener.onOpen(this);
        }
    }

    public void closeMenu() {//0

        scroller.startScroll(getScrollX(), getScrollY(), 0 - getScrollX(), 0);
        invalidate();//computeScroll

        if(onStateChangeListener != null){
            onStateChangeListener.onClose(this);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(scroller.computeScrollOffset()){
            scrollTo(scroller.getCurrX(),scroller.getCurrY());
            invalidate();
        }
    }

    /**
     * 当返回true的时候，事件拦截，孩子没有事件；但是会触发当层视图的onTouchEvent()
     * 当防护flase的时候，事件不拦截，孩子有事件，单不会触发当层视图的onTouchEvent()
     * @param event
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercept = false;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //1.记录按下的坐标
                donwX =startX = event.getX();
                downY =startY = event.getY();
                if(onStateChangeListener != null){
                    onStateChangeListener.onDown(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //2.来到新的坐标
                float endX = event.getX();
                float endY = event.getY();

                //计算在水平方向和竖直方向移动的距离
                float DX = Math.abs(endX - donwX);
                if( DX >8){
                    intercept =true;
                }

                break;
        }

        return intercept;
    }

    public interface OnStateChangeListener{

        /**
         当侧滑菜单关闭的时候被回调
         */
        public void onClose(SlideLayout layout);

        /**
         当侧滑菜单打开的时候被回调
         */
        public void onOpen(SlideLayout layout);

        /**
         当侧滑菜单按下的时候被回调
         */
        public void onDown(SlideLayout layout);

    }

    private OnStateChangeListener onStateChangeListener;

    public void setOnStateChangeListener(OnStateChangeListener l){

        this.onStateChangeListener = l;

    }
}
