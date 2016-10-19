package com.chenluwei.beijingnews.fragment;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chenluwei.beijingnews.MainActivity;
import com.chenluwei.beijingnews.R;
import com.chenluwei.beijingnews.base.BaseFragment;
import com.chenluwei.beijingnews.base.BasePager;
import com.chenluwei.beijingnews.pager.GovaffairPager;
import com.chenluwei.beijingnews.pager.HomePager;
import com.chenluwei.beijingnews.pager.NewscenterPager;
import com.chenluwei.beijingnews.pager.SettingPager;
import com.chenluwei.beijingnews.pager.SmartService;
import com.chenluwei.beijingnews.view.NoScrollViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by lw on 2016/5/18.
 */
public class ContentFragment extends BaseFragment{
    private static final String TAG = LeftMenuFragment.class.getSimpleName();
    @ViewInject(R.id.vp_content)
    private NoScrollViewPager vp_content;
    @ViewInject(R.id.rg_bottom_tag)
    private RadioGroup rg_bottom_tag;
    @ViewInject(R.id.rb_home)
    private RadioButton rb_home;
    /**
     *
     */
    private ArrayList<BasePager> basePagers;


    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.content_fragment,null);
        //把当前的视图通过xutils注入到fragment
        x.view().inject(this,view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        //1.在布局文件中定义 2代码中实例化 3准备数据 4设置适配器
        basePagers = new ArrayList<>();
        basePagers.add(new HomePager(context));//主界面
        basePagers.add(new NewscenterPager(context));//新闻中心
        basePagers.add(new SmartService(context));//智慧服务
        basePagers.add(new GovaffairPager(context));//政要指南
        basePagers.add(new SettingPager(context));//设置中心

        //设置适配器
        vp_content.setAdapter(new ContentFragmentAdapter());
        //设置RadioGroup状态的监听
        rb_home.setChecked(true);
        basePagers.get(0).initData();
        isEnableSlidingMenu(false);//设置SlidingMenu一进来时不可以滑动
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

        //监听页面的改变
        vp_content.addOnPageChangeListener(new MyOnPageChangeListener());
    }

    /**
     * 得到新闻中心页面
     * @return
     */
    public NewscenterPager getNewscenterPager() {
        return (NewscenterPager) basePagers.get(1);
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //当选择某个页面的时候调用它的initData()
            basePagers.get(position).initData();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{
        //根据选中的RadioButton切换到Viewpager不同页面
        // vp_content.setCurrentItem(0);
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_home ://首页
                    vp_content.setCurrentItem(0,false);
                    //不可以滑动
                    isEnableSlidingMenu(false);
                    break;
                case R.id.rb_newscenter ://新闻中心
                    vp_content.setCurrentItem(1,false);
                    //可以滑动
                    isEnableSlidingMenu(true);
                    break;
                case R.id.rb_smartservice ://智慧服务
                    vp_content.setCurrentItem(2,false);
                    isEnableSlidingMenu(false);
                    break;
                case R.id.rb_govaffair ://正要指南
                    vp_content.setCurrentItem(3,false);
                    isEnableSlidingMenu(false);
                    break;
                case R.id.rb_setting ://设置中心
                    vp_content.setCurrentItem(4,false);
                    isEnableSlidingMenu(false);
                    break;
            }
        }
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

    class ContentFragmentAdapter extends PagerAdapter{
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BasePager basePager = basePagers.get(position);//具体的某个页面
            View rootView = basePager.rootView;
            //因为适配器会一次初始化3个页面所以不能在这里初始化
            //basePager.initData();//调用各个页面的initData()
            container.addView(rootView);
            return rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
//            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return basePagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
