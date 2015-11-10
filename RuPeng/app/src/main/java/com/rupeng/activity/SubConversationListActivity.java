package com.rupeng.activity;

import android.content.Intent;
import android.os.Bundle;

import com.rupeng.R;

/**
 * Created by AMing on 15/11/11.
 * Company RongCloud
 * 聚合会话必须配置此类
 */
public class SubConversationListActivity extends BaseActivity{

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
            setTitle("群组");
        } else if (type.equals("private")) {
            setTitle("我的私人会话");
        } else if (type.equals("discussion")) {
            setTitle("我的讨论组");
        } else if (type.equals("system")) {
            setTitle("系统消息");
        } else {
            setTitle("聊天");
        }

    }
}
