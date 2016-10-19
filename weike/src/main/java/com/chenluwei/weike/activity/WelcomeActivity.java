package com.chenluwei.weike.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chenluwei.weike.R;
import com.chenluwei.weike.bean.FileInfo;
import com.chenluwei.weike.bean.MyUser;
import com.chenluwei.weike.bean.UpdataInfo;
import com.chenluwei.weike.net.APIClient;
import com.chenluwei.weike.util.Contants;
import com.chenluwei.weike.util.SpUtils;
import com.chenluwei.weike.util.WkUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.GetListener;

public class WelcomeActivity extends Activity {

    private static final int CONN_SUCCESS = 1;
    private static final int CONN_FAIL = 2;
    private static final int DOWN_SUCCESS = 3;
    private static final int DOWN_FAIL = 4;
    private static final int TO_MAIN_UI = 5;

    private String mVersion;//当前版本号

    private RelativeLayout rl_welcome_background;//背景控件
    private TextView tv_welcome_version;//版本号控件
//    private UpdataInfo mUpdataInfo;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(isFinishing()) {
                return;
            }
            switch (msg.what){
                case CONN_SUCCESS://联网成功，检查版本号
                    updataInfo = (UpdataInfo) msg.obj;
                    if(mVersion.equals(updataInfo.getVersion())) {

                        toMainUI();
                    }else {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showDownloadDialog();

                            }
                        }, 3000);
                    }
                    break;
                case CONN_FAIL:
                    Toast.makeText(WelcomeActivity.this, "获取版本号失败", Toast.LENGTH_SHORT).show();
                    toMainUI();
                    break;

                case DOWN_SUCCESS:
                    //下载成功，安装最新版本
                    //取消进度条
                    mPd.dismiss();
                    //进入安装界面
                    install();
                    break;

                case DOWN_FAIL:
                    Toast.makeText(WelcomeActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    toMainUI();
                    break;

                //进入主界面
                case TO_MAIN_UI:
                    Log.e("TAG", "进入tomain");
                    //判断是否要直接进入主界面
                    boolean isEnterMain = SpUtils.getInstance(WelcomeActivity.this).getBoolean(SpUtils.ENTERMAIN,false);
                    if(isEnterMain) {//如果为true，进入主界面
                        //判断是否进入注册界面
                        Log.e("TAG", "进入isEnterMain");

                        MyUser user = BmobUser.getCurrentUser(WelcomeActivity.this,MyUser.class);

                        if(user!= null) {

                            //如果已经有缓存，则直接进入主界面
                            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                            intent.putExtra("user",user);
                            startActivity(intent);
                            finish();
                        }else {
                            //Log.e("TAG", "3333333" + user.toString());
                            Intent intent = new Intent(WelcomeActivity.this, LoadActivity.class);
                            //intent.putExtra("user",user);
                            startActivity(intent);
                            finish();
                        }

                        //切换动画
                        //overridePendingTransition( R.anim.right_in, R.anim.left_out);
                    }else {//如果为false : 1.提示创建快捷方式 2.进入主界面
                        //询问是否创建快捷方式
                        //SpUtils.getInstance(WelcomeActivity.this).getBoolean(SpUtils.ENTERMAIN,false);
                        SpUtils.getInstance(WelcomeActivity.this).save(SpUtils.ENTERMAIN,true);

                        makeShortCut();
                    }

                    break;

            }
        }
    };
    private UpdataInfo updataInfo;

    private void loadGuide() {



        Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
        startActivity(intent);
        finish();
        //切换动画
        //overridePendingTransition( R.anim.right_in, R.anim.left_out);
    }

    //创建快捷方式
    private void makeShortCut() {
        AlertDialog shortCutDialog = new AlertDialog.Builder(this)
                .setMessage("是否在桌面添加图标？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //通过发送广播使系统创建快捷方式
                        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                        //把图标传过去
                        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.icon));
                        //把应用名称传过去
                        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "微课APP");

                        //设置点击的意图
                        Intent clickIntent = new Intent("com.atguigu.ms.activity.MainActivity");
                        //把点击的意图点过去
                        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, clickIntent);
                        //发送这个广播
                        sendBroadcast(intent);

                        //进入引导界面
                        loadGuide();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //进入引导
                        loadGuide();
                    }
                })
                .show();




    }

    private File mApkFile;
    private long mCt;

    private void install() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(mApkFile), "application/vnd.android.package-archive");
        //安装时点取消则一直在欢迎界面，为了避免这点使用带回调的意图，
        // 系统的安装程序那边会有一个带结果的返回
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_CANCELED) {
            toMainUI();
        }
    }

    private ProgressDialog mPd;

    //弹出下载新版本的窗口
    private void showDownloadDialog() {
        new AlertDialog.Builder(this)
                .setTitle("下载最新版本")
                //.setView() 等美化UI的时候再写这里
                .setMessage(updataInfo.getDesc())
                .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startDownloadApk();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toMainUI();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void startDownloadApk() {
        mPd = new ProgressDialog(this);
        mPd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mPd.show();
        mApkFile = new File(getExternalFilesDir(null), "weike.apk");
        new Thread(){
            public void run(){
                try {
                    APIClient.download(mApkFile,mPd,updataInfo.getApkUrl());
                    handler.sendEmptyMessage(DOWN_SUCCESS);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(DOWN_FAIL);
                }

            }
        }.start();
    }


    private  HttpURLConnection mConn;
    private  InputStream mIs;

    private  String mUrlStirng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //一些初始化工作
        initial();
        //检查更新
        checkUpdate();
    }

    /**
     * 进行一些初始化工作
     */
    private void initial() {
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //隐藏顶部的系统状态栏，注意要在加载布局前设置
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);



        //获取一下进入该界面的开始时间，以便延时
        mCt = System.currentTimeMillis();

        //加载需要使用的控件
        rl_welcome_background = (RelativeLayout)findViewById(R.id.rl_welcome_background);
        tv_welcome_version = (TextView)findViewById(R.id.tv_welcome_version);

        //加载进入动画
        showAnimation();

        //获取当前版本号
        mVersion = getVersion();
        tv_welcome_version.setText("版本号:"+mVersion);
        //将assets下的静态数据库资源拷贝到files目录下
        //目前还不知道为什么要这样做
        //****目前还没有数据库，先把框架写好，传入具体数据库的任务待以后完成****
        copyAllDatabases();
    }

    private void showAnimation() {
        //拉伸动画
        ScaleAnimation sa = new ScaleAnimation(0.5f,1,0.5f,1, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        sa.setDuration(1000);
        sa.setFillAfter(true);//最终保持动画完成后的状态

        //渐变动画
        AlphaAnimation aa = new AlphaAnimation(0,1);
        aa.setDuration(1000);
        aa.setFillAfter(true);

        //动画集合
        AnimationSet as = new AnimationSet(true);
        as.addAnimation(sa);
        as.addAnimation(aa);
        rl_welcome_background.startAnimation(as);
    }

    //检查更新的方法
    private void checkUpdate() {
        //先检查是否联网
        //获取联网状态
        boolean isConnect = WkUtils.getIsConncet(this);

        //判断
        if(isConnect) {
        //联网获取版本号
            new Thread(){
                public void run(){
                    try {


                        final UpdataInfo[] updataInfo = new UpdataInfo[1];
                        // 初始化 Bmob SDK
                        // 第二个参数Application ID是我在Bmob服务器端创建的Application ID
                        Bmob.initialize(WelcomeActivity.this, Contants.BMOBID);

                        BmobQuery<FileInfo> query = new BmobQuery<FileInfo>();
                        query.getObject(WelcomeActivity.this, Contants.BMOB_JSON, new GetListener<FileInfo>() {
                            @Override
                            public void onSuccess(final FileInfo fileInfo) {
                                Log.e("eee", "onSuccess");
                                new Thread(){
                                    @Override
                                    public void run() {
                                        super.run();
                                        System.out.println(Thread.currentThread().getName());
                                        //通过Bmob的sdk获得服务器端Json文件的URL
                                        BmobFile file = fileInfo.getFile();
                                        Log.e("TAG", "BmobFile:" + file);
                                        mUrlStirng = file.getFileUrl(WelcomeActivity.this);
                                        Log.e("TAG", "onSuccess:" + mUrlStirng);

                                        try {
                                            URL url = new URL(mUrlStirng);
                                            mConn = (HttpURLConnection) url.openConnection();
                                            mConn.setConnectTimeout(5000);
                                            mConn.setReadTimeout(5000);
                                            mConn.connect();
                                            int code = mConn.getResponseCode();
                                            if (code == 200) {
                                                mIs = mConn.getInputStream();
                                                Gson gson = new Gson();
                                                //使用Gson把Json数据转化为Bean，并且发给handler
                                                updataInfo[0] = gson.fromJson(new InputStreamReader(mIs), UpdataInfo.class);
                                                Message message = Message.obtain();
                                                message.obj = updataInfo[0];
                                                message.what = CONN_SUCCESS;
                                                handler.sendMessage(message);
                                            } else {
                                                throw new RuntimeException();
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(WelcomeActivity.this, "检查更新失败，请检查网络！", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        } finally {
                                            if (mConn != null) {
                                                mConn.disconnect();
                                            }

                                            if (mIs != null) {
                                                try {
                                                    mIs.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                    }
                                }.start();



                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Toast.makeText(WelcomeActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.e("TAG", "updataInfo[0]:" + updataInfo[0]);

                    } catch (Exception e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(CONN_FAIL);
                    }

                }
            }.start();
        }else{
            Log.e("eee", "连接失败");
            Toast.makeText(WelcomeActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            //如果没有联网，则直接进入主界面
            toMainUI();
        }
    }

    //进入主界面的方法
    //1.要延时三秒
    //2.要有切换动画
    private void toMainUI() {
        long delayed = System.currentTimeMillis() - mCt;
        if(delayed > 3000) {
            delayed = 0;
        }else{
            delayed = 3000 - delayed;
        }
        handler.sendEmptyMessageDelayed(TO_MAIN_UI,delayed);
    }

    //拷贝数据库的方法
    private void copyAllDatabases() {
        //***这里使用copyDatabase()方法传入具体的数据库，待以后完成***
    }
    //拷贝数据库的具体实现
    private void  copyDatabase(String fileName){
        //准备工作
        //得到要传入的files内的文件地址
        File file = new File(getFilesDir(),fileName);
        //判断是否已经导入过数据库
        if(!file.exists() && file.length()>0) {
            return;
        }

        InputStream is = null;
        OutputStream os = null;
        //获取Assets的管理器
        AssetManager manager = getAssets();
        try {//获取输入流
             is = manager.open(fileName);
            os = new FileOutputStream(file);
            byte[] buff = new byte[1024];
            int len;
            while((len = is.read(buff)) != -1) {
                os.write(buff,0,len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //获取当前的版本号(通过包管理者)
    private String getVersion() {
        //获取包管理者
        PackageManager pm = this.getPackageManager();
        String versionName = "未知";
        try {
            //填0是获取包基本信息
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }


}
