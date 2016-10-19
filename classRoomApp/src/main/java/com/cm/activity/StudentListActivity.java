package com.cm.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cm.adapter.UsersAdapter;
import com.cm.bean.users;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miebo.utils.BaseActivity;

public class StudentListActivity extends BaseActivity implements
		OnClickListener {

	private List<users> list;
	private UsersAdapter adapter;
	private ListView listview1;
	private Button btnSign;
	private final Gson gson = new Gson();
	private int row = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		findview();
		new loadAsyncTask().execute();
	}

	private void findview() {
		((TextView) findViewById(R.id.tvTopTitleCenter)).setText("学生列表");

		listview1 = (ListView) findViewById(R.id.listview1);
		listview1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				row = position;
				showContextDialog(position);
			}

		});
	}

	private class loadAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(StudentListActivity.this, "提示",
					"获取中,请稍后..");
		}

		@Override
		protected String doInBackground(String... params) {
			String urlString = AppConstant.getUrl(getApplicationContext())
					+ "ServletService?Action=getuserlist";
			urlString += "&typename=学生";
			return httpHelper.HttpRequest(urlString);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result != null && result.trim().length() > 0) {
				list = gson.fromJson(result, new TypeToken<List<users>>() {
				}.getType());
			} else {
				list = new ArrayList<users>();
				toastUtil.show("没有数据");
			}
			adapter = new UsersAdapter(StudentListActivity.this, list);
			listview1.setAdapter(adapter);
		}
	}

	// 弹出上下文菜单
	private void showContextDialog(final int position) {
		String[] arg = new String[] { "查看考勤情况" };
		new AlertDialog.Builder(this).setTitle("选择操作")
				.setItems(arg, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						intent = new Intent(StudentListActivity.this,
								MainActivity.class);
						intent.putExtra("userid", list.get(position).getId());
						intent.putExtra("name", list.get(position).getName());
						startActivity(intent);
					}
				}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 1) {
			new loadAsyncTask().execute();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 102, 0, "退出").setIcon(R.drawable.icon_application);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 100:
			intent = new Intent(StudentListActivity.this,
					RegisterActivity.class);
			startActivity(intent);
			break;

		case 102:
			finish();
			System.exit(0);
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {

		default:
			break;
		}
	}

}