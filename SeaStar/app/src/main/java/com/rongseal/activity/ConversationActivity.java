package com.rongseal.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rongseal.R;

import java.util.Locale;

import io.rong.imlib.model.Conversation;

/**
 * Created by AMing on 15/11/2.
 * Company RongCloud
 */
public class ConversationActivity extends BaseActivity{

    private Button mRightBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rp_conversation_activity);
        String id = getIntent().getData().getQueryParameter("targetId"); //获取id
        String name = getIntent().getData().getQueryParameter("title"); //获取昵称
        if (!TextUtils.isEmpty(name)) {
            setTitle(name);
        }else {
            setTitle(id);
        }
        //获取会话类型
//        Conversation.ConversationType.valueOf(getIntent().getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
        initView();
    }

    private void initView() {
        mRightBtn = getBtn_right();
        mRightBtn.setVisibility(View.VISIBLE);
        mRightBtn.setText("详情");
        mRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 个人详情页面
                Toast.makeText(ConversationActivity.this, "详情页", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
