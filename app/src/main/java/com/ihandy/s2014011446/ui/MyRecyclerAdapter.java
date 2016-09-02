package com.ihandy.s2014011446.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihandy.s2014011446.R;
import com.ihandy.s2014011446.bean.NewsItem;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>{

    //当前显示的数据
    private List<NewsItem> mNewsList = new ArrayList<NewsItem>();

    private Drawable mDrawble;
//    private Context mContext;


    public MyRecyclerAdapter(Context context){
        this(context,null);
    }


    public MyRecyclerAdapter(Context context, List<NewsItem> myDataset){
        mNewsList = myDataset != null ? myDataset : new ArrayList<NewsItem>();
    }

    public List<NewsItem> getmNewsList() {
        return mNewsList;
    }
    public void addNews(List<NewsItem> news){
        mNewsList.addAll(news);
        Log.i("LIXU", "adapter" + mNewsList.size());
    }


    /**
     * 创建ViewHolder
     * @param viewGroup 父View
     * @param i 位置
     * @return 返回得到的ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview,
                viewGroup,false);
        TextView titleTextView = (TextView) v.findViewById(R.id.titleTextView);
        TextView sourceTextView = (TextView) v.findViewById(R.id.sourceTextView);
        ImageView titleImageView = (ImageView) v.findViewById(R.id.titleImageView);
        return new ViewHolder(v,titleTextView,sourceTextView,titleImageView);
    }

    /**
     * 将数据绑定到ViewHolder
     * @param viewHolder    要绑定的ViewHolder
     * @param i             ViewHolder的位置
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
//        viewHolder.mTitleTextView.setText(mNewsList.get(i).getTitle());
//        viewHolder.mSourceTextView.setText(mNewsList.get(i).getOrigin());
//        viewHolder.mTitleImageView.setImageBitmap(mNewsList.get(i).getBitmap());
        viewHolder.bindData(mNewsList.get(i));
    }

    @Override
    public int getItemCount() {
        if (mNewsList != null) {
            return mNewsList.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private View mView;
        private TextView mTitleTextView;
        private TextView mSourceTextView;
        private ImageView mTitleImageView;

        private NewsItem mNewsItem;

        public ViewHolder(View v){
            super(v);
        }

        public ViewHolder(View v,TextView titleTextView,TextView sourceTextView,ImageView imageView){
            this(v);
            v.setOnClickListener(this);
            mTitleTextView = titleTextView;
            mSourceTextView = sourceTextView;
            mTitleImageView = imageView;
            mView = v;
        }


        /**
         * 将新闻列表绑定至ViewHolder
         * @param newsItem     新闻列表
         */
        public void bindData(NewsItem newsItem){
            mTitleTextView.setText(newsItem.getTitle());
            mSourceTextView.setText(newsItem.getOrigin());
           // mTitleImageView.setImageBitmap(newsItem.getBitmap());
            mNewsItem = newsItem;
        }

        @Override
        public void onClick(View view) {
        }
    }
}