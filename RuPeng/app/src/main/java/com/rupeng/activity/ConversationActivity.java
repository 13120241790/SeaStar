package com.rupeng.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rupeng.R;

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
