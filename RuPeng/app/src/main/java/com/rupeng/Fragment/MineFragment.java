package com.rupeng.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rupeng.R;
import com.rupeng.activity.DetailActivity;
import com.rupeng.widget.pulltorefresh.PullToRefreshBase;

/**
 * Created by AMing on 15/11/2.
 * Company RongCloud
 */
public class MineFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener, View.OnClickListener {

    public static final int REFRESH_CODE = 354;

    private LinearLayout layout_user;

    public static MineFragment instance = null;

    public static MineFragment getInstance() {
        if (instance == null) {
            instance = new MineFragment();
        }
        return instance;
    }


    private View fragmentView;
    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.rp_mine_fragment,null);
        return fragmentView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    public void initViews(){
        //TODO findviewbyid
        layout_user = (LinearLayout) fragmentView.findViewById(R.id.layout_user);
        layout_user.setOnClickListener(this);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_user:
                //TODO 此处需要判断是否登录 如果登录 跳转 DetailActivity  如果没登录 跳转login 此处代码未完
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                startActivityForResult(intent, REFRESH_CODE);
                break;
        }
    }
}
