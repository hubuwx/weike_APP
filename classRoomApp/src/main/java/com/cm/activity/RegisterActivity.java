package com.cm.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cm.bean.users;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miebo.utils.BaseActivity;
import com.miebo.utils.BaseUtil;

/**
 * 
 * @author jinzhao
 * 
 */
public class RegisterActivity extends BaseActivity {

	private Button btnLogin, btnRegister;
	private EditText etLoginID, etPassword, etPasswordOK, etName, etEmail;
	private EditText etSpecialty, etClasss, etPhone;
	private String role;
	private SubmitAsyncTask submitloadAsyncTask;
	private int id = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		role = getIntent().getStringExtra("role");
		findview();
		setListener();
		if (user != null) {   
			id = user.getId();
			((TextView) findViewById(R.id.tvTopTitleCenter)).setText("修改个人信息");
			etLoginID.setText(user.getLoginid());
			etName.setText(user.getName());
			btnRegister.setText("修改");
			btnLogin.setText("取消");
			new loadAsyncTask().execute();

		}

	}

	private void findview() {
		((TextView) findViewById(R.id.tvTopTitleCenter)).setText("注册");
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		etLoginID = (EditText) findViewById(R.id.etLoginID);
		etPassword = (EditText) findViewById(R.id.etPassword);

		etPasswordOK = (EditText) findViewById(R.id.etPasswordOK);
		etName = (EditText) findViewById(R.id.etName);
		etEmail = (EditText) findViewById(R.id.etEmail);
		etPhone = (EditText) findViewById(R.id.etPhone);
		etSpecialty = (EditText) findViewById(R.id.etSpecialty);
		etClasss = (EditText) findViewById(R.id.etClasss);

	}

	private void setListener() {
		btnRegister.setOnClickListener(new btnRegisterOnClickListener());
		btnLogin.setOnClickListener(this);
	}

	@SuppressWarnings("unchecked")
	private class btnRegisterOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (etLoginID.getText().length() == 0) {
				toastUtil.show("请输入账号");
				return;
			}

			if (etName.getText().length() == 0) {
				toastUtil.show("请输入姓名");
				return;
			}
			if (etPassword.getText().length() == 0) {
				toastUtil.show("请输入密码");
				return;
			}

			if (etPasswordOK.getText().length() == 0) {
				toastUtil.show("请再次输入密码");
				return;
			}
			if (!etPassword.getText().toString()
					.equals(etPasswordOK.getText().toString())) {
				toastUtil.show("两次输入的密码不一致");
				return;
			}

			BaseUtil.HideKeyboard(RegisterActivity.this);
			submitloadAsyncTask = new SubmitAsyncTask();
			submitloadAsyncTask.execute("");

		}
	};

	@SuppressWarnings("deprecation")
	private class SubmitAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(RegisterActivity.this);
			dialog.setTitle("提示");
			dialog.setMessage("处理中,请稍后..");
			dialog.setCancelable(true);
			dialog.setButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (submitloadAsyncTask != null) {
						submitloadAsyncTask.cancel(true);
						submitloadAsyncTask = null;
						toastUtil.show("操作被取消");
					}
				}
			});
			dialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			String urlString = AppConstant.getUrl(getApplicationContext())
					+ "ServletService";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("Action", "register");
			map.put("id", id);
			map.put("loginid", etLoginID.getText());
			map.put("password", etPassword.getText());
			map.put("name", etName.getText());
			map.put("phone", etPhone.getText());
			map.put("email", etEmail.getText());
			map.put("typename", role);
			map.put("classs", etClasss.getText());
			map.put("specialty", etSpecialty.getText());
			String result = httpHelper.HttpPost(urlString, map);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			submitloadAsyncTask = null;
			dialog.dismiss();
			if (result != null && result.trim().equals("1")) {
				if (id == 0) {
					toastUtil.show("注册成功");
				} else {
					toastUtil.show("修改成功");

				}
				finish();
			} else {
				toastUtil.show("操作失败");
			}
		}
	}

	private class loadAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(RegisterActivity.this, "提示", "获取中..");
		}

		@Override
		protected String doInBackground(String... params) {
			String urlString = AppConstant.getUrl(getApplicationContext())
					+ "ServletService?Action=getOneRow&Table=users&ID="
					+ user.getId();
			String json = httpHelper.HttpRequest(urlString);
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			List<users> list = new Gson().fromJson(result,
					new TypeToken<List<users>>() {
					}.getType());
			users model = list.get(0);

			etEmail.setText(model.getEmail());
			etLoginID.setText(model.getLoginid());
			etName.setText(model.getName());
			etPassword.setText(model.getPassword());
			etPasswordOK.setText(model.getPassword());
			etPhone.setText(model.getPhone());
			etSpecialty.setText(model.getSpecialty());

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:
			finish();
			break;

		}
	}

}
