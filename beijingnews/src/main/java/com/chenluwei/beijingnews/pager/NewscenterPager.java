package com.chenluwei.beijingnews.pager;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chenluwei.beijingnews.MainActivity;
import com.chenluwei.beijingnews.base.BasePager;
import com.chenluwei.beijingnews.base.MenuDetailBasePager;
import com.chenluwei.beijingnews.domain.NewsCenterBean2;
import com.chenluwei.beijingnews.fragment.LeftMenuFragment;
import com.chenluwei.beijingnews.menudetail.InteracMenuDetailPager;
import com.chenluwei.beijingnews.menudetail.NewsMenuDetailPager;
import com.chenluwei.beijingnews.menudetail.PhotosMenuDetailPager;
import com.chenluwei.beijingnews.menudetail.TopicMenuDetailPager;
import com.chenluwei.beijingnews.menudetail.TopicTabDetailPager;
import com.chenluwei.beijingnews.utils.CacheUtils;
import com.chenluwei.beijingnews.utils.Url;
import com.chenluwei.beijingnews.volley.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lw on 2016/5/18.
 */
public class NewscenterPager extends BasePager {
    private static final String TAG = NewscenterPager.class.getSimpleName();
    public String url;
    /**
     * 左侧菜单对应的页面
     */
    private ArrayList<MenuDetailBasePager> menuDetailBasePagers;
    /**
     * 左侧菜单对应的数据
     */
    private List<NewsCenterBean2.NewsCenterData> leftMenuData;

    public NewscenterPager(Context context) {
        super(context);
    }


    @Override
    public void initData() {
        super.initData();
        //设置显示侧滑按钮
        ib_menu.setVisibility(View.VISIBLE);
        //设置标题
        tv_title.setText("新闻");
        //设置内容
        TextView textView = new TextView(context);
        textView.setText("新闻中心内容");
        textView.setTextSize(25);
        textView.setTextColor(Color.RED);
        //将子视图添加到FramLayout中
        fl_base_content.addView(textView);
        url = Url.NEWCENTER_URL;
        String saveJson = CacheUtils.getString(context,url);//工具类里默认值是返回""
        if(!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }
        //联网请求数据
        //getDataFromNet();
        //用volley请求数据
        getDataFromUseVolley();
    }

