package com.rongseal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.rongseal.R;


/**
 * Created by AMing on 15/11/12.
 * Company RongCloud
 */
public class ContactActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout SearchFriend, SearchGroup, MyGroup, MyBlackList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.contact);
        setContentView(R.layout.sr_contact_activity);
        initView();
    }

    private void initView() {
        SearchFriend = (LinearLayout) findViewById(R.id.contact_search);
        SearchGroup = (LinearLayout) findViewById(R.id.search_group);
        MyGroup = (LinearLayout) findViewById(R.id.my_group);
        MyBlackList = (LinearLayout) findViewById(R.id.my_blacklist);
        SearchGroup.setOnClickListener(this);
        SearchFriend.setOnClickListener(this);
        MyGroup.setOnClickListener(this);
        MyBlackList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_search:
                startActivity(new Intent(mContext, SearchFriendActivity.class));
                break;
            case R.id.search_group:
                startActivity(new Intent(mContext, AllGroupActivity.class));
                break;
            case R.id.my_group:
                startActivity(new Intent(mContext, MyGroupActivity.class));
                break;
            case R.id.my_blacklist:
                startActivity(new Intent(mContext, BlackListActivity.class));
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
