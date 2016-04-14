package com.chenluwei.weike.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.chenluwei.weike.R;
import com.chenluwei.weike.fragment.ContentFragment;
import com.chenluwei.weike.fragment.LeftMenuFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.mxn.soul.flowingdrawer_core.FlowingView;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;

public class MainActivity extends  FragmentActivity{

    private LeftDrawerLayout mLeftDrawerLayout;
    private LeftMenuFragment mMenuFragment;
    private FlowingView mFlowingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置主页面
        setContentView(R.layout.activity_main);
        //加载左侧菜单栏
        LoadLeftMenu();

    }

    //加载左侧菜单栏
    private void LoadLeftMenu() {
        mLeftDrawerLayout = (LeftDrawerLayout)findViewById(R.id.leftDrawerLayout);

        FragmentManager fm = getSupportFragmentManager();
        mMenuFragment = (LeftMenuFragment) fm.findFragmentById(R.id.id_container_menu);
        mFlowingView = (FlowingView) findViewById(R.id.sv);

        if (mMenuFragment == null) {
            //填充自己的fragment
            //其本质还是动态加载Fragment
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment = new LeftMenuFragment()).commit();
        }
        mLeftDrawerLayout.setFluidView(mFlowingView);
        mLeftDrawerLayout.setMenuFragment(mMenuFragment);
    }


}
