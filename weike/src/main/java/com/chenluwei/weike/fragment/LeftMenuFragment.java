package com.chenluwei.weike.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chenluwei.weike.R;
import com.chenluwei.weike.activity.MainActivity;
import com.mxn.soul.flowingdrawer_core.MenuFragment;

/**
 * 左侧Fragment
 * Created by lw on 2016/4/7.
 */
public class LeftMenuFragment extends BaseFragment {

    //创建特有的view
    @Override
    public View iniView() {
        View view = View.inflate(getActivity(),R.layout.fragment_left_menu,null);

        return  setupReveal(view) ;
    }


}
