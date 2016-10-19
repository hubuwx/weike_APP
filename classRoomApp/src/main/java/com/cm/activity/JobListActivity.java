package com.cm.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cm.adapter.JobAdapter;
import com.cm.bean.tb_jobs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miebo.utils.AsyncUtils;
import com.miebo.utils.AsyncUtils.AsyncListener;
import com.miebo.utils.BaseActivity;

/**
 * 
 * 
 * @author jinzhao
 * 
 */
public class JobListActivity extends BaseActivity {
	private TextView tvTotal;
	private Button btnTopTitleLeft, btnTopTitleRight;
	private JobAdapter adapter;
	private ListView listview1;
	private TextView tvTopTitleCenter;
	private final Gson gson = new Gson();
	private List<tb_jobs> list;
	private int row = 0;
	private final int type = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		findview();
		init();
	}

	private void findview() {
		tvTotal = (TextView) findViewById(R.id.tvTotal);
		tvTopTitleCenter = ((TextView) findViewById(R.id.tvTopTitleCenter));
		tvTopTitleCenter.setText("小测验");
		btnTopTitleLeft = (Button) findViewById(R.id.btnTopTitleLeft);
		btnTopTitleRight = (Button) findViewById(R.id.btnTopTitleRight);
		btnTopTitleRight.setOnClickListener(this);
		if (user.getRole().equals("教师")) {
			btnTopTitleRight.setVisibility(View.VISIBLE);
			btnTopTitleRight.setText("添加");
		}

		listview1 = (ListView) findViewById(R.id.listview1);
		listview1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				row = position;
				if (user.getRole().equals("教师")) {
					showEditDialog();
				} else {
					intent = new Intent(JobListActivity.this,
							JobSubmitActivity.class);
					intent.putExtra("model", list.get(row));
					startActivity(intent);
				}
			}

		});
//		listview1.setOnItemLongClickListener(new OnItemLongClickListener() {
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				row = position;
//				return true;
//			}
//		});
	}
	//初始化显示内容
	private void init() {
		search();
	}

	private void search() {
		dialog = ProgressDialog.show(this, "提示", "获取中..");
		AsyncUtils.getInstance().addListener(new AsyncListener() {
			@Override
			public void onPostExecute() {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				adapter = new JobAdapter(JobListActivity.this, list);
				listview1.setAdapter(adapter);

				if (list.size() > 0 && user.getRole().equals("学生")) {
					tvTotal.setText("提示:点击可以答题哦");
					tvTotal.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void doInBackground() {
				String json = null;
				serverUrl = AppConstant.getUrl(getApplicationContext())
						+ "ServletService?Action=getOneRow&Table=tb_jobs";
				json = httpHelper.HttpRequest(serverUrl);
				if (!TextUtils.isEmpty(json) && json.trim().length() > 0) {
					list = gson.fromJson(json, new TypeToken<List<tb_jobs>>() {
					}.getType());

				} else {
					list = new ArrayList<tb_jobs>();
				}
			}
		}).start();
	}
 //当返回当前activity时调用，有系统回调
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			search();
		}
	}

	// 弹出上下文菜单
	private void showEditDialog() {
		final String[] arg = new String[] { "修改", "删除" };

		//new一个弹窗
		new AlertDialog.Builder(this).setTitle("选择操作")
				.setItems(arg, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (arg[which].equals("修改")) {
							intent = new Intent(JobListActivity.this,
									JobEditActivity.class);
							//把选中的数据传递过去
							intent.putExtra("model", list.get(row));
							//带回调的启动，回到之前页面能显示修改过后的数据
							startActivityForResult(intent, 1);
						}
						if (arg[which].equals("删除")) {
							new deleteAsyncTask().execute();
						}
					}
				}).show();
	}

	private class deleteAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(JobListActivity.this, "提示",
					"处理中,请稍后..");
		}

		@Override
		protected String doInBackground(String... params) {
			serverUrl = AppConstant.getUrl(getApplicationContext())
					+ "ServletService?Action=Del&Table=tb_jobs&ID="
					+ list.get(row).getId();
			String json = httpHelper.HttpRequest(serverUrl);
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result != null && result.trim().length() > 0) {
				toastUtil.show("删除成功");
				list.remove(row);
				adapter.notifyDataSetChanged();
			} else {
				toastUtil.show("删除失败");

			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnTopTitleRight:
			intent = new Intent(JobListActivity.this, JobEditActivity.class);
			startActivityForResult(intent, 1);
			break;

		}

	}

}
