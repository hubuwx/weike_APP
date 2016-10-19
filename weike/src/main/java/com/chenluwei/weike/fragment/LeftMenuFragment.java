package com.chenluwei.weike.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chenluwei.weike.R;
import com.chenluwei.weike.activity.DownloadActivity;
import com.chenluwei.weike.activity.LoadActivity;
import com.chenluwei.weike.activity.MainActivity;
import com.chenluwei.weike.bean.MyUser;
import com.chenluwei.weike.service.DownloadService;
import com.mxn.soul.flowingdrawer_core.MenuFragment;

import cn.bmob.v3.BmobUser;

/**
 * 左侧Fragment
 * Created by lw on 2016/4/7.
 */
public class LeftMenuFragment extends BaseFragment implements View.OnClickListener {
    private  TextView downnnn;
    private  TextView tv_user;
    private TextView tv_exit;
    private MainActivity mainActivity;
    public LeftMenuFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    //创建特有的view
    @Override
    public View iniView() {
        View view = View.inflate(getActivity(),R.layout.fragment_left_menu,null);
        downnnn = (TextView) view.findViewById(R.id.downnnn);
        tv_user = (TextView) view.findViewById(R.id.tv_user);
        tv_exit = (TextView) view.findViewById(R.id.tv_exit);
     
        //初始化用户信息
        MyUser userInfo = mainActivity.getUserInfo();
        tv_user.setText("欢迎您："+userInfo.getUsername());
        tv_user.setOnClickListener(this);
        tv_exit.setOnClickListener(this);
        downnnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mMainActivity, DownloadActivity.class);
                startActivity(intent);
            }
        });
        return  setupReveal(view) ;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_user:

                break;
            case R.id.tv_exit:
                Log.e("TAGxxxxx", "tv_exit");
                BmobUser.logOut(mMainActivity);   //清除缓存用户对象
                Intent intent = new Intent(mainActivity, LoadActivity.class);
                startActivity(intent);
                mMainActivity.finish();
                break;
        }
    }
}
