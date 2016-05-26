package com.chenluwei.weike.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chenluwei.weike.R;
import com.chenluwei.weike.bean.WeiboInfo;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import pl.droidsonroids.gif.GifImageView;



/**
 * Created by lw on 2016/4/29.
 */
public class WeiboAdapter extends BaseAdapter {
    private static final int TYPE_VIDEO = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_TEXT = 2;
    private static final int TYPE_GIF = 3;
    private static final int TYPE_AD = 4;
    private List<WeiboInfo> weiboInfos;
    private Context context;
    private ImageOptions options;
    private ImageOptions optionsMiddle;

    public WeiboAdapter(List<WeiboInfo> weiboInfos, Context context) {
        this.weiboInfos = weiboInfos;
        this.context = context;
        //头像图片的设置
        options = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(60), DensityUtil.dip2px(60))
                .setRadius(DensityUtil.dip2px(50))
                        // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(false) // 很多时候设置了合适的scaleType也不需要它.
                        // 加载中或错误图片的ScaleType
                        //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setLoadingDrawableId(R.drawable.head_portrait)
                .setFailureDrawableId(R.drawable.head_portrait)
                .build();

        optionsMiddle = new ImageOptions.Builder()
               // .setSize(DensityUtil.dip2px(100), DensityUtil.dip2px(100))
                //.setRadius(DensityUtil.dip2px(20))
                        // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(false) // 很多时候设置了合适的scaleType也不需要它.
                        // 加载中或错误图片的ScaleType
                        //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setLoadingDrawableId(R.drawable.bg_item)
                .setFailureDrawableId(R.drawable.bg_item)
                .build();
    }

    /**
     * 数据类型的数量
     *
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        //得到的数据的类型
        int itemViewType = -1;
        //根据位置，从列表中得到一个数据对象
        WeiboInfo weiboInfo = weiboInfos.get(position);
        String type = weiboInfo.getType();
        if ("video".equals(type)) {
            itemViewType = TYPE_VIDEO;
        } else if ("image".equals(type)) {
            itemViewType = TYPE_IMAGE;
        } else if ("text".equals(type)) {
            itemViewType = TYPE_TEXT;
        } else if ("gif".equals(type)) {
            itemViewType = TYPE_GIF;
        } else {
            itemViewType = TYPE_AD;
        }//这里没有处理广告
        return itemViewType;
    }

    @Override
    public int getCount() {

        return weiboInfos.size();
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
        ViewHolder holder = null;
        TextViewHolder textHolder = null;
        ImageViewHolder imageHolder = null;
        GifViewHolder gifHolder = null;
        VideoViewHolder videoHolder = null;

        WeiboInfo weiboInfo = weiboInfos.get(position);

        int itemViewType = getItemViewType(position);

        if (convertView == null) {

            //不同的中间部分，分类处理
            switch (itemViewType) {
                case TYPE_TEXT:
                    textHolder = new TextViewHolder();
                    convertView = View.inflate(context, R.layout.item_weibo_text, null);
                    findPublicView(convertView, textHolder);
                    textHolder.tv_middle_text = (TextView) convertView.findViewById(R.id.tv_middle_text);

                    convertView.setTag(textHolder);
                    break;
                case TYPE_IMAGE:
                    imageHolder = new ImageViewHolder();
                    convertView = View.inflate(context, R.layout.item_weibo_image, null);
                    findPublicView(convertView, imageHolder);
                    imageHolder.tv_middle_text = (TextView) convertView.findViewById(R.id.tv_middle_text);
                    imageHolder.iv_image_middle = (ImageView) convertView.findViewById(R.id.iv_image_middle);

                    convertView.setTag(imageHolder);
                    break;
                case TYPE_GIF:
                   gifHolder = new GifViewHolder();
                   convertView = View.inflate(context, R.layout.item_weibo_gif, null);
                    findPublicView(convertView, gifHolder);
                    gifHolder.tv_middle_text = (TextView) convertView.findViewById(R.id.tv_middle_text);
                    gifHolder.gif_middle = (GifImageView) convertView.findViewById(R.id.gif_middle);
                    convertView.setTag(gifHolder);
                    break;
                case TYPE_VIDEO:
                   videoHolder = new VideoViewHolder();
                   convertView = View.inflate(context, R.layout.item_weibo_video, null);
                    findPublicView(convertView, videoHolder);
                    videoHolder.tv_middle_text = (TextView) convertView.findViewById(R.id.tv_middle_text);
                    videoHolder.jcvideo_middle = (JCVideoPlayer) convertView.findViewById(R.id.jcvideo_middle);
                    convertView.setTag(videoHolder);
                    break;
                case TYPE_AD:
                    return null;

            }

        } else {
            switch (itemViewType) {
                case TYPE_TEXT:
                    textHolder = (TextViewHolder) convertView.getTag();
                    Log.e("lllllooooo", "赋值"+textHolder.toString());
                    break;
                case TYPE_IMAGE:
                    imageHolder = (ImageViewHolder) convertView.getTag();
                    Log.e("lllllooooo", "赋值"+imageHolder.toString());
                    break;
                case TYPE_GIF:
                    gifHolder = (GifViewHolder) convertView.getTag();
                    Log.e("lllllooooo", "赋值"+gifHolder.toString());
                    break;
                case TYPE_VIDEO:
                    videoHolder = (VideoViewHolder) convertView.getTag();
                    Log.e("lllllooooo", "赋值"+videoHolder.toString());
                    break;
                case TYPE_AD:
                    Log.e("lllllooooo", "广告");
                    return null;

            }
        }


            //公共部分赋值

            Log.e("mmmmmmm", weiboInfo.getU().getName());
            Log.e("mmmmmmm", weiboInfo.getPasstime());


            //各自部分赋值
            switch (itemViewType) {
                case TYPE_TEXT:
                    Log.e("lllllooooo", textHolder.toString());
                    setPublicValue(textHolder, weiboInfo);

                    textHolder.tv_middle_text.setText(weiboInfo.getText());
                    break;
                case TYPE_IMAGE:
                    Log.e("lllllooooo", imageHolder.toString());
                    setPublicValue(imageHolder, weiboInfo);

                    imageHolder.tv_middle_text.setText(weiboInfo.getText());
                    x.image().bind(imageHolder.iv_image_middle, weiboInfo.getImage().getBig().get(0),optionsMiddle);
                    break;
                case TYPE_GIF:
                    Log.e("lllllooooo", gifHolder.toString());
                    setPublicValue(gifHolder, weiboInfo);

                    gifHolder.tv_middle_text.setText(weiboInfo.getText());
                    Glide.with(context).load(weiboInfo.getGif().getImages().get(0)).into(gifHolder.gif_middle);
                    break;
                case TYPE_VIDEO:
                    Log.e("lllllooooo", "position"+position);
                    Log.e("lllllooooo", videoHolder.toString());
                    setPublicValue(videoHolder, weiboInfo);

                    videoHolder.tv_middle_text.setText(weiboInfo.getText());
                    //设置视频地址、缩略图地址、标题,不显示标题的标志
                    Log.e("mmmmjcvideo_middle", videoHolder.jcvideo_middle.toString());
                    videoHolder.jcvideo_middle.setUp(weiboInfo.getVideo().getVideo().get(0),
                            weiboInfo.getVideo().getThumbnail().get(0), "", false);
                    break;
            }

            return convertView;
        }

    private void setPublicValue(ViewHolder holder, WeiboInfo weiboInfo) {
        Log.e("llllllll",  holder.toString());
        Log.e("llllll",  holder.tv_head_name.toString());
        holder.tv_head_name.setText(weiboInfo.getU().getName());
        holder.tv_head_time.setText(weiboInfo.getPasstime());
        x.image().bind(holder.iv_head_icon, weiboInfo.getU().getHeader().get(0), options);
    }

    private void findPublicView(View convertView, ViewHolder holder) {
        holder.tv_head_name = (TextView) convertView.findViewById(R.id.tv_head_name);
        holder.iv_head_icon = (ImageView) convertView.findViewById(R.id.iv_head_icon);
        holder.tv_head_time = (TextView) convertView.findViewById(R.id.tv_head_time);
    }


    public class TextViewHolder extends ViewHolder {


        //只有文字类型的Viewholder
        TextView tv_middle_text;

    }

    public class ImageViewHolder extends ViewHolder{


        //只有文字类型的Viewholder
        TextView tv_middle_text;
        //图片类型的ViewHolder
        ImageView iv_image_middle;


    }

    public class GifViewHolder extends ViewHolder{


        //只有文字类型的Viewholder
        TextView tv_middle_text;
        //GIF类TYPE_IMAGE型的ViewHolder
        GifImageView gif_middle;


    }

    public class VideoViewHolder extends ViewHolder{


        //只有文字类型的Viewholder
        TextView tv_middle_text;
        //视频类型的ViewHolder
        JCVideoPlayer jcvideo_middle;

    }

    private class ViewHolder {
        ImageView iv_head_icon;
        TextView tv_head_name;
        TextView tv_head_time;


    }
}






