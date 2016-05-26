package com.chenluwei.beijingnews.menudetail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.chenluwei.beijingnews.R;

/**
 * Created by lw on 2016/5/26.
 */
public class NewsDetailActivity extends Activity {
    /**
     * 加载网页，网页如果有视频和图片都能播放或者显示，支持html5和html
     */
    private WebView webview;
    private String url;
    private WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        findViewById(R.id.tv_title).setVisibility(View.GONE);
        findViewById(R.id.ib_menu).setVisibility(View.GONE);
        findViewById(R.id.ib_back).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_textsize).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_shared).setVisibility(View.VISIBLE);
        webview = (WebView)findViewById(R.id.webview);


        //设置监听事件
        MyOnClickListener myOnClickListener = new MyOnClickListener();
        findViewById(R.id.ib_back).setOnClickListener(myOnClickListener);
        findViewById(R.id.ib_textsize).setOnClickListener(myOnClickListener);
        findViewById(R.id.ib_shared).setOnClickListener(myOnClickListener);

        //加载网络地址，把网页加载进来
        //获取网络地址
        url = getIntent().getStringExtra("url");
        webSettings = webview.getSettings();
        //设置为支持JS
        webSettings.setJavaScriptEnabled(true);
        //添加缩放按钮
        webSettings.setBuiltInZoomControls(true);
        //默认设置字体大小为normal
        webSettings.setTextSize(WebSettings.TextSize.NORMAL);
        //设置双击变大变小
        webSettings.setUseWideViewPort(true);
        webview.loadUrl(url);
        //默认的会调外部浏览器
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Toast.makeText(NewsDetailActivity.this, "网页加载完成", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_back :
                    finish();
                    break;
                case R.id.ib_textsize :
                    Toast.makeText(NewsDetailActivity.this, "设置文字大小", Toast.LENGTH_SHORT).show();
                    showChangeTextSizeDialog();
                    break;
                case R.id.ib_shared :
                    Toast.makeText(NewsDetailActivity.this, "分享内容", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
        //字体缓存
    private int tempSize = 2;
    //真正的字体大小
    private int realSize = 2;
    private void showChangeTextSizeDialog() {
        String []items = {"超大字体","大号字体","正常字体","小字体","超小字体"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置文字大小");
        builder.setSingleChoiceItems(items, realSize, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tempSize = which;
            }
        });
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realSize = tempSize;
                //改变字体大小
                changeTextSize(realSize);
            }
        });
        builder.show();
    }

    /**
     * 设置字体大小
     * @param realSize
     */
    private void changeTextSize(int realSize) {
        switch (realSize){
            case 0:
               // webSettings.setTextSize(WebSettings.TextSize.LARGEST);
                webSettings.setTextZoom(200);
                break;
            case 1:
               // webSettings.setTextSize(WebSettings.TextSize.LARGER);
                webSettings.setTextZoom(150);
                break;
            case 2:
//                webSettings.setTextSize(WebSettings.TextSize.NORMAL);
                webSettings.setTextZoom(100);
                break;

            case 3:
//                webSettings.setTextSize(WebSettings.TextSize.SMALLER);
                webSettings.setTextZoom(75);
                break;
            case 4:
//                webSettings.setTextSize(WebSettings.TextSize.SMALLEST);
                webSettings.setTextZoom(50);
                break;
        }

    }


}
