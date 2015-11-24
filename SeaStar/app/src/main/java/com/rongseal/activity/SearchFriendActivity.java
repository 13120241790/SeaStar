package com.rongseal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rongseal.R;
import com.rongseal.adapter.SearchListAdapter;
import com.rongseal.bean.response.SearchEmailResponse;
import com.rongseal.bean.response.SearchUserNameResponse;
import com.rongseal.widget.ClearWriteEditText;
import com.rongseal.widget.dialog.LoadDialog;
import com.rongseal.widget.pulltorefresh.PullToRefreshBase;
import com.rongseal.widget.pulltorefresh.PullToRefreshListView;
import com.sd.core.network.http.HttpException;
import com.sd.core.utils.NToast;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AMing on 15/11/12.
 * Company RongCloud
 */
public class SearchFriendActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2<ListView> {

    private static final int SEARCH_FRIRND = 2018;
    private static final int SEARCH_FRIRND_WITH_EMAIL = 2019;
    private ClearWriteEditText searchEdit;

    private Button searchCommit;
    private String s;

    //搜索单邮箱
    RelativeLayout singeItem;
    ImageView singeHead;
    TextView singeId, singeUserName;

    SharedPreferences sp;

    private PullToRefreshListView refreshlistview;
    private SearchListAdapter listAdapter;
    private SearchEmailResponse mailRes;
    private SearchUserNameResponse userNameRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeadVisibility(View.GONE);
        setContentView(R.layout.sr_searchfriend_activity);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        initView();
    }

    private void initView() {

        singeItem = (RelativeLayout) findViewById(R.id.email_item);
        singeHead = (ImageView) findViewById(R.id.email_img_head);
        singeId = (TextView) findViewById(R.id.email_tv_userid);
        singeUserName = (TextView) findViewById(R.id.email_tv_username);


        refreshlistview = (PullToRefreshListView) findViewById(R.id.refreshlistview);
        searchEdit = (ClearWriteEditText) findViewById(R.id.search_friend);
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (!TextUtils.isEmpty(String.valueOf(arg0))) {
                    searchCommit.setText(R.string.search_text);
                } else {
                    searchCommit.setText(R.string.common_cancel);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });

        searchCommit = (Button) findViewById(R.id.search_commit);
        searchCommit.setOnClickListener(this);
        singeItem.setOnClickListener(this);
        refreshlistview.setEmptyView(LayoutInflater.from(mContext).inflate(R.layout.layout_empty, null));
        refreshlistview.setOnItemClickListener(this);
        refreshlistview.setOnRefreshListener(this);

        listAdapter = new SearchListAdapter(mContext);
        refreshlistview.setAdapter(listAdapter);
    }

    @Override
    public void onClick(View v) {
       String loginUser =  sp.getString("loginemail","");
        if (loginUser.equals(s) && !TextUtils.isEmpty(loginUser)) {
            NToast.shortToast(mContext,"自己不能添加自己");
            return;
        }
        switch (v.getId()) {
            case R.id.search_commit:
                s = searchEdit.getText().toString().trim();
                if (TextUtils.isEmpty(s)) {
                    NToast.shortToast(mContext, "不能为空");
                    searchEdit.setShakeAnimation();
                    return;
                }
                LoadDialog.show(mContext, "正在搜索...");
                if (isEmail(s)) {
                    request(SEARCH_FRIRND_WITH_EMAIL);
                } else {
                    request(SEARCH_FRIRND);
                }
                break;
            case R.id.email_item:
                Intent intent = new Intent(this,UserDetailActivity.class);
                intent.putExtra("search_username",mailRes.getResult().getUsername());
                intent.putExtra("search_userid",mailRes.getResult().getId());
                intent.putExtra("search_portrait",mailRes.getResult().getPortrait());
                startActivity(intent);
                break;
        }
    }

    @Override
    public Object doInBackground(int requestCode) throws HttpException {
        switch (requestCode) {
            case SEARCH_FRIRND:
                return action.searchUserName(s);
            case SEARCH_FRIRND_WITH_EMAIL:
                return action.searchEmail(s);
        }
        return super.doInBackground(requestCode);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode) {
            case SEARCH_FRIRND:
                if (result != null) {
                    userNameRes = (SearchUserNameResponse) result;
                    if (userNameRes.getCode() == 200) {
                        listAdapter.removeAll();
                        refreshlistview.setVisibility(View.VISIBLE);
                        singeItem.setVisibility(View.GONE);
                        listAdapter.addData(userNameRes.getResult());
                        listAdapter.notifyDataSetChanged();
                        LoadDialog.dismiss(mContext);
                        refreshlistview.onRefreshComplete();
                    }
                }
                break;
            case SEARCH_FRIRND_WITH_EMAIL:
                if (result != null) {
                    mailRes = (SearchEmailResponse) result;
                    if (mailRes.getCode() == 200) {
                        refreshlistview.setVisibility(View.GONE);
                        singeItem.setVisibility(View.VISIBLE);
                        LoadDialog.dismiss(mContext);
                        refreshlistview.onRefreshComplete();
                        singeItem.setOnClickListener(this);
                        singeUserName.setText(mailRes.getResult().getUsername());
                        singeId.setText("id:" + mailRes.getResult().getId());
                        Picasso.with(mContext)
                                .load(mailRes.getResult().getPortrait())
                                .placeholder(R.drawable.rp_default_head)
                                .centerCrop()
                                .into(singeHead);
                    }
                }
                break;
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case SEARCH_FRIRND:
                LoadDialog.dismiss(mContext);
                refreshlistview.onRefreshComplete();
                break;
            case SEARCH_FRIRND_WITH_EMAIL:
                LoadDialog.dismiss(mContext);
                refreshlistview.onRefreshComplete();
                break;
        }
    }

    /**
     * 邮箱格式是否正确
     *
     * @param email
     * @return
     */
    public boolean isEmail(String email) {

        if (TextUtils.isEmpty(email))
            return false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        if (matcher.matches())
            return true;
        else
            return false;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //TODO 此处需要检查是自己 是自己不给开启  代码未编写

        Intent intent = new Intent(this,UserDetailActivity.class);
        intent.putExtra("search_username",userNameRes.getResult().get(position-1).getUsername());
        intent.putExtra("search_userid",userNameRes.getResult().get(position-1).getId());
        intent.putExtra("search_portrait",userNameRes.getResult().get(position-1).getPortrait());
        startActivity(intent);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (TextUtils.isEmpty(s)) {
            NToast.shortToast(mContext, "关键字不能为空");
            refreshlistview.onRefreshComplete();
            return;
        }
        LoadDialog.show(mContext, "正在搜索");
        if (isEmail(s)) {
            request(SEARCH_FRIRND_WITH_EMAIL);
        } else {
            request(SEARCH_FRIRND);
        }
    }


    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }
}
