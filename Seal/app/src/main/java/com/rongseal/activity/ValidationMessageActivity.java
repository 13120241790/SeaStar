package com.rongseal.activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.rongseal.R;
import com.rongseal.RongCloudEvent;
import com.rongseal.adapter.NewFriendsAdapter;
import com.rongseal.bean.response.FeedBackFriendRequestResponse;
import com.rongseal.bean.response.NewFriendsListResponse;
import com.rongseal.db.com.rongseal.database.DBManager;
import com.rongseal.db.com.rongseal.database.Friend;
import com.rongseal.message.AgreedFriendRequestMessage;
import com.rongseal.widget.dialog.LoadDialog;
import com.rongseal.widget.pulltorefresh.PullToRefreshBase;
import com.rongseal.widget.pulltorefresh.PullToRefreshListView;
import com.sd.core.common.broadcast.BroadcastManager;
import com.sd.core.network.http.HttpException;
import com.sd.core.utils.NToast;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 15/11/14.
 * Company RongCloud
 */
public class ValidationMessageActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {

    private static final int GETNEWFRIENDS = 2099;
    private static final int FEEKBACKFRIENDREQUEST = 2098;
    private PullToRefreshListView refreshlistview;

    private NewFriendsAdapter mNewFriendsAdapter;

    private TextView mTextNot;
    private NewFriendsListResponse res;
    private FeedBackFriendRequestResponse feekRes;

    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("验证消息");
        setContentView(R.layout.sr_validation_activity);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        LoadDialog.show(mContext);
        request(GETNEWFRIENDS);
        initView();
    }

    private void initView() {
        refreshlistview = (PullToRefreshListView) findViewById(R.id.newfriendlistview);
        mTextNot = (TextView) findViewById(R.id.nonewfriend);
        refreshlistview.setOnRefreshListener(this);
        mNewFriendsAdapter = new NewFriendsAdapter(mContext);
        refreshlistview.setAdapter(mNewFriendsAdapter);
    }

    @Override
    public Object doInBackground(int requestCode) throws HttpException {
        switch (requestCode) {
            case GETNEWFRIENDS:
                return action.getNewFriendsList();
            case FEEKBACKFRIENDREQUEST:
                return action.FeedBackFriendRequest(res.getResult().get(index).getId(), "1");
        }
        return super.doInBackground(requestCode);
    }


    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode) {
            case GETNEWFRIENDS:
                if (result != null) {
                    res = (NewFriendsListResponse) result;
                    if (res.getCode() == 200) {
                        mNewFriendsAdapter.removeAll();
                        mNewFriendsAdapter.addData(res.getResult());
                        if (res.getResult().size() != 0) {
                            mTextNot.setVisibility(View.GONE);
                        }
                        mNewFriendsAdapter.notifyDataSetChanged();
                        mNewFriendsAdapter.setOnItemButtonClick(mOnItemButtonClick);
                        LoadDialog.dismiss(mContext);
                        refreshlistview.onRefreshComplete();
                    }
                }
                break;
            case FEEKBACKFRIENDREQUEST:
                if (result != null) {
                    feekRes = (FeedBackFriendRequestResponse) result;
                    if (feekRes.getCode() == 306) {
                        NToast.shortToast(mContext, "未知错误...");
                        LoadDialog.dismiss(mContext);
                        refreshlistview.onRefreshComplete();
                    } else if (feekRes.getCode() == 200) {
                        NToast.shortToast(mContext, "已同意好友关系...");
                        LoadDialog.dismiss(mContext);
                        refreshlistview.onRefreshComplete();
                        NewFriendsListResponse.ResultEntity bean = res.getResult().get(index);
                        DBManager.getInstance(mContext).getDaoSession().getFriendDao().insertOrReplace(new Friend(bean.getId(), bean.getUsername(), bean.getPortrait()));
                        sendAgreeMessage(res.getResult().get(index).getId());
                        BroadcastManager.getInstance(mContext).sendBroadcast(RongCloudEvent.REFRESHUI);
                        request(GETNEWFRIENDS);//刷新按钮UI
                    }
                }
                break;
        }
    }


    private int index;
    NewFriendsAdapter.OnItemButtonClick mOnItemButtonClick = new NewFriendsAdapter.OnItemButtonClick() {
        @Override
        public boolean onButtonClick(int position, View view, int status) {
            index = position;
            switch (status) {
                case 1://好友
                    break;
                case 2://请求添加
                    break;
                case 3://请求被添加
                    LoadDialog.show(mContext);
                    request(FEEKBACKFRIENDREQUEST);
                    break;
                case 4://请求被拒绝
                    break;
                case 5://我被对方删除
                    break;
            }

            return false;
        }
    };

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case GETNEWFRIENDS:
                NToast.shortToast(mContext, "加载失败...");
                LoadDialog.dismiss(mContext);
                refreshlistview.onRefreshComplete();
                break;
            case FEEKBACKFRIENDREQUEST:
                NToast.shortToast(mContext, "加载失败...");
                LoadDialog.dismiss(mContext);
                refreshlistview.onRefreshComplete();
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        request(GETNEWFRIENDS);
        LoadDialog.show(mContext);
    }

    /**
     * 当收到好友请求 点击同意添加按钮后 发送消息通知对方我已同意
     *
     * @param id
     */
    private void sendAgreeMessage(String id) {
        final AgreedFriendRequestMessage message = new AgreedFriendRequestMessage(id, "agree");
        //获取当前用户登录用户的相关信息
        String sUserName = sp.getString("username", "");
        String sUserId = sp.getString("userid", "");
        String sPortrait = sp.getString("portrait", "");
        if (!TextUtils.isEmpty(sUserId)) {
            new RuntimeException("sharedPreferences save userid! is empty");
        }
        UserInfo userInfo = new UserInfo(sUserId, sUserName, Uri.parse(sPortrait));
        message.setUserInfo(userInfo);
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE, id, message, null, null, new RongIMClient.SendMessageCallback() {
                @Override
                public void onError(Integer messageId, RongIMClient.ErrorCode e) {
                }

                @Override
                public void onSuccess(Integer integer) {

                }
            });
        }
    }
}