    /**
     * 用Volley请求数据
     */
    private void getDataFromUseVolley() {
        //注意要有请求队列，然后把请求加到队列里
      //  RequestQueue queue = Volley.newRequestQueue(context);
        //创建请求
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e(TAG, "请求成功==" + s);
                //解析数据
                processData(s);
                //数据缓存
                CacheUtils.putString(context,url,s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "请求错误=="+volleyError.getMessage());
            }
        }){//解决乱码的办法
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                //很怪的写法
                try {
                    String parsed = new String(response.data,"UTF-8");
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };
        //别忘了把请求加到队列里
        VolleyManager.addRequest(request,"NewsCenterPager");
    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "请求成功==" + result);
                //解析数据
                processData(result);
                //数据缓存
                CacheUtils.putString(context,url,result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "请求错误=="+ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "请求取消=="+cex);
            }

            @Override
            public void onFinished() {
                Log.e(TAG, "请求完成==");
            }
        });
    }

    /**
     * 解析和显示数据
     * @param result
     */
    private void processData(String result) {
        Log.e(TAG, "processData=====");
        //解析数据：手动和第三方
        //gson解析：1.创建bean对象 2.使用gson解析
        //手动解析 1.创建Bean对象
//        NewsCenterBean newsCenterBean = new Gson().fromJson(result,NewsCenterBean.class);
//        leftMenuData = newsCenterBean.getData();

        NewsCenterBean2 newsCenterBean = paseJson(result);
        leftMenuData = newsCenterBean.getData();

        //创建各个菜单页面
        //注意这块要放到前面，否则leftfragment调用switchPager()的时候会空指针
        menuDetailBasePagers = new ArrayList<>();
        menuDetailBasePagers.add(new NewsMenuDetailPager(context,leftMenuData.get(0)));
        menuDetailBasePagers.add(new TopicMenuDetailPager(context,leftMenuData.get(0)));
        menuDetailBasePagers.add(new PhotosMenuDetailPager(context));
        menuDetailBasePagers.add(new InteracMenuDetailPager(context));

        //把数据传递到左侧菜单
        MainActivity mainActivity = (MainActivity) context;
        //得到左侧菜单
        LeftMenuFragment leftMenuFragment = mainActivity.getLeftMenuFragment();
        //把请求来的网络数据传给左侧菜单
        leftMenuFragment.setData(leftMenuData);



    }

    /**
     * 手动解析，用系统的API解析json
     * @param json
     * @return
     */
    private NewsCenterBean2 paseJson(String json) {
        NewsCenterBean2 centerBean2 = new NewsCenterBean2();
        try {
            JSONObject jsonObject = new JSONObject(json);

            int retcode = jsonObject.optInt("retcode");
            centerBean2.setRetcode(retcode);

            JSONArray data = jsonObject.optJSONArray("data");
            if(data != null && data.length() > 0) {
                List<NewsCenterBean2.NewsCenterData> dataBean = new ArrayList<NewsCenterBean2.NewsCenterData>();
                //放这也可以，注意java的引用原理
                centerBean2.setData(dataBean);
                for (int i = 0;i<data.length();i++) {
                    JSONObject jsonObject1 = (JSONObject) data.get(i);
                    if(jsonObject1 != null) {
                        NewsCenterBean2.NewsCenterData newsCenterData = new NewsCenterBean2.NewsCenterData();

                        int id = jsonObject1.optInt("id");
                        newsCenterData.setId(id);
                        String title = jsonObject1.optString("title");
                        newsCenterData.setTitle(title);
                        int type = jsonObject1.optInt("type");
                        newsCenterData.setType(type);
                        String url = jsonObject1.optString("url");
                        newsCenterData.setUrl(url);

                        String url1 = jsonObject1.optString("url1");
                        newsCenterData.setUrl(url1);

                        String dayurl = jsonObject1.optString("dayurl");
                        newsCenterData.setDayurl(dayurl);
                        String excurl = jsonObject1.optString("excurl");
                        newsCenterData.setExcurl(excurl);
                        String weekurl = jsonObject1.optString("weekurl");
                        newsCenterData.setWeekurl(weekurl);
                        dataBean.add(newsCenterData);

                        JSONArray childrenData = jsonObject1.optJSONArray("children");
                        if(childrenData != null && childrenData.length() > 0) {
                            List<NewsCenterBean2.NewsCenterData.ChrildrenData> children = new ArrayList<>();
                            newsCenterData.setChildren(children);
                            for (int  j= 0;j< childrenData.length();j++){
                                JSONObject childrenJson = (JSONObject) childrenData.get(j);
                                if(childrenJson != null) {
                                    NewsCenterBean2.NewsCenterData.ChrildrenData childrenData1 = new NewsCenterBean2.NewsCenterData.ChrildrenData();
                                    int idc = childrenJson.optInt("id");
                                    childrenData1.setId(idc);
                                    String titlec = childrenJson.optString("title");
                                    childrenData1.setTitle(titlec);
                                    int typec = childrenJson.optInt("type");
                                    childrenData1.setType(typec);
                                    String urlc = childrenJson.optString("url");
                                    childrenData1.setUrl(urlc);

                                    children.add(childrenData1);
                                }
                            }
                        }
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return centerBean2;
    }

    /**
     * 根据位置切换到对应的菜单页面
     * @param selectPosition
     */
    public void switchPager(int selectPosition) {
        //设置标题
        tv_title.setText(leftMenuData.get(selectPosition).getTitle());
        MenuDetailBasePager menuDetailBasePager = menuDetailBasePagers.get(selectPosition);
        View rootView = menuDetailBasePager.rootView;
        //初始化数据
        menuDetailBasePager.initData();
        fl_base_content.removeAllViews();
        fl_base_content.addView(rootView);

        if(selectPosition == 2) {
            //图组
        ib_switch_list_grid.setVisibility(View.VISIBLE);
        ib_switch_list_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotosMenuDetailPager pager = (PhotosMenuDetailPager) menuDetailBasePagers.get(2);
                pager.switchListAndGrid(ib_switch_list_grid);
            }
        });
        }else {
            ib_switch_list_grid.setVisibility(View.GONE);
        }
    }
}
