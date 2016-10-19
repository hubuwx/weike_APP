package com.atguigu.ms.bean;

/**
 * Created by lenovo on 2016/3/30.
 * 黑名单
 */
public class BlackNumInfo {
    private  int id;
    private String number;

    public BlackNumInfo(int id, String number) {
        this.id = id;
        this.number = number;
    }

    public BlackNumInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "BlackNumInfo{" +
                "id=" + id +
                ", number='" + number + '\'' +
                '}';
    }
}
