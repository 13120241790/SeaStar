package com.rongseal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.rongseal.R;

import java.util.Locale;

import de.greenrobot.event.EventBus;
import io.rong.imlib.model.Conversation.ConversationType;

import static io.rong.imlib.model.Conversation.ConversationType.*;

/**
 * Created by AMing on 15/11/2.
 * Company RongCloud
 */
public class ConversationActivity extends BaseActivity {

    private static final int GROUPDETIALS = 1265;
    private Button mRightBtn;

    private ConversationType type;
    private String targetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rp_conversation_activity);
        targetId = getIntent().getData().getQueryParameter("targetId"); //获取id
        String name = getIntent().getData().getQueryParameter("title"); //获取昵称
        if (!TextUtils.isEmpty(name)) {
            setTitle(name);
        } else {
            setTitle(targetId);
        }
        //获取会话类型
        type = valueOf(getIntent().getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
        initView();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        mRightBtn = getBtn_right();
        mRightBtn.setVisibility(View.VISIBLE);
        switch (type) {
            case GROUP:
                mRightBtn.setText("群组详情");
                mRightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ConversationActivity.this, GroupAetailsActivity.class);
                        intent.putExtra("groupId", targetId);
                        startActivityForResult(intent, GROUPDETIALS);
                    }
                });
                break;
            case PRIVATE:
                mRightBtn.setText("用户详情");
                break;
        }
    }


    public void onEventMainThread(GroupAetailsActivity.FinishActivity fa) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
