package com.chenluwei.weike.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chenluwei.weike.R;
import com.chenluwei.weike.activity.DownloadActivity;
import com.chenluwei.weike.activity.MainActivity;
import com.chenluwei.weike.service.DownloadService;
import com.mxn.soul.flowingdrawer_core.MenuFragment;

/**
 * 左侧Fragment
 * Created by lw on 2016/4/7.
 */
public class LeftMenuFragment extends BaseFragment {
    private  TextView downnnn;
    //创建特有的view
    @Override
    public View iniView() {
        View view = View.inflate(getActivity(),R.layout.fragment_left_menu,null);
        downnnn = (TextView) view.findViewById(R.id.downnnn);
        downnnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mMainActivity, DownloadActivity.class);
                startActivity(intent);
            }
        });
        return  setupReveal(view) ;
    }


}
