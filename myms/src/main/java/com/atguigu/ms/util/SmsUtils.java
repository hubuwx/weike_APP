package com.atguigu.ms.util;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.Toast;

import com.atguigu.ms.activity.ToolActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lw on 2016/4/8.
 */
public class SmsUtils {


    private static ProgressDialog mPd;
    public static void restore(final Context context) {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPreExecute() {
                mPd =new ProgressDialog(context);
                mPd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mPd.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                List<Map<String,String>> list = new ArrayList<Map<String, String>>();

                ContentResolver resolver = context.getContentResolver();
                Cursor cursor = resolver.query(Uri.parse("content://sms"), new String[]{"address", "date", "type", "body"}, null, null, null);

                mPd.setMax(cursor.getCount());

                while (cursor.moveToNext()){
                    String address = cursor.getString(0);
                    String date = cursor.getString(1);
                    String type = cursor.getString(2);
                    String body = cursor.getString(3);
                    Map<String,String> map = new HashMap<String, String>();

                    map.put("address",address);
                    map.put("date",date);
                    map.put("type",type);
                    map.put("body",body);
                    list.add(map);

                    mPd.incrementProgressBy(1);
                    SystemClock.sleep(500);
                }

                String json = new Gson().toJson(list);

                FileOutputStream fos = null;
                String path = context.getFilesDir().getAbsolutePath()+"/sms.json";


                try {
                    fos = context.openFileOutput(path,Context.MODE_PRIVATE);
                    fos.write(json.getBytes("utf-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(cursor != null) {
                        cursor.close();
                    }
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mPd.dismiss();
                Toast.makeText(context, "备份完成", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    public static void backup(final Context context) {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPreExecute() {
                mPd = new ProgressDialog(context);
                mPd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mPd.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                List<Map<String,String>> list = new ArrayList<Map<String, String>>();
                String path = context.getFilesDir().getAbsolutePath()+"/sms.json";
                FileInputStream fis = null;

                try {
                    fis = context.openFileInput(path);
                    list = new Gson().fromJson(new InputStreamReader(fis), new TypeToken<List<Map<String, String>>>() {
                    }.getType());
                    mPd.setMax(list.size());
                    ContentResolver resolver = context.getContentResolver();
                    ContentValues values = new ContentValues();
                    for (Map<String,String> map:list){
                        values.put("address",map.get("address"));
                        values.put("date",map.get("date"));
                        values.put("type",map.get("type"));
                        values.put("body",map.get("body"));

                        resolver.insert(Uri.parse("content://sms"), values);
                        mPd.incrementProgressBy(1);

                        SystemClock.sleep(500);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                    mPd.dismiss();
                    Toast.makeText(context, "还原完成", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}
