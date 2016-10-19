package com.atguigu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.util.MsUtils;
import com.atguigu.mobilesafe.util.SpUtils;

/**
 * 设置页面2
 */
public class SetUp2Activity extends Activity {
    private CheckBox cb_setup2;
    private String simNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up2);

        cb_setup2 = (CheckBox)findViewById(R.id.cb_setup2);

        // 设置SIM卡
        String simNum = SpUtils.getInstance(this).getString(SpUtils.SIM_NUM, null);
        if(simNum != null) {
            cb_setup2.setChecked(true);
            cb_setup2.setTextColor(Color.BLACK);
            cb_setup2.setText("SIM卡已绑定");
        } else {
            cb_setup2.setChecked(false);
            cb_setup2.setTextColor(Color.RED);
            cb_setup2.setText("没有绑定SIM卡");
        }

        // 监听SIM绑定
        cb_setup2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    simNumber = MsUtils.getSimNumber(SetUp2Activity.this);
                    cb_setup2.setTextColor(Color.BLACK);
                    cb_setup2.setText("SIM卡已绑定");
                    // 保存
                    SpUtils.getInstance(SetUp2Activity.this).save(SpUtils.SIM_NUM,simNumber);
                }else {
                    cb_setup2.setTextColor(Color.RED);
                    cb_setup2.setText("没有绑定SIM卡");
                    SpUtils.getInstance(SetUp2Activity.this).remove(SpUtils.SIM_NUM);
                }
            }
        });

    }

    public void next(View v) {
        if(!cb_setup2.isChecked()) {
            ///提示SIM卡必须绑定
            MsUtils.showMsg(this,"SIM卡必须绑定");
            return;
        }

        Intent intent = new Intent(SetUp2Activity.this, SetUp3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }

    public void previous(View v) {
        Intent intent = new Intent(SetUp2Activity.this, SetUp1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
    }
}
