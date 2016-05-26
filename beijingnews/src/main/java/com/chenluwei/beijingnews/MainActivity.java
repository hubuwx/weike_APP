package com.chenluwei.beijingnews;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.chenluwei.beijingnews.fragment.ContentFragment;
import com.chenluwei.beijingnews.fragment.LeftMenuFragment;
import com.chenluwei.beijingnews.utils.DensityUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {

    public static final String MAIN_TAG = "main_tag";
    public static final String LEFTMENU_TAG = "leftmenu_tag";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //设置主页面
        setContentView(R.layout.content);
        //设置左侧菜单
        setBehindContentView(R.layout.leftmenu);
        //设置模式
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setMode(SlidingMenu.LEFT);
        //设置滑动区域
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //设置主页面占的宽度
        slidingMenu.setBehindOffset(DensityUtil.dip2px(this,200));
        initFragment();
    }

    private void initFragment() {
        //1.得到FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction ft = fm.beginTransaction();
        //3.替换Fragment
        ft.replace(R.id.fl_leftmenu, new LeftMenuFragment(), LEFTMENU_TAG);
        //4.事务提交
        ft.replace(R.id.fl_main, new ContentFragment(), MAIN_TAG);
        ft.commit();
    }

    /**
     * 得到左侧菜单
     * 用TAG查找
     * @return
     */
    public LeftMenuFragment getLeftMenuFragment() {
        FragmentManager fm = getSupportFragmentManager();
        LeftMenuFragment leftMenuFragment = (LeftMenuFragment) fm.findFragmentByTag(LEFTMENU_TAG);
        return leftMenuFragment;
    }

    /**
     * 得到正文Fragment
     * @return
     */
    public ContentFragment getContentFragment() {
        FragmentManager fm = getSupportFragmentManager();
        ContentFragment contentFragment = (ContentFragment) fm.findFragmentByTag(MAIN_TAG);
        return contentFragment;
    }
}
