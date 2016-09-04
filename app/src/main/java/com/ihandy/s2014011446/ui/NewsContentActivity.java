package com.ihandy.s2014011446.ui;

import android.app.Activity;
import android.content.Intent;
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
import com.ihandy.s2014011446.bean.FavoriteItem;
import com.ihandy.s2014011446.bean.NewsItem;
import com.ihandy.s2014011446.biz.NewsItemBiz;
import com.ihandy.s2014011446.dao.FavoriteItemDao;
import com.ihandy.s2014011446.ui.widget.GestureFrameLayout;
import com.ihandy.s2014011446.ui.widget.ObservableScrollView;
import com.ihandy.s2014011446.utils.HttpUtils;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import java.sql.SQLException;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class NewsContentActivity extends BaseActivity {

    private int mActionBarSize;

    private int mToolbarColor;

    private GestureFrameLayout gestureFrameLayout;  //滑动返回
    private WebView mWebView;
    private FavoriteItemDao mFavoriteItemDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);

        init();
        //通过bundle获取文章内容的url
        String mNewsContentUrl = this.getIntent().getBundleExtra("key").getString("url");
        //mNewsContentUrl = "https://www.baidu.com";
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

        mFavoriteItemDao = new FavoriteItemDao(NewsContentActivity.this);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_18dp));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsContentActivity.this.finish();
                String caller = NewsContentActivity.this.getIntent().getBundleExtra("key").getString("caller");
                if(caller != null && caller.equals("FavoriteActivity")) {
                    Intent intent = new Intent(NewsContentActivity.this, FavoriteActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//刷新
                    startActivity(intent);
                }
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
        String newsId = this.getIntent().getBundleExtra("key").getString("news_id");
        try {
            if(mFavoriteItemDao.searchIsExistByNewsId(newsId))
                getMenuInflater().inflate(R.menu.menu_news_content_favorite, menu);
            else
                getMenuInflater().inflate(R.menu.menu_news_content, menu);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            String origin =  this.getIntent().getBundleExtra("key").getString("origin");
            String imageUrl =  this.getIntent().getBundleExtra("key").getString("imageUrl");
            if(imageUrl == null)
                imageUrl = "http://icons.iconarchive.com/icons/designbolts/free-multimedia/1024/News-Mic-iPhone-icon.png";
            //showShare(this, title + " 详见：" + url + " \n分享自HHNews");
            showShare(this, origin , title, null, imageUrl, url);
            return true;
        }
        else if(id == R.id.action_favorite) {
            if(item.getTitle().equals("common")) { //不在收藏列表里面
                item.setTitle("favorite");
                item.setIcon(R.drawable.ic_favorite_red);
                String newsId = this.getIntent().getBundleExtra("key").getString("news_id");
                String title = this.getIntent().getBundleExtra("key").getString("title");
                String origin = this.getIntent().getBundleExtra("key").getString("origin");
                String sourceUrl =  this.getIntent().getBundleExtra("key").getString("url");
                FavoriteItem favoriteItem = new FavoriteItem();
                favoriteItem.setNewsId(newsId);
                favoriteItem.setTitle(title);
                favoriteItem.setOrigin(origin);
                favoriteItem.setSourceUrl(sourceUrl);
                mFavoriteItemDao.createOrUpdate(favoriteItem);
            }
            else if(item.getTitle().equals("favorite")) { //在收藏列表里面
                item.setTitle("common");
                item.setIcon(R.drawable.ic_favorite_white);
                String newsId = this.getIntent().getBundleExtra("key").getString("news_id");
                try {
                    mFavoriteItemDao.deleteByNewsId(newsId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}