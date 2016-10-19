package com.atguigu.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miao on 2016/3/22.
 * 程序锁的数据库操作类
 */
public class AppLockDao {

    private final DBHelper dbHelper;

    public AppLockDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    // 查询
    public List<String> getAll(){
        List<String> list = new ArrayList<>();
        // 获取连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // 查询
        Cursor cursor = database.rawQuery("select package_name from app_lock", null);
        // 遍历
        while (cursor.moveToNext()){
            String packageName = cursor.getString(0);
            list.add(packageName);
        }
        // 关闭资源
        cursor.close();
        database.close();

        return list;
    }

    // 添加
    public void add(String packageName){
        // 创建连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // 添加数据
        ContentValues values = new ContentValues();
        values.put("package_name", packageName);
        long id = database.insert("app_lock", null, values);

        // 关闭资源
        database.close();
    }

    // 删除
    public void delete(String packageName){
        // 创建连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // 删除
        database.delete("app_lock","package_name=?",new String[]{packageName});

        // 关闭资源
        database.close();
    }
}
