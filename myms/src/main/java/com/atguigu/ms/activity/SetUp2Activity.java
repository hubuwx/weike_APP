package com.atguigu.ms.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.atguigu.ms.R;
import com.atguigu.ms.util.MsUtils;
import com.atguigu.ms.util.SpUtils;

public class SetUp2Activity extends Activity {
    private CheckBox cb_setup2_sim;
    private String mSimNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up2);

        // 获取控件对象
        cb_setup2_sim = (CheckBox)findViewById(R.id.cb_setup2_sim);

        mSimNum = MsUtils.getSimNum(this);

        // 回显设置
        String simNum = SpUtils.getInstance(this).get(SpUtils.SIM_NUM,null);
        if(simNum != null) {
            cb_setup2_sim.setText("SIM卡已绑定");
            cb_setup2_sim.setTextColor(Color.BLACK);
            cb_setup2_sim.setChecked(true);
        }


        // 监听点击事件
        cb_setup2_sim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    cb_setup2_sim.setText("SIM卡已绑定");
                    cb_setup2_sim.setTextColor(Color.BLACK);

                    SpUtils.getInstance(SetUp2Activity.this).save(SpUtils.SIM_NUM, mSimNum);

                }else {
                    cb_setup2_sim.setText("SIM卡未绑定");
                    cb_setup2_sim.setTextColor(Color.RED);

                    SpUtils.getInstance(SetUp2Activity.this).remove(SpUtils.SIM_NUM);
                }
            }
        });
    }

    // 上一步
    public void previous(View v) {
        Intent intent = new Intent(SetUp2Activity.this, SetUp1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
    }

    // 下一步
    public void next(View v) {

        // 不绑定SIM不不让退出
        if(!cb_setup2_sim.isChecked()) {
            // 提示
            MsUtils.showMsg(this,"必须绑定SIM卡");
            return;
        }


        Intent intent = new Intent(SetUp2Activity.this, SetUp3Activity.class);
        startActivity(intent);
        finish();

        // 平移动画
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }
}
