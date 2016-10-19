package com.chenluwei.weike.media;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chenluwei.weike.IMusicPlayerService;
import com.chenluwei.weike.R;
import com.chenluwei.weike.service.MusicPlayerService;
import com.chenluwei.weike.util.TimeUtils;

import org.xutils.common.util.LogUtil;

public class AudioPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PROGRESS = 1;
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar audioSeekBar;
    private Button btnPlaymode;
    private Button btnPre;
    private Button btnPlayPause;
    private Button btnNext;
    private Button btnLyric;
    //当前音频播放列表的位置
    private int position;

    private TimeUtils timeUtils;
    /**
     * 是否来自于状态栏
     */
    private boolean from_notification;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-04-26 18:03:58 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        ivIcon = (ImageView)findViewById( R.id.iv_icon );
        tvArtist = (TextView)findViewById( R.id.tv_artist );
        tvName = (TextView)findViewById( R.id.tv_name );
        tvTime = (TextView)findViewById( R.id.tv_time );
        audioSeekBar = (SeekBar)findViewById( R.id.audio_seekBar );
        btnPlaymode = (Button)findViewById( R.id.btn_playmode );
        btnPre = (Button)findViewById( R.id.btn_pre );
        btnPlayPause = (Button)findViewById( R.id.btn_play_pause );
        btnNext = (Button)findViewById( R.id.btn_next );
        btnLyric = (Button)findViewById( R.id.btn_lyric );

        btnPlaymode.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnPlayPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnLyric.setOnClickListener(this);
        //设置拖动监听
        audioSeekBar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }
    class  MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private  Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case PROGRESS:
                    try {
                        //获取当前位置
                        int currentPosition = service.getCurrentPosition();
                        //进度的更新
                        audioSeekBar.setProgress(currentPosition);
                        //时间的更新
                        tvTime.setText(timeUtils.stringForTime(currentPosition)+"/"+timeUtils.stringForTime(service.getDuration()));
                        audioSeekBar.setMax(service.getDuration());
                        handler.sendEmptyMessageDelayed(PROGRESS,1000);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-04-26 18:03:58 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnPlaymode ) {//模式切换的按钮
            // Handle clicks for btnPlaymode
            setPlayMode();
        } else if ( v == btnPre ) {//上一首的按钮
            try {
                service.pre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Handle clicks for btnPre
        } else if ( v == btnPlayPause ) {//播放和暂停按钮
            try {
                if(service.isPlaying()) {
                    //如果正在播放，则暂停
                    service.pause();
                    //按钮设置为暂停播放状态
                    btnPlayPause.setBackgroundResource(R.drawable.btn_play_audio_selector);
                }else {
                    //播放
                    service.play();
                    //按钮设置
                    btnPlayPause.setBackgroundResource(R.drawable.btn_pause_audio_selector);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Handle clicks for btnPlayPause
        } else if ( v == btnNext ) {//下一首的按钮
            try {
                service.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Handle clicks for btnNext
        } else if ( v == btnLyric ) {
            // Handle clicks for btnLyric
        }
    }

    private void setPlayMode() {
        try {
            int playmode = service.getPlayMode();
            LogUtil.e(playmode + "aaaaa");
            if((playmode == MusicPlayerService.REPEAT_NORMAL)) {
                playmode = MusicPlayerService.REPEAT_SINGLE;
                LogUtil.e(playmode + "bbb");
            }else if(playmode == MusicPlayerService.REPEAT_SINGLE) {
                playmode = MusicPlayerService.REPEAT_ALL;
            }else if(playmode == MusicPlayerService.REPEAT_ALL) {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }else {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }
            service.setPlayMode(playmode);
            showPlayMode();
            LogUtil.e(playmode + "ccccc");
            //保存到service中
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPlayMode() {
        try {
            int playmode = service.getPlayMode();
            LogUtil.e(playmode+"============");
            if((playmode == MusicPlayerService.REPEAT_NORMAL)) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_normal_selector);
            }else if(playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_single_selector);
            }else if(playmode == MusicPlayerService.REPEAT_ALL) {
               btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_all_selector);
            }else {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_normal_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        Log.e("bbb", "Activity---onCreate");
        initData();
        //设置控件并设置点击监听
        findViews();
        //播放上方的帧动画
        playAnimation();
        //获取位置
        getData();
        //绑定启动服务
        bindAndStartService();
    }


    private MyReceiver receiver;
    private void initData() {
        timeUtils = new TimeUtils();
        receiver = new MyReceiver();
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPEN_AUDIO);
        registerReceiver(receiver, intentFilter);
    }

    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //为什么要这一句？
            if(intent.getAction(
            ).equals(MusicPlayerService.OPEN_AUDIO)) {
                //接收到准备完成的广播后开始更新视图
                setViewData();
            }
        }
    }

    /**
     * 在广播接收器到Service那边准备完成的广播后，开始执行此方法
     */
    private void setViewData() {
        try {
            //设置播放模式的按钮状态
            showPlayMode();
            tvArtist.setText(service.getArtist());
            tvName.setText(service.getAudioName());
            audioSeekBar.setMax(service.getDuration());
            //开始发消息
            handler.sendEmptyMessage(PROGRESS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void getData() {
        from_notification = getIntent().getBooleanExtra("notification", false);
        if(!from_notification) {
            //如果不来自状态栏
            position = getIntent().getIntExtra("position", 0);
        }
    }

    /**
     * 服务的代理类，可以操作服务
     */
    private IMusicPlayerService service;


    /**
     * 针对于绑定的方式，如果onBind()方法返回值非空，则会调用启动者(比如：activity)
     中的ServiceConnection中的onServiceConnected()方法。
     */
    private ServiceConnection conn = new ServiceConnection() {

        /**
         * 当Activity和Service绑定成功后绑定这个方法
         * @param name
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            //那边返回的是一个stub，在这里再转化为代理类
            service = IMusicPlayerService.Stub.asInterface(iBinder);
            if(service != null) {
                try {
                    if(!from_notification) {
                        //这里传入播放的位置
                        service.openAudio(position);
                    }else {
                        //如果是从状态栏启动的
                        //刷新状态,这里让service再发一次准备完成的广播
                        //否则会获取不到歌曲数据
                        service.notifyChange(MusicPlayerService.OPEN_AUDIO);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 当Activity和Service断开连接的时候回调这个方法
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    /**
     * 启动服务
     */
    private void bindAndStartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.clw.OPENAUDIO");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
        Log.e("bbb", "bindAndStartService");
    }

    /**
     * 播放上方的帧动画
     */
    private void playAnimation() {
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) ivIcon.getBackground();
        animationDrawable.start();
    }

    @Override
    protected void onDestroy() {
        if(conn != null) {
            unbindService(conn);
            conn = null;
            Log.e("bbb", "Activity-----onDestroy");
        }

        //广播解注册
        if(receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
