package com.chenluwei.beijingnews.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 作者：杨光福 on 2016/5/5 15:26
 * 微信：yangguangfu520
 * QQ号：541433511
 */
public class HorizontalScrollViewPager extends ViewPager {

    public HorizontalScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 重写dispatchTouchEvent并且要在按下的时候
     //在down的时候
     getParent().requestDisallowInterceptTouchEvent(true);


     //在move的时候的处理

     1.上下滑动，不反拦截事件
     getParent().requestDisallowInterceptTouchEvent(false);


     2.水平方向滑动
     2.1,滑动方向是，从左往右滑动，并且是第0个位置，左侧菜单侧滑出来
     getParent().requestDisallowInterceptTouchEvent(false);

     2.2,滑动方向是，从右往左滑动，并且是第最后一个位置，不反拦截
     getParent().requestDisallowInterceptTouchEvent(false);

     2.3，其他
     getParent().requestDisallowInterceptTouchEvent(true);


     * @param ev
     * @return
     */
    private float startX;
    private float startY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                //1.记录第一次按下的坐标
                startX = ev.getX();
                startY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                //2.来到新的坐标
                float endX = ev.getX();
                float endY = ev.getY();
                //3.计算偏移量
                float distanceX = endX - startX;
                float distanceY = endY - startY;
                //4.判断滑动方向
                if(Math.abs(distanceX) > Math.abs(distanceY)){
                    //水平方向滑动
//                    2.水平方向滑动
//                    2.1,滑动方向是，从左往右滑动，并且是第0个位置，左侧菜单侧滑出来
                    if(getCurrentItem()==0&&distanceX >0){
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
//
//                    2.2,滑动方向是，从右往左滑动，并且是第最后一个位置，不反拦截
//                    getParent().requestDisallowInterceptTouchEvent(false);
                    else if((getCurrentItem()==getAdapter().getCount()-1)&& distanceX <0){
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
//
//                    2.3，其他
//                    getParent().requestDisallowInterceptTouchEvent(true);
                    else{
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }

                }else{
                    //竖直方向滑动
                    getParent().requestDisallowInterceptTouchEvent(false);
                }


                break;

            case MotionEvent.ACTION_UP:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }
}
