package com.rongseal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rongseal.App;
import com.rongseal.R;
import com.rongseal.RongCloudEvent;
import com.rongseal.bean.response.AddFriendResponse;
import com.rongseal.bean.response.DeleteFriendResponse;
import com.rongseal.bean.response.UserDetailInfoResponse;
import com.rongseal.db.com.rongseal.database.DBManager;
import com.rongseal.db.com.rongseal.database.Friend;
import com.rongseal.utlis.DialogWithYesOrNoUtils;
import com.rongseal.widget.CircleImageView;
import com.rongseal.widget.dialog.LoadDialog;
import com.sd.core.common.broadcast.BroadcastManager;
import com.sd.core.network.http.HttpException;
import com.sd.core.utils.NToast;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by AMing on 15/11/26.
 * Company RongCloud
 */
public class UserDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final int USERDETAILS = 9876;
    private static final int ADDFRIEND = 8765;
    private static final int DELETEFRIEND = 7654;

    private String userid;

    private List<Friend> friendsList;

    private CircleImageView mHead;

    private TextView mName, mId, mIsFriend;

    private boolean isFriend = false;

    private Button mAddFriend, mDeleteFriend, mAddBlackList, mRequestSend;
    private UserDetailInfoResponse udRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("用户详情");
        setContentView(R.layout.userdetail_activity);
        userid = getIntent().getStringExtra("userid");
        LoadDialog.show(mContext);
        request(USERDETAILS);
        initView();
    }

    private void initView() {
        mRequestSend = (Button) findViewById(R.id.user_see_status);
        mRequestSend.setVisibility(View.GONE);
        mAddFriend = (Button) findViewById(R.id.user_add_friend);
        mDeleteFriend = (Button) findViewById(R.id.user_delete_friend);
        mAddBlackList = (Button) findViewById(R.id.user_add_blacklist);
        mAddFriend.setOnClickListener(this);
        mDeleteFriend.setOnClickListener(this);
        mAddBlackList.setOnClickListener(this);
        mHead = (CircleImageView) findViewById(R.id.user_head);
        mName = (TextView) findViewById(R.id.detial_user_name);
        mId = (TextView) findViewById(R.id.user_id);
        mIsFriend = (TextView) findViewById(R.id.isfriend);
        friendsList = DBManager.getInstance(mContext).getDaoSession().getFriendDao().queryBuilder().list();
        for (Friend list : friendsList) {
            if (list.getUserId().equals(userid)) {
                isFriend = true;
                mAddFriend.setVisibility(View.GONE);
                mAddBlackList.setVisibility(View.VISIBLE);
                mDeleteFriend.setVisibility(View.VISIBLE);
            } else {
                mAddBlackList.setVisibility(View.GONE);
                mDeleteFriend.setVisibility(View.GONE);
                mAddFriend.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public Object doInBackground(int requestCode) throws HttpException {
        switch (requestCode) {
            case USERDETAILS:
                return action.getUserDetailInfo(userid);
            case ADDFRIEND:
                return action.addFriend(userid, "你好,我在多人聊天中看到你");
            case DELETEFRIEND:
                return action.deleteFriend(userid);
        }
        return super.doInBackground(requestCode);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode) {
            case USERDETAILS:
                if (result != null) {
                    udRes = (UserDetailInfoResponse) result;
                    if (udRes.getCode() == 200) {
                        ImageLoader.getInstance().displayImage(udRes.getResult().getPortrait(), mHead, App.getOptions());
                        mName.setText("用户名:" + udRes.getResult().getUsername());
                        mId.setText("id:" + udRes.getResult().getId());
                        if (isFriend) {
                            mIsFriend.setText("关系状态:是好友");
                        } else {
                            mIsFriend.setText("关系状态:非好友");
                        }
                        LoadDialog.dismiss(mContext);
                    }
                }
                break;
            case ADDFRIEND:
                if (result != null) {
                    AddFriendResponse res = (AddFriendResponse) result;
                    if (res.getCode() == 200) {
                        mDeleteFriend.setVisibility(View.GONE);
                        mAddBlackList.setVisibility(View.GONE);
                        mAddFriend.setVisibility(View.GONE);
                        mRequestSend.setVisibility(View.VISIBLE);
                        mRequestSend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(UserDetailActivity.this, ValidationMessageActivity.class));
                            }
                        });
                        NToast.shortToast(mContext, "请求成功");
                        LoadDialog.dismiss(mContext);
                    } else if (res.getCode() == 301) {
                        mDeleteFriend.setVisibility(View.GONE);
                        mAddBlackList.setVisibility(View.GONE);
                        mAddFriend.setVisibility(View.GONE);
                        mRequestSend.setVisibility(View.VISIBLE);
                        mRequestSend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(UserDetailActivity.this, ValidationMessageActivity.class));
                            }
                        });
                        NToast.shortToast(mContext, "请求已经发出,等待对方响应。请勿重复请求");
                        LoadDialog.dismiss(mContext);
                    } else {
                        NToast.shortToast(mContext, "请求失败:" + res.getCode());
                        LoadDialog.dismiss(mContext);
                    }
                }
                break;
            case DELETEFRIEND:
                if (result != null) {
                    DeleteFriendResponse res = (DeleteFriendResponse) result;
                    if (res.getCode() == 200) {
                        DBManager.getInstance(mContext).getDaoSession().getFriendDao().delete(new Friend(udRes.getResult().getId()));
                        EventBus.getDefault().post(new DeleteFriend());
                        BroadcastManager.getInstance(mContext).sendBroadcast(RongCloudEvent.REFRESHUI);
                        if (RongIM.getInstance() != null) {
                            RongIM.getInstance().getRongIMClient().removeConversation(Conversation.ConversationType.PRIVATE, userid);
                        }
                        NToast.shortToast(mContext, "删除成功");
                        LoadDialog.dismiss(mContext);
                        finish();
                    } else {
                        NToast.shortToast(mContext, "删除失败:" + res.getCode());
                        LoadDialog.dismiss(mContext);
                    }
                }
                break;
        }
    }

    public class DeleteFriend {
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case USERDETAILS:
                LoadDialog.dismiss(mContext);
                break;
            case ADDFRIEND:
                LoadDialog.dismiss(mContext);
                break;
            case DELETEFRIEND:
                LoadDialog.dismiss(mContext);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_add_friend:
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "请求添加对方为好友？", new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void exectEvent() {
                        LoadDialog.show(mContext, "添加好友...");
                        request(ADDFRIEND);
                    }
                });
                break;
            case R.id.user_delete_friend:
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "是否删除好友？", new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void exectEvent() {
                        LoadDialog.show(mContext, "删除好友...");
                        request(DELETEFRIEND);
                    }
                });
                break;
            case R.id.user_add_blacklist:
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "是否将其加入黑名单?", new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void exectEvent() {
                        if (RongIM.getInstance() != null && udRes != null) {
                            RongIM.getInstance().getRongIMClient().addToBlacklist(udRes.getResult().getId(), new RongIMClient.OperationCallback() {
                                @Override
                                public void onSuccess() {
                                    NToast.shortToast(mContext, "加入黑名单成功");
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    NToast.shortToast(mContext, "加入黑名单失败:" + errorCode.getValue());
                                }
                            });
                        }
                    }
                });
                break;
        }
    }
}
