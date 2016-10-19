package com.chenluwei.weike.bean;

import java.util.List;

/**
 * Created by lw on 2016/4/29.
 */
public class WeiboInfo {
    @Override
    public String toString() {
        return "WeiboInfo{" +
                "bookmark='" + bookmark + '\'' +
                ", comment='" + comment + '\'' +
                ", down=" + down +
                ", forward='" + forward + '\'' +
                ", id='" + id + '\'' +
                ", image=" + image +
                ", gif=" + gif +
                ", video=" + video +
                ", passtime='" + passtime + '\'' +
                ", share_url='" + share_url + '\'' +
                ", text='" + text + '\'' +
                ", type='" + type + '\'' +
                ", u=" + u +
                ", up='" + up + '\'' +
                ", tags=" + tags +
                '}';
    }

    /**
     * bookmark : 82
     * comment : 19
     * down : 91
     * forward : 177
     * id : 18264037
     * image : {"big":["http://wimg.spriteapp.cn/ugc/2016/04/27/5720a059db297_1.jpg","http://dimg.spriteapp.cn/ugc/2016/04/27/5720a059db297_1.jpg"],"download_url":["http://wimg.spriteapp.cn/ugc/2016/04/27/5720a059db297_d.jpg","http://dimg.spriteapp.cn/ugc/2016/04/27/5720a059db297_d.jpg","http://wimg.spriteapp.cn/ugc/2016/04/27/5720a059db297.jpg","http://dimg.spriteapp.cn/ugc/2016/04/27/5720a059db297.jpg"],"height":808,"medium":[],"small":[],"width":743}
     * passtime : 2016-04-29 16:02:01
     * share_url : http://b.f.zk111.com.cn/share/18264037.html?wx.qq.com
     * tags : [{"id":1,"name":"搞笑"},{"id":61,"name":"恶搞"},{"id":863,"name":"奇葩"}]
     * text : 小小年纪就有如此高境界的领悟
     * type : image
     * u : {"header":["http://tp2.sinaimg.cn/1588923021/50/5617939022/1","http://tp2.sinaimg.cn/1588923021/50/5617939022/1"],"is_v":false,"is_vip":false,"name":"周C_c","uid":"15443632"}
     * up : 807
     */

    private String bookmark;
    private String comment;
    private int down;
    private String forward;
    private String id;
    /**
     * big : ["http://wimg.spriteapp.cn/ugc/2016/04/27/5720a059db297_1.jpg","http://dimg.spriteapp.cn/ugc/2016/04/27/5720a059db297_1.jpg"]
     * download_url : ["http://wimg.spriteapp.cn/ugc/2016/04/27/5720a059db297_d.jpg","http://dimg.spriteapp.cn/ugc/2016/04/27/5720a059db297_d.jpg","http://wimg.spriteapp.cn/ugc/2016/04/27/5720a059db297.jpg","http://dimg.spriteapp.cn/ugc/2016/04/27/5720a059db297.jpg"]
     * height : 808
     * medium : []
     * small : []
     * width : 743
     */

    private ImageBean image;
    private GifBean gif;
    private VideoBean video;

    private String passtime;
    private String share_url;
    private String text;
    private String type;

    public VideoBean getVideo() {
        return video;
    }

    public void setVideo(VideoBean video) {
        this.video = video;
    }

    /**
     * header : ["http://tp2.sinaimg.cn/1588923021/50/5617939022/1","http://tp2.sinaimg.cn/1588923021/50/5617939022/1"]
     * is_v : false
     * is_vip : false
     * name : 周C_c
     * uid : 15443632
     */



     public static  class VideoBean{

        /**
         * download : ["http://wvideo.spriteapp.cn/video/2016/0406/5704f07099587_wpc.mp4","http://dvideo.spriteapp.cn/video/2016/0406/5704f07099587_wpc.mp4","http://bvideo.spriteapp.cn/video/2016/0406/5704f07099587_wpc.mp4"]
         * duration : 74
         * height : 236
         * playcount : 11836
         * playfcount : 4388
         * thumbnail : ["http://wimg.spriteapp.cn/picture/2016/0406/5704f07099587__10.jpg","http://dimg.spriteapp.cn/picture/2016/0406/5704f07099587__10.jpg"]
         * video : ["http://wvideo.spriteapp.cn/video/2016/0406/5704f07099587_wpd.mp4","http://dvideo.spriteapp.cn/video/2016/0406/5704f07099587_wpd.mp4","http://bvideo.spriteapp.cn/video/2016/0406/5704f07099587_wpd.mp4"]
         * width : 426
         */

