package com.chenluwei.weike.bean;

import java.util.List;

/**
 * Created by lw on 2016/6/9.
 */
public class TabDetailInfo {

    /**
     * cursor : 10
     * data : [{"plid":"MA0BO3ALV","rid":"MA0BOP6AH","rtype":2,"courseType":"医学 可汗学院","title":"可汗学院公开课：保健与医学-循环系统","description":null,"viewcount":77652,"picUrl":"http://img2.ph.126.net/CmrpZYCQZXfVCYmigUezRQ==/1325465665348561217.jpg","publishTime":1465375854000,"quantity":"共95集","tagBgColor":null,"flag":1,"playcount":95,"pageUrl":null},{"plid":"M8DOH67K8","rid":"M8DOHB01D","rtype":2,"courseType":"经济 可汗学院","title":"可汗学院公开课：金融学","description":null,"viewcount":1911957,"picUrl":"http://img0.ph.126.net/F17T1f7b0kSy1GiCG3miSg==/6631237090492726296.jpg","publishTime":1465375750000,"quantity":"共197集","tagBgColor":null,"flag":1,"playcount":197,"pageUrl":null},{"plid":"M8PDR0NF8","rid":"M8PDR489P","rtype":2,"courseType":"数学 可汗学院","title":"可汗学院公开课：复数","description":null,"viewcount":38432,"picUrl":"http://img6.cache.netease.com/video/2014/4/16/2014041610310297cbe.jpg","publishTime":1459134078000,"quantity":"共24集","tagBgColor":null,"flag":1,"playcount":24,"pageUrl":null},{"plid":"M8LHU8P7V","rid":"M8LI5NFFH","rtype":2,"courseType":"数学 可汗学院","title":"可汗学院公开课：对数","description":null,"viewcount":62700,"picUrl":"http://img1.ph.126.net/ZSK7uLu5GVJAkMv1KU6Glw==/6631364633841546385.jpg","publishTime":1459134017000,"quantity":"共21集","tagBgColor":null,"flag":1,"playcount":21,"pageUrl":null},{"plid":"M7S6Q22NH","rid":"M7S9A4MRT","rtype":2,"courseType":"数学 可汗学院","title":"可汗学院公开课：基础代数","description":null,"viewcount":521359,"picUrl":"http://img0.ph.126.net/bP2k0-owN5CGZXxNVzm88g==/6631353638725269036.jpg","publishTime":1459133661000,"quantity":"共91集","tagBgColor":null,"flag":1,"playcount":91,"pageUrl":null},{"plid":"M7S6P0RL8","rid":"M7S8STEGN","rtype":2,"courseType":"化学 可汗学院","title":"可汗学院公开课：基础化学","description":null,"viewcount":600759,"picUrl":"http://img2.ph.126.net/YkWpw5FhoXgbgGlnZEGYsw==/6631254682678765145.jpg","publishTime":1459133628000,"quantity":"共104集","tagBgColor":null,"flag":1,"playcount":104,"pageUrl":null},{"plid":"M84PSU1FA","rid":"M84PT34R2","rtype":2,"courseType":"数学 可汗学院","title":"可汗学院公开课：代数习题课","description":null,"viewcount":105427,"picUrl":"http://img0.ph.126.net/PR2m0TO8-UVWByezvLVwwg==/6631431704050845411.jpg","publishTime":1458906240000,"quantity":"共107集","tagBgColor":null,"flag":1,"playcount":107,"pageUrl":null},{"plid":"M8O9CQ2V6","rid":"M8OC91QU7","rtype":2,"courseType":"数学 可汗学院","title":"可汗学院公开课：绝对值","description":null,"viewcount":22911,"picUrl":"http://img6.cache.netease.com/video/2014/3/5/20140305101544cffbd.jpg","publishTime":1458906240000,"quantity":"共15集","tagBgColor":null,"flag":1,"playcount":15,"pageUrl":null},{"plid":"M82IF3HFQ","rid":"M831V1DBQ","rtype":2,"courseType":"数学 可汗学院","title":"可汗学院公开课：概率 ","description":null,"viewcount":472109,"picUrl":"http://img2.ph.126.net/12_afd9IjrwO5EwN-v9tRg==/6631274473888070048.jpg","publishTime":1458906233000,"quantity":"共55集","tagBgColor":null,"flag":1,"playcount":55,"pageUrl":null},{"plid":"M84QEN6NI","rid":"M84QEPFKJ","rtype":2,"courseType":"数学 可汗学院","title":"可汗学院公开课：欧几里得几何学","description":null,"viewcount":44297,"picUrl":"http://img0.ph.126.net/8TfRNf4HfQ8fNw_kPG8fzw==/6631345942143886134.jpg","publishTime":1458906232000,"quantity":"共12集","tagBgColor":null,"flag":1,"playcount":12,"pageUrl":null}]
     * code : 200
     */

    private String cursor;
    private int code;
    /**
     * plid : MA0BO3ALV
     * rid : MA0BOP6AH
     * rtype : 2
     * courseType : 医学 可汗学院
     * title : 可汗学院公开课：保健与医学-循环系统
     * description : null
     * viewcount : 77652
     * picUrl : http://img2.ph.126.net/CmrpZYCQZXfVCYmigUezRQ==/1325465665348561217.jpg
     * publishTime : 1465375854000
     * quantity : 共95集
     * tagBgColor : null
     * flag : 1
     * playcount : 95
     * pageUrl : null
     */

    private List<DataBean> data;

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String plid;
        private String rid;
        private int rtype;
        private String courseType;
        private String title;
        private Object description;
        private int viewcount;
        private String picUrl;
        private long publishTime;
        private String quantity;
        private Object tagBgColor;
        private int flag;
        private int playcount;
        private Object pageUrl;

        public String getPlid() {
            return plid;
        }

        public void setPlid(String plid) {
            this.plid = plid;
        }

        public String getRid() {
            return rid;
        }

        public void setRid(String rid) {
            this.rid = rid;
        }

        public int getRtype() {
            return rtype;
        }

        public void setRtype(int rtype) {
            this.rtype = rtype;
        }

        public String getCourseType() {
            return courseType;
        }

        public void setCourseType(String courseType) {
            this.courseType = courseType;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Object getDescription() {
            return description;
        }

        public void setDescription(Object description) {
            this.description = description;
        }

        public int getViewcount() {
            return viewcount;
        }

        public void setViewcount(int viewcount) {
            this.viewcount = viewcount;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public long getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(long publishTime) {
            this.publishTime = publishTime;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public Object getTagBgColor() {
            return tagBgColor;
        }

        public void setTagBgColor(Object tagBgColor) {
            this.tagBgColor = tagBgColor;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public int getPlaycount() {
            return playcount;
        }

        public void setPlaycount(int playcount) {
            this.playcount = playcount;
        }

        public Object getPageUrl() {
            return pageUrl;
        }

        public void setPageUrl(Object pageUrl) {
            this.pageUrl = pageUrl;
        }
    }
}
