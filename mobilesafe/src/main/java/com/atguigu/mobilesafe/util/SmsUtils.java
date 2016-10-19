package com.atguigu.mobilesafe.util;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xfzhang on 2016/2/20.
 * 短信备份和还原的工具类
 */
public class SmsUtils {
    /**
     * 还原短信
     * @param context
     */
    public static void restore(final Context context) {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(context);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                ContentResolver resolver = context.getContentResolver();

                //1. 读取sms.json文件, 解析json成List<Map<String,String>>类型的集合
                try {
                    FileInputStream fis = context.openFileInput("sms.json");
                    List<Map<String,String>> list = new Gson().fromJson(new InputStreamReader(fis, "utf-8"),
                            new TypeToken<List<Map<String,String>>>(){}.getType());
                    pd.setMax(list.size());
                    //2. 遍历集合, 使用resolver将数据保存到sms表中
                    ContentValues values = new ContentValues();
                    for (Map<String, String> map : list) {
                        values.put("address", map.get("address"));
                        values.put("date", map.get("date"));
                        values.put("type", map.get("type"));
                        values.put("body", map.get("body"));
                        resolver.insert(Uri.parse("content://sms"), values);

                        SystemClock.sleep(500);
                        pd.incrementProgressBy(1);
                    }

                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                pd.dismiss();
                MsUtils.showMsg(context, "还原完成");
            }
        }.execute();
    }

    private static ProgressDialog pd;
    /**
     * 备份短信
     * @param context
     */
    public static void backup(final Context context) {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(context);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                //1. 查询得到所有的短信数据的集合
                ContentResolver resolver = context.getContentResolver();
                Cursor cursor = resolver.query(Uri.parse("content://sms"), new String[]{"address", "date", "type", "body"},
                        null, null, null);
                pd.setMax(cursor.getCount());
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                while (cursor.moveToNext()) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("address", cursor.getString(0));
                    map.put("date", cursor.getString(1));
                    map.put("type", cursor.getString(2));
                    map.put("body", cursor.getString(3));
                    list.add(map);

                    SystemClock.sleep(500);
                    pd.incrementProgressBy(1);
                }
                cursor.close();
                //2. 转换为json字符串
                String json = new Gson().toJson(list);

                //3. 保存到文件中(files/sms.json)
                try {
                    FileOutputStream fos = context.openFileOutput("sms.json", Context.MODE_PRIVATE);
                    fos.write(json.getBytes("utf-8"));
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                pd.dismiss();
                MsUtils.showMsg(context, "备份完成");
            }
        }.execute();
    }
}
