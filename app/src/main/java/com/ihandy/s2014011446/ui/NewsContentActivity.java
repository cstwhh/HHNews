package com.ihandy.s2014011446.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconToolbar;
import com.ihandy.s2014011446.R;
import com.ihandy.s2014011446.bean.NewsItem;
import com.ihandy.s2014011446.biz.NewsItemBiz;
import com.ihandy.s2014011446.ui.widget.GestureFrameLayout;
import com.ihandy.s2014011446.ui.widget.ObservableScrollView;
import com.ihandy.s2014011446.utils.HttpUtils;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class NewsContentActivity extends BaseActivity {

    private int mActionBarSize;

    private int mToolbarColor;

    private GestureFrameLayout gestureFrameLayout;  //滑动返回
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);

        init();
        //通过bundle获取文章内容的url
        String mNewsContentUrl = this.getIntent().getBundleExtra("key").getString("url");
        mNewsContentUrl = "https://www.baidu.com";
        mWebView.loadUrl(mNewsContentUrl);
    }

    private void init() {
        mWebView = (WebView) findViewById(R.id.content_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultFontSize(18);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //设置点击网页里面的链接还是在当前的webview里跳转
                view.loadUrl(url);
                return true;
            }
        });


        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_18dp));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsContentActivity.this.finish();
            }
        });

        mToolbarColor = getResources().getColor(R.color.primary_color);

        mActionBarSize = getActionBarSize();

        gestureFrameLayout = (GestureFrameLayout) findViewById(R.id.news_content_gesture_layout);
        gestureFrameLayout.attachToActivity(this);

        //因为顶栏透明，要让出顶栏和底栏空间
        if (isNavBarTransparent()) {
            gestureFrameLayout.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();//返回webView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            String title = this.getIntent().getBundleExtra("key").getString("title");
            String url =  this.getIntent().getBundleExtra("key").getString("url");
            showShare(this, title + " 详见：" + url + " \n分享自HHNews");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}