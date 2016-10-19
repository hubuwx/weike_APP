package com.chenluwei.weike.bean;

import java.io.Serializable;

/**
 * Created by lw on 2016/5/8.
 * 下载文件的bean类
 */
public class DownloadFileInfo implements Serializable {
    private int id;
    private String url;
    private String fileName;
    private int length;//文件的长度
    private int finished;//完成的进度


    @Override
    public String toString() {
        return "DownloadFileInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", length=" + length +
                ", finished=" + finished +
                '}';
    }

    public DownloadFileInfo() {
    }

    public DownloadFileInfo(int id, String url, String fileName, int length, int finished) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.length = length;
        this.finished = finished;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }
}
