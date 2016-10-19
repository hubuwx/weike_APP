package com.cm.activity;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miebo.utils.BaseActivity;

/**
 * 
 * @author jinzhao
 * 
 */
public class JobEditActivity extends BaseActivity {
	private TextView tvTopTitleCenter;
	private Button btnTopTitleRight;
	private EditText etTitle, etBody;
	private int id = 0;
	private List<tb_jobs> list;
	private final Gson gson = new Gson();
	private tb_jobs model;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editjob);
		//��ȡ��joblistactivity�е�intent���ݵĶ���
		model = (tb_jobs) getIntent().getSerializableExtra("model");
		if (model != null) {
			id = model.getId();
		}
		findview();
		if (id != 0) {
		    new loadAsyncTask().execute();
			etTitle.setText(model.getTitle());
			etBody.setText(model.getBody());

		}
	}

	private void findview() {
		tvTopTitleCenter = (TextView) findViewById(R.id.tvTopTitleCenter);
		btnTopTitleRight = (Button) findViewById(R.id.btnTopTitleRight);
		btnTopTitleRight.setVisibility(View.VISIBLE);
		btnTopTitleRight.setOnClickListener(this);
		btnTopTitleRight.setText("���");

		etTitle = (EditText) findViewById(R.id.etTitle);

		etBody = (EditText) findViewById(R.id.etBody);

		if (id == 0) {
			tvTopTitleCenter.setText("������ҵ");
		} else {
			tvTopTitleCenter.setText("�޸���ҵ");
		}
	}

	private void submit() {
		if (etTitle.getText().length() == 0) {
			toastUtil.show("��������ҵ����");
			return;
		}

		if (etBody.getText().length() == 0) {
			toastUtil.show("��������ҵ����");
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
			dialog = ProgressDialog.show(JobEditActivity.this, "��ʾ",
					"��ȡ��,���Ժ�..");
		}

		@Override
		protected String doInBackground(String... params) {
			serverUrl = AppConstant.getUrl(getApplicationContext())
					+ "ServletService?Action=getOneRow&Table=tb_jobs&ID=" + id;
			String json = httpHelper.HttpRequest(serverUrl);
			return json;
		}

		@Override
		protected void onPostExecute(String json) {
			super.onPostExecute(json);
			dialog.dismiss();
			if (!TextUtils.isEmpty(json) && json.trim().length() > 0) {
				list = gson.fromJson(json, new TypeToken<List<tb_jobs>>() {
				}.getType());
				if (list != null && list.size() > 0) {
					etTitle.setText(list.get(0).getTitle());
					etBody.setText(list.get(0).getBody());

				}
			}
		}
	}

	private class submitAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(JobEditActivity.this, "��ʾ",
					"�ύ��,���Ժ�..");
		}

		@Override
		protected String doInBackground(String... params) {
			serverUrl = AppConstant.getUrl(getApplicationContext())
					+ "ServletService";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("Action", "editjob");
			map.put("id", id);
			map.put("title", etTitle.getText());
			map.put("body", etBody.getText());

			String result = httpHelper.HttpPost(serverUrl, map);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (!TextUtils.isEmpty(result) && result.trim().equals("1")) {
				toastUtil.show("�ύ�ɹ�");
				//���ûص��ķ���ֵ
				setResult(RESULT_OK);
				//������ɺ�finish��ǰactivity���ص�joblistactivity�лص�����
				finish();
			} else {
				toastUtil.show("�ύʧ��");
			}
		}
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btnTopTitleRight:// ��ɰ�ť
			submit();
			break;
		default:
			break;
		}

	}

}
