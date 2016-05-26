package com.chenluwei.weike.bean;

import java.io.Serializable;

/**
 * Created by lw on 2016/4/20.
 * 作用：代表一个音频或者视频
 * 注意这个Bean要序列化
 */
public class MediaItem implements Serializable {
    private String name;

    private long createTime;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    private long duration;

    private long size;

    private String data;

    private String quantity;

    private  String plid;//这里是用来查找相应的二级json的id

    private  String thunmbnail;

    private String des;

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getThunmbnail() {
        return thunmbnail;
    }

    public void setThunmbnail(String thunmbnail) {
        this.thunmbnail = thunmbnail;
    }

    public String getPlid() {
        return plid;
    }

    public void setPlid(String plid) {
        this.plid = plid;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    private String title;

    private String coverImg;

    private String artist;

    private long countView;

    public long getCountView() {
        return countView;
    }

    public void setCountView(long countView) {
        this.countView = countView;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
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
                ", createTime=" + createTime +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                ", quantity='" + quantity + '\'' +
                ", plid='" + plid + '\'' +
                ", title='" + title + '\'' +
                ", coverImg='" + coverImg + '\'' +
                ", artist='" + artist + '\'' +
                ", countView=" + countView +
                '}';
    }
}
