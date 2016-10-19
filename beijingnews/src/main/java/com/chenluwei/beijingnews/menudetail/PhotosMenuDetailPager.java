package com.chenluwei.beijingnews.menudetail;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.chenluwei.beijingnews.R;
import com.chenluwei.beijingnews.base.MenuDetailBasePager;
import com.chenluwei.beijingnews.domain.PhotosMenuDetailPagerBean;
import com.chenluwei.beijingnews.utils.CacheUtils;
import com.chenluwei.beijingnews.utils.LogUtil;
import com.chenluwei.beijingnews.utils.Url;
import com.chenluwei.beijingnews.volley.VolleyManager;
import com.google.gson.Gson;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by lw on 2016/5/19.
 * 图组菜单详情页面
 */
public class PhotosMenuDetailPager extends MenuDetailBasePager {


    @ViewInject(R.id.listview)
    private ListView listView;
    @ViewInject(R.id.gridview)
    private GridView gridView;
    private  MyPhotosAdapter adapter;
    private String url;
    /**
     * 图组的数据
     */
    private List<PhotosMenuDetailPagerBean.DataEntity.NewsEntity> news;
    private ImageLoader imageLoader ;

    public PhotosMenuDetailPager(Context context) {
        super(context);
        imageLoader = VolleyManager.getImageLoader();
        adapter = new MyPhotosAdapter();
    }

    @Override
    public View initView() {
        //设置内容
       View view = View.inflate(context, R.layout.photos_menu_detail_pager,null);
        //listView = (ListView) view.findViewById(R.id.listview);
        //注意这里注入要填this，不能填context
        x.view().inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();

        url = Url.PHOTOS_URL;
        String saveJson = CacheUtils.getString(context, url);
        if(!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }
        //默认设置listView的数据
        getDataFromNetByVolley();
    }

    private void getDataFromNetByVolley() {
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogUtil.e("图组数据请求成功====="+s);
                //联网成功
                //缓存文本数据
                CacheUtils.putString(context,url,s);
                processData(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.e("图组数据请求失败====="+volleyError.getMessage());
            }
        }){
            //解决乱码问题

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String parsed = new String(response.data,"UTF-8");
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };

        //添加到队列里
        VolleyManager.addRequest(request, "photosmenuDetailPager");
    }

    /**
     * 解析数据，显示数据
     * @param json
     */
    private void processData(String json) {
        //解析数据

        PhotosMenuDetailPagerBean bean = new Gson().fromJson(json,PhotosMenuDetailPagerBean.class);
        String title = bean.getData().getNews().get(0).getTitle();
        LogUtil.e(title+"-----------------");
        //显示数据
        news = bean.getData().getNews();
        //设置listView的数据

        listView.setAdapter(adapter);
    }

    /**
     * true:显示listview，隐藏gridview
     * false:显示gridview，隐藏listiew
     */
    private boolean isShowListView = true;
    /**
     * listview和gridview切换
     * @param iv_switch
     */
    public void switchListAndGrid(ImageButton iv_switch) {
        if(isShowListView) {
            //显示gridview
            gridView.setVisibility(View.VISIBLE);
            gridView.setAdapter(new MyPhotosAdapter());
            //隐藏listview
            listView.setVisibility(View.GONE);
            //按钮显示listview
            iv_switch.setImageResource(R.drawable.icon_pic_list_type);
            isShowListView = false;
        }else {
            //显示listview
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(new MyPhotosAdapter());
            //隐藏gridview
            gridView.setVisibility(View.GONE);
            //显示按钮gridview
            iv_switch.setImageResource(R.drawable.icon_pic_grid_type );
            isShowListView = true;
        }
    }

    class MyPhotosAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(context,R.layout.item_photos,null);
                holder.iv_photos_icon = (ImageView) convertView.findViewById(R.id.iv_photos_icon);
                holder.tv_photos_title = (TextView) convertView.findViewById(R.id.tv_photo_title);
                convertView.setTag(holder);

            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            PhotosMenuDetailPagerBean.DataEntity.NewsEntity newsEntity = news.get(position);
            holder.tv_photos_title.setText(newsEntity.getTitle());
            //请求图片
            loaderImager(holder,newsEntity.getListimage().replace("10.0.2.2","192.168.56.1"));

            return convertView;
        }
    }
    /**
     *用Volley加载图片的模板代码
     * @param viewHolder
     * @param imageurl
     */
    private void loaderImager(final ViewHolder viewHolder, String imageurl) {

        viewHolder.iv_photos_icon.setTag(imageurl);
        //直接在这里请求会乱位置
        ImageLoader.ImageListener listener = new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if (imageContainer != null) {

                    if (viewHolder.iv_photos_icon != null) {
                        if (imageContainer.getBitmap() != null) {
                            viewHolder.iv_photos_icon.setImageBitmap(imageContainer.getBitmap());
                        } else {
                            viewHolder.iv_photos_icon.setImageResource(R.drawable.pic_item_list_default);
                        }
                    }
                }
            }
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //如果出错，则说明都不显示（简单处理），最好准备一张出错图片
                viewHolder.iv_photos_icon.setImageResource(R.drawable.pic_item_list_default);
            }
        };
        imageLoader.get(imageurl, listener);
    }
    static class ViewHolder{
        ImageView iv_photos_icon;
        TextView tv_photos_title;
    }
}
