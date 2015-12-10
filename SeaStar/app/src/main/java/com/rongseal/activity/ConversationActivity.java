package com.rongseal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.rongseal.R;
import com.rongseal.RongCloudEvent;
import com.rongseal.widget.dialog.LoadDialog;
import com.sd.core.utils.NLog;

import java.util.Locale;

import de.greenrobot.event.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation.ConversationType;

import static io.rong.imlib.model.Conversation.ConversationType.valueOf;

/**
 * Created by AMing on 15/11/2.
 * Company RongCloud
 */
public class ConversationActivity extends BaseActivity {

    private static final int GROUPDETIALS = 1265;
    private Button mRightBtn;
    private ConversationType type;
    private String targetId;

    private RelativeLayout rlVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rp_conversation_activity);

        Intent intent = getIntent();
        if (intent == null || intent.getData() == null)
            return;

        isPushMessage(intent);
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
            case DISCUSSION:
                mRightBtn.setText("讨论组详情");
                mRightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ConversationActivity.this, DiscussionAetailsActivity.class);
                        intent.putExtra("discussionId", targetId);
                        startActivity(intent);
                    }
                });
                break;
        }
    }



    /**
     * 判断是否是 Push 消息，判断是否需要做 connect 操作
     *
     * @param intent
     */
    private void isPushMessage(Intent intent) {
        NLog.e("isPushMessage");
        //push
        if (intent.getData().getScheme().equals("rong") && intent.getData().getQueryParameter("push") != null) {
            NLog.e("isPushMessage2");
            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("push").equals("true")) {
                String id = intent.getData().getQueryParameter("pushId");
                RongIM.getInstance().getRongIMClient().recordNotificationEvent(id);
                LoadDialog.show(mContext);
                NLog.e("isPushMessage3");
                enterActivity();
            }

        } else {//通知过来
            //程序切到后台，收到消息后点击进入,会执行这里
            if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null) {
                LoadDialog.show(mContext);
                enterActivity();
            }
        }
    }

    /**
     * 收到 push 消息后，选择进入哪个 Activity
     * 如果程序缓存未被清理，进入 MainActivity
     * 程序缓存被清理，进入 LoginActivity，重新获取token
     * <p/>
     * 作用：由于在 manifest 中 intent-filter 是配置在 ConversationActivity 下面，所以收到消息后点击notifacition 会跳转到 DemoActivity。
     * 以跳到 MainActivity 为例：
     * 在 ConversationActivity 收到消息后，选择进入 MainActivity，这样就把 MainActivity 激活了，当你读完收到的消息点击 返回键 时，程序会退到
     * MainActivity 页面，而不是直接退回到 桌面。
     */
    private void enterActivity() {
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        String tokenCache = sp.getString("token", "");
        if (!TextUtils.isEmpty(tokenCache)) {
            startActivity(new Intent(ConversationActivity.this, LoginActivity.class));
            finish();
        } else {
            RongIM.connect(tokenCache, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {

                }

                @Override
                public void onSuccess(String s) {
                    if (RongCloudEvent.getInstance() != null)
                        RongCloudEvent.getInstance().setConnectedListener();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }


    public void onEventMainThread(GroupAetailsActivity.FinishActivity fa) {
        finish();
    }

    public void onEventMainThread(UserDetailActivity.DeleteFriend df) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
