package com.rongseal.activity;

import android.os.Bundle;

import com.rongseal.R;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Discussion;

/**
 * Created by AMing on 15/12/10.
 * Company RongCloud
 */
public class DiscussionAetailsActivity extends BaseActivity {

    /**
     * removeMemberFromDiscussion(java.lang.String discussionId, java.lang.String userId, RongIMClient.OperationCallback callback)
     * 供创建者将某用户移出讨论组
     * <p/>
     * <p/>
     * <p/>
     * setDiscussionInviteStatus(java.lang.String discussionId, RongIMClient.DiscussionInviteStatus status, RongIMClient.OperationCallback callback)
     * 设置讨论组成员邀请权限。
     * <p/>
     * <p/>
     * <p/>
     * setDiscussionName(java.lang.String discussionId, java.lang.String name, RongIMClient.OperationCallback callback)
     * 设置讨论组名称。
     * <p/>
     * <p/>
     * <p/>
     * addMemberToDiscussion(java.lang.String discussionId, java.util.List<java.lang.String> userIdList, RongIMClient.OperationCallback callback)
     * 添加一名或者一组用户加入讨论组。
     * <p/>
     * <p/>
     * quitDiscussion(java.lang.String discussionId, RongIMClient.OperationCallback callback)
     * 退出当前用户所在的某讨论组。
     **/

    private String discussionId;

    private Discussion mDiscussion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ss_discussionaetail_activity);
        setTitle(R.string.disc_aetails);
        discussionId = getIntent().getStringExtra("discussionId");
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().getRongIMClient().getDiscussion(discussionId, new RongIMClient.ResultCallback<Discussion>() {
                @Override
                public void onSuccess(Discussion discussion) {
                    mDiscussion = discussion;
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }
}
