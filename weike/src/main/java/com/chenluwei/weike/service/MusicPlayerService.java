package com.chenluwei.weike.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.chenluwei.weike.IMusicPlayerService;
import com.chenluwei.weike.R;
import com.chenluwei.weike.bean.MediaItem;
import com.chenluwei.weike.media.AudioPlayerActivity;
import com.chenluwei.weike.util.SpUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lw on 2016/4/26.
 */
public class MusicPlayerService extends Service{
    private int position;//当前列表中播放到哪个了
    public static final String OPEN_AUDIO = "com.clw.OPENAUDIO";
    /**
     * 这个类可以播放视频也可以播放音频
     */
    private MediaPlayer mediaPlayer;
    /**
     * 顺序播放
     */
    public static final int REPEAT_NORMAL = 1;
    /**
     * 单曲播放
     */
    public static final int REPEAT_SINGLE = 2;
    /**
     * 全部播放
     */
    public static final int REPEAT_ALL = 3;

    /**
     * 播放模式
     */
    private int playmode = REPEAT_NORMAL;


    IMusicPlayerService.Stub stub= new IMusicPlayerService.Stub(){
        //用內部类获得外部类Service的引用，再调用service中的方法，而实现写在service的方法中
        MusicPlayerService service = MusicPlayerService.this;
        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void play() throws RemoteException {
            service.play();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            //返回mediaPlayer的播放状态
            return mediaPlayer.isPlaying();
        }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {
            service.setPlayMode(playMode);
        }



        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public String getAudioName() throws RemoteException {
            return service.getAudioName();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public void notifyChange(String action) throws RemoteException {
           service.notifyChange(action);
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }


    };
    /**
     * 音频列表
     */
    private ArrayList<MediaItem> mediaItems;
    private MediaItem mediaItem;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("bbb", "onBind");
        return stub;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("bbb", "service---onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    private void getData() {
        new Thread() {
            public void run() {

                mediaItems = new ArrayList<>();
                ContentResolver resolver = getContentResolver();
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
                        Log.e("TAGid", "" + id);
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
                //在pager中因为是要获得显示listview的数据，所以必须用handler发送。但在这里，只有当Activity中调用时才需要用到mediaItems中的数据
                //所以不需要再发送handler
                //handler.sendEmptyMessage(0);

            }
        }.start();


    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("bbb", "service---onCreate");
        //获取缓存的播放模式
        getPlayModeFromSave();
        getData();
    }

    /**
     * 获取本地缓存的播放模式
     */
    private void getPlayModeFromSave() {
        SpUtils.getInstance(this).getInt("playmode", playmode);

    }


    /**
     * 根据位置打开音频
     * @param position
     */
    private void openAudio(int position){
        if(mediaItems != null && mediaItems.size()>0) {
            try {
                mediaItem = mediaItems.get(position);
                this.position = position;
                if(mediaPlayer != null) {
                    //如果已经创建过播放类，则先释放(否则会调用两个mediaplayer就会播两首)
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                mediaPlayer = new MediaPlayer();
                //设置mediaplayer准备好的的监听
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                //设置mediaplayer播放出错的监听(这里不牵扯万能播放器，如果播放出错则播放下一首或者直接弹Toast)
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                //设置mediaplayer播放完成的监听
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                //设置播放数据
                //注意这里实际上使用的path参数
                mediaPlayer.setDataSource(mediaItem.getData());
                //这里需要调用准备方法而videoPlayer不需要调用，是因为videoPlayer中封装了mediaPlayer并调用了准备方法
                mediaPlayer.prepareAsync();//异步的准备

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(MusicPlayerService.this, "没有得到数据", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * mediaPlayer准备好的监听
     */
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener{

        @Override
        public void onPrepared(MediaPlayer mp) {
            notifyChange(OPEN_AUDIO);
            play();
        }
    }

    /**
     * 发广播通知Activity
     * @param action
     */
    private void notifyChange(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }



    /**
     * mediaPlayer播放出错的监听
     */
     class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //出错则播放下一首
            next();
            return true;
        }
    }

    /**
     * mediaPlayer播放完成的监听
     */
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            //播放完成则播放下一首
            next();
        }
    }

    private NotificationManager manager;

    /**
     * 音频的暂停
     */
    private void pause(){
        //停止播放歌曲
        mediaPlayer.pause();
        //状态栏停止
        manager.cancel(1);//这里因为发送的请求吗是1
    }

    /**
     * 播放下一个
     */
    private void next(){
        setNextPosition();//设置下标
        openNextAudio();//具体播放
    }

    private void openNextAudio() {
        int playmode = getPlayMode();
        if(playmode == REPEAT_NORMAL) {
            //顺序循环
            if(position <= mediaItems.size()-1) {
                openAudio(position);
            }else {
                position = mediaItems.size()-1;
            }

        }else if(playmode == REPEAT_SINGLE) {

           openAudio(position);
        }else if(playmode == REPEAT_ALL) {
            openAudio(position);
        }else {
            //默认
            //顺序循环
            if(position <= mediaItems.size()-1) {
                openAudio(position);
            }else {
                position = mediaItems.size()-1;
            }
        }
    }

    private void setNextPosition() {
        int playmode = getPlayMode();
        if(playmode == REPEAT_NORMAL) {
            //顺序循环
            position++;
        }else if(playmode == REPEAT_SINGLE) {
            //不处理
            position++;
        }else if(playmode == REPEAT_ALL) {
            position++;
            //播放最后一个位置了，
            if(position > mediaItems.size()-1) {
                position = 0;
            }
        }else {
            //默认
            position++;
        }
    }

    /**
     * 音频的拖动
     * @param position
     */
    private void seekTo(int position){
        if(mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    /**
     * 得到总时长
     */
    public int getDuration(){
        if(mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 判断是否在播放音频
     * @return
     */
    private boolean isPlaying(){
        return false;
    }

    /**
     * 设置播放模式
     */
    private void setPlayMode(int playMode){
        this.playmode = playMode;
    }


    private void pre(){
        setPrePosition();//设置下标
        openPreAudio();//具体播放
    }

    private void openPreAudio() {
        int playmode = getPlayMode();
        if(playmode == REPEAT_NORMAL) {
            //顺序循环
            if(position >= 0) {
                openAudio(position);
            }else {
                position = 0;
            }

        }else if(playmode == REPEAT_SINGLE) {
            //直接打开
            openAudio(position);
        }else if(playmode == REPEAT_ALL) {
            openAudio(position);
        }else {
            //默认
            //顺序循环
            if(position >= 0) {
                openAudio(position);
            }else {
                position = 0;
            }

        }
    }

    private void setPrePosition() {
        int playmode = getPlayMode();
        if(playmode == REPEAT_NORMAL) {
            //顺序循环
            position--;
        }else if(playmode == REPEAT_SINGLE) {
            //不处理
        }else if(playmode == REPEAT_ALL) {
            position--;
            //播放第一个了，
            if(position < 0) {
                position = mediaItems.size()-1;
            }
        }else {
            //默认
            position--;
        }
    }

    /**
     * 得到歌曲名称
     */
    private String getAudioName(){
        if(mediaItem != null) {
            return mediaItem.getName();
        }
        return "";
    }

    /**
     * 音频的播放
     */
    private void play(){
        mediaPlayer.start();
        //弹出状态栏-正常播放歌曲-点击后进入播放页面
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra("notification",true);//标示是从状态栏来的意图
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)//设置一个小图标
                .setContentTitle("微课")
                .setContentText("正在播放听力："+mediaItem.getName())
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(1,notification);
    }
    /**
     * 得到艺术家
     * @return
     */
    private String getArtist(){
        if(mediaItem != null) {
            return  mediaItem.getArtist();
        }
        return "";
    }
    /**
     * 得到播放模式
     * @return
     */
    private int getPlayMode(){
        return 0;
    }

    public int getCurrentPosition(){
        if(mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("bbb", "service---onUnbind");
        return super.onUnbind(intent);
    }



    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.e("bbb", "service---onDestroy");
    }
}
