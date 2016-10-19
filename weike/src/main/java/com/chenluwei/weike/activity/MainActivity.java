package com.chenluwei.weike.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.chenluwei.weike.R;
import com.chenluwei.weike.base.BasePager;
import com.chenluwei.weike.bean.MyUser;
import com.chenluwei.weike.fragment.ContentFragment;
import com.chenluwei.weike.fragment.LeftMenuFragment;

import com.chenluwei.weike.pager.AudioPager;
import com.chenluwei.weike.pager.MainPager;
import com.chenluwei.weike.pager.NetAudioPager;
import com.chenluwei.weike.pager.NetVideoPager;
import com.chenluwei.weike.pager.VideoPager;
import com.chenluwei.weike.pager.WeiboPager;
import com.mxn.soul.flowingdrawer_core.FlowingView;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends  FragmentActivity{

    private LeftDrawerLayout mLeftDrawerLayout;
    private LeftMenuFragment mMenuFragment;
    private FlowingView mFlowingView;

    private RadioGroup rg_main;
    private int position;
    private EditText tv_serach;
    private List<BasePager> basePagers;
    private Button rb_mine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置主页面
        setContentView(R.layout.activity_main);
        //获取到用户信息
        getUserInfo();
        rb_mine = (Button)findViewById(R.id.rb_mine);
        tv_serach = (EditText)findViewById(R.id.tv_serach);
        tv_serach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        //初始化左侧菜单栏
        LoadLeftMenu();
        //初始化底部RadioGroup
        InitiaRadioGroup();
    }

    /**
     * 获取到用户信息
     */
    public MyUser getUserInfo() {
        MyUser user = (MyUser) getIntent().getSerializableExtra("user");
        return user;

    }

    private void InitiaRadioGroup() {
        basePagers = new ArrayList<>();
        basePagers.add(new MainPager(this));
        //basePagers.add(new NetVideoPager(this));
        basePagers.add(new VideoPager(this));
        basePagers.add(new AudioPager(this));
        //basePagers.add(new WeiboPager(this));
       // basePagers.add(new NetAudioPager(this));
        rg_main = (RadioGroup)findViewById(R.id.rg_main);
        //设置监听
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rb_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeftDrawerLayout.openDrawer();
            }
        });
        //设置默认选项
        rg_main.check(R.id.rb_main);
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
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment = new LeftMenuFragment(this)).commit();
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
                case R.id.rb_channel:
                    position = 1;
                    break;
                case R.id.rb_outline:
                    position = 2;
                    break;
//                case R.id.rb_mine:
//
//                       mLeftDrawerLayout.openDrawer();
//
//                    break;
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
        ft.replace(R.id.fl_main_content,new ContentFragment(basePagers,position));
        //提交
        ft.commit();
    }



}