        private int duration;
        private int height;
        private int playcount;
        private int playfcount;
        private int width;
        private List<String> download;
        private List<String> thumbnail;
        private List<String> video;

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getPlaycount() {
            return playcount;
        }

        public void setPlaycount(int playcount) {
            this.playcount = playcount;
        }

        public int getPlayfcount() {
            return playfcount;
        }

        public void setPlayfcount(int playfcount) {
            this.playfcount = playfcount;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public List<String> getDownload() {
            return download;
        }

        public void setDownload(List<String> download) {
            this.download = download;
        }

        public List<String> getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(List<String> thumbnail) {
            this.thumbnail = thumbnail;
        }

        public List<String> getVideo() {
            return video;
        }

        public void setVideo(List<String> video) {
            this.video = video;
        }
    }


    private UBean u;
    private String up;
    /**
     * id : 1
     * name : 搞笑
     */

    private List<TagsBean> tags;

    public String getBookmark() {
        return bookmark;
    }

    public void setBookmark(String bookmark) {
        this.bookmark = bookmark;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getDown() {
        return down;
    }

    public void setDown(int down) {
        this.down = down;
    }

    public String getForward() {
        return forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ImageBean getImage() {
        return image;
    }

    public void setImage(ImageBean image) {
        this.image = image;
    }

    public String getPasstime() {
        return passtime;
    }

    public void setPasstime(String passtime) {
        this.passtime = passtime;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UBean getU() {
        return u;
    }

    public void setU(UBean u) {
        this.u = u;
    }

    public String getUp() {
        return up;
    }

    public void setUp(String up) {
        this.up = up;
    }

    public List<TagsBean> getTags() {
        return tags;
    }

    public void setTags(List<TagsBean> tags) {
        this.tags = tags;
    }

    public static class ImageBean {
        private int height;
        private int width;
        private List<String> big;
        private List<String> download_url;
        private List<?> medium;
        private List<?> small;

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public List<String> getBig() {
            return big;
        }

        public void setBig(List<String> big) {
            this.big = big;
        }

        public List<String> getDownload_url() {
            return download_url;
        }

        public void setDownload_url(List<String> download_url) {
            this.download_url = download_url;
        }

        public List<?> getMedium() {
            return medium;
        }

        public void setMedium(List<?> medium) {
            this.medium = medium;
        }

        public List<?> getSmall() {
            return small;
        }

        public void setSmall(List<?> small) {
            this.small = small;
        }
    }


    public static class GifBean {

        /**
         * download_url : ["http://wimg.spriteapp.cn/ugc/2016/04/28/57218c33ad5cf_d.jpg","http://dimg.spriteapp.cn/ugc/2016/04/28/57218c33ad5cf_d.jpg","http://wimg.spriteapp.cn/ugc/2016/04/28/57218c33ad5cf_a_1.jpg","http://dimg.spriteapp.cn/ugc/2016/04/28/57218c33ad5cf_a_1.jpg"]
         * gif_thumbnail : ["http://wimg.spriteapp.cn/ugc/2016/04/28/57218c33ad5cf_a_1.jpg","http://dimg.spriteapp.cn/ugc/2016/04/28/57218c33ad5cf_a_1.jpg"]
         * height : 210
         * images : ["http://wimg.spriteapp.cn/ugc/2016/04/28/57218c33ad5cf.gif","http://dimg.spriteapp.cn/ugc/2016/04/28/57218c33ad5cf.gif"]
         * width : 280
         */

        private int height;
        private int width;
        private List<String> download_url;
        private List<String> gif_thumbnail;
        private List<String> images;

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public List<String> getDownload_url() {
            return download_url;
        }

        public void setDownload_url(List<String> download_url) {
            this.download_url = download_url;
        }

        public List<String> getGif_thumbnail() {
            return gif_thumbnail;
        }

        public void setGif_thumbnail(List<String> gif_thumbnail) {
            this.gif_thumbnail = gif_thumbnail;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }

    public static class UBean {
        private boolean is_v;
        private boolean is_vip;
        private String name;
        private String uid;
        private List<String> header;

        public boolean isIs_v() {
            return is_v;
        }

        public void setIs_v(boolean is_v) {
            this.is_v = is_v;
        }

        public boolean isIs_vip() {
            return is_vip;
        }

        public void setIs_vip(boolean is_vip) {
            this.is_vip = is_vip;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public List<String> getHeader() {
            return header;
        }

        public void setHeader(List<String> header) {
            this.header = header;
        }
    }

    public static class TagsBean {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public GifBean getGif() {
        return gif;
    }

    public void setGif(GifBean gif) {
        this.gif = gif;
    }
}
