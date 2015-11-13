package com.rongseal.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.rongseal.R;
import com.rongseal.adapter.SearchListAdapter;
import com.rongseal.widget.ClearWriteEditText;
import com.rongseal.widget.dialog.LoadDialog;
import com.rongseal.widget.pulltorefresh.PullToRefreshBase;
import com.rongseal.widget.pulltorefresh.PullToRefreshListView;
import com.sd.core.network.http.HttpException;
import com.sd.core.utils.NToast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AMing on 15/11/12.
 * Company RongCloud
 */
public class SearchFriendActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener ,PullToRefreshBase.OnRefreshListener2<ListView> {

    private static final int SEARCH_FRIRND = 2018;
    private static final int SEARCH_FRIRND_WITH_EMAIL = 2019;
    private ClearWriteEditText searchEdit;

    private Button searchCommit;
    private String s;

    private PullToRefreshListView refreshlistview;
    private SearchListAdapter listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeadVisibility(View.GONE);
        setContentView(R.layout.sr_searchfriend_activity);
        initView();
    }

    private void initView() {

        refreshlistview = (PullToRefreshListView)findViewById(R.id.refreshlistview);
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

        refreshlistview.setEmptyView(LayoutInflater.from(mContext).inflate(R.layout.layout_empty, null));
//        refreshlistview.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        refreshlistview.setOnItemClickListener(this);
        refreshlistview.setOnRefreshListener(this);

        listAdapter = new SearchListAdapter(mContext);
        refreshlistview.setAdapter(listAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_commit:
                s = searchEdit.getText().toString().trim();
                if (TextUtils.isEmpty(s)) {
                    NToast.shortToast(mContext, "不能为空");
                    searchEdit.setShakeAnimation();
                    return;
                }
                LoadDialog.show(mContext,"正在搜索...");
                if (isEmail(s)) {
                    request(SEARCH_FRIRND_WITH_EMAIL);
                } else {
                    request(SEARCH_FRIRND);
                }
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

                break;
            case SEARCH_FRIRND_WITH_EMAIL:

                break;
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case SEARCH_FRIRND:

                break;
            case SEARCH_FRIRND_WITH_EMAIL:

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

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }
}
