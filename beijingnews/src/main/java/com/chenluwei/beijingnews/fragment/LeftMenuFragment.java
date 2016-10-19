package com.chenluwei.beijingnews.fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chenluwei.beijingnews.MainActivity;
import com.chenluwei.beijingnews.R;
import com.chenluwei.beijingnews.base.BaseFragment;
import com.chenluwei.beijingnews.base.BasePager;
import com.chenluwei.beijingnews.domain.NewsCenterBean;
import com.chenluwei.beijingnews.domain.NewsCenterBean2;
import com.chenluwei.beijingnews.pager.NewscenterPager;
import com.chenluwei.beijingnews.utils.DensityUtil;

import java.util.List;

/**
 * Created by lw on 2016/5/18.
 * 左侧菜单Fragment
 */
public class LeftMenuFragment  extends BaseFragment{
    private static final String TAG = LeftMenuFragment.class.getSimpleName();
    private ListView listView;
    private LeftMenuAdapter adapter;
    /**
     *被点击的item位置
     */
    int selectPosition;
    /**
     * 左侧菜单的数据
     */
    private List<NewsCenterBean2.NewsCenterData> leftMenuData;
    @Override
    public View initView() {
        listView = new ListView(context);
        listView.setBackgroundColor(Color.BLACK);
        listView.setPadding(0, DensityUtil.dip2px(context, 40), 0, 0);
        listView.setDividerHeight(0);
        //防止在低版本的时候变色
        listView.setCacheColorHint(Color.TRANSPARENT);
        //当我们选择某条的时候，没有任何颜色变化
        listView.setSelection(android.R.color.transparent);
        //监听点击事件，设置enable
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //1.点击这一条变成高亮显示
                selectPosition = position;
                adapter.notifyDataSetChanged();
                //2.左侧菜单收起来
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();//关-->开，开-->关
                //3.切换到对应的页面，新闻详情，专题详情页面，图组详情页面，互动详情页面
                switchPager(selectPosition);
            }
        });
        return listView;
    }

    /**
     * 切换对应的菜单页面
     * @param selectPosition
     */
    private void switchPager(int selectPosition) {
        MainActivity mainActivity = (MainActivity) context;
        ContentFragment contentFragment = mainActivity.getContentFragment();
        NewscenterPager newscenterPager = contentFragment.getNewscenterPager();
        newscenterPager.switchPager(selectPosition);
    }

    @Override
    public void initData() {
        super.initData();
        Log.e(TAG, "左侧菜单被初始化");

    }

    public void setData(List<NewsCenterBean2.NewsCenterData> leftMenuData) {
        this.leftMenuData = leftMenuData;
        adapter = new LeftMenuAdapter();
        //设置适配器
        listView.setAdapter(adapter);
        switchPager(selectPosition);
    }

    class  LeftMenuAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return leftMenuData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) View.inflate(context, R.layout.item_leftmenu,null);
            textView.setText(leftMenuData.get(position).getTitle());
            //选中的显示高亮
           textView.setEnabled(selectPosition == position);
            return textView;
        }
    }
}
