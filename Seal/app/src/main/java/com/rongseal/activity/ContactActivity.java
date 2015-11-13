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

    private LinearLayout SearchFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("社交");
        setContentView(R.layout.sr_contact_activity);
        initView();
    }

    private void initView() {
        SearchFriend = (LinearLayout) findViewById(R.id.contact_search);
        SearchFriend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_search :
                startActivity(new Intent(mContext,SearchFriendActivity.class));
                break;
        }
    }
}
