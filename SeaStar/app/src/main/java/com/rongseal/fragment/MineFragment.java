package com.rongseal.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rongseal.App;
import com.rongseal.R;
import com.rongseal.RongCloudEvent;
import com.rongseal.activity.ContactActivity;
import com.rongseal.activity.MyDetailActivity;
import com.rongseal.activity.ValidationMessageActivity;
import com.rongseal.widget.CircleImageView;
import com.rongseal.widget.pulltorefresh.PullToRefreshBase;
import com.sd.core.common.broadcast.BroadcastManager;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;


/**
 * Created by AMing on 15/11/2.
 * Company RongCloud
 */
public class MineFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener, View.OnClickListener {

    public static final int REFRESH_CODE = 354;

    private LinearLayout layout_user, Validation;

    public static MineFragment instance = null;

    private ImageView redIcon;

    private CircleImageView mineHead;

    private TextView mineUserName, mineEmail, mineUserId;

    private SharedPreferences sp;

    public static MineFragment getInstance() {
        if (instance == null) {
            instance = new MineFragment();
        }
        return instance;
    }

    private LinearLayout LSetting, LAbout, LContact, LCustomerService;

    private View fragmentView;

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.rp_mine_fragment, null);
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        listenerBroadcast();
        return fragmentView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    public void initViews() {
        mineHead = (CircleImageView) fragmentView.findViewById(R.id.mine_userimg);
        mineUserName = (TextView) fragmentView.findViewById(R.id.mine_username);
        mineEmail = (TextView) fragmentView.findViewById(R.id.mine_email);
        mineUserId = (TextView) fragmentView.findViewById(R.id.mine_userid);
        mineUserName.setText(sp.getString("username", ""));
        mineEmail.setText(sp.getString("loginemail", ""));
        mineUserId.setText("id:" + sp.getString("userid", ""));
        String url = sp.getString("portrait", "");
        if (!TextUtils.isEmpty(url)) {
            ImageLoader.getInstance().displayImage(url, mineHead, App.getOptions());
        }

        redIcon = (ImageView) fragmentView.findViewById(R.id.reddot_icon);
        Validation = (LinearLayout) fragmentView.findViewById(R.id.validation);
        layout_user = (LinearLayout) fragmentView.findViewById(R.id.layout_user);
        layout_user.setOnClickListener(this);
        LSetting = (LinearLayout) fragmentView.findViewById(R.id.mine_setting);
        LContact = (LinearLayout) fragmentView.findViewById(R.id.mine_contact);
        LCustomerService = (LinearLayout) fragmentView.findViewById(R.id.customer_service);
        LAbout = (LinearLayout) fragmentView.findViewById(R.id.mine_about);
        LAbout.setOnClickListener(this);
        LCustomerService.setOnClickListener(this);
        LContact.setOnClickListener(this);
        LSetting.setOnClickListener(this);
        Validation.setOnClickListener(this);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_user:
                Intent intent = new Intent(getActivity(), MyDetailActivity.class);
                startActivityForResult(intent, REFRESH_CODE);
                break;
            case R.id.mine_about://关于

                break;
            case R.id.mine_contact://社交
                startActivity(new Intent(getActivity(), ContactActivity.class));
                break;
            case R.id.mine_setting://设置
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.customer_service://客服
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.APP_PUBLIC_SERVICE, "KEFU144542424649464", "在线客服");
                }
                break;
            case R.id.validation:
                startActivity(new Intent(getActivity(), ValidationMessageActivity.class));
                redIcon.setVisibility(View.GONE);
                break;
        }
    }


    private void listenerBroadcast() {
        BroadcastManager.getInstance(getActivity()).addAction(RongCloudEvent.FRIEND_MESSAGE, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getAction();
                if (!TextUtils.isEmpty(command)) {
                    redIcon.setVisibility(View.VISIBLE);
                }
            }
        });

        BroadcastManager.getInstance(getActivity()).addAction(RongCloudEvent.GONEREDDOT, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getAction();
                if (!TextUtils.isEmpty(command)) {
                    redIcon.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mineUserName.setText(sp.getString("username",""));
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        BroadcastManager.getInstance(getActivity()).destroy(RongCloudEvent.FRIEND_MESSAGE);
        BroadcastManager.getInstance(getActivity()).destroy(RongCloudEvent.GONEREDDOT);
    }
}
