package com.rongseal.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by AMing on 15/11/2.
 * Company RongCloud
 */
public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    private View mContentView = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = onCreateFragmentView(inflater, container, savedInstanceState);
        return mContentView;
    }

    /**
     * 创建view方法，子类必须重写
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public abstract View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void initViews(){

    }
}
