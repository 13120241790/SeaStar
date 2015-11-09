package com.rupeng.activity;

import android.os.Bundle;

import com.rupeng.R;
import com.rupeng.widget.pulltorefresh.PullToRefreshBase;


/**
 * Created by AMing on 15/11/6.
 * Company RongCloud
 */
public class DetailActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rp_detail_activity);
        getBtn_left().setText("我的");
        initView();
    }

    private void initView() {

    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        
    }
}
