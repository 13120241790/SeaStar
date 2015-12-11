package com.rongseal.activity;

import android.content.Intent;
import android.os.Bundle;

import com.rongseal.R;

/**
 * Created by AMing on 15/11/11.
 * Company RongCloud
 * 聚合会话必须配置此类
 */
public class SubConversationListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rp_subconversation_activity);
        Intent intent = getIntent();
        String type = intent.getData().getQueryParameter("type");
        if (type == null) {
            return;
        }
        if (type.equals("group")) {
            setTitle(R.string.group);
        } else if (type.equals("private")) {
            setTitle(R.string.private_conversation);
        } else if (type.equals("discussion")) {
            setTitle(R.string.my_disc);
        } else if (type.equals("system")) {
            setTitle(R.string.system_message);
        } else {
            setTitle(R.string.chat);
        }

    }
}
