package com.atguigu.mobileplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
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

import com.atguigu.mobileplayer.Utils.Utils;
import com.atguigu.mobileplayer.domain.MediaItem;
import com.atguigu.mobileplayer.view.VitamioVideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

/**
 * 作者：杨光福 on 2016/4/20 16:29
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：xxxx
 */
public class VitamioVideoPlayer extends Activity implements View.OnClickListener {

    /**
     * 视频的进度跟新
     */
    private static final int PROGRESS = 1;
    private static final int HIDE_MEDIA_CONTROLLER = 2;
    /**
     * 全屏屏幕播放
     */
    private static final int FULL_SCREEN = 3;
    /**
     * 默认屏幕播放
     */
    private static final int DEFUALT_SCREEN = 4;
    private static final int SHOW_NETSPEED = 5;
    private Uri uri;
    private ArrayList<MediaItem> mediaItems;
    private VitamioVideoView videview;
    private LinearLayout ll_video_loading;
    private LinearLayout ll_video_buffer;
    private TextView tv_netspeed;
    private TextView tv_loading_netspeed;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvTime;
    private Button btnVideoVoice;
    private SeekBar seekBarVoice;
    private Button btnVideoSwitchPlayer;
    private LinearLayout llBottom;
    private TextView btnCurrentTime;
    private SeekBar seekBarVideo;
    private TextView tvDuration;
    private Button btnVideoExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSwitchScreen;
    private RelativeLayout media_controller;
    private Utils utils;
    private int screenWidth;
    private int screenHeight;
    private int VideoWidth = 0;
    private  int VideoHeight = 0;
    private MyReceiver receiver;

    //1.定义手势识别器
    private GestureDetector detector;
    /**
     * 视频列表中的位置
     */
    private int position;
    /**
     * 是否隐藏控制面板
     * true:隐藏了
     * false:不隐藏
     */
    private boolean isHideMediaControll = false;
    /**
     * true:全屏播放
     * flase:默认播放
     */
    private boolean isFullScreen = false;

    /**
     * 调节声音
     */
    private AudioManager audioManager;

    /**
     * 当前音量
     */
    private int currentVoice;

    /**
     * 最大音量
     */
    private int maxVoice;

    /**
     * 是否是静音
     */
    private boolean isMute = false;
    /**
     * 是否是网络资源
     */
    private boolean isNetUri;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-04-22 10:51:42 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        videview = (VitamioVideoView) findViewById(R.id.videview);
        llTop = (LinearLayout)findViewById( R.id.ll_top );
        ll_video_loading = (LinearLayout) findViewById(R.id.ll_video_loading);
        ll_video_buffer = (LinearLayout) findViewById(R.id.ll_video_buffer);
        tv_netspeed = (TextView) findViewById(R.id.tv_netspeed);
        tv_loading_netspeed = (TextView) findViewById(R.id.tv_loading_netspeed);
        tvName = (TextView)findViewById( R.id.tv_name );
        ivBattery = (ImageView)findViewById( R.id.iv_battery );
        tvTime = (TextView)findViewById( R.id.tv_time );
        btnVideoVoice = (Button)findViewById( R.id.btn_video_voice );
        seekBarVoice = (SeekBar)findViewById( R.id.seekBar_voice );
        btnVideoSwitchPlayer = (Button)findViewById( R.id.btn_video_switch_player );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        btnCurrentTime = (TextView)findViewById( R.id.btn_current_time );
        seekBarVideo = (SeekBar)findViewById( R.id.seekBar_video );
        tvDuration = (TextView)findViewById( R.id.tv_duration );
        btnVideoExit = (Button)findViewById( R.id.btn_video_exit );
        btnVideoPre = (Button)findViewById( R.id.btn_video_pre );
        btnVideoStartPause = (Button)findViewById( R.id.btn_video_start_pause );
        btnVideoNext = (Button)findViewById( R.id.btn_video_next );
        btnVideoSwitchScreen = (Button)findViewById( R.id.btn_video_switch_screen );
        media_controller = (RelativeLayout) findViewById(R.id.media_controller);

