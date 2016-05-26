package com.chenluwei.weike.bean;

/**
 * Created by lw on 2016/5/8.
 * 下载线程的信息
 */
public class ThreadInfo {
    private int id;
    private String url;
    private int start;//从哪里开始下载
    private int end;//到哪里结束
    private int finished;//完成了多少

    public ThreadInfo() {
    }

    public ThreadInfo(int id, String url, int start, int end, int finished) {

        this.id = id;
        this.url = url;
        this.start = start;
        this.end = end;
        this.finished = finished;
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

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
