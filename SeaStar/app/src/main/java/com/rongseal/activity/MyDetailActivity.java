package com.rongseal.activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.rongseal.R;
import com.rongseal.bean.response.UpdateUserNameResponse;
import com.rongseal.widget.ClearWriteEditText;
import com.rongseal.widget.dialog.LoadDialog;
import com.rongseal.widget.pulltorefresh.PullToRefreshBase;
import com.sd.core.network.http.HttpException;
import com.sd.core.utils.NToast;

import java.util.HashMap;

import de.greenrobot.event.EventBus;
import io.rong.imkit.RongContext;
import io.rong.imlib.model.UserInfo;


/**
 * Created by AMing on 15/11/6.
 * Company RongCloud
 */
public class MyDetailActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {

    private static final int UPDATENAME = 675;
    private ClearWriteEditText updateName;
    private String name;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rp_detail_activity);
        setTitle("更改昵称");
        getBtn_right().setText("确定");
        getBtn_right().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = updateName.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    updateName.setShakeAnimation();
                    NToast.shortToast(mContext, "昵称不能为空");
                    return;
                }
                LoadDialog.show(mContext);
                request(UPDATENAME);
            }
        });
        initView();
    }


    @Override
    public Object doInBackground(int requestCode) throws HttpException {
        switch (requestCode) {
            case UPDATENAME:
                return action.updateUserName(name);
        }
        return super.doInBackground(requestCode);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode) {
            case UPDATENAME:
                if (result != null) {
                    UpdateUserNameResponse res = (UpdateUserNameResponse) result;
                    if (res.getCode() == 200) {
                        String id = sp.getString("userid", "");
                        String headUri = sp.getString("portrait", "");
                        if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(headUri)) {
                            RongContext.getInstance().getUserInfoCache().put(id, new UserInfo(id, name, Uri.parse(headUri)));
                        }
                        SharedPreferences.Editor e = sp.edit();
                        e.putString("username", name);
                        e.commit();
                        HashMap<String, String> hashName = new HashMap<String, String>();
                        hashName.put("upadtename",name);
                        NToast.shortToast(mContext,"更新成功");
                        LoadDialog.dismiss(mContext);
                        finish();
                    }
                }
                break;
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case UPDATENAME:
                NToast.shortToast(mContext,"更新失败");
                LoadDialog.dismiss(mContext);
                break;
        }
    }

    private void initView() {
        updateName = (ClearWriteEditText) findViewById(R.id.updatename);
        sp = getSharedPreferences("config", MODE_PRIVATE);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {

    }
}
