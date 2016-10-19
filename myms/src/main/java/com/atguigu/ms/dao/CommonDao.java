package com.atguigu.ms.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lw on 2016/4/8.
 */
public class CommonDao {
    private Context mContext;
    public CommonDao(Context context){
        mContext = context;
    }
    //获取组数据
    public List<String> getGroupList(){
        List<String> list = new ArrayList<String>();
        //获取数据库
        String path = mContext.getFilesDir().getAbsolutePath()+"/commonnum.db";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        Cursor cursor = database.rawQuery("select * from classlist", null);

        while (cursor.moveToNext()){
            String name = cursor.getString(0);
            list.add(name);
        }

        cursor.close();
        database.close();

        return list;
    }

    //获取子数据
    public List<List<String>> getChildList(){
        List<List<String>> list = new ArrayList<>();

        String path = mContext.getFilesDir().getAbsolutePath()+"/commonnum.db";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        for (int i = 1;i<=8;i++){
            List<String> childList = new ArrayList<>();

            Cursor cursor = database.rawQuery("select name,number from table" + i, null);
            while(cursor.moveToNext()) {
                String name = cursor.getString(0);
                String num = cursor.getString(1);

                childList.add(name+"_"+num);
            }

            list.add(childList);

            cursor.close();
        }
        database.close();


        return list;
    }

}
