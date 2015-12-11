package com.rongseal.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.rongseal.R;
import com.rongseal.adapter.BlackListAdapter;
import com.rongseal.bean.response.UserDetailInfoResponse;
import com.rongseal.widget.dialog.LoadDialog;
import com.rongseal.widget.pulltorefresh.PullToRefreshBase;
import com.rongseal.widget.pulltorefresh.PullToRefreshListView;
import com.sd.core.common.broadcast.BroadcastManager;
import com.sd.core.network.http.HttpException;
import com.sd.core.utils.NToast;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 15/11/30.
 * Company RongCloud
 */
public class BlackListActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {

    private static final int GETDATA = 3453;
    private ArrayList<String> blackList = new ArrayList<>();
    private ArrayList<UserInfo> blackListInfo = new ArrayList<>();

    private PullToRefreshListView mListView;
    private TextView mTextView;
    private BlackListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.blacklist);
        setContentView(R.layout.ss_blacklsit_activity);
        initView();
        initData();
        BroadcastManager.getInstance(mContext).addAction(BlackListAdapter.BLACKLISTUI, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getAction();
                if (!TextUtils.isEmpty(command)) {
                    blackList.clear();
                    blackListInfo.clear();
                    initData();
                    adapter = new BlackListAdapter(mContext);
                    if (blackListInfo != null && blackList.size() > 0) {
                        adapter.setDataSet(blackListInfo);
                        mTextView.setVisibility(View.GONE);
                    } else {
                        mTextView.setVisibility(View.VISIBLE);
                    }
                    mListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void initView() {
        mListView = (PullToRefreshListView) findViewById(R.id.blacklistview);
        mTextView = (TextView) findViewById(R.id.noblacklist);
    }


    @Override
    public Object doInBackground(int requestCode) throws HttpException {
        switch (requestCode) {
            case GETDATA:
                for (String userid : blackList) {
                    return action.getUserDetailInfo(userid);
                }
        }
        return super.doInBackground(requestCode);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode) {
            case GETDATA:
                if (result != null) {
                    UserDetailInfoResponse res = (UserDetailInfoResponse) result;
                    if (res.getCode() == 200) {
                        blackListInfo.add(new UserInfo(res.getResult().getId(), res.getResult().getUsername(), Uri.parse(res.getResult().getPortrait())));
                    }
                }
                adapter = new BlackListAdapter(mContext);
                if (blackListInfo != null && blackList.size() > 0) {
                    adapter.setDataSet(blackListInfo);
                    mTextView.setVisibility(View.GONE);
                } else {
                    mTextView.setVisibility(View.VISIBLE);
                }
                mListView.setAdapter(adapter);
                NToast.shortToast(mContext, getResources().getString(R.string.load_success));
                LoadDialog.dismiss(mContext);
                break;

        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        NToast.shortToast(mContext, getResources().getString(R.string.load_fail));
        LoadDialog.dismiss(mContext);
    }

    private void initData() {
        if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {
            RongIM.getInstance().getRongIMClient().getBlacklist(new RongIMClient.GetBlacklistCallback() {
                @Override
                public void onSuccess(String[] strings) {
                    if (strings != null && strings.length > 0) {
                        for (int i = 0; i < strings.length; i++) {
                            blackList.add(strings[i]);
                        }
                        if (blackList != null && blackList.size() > 0) {
                            LoadDialog.show(mContext);
                            request(GETDATA);
                        }
                    } else {
                        mTextView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastManager.getInstance(mContext).destroy(BlackListAdapter.BLACKLISTUI);
    }
}
