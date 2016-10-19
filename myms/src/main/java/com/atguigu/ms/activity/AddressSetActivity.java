package com.atguigu.ms.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.util.SpUtils;

// 来电显示位置设置页面
public class AddressSetActivity extends Activity implements View.OnTouchListener {
    private TextView tv_address_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_set);

        // 获取控件对象
        tv_address_set = (TextView)findViewById(R.id.tv_address_set);

        // 回显位置
        int upleft = SpUtils.getInstance(this).get(SpUtils.UPLEFT,-1);
        int uptop = SpUtils.getInstance(this).get(SpUtils.UPTOP,-1);

        if(upleft != -1 ){
            // 获取出来
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tv_address_set.getLayoutParams();
            // 修改
            layoutParams.leftMargin = upleft;
            layoutParams.topMargin = uptop;
            // 保存回去
            tv_address_set.setLayoutParams(layoutParams);
        }

        // 回显背景
        int index = SpUtils.getInstance(this).get(SpUtils.STYLE_INDEX,0);
        int[] icons = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_green,R.drawable.call_locate_blue,R.drawable.call_locate_gray};
        tv_address_set.setBackgroundResource(icons[index]);

        // 监听touch事件
        tv_address_set.setOnTouchListener(this);
    }

    // 上一个点坐标
    private int mLastX;
    private int mLastY;
    private int mMaxRight;
    private int mMaxBottom;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 最新点的坐标
        int eventX = (int) event.getRawX();
        int eventY = (int) event.getRawY();

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:// 按下
                if(mMaxBottom == 0) {
                    RelativeLayout parent = (RelativeLayout) tv_address_set.getParent();
                    mMaxRight = parent.getRight();
                    mMaxBottom = parent.getBottom();
                }

                mLastX = eventX;
                mLastY = eventY;
                break;

            case MotionEvent.ACTION_MOVE:// 移动
                // 移动的距离
                int dx = eventX - mLastX;
                int dy = eventY - mLastY;

                int left = tv_address_set.getLeft() + dx;
                int top = tv_address_set.getTop() +dy;
                int right = tv_address_set.getRight() + dx;
                int bottom = tv_address_set.getBottom() + dy;

                // 限制一下
                if(left < 0) {
                    right = right -left;
                    left = 0;
                }

                if(top < 0) {
                    bottom = bottom - top;
                    top = 0;
                }

                if(right > mMaxRight) {
                    left = left - (right - mMaxRight);
                    right = mMaxRight;
                }

                if(bottom > mMaxBottom) {
                    top = top - (bottom - mMaxBottom);
                    bottom = mMaxBottom;
                }

                // 更新显示
                tv_address_set.layout(left,top,right,bottom);

                //
                mLastX = eventX;
                mLastY = eventY;
                break;
            case MotionEvent.ACTION_UP:// 抬起
                // 保存位置
                int upleft = tv_address_set.getLeft();
                int uptop = tv_address_set.getTop();

                SpUtils.getInstance(AddressSetActivity.this).save(SpUtils.UPLEFT,upleft);
                SpUtils.getInstance(AddressSetActivity.this).save(SpUtils.UPTOP,uptop);
                break;
        }



        return true;
    }
}
