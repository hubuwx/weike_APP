package com.atguigu.mobilesafe.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.bean.BlackNumInfo;
import com.atguigu.mobilesafe.dao.BlackNumDao;

import java.util.List;

// 通讯卫士页面
public class BlackNumActivity extends ListActivity {
    private ListView listView;
    private BlackNumAdapter blackNumAdapter;
    private List<BlackNumInfo> blackNum;
    private BlackNumDao blackNumDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_num);

        //获取view对象
        listView = getListView();
        blackNumDao = new BlackNumDao(this);
        blackNum = blackNumDao.getBlackNum();

        // 准备数据
        // 填充数据适配器
        blackNumAdapter = new BlackNumAdapter();
        listView.setAdapter(blackNumAdapter);

        // 获取通知栏推送过来的骚扰电话号码
        String number = getIntent().getStringExtra("number");
        if(number != null) {
            showAddDialog(number);
        }

        // 删除和更新处理
        listView.setOnCreateContextMenuListener(this);
    }

    // 添加黑名单号码的对话框
    private void showAddDialog(String number) {
        final EditText editText = new EditText(this);
        if(number != null) {
            editText.setText(number);
        }else {
            editText.setHint("输入黑名单");
        }

        new AlertDialog.Builder(this)
                .setTitle("添加黑名单")
                .setView(editText)
                .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取输入的黑名单号码
                        String num = editText.getText().toString().trim();
                        if(!"".equals(num)) {
                            // 更新内存显示
                            BlackNumInfo blackNumInfo = new BlackNumInfo(-1, num);

                            blackNum.add(0, blackNumInfo);
                            // 更新存储
                            blackNumDao.addBlackNum(blackNumInfo);
                            // 更新页面
                            blackNumAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private int position;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, 1, 0, "更新");
        menu.add(0, 2, 0, "删除");

        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) menuInfo;
        this.position = info.position;

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final BlackNumInfo blackNumInfo = blackNum.get(position);
        switch (item.getItemId()){
            case 1://更新
                final EditText editText = new EditText(BlackNumActivity.this);
                editText.setHint(blackNumInfo.getNumber());

                new AlertDialog.Builder(this)
                            .setTitle("修改黑名单")
                            .setView(editText)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 获取输入的名称
                                    String num = editText.getText().toString().trim();
                                    // 更新内存
                                    blackNumInfo.setNumber(num);
                                    // 更新存储
                                    blackNumDao.updateBlackNum(blackNumInfo);
                                    // 更新页面
                                    blackNumAdapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                break;

            case 2://删除
                blackNum.remove(position);
                blackNumDao.deleteBlackNum(blackNumInfo);
                blackNumAdapter.notifyDataSetChanged();
                break;

        }
        return super.onContextItemSelected(item);
    }

    // 添加黑名单按钮
    public void addBlackNum(View v) {
        showAddDialog(null);
    }

    class  BlackNumAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return blackNum ==null? 0:blackNum.size();
        }

        @Override
        public Object getItem(int position) {
            return blackNum.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(BlackNumActivity.this,android.R.layout.simple_list_item_1,null);
                holder.textView = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            BlackNumInfo blackNumInfo = blackNum.get(position);
            holder.textView.setText(blackNumInfo.getNumber());

            return convertView;
        }

        class ViewHolder{
            TextView textView;
        }
    }
}
