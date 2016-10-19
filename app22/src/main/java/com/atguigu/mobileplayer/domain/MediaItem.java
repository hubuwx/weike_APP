package com.atguigu.mobileplayer.domain;

import java.io.Serializable;

/**
 * 作者：杨光福 on 2016/4/20 15:15
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：代表一个音频或者视频
 */
public class MediaItem  implements Serializable{

    private String name;//movieName

    private long duration;

    private long size;

    private String data;//url

    private String coverImg;

    private String videoTitle;

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                ", coverImg='" + coverImg + '\'' +
                ", videoTitle='" + videoTitle + '\'' +
                '}';
    }
}
