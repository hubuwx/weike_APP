package com.chenluwei.weike.media;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.chenluwei.weike.R;
import com.chenluwei.weike.bean.MediaItem;
import com.chenluwei.weike.util.TimeUtils;
import com.chenluwei.weike.util.WkUtils;
import com.chenluwei.weike.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by lw on 2016/4/21.
 */
public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    private static final int SHOW_NETSPEED = 5;
    private TextView tv_loading_netspeed;
    private static final int PROGRESSUPDATE = 1;//视频进度更新
    private static final int HIDE_MEDIA_CONTROLLER = 2;
    private static final int FULL_SCREEN = 3;//全屏播放
    private static final int DEFAULT_SCREEN = 4;//默认屏幕播放
    private Uri uri;
    private VideoView videoview;
    private LinearLayout ll_video_buffer;
    private ArrayList<MediaItem> mediaItems;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvTime;
    private Button btnVideoVoice;
    private SeekBar seekBarVoice;
    private LinearLayout ll_video_loding;
    private Button btnVideoSwitchPlayer;
    private LinearLayout llBottom;
    private TextView tv_current_time;
    private SeekBar seekBarVideo;
    private TextView tvDuration;
    private Button btnVideoExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSwitchScreen;
    private MyReceiver receiver;
    private RelativeLayout media_controller;
    private TextView tv_netspeed;
    //1.定义手势识别器
    private GestureDetector detector;
    private TimeUtils timeUtils;
    private int position;
    //是否隐藏了控制面板
    private boolean isHideMediaControll = false;
    //是否全屏
    private boolean isFullScreen = false;
    //调节声音
    private AudioManager audioManager;
    //当前音量
    private int currentVoice;
    //最大音量
    private int maxVoice;
    private int screenWidth;
    private int screenHeight;

    //是否静音
    private boolean isMute = false;
    private int realVideoWidth = 0;
    private int realVideoHeight = 0;
    //是否是网络资源
    private boolean isNetUri;

    private WkUtils wkUtils;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
   // private GoogleApiClient client;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-04-22 19:41:44 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        tv_loading_netspeed = (TextView)findViewById(R.id.tv_loading_netspeed);
        tv_netspeed = (TextView)findViewById(R.id.tv_netspeed);
        ll_video_buffer = (LinearLayout)findViewById(R.id.ll_video_buffer);
        ll_video_loding = (LinearLayout)findViewById(R.id.ll_video_loding);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvTime = (TextView) findViewById(R.id.tv_time);
        btnVideoVoice = (Button) findViewById(R.id.btn_video_voice);
        seekBarVoice = (SeekBar) findViewById(R.id.seekBar_voice);
        btnVideoSwitchPlayer = (Button) findViewById(R.id.btn_video_switch_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tv_current_time = (TextView) findViewById(R.id.tv_current_time);
        seekBarVideo = (SeekBar) findViewById(R.id.seekBar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnVideoExit = (Button) findViewById(R.id.btn_video_exit);
        btnVideoPre = (Button) findViewById(R.id.btn_video_pre);
        btnVideoStartPause = (Button) findViewById(R.id.btn_video_start_pause);
        btnVideoNext = (Button) findViewById(R.id.btn_video_next);
        btnVideoSwitchScreen = (Button) findViewById(R.id.btn_video_switch_screen);
        videoview = (VideoView) findViewById(R.id.videoview);
        media_controller = (RelativeLayout)findViewById(R.id.media_controller);
        btnVideoVoice.setOnClickListener(this);
        btnVideoSwitchPlayer.setOnClickListener(this);
        btnVideoExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoSwitchScreen.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-04-22 19:41:44 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     * 控制面板的各个按钮的监听
     */



    @Override
    public void onClick(View v) {
        if (v == btnVideoVoice) {//静音按钮
            // Handle clicks for btnVideoVoice
            isMute = !isMute;
            update(currentVoice);
        } else if (v == btnVideoSwitchPlayer) {//切换播放器(系统播放器与Vitamio)
            // Handle clicks for btnVideoSwitchPlayer
            showSwitchPlayerDialog();
        } else if (v == btnVideoExit) {//退出按钮
            //先停止掉视频
            if(videoview != null) {
                videoview.stopPlayback();
            }
            //关闭掉当前页面
            finish();
           
        } else if (v == btnVideoPre) {//播放上一个视频
            setPlayPreVideo();
            // Handle clicks for btnVideoPre
        } else if (v == btnVideoStartPause) {//播放和暂停按钮
            //根据判断播放或暂停
            playAndPause();
        } else if (v == btnVideoNext) {//播放下一个视频
            setPlayNextVideo();
            // Handle clicks for btnVideoNext
        } else if (v == btnVideoSwitchScreen) {//切换屏幕大小
            if(isFullScreen) {
                //如果是全屏则切换成默认
                setVideoType(DEFAULT_SCREEN);
            }else {
                //如果是默认则切换成全屏
                setVideoType(FULL_SCREEN);
            }
            // Handle clicks for btnVideoSwitchScreen
        }

        handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,5000);
    }

    private void showSwitchPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("当前使用的是系统播放器，如果播放视频出现错误，请您切换到万能播放器播放")
                .setNegativeButton("切换", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //确定切换则启动vitamio播放
                        startvitamioPlayer();
                    }
                })
                .setPositiveButton("取消",null);//注意这里设为null会自动消失
        builder.show();

    }

    /**
     * 播放或暂停的设置按钮
     */
    private void playAndPause() {
        if(videoview.isPlaying()) {
            //暂停
            videoview.pause();
            //设置按钮的状态为播放
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        }else {
            //播放
            videoview.start();
            //设置按钮状态为暂停
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    private void setPlayPreVideo() {
        if(mediaItems != null && mediaItems.size()>0) {
            position--;
            if(position >=0) {
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = WkUtils.isNetUri(mediaItem.getData());
                videoview.setVideoPath(mediaItem.getData());//重新设置地址
                ll_video_loding.setVisibility(View.VISIBLE);
                //重置按钮状态
                setButtonState();

                if(position == 0) {
                    //如果是最后一个
                    Toast.makeText(SystemVideoPlayer.this, "第一个视频了", Toast.LENGTH_SHORT).show();
                }
            }else {
                finish();//退出播放器
            }
        }else if(uri != null) {
            //此时是通过外界视频进入此应用
            finish();
        }
    }

    /**
     * 播放下一个视频
     */
    private void setPlayNextVideo() {
        if(mediaItems != null && mediaItems.size()>0) {
            position++;
            if(position <mediaItems.size()) {
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = WkUtils.isNetUri(mediaItem.getData());
                videoview.setVideoPath(mediaItem.getData());//重新设置地址
                //在进入下一个视频时显示一开始的加载界面
                ll_video_loding.setVisibility(View.VISIBLE);

                setButtonState();
                
                if(position == mediaItems.size()-1) {
                    //如果是最后一个
                    Toast.makeText(SystemVideoPlayer.this, "最后一个视频了", Toast.LENGTH_SHORT).show();
                }
            }else {
                finish();//退出播放器
            }
        }else if(uri != null) {
            //此时是通过外界视频进入此应用
            finish();
        }

    }

    /**
     * 设置下一个和上一个按钮状态的方法
     */
    private void setButtonState() {
        if(mediaItems != null && mediaItems.size()>0) {
            if(position == 0) {
                //如果是第一个视频，则把上一个按钮置为不可点
                btnVideoPre.setEnabled(false);
                btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            }else if(position == mediaItems.size()-1) {
                //如果是最后一个视频，则把下一个按钮置为不可点
                btnVideoNext.setEnabled(false);
                btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            }else {
                //否则都置为可点
                btnVideoPre.setEnabled(true);
                btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                btnVideoNext.setEnabled(true);
                btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            }
        }else if(uri != null) {
            //外界传来的地址
            btnVideoPre.setEnabled(false);
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoNext.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_NETSPEED:
                    String netSpeed = wkUtils.getNetSpeed(SystemVideoPlayer.this);
                    //显示加载界面的网速
                    tv_loading_netspeed.setText("玩命加载中..."+netSpeed);
                    //显示缓冲时的网速
                    tv_netspeed.setText("当前网速..."+netSpeed);
                    handler.sendEmptyMessageDelayed(SHOW_NETSPEED, 1000);
                    break;
                case PROGRESSUPDATE ://视频进度的更新
                    int currentPosition = videoview.getCurrentPosition();
                    seekBarVideo.setProgress(currentPosition);
                    //设置播放时间
                    tv_current_time.setText(timeUtils.stringForTime(currentPosition));
                    //设置系统时间
                    tvTime.setText(getSystemTime());
                    //网络视频的缓冲进度
                    if(isNetUri) {

                        //设置缓冲进度(以下计算其实就是：当前进度条长度 = 总进度条长度*(当前缓冲/100))
                        int buff = videoview.getBufferPercentage();//0-100
                        int totalBuffer = seekBarVideo.getMax()*buff;
                        int secondaryProgress = totalBuffer/100;
                        seekBarVideo.setSecondaryProgress(secondaryProgress);
                    }else {
                        //本地视频不需要设置，为0
                        seekBarVoice.setSecondaryProgress(0);
                    }





                    if(!isActivityDestroy) {
                        //这里的判断是为了避免退出前刚好发了最后一次，导致内存泄漏
                        handler.removeMessages(PROGRESSUPDATE);//注意要撤销之前的handler，否则会越来越多
                        handler.sendEmptyMessageDelayed(PROGRESSUPDATE,1000);
                    }
                    break;
                case HIDE_MEDIA_CONTROLLER://定时隐藏控制面板
                    hideMediaController();
                    break;
            }
        }
    };

    /**
     * 得到系统时间
     * @return
     */
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化一些数据
        initData();
        setContentView(R.layout.activity_system_video_player);
        //得到播放信息
        getData();
        //找到控件对象
        findViews();

        //设置屏幕不锁屏
        videoview.setKeepScreenOn(true);
        //设置seekBar最大音量
        seekBarVoice.setMax(maxVoice);
        //把seekBarVoice的初始值设为系统音量
        seekBarVoice.setProgress(currentVoice);
        //开始发送监听网速的handler
        handler.sendEmptyMessage(SHOW_NETSPEED);
        //初始化右上角系统时间
        tvTime.setText(getSystemTime());
        //设置监听
        setListener();

        //设置播放的数据
        setPlayData();

        //设置控制面板
        //videoview.setMediaController(new MediaController(this));
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setListener() {
        //准备好的监听
        videoview.setOnPreparedListener(new MyOnPreparedListener());
        //监听播放出错
        videoview.setOnErrorListener(new MyOnErrorListener());
        //监听播放完成
        videoview.setOnCompletionListener(new MyOnCompletionListener());
        //监听SeekBar拖动的状态
        seekBarVideo.setOnSeekBarChangeListener(new MyOnSeekChangeListener());
        //设置拖动音量
        seekBarVoice.setOnSeekBarChangeListener(new MyVoiceSeekBarChangeListener());
        //设置监听卡顿
        videoview.setOnInfoListener(new MyOnInfoListener());
    }

    class MyOnInfoListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://开始卡，拖动卡
                    ll_video_buffer.setVisibility(View.VISIBLE);
                    break;
                
                case MediaPlayer.MEDIA_INFO_BUFFERING_END://开始卡结束，拖动卡结束
                    ll_video_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }

    /**
     * 实现音量的seekBar的监听
     */
    class MyVoiceSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 当滚动时
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                updateProgress(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,5000);
        }
    }

    /**
     * 这里是为了静音按钮的点击事件而使用的方法
     * @param voice
     */
    private void update(int voice) {

        if(isMute ){
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            seekBarVoice.setProgress(0);
        }else{
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,voice,0);
            seekBarVoice.setProgress(voice);
        }


        currentVoice = voice;

    }
    /**
     * 这里是用来seekBar拖动而更新声音的方法
     * @param voice
     */
    private void updateProgress(int voice) {
        if(voice <= 0) {
            isMute = true;
        }else {
            isMute = false;
        }
       audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, voice, 0);//注意第三个参数是要不要系统的
        seekBarVoice.setProgress(voice);
        currentVoice = voice;
    }

    /**
     * 设置播放的数据
     */
    private void setPlayData() {//这里注意，从列表传来的是通过path播放，是一个字符串，而从外界文件传来的是一个URI
        if(mediaItems != null && mediaItems.size()>0) {
            //如果播放列表有视频
            MediaItem mediaItem = mediaItems.get(position);

            isNetUri = WkUtils.isNetUri(mediaItem.getData());

            //设置播放地址
            videoview.setVideoPath(mediaItem.getData());
            //设置标题
            tvName.setText(mediaItem.getName());
        }else if(uri != null) {
            //如果没有播放列表
            //用原来的方法设置播放地址,此时是从外界传来的
            videoview.setVideoURI(uri);
            isNetUri = WkUtils.isNetUri(uri.toString());
        }else {
            Toast.makeText(SystemVideoPlayer.this, "没有传入播放地址", Toast.LENGTH_SHORT).show();
        }
        //一进来时就设置一下按钮状态
        setButtonState();
    }



    /**
     *获取视频数据
     */
    private void getData() {
        //获取uri(如果没有播放列表，则用到这个)
        uri = getIntent().getData();//Intent.setData//通常用于文件夹，相册浏览
        //获取播放列表
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        //获取传入的位置
        position = getIntent().getIntExtra("position",0);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        wkUtils = new WkUtils();
        //得到音量管理者
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //得到当前音量
        currentVoice = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //得到最大音量
        maxVoice = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //得到屏幕的宽和高,这是旧的，不用了
//        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
//       screenWidth = wm.getDefaultDisplay().getWidth();
//       screenHeight = wm.getDefaultDisplay().getHeight();

        //用这个新的得到屏幕宽和高的方法
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        timeUtils = new TimeUtils();
        //注册一个电量变化的广播
        myReceiver = new MyReceiver();
        //增加广播接收的过滤条件(即接收电量变化的广播)
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(myReceiver, filter);
        //初始化手势识别器
        detector =new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            /**
             * 双击
             * @param e
             * @return
             */
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if(isFullScreen) {
                    //如果是全屏则切换成默认
                    setVideoType(DEFAULT_SCREEN);
                }else {
                    //如果是默认则切换成全屏
                    setVideoType(FULL_SCREEN);
                }
                return super.onDoubleTap(e);
            }

            /**
             * 单击则显示或隐藏控制面板
             * @param e
             * @return
             */
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(isHideMediaControll) {
                    //如果是隐藏的，则显示
                    showMediaController();
                    //如果显示了，隔一段时间要隐藏
                    handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,5000);
                }else {
                    //如果是显示的，则隐藏
                    hideMediaController();
                    handler.removeMessages(HIDE_MEDIA_CONTROLLER);
                }
                return super.onSingleTapConfirmed(e);
            }

            /**
             * 长按播放或暂停
             * @param e
             */
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                //调用播放或暂停的方法
                playAndPause();
            }
        });

    }

    private void setVideoType(int type) {
        switch (type) {
            case  FULL_SCREEN://全屏播放
                videoview.setVideoSize(screenWidth,screenHeight);//这两个参数已经在initData中得到屏幕的值了
                isFullScreen = true;
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_defualt_screen);
                break;
            case DEFAULT_SCREEN://默认播放
                //真实视频的高和宽
                int mVideoWidth = realVideoWidth;
                int mVideoHeight = realVideoHeight;

                //在屏幕的宽和高基础上计算
                int width = screenWidth;
                int  height = screenHeight;

                // for compatibility, we adjust size based on aspect ratio
                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                videoview.setVideoSize(width,height);
                isFullScreen = false;
                //设置按钮的状态为默认
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_full_screen);
                break;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * 隐藏控制面板的方法
     */
    private void hideMediaController() {
        isHideMediaControll = true;
        media_controller.setVisibility(View.GONE);
    }

    /**
     * 显示控制面板的方法
     */
    private void showMediaController() {
        isHideMediaControll = false;
        media_controller.setVisibility(View.VISIBLE);
    }

    /**
     * 监听电量变化的广播
     */
    private MyReceiver myReceiver;
    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);//0-100等分
            //主线程
            setBatteryStatus(level);
        }
    }

    /**
     * 设置电量状态
     * 根据电量范围设置相应的图片
     */
    private void setBatteryStatus(int level) {
        if(level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        }else if(level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        }else if(level <=20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        }else if(level<=40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        }else if(level<=60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        }else if(level<=80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        }else if(level<=100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }

    }

    /**
     * 监听SeekBar拖动的状态
     */
    class MyOnSeekChangeListener implements SeekBar.OnSeekBarChangeListener{
        /**
         * 当SeekBar的进度发生变化的时候回调这个方法，无论是不是人为滑动，都会执行回调
         * @param seekBar
         * @param progress 当前的进度
         * @param fromUser 如果是人为改变的就是true，否则就是false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                videoview.seekTo(progress);
            }
        }

        /**
         * 当手指触碰时的回调方法
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //当触碰seekBar时就移除隐藏控制面板的消息
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        }

        /**
         * 当手指离开时的回调方法
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,5000);
        }
    }
    //设置准备监听
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {

            //真实的视频的尺寸
            realVideoWidth = mp.getVideoWidth();
            realVideoHeight = mp.getVideoHeight();
            //准备好才可以播放,注意：如果是mediaPlayer需要先调用准备方法才可以播放
            //而videoview封装了mediaPlayer，里面已经调用了准备方法，所以不需要再调用了
            videoview.start();//开始播放
            //注意只有准备好的时候才能获取时长
            int duration = videoview.getDuration();
            //视频的总长度和SeekBar.setMax();
            //videoview.seekTo(3000);//定位到之前播放的位置
            seekBarVideo.setMax(duration);

            tvDuration.setText(timeUtils.stringForTime(duration));

            //一进视频就隐藏控制面板
            hideMediaController();
            //设置视频默认尺寸
            setVideoType(DEFAULT_SCREEN);
            //隐藏进入时的加载界面
            ll_video_loding.setVisibility(View.GONE);
            //发送一个消息
            handler.sendEmptyMessageDelayed(PROGRESSUPDATE,1000);

        }
    }

    //出错监听
    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //Toast.makeText(SystemVideoPlayer.this, "播放出错了", Toast.LENGTH_SHORT).show();
            //1.文件不支持-出错,则调用vitamio
            startvitamioPlayer();
            //2.播放过程中网络异常
            //3.播放文件残缺
            return true;
        }
    }

    /**
     * 当出错的时候调用此方法启动vitamio
     * 理解：videoPager传递序列化集合---->SystemVideoPlayer获得到这个集合，再通过同样的方式---->vitamioVideoPlayer
     */
    private void startvitamioPlayer() {
        if(videoview != null) {
            //先把自己停止掉
            videoview.stopPlayback();
        }

        Intent intent = new Intent(this,VitamioVideoPlayer.class);
        if(mediaItems != null && mediaItems.size()>0) {
            //传递视频列表给播放器

            //这里进行了序列化
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", mediaItems);
            //传播放列表
            intent.putExtras(bundle);
            //因为播放要知道是哪个视频，所以还要传一下位置
            intent.putExtra("position", position);

        }else if(uri != null) {
            intent.setData(uri);
        }
        startActivity(intent);
        finish();
    }

    //播放完成监听
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            //退出播放器或者播放下一个
            setPlayNextVideo();
            //finish();
        }
    }


    private float startY;
    private int mVol;//当前音量
    private int touchRang;//滑动的区域
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                handler.removeMessages(HIDE_MEDIA_CONTROLLER);
                //获得起始y坐标
                startY = event.getY();
                //获得当前音量
                mVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                //获得滑动区域
                touchRang = Math.min(screenHeight,screenWidth);
                break;
            case MotionEvent.ACTION_MOVE:
                float endY = event.getY();
                //及时计算偏移量
                float distanceY = startY - endY;
                //改变的声音 = (滑动的距离/总距离)*总音量
                float delta = (distanceY/touchRang)*maxVoice;
                //要设置的音量 = 原来的音量+改变的声音
                if(delta != 0) {//这里相当于用if限制了最大最小值
                    int voice = (int) Math.min(Math.max(mVol+delta,0),maxVoice);
                    updateProgress(voice);
                }
                break;
            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,5000);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果此时控制面板没显示，则先让控制面板显示出来
        if(isHideMediaControll) {
            showMediaController();
        }
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVoice--;
            updateProgress(currentVoice);
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 5000);
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVoice++;
            updateProgress(currentVoice);
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,5000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isActivityDestroy = false;
    @Override
    protected void onDestroy() {
        handler.removeMessages(PROGRESSUPDATE);
        isActivityDestroy = true;
        //解注册
        if(myReceiver != null) {
            unregisterReceiver(myReceiver);
            myReceiver = null;
        }
        //注意先释放子类的再释放父类的
        super.onDestroy();
    }
}
