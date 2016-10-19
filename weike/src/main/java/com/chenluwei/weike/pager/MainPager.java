package com.chenluwei.weike.pager;

import android.app.Activity;
import android.content.Context;

import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chenluwei.weike.R;
import com.chenluwei.weike.adapter.MainAdapter;
import com.chenluwei.weike.base.BasePager;
import com.chenluwei.weike.base.MenuDetialBasePager;
import com.chenluwei.weike.util.Contants;
import com.chenluwei.weike.util.SpUtils;
import com.google.gson.Gson;
import com.viewpagerindicator.TabPageIndicator;


import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 对应的页签页面
 * Created by lw on 2016/4/21.
 *
 */
public class MainPager extends BasePager{

    @ViewInject(R.id.vp_detailpager)
    private ViewPager vp_detailpager;
    @ViewInject(R.id.tabpager_indicator)
    private TabLayout tabpager_indicator;
    //页签对应的json数据
    private List<String> childrenDatas = new ArrayList<>();
    //页签对应的页面
    private ArrayList<BasePager> detailBasePagers;

    private DetailPagerAdapter adapter;

    public MainPager(Activity activity) {
        super(activity);
        childrenDatas.add(Contants.FIRST_PAGER);
        childrenDatas.add(Contants.CHINA_PAGER);
        childrenDatas.add(Contants.FORIEN_PAGER);
        childrenDatas.add(Contants.KEHAN_PAGER);
        childrenDatas.add(Contants.JILUPIAN_PAGER);


    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.main_pager, null);
        x.view().inject(this, view);
       // ViewUtils.inject(this,view);//把当前view和Xutils工具绑定
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        //1.在布局中定义
        //2.在代码中实例化
        //3.准备数据-页面
        //4.设置适配器
        adapter = new DetailPagerAdapter();
        detailBasePagers = new ArrayList<>();
        //先把第一个TED的加进去
        //在这里获取顶部轮播图数据，并传给第一个tab
        getTopData();



    }

    private void getTopData() {
        RequestParams params = new RequestParams(Contants.JILUPIAN_PAGER);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess" + result);
                //CacheUtils.putString(context, url, result);//缓存数据
                //初始化各个TAb
                initChildrenData(result);

                // lv_tabdetail_pager.onRefreshFinish(true);//更新刷新时间
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "onError" + ex.getMessage());
                // lv_tabdetail_pager.onRefreshFinish(false);//
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e("TAG", "onCancelled" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                Log.e("TAG", "onFinished");
            }
        });
    }

    private void initChildrenData(String topJson) {
        NetVideoPager netVideoPager = new NetVideoPager(context,topJson);
        detailBasePagers.add(netVideoPager);
        for (int i = 1;i<=childrenDatas.size();i++){
            TableDetailPager tableDetailPager = new TableDetailPager(context,childrenDatas.get(i-1));
            detailBasePagers.add(tableDetailPager);
        }

        vp_detailpager.setAdapter(adapter);
        //关联页签
        tabpager_indicator.setupWithViewPager(vp_detailpager);
        //设置滚动模式
        tabpager_indicator.setTabMode(TabLayout.MODE_SCROLLABLE);
        //要调用以下代码才有效果
        for (int i = 0; i < tabpager_indicator.getTabCount(); i++) {
            TabLayout.Tab tab = tabpager_indicator.getTabAt(i);
            tab.setCustomView(adapter.getTabView(i));
        }
    }

    class DetailPagerAdapter extends PagerAdapter{
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BasePager detailBasePager = detailBasePagers.get(position);
            View rootView = detailBasePager.rootView;
            //记得一定要加载数据
            detailBasePager.initData();
            container.addView(rootView);
            return rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
            container.removeView((View) object);
        }


        /**
         * 用来适配tabLayout,设计布局
         * @param position
         * @return
         */
        public View getTabView(int position){
            View view = LayoutInflater.from(context).inflate(R.layout.tab_item, null);
            TextView tv= (TextView) view.findViewById(R.id.textView);
            switch (position){
                case 0:
                    tv.setText("TED");
                    break;
                case 1:
                    tv.setText("微课学院");
                    break;
                case 2:
                    tv.setText("中国大学");
                    break;
                case 3:
                    tv.setText("外国大学");
                    break;
                case 4:
                    tv.setText("可汗学院");
                    break;
                case 5:
                    tv.setText("纪录片");
                    break;
            }


            return view;
        }
        @Override
        public int getCount() {
            return detailBasePagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
