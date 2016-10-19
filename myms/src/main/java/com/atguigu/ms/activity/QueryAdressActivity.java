package com.atguigu.ms.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.dao.AddressDao;

// 查询归属地
public class QueryAdressActivity extends Activity implements View.OnClickListener {
    private EditText et_query_address;
    private Button bt_query_address;
    private TextView tv_query_address;
    private AddressDao mAddressDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_adress);

        // 获取控件对象
        et_query_address = (EditText)findViewById(R.id.et_query_address);
        bt_query_address = (Button)findViewById(R.id.bt_query_address);
        tv_query_address = (TextView)findViewById(R.id.tv_query_address);

        // 监听事件处理
        bt_query_address.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_query_address:
                // 获取输入的号码
                String number = et_query_address.getText().toString().trim();
                if("".equals(number)) {
                    // 为空 抖动
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
                    et_query_address.startAnimation(animation);
                }else {
                    // 不为空 去查询数据库
                    mAddressDao = new AddressDao(QueryAdressActivity.this);
                    String address = mAddressDao.getAddress(number);
                    // 更新下显示
                    tv_query_address.setText(address);
                }
                break;
        }
    }
}