        btnVideoVoice.setOnClickListener( this );
        btnVideoSwitchPlayer.setOnClickListener( this );
        btnVideoExit.setOnClickListener( this );
        btnVideoPre.setOnClickListener( this );
        btnVideoStartPause.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );
        btnVideoSwitchScreen.setOnClickListener( this );

        //设置屏幕不锁屏
        videview.setKeepScreenOn(true);

        seekBarVoice.setMax(maxVoice);//设置最大音量
        seekBarVoice.setProgress(currentVoice);//设置默认进度

        //每隔一秒发一次消息
        handler.sendEmptyMessage(SHOW_NETSPEED);
    }

    /**
     * 上一秒的播放进度
     */
    private int prePosition;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_NETSPEED:

                    //显示网络速度
                    String netSpeed = utils.getNetSpeed(VitamioVideoPlayer.this);

                    tv_netspeed.setText("当前网速"+netSpeed);
                    tv_loading_netspeed.setText("玩命加载中..."+netSpeed);

                    //每隔一秒发一次消息
                    handler.sendEmptyMessageDelayed(SHOW_NETSPEED, 1000);
                    break;
                case PROGRESS://视频更新的消息

                    int currentPosition  = (int) videview.getCurrentPosition();

                    seekBarVideo.setProgress(currentPosition);//视频进度的更新



                    //设置当前的时间的更新
                    btnCurrentTime.setText(utils.stringForTime(currentPosition));


                    //设置系统时间
                    tvTime.setText(getSystemTime());

                    //网络视频的缓存
                    if(isNetUri){
                        //设置缓存
                        int buffer = videview.getBufferPercentage();//0~100;
                        int totalBuffer = seekBarVideo.getMax()*buffer;
                        int secondaryProgress = totalBuffer/100;
                        seekBarVideo.setSecondaryProgress(secondaryProgress);
                    }else{
                        //本地视频不需要设置
                        seekBarVideo.setSecondaryProgress(0);
                    }

                    //当前的进度-上一秒播放进度
                    if(videview.isPlaying()){
                        int buffer = currentPosition - prePosition;
                        if(buffer < 300){
                            //卡了
                            ll_video_buffer.setVisibility(View.VISIBLE);
                        }else{
                            //不卡
                            ll_video_buffer.setVisibility(View.GONE);
                        }
                    }

                    prePosition = currentPosition;//1000//2000



                    //每隔一秒发一次消息
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);

                    break;
                case HIDE_MEDIA_CONTROLLER://隐藏控制面板
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

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-04-22 10:51:42 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnVideoVoice ) {
            isMute = !isMute;
            update(currentVoice);
            // Handle clicks for btnVideoVoice
        } else if ( v == btnVideoSwitchPlayer ) {
            // Handle clicks for btnVideoSwitchPlayer
            showSwitchPlayerDialog();
        } else if ( v == btnVideoExit ) {
            if(videview != null){
                videview.stopPlayback();
            }
            finish();
            // Handle clicks for btnVideoExit
        } else if ( v == btnVideoPre ) {
            // Handle clicks for btnVideoPre
            setPlayPreVideo();
        } else if ( v == btnVideoStartPause ) {
            playAndPause();
            // Handle clicks for btnVideoStartPause
        } else if ( v == btnVideoNext ) {
            // Handle clicks for btnVideoNext
            setPlayNextVideo();
        } else if ( v == btnVideoSwitchScreen ) {
            // Handle clicks for btnVideoSwitchScreen
            if(isFullScreen){
                //默认屏幕播放器视频
                setVideoType(DEFUALT_SCREEN);
            }else{
                //全屏播放视频
                setVideoType(FULL_SCREEN);
            }
        }

        handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,5000);
    }

    private void showSwitchPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当前使用的是万能播放器播放，如果播放视频不够流畅，请切换到系统播放器播放");
        builder.setNegativeButton("切换", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startSystemPlayer();
            }
        });
        builder.setPositiveButton("取消", null);
        builder.show();

    }

    private void startSystemPlayer() {
        if(videview != null){
            videview.stopPlayback();
        }
        Intent intent = new Intent(this, SystemVideoPlayer.class);
        if(mediaItems != null && mediaItems.size() > 0){
            //传递视频列表给播放器

            Bundle bundle = new Bundle();
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
            bundle.putSerializable("videolist", mediaItems);
            intent.putExtras(bundle);

            intent.putExtra("position", position);


        }else if(uri != null){
            intent.setData(uri);
        }

        startActivity(intent);

        finish();

    }

    private void playAndPause() {
        if(videview.isPlaying()){
            //暂停
            videview.pause();
            //按钮状态要设置播放
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        }else{
            //播放
            videview.start();
            //设置按钮状态暂停
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    private void setPlayPreVideo() {
            if(mediaItems != null && mediaItems.size() >0){
                position--;
                if(position >= 0){

                    MediaItem mediaItem = mediaItems.get(position);
                    tvName.setText(mediaItem.getName());//设置名称
                    isNetUri = utils.isNetUri(mediaItem.getData());
                    ll_video_loading.setVisibility(View.VISIBLE);
                    videview.setVideoPath(mediaItem.getData());//设置播放地址

                    setButtonState();//设置按钮状态

                    if(position ==0){
                        Toast.makeText(VitamioVideoPlayer.this, "第一个视频了", Toast.LENGTH_SHORT).show();
                    }


                }else{
                    position = 0;
                    //
                    finish();//退出播放器
                }
            }else if(uri != null){
                finish();//退出播放器
            }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(this);
        System.out.println("SystemVideoPlayer--onCreate=="+savedInstanceState);
        setContentView(R.layout.activity_vitamio_video_player);

        initData();

        findViews();

        getData();


        setListener();
        setData();


        //设置控制面板
//        videview.setMediaController(new MediaController(this));
    }

    private void setData() {
        if(mediaItems != null && mediaItems.size() >0 ){
            MediaItem mediaItem = mediaItems.get(position);

            isNetUri = utils.isNetUri(mediaItem.getData());
            //设置播放地址
            videview.setVideoPath(mediaItem.getData());
//            videview.setVideoPath("http://cctv13.vtime.cntv.cloudcdn.net:8500/live/no/23_/seg0/index.m3u8?uid=default&AUTH=Xjx2BNhiwBbrA/Am3/9QhvmMEXk2NSWLY53QeqNu0XLc6YE1FEf+M1RtWvbeoHkeUZ8SwwLktHkuSPI5JO4w5A==");

            //设置标题
            tvName.setText(mediaItem.getName());


        }else if(uri != null){
            //设置播放地址
            videview.setVideoURI(uri);
            isNetUri = utils.isNetUri(uri.toString());
            tvName.setText(uri.toString());
        }else{
            Toast.makeText(this,"没有传入播放地址...",Toast.LENGTH_SHORT).show();
        }


        setButtonState();

    }



    private void getData() {

        uri = getIntent().getData();//Intent.setData();//文件夹，相册浏览

        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");

        position = getIntent().getIntExtra("position",0);
    }

    private void initData() {

        //得到音量
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //得到屏幕的宽和高
//        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
//        screenWidth = wm.getDefaultDisplay().getWidth();
//        screenHeight = wm.getDefaultDisplay().getHeight();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        Log.e(VitamioVideoPlayer.class.getSimpleName(),screenWidth+"----"+screenHeight);

        utils = new Utils();

        //注册监听电量广播
        receiver = new MyReceiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentfilter);

        //2.实例化手势识别器
        detector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                playAndPause();
//                Toast.makeText(SystemVideoPlayer.this, "被长按了....", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
//                Toast.makeText(SystemVideoPlayer.this, "被双击了....", Toast.LENGTH_SHORT).show();
                if(isFullScreen){
                    //默认屏幕播放器视频
                    setVideoType(DEFUALT_SCREEN);
                }else{
                    //全屏播放视频
                    setVideoType(FULL_SCREEN);
                }
                return super.onDoubleTap(e);

            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
//                Toast.makeText(SystemVideoPlayer.this, "被点击了....", Toast.LENGTH_SHORT).show();
                if(isHideMediaControll){
                    //显示
                    showMediaController();
                    //如果显示了，隔一段时间要隐藏
                    handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,5000);
                }else{
                    //隐藏
                    hideMediaController();
                    handler.removeMessages(HIDE_MEDIA_CONTROLLER);
                }
                return super.onSingleTapConfirmed(e);
            }
        });


    }

    private void setVideoType(int type) {
        switch (type){
            case FULL_SCREEN://全屏播放
                videview.setVideoSize(screenWidth,screenHeight);
                isFullScreen = true;
                //设置按钮的状态为默认
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_defualt_screen);
                break;
            case DEFUALT_SCREEN://默认播放


                //真实视频的高和宽
                int mVideoWidth = VideoWidth;
                int mVideoHeight = VideoHeight;

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

                videview.setVideoSize(width,height);
                isFullScreen = false;
                //设置按钮的状态为默认
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_full_screen);


                break;
        }
    }

    private void hideMediaController() {
        isHideMediaControll = true;
        media_controller.setVisibility(View.GONE);
    }

    private void showMediaController() {
        isHideMediaControll = false;
        media_controller.setVisibility(View.VISIBLE);

    }

    private void setListener() {
        //设置监听
        //准备好的监听
        videview.setOnPreparedListener(new MyOnPreparedListener());
        //监听播放出错
        videview.setOnErrorListener(new MyOnErrorListener());
        //监听监听播放完成
        videview.setOnCompletionListener(new MyOnCompletionListener());

        //设置视频拖动监听
        seekBarVideo.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());

        //设置拖动音量
        seekBarVoice.setOnSeekBarChangeListener(new MyVoiceOnSeekBarChangeListener());

