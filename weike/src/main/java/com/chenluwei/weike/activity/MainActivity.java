package com.chenluwei.weike.activity;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioGroup;

import com.chenluwei.weike.R;
import com.chenluwei.weike.base.BasePager;
import com.chenluwei.weike.fragment.ContentFragment;
import com.chenluwei.weike.fragment.LeftMenuFragment;

import com.chenluwei.weike.pager.AudioPager;
import com.chenluwei.weike.pager.NetAudioPager;
import com.chenluwei.weike.pager.NetVideoPager;
import com.chenluwei.weike.pager.VideoPager;
import com.mxn.soul.flowingdrawer_core.FlowingView;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends  FragmentActivity{

    private LeftDrawerLayout mLeftDrawerLayout;
    private LeftMenuFragment mMenuFragment;
    private FlowingView mFlowingView;

    private RadioGroup rg_main;
    private int position;

    private List<BasePager> basePagers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置主页面
        setContentView(R.layout.activity_main);
        //初始化左侧菜单栏
        LoadLeftMenu();
        //初始化底部RadioGroup
        InitiaRadioGroup();
    }

    private void InitiaRadioGroup() {
        basePagers = new ArrayList<>();
        basePagers.add(new AudioPager(this));
        basePagers.add(new VideoPager(this));
        basePagers.add(new NetVideoPager(this));
        basePagers.add(new NetAudioPager(this));
        rg_main = (RadioGroup)findViewById(R.id.rg_main);
        //设置监听
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        //设置默认选项
        rg_main.check(R.id.rb_video);
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

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
               default:
                   position = 0;
                   break;
                case R.id.rb_video:
                    position = 1;
                    break;
                case R.id.rb_netvideo:
                    position = 2;
                    break;
                case R.id.rb_netaudio:
                    position = 3;
                    break;
            }

            setFragment();
        }
    }

    /**
     * 根据位置得到不同的页面，把视图替换到fragment中
     */
    private void setFragment() {
        //得到fragmentManager
        FragmentManager manager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction ft = manager.beginTransaction();
        //替换(在这里直接new fragment)
        ft.replace(R.id.fl_main_content,new Fragment(){
            @Nullable
            @Override
            /**
             * 初始化fragMent
             */
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                BasePager basePager = getBasePager();
                if(basePager.rootView!=null) {
                    return basePager.rootView;
                }
                return null;
            }
        });
        //提交
        ft.commit();
    }


    /**
     *
     得到相应pager的对象，并数据初始化
     */

    private BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        //注意要判断该页面是否被已经初始化过
        if(basePager != null && !basePager.isInitData) {
            //数据初始化
            basePager.initData();
            basePager.isInitData = true;
        }

        return basePager;
    }

}
