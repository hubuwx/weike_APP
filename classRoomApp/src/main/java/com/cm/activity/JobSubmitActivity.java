package com.cm.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cm.bean.tb_jobs;
import com.cm.bean.tb_jobsubmit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miebo.utils.BaseActivity;

/**
 * 
 * @author jinzhao
 * 
 */
public class JobSubmitActivity extends BaseActivity {
	private TextView tvTopTitleCenter;
	private Button btnTopTitleRight;
	private TextView tvTitle;
	private EditText etBody;
	private int id = 0;
	private List<tb_jobsubmit> list;
	private final Gson gson = new Gson();
	private tb_jobs model;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editsubmitjob);
		model = (tb_jobs) getIntent().getSerializableExtra("model");
		if (model != null) {
			id = model.getId();
		}
		findview();
		if (id != 0) {
			new loadAsyncTask().execute();
			tvTitle.setText(model.getTitle());

		}
	}

	private void findview() {
		tvTopTitleCenter = (TextView) findViewById(R.id.tvTopTitleCenter);
		btnTopTitleRight = (Button) findViewById(R.id.btnTopTitleRight);
		btnTopTitleRight.setVisibility(View.VISIBLE);
		btnTopTitleRight.setOnClickListener(this);
		btnTopTitleRight.setText("提交");
		tvTitle = (TextView) findViewById(R.id.tvTitle);

		etBody = (EditText) findViewById(R.id.etBody);

		tvTopTitleCenter.setText("答题");
	}

	private void submit() {

		if (etBody.getText().length() == 0) {
			toastUtil.show("请输入答案");
			return;
		}

		new submitAsyncTask().execute("");
	}

	public void back(View view) {
		finish();
	}

	private class loadAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(JobSubmitActivity.this, "提示",
					"获取中,请稍后..");
		}

		@Override
		protected String doInBackground(String... params) {
			serverUrl = AppConstant.getUrl(getApplicationContext())
					+ "ServletService?Action=checksubmit&userid="
					+ user.getId() + "&jobid=" + id;
			String json = httpHelper.HttpRequest(serverUrl);
			return json;
		}

		@Override
		protected void onPostExecute(String json) {
			super.onPostExecute(json);
			dialog.dismiss();
			if (!TextUtils.isEmpty(json) && json.trim().length() > 0) {
				list = gson.fromJson(json, new TypeToken<List<tb_jobsubmit>>() {
				}.getType());
				if (list.get(0).getScore() > 0) {
					toastUtil.show("该题已经评分,不能再次答");
					finish();
				} else {
					etBody.setText(list.get(0).getBody());
				}

			}
			if (list == null) {
				list = new ArrayList<tb_jobsubmit>();
			}
		}
	}

	private class submitAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(JobSubmitActivity.this, "提示",
					"提交中,请稍后..");
		}

		@Override
		protected String doInBackground(String... params) {
			serverUrl = AppConstant.getUrl(getApplicationContext())
					+ "ServletService";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("Action", "submitjob");
			map.put("id", list.size() > 0 ? list.get(0).getId() : 0);
			map.put("jobid", id);
			map.put("userid", user.getId());
			map.put("body", etBody.getText());

			String result = httpHelper.HttpPost(serverUrl, map);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (!TextUtils.isEmpty(result) && result.trim().equals("1")) {
				toastUtil.show("提交成功");
				setResult(RESULT_OK);
				finish();
			} else {
				toastUtil.show("提交失败");
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btnTopTitleRight:// 完成按钮
			submit();
			break;
		default:
			break;
		}

	}

}