//        //用系统的API设置监听卡
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            videview.setOnInfoListener(new MyOnInfoListener());
//        }
    }

    class MyOnInfoListener implements MediaPlayer.OnInfoListener{

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://开始卡了，拖动卡
                    ll_video_buffer.setVisibility(View.VISIBLE);
                    break;

                case MediaPlayer.MEDIA_INFO_BUFFERING_END://开始卡结束，拖动卡结束
                    ll_video_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }
    class MyVoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
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
     * 更新声音
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

    private void updateProgress(int voice) {

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,voice,0);
        if(voice <=0){
            isMute = true;
        }else{
            isMute = false;
        }
        seekBarVoice.setProgress(voice);

        currentVoice = voice;

    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 当状态变化的时候回调这个方法
         * @param seekBar
         * @param progress 当前进度
         * @param fromUser 不是人为改变的时候就是false,人为改变的是true
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                videview.seekTo(progress);
            }

        }

        /**
         * 当手指第一次触碰的时候回调这个方法
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        }

        /**
         * 当手指离开SeeKbar的时候回调这方法
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,5000);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("SystemVideoPlayer--onSaveInstanceState=="+outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("SystemVideoPlayer--onStart");

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("SystemVideoPlayer--onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("SystemVideoPlayer--onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("SystemVideoPlayer--onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("SystemVideoPlayer--onStop");
    }

    @Override
    protected void onDestroy() {

        System.out.println("SystemVideoPlayer--onDestroy");
        handler.removeCallbacksAndMessages(null);

        //取消监听
        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }

        super.onDestroy();
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {

//            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                @Override
//                public void onSeekComplete(MediaPlayer mp) {
//                    Toast.makeText(SystemVideoPlayer.this, "拖动结束了。。", Toast.LENGTH_SHORT).show();
//                }
//            });

            VideoWidth = mp.getVideoWidth();
            VideoHeight = mp.getVideoHeight();
            videview.start();

            //设置SeekBar.setMax();
            int duration = (int) videview.getDuration();//得到视频的总长度
            seekBarVideo.setMax(duration);

            //设置总时长
            tvDuration.setText(utils.stringForTime(duration));

            //隐藏控制面板
            hideMediaController();

            //设置视频默认播放
            setVideoType(DEFUALT_SCREEN);

            //隐藏加载效果
            ll_video_loading.setVisibility(View.GONE);

            //发消息更新视频的进度
            handler.sendEmptyMessage(PROGRESS);
        }
    }
    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
//            Toast.makeText(VitamioVideoPlayer.this,"播放出错了",Toast.LENGTH_SHORT).show();
            //1.文件不支持-出错 -解决办法-切换到万能播放器播放视频
            showErrorDialog();

            //2.播放过程中网络异常--重新播放
            //3.播放文件残缺
            return true;
        }
    }

    /**
     * 提示出错
     */
    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提醒");
        builder.setMessage("视频播放出错了，请检查网络或者检查视频文件是否有损坏");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (videview != null) {
                    videview.stopPlayback();
                }
                finish();
            }
        });
        builder.show();
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {

            setPlayNextVideo();
            //退出播放器或者播放下一个
//            finish();
        }
    }

    /**
     * 播放下一个视频
     */
    private void setPlayNextVideo() {
        if(mediaItems != null && mediaItems.size() >0){
            position++;
            if(position < mediaItems.size()){

                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = utils.isNetUri(mediaItem.getData());
                videview.setVideoPath(mediaItem.getData());
                ll_video_loading.setVisibility(View.VISIBLE);
                setButtonState();

                if(position ==mediaItems.size()-1){
                    Toast.makeText(VitamioVideoPlayer.this, "最后一个视频了", Toast.LENGTH_SHORT).show();
                }


            }else{
                //
                finish();//退出播放器
            }
        }else if(uri != null){
            finish();//退出播放器
        }


    }

    private void setButtonState() {
        if(mediaItems != null && mediaItems.size() > 0){
            if(position==0){
                btnVideoPre.setEnabled(false);
                btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            }else if(position==mediaItems.size()-1){
                btnVideoNext.setEnabled(false);
                btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            }else{
                btnVideoPre.setEnabled(true);
                btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                btnVideoNext.setEnabled(true);
                btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            }
        }else if(uri != null){//当外界只传一个地址给当前播放器的时候
            btnVideoPre.setEnabled(false);
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoNext.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
        }

    }

    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level",0);//电量值：0~100
            //在主线程中执行
            setBattey(level);
        }
    }

    /**
     * 设置电量状态
     * @param level
     */
    private void setBattey(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        }else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        }else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        }else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        }else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private  float startY;
    private int mVol;//当前音量
    private int touchRang;//滑动的区域
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                handler.removeMessages(HIDE_MEDIA_CONTROLLER);
                startY = event.getY();
                mVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang  = Math.min(screenHeight,screenWidth);
                break;

            case MotionEvent.ACTION_MOVE:
                float endY = event.getY();
                //计算偏移量
                float distanceY = startY - endY;

                //滑动的距离：总距离 = 改变的声音：总音量


                //  改变的声音 = （滑动的距离/总距离）*总音量
                float delta =  (distanceY/touchRang)*maxVoice;

                 //要设置的音量 = 原来的音量 + 改变的声音
                if(delta != 0){
                    int voice = (int) Math.min(Math.max(mVol+delta,0),maxVoice);
                    updateProgress(voice);
                }

//                startY = event.getY();//不能加上
                break;



            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,5000);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode ==KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice --;
            updateProgress(currentVoice);
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,5000);
            return true;
        }else if(keyCode ==KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice ++;
            updateProgress(currentVoice);
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,5000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
