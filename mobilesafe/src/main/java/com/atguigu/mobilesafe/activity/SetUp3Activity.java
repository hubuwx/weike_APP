package com.atguigu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.util.MsUtils;
import com.atguigu.mobilesafe.util.SpUtils;

/**
 * 设置页面3
 */
public class SetUp3Activity extends Activity {
    private EditText et_setup3_safenum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up3);

        et_setup3_safenum = (EditText) findViewById(R.id.et_setup3_safenum);

        // 获取保存的安全号码
        String safeNum = SpUtils.getInstance(this).getString(SpUtils.SAFE_NUM, null);
        if (safeNum != null) {
            et_setup3_safenum.setText(safeNum);
        }
    }

    // 获取联系人电话号码
    public void showContactList(View v) {
        Intent intent = new Intent(SetUp3Activity.this, ContactListActivity.class);
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK) {
            String number = data.getStringExtra("NUMBER");
            et_setup3_safenum.setText(number);
        }
    }

    public void next(View v) {
        String safenum = et_setup3_safenum.getText().toString().trim();
        if ("".equals(safenum)) {
            // 提示
            MsUtils.showMsg(this, "安全密码不能空");
            return;
        }

        // 保存安全密码
        SpUtils.getInstance(this).save(SpUtils.SAFE_NUM, safenum);

        Intent intent = new Intent(SetUp3Activity.this, SetUp4Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void previous(View v) {
        Intent intent = new Intent(SetUp3Activity.this, SetUp2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
