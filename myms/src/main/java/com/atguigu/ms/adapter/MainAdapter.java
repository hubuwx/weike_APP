package com.atguigu.ms.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.util.SpUtils;

/**
 * Created by lenovo on 2016/3/26.
 * 主页面的适配器
 */
public class MainAdapter extends BaseAdapter {
    private static final String[] NAMES = new String[]{
            "手机防盗", "通讯卫士", "软件管理", "流量管理", "进程管理",
            "手机杀毒", "缓存清理", "高级工具", "设置中心"};

    private static final int[] ICONS = new int[]{R.drawable.widget01,
            R.drawable.widget02, R.drawable.widget03, R.drawable.widget04,
            R.drawable.widget05, R.drawable.widget06, R.drawable.widget07,
            R.drawable.widget08, R.drawable.widget09};

    private Context mContext;
//    private final SharedPreferences mSp;

    public MainAdapter(Context context) {
        mContext = context;
//        mSp = context.getSharedPreferences("ms", Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return NAMES.length;
    }

    @Override
    public Object getItem(int position) {
        return NAMES[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取或创建一个Viewholder
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = View.inflate(mContext, R.layout.item_main_gridview, null);
            holder.iv_item_icon = (ImageView) convertView.findViewById(R.id.iv_item_icon);
            holder.tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 获取当前item的数据
        String name = NAMES[position];
        int icon = ICONS[position];

        // 显示当前item数据
        holder.iv_item_icon.setImageResource(icon);
        holder.tv_item_name.setText(name);

        // 修改名称处理
        if (position == 0) {
            // 获取保存修改名称
//            String newName = mSp.getString("name", null);

            String newName =  SpUtils.getInstance(mContext).get(SpUtils.NANE, null);
            if (newName != null) {
                holder.tv_item_name.setText(newName);
            }
        }

        // 返回converview
        return convertView;
    }

    class ViewHolder {
        ImageView iv_item_icon;
        TextView tv_item_name;
    }

}
