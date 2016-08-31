package com.ihandy.s2014011446.ui;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconToolbar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ihandy.s2014011446.R;
import com.ihandy.s2014011446.bean.NewsItem;
import com.ihandy.s2014011446.bean.NewsType;
import com.ihandy.s2014011446.biz.NewsItemBiz;
import com.ihandy.s2014011446.biz.NewsTypeBiz;
import com.ihandy.s2014011446.common.NewsTypes;
import com.ihandy.s2014011446.ui.fragments.NewsListFragment;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.ihandy.s2014011446.utils.HttpUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.*;


public class MainActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    private ViewPager mViewPager;
    //新闻列表
    private List<NewsListFragment> mFragmentList;

    private ViewGroup mMainPage;
    private DrawerLayout mDrawerLayout;
    private ViewGroup mDrawer;

    //设置按钮区域
    private View mAppSetting;
    private View mAboutButton;
    private View mShareButton;
    private View mFeedBackButton;

    private MaterialMenuIconToolbar mMaterialMenu;

    PagerSlidingTabStrip mTabs;

    private ViewGroup mContent;

    //缓存
    private List<NewsType> mNewsTypes = new ArrayList<NewsType>();

    //侧边栏头部图片
    private ImageView mHeaderImage;

    //标识是否点击过一次back退出
    private boolean mIsExit = false;
    //点击返回键时，延时 TIME_TO_EXIT 毫秒发送此handler重置mIsExit，再其被重置前如果再按一次返回键则退出应用
    private Handler mExitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mIsExit = false;
        }
    };
    final static int TIME_TO_EXIT = 2000;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViews();

        initViewPager();

        UmengUpdateAgent.update(this);  //检查更新
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.main_activity_title));
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        mMaterialMenu = new MaterialMenuIconToolbar(this, Color.BLACK, MaterialMenuDrawable.Stroke.THIN) {
            @Override
            public int getToolbarViewId() {
                return R.id.toolbar;
            }
        };
        mMaterialMenu.setNeverDrawTouch(true);

        mTintManager.setStatusBarTintEnabled(false);

        //设置侧滑菜单的监听
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {
                mMaterialMenu.setTransformationOffset(MaterialMenuDrawable.AnimationState.BURGER_ARROW,
                        v);
            }

            @Override
            public void onDrawerOpened(View view) {
                mMaterialMenu.animatePressedState(intToState(1));
            }

            @Override
            public void onDrawerClosed(View view) {
                mMaterialMenu.animatePressedState(intToState(0));
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });


        mContent = (ViewGroup) findViewById(R.id.content);
        mDrawer = (ViewGroup) findViewById(R.id.drawer);
        mMainPage = (ViewGroup) findViewById(R.id.main_page);
        //因为导航栏透明，要让出顶部和底部空间
        if (isNavBarTransparent()) {
            mMainPage.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight());
            mDrawer.setPadding(0, 0, 0, getNavigationBarHeight());
        }

        //侧边栏
        mHeaderImage = (ImageView) findViewById(R.id.header_img);
        mHeaderImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //resume the click
            }
        });
        //设置
        mAppSetting = (ViewGroup) findViewById(R.id.bottom_drawer);
        mAppSetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        //关于
        mAboutButton = findViewById(R.id.drawer_item_about);
        mAboutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
        //分享
        mShareButton = findViewById(R.id.drawer_item_share);
        mShareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showShare(MainActivity.this, MainActivity.this.getResources().getString(R.string.share_app_string));
            }
        });

    }

    private void initViewPager() {

        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mFragmentList = new ArrayList<NewsListFragment>();

        //初始化fragment
//        NewsListFragment fragment1 = NewsListFragment.newInstance(NewsTypes.NEWS_TPYE_XXYW);
//        NewsListFragment fragment2 = NewsListFragment.newInstance(NewsTypes.NEWS_TPYE_XYKX);
//        NewsListFragment fragment3 = NewsListFragment.newInstance(NewsTypes.NEWS_TPYE_KJDT);
//        NewsListFragment fragment4 = NewsListFragment.newInstance(NewsTypes.NEWS_TPYE_MTJJ);
//        NewsListFragment fragment5 = NewsListFragment.newInstance(NewsTypes.NEWS_TPYE_BMXW);
//
//        mFragmentList.add(fragment1);
//        mFragmentList.add(fragment2);
//        mFragmentList.add(fragment3);
//        mFragmentList.add(fragment4);
//        mFragmentList.add(fragment5);

        getFragmenList(mFragmentList, false);
        //初始化ViewPager

    }

    private void getFragmenList(List<NewsListFragment> fragmentList, boolean forced) {
        int total = mNewsTypes.size();
        //不强制刷新时，如果此页已存在则直接从内存中加载
//        TODO
//        if (!forced && total > 0) {
//            mAdapter.addNews(mNewsTypes);
//            mAdapter.notifyDataSetChanged();
//            return;
//        }
//
//        if (forced && mNewsTypes.size() > 0) {
//            mNewsTypes.clear();
//        }
        LoadNewsTypesTask loadDataTask = new LoadNewsTypesTask(fragmentList, forced);
        loadDataTask.execute(0);
    }

    /**
     * 加载新闻列表的任务
     */
    class LoadNewsTypesTask extends AsyncTask<Integer, Integer, List<NewsType>> {

        private List<NewsListFragment> mFragmentList;
        private boolean mIsForced;

        public LoadNewsTypesTask(List<NewsListFragment> fragmentList, boolean forced) {
            super();
            mFragmentList = fragmentList;
            mIsForced = forced;
        }

        /**
         * 得到当前页码的新闻列表
         *
         * @param currentPage 当前页码
         * @return 当前页码的新闻列表, 出错返回null
         */
        @Override
        protected List<NewsType> doInBackground(Integer... currentPage) {

            try {
                //TODO
//                boolean netAvailable = HttpUtils.IsNetAvailable(getActivity());
//                //如果当前是第一次加载，则直接从数据库读取
//                if (netAvailable && mIsFirstLoad){
//                    mIsFirstLoad = false;
//                    mNewsTypeBiz.getNewsTypes(true);
//                    return mNewsItemBiz.getNewsItemCache(mNewsType, currentPage[0], true);
//                }
//                return mNewsItemBiz.getNewsItems(mNewsType, currentPage[0],netAvailable);
                NewsTypeBiz mNewsTypeBiz = new NewsTypeBiz(MainActivity.this);
                return mNewsTypeBiz.getNewsTypes(true);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("ASDNET", "neterror :" + e);
                return null;
            }

        }

        /**
         * 得到新闻列表后将其加载
         *
         * @param newsTypes 得到的新闻列表
         */
        @Override
        protected void onPostExecute(List<NewsType> newsTypes) {
            if (newsTypes == null) {
                Toast.makeText(MainActivity.this, "获取type失败"
                        , Toast.LENGTH_LONG).show();
                return;
            }
            //处理强制刷新
//            TODO
//            if (mIsForced) {
//                mAdapter.getmNewsList().clear();
//            }
            mNewsTypes.addAll(newsTypes);
//            mAdapter.addNews(newsItems);
//            mAdapter.notifyDataSetChanged();
//            frame.refreshComplete();
            int i = 0;
            for(NewsType newsType : newsTypes) {
                if(i == 4)  break;
                ++i;
                Log.i(getClass().getName(), "UrlType: " + newsType.getUrlType());
                if(newsType.getUrlType() == "business") {
                    NewsListFragment fragment = NewsListFragment.newInstance(NewsTypes.NEWS_TPYE_XXYW);
                    mFragmentList.add(fragment);
                }else if(newsType.getUrlType() == "health") {
                    NewsListFragment fragment = NewsListFragment.newInstance(NewsTypes.NEWS_TPYE_XYKX);
                    mFragmentList.add(fragment);
                }else {
                    NewsListFragment fragment = NewsListFragment.newInstance(NewsTypes.NEWS_TPYE_BMXW);
                    mFragmentList.add(fragment);
                }
            }

            MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager(), mFragmentList);
            mViewPager.setAdapter(adapter);
            mViewPager.setCurrentItem(0);

            mTabs.setViewPager(mViewPager);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    /**
     * 根据滑动方向设置ToolBar的显隐
     *
     * @param scrollState 滑动方向
     */
    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState == ScrollState.UP) {
            if (toolbarIsShown()) {
                hideToolbar();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (toolbarIsHidden()) {
                showToolbar();
            }
        }
    }


    private boolean toolbarIsShown() {
        return mToolbar.getTranslationY() == 0;
    }

    private boolean toolbarIsHidden() {
        return mToolbar.getTranslationY() == -mToolbar.getHeight();
    }

    private void showToolbar() {
        moveToolbar(0);
    }


    private void hideToolbar() {
        moveToolbar(-mToolbar.getHeight());
    }


    /**
     * 将toolbar移动到某个位置
     *
     * @param toTranslationY 移动到的Y轴位置
     */
    private void moveToolbar(float toTranslationY) {
        if (mToolbar.getTranslationY() == toTranslationY) {
            return;
        }
        //利用动画过渡移动的过程
        final ValueAnimator animator = ValueAnimator.ofFloat(mToolbar.getTranslationY(), toTranslationY).
                setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float translationY = (Float) animator.getAnimatedValue();
                mToolbar.setTranslationY(translationY);
                mContent.setTranslationY(translationY);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContent.getLayoutParams();
                lp.height = (int) (getScreenHeight() - translationY - getStatusBarHeight()
                        - lp.topMargin);
                if (CURRENT_VERSION >= VERSION_KITKAT && VERSION_LOLLIPOP > CURRENT_VERSION) {
                    lp.height -= getNavigationBarHeight();
                }
                Log.i("TEST", "after" + Float.toString(mToolbar.getHeight()));
                mContent.requestLayout();
            }
        });
        animator.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 实现点击两次退出程序
     */
    private void exit() {
        if (mIsExit) {
            finish();
            System.exit(0);
        } else {
            mIsExit = true;
            Toast.makeText(getApplicationContext(), R.string.click_to_exit, Toast.LENGTH_SHORT).show();
            //两秒内不点击back则重置mIsExit
            mExitHandler.sendEmptyMessageDelayed(0, TIME_TO_EXIT);
        }
    }
}
