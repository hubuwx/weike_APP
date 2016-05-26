package com.chenluwei.weike.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.chenluwei.weike.bean.DownloadFileInfo;
import com.chenluwei.weike.bean.ThreadInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lw on 2016/5/8.
 */
public class ThreadDaoImpl implements ThreadDAO {
    /**
     * 数据访问接口实现
     * @author Yann
     * @date 2015-8-8 上午11:00:38
     */

        private DBHelper mHelper = null;

        public ThreadDaoImpl(Context context)
        {
            mHelper = DBHelper.getInstance(context);
        };


        @Override
        public synchronized void insertThread(ThreadInfo threadInfo,DownloadFileInfo fileInfo,Integer progress)
        {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.execSQL("insert into thread_info(thread_id,url,start,end,finished,fileName,progress) values(?,?,?,?,?,?,?)",
                    new Object[]{threadInfo.getId(), threadInfo.getUrl(),
                            threadInfo.getStart(), threadInfo.getEnd(), threadInfo.getFinished(),fileInfo.getFileName(),progress});
            db.close();
        }




        @Override
        public synchronized void deleteThread(String url)
        {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.execSQL("delete from thread_info where url = ?",
                    new Object[]{url});
            db.close();
        }


        @Override
        public synchronized void updateThread(String url, int thread_id, int finished,int progress)
        {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.execSQL("update thread_info set finished = ?  ,progress = ?   where url = ? and thread_id = ?",
                    new Object[]{finished,progress, url, thread_id});
            db.close();
        }


        @Override
        public List<ThreadInfo> getThreads(String url)
        {
            List<ThreadInfo> list = new ArrayList<ThreadInfo>();

            SQLiteDatabase db = mHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from thread_info where url = ?", new String[]{url});
            while (cursor.moveToNext())
            {
                ThreadInfo threadInfo = new ThreadInfo();
                threadInfo.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
                threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                threadInfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
                threadInfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
                threadInfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
                list.add(threadInfo);
            }
            cursor.close();
            db.close();
            return list;
        }


    /**
     *
     * @return
     */
    @Override
    public List<Map<String,Object>> getFileSimpleInfo() {
        List<Map<String,Object>> list = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select fileName,progress,url from thread_info where thread_id = ?",new String[]{"0"});
        while(cursor.moveToNext()) {
            Map map = new HashMap();
            Log.i("downloadfilesimple", "map-----" + cursor.getString(0));
            map.put("fileName", cursor.getString(0));
            Log.i("downloadfilesimple", "map-----" + cursor.getInt(1));
            map.put("progress", cursor.getInt(1));
            Log.i("downloadfilesimple", "map-----"+cursor.getString(2));
            map.put("url",cursor.getString(2));
            list.add(map);
        }
        cursor.close();
        db.close();
        return list;
    }


    @Override
        public boolean isExists(String url, int thread_id)
        {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from thread_info where url = ? and thread_id = ?", new String[]{url, thread_id+""});
            boolean exists = cursor.moveToNext();
            cursor.close();
            db.close();
            return exists;
        }
    }


