package com.cm.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cm.adapter.ScoreAdapter;
import com.cm.bean.tb_jobsubmit;
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
public class ScoreListActivity extends BaseActivity {
	private TextView tvTotal;
	private Button btnTopTitleLeft, btnTopTitleRight;
	private ScoreAdapter adapter;
	private ListView listview1;
	private TextView tvTopTitleCenter;
	private final Gson gson = new Gson();
	private List<tb_jobsubmit> list;
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
		if (user.getRole().equals("教师")){
			tvTopTitleCenter.setText("测验评分");
		}
		else{
			tvTopTitleCenter.setText("成绩查询");
		}
		btnTopTitleLeft = (Button) findViewById(R.id.btnTopTitleLeft);
		btnTopTitleRight = (Button) findViewById(R.id.btnTopTitleRight);
		btnTopTitleRight.setOnClickListener(this);
		//if (user.getRole().equals("教师")) {

		//}

		listview1 = (ListView) findViewById(R.id.listview1);
		listview1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				row = position;
				if (user.getRole().equals("教师")) {
					showEditDialog();
				} else {
					 //intent = new Intent(ScoreListActivity.this,
					 //JobSubmitActivity.class);
					 //intent.putExtra("model", list.get(row));
					 //startActivity(intent);
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
				adapter = new ScoreAdapter(ScoreListActivity.this, list);
				listview1.setAdapter(adapter);

				if (list.size() > 0 && user.getRole().equals("教师")) {
					tvTotal.setText("提示:点击可以评分");
					tvTotal.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void doInBackground() {
				String json = null;
				serverUrl = AppConstant.getUrl(getApplicationContext())
						+ "ServletService?Action=getscorelist";
				if (user.getRole().equals("学生")) {
					serverUrl += "&userid=" + user.getId();
				}
				json = httpHelper.HttpRequest(serverUrl);
				if (!TextUtils.isEmpty(json) && json.trim().length() > 0) {
					list = gson.fromJson(json,
							new TypeToken<List<tb_jobsubmit>>() {
							}.getType());

				} else {
					list = new ArrayList<tb_jobsubmit>();
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
		final String[] arg = new String[] { "评分", "删除" };

		new AlertDialog.Builder(this).setTitle("选择操作")
				.setItems(arg, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (arg[which].equals("评分")) {
							dialog();
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
			dialog = ProgressDialog.show(ScoreListActivity.this, "提示",
					"处理中,请稍后..");
		}

		@Override
		protected String doInBackground(String... params) {
			serverUrl = AppConstant.getUrl(getApplicationContext())
					+ "ServletService?Action=Del&Table=tb_jobsubmit&ID="
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

	private void dialog() {

		final EditText etScore = new EditText(this);
		etScore.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		if (list.get(row).getScore() != 0) {
			etScore.setText(list.get(row).getScore() + "");
		}
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle("评分")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton("确定评分",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (etScore.getText().length() == 0) {
									toastUtil.show("请输入评分");
									return;
								}
								new submitAsyncTask(etScore.getText()
										.toString()).execute();
							}

						}).setNegativeButton("取消", null).create();
		//设置弹窗中的编辑框
		dialog.setView(etScore);
		dialog.show();

	}

	private class submitAsyncTask extends AsyncTask<String, Integer, String> {
		private final String score;

		public submitAsyncTask(String score) {
			this.score = score;
		}

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(ScoreListActivity.this, "提示",
					"提交中,请稍后..");
		}

		@Override
		protected String doInBackground(String... params) {
			serverUrl = AppConstant.getUrl(getApplicationContext())
					+ "ServletService";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("Action", "score");
			map.put("id", list.get(row).getId());
			map.put("score", score);
			String result = httpHelper.HttpPost(serverUrl, map);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (!TextUtils.isEmpty(result) && result.trim().equals("1")) {
				toastUtil.show("评分成功");
				list.get(row).setScore(Integer.valueOf(score));
				adapter.notifyDataSetChanged();
			} else {
				toastUtil.show("提交失败");
			}
		}
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.btnTopTitleRight:
//			intent = new Intent(ScoreListActivity.this, JobEditActivity.class);
//			startActivityForResult(intent, 1);
//			break;

		//}

	}

}
