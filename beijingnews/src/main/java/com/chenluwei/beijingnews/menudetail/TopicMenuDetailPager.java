package com.chenluwei.beijingnews.menudetail;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;


import com.chenluwei.beijingnews.MainActivity;
import com.chenluwei.beijingnews.R;
import com.chenluwei.beijingnews.base.MenuDetailBasePager;
import com.chenluwei.beijingnews.domain.NewsCenterBean2;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：杨光福 on 2016/5/4 16:19
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：专题菜单详情页面
 */
public class TopicMenuDetailPager extends MenuDetailBasePager {
    @ViewInject(R.id.vp_news_mewnu_detailpager)
    private ViewPager vp_news_mewnu_detailpager;
    @ViewInject(R.id.tabpager_indicator)
    private TabPageIndicator tabpager_indicator;

    /**
     * 左侧菜单对应详情页签页面的数据
     */
    private List<NewsCenterBean2.NewsCenterData.ChrildrenData> childrenDatas;
    /**
     * 左侧菜单对应新闻详情页面的集合
     */
    private ArrayList<MenuDetailBasePager> detailBasePagers;

    public TopicMenuDetailPager(Context context, NewsCenterBean2.NewsCenterData newsCenterData) {
        super(context);
        childrenDatas =newsCenterData.getChildren();
    }

    @Override
    public View initView() {
        //设置内容
        View view = View.inflate(context, R.layout.news_menu_detail_pager,null);
        x.view().inject(this,view);
        return view;
    }

    /**
     * 最右边箭头的点击事件
     * @param view
     */
    @Event(value = R.id.ib_next_tab)
    private void nextTab(View view){
        //往后移动一个
        vp_news_mewnu_detailpager.setCurrentItem(vp_news_mewnu_detailpager.getCurrentItem() + 1);
    }

    @Override
    public void initData() {
        super.initData();
        //1.在布局中定义
        //2.在代码中实例化
        //3.准备数据-页面
        //4.设置适配器

        detailBasePagers = new ArrayList<>();
        for (int i = 0;i<childrenDatas.size();i++){
            TopicTabDetailPager tableDetailPager = new TopicTabDetailPager(context,childrenDatas.get(i));
            detailBasePagers.add(tableDetailPager);
        }

        vp_news_mewnu_detailpager.setAdapter(new NewsMenuDetailPagerAdapter());
        //5.关联viewpager，tabpagerindicator才可以显示
        tabpager_indicator.setViewPager(vp_news_mewnu_detailpager);
        //设置页面的监听需要用tabIndicator
        tabpager_indicator.setOnPageChangeListener(new MyOnPageChangeListener());
    }
    /**
     * 是否让slidingMenu可以滑动
     * @param isEnableSlidingMenu
     */
    private void isEnableSlidingMenu(boolean isEnableSlidingMenu) {
        MainActivity mainActivity = (MainActivity) context;
        SlidingMenu slidingMenu = mainActivity.getSlidingMenu();
        if(isEnableSlidingMenu) {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        }else {

            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }
    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(position == 0) {
                //可以侧滑
                isEnableSlidingMenu(true);
            }else {
                //不可以侧滑
                isEnableSlidingMenu(false);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 适配器
     */
    class  NewsMenuDetailPagerAdapter extends PagerAdapter{

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //从集合中获取某一个界面
            MenuDetailBasePager detailBasePager = detailBasePagers.get(position);
            View rootView = detailBasePager.rootView;
            detailBasePager.initData();
            container.addView(rootView);
            return rootView;
        }

        /**
         * 重写该方法，用来给指示器标题
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return childrenDatas.get(position).getTitle();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
            container.removeView((View) object);
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