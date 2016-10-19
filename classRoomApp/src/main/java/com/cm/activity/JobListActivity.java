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
		tvTopTitleCenter.setText("С����");
		btnTopTitleLeft = (Button) findViewById(R.id.btnTopTitleLeft);
		btnTopTitleRight = (Button) findViewById(R.id.btnTopTitleRight);
		btnTopTitleRight.setOnClickListener(this);
		if (user.getRole().equals("��ʦ")) {
			btnTopTitleRight.setVisibility(View.VISIBLE);
			btnTopTitleRight.setText("���");
		}

		listview1 = (ListView) findViewById(R.id.listview1);
		listview1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				row = position;
				if (user.getRole().equals("��ʦ")) {
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
	//��ʼ����ʾ����
	private void init() {
		search();
	}

	private void search() {
		dialog = ProgressDialog.show(this, "��ʾ", "��ȡ��..");
		AsyncUtils.getInstance().addListener(new AsyncListener() {
			@Override
			public void onPostExecute() {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				adapter = new JobAdapter(JobListActivity.this, list);
				listview1.setAdapter(adapter);

				if (list.size() > 0 && user.getRole().equals("ѧ��")) {
					tvTotal.setText("��ʾ:������Դ���Ŷ");
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
 //�����ص�ǰactivityʱ���ã���ϵͳ�ص�
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			search();
		}
	}

	// ���������Ĳ˵�
	private void showEditDialog() {
		final String[] arg = new String[] { "�޸�", "ɾ��" };

		//newһ������
		new AlertDialog.Builder(this).setTitle("ѡ�����")
				.setItems(arg, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (arg[which].equals("�޸�")) {
							intent = new Intent(JobListActivity.this,
									JobEditActivity.class);
							//��ѡ�е����ݴ��ݹ�ȥ
							intent.putExtra("model", list.get(row));
							//���ص����������ص�֮ǰҳ������ʾ�޸Ĺ��������
							startActivityForResult(intent, 1);
						}
						if (arg[which].equals("ɾ��")) {
							new deleteAsyncTask().execute();
						}
					}
				}).show();
	}

	private class deleteAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(JobListActivity.this, "��ʾ",
					"������,���Ժ�..");
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
				toastUtil.show("ɾ���ɹ�");
				list.remove(row);
				adapter.notifyDataSetChanged();
			} else {
				toastUtil.show("ɾ��ʧ��");

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
