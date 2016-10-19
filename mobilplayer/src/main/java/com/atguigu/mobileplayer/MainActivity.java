package com.atguigu.mobileplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.atguigu.mobileplayer.base.BasePager;
import com.atguigu.mobileplayer.pager.AudioPager;
import com.atguigu.mobileplayer.pager.NetAudioPager;
import com.atguigu.mobileplayer.pager.NetVideoPager;
import com.atguigu.mobileplayer.pager.VideoPager;

import java.util.ArrayList;

/**
 * 作者：杨光福 on 2016/4/20 09:31
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：主页面
 */
public class MainActivity extends FragmentActivity {
    private RadioGroup rg_main;
    private ArrayList<BasePager> basePagers;

    /**
     * 当前显示哪个页面
     */
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_main = (RadioGroup) findViewById(R.id.rg_main);


        basePagers = new ArrayList<>();
        basePagers.add(new VideoPager(this));//添加视频页面
        basePagers.add(new AudioPager(this));//添加音频页面
        basePagers.add(new NetVideoPager(this));//添加网络视频页面
        basePagers.add(new NetAudioPager(this));//添加网络音乐页面

        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_main.check(R.id.rb_video);//默认选中

    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                default:
                    position = 0;
                    break;
                case R.id.rb_audio:
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
     * 根据位置得到不同的页面，把视图替换到Fragment中
     */
    private void setFragment() {
        //得到FragmentManger
        FragmentManager manager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction ft = manager.beginTransaction();

        //替换
        ft.replace(R.id.fl_main_content, new Fragment() {
            @Nullable
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

                BasePager basePager = getBasePager();
                if (basePager != null) {
                    return basePager.rootView;
                }
                return null;
            }
        });

        //提交
        ft.commit();

    }

    private BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        if (basePager != null && !basePager.isInitData) {
            //数据初始化
            basePager.initData();
            basePager.isInitData = true;
        }
        return basePager;
    }
}
