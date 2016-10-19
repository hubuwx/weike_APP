package com.cm.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cm.adapter.ChatAdapter;
import com.cm.bean.chats;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miebo.utils.BaseActivity;

public class ChatActivity extends BaseActivity {

	private TextView tvTopTitleRight;
	private ChatAdapter adapter;
	private ListView listview1;
	private Button button1;
	private EditText editText1;
	private List<chats> list = new ArrayList<chats>();
	private TimerTask timerTask;
	private long totalTime = 0;
	private Timer timer;
	private final Gson gson = new Gson();
	private int userid2;// �Է�UserID
	private String username2;
	private int row;
	private boolean isDeleteing = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		//userid2 = getIntent().getIntExtra("userid2", 0);
		//username2 = getIntent().getStringExtra("username2");
		findview();
		setListener();
		get_chat();
	}

	private void findview() {
		((TextView) findViewById(R.id.tvTopTitleCenter)).setText("ʦ������");
		listview1 = (ListView) findViewById(R.id.listview1);
		list = new ArrayList<chats>();
		adapter = new ChatAdapter(this, list);
		listview1.setAdapter(adapter);
		editText1 = (EditText) findViewById(R.id.editText1);
		button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(this);

		tvTopTitleRight = (TextView) findViewById(R.id.tvTopTitleRight);
		//tvTopTitleRight.setText("��ʷ");
		//tvTopTitleRight.setVisibility(View.VISIBLE);
		tvTopTitleRight.setOnClickListener(this);
	}

	private void setListener() {
		listview1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			//ÿ����Ŀ����
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				row = position;
				showEditDialog();
			}
		});
		startTime();
	}

	// ��ʱ��
	private void startTime() {
		totalTime = 1;
		//timer��ʵ����
		timerTask = new TimerTask() {
			@Override
			public void run() {
				totalTime++;
				stepTimeHandler.sendEmptyMessage(0);
			}
		};
		timer = new Timer(true); //���嶨ʱ������
		/**\
		 * ��һ���������� TimerTask �࣬�ڰ���import java.util.TimerTask .ʹ����Ҫ�̳и��࣬��ʵ��public void run() ��������Ϊ TimerTask �� ʵ���� Runnable �ӿڡ�
�ڶ�����������˼�ǣ�������ø÷����󣬸÷�����Ȼ����� TimerTask �� TimerTask �� �е� run()�����������������������֮��Ĳ�ֵ��ת���ɺ������˼����˵���û����� schedule() ������Ҫ�ȴ���ô����ʱ��ſ��Ե�һ��ִ��run() ������
��������������˼���ǣ���һ�ε���֮�󣬴ӵڶ��ο�ʼÿ���೤��ʱ�����һ�� run() ������
		 */
		timer.schedule(timerTask, 0, 1000);

	}

	private final Handler stepTimeHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			// ÿ3��ִ��һ�λ�ȡ��Ϣ�б�
			if (totalTime % 3 == 0 && !isDeleteing) {
				get_chat();
			}

		}
	};

	/**
	 * ��ȡ������Ϣ�Į���������
	 */
	private void get_chat() {
		new AsyncTask<String, String, String>() {
			@Override
			protected String doInBackground(String... params) {
				String urlString = AppConstant.getUrl(getApplicationContext())
						+ "ServletService?Action=getchat&userid="
						+ user.getId() + "&userid2=" + userid2;
				String json = httpHelper.HttpRequest(urlString);
				return json;
			}

			@Override
			protected void onPostExecute(String result) {
				if (!TextUtils.isEmpty(result)) {
					try {
						list = gson.fromJson(result,
								new TypeToken<List<chats>>() {
								}.getType());
						if (list != null) {
							adapter = new ChatAdapter(ChatActivity.this, list);
							listview1.setAdapter(adapter);
							//��ʼ��ʱ��ʾ��listview�����һ��
							listview1.setSelection(list.size() - 1);
						} else {
							list = new ArrayList<chats>();
						}

						adapter.notifyDataSetChanged();
					} catch (Exception e) {

					}
				} else {

				}

			}

		}.execute();
	}
/**
 * ������Ϣ�ķ���
 */
	private void add() {
		new AsyncTask<String, String, String>() {
			@Override
			protected String doInBackground(String... params) {
				String urlString = AppConstant.getUrl(getApplicationContext())
						+ "ServletService";
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("Action", "chat_add");
				map.put("userid", user.getId());
				map.put("username", user.getName());
				map.put("body", editText1.getText());
				String result = httpHelper.HttpPost(urlString, map);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null && result.trim().equals("1")) {
					chats model = new chats();
					model.setBody(editText1.getText().toString());
					model.setCreatetime("�ո�");
					model.setUsername(user.getName());
					list.add(model);
					adapter.notifyDataSetChanged(); 
					editText1.setText("");
				} else {
					toastUtil.show("����ʧ��");
				}
			}

		}.execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			if (editText1.getText().length() > 0) {
				add();
			} else {
				toastUtil.show("����������");
			}
			break;
		case R.id.tvTopTitleRight:

			break;
		}
	}

	// ����ɾ���˵�
	private void showEditDialog() {
		final String[] arg = new String[] { "ɾ��" };
		//newһ������
		new AlertDialog.Builder(this).setTitle("ѡ�����")
				.setItems(arg, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (arg[which].equals("ɾ��")) {
							new deleteAsyncTask().execute();
						}
					}
				}).show();
	}
    //ɾ��item���첽������
	private class deleteAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			isDeleteing = true;
			dialog = ProgressDialog.show(ChatActivity.this, "��ʾ", "ɾ����,���Ժ�..");
		}

		@Override
		protected String doInBackground(String... params) {
			serverUrl = AppConstant.getUrl(getApplicationContext())
					+ "ServletService?Action=Del&ID=" + list.get(row).getId()
					+ "&Table=chats";
			String json = httpHelper.HttpRequest(serverUrl);
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			isDeleteing = false;
			if (result != null && result.trim().length() > 0) {
				toastUtil.show("ɾ���ɹ�");
				//�ڼ�����ɾ��
				list.remove(row);
				//ˢ��listview
				adapter.notifyDataSetChanged();
			} else {
				toastUtil.show("ɾ��ʧ��");

			}

		}
	}
   /**
    * ��ֹ�ڴ�й©
    */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
		if (timerTask != null) {
			timerTask.cancel();
		}

	}
}
