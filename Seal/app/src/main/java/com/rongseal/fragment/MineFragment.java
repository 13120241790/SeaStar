package com.rongseal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.rongseal.R;
import com.rongseal.activity.ContactActivity;
import com.rongseal.activity.DetailActivity;
import com.rongseal.widget.pulltorefresh.PullToRefreshBase;

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

    private LinearLayout LSetting , LAbout , LContact , LCustomerService;

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
        LSetting = (LinearLayout) fragmentView.findViewById(R.id.mine_setting);
        LContact = (LinearLayout) fragmentView.findViewById(R.id.mine_contact);
        LCustomerService = (LinearLayout) fragmentView.findViewById(R.id.customer_service);
        LAbout = (LinearLayout) fragmentView.findViewById(R.id.mine_about);
        LAbout.setOnClickListener(this);
        LCustomerService.setOnClickListener(this);
        LContact.setOnClickListener(this);
        LSetting.setOnClickListener(this);
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
            case R.id.mine_about://关于

                break;
            case R.id.mine_contact://社交
                startActivity(new Intent(getActivity(),ContactActivity.class));
                break;
            case R.id.mine_setting://设置

                break;
            case R.id.customer_service://客服

                break;
        }
    }
}
