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

import com.cm.adapter.NoticeAdapter;
import com.cm.bean.tb_notices;
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
public class NoticeListActivity extends BaseActivity {
	private Button btnTopTitleLeft, btnTopTitleRight;
	private NoticeAdapter adapter;
	private ListView listview1;
	private TextView tvTopTitleCenter;
	private final Gson gson = new Gson();
	private List<tb_notices> list;
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
		tvTopTitleCenter = ((TextView) findViewById(R.id.tvTopTitleCenter));
		tvTopTitleCenter.setText("教学信息");
		btnTopTitleLeft = (Button) findViewById(R.id.btnTopTitleLeft);
		btnTopTitleRight = (Button) findViewById(R.id.btnTopTitleRight);
		btnTopTitleRight.setOnClickListener(this);
		if (user.getRole().equals("教师")) {
			btnTopTitleRight.setVisibility(View.VISIBLE);
			btnTopTitleRight.setText("发布信息");
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
					intent = new Intent(NoticeListActivity.this,
							NoticeViewActivity.class);
					intent.putExtra("model", list.get(row));
					startActivity(intent);
				}
			}

		});
		listview1.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				row = position;
				return true;
			}
		});
	}

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
				adapter = new NoticeAdapter(NoticeListActivity.this, list);
				listview1.setAdapter(adapter);
			}

			@Override
			public void doInBackground() {
				String json = null;
				serverUrl = AppConstant.getUrl(getApplicationContext())
						+ "ServletService?Action=getOneRow&Table=tb_notices";
				json = httpHelper.HttpRequest(serverUrl);
				if (!TextUtils.isEmpty(json) && json.trim().length() > 0) {
					list = gson.fromJson(json,
							new TypeToken<List<tb_notices>>() {
							}.getType());

				} else {
					list = new ArrayList<tb_notices>();
				}
			}
		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			search();
		}
	}

	// 弹出上下文菜单
	private void showEditDialog() {
		final String[] arg = new String[] { "修改此信息", "删除此信息" };

		new AlertDialog.Builder(this).setTitle("选择操作")
				.setItems(arg, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (arg[which].equals("修改此信息")) {
							intent = new Intent(NoticeListActivity.this,
									NoticeEditActivity.class);
							intent.putExtra("model", list.get(row));
							startActivityForResult(intent, 1);
						}
						if (arg[which].equals("删除此信息")) {
							new deleteAsyncTask().execute();
						}
					}
				}).show();
	}

	private class deleteAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(NoticeListActivity.this, "提示",
					"处理中,请稍后..");
		}

		@Override
		protected String doInBackground(String... params) {
			serverUrl = AppConstant.getUrl(getApplicationContext())
					+ "ServletService?Action=Del&Table=tb_notices&ID="
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
			intent = new Intent(NoticeListActivity.this,
					NoticeEditActivity.class);
			startActivityForResult(intent, 1);
			break;

		}

	}

}
