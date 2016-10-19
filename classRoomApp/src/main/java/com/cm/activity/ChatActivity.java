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
	private int userid2;// 对方UserID
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
		((TextView) findViewById(R.id.tvTopTitleCenter)).setText("师生交流");
		listview1 = (ListView) findViewById(R.id.listview1);
		list = new ArrayList<chats>();
		adapter = new ChatAdapter(this, list);
		listview1.setAdapter(adapter);
		editText1 = (EditText) findViewById(R.id.editText1);
		button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(this);

		tvTopTitleRight = (TextView) findViewById(R.id.tvTopTitleRight);
		//tvTopTitleRight.setText("历史");
		//tvTopTitleRight.setVisibility(View.VISIBLE);
		tvTopTitleRight.setOnClickListener(this);
	}

	private void setListener() {
		listview1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			//每个条目监听
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				row = position;
				showEditDialog();
			}
		});
		startTime();
	}

	// 计时器
	private void startTime() {
		totalTime = 1;
		//timer的实现类
		timerTask = new TimerTask() {
			@Override
			public void run() {
				totalTime++;
				stepTimeHandler.sendEmptyMessage(0);
			}
		};
		timer = new Timer(true); //定义定时器工具
		/**\
		 * 第一个参数，是 TimerTask 类，在包：import java.util.TimerTask .使用者要继承该类，并实现public void run() 方法，因为 TimerTask 类 实现了 Runnable 接口。
第二个参数的意思是，当你调用该方法后，该方法必然会调用 TimerTask 类 TimerTask 类 中的 run()方法，这个参数就是这两者之间的差值，转换成汉语的意思就是说，用户调用 schedule() 方法后，要等待这么长的时间才可以第一次执行run() 方法。
第三个参数的意思就是，第一次调用之后，从第二次开始每隔多长的时间调用一次 run() 方法。
		 */
		timer.schedule(timerTask, 0, 1000);

	}

	private final Handler stepTimeHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			// 每3秒执行一次获取消息列表
			if (totalTime % 3 == 0 && !isDeleteing) {
				get_chat();
			}

		}
	};

	/**
	 * 获取交流信息的異步任务类
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
							//初始化时显示到listview的最后一条
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
 * 发送信息的方法
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
					model.setCreatetime("刚刚");
					model.setUsername(user.getName());
					list.add(model);
					adapter.notifyDataSetChanged(); 
					editText1.setText("");
				} else {
					toastUtil.show("发送失败");
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
				toastUtil.show("请输入内容");
			}
			break;
		case R.id.tvTopTitleRight:

			break;
		}
	}

	// 弹出删除菜单
	private void showEditDialog() {
		final String[] arg = new String[] { "删除" };
		//new一个弹窗
		new AlertDialog.Builder(this).setTitle("选择操作")
				.setItems(arg, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (arg[which].equals("删除")) {
							new deleteAsyncTask().execute();
						}
					}
				}).show();
	}
    //删除item的异步任务类
	private class deleteAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			isDeleteing = true;
			dialog = ProgressDialog.show(ChatActivity.this, "提示", "删除中,请稍后..");
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
				toastUtil.show("删除成功");
				//在集合中删除
				list.remove(row);
				//刷新listview
				adapter.notifyDataSetChanged();
			} else {
				toastUtil.show("删除失败");

			}

		}
	}
   /**
    * 防止内存泄漏
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
