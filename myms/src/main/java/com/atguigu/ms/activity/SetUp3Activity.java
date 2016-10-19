package com.atguigu.ms.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.atguigu.ms.R;
import com.atguigu.ms.util.MsUtils;
import com.atguigu.ms.util.SpUtils;

public class SetUp3Activity extends Activity {
    private EditText et_setup3_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up3);

        // 获取控件对象
        et_setup3_num = (EditText) findViewById(R.id.et_setup3_num);

        // 回显
        String safeNum = SpUtils.getInstance(this).get(SpUtils.SAFE_NUM, null);
        if (safeNum != null) {
            et_setup3_num.setText(safeNum);
        }
    }

    // 上一步
    public void previous(View v) {
        Intent intent = new Intent(SetUp3Activity.this, SetUp2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    // 下一步
    public void next(View v) {
        // 必须输入安全号码
        String num = et_setup3_num.getText().toString().trim();
        if ("".equals(num)) {
            // 提示
            MsUtils.showMsg(this, "必须输入安全号码");
            return;
        }

        // 保存安全号码
        SpUtils.getInstance(this).save(SpUtils.SAFE_NUM, num);


        Intent intent = new Intent(SetUp3Activity.this, SetUp4Activity.class);
        startActivity(intent);
        finish();

        // 平移动画
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}
