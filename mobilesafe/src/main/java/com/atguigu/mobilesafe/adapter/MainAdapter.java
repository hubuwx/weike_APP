package com.atguigu.mobilesafe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.util.SpUtils;

/**
 * Created by miao on 2016/3/15.
 */
public class MainAdapter extends BaseAdapter{
    private Context context;
    private String[] names;
    private int[] icons;
//    private final SharedPreferences sp;

    public MainAdapter(Context context, String[] names, int[] icons) {
        this.context = context;
        this.icons = icons;
        this.names = names;

//        sp = context.getSharedPreferences("ms", Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return names[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.item_main_gridview,null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_main);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_item_main);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //  获取当前item的数据
        String name = names[position];
        int icon = icons[position];
        // 赋值
        viewHolder.imageView.setImageResource(icon);
        viewHolder.textView.setText(name);

        if(position == 0) {
//            String newName = sp.getString("name", null);
            String newName = SpUtils.getInstance(context).get("name",null);
            if(newName != null) {
                viewHolder.textView.setText(newName);
            }
        }


        return convertView;
    }

    class ViewHolder{
        ImageView imageView;
        TextView textView;
    }
}
