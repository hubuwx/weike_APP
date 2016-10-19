package com.atguigu.ms.activity;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.bean.BlackNumInfo;
import com.atguigu.ms.dao.BlackNumDao;

import java.util.List;

// 通讯卫士 黑名单
public class BlackNumActivity extends ListActivity implements View.OnClickListener {
    private ListView mListView;
    private Button bt_blacknum;
    private List<BlackNumInfo> mList;
    private BlackNumAdapter mBlackNumAdapter;
    private BlackNumDao mBlackNumDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_num);

        // 获取控件对象
        bt_blacknum = (Button) findViewById(R.id.bt_blacknum);
//        mListView = (ListView)findViewById(android.R.id.list);
        mListView = getListView();

        // 获取数据
        mBlackNumDao = new BlackNumDao(this);
        mList = mBlackNumDao.get();

        // 填充数据
        mBlackNumAdapter = new BlackNumAdapter();
        mListView.setAdapter(mBlackNumAdapter);

        // 获取通知栏推送过来的号码
        String number = getIntent().getStringExtra("number");
//        Log.e("number",number);

        if(number!= null) {
            showAddBlackNumDialog(number);
        }

        // 监听处理
        bt_blacknum.setOnClickListener(this);

        mListView.setOnCreateContextMenuListener(this);
    }

    private int mPosition;
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(0,1,0,"更新");
        menu.add(0,2,0,"删除");

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        mPosition = info.position;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        BlackNumInfo blackNumInfo = mList.get(mPosition);

        switch (item.getItemId()){
            case 1:// 更新
                showUpdateDialog(blackNumInfo);
                break;
            case 2:// 删除
                // 内存变化
                mList.remove(mPosition);
                // 存储变化
                mBlackNumDao.deleteById(blackNumInfo.getId());

                // 页面变化
                mBlackNumAdapter.notifyDataSetChanged();
                break;
        }


        return super.onContextItemSelected(item);
    }

    // 更新号码对话框
    private void showUpdateDialog(final BlackNumInfo blackNumInfo) {
        final EditText editText = new EditText(BlackNumActivity.this);
        editText.setHint(blackNumInfo.getNumber());

        new AlertDialog.Builder(this)
                    .setTitle("修改黑名单")
                    .setView(editText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 获取输入的号码
                            String num = editText.getText().toString().trim();

                            // 更新内存
                            blackNumInfo.setNumber(num);

                            // 存储
                            mBlackNumDao.update(blackNumInfo);

                            // 页面
                            mBlackNumAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_blacknum:// 添加按钮
                showAddBlackNumDialog(null);
                break;
        }
    }

    private void showAddBlackNumDialog(String number) {
        final EditText editText = new EditText(BlackNumActivity.this);
        if(number == null) {
            editText.setHint("输入黑名单号码");
        }else {
            editText.setText(number);
        }

        new AlertDialog.Builder(this)
                .setTitle("添加黑名单")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取输入的黑名单号码
                        String number = editText.getText().toString().trim();
                        BlackNumInfo blackNumInfo = new BlackNumInfo(-1, number);
                        // 更新内存
                        mList.add(0, blackNumInfo);

                        // 更新页面
                        mBlackNumAdapter.notifyDataSetChanged();
                        // 更新存储
                        int id = mBlackNumDao.add(blackNumInfo);
                        blackNumInfo.setId(id);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }


    class BlackNumAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 创建或获取viewHolder
            ViewHolder holder;
            if(convertView ==null) {
                holder = new ViewHolder();
                convertView = View.inflate(BlackNumActivity.this,android.R.layout.simple_list_item_1,null);

                holder.textView = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            // 获取当前item
            BlackNumInfo blackNumInfo = mList.get(position);
            // 赋值当前item数据
            holder.textView.setText(blackNumInfo.getNumber());
            // 返回view

            return convertView;
        }

        class ViewHolder{
            TextView textView;
        }
    }
}
