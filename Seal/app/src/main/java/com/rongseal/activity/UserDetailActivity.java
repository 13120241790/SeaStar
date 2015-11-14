package com.rongseal.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rongseal.R;
import com.rongseal.bean.response.AddFriendResponse;
import com.rongseal.widget.ClearWriteEditText;
import com.rongseal.widget.dialog.LoadDialog;
import com.sd.core.network.http.HttpException;
import com.sd.core.utils.NToast;

/**
 * Created by AMing on 15/11/14.
 * Company RongCloud
 */
public class UserDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final int ADDFRIEND = 2213;

    private TextView mUserName, mUserId;

    private ImageView mHead;

    private Button mAddFriend;

    private ClearWriteEditText addFriendEdit;

    private String addMessage, sUserId;

//    intent.putExtra("search_username",userNameRes.getResult().get(position).getUsername());
//    intent.putExtra("search_userid",userNameRes.getResult().get(position).getId());
//    intent.putExtra("search_portrait",userNameRes.getResult().get(position).getPortrait());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("用户信息");
        setContentView(R.layout.sr_userdetail_activity);

        initView();
    }

    private void initView() {
        String sUserName = getIntent().getStringExtra("search_username");
        sUserId = getIntent().getStringExtra("search_userid");
        String sPortrait = getIntent().getStringExtra("search_portrait");
        addFriendEdit = (ClearWriteEditText) findViewById(R.id.add_friend_chat);
        mHead = (ImageView) findViewById(R.id.detail_head);
        mUserName = (TextView) findViewById(R.id.detail_name);
        mUserId = (TextView) findViewById(R.id.detail_id);
        mUserName.setText(sUserName);
        mUserId.setText("ID:"+sUserId);
        mAddFriend = (Button) findViewById(R.id.add_friend);
        mAddFriend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_friend) {
            addMessage = addFriendEdit.getText().toString().trim();
            if (TextUtils.isEmpty(addMessage)){
                NToast.shortToast(mContext, "请求添加好友信息不能为空");
                addFriendEdit.setShakeAnimation();
                return;
            }
            LoadDialog.show(mContext, "正在请求...");
            request(ADDFRIEND);
        }
    }

    @Override
    public Object doInBackground(int requestCode) throws HttpException {
        switch (requestCode) {
            case ADDFRIEND:
                return action.addFriend(sUserId, addMessage);
        }
        return super.doInBackground(requestCode);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode) {
            case ADDFRIEND:
                if (result != null) {
                    AddFriendResponse res = (AddFriendResponse) result;
                    if (res.getCode() == 200) {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, "请求成功!");
                        mAddFriend.setClickable(false);
                        mAddFriend.setTextColor(getResources().getColor(R.color.group_list_gray));
                        mAddFriend.setText("请求已发出");
                    }else if (res.getCode() == 301) {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, "已是好友,请勿重复添加!");
                        mAddFriend.setClickable(false);
                        mAddFriend.setTextColor(getResources().getColor(R.color.group_list_gray));
                        mAddFriend.setText("已存在好友关系");
                    }
                }
                break;
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case ADDFRIEND:
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, "请求失败!");
                break;
        }
    }
}
