package com.rongseal.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rongseal.App;
import com.rongseal.R;
import com.rongseal.bean.response.UserDetailInfoResponse;
import com.rongseal.db.com.rongseal.database.DBManager;
import com.rongseal.db.com.rongseal.database.Friend;
import com.rongseal.widget.dialog.LoadDialog;
import com.sd.core.network.http.HttpException;

import java.util.List;

/**
 * Created by AMing on 15/11/26.
 * Company RongCloud
 */
public class UserDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final int USERDETAILS = 9876;

    private String userid;

    private List<Friend> friendsList;

    private ImageView mHead;

    private TextView mName, mId, mIsFriend;

    private boolean isFriend = false;

    private Button mAddFriend, mDeleteFriend, mAddBlackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("用户详情");
        setContentView(R.layout.userdetail_activity);
        userid = getIntent().getStringExtra("userid");
        LoadDialog.show(mContext);
        request(USERDETAILS);
        initView();
        friendsList = DBManager.getInstance(mContext).getDaoSession().getFriendDao().queryBuilder().list();
        for (Friend list : friendsList) {
            if (list.getUserId().equals(userid)) {
                isFriend = true;
                mAddFriend.setVisibility(View.GONE);
            } else {
                mAddBlackList.setVisibility(View.GONE);
                mDeleteFriend.setVisibility(View.GONE);

            }
        }
    }

    private void initView() {
        mAddFriend = (Button) findViewById(R.id.user_add_friend);
        mDeleteFriend = (Button) findViewById(R.id.user_delete_friend);
        mAddBlackList = (Button) findViewById(R.id.user_add_blacklist);
        mAddFriend.setOnClickListener(this);
        mDeleteFriend.setOnClickListener(this);
        mAddBlackList.setOnClickListener(this);
        mHead = (ImageView) findViewById(R.id.user_head);
        mName = (TextView) findViewById(R.id.detial_user_name);
        mId = (TextView) findViewById(R.id.user_id);
        mIsFriend = (TextView) findViewById(R.id.isfriend);
    }

    @Override
    public Object doInBackground(int requestCode) throws HttpException {
        switch (requestCode) {
            case USERDETAILS:
                return action.getUserDetailInfo(userid);
        }
        return super.doInBackground(requestCode);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode) {
            case USERDETAILS:
                if (result != null) {
                    UserDetailInfoResponse res = (UserDetailInfoResponse) result;
                    if (res.getCode() == 200) {
                        ImageLoader.getInstance().displayImage(res.getResult().getPortrait(), mHead, App.getOptions());
                        mName.setText("用户名:" + res.getResult().getUsername());
                        mId.setText("id:" + res.getResult().getId());
                        if (isFriend) {
                            mIsFriend.setText("关系状态:是好友");
                        } else {
                            mIsFriend.setText("关系状态:非好友");
                        }
                        LoadDialog.dismiss(mContext);
                    }
                }
                break;
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case USERDETAILS:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_add_friend:
                break;
            case R.id.user_delete_friend:
                break;
            case R.id.user_add_blacklist:
                break;
        }
    }
}
