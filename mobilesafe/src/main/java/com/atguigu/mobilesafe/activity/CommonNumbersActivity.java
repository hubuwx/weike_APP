package com.atguigu.mobilesafe.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.dao.CommonNumDao;

import java.util.List;

// 常用号码查询页面
public class CommonNumbersActivity extends Activity {
    private ExpandableListView elv_list;
    private NumberAdapter adapter;
    private List<String> groupData;
    private List<List<String>> childData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_numbers);

        elv_list = (ExpandableListView) findViewById(R.id.elv_list);

        //读取数据
        groupData = CommonNumDao.getGroupList(this);
        childData = CommonNumDao.getChildList(this);

        //设置adapter
        adapter = new NumberAdapter();
        elv_list.setAdapter(adapter);

        //设置点击监听, 打电话
        elv_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String number = childData.get(groupPosition).get(childPosition).split("_")[1];
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));

                if (ActivityCompat.checkSelfPermission(CommonNumbersActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return false;
                }

                startActivity(intent);

                return true;
            }
        });
    }

    class NumberAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return groupData.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childData.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupData.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childData.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(CommonNumbersActivity.this, R.layout.item_expandable_group, null);
            }
            TextView textView = (TextView) convertView;
            textView.setText(groupData.get(groupPosition));

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = View.inflate(CommonNumbersActivity.this, R.layout.item_expandable_children, null);
            }

            String name_number = (String) getChild(groupPosition, childPosition);
            String[] ss = name_number.split("_");
            String name = ss[0];
            String number = ss[1];
            TextView nameTV = (TextView) convertView.findViewById(R.id.tv_item_expandable_child_name);
            nameTV.setText(name);
            TextView numTV = (TextView) convertView.findViewById(R.id.tv_item_expandable_child_num);
            numTV.setText(number);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true; //child Item才可以响应点击
        }
    }
}
