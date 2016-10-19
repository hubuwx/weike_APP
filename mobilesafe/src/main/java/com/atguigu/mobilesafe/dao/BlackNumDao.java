package com.atguigu.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.atguigu.mobilesafe.bean.BlackNumInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miao on 2016/3/22.
 */
public class BlackNumDao {

    private final DBHelper dbHelper;

    public BlackNumDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    // 查
    public List<BlackNumInfo> getBlackNum() {
        List<BlackNumInfo> list = new ArrayList<>();
        // 获取数据库连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sql = "select * from black_num order by _id desc";
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String number = cursor.getString(1);
            BlackNumInfo blackNumInfo = new BlackNumInfo(id, number);
            list.add(blackNumInfo);
        }

        cursor.close();
        database.close();

        return list;
    }

    // 增
    public void addBlackNum(BlackNumInfo blackNumInfo) {
        // 获取数据库连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // 添加数据库
        ContentValues values = new ContentValues();
        values.put("number", blackNumInfo.getNumber());
        long id = database.insert("black_num", null, values);
        blackNumInfo.setId((int) id);
        // 关闭资源
        database.close();
    }

    // 删
    public void deleteBlackNum(BlackNumInfo blackNumInfo) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        int id = database.delete("black_num", "_id=?", new String[]{blackNumInfo.getId() + ""});

        database.close();
    }

    // 改
    public void updateBlackNum(BlackNumInfo blackNunInfo) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", blackNunInfo.getNumber());
        database.update("black_num", values, "_id=?", new String[]{blackNunInfo.getId() + ""});
    }

    // 判断是否是黑名单号码
    public boolean isBlackNum(String number) {
        boolean isBlack = false;

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("select * from black_num where number=?", new String[]{number});
        isBlack = cursor.getCount() > 0;
        database.close();

        return isBlack;
    }
}
