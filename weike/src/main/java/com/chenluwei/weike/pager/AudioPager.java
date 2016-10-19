package com.chenluwei.weike.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chenluwei.weike.R;
import com.chenluwei.weike.base.BasePager;
import com.chenluwei.weike.bean.MediaItem;
import com.chenluwei.weike.media.AudioPlayerActivity;
import com.chenluwei.weike.media.SystemVideoPlayer;
import com.chenluwei.weike.util.TimeUtils;
import com.chenluwei.weike.view.SlideLayout;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by lw on 2016/4/20.
 */
public class AudioPager extends BasePager{
    private ListView lv_video_pager;
    private TextView tv_nodata;
    private ProgressBar pb_loading;
    private ImageView iv_icon;
    private MyAdapter adapter;
    private ArrayList<MediaItem> mediaItems;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e("handleMessage", mediaItems.toString());
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size() > 0) {
                pb_loading.setVisibility(View.GONE);
                tv_nodata.setVisibility(View.GONE);
                lv_video_pager.setAdapter(adapter);
            } else {
                tv_nodata.setVisibility(View.VISIBLE);
                tv_nodata.setText("没有发现音频");
                pb_loading.setVisibility(View.GONE);
            }
        }
    };

    public AudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager, null);
        lv_video_pager = (ListView) view.findViewById(R.id.lv_video_pager);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        return view;
    }

    @Override
    public void initData() {
        adapter = new MyAdapter();
        timeUtils = new TimeUtils();
        super.initData();
        //使用listView
        //1.在布局中定义或者在代码中创建
        //2.实例化ListView
        //3.准备数据
        getData();
        //4.设置适配器
        //5.写适配器item布局
    }

    /**
     * 得到手机里面的视频信息(通过内容提供者)
     */
    private void getData() {
        new Thread() {
            public void run() {
                SystemClock.sleep(1000);
                mediaItems = new ArrayList<>();
                ContentResolver resolver = context.getContentResolver();
                //注意这里uri改成了音频对应的表
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] table = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//在sd卡上的名称
                        MediaStore.Audio.Media.DURATION,//音频的时长
                        MediaStore.Audio.Media.SIZE,//音频的大小
                        MediaStore.Audio.Media.DATA,//音频存储的绝对地址
                        MediaStore.Audio.Media._ID,//音频的ID
                        MediaStore.Audio.Media.ARTIST//音频的艺术家
                };
                Cursor cursor = resolver.query(uri, table, null, null, null);

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem mediaItem = new MediaItem();
                        String name = cursor.getString(0);
                        mediaItem.setName(name);

                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);

                        long size = cursor.getLong(2);
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);
                        mediaItem.setData(data);

                        //获取当前Video对应的Id，然后根据该ID获取其Thumb
                        int id = cursor.getInt(4);
                        Log.e("TAGid", ""+id);
                       // String selection = MediaStore.Video.Thumbnails.VIDEO_ID + "=?";

                        String artist = cursor.getString(5);
                        mediaItem.setArtist(artist);
//
//                        String[] selectionArgs = new String[]{
//                                id + ""
//                        };
                        //查询视频缩略图，此处一直不成功
//                        String[] thumbColumns = new String[]{
//                                MediaStore.Video.Thumbnails.DATA,
//                                MediaStore.Video.Thumbnails.VIDEO_ID};
//
//                        Cursor thumbCursor = resolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbColumns, selection, selectionArgs, null);
//                        Log.e("ImageUrl", thumbCursor.toString());
//                        if (thumbCursor.moveToFirst()) {
//                            String ImageUrl = cursor.getString(0);
//                            Log.e("ImageUrlInto", ImageUrl);
//                            mediaItem.setImageUrl(ImageUrl);
//                        }

                        mediaItems.add(mediaItem);


                    }
                    cursor.close();
                }
                //这里注意不能把适配数据写在分线程里，那样会取不到数据，要写一个handler发送出去
                handler.sendEmptyMessage(0);

            }
        }.start();


    }

    TimeUtils timeUtils;

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mediaItems.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(context, R.layout.item_slide_main, null);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                //找到两个区域布局
                holder.item_slide_content = (RelativeLayout) convertView.findViewById(R.id.item_slide_content);
                holder.item_slide_menu = (RelativeLayout) convertView.findViewById(R.id.item_slide_menu);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final MediaItem mediaItem = mediaItems.get(position);
            String name = mediaItem.getName();
            //这里注意要把时间格式化
            String time = timeUtils.stringForTime((int) mediaItem.getDuration());
            //这里注意要把大小格式化
            String size = Formatter.formatFileSize(context, mediaItem.getSize());


            //视频缩略图地址，此处不成功
            // String imageUrl = mediaItem.getImageUrl();
            holder.tv_name.setText(name);
            holder.tv_time.setText(time);
            holder.tv_size.setText(size);
            holder.iv_icon.setImageResource(R.drawable.music_default_bg);

            holder.item_slide_content.setTag(position);

            //内容区域的点击监听，用来播放
            holder.item_slide_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();//这里的v就是点击的那片区域也就是当前的item_slide_content
                    Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show();

//                    Intent intent = new Intent(context, SystemVideoPlayer.class);
//                    intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//                    context.startActivity(intent);

                    //传递视频列表给播放器
                    Intent intent = new Intent(context, AudioPlayerActivity.class);
//                    //这里进行了序列化
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("videolist", mediaItems);
//                    //传播放列表
//                    intent.putExtras(bundle);
                    //因为播放要知道是哪个视频，所以还要传一下位置

                    //注意这里不需要传序列化的集合了，只需要传一个位置，以为在那边有getdata
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            });
            //   holder.iv_icon.setImageURI(Uri.parse(imageUrl));

            holder.item_slide_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SlideLayout slideLayout = (SlideLayout) v.getParent();
                    slideLayout.closeMenu();
                    File file = new File(mediaItem.getData());
                    if(file.exists()) {
                        file.delete();
                    }
                    mediaItems.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });
            SlideLayout slideLayout = (SlideLayout) convertView;
            //监听回调，用来实现点击别的item时此item就关闭的效果
            slideLayout.setOnStateChangeListener(new MyOnStateChangeListener());
            return convertView;
        }

        class ViewHolder {
            TextView tv_name;
            TextView tv_time;
            TextView tv_size;
            ImageView iv_icon;

            RelativeLayout item_slide_content;
            RelativeLayout item_slide_menu;
        }
    }
    private SlideLayout slideLayout;//用来记录打开的item
    class MyOnStateChangeListener implements SlideLayout.OnStateChangeListener{

        @Override
        public void onClose(SlideLayout layout) {
            if(slideLayout != null) {
                slideLayout = null;
            }
        }

        @Override
        public void onOpen(SlideLayout layout) {
            slideLayout = layout;//用打开的layout给它赋值
        }

        @Override
        public void onDown(SlideLayout layout) {
            if(slideLayout != null & slideLayout!=layout) {//如果已经有打开的，并且不等于目前点击的这个layout
                slideLayout.closeMenu();

            }
        }
    }
}
