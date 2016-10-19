package com.chenluwei.weike.bean;

/**
 * Created by lw on 2016/3/27.
 * 用于将服务器传来的Json数据转化为该JavaBean
 */
public class UpdataInfo {

        /**
         * version:版本号(注意是)
         * apkUrl: 新版本的URL
         * desc:优化程序升级的说明
         */

        private String version;
        private String apkUrl;
        private String desc;

        public UpdataInfo() {
        }

        public UpdataInfo(String version, String apkUrl, String desc) {
            this.version = version;
            this.apkUrl = apkUrl;
            this.desc = desc;
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

        @Override
        public String toString() {
            return "UpdateInfo{" +
                    "version='" + version + '\'' +
                    ", apkUrl='" + apkUrl + '\'' +
                    ", desc='" + desc + '\'' +
                    '}';
        }
    }


