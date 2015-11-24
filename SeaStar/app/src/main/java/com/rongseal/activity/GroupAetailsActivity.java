package com.rongseal.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.rongseal.R;

/**
 * Created by AMing on 15/11/24.
 * Company RongCloud
 */
public class GroupAetailsActivity extends BaseActivity{

    private String groupId;
    private TextView mGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("群组详情");
        setContentView(R.layout.group_aetails_activity);
        mGroupId = (TextView) findViewById(R.id.groupid);
        groupId = getIntent().getStringExtra("groupId");
        mGroupId.setText(groupId);
    }
}
