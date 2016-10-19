package com.atguigu.mobilesafe.bean;

/**
 * Created by miao on 2016/3/15.
 */
public class UpdateInfo {
    private  String version;
    private String apkUrl;
    private String desc;

    public UpdateInfo(String version, String apkUrl, String desc) {
        this.version = version;
        this.apkUrl = apkUrl;
        this.desc = desc;
    }

    public UpdateInfo() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
