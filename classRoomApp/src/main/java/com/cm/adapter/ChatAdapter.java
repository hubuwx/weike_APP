package com.cm.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cm.activity.AppConstant;
import com.cm.activity.R;
import com.cm.bean.chats;
import com.miebo.utils.AsyncImageLoader;

public class ChatAdapter extends BaseAdapter {
	private List<chats> list = null;
	private final Context context;
	private LayoutInflater infater = null;
	private final AsyncImageLoader asyncImageLoader;
	private final String serverUrl;

	public ChatAdapter(Context context, List<chats> list) {
		this.infater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
		asyncImageLoader = new AsyncImageLoader(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.pc_loading_fali));
		serverUrl = AppConstant.getRootUrl(context);
	}

	@Override
	public int getCount() {
		if (list == null) {
			return 0;
		} else {
			return list.size();
		}

	}

	@Override
	public Object getItem(int position) {

		return null;
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}
   /**
    * ��ȡlistview��ÿ������
    */
	@Override
	public View getView(final int position, View convertview, ViewGroup parent) {
		ViewHolder holder = null;
		//���convertviewû�б���ֵ�����ֶ����ز���
		if (convertview == null) {
			holder = new ViewHolder();
			//����item��ÿһ�����Ĳ���
			convertview = infater.inflate(R.layout.listview_item_common, null);

			holder.textView1 = (TextView) convertview
					.findViewById(R.id.textView1);
			holder.textView2 = (TextView) convertview
					.findViewById(R.id.textView2);
			holder.textView3 = (TextView) convertview
					.findViewById(R.id.textView3);
			holder.textView3.setVisibility(View.VISIBLE);
			convertview.setTag(holder);
		} else {
			holder = (ViewHolder) convertview.getTag();
		}

		holder.textView1.setText(list.get(position).getUsername());
		holder.textView2.setText(list.get(position).getBody());
		holder.textView3.setText(list.get(position).getCreatetime());

		return convertview;
	}

	class ViewHolder {

		private TextView textView1;
		private TextView textView2;
		private TextView textView3;

	}

}
