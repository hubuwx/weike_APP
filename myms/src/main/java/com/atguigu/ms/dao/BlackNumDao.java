package com.atguigu.ms.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.atguigu.ms.bean.BlackNumInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/3/30.
 * 黑名单操作dao
 */
public class BlackNumDao {

    private final DBHelper mDbHelper;

    public BlackNumDao(Context context) {
        mDbHelper = new DBHelper(context);
    }


    // 查询
    public List<BlackNumInfo> get() {
        List<BlackNumInfo> list = new ArrayList<>();
        // 获取数据库
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // 查询
        String sql = "select *from black_num order by _id desc";
        Cursor cursor = database.rawQuery(sql, null);
        // 遍历
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String number = cursor.getString(1);
            BlackNumInfo blackNumInfo = new BlackNumInfo(id, number);
            list.add(blackNumInfo);
        }

        // 关闭资源
        cursor.close();
        database.close();

        return list;
    }

    // 添加
    public int add(BlackNumInfo blackNumInfo) {
        // 获取数据库
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // 执行添加操作
        ContentValues values = new ContentValues();
        values.put("number", blackNumInfo.getNumber());
        long id = database.insert("black_num", null, values);
//        blackNumInfo.setId((int) id);
        // 关闭资源
        database.close();

        return (int) id;
    }

    // 更新
    public void update(BlackNumInfo blackNumInfo) {
        // 获取数据库
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // 执行更新操作
        ContentValues values = new ContentValues();
        values.put("number", blackNumInfo.getNumber());
        int count = database.update("black_num", values, "_id=?", new String[]{blackNumInfo.getId() + ""});

        // 关闭资源
        database.close();
    }

    // 删除
    public void deleteById(int id){
        // 获取数据库
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // 执行删除操作
        int count = database.delete("black_num", "_id=?", new String[]{id + ""});

        // 关闭资源
        database.close();
    }

    // 判断是否是黑名单号码
    public boolean isBlackNum(String phoneNum) {
        boolean isBlacknum = false;

        // 获取数据库
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // 查询
        String sql = "select *from black_num where number=?";
        Cursor cursor = database.rawQuery(sql, new String[]{phoneNum});
        isBlacknum = cursor.moveToNext();

        // 关闭资源
        cursor.close();
        database.close();


        return isBlacknum;
    }
}
