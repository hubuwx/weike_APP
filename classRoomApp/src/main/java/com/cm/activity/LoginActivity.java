package com.cm.activity;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cm.bean.users;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miebo.utils.BaseActivity;
import com.miebo.utils.BaseUtil;
import com.miebo.utils.CommonApplication;
import com.miebo.utils.OnLineUser;
import com.miebo.utils.SPUtil;
import com.miebo.utils.UIUtils;

/**
 * 登录界面
 * 
 * @author jinzhao
 * 
 */
public class LoginActivity extends BaseActivity {

	private Button btnLogin, btnRegister;
	private EditText etLoginID, etPassword;
	private CheckBox ckbRememberLogin;
	private loadAsyncTask loginAsyncTask;
	private final Gson gson = new Gson();
	private Spinner spinner1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		findview();
		setListener();
		UIUtils.bindSpinner(this, spinner1, new String[] { "学生", "教师" });   //xialacaidan
		//etLoginID.setText("2001");
		//etPassword.setText("111111");
		spinner1.setSelection(1);
	}

	@Override
	protected void onResume() {
		super.onResume();
		application.setLoginUser(null);
	}

	private void findview() {
		((TextView) findViewById(R.id.tvTopTitleCenter)).setText("登录");
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		etLoginID = (EditText) findViewById(R.id.etLoginID);
		etPassword = (EditText) findViewById(R.id.etPassword);
		ckbRememberLogin = (CheckBox) findViewById(R.id.ckbRememberLogin);
		spinner1 = (Spinner) findViewById(R.id.spinner1);
	}

	private void setListener() {
		btnLogin.setOnClickListener(new btnLoginOnClickListener());
		etLoginID.setText(SPUtil.get(LoginActivity.this, "loginid", ""));
		etPassword.setText(SPUtil.get(LoginActivity.this, "password", ""));
		btnRegister.setOnClickListener(this);
	}

	@SuppressWarnings("unchecked")
	private class btnLoginOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (etLoginID.getText().length() == 0) {
				toastUtil.show("请输入账号");
				return;
			}
			if (etPassword.getText().length() == 0) {
				toastUtil.show("请输入密码");
				return;
			}

			BaseUtil.HideKeyboard(LoginActivity.this);  //隐藏键盘
			loginAsyncTask = new loadAsyncTask();    //
			loginAsyncTask.execute("");

		}
	};

	@SuppressWarnings("deprecation")
	private class loadAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {    //联网前主界面提示方法
			dialog = new ProgressDialog(LoginActivity.this);
			dialog.setTitle("提示");
			dialog.setMessage("登录中,请稍后..");
			dialog.setCancelable(true);
			dialog.setButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (loginAsyncTask != null) {
						loginAsyncTask.cancel(true);
						loginAsyncTask = null;
						toastUtil.show("登录被取消");
					}
				}
			});
			dialog.show();

		}

		@Override
		protected String doInBackground(String... params) {   //zaifenxianchengzhongcaozuolianwang
			String urlString = AppConstant.getUrl(getApplicationContext())
					+ "ServletService?Action=login";
			urlString += "&loginid=" + etLoginID.getText() + "&password="
					+ etPassword.getText() + "&typename="
					+ spinner1.getSelectedItem().toString();
			String json = httpHelper.HttpRequest(urlString);
			return json;
		}

		@Override
		protected void onPostExecute(String result) {  //
			super.onPostExecute(result);
			loginAsyncTask = null;
			dialog.dismiss();
			if (result != null && result.trim().length() > 0) {
				List<users> list = gson.fromJson(result,
						new TypeToken<List<users>>() {
						}.getType());
				if (list == null || list.size() == 0) {
					toastUtil.show("登录失败");
				} else {

					if (ckbRememberLogin.isChecked()) {
						SPUtil.set(LoginActivity.this, "login", result);
					}
					dealLogin(result);
				}
			} else {
				toastUtil.show("登录失败");
			}
		}
	}

	private void dealLogin(String result) {
		try {
			jsonArray = new JSONArray(result);
			jsonObject = jsonArray.getJSONObject(0);
			// 保存登录用户信息
			CommonApplication application = (CommonApplication) getApplicationContext();
			OnLineUser model = new OnLineUser();
			model.setId(jsonObject.getInt("id"));
			model.setLoginid(etLoginID.getText().toString());
			model.setName(jsonObject.getString("name"));
			model.setRole(spinner1.getSelectedItem().toString());
			application.setLoginUser(model);
			toastUtil.show(model.getName() + ",登录成功");
			intent = new Intent(LoginActivity.this, MainActivity.class);

			startActivity(intent);
			finish();
		} catch (JSONException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRegister:
			intent = new Intent(LoginActivity.this, RegisterActivity.class);
			intent.putExtra("role", spinner1.getSelectedItem().toString());
			startActivity(intent);
			break;

		default:
			break;
		}
	}

}
