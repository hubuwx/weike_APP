package com.atguigu.ms.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.util.SpUtils;
// 防盗保护详情页面
public class ProtectInfoActivity extends Activity {
    private TextView tv_protect_num;
    private ImageView iv_protect_lock;
    private TextView tv_protect_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protect_info);

        // 获取控件对象
        tv_protect_num = (TextView) findViewById(R.id.tv_protect_num);
        iv_protect_lock = (ImageView) findViewById(R.id.iv_protect_lock);

        tv_protect_reset = (TextView)findViewById(R.id.tv_protect_reset);

        // 回显
        String safeNum =SpUtils.getInstance(this).get(SpUtils.SAFE_NUM,null);
        if(safeNum != null) {
            tv_protect_num.setText(safeNum);
        }

        // lock回显
        boolean isfProtect = SpUtils.getInstance(this).get(SpUtils.PROTECT,false);
        if(isfProtect) {
            iv_protect_lock.setImageResource(R.drawable.lock);
        }else {
            iv_protect_lock.setImageResource(R.drawable.unlock);
        }

//
//        tv_protect_reset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ProtectInfoActivity.this, SetUp1Activity.class);
//                startActivity(intent);
//            }
//        });

    }

    // 重新设置
    public void reset(View v) {
        Intent intent = new Intent(ProtectInfoActivity.this, SetUp1Activity.class);
        startActivity(intent);
    }
}
