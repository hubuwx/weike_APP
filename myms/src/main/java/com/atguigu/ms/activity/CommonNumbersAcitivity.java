package com.atguigu.ms.activity;

import android.Manifest;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.dao.CommonDao;

import java.util.List;

//常用号码查询界面
public class CommonNumbersAcitivity extends AppCompatActivity {
    private ExpandableListView elv_list;
    private List<String> mGroupList;
    private List<List<String>> mChildList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_common_numbers_acitivity);
        //获取控件对象
        elv_list = (ExpandableListView)findViewById(R.id.elv_list);

        //获取数据
        CommonDao commonDao = new CommonDao(this);
        mGroupList = commonDao.getGroupList();
        mChildList = commonDao.getChildList();

        //设置适配器
        NumberAdapter adapter = new NumberAdapter();
        elv_list.setAdapter(adapter);



        elv_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String data = mChildList.get(groupPosition).get(childPosition);// name_number
                String[] ss = data.split("_");

                // 打电话
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + ss[1]));
                if (ActivityCompat.checkSelfPermission(CommonNumbersAcitivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
                return  true;
            }
        });
    }

    class NumberAdapter extends BaseExpandableListAdapter{

        @Override
        public int getGroupCount() {
            return mGroupList.size();
        }

        @Override
        //组的数量
        public int getChildrenCount(int groupPosition) {
            return mGroupList.size();
        }

        @Override
        //当前组的子的数量
        public Object getGroup(int groupPosition) {
            return mChildList.get(groupPosition).size();
        }

        @Override
        //当前组的子
        public Object getChild(int groupPosition, int childPosition) {
            return mChildList.get(groupPosition).get(childPosition);
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
             if(convertView == null) {
                 convertView = View.inflate(CommonNumbersAcitivity.this,R.layout.item_expandable_group,null);
             }
            String name = mGroupList.get(groupPosition);
            TextView textView = (TextView) convertView;
            textView.setText(name);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = View.inflate(CommonNumbersAcitivity.this, R.layout.item_expandable_children,null);
            }
            String content = mChildList.get(groupPosition).get(childPosition);
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_item_expandable_child_name);
            TextView tv_num = (TextView) convertView.findViewById(R.id.tv_item_expandable_child_num);
            String ss[] = content.split("_");
            tv_name.setText(ss[0]);
            tv_num.setText(ss[1]);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}

