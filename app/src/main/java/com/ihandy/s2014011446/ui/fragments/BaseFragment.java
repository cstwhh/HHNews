package com.ihandy.s2014011446.ui.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ihandy.s2014011446.biz.NewsItemBiz;
import com.ihandy.s2014011446.biz.NewsTypeBiz;


public class BaseFragment extends Fragment {
    protected NewsItemBiz mNewsItemBiz;
//    protected NewsTypeBiz mNewsTypeBiz;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewsItemBiz = new NewsItemBiz(getActivity());
//        mNewsItemBiz = new NewsItemBiz(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mNewsItemBiz != null){
        }
    }
}
