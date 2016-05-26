package com.chenluwei.weike.pager;

import android.content.Context;
import android.drm.ProcessedData;
import android.util.Log;
import android.view.View;

import com.chenluwei.weike.R;
import com.chenluwei.weike.adapter.WeiboAdapter;
import com.chenluwei.weike.base.BasePager;
import com.chenluwei.weike.bean.WeiboInfo;
import com.chenluwei.weike.util.Contants;
import com.chenluwei.weike.view.XListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;

/**
 * Created by lw on 2016/4/29.
 */
public class WeiboPager extends BasePager {

    private XListView listView;
    private WeiboAdapter adapter;
    private List<WeiboInfo> weiboInfos;
    private boolean isBmob = true;


    public WeiboPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.weibo_pager,null);
        listView = (XListView) view.findViewById(R.id.weibo_listview);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        //获取数据

            getData();

        //设置适配器
        adapter = new WeiboAdapter(weiboInfos,context);

    }


    /**
     * 从网络获取数据
     */
    private void getData() {
        weiboInfos = new ArrayList<>();
        RequestParams param = new RequestParams(Contants.WEIBO_URL);
        Log.e("weiboInfos_size", Contants.WEIBO_URL);
        x.http().get(param, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                /**
                 * 得到了Json，开始解析数据
                 */
                Log.e("weiboInfos_size", "获取数据成功");
                Log.e("weiboInfos_size", result);
                processedData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("weiboInfos_size", "获取数据失败");
                setAdapter();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void setAdapter() {
        Log.e("weiboInfos_adapter", "进入setAdapter了");
        if(weiboInfos != null && weiboInfos.size() >0){
            //设置适配器
            adapter = new WeiboAdapter(weiboInfos,context);
            listView.setAdapter(adapter);
//                    tv_nodata.setVisibility(View.GONE);
//                    pb_loading.setVisibility(View.GONE);
        }else{
//                    tv_nodata.setVisibility(View.VISIBLE);
//                    pb_loading.setVisibility(View.GONE);
        }
    }

    /**
     * 解析得到的json数据
     * @param result
     */
    private void processedData(String result) {
        Log.e("weiboInfos_processed","进入processedData了");

        try {
            JSONObject  object = new JSONObject(result);
            String json = object.optString("list");

            Gson gson = new Gson();
            Log.e("weiboInfos_processed","进入processedData了222222222");
            weiboInfos = gson.fromJson(json, new TypeToken<List<WeiboInfo>>(){}.getType());
            Log.e("weiboInfos_processed", "333333333333333");
            setAdapter();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
