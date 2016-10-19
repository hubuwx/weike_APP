package com.atguigu.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.util.SpUtils;

// 电话归属地位置设置
public class AddressSetActivity extends Activity implements View.OnTouchListener {
    private TextView tv_address_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_set);

        // 获取控件对象
        tv_address_set = (TextView) findViewById(R.id.tv_address_set);

        // 回显位置
        int upleft = SpUtils.getInstance(this).get(SpUtils.UP_LEFT, -1);
        int uptop = SpUtils.getInstance(this).get(SpUtils.UP_TOP, -1);

        if (upleft != -1) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv_address_set.getLayoutParams();
            params.leftMargin = upleft;
            params.topMargin = uptop;
            tv_address_set.setLayoutParams(params);
        }

        // 背景图片的回显
        int index = SpUtils.getInstance(this).get(SpUtils.STYLE_INDEX, 0);
        int[] icons = {R.drawable.call_locate_white, R.drawable.call_locate_orange, R.drawable.call_locate_green, R.drawable.call_locate_blue, R.drawable.call_locate_gray};
        tv_address_set.setBackgroundResource(icons[index]);

        // 设置触摸事件
        tv_address_set.setOnTouchListener(this);
    }

    private int lastX;
    private int lastY;
    private int maxRight;
    private int maxBottom;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int eventX = (int) event.getRawX();
        int eventY = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (maxBottom == 0) {
                    RelativeLayout parent = (RelativeLayout) tv_address_set.getParent();
                    maxRight = parent.getRight();
                    maxBottom = parent.getBottom();
                }

                lastX = eventX;
                lastY = eventY;
                break;

            case MotionEvent.ACTION_MOVE:
                int dx = eventX - lastX;
                int dy = eventY - lastY;

                // 获取移动后的宽高
                int left = tv_address_set.getLeft() + dx;
                int top = tv_address_set.getTop() + dy;
                int right = tv_address_set.getRight() + dx;
                int bottom = tv_address_set.getBottom() + dy;

                // 限制宽高
                if (left < 0) {
                    right = right - left;
                    left = 0;
                }

                if (top < 0) {
                    bottom = bottom - top;
                    top = 0;
                }

                if (right > maxRight) {
                    left = left - (right - maxRight);
                    right = maxRight;
                }

                if (bottom > maxBottom) {
                    top = top - (bottom - maxBottom);
                    bottom = maxBottom;
                }
                // 绘制
                tv_address_set.layout(left, top, right, bottom);

                lastX = eventX;
                lastY = eventY;
                break;

            case MotionEvent.ACTION_UP:
                int upleft = tv_address_set.getLeft();
                int uptop = tv_address_set.getTop();

                // 保存
                SpUtils.getInstance(AddressSetActivity.this).save(SpUtils.UP_LEFT, upleft);
                SpUtils.getInstance(AddressSetActivity.this).save(SpUtils.UP_TOP, uptop);
                break;
        }

        return true;
    }
}
