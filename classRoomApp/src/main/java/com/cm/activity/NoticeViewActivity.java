package com.cm.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cm.bean.tb_notices;
import com.google.gson.Gson;
import com.miebo.utils.BaseActivity;

/**
 * 
 * @author jinzhao
 * 
 */
public class NoticeViewActivity extends BaseActivity {
	private TextView tvTopTitleCenter;
	private Button btnTopTitleRight;
	private TextView tvTitle, tvTypeName, tvBody;
	private int id = 0;
	private List<tb_notices> list;
	private final Gson gson = new Gson();
	private tb_notices model;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_noticeview);
		model = (tb_notices) getIntent().getSerializableExtra("model");
		if (model != null) {
			id = model.getId();
		}
		findview();
		if (id != 0) {
			// new loadAsyncTask().execute();
			tvTitle.setText(model.getTitle());
			tvBody.setText(model.getBody());
			tvTypeName.setText(model.getTypename());
		}
	}

	private void findview() {
		tvTopTitleCenter = (TextView) findViewById(R.id.tvTopTitleCenter);
		btnTopTitleRight = (Button) findViewById(R.id.btnTopTitleRight);
		// btnTopTitleRight.setVisibility(View.VISIBLE);
		btnTopTitleRight.setOnClickListener(this);
		btnTopTitleRight.setText("完成");

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTypeName = (TextView) findViewById(R.id.tvTypeName);
		tvBody = (TextView) findViewById(R.id.tvBody);

		tvTopTitleCenter.setText("详情");
	}

	public void back(View view) {
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btnTopTitleRight:// 完成按钮

			break;
		default:
			break;
		}

	}

}
