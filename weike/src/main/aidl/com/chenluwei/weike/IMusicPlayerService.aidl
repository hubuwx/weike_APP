// IMusicPlayerService.aidl
package com.chenluwei.weike;

// Declare any non-default types here with import statements

interface IMusicPlayerService {
      /**
       * 根据位置打开音频
       * @param position
       */
      void openAudio(int position);
      /**
       * 音频的暂停
       */
      void pause();


       void play();
      /**
       * 播放下一个
       */
      void next();

      /**
       * 音频的拖动
       * @param position
       */
     void seekTo(int position);

      /**
       * 得到总时长
       */
      int getDuration();

      /**
       * 判断是否在播放音频
       * @return
       */
      boolean isPlaying();

      /**
       * 设置播放模式
       */
      void setPlayMode(int playMode);

      /**
       * 得到播放模式
       * @return
       */
      int getPlayMode();

         /**
           * 得到歌曲名称
           */
    String getAudioName();

          /**
           * 得到艺术家
           * @return
           */
     String getArtist();

     int getCurrentPosition();

     void notifyChange(String action);

     void pre();
}
