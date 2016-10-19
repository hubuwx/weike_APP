package com.atguigu.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.dao.AddressDao;

/**
 * 查询联系人归属地
 */
public class QueryAddressActivity extends Activity implements View.OnClickListener {
    private EditText et_query_address;
    private Button bt_query_address;
    private TextView tv_query_address;
    private AddressDao mAddressDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);
        
        // 获取控件对象
        et_query_address = (EditText)findViewById(R.id.et_query_address);
        bt_query_address = (Button)findViewById(R.id.bt_query_address);
        tv_query_address = (TextView)findViewById(R.id.tv_query_address);

        mAddressDao = new AddressDao(this);
        bt_query_address.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.bt_query_address) {
            // 获取输入的电话号码
            String phoneNum = et_query_address.getText().toString().trim();
            // 查询
            String location = mAddressDao.getLocation(phoneNum);
            // 更新显示
            tv_query_address.setText(location);
        }
    }
}
