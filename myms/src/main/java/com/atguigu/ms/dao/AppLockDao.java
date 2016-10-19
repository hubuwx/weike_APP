package com.atguigu.ms.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/4/2.
 * 程序锁的dao
 */
public class AppLockDao {

    private final DBHelper mDbHelper;

    public AppLockDao(Context context) {
        mDbHelper = new DBHelper(context);
    }

    // 查询
    public List<String> get(){
        List<String> list = new ArrayList<>();

        //获取数据库
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // 执行查询
        String sql = "select package_name from app_lock";
        Cursor cursor = database.rawQuery(sql, null);
        //遍历
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

        // 获取数据库
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // 执行添加语句

        ContentValues values = new ContentValues();
        values.put("package_name", packageName);
        long id = database.insert("app_lock", null, values);

        // 关闭资源
        database.close();
    }

    // 删除
    public void delete(String packageName){

        // 获取数据库
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // 执行删除语句
        int conut = database.delete("app_lock", "package_name=?", new String[]{packageName});


        // 关闭资源
        database.close();
    }

}
