package com.cm.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.miebo.utils.BaseActivity;
import com.miebo.utils.SPUtil;

public class MainActivity extends BaseActivity {
	private TextView tvTopTitleCenter;
	private String[] menutext = null;
	private final Integer[] itemImages = new Integer[] { R.drawable.icon1,
			R.drawable.icon2, R.drawable.icon4, R.drawable.icon4,
			R.drawable.icon5, R.drawable.icon6, R.drawable.icon7 };
	private GridView grd;
	private TextView tvMain;
	private Button btnTopTitleLeft;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findview();
		btnTopTitleLeft.setOnClickListener(this);
		init();
	}

	private void findview() {
		tvTopTitleCenter = (TextView) findViewById(R.id.tvTopTitleCenter);
		btnTopTitleLeft = (Button) findViewById(R.id.btnTopTitleLeft);
		btnTopTitleLeft.setVisibility(View.VISIBLE);
		btnTopTitleLeft.setText("?????");
		tvTopTitleCenter.setText(getString(R.string.app_name));
		tvMain = (TextView) findViewById(R.id.tvMain);
	    tvMain.setVisibility(View.VISIBLE);
		tvMain.setText("?????" + user.getName());
		grd = (GridView) findViewById(R.id.grd);
		
	}

	private void init() {
		if (user.getRole().equals("???")) {
			menutext = new String[] {"?????", "??????", "С????", "????????", "??????", "???" };
		} else {
			menutext = new String[] {"?????", "??????", "С????", "??????", "??????", "???" };
		}
		//???gridview???
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menutext.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", itemImages[i]);
			map.put("ItemText", menutext[i]);
			lstImageItem.add(map);
		}
		//??????????????????
		SimpleAdapter saImageItems = new SimpleAdapter(this, lstImageItem,
				R.layout.gridview_item_detailmenu, new String[] { "ItemImage",
						"ItemText" },
				new int[] { R.id.ItemImage, R.id.ItemText });
		grd.setAdapter(saImageItems);
		grd.setSelector(new ColorDrawable(Color.TRANSPARENT));
		grd.setOnItemClickListener(new ItemClickListener());
	}

	// ???
	private class ItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			intent = null;
			if (menutext[position].equals("??????")) {
				intent = new Intent(MainActivity.this, NoticeListActivity.class);
			}
			if (menutext[position].equals("С????")) {
				intent = new Intent(MainActivity.this, JobListActivity.class);
			}
			if (menutext[position].equals("?????")) {
				intent = new Intent(MainActivity.this, ChatActivity.class);

			}
			if (menutext[position].equals("????????")||menutext[position].equals("??????")) {
				intent = new Intent(MainActivity.this, ScoreListActivity.class);

			}

			if (menutext[position].equals("??????")) {
				intent = new Intent(MainActivity.this, RegisterActivity.class);
			}

			if (menutext[position].equals("???")) {
				// ?????????
				SPUtil.set(MainActivity.this, "auto", false);
				finish();
			}
			if (intent != null) {
				startActivity(intent);
			}

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnTopTitleLeft:
			intojiaoxuewang();
			break;
		}
	}

	private void intojiaoxuewang() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.BROWSABLE");
		intent.setData(Uri.parse("http://218.25.35.28/"));
		startActivity(intent);
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
