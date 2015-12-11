package com.rongseal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rongseal.MainActivity;
import com.rongseal.R;
import com.rongseal.RongCloudEvent;
import com.rongseal.bean.response.LoginResponse;
import com.rongseal.bean.response.NewFriendsListResponse;
import com.rongseal.db.com.rongseal.database.DBManager;
import com.rongseal.db.com.rongseal.database.Friend;
import com.rongseal.widget.ClearWriteEditText;
import com.rongseal.widget.dialog.LoadDialog;
import com.sd.core.network.http.HttpException;
import com.sd.core.utils.NToast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 15/11/2.
 * Company RongCloud
 * 登陆
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final int REGISTCODE = 101;
    private static final int LOGIN_CODE = 2016;
    private static final int SYNCFRIEND = 199;

    // 用户名 密码
    private ClearWriteEditText mUserName, mPassWord;
    // 登录
    private Button mButtonLogin;
    // 忘记密码 注册 左侧title  又侧title
    private TextView mRegister, mButtonRegist;
    // 大 logo 图片  背景图片
    private ImageView mImgBackgroud;

    private String sUserName, sPassword, oldUserName, oldPassWord;

    private SharedPreferences sp;

    boolean isCache = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setHeadVisibility(View.GONE);
        setContentView(R.layout.rp_login_activity);
        initView();
    }

    private void initView() {
        mUserName = (ClearWriteEditText) findViewById(R.id.app_username_et);
        mPassWord = (ClearWriteEditText) findViewById(R.id.app_password_et);
        mButtonLogin = (Button) findViewById(R.id.app_sign_in_bt);
        mRegister = (TextView) findViewById(R.id.de_login_register);
        mImgBackgroud = (ImageView) findViewById(R.id.de_img_backgroud);
        mButtonRegist = (TextView) findViewById(R.id.de_login_register);
        mButtonLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mUserName.setOnClickListener(this);
        mPassWord.setOnClickListener(this);
        mButtonRegist.setOnClickListener(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.translate_anim);
                mImgBackgroud.startAnimation(animation);
            }
        }, 200);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        oldUserName = sp.getString("loginemail", "");
        oldPassWord = sp.getString("loginpassword", "");


        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("fristlogin", false);
        edit.commit();

        mUserName.setText(oldUserName);
        mPassWord.setText(oldPassWord);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_sign_in_bt:
                sUserName = mUserName.getText().toString().trim();
                sPassword = mPassWord.getText().toString().trim();
                SharedPreferences.Editor edit = sp.edit();
                if (TextUtils.isEmpty(sUserName)) {
                    NToast.shortToast(mContext, getResources().getString(R.string.username_isnull));
                    mUserName.setShakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(sPassword)) {
                    NToast.shortToast(mContext, getResources().getString(R.string.password_isnull));
                    mUserName.setShakeAnimation();
                    return;
                }
                if (!isEmail(sUserName)) {
                    NToast.shortToast(mContext, getResources().getString(R.string.email_iserror));
                    return;
                }
                LoadDialog.show(mContext, getResources().getString(R.string.login));
                String sToken = sp.getString("token", "");
                if (!TextUtils.isEmpty(sToken) && sUserName.equals(oldUserName)) {
                    edit.remove("loginemail");
                    edit.remove("loginpassword");
                    isCache = true;
                    connectServer(sToken);
                } else {
                    request(LOGIN_CODE);
                }


                edit.putString("loginemail", sUserName);
                edit.putString("loginpassword", sPassword);
                edit.putBoolean("fristlogin", true);
                edit.commit();

                break;
            case R.id.de_login_register:
                Intent regIntent = new Intent(this, RegistActivity.class);
                startActivityForResult(regIntent, REGISTCODE);
                break;
        }
    }


    @Override
    public Object doInBackground(int requestCode) throws HttpException {
        switch (requestCode) {
            case LOGIN_CODE:
                return action.login(sUserName, sPassword);
            case SYNCFRIEND:
                return action.getNewFriendsList();
        }
        return super.doInBackground(requestCode);
    }


    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode) {
            case LOGIN_CODE:
                LoginResponse res = (LoginResponse) result;
                if (res.getCode() == 200) {
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("token", res.getResult().getToken());
                    edit.putString("portrait", res.getResult().getPortrait());
                    edit.putString("userid", res.getResult().getId());
                    edit.putString("username", res.getResult().getUsername());
                    edit.commit();
                    connectServer(res.getResult().getToken());
                } else if (res.getCode() == 104) {
                    LoadDialog.dismiss(mContext);
                    NToast.shortToast(mContext, getResources().getString(R.string.email_or_password_iserror));
                } else if (res.getCode() == 103) {
                    LoadDialog.dismiss(mContext);
                    NToast.shortToast(mContext, getResources().getString(R.string.email_or_password_iserror));
                }
                break;
            case SYNCFRIEND:
                if (!isCache) {
                    NewFriendsListResponse syncRes = (NewFriendsListResponse) result;
                    if (syncRes.getCode() == 200) {
                        List<NewFriendsListResponse.ResultEntity> beanList = syncRes.getResult();
                        if (beanList != null) {
                            DBManager.getInstance(mContext).getDaoSession().getFriendDao().deleteAll();
                            for (int i = 0; i < beanList.size(); i++) {
                                if (beanList.get(i).getStatus().equals("1")) {
                                    DBManager.getInstance(mContext).getDaoSession().getFriendDao().insertOrReplace(new Friend(
                                            beanList.get(i).getId(),
                                            beanList.get(i).getUsername(),
                                            beanList.get(i).getPortrait()
                                    ));
                                }
                            }
                        }
                    }
                }
                NToast.shortToast(mContext, getResources().getString(R.string.login_success));
                LoadDialog.dismiss(mContext);
                RongCloudEvent.getInstance().setConnectedListener();

                Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mIntent);
                LoginActivity.this.finish();
                break;
        }
    }


    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case LOGIN_CODE:
                NToast.shortToast(mContext, getResources().getString(R.string.login_fail));
                LoadDialog.dismiss(mContext);
                break;
            case SYNCFRIEND:
                NToast.shortToast(mContext, getResources().getString(R.string.sync_friend_data_fail));
                LoadDialog.dismiss(mContext);
                break;
        }
    }

    // 拿到注册返回的值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String regEmail = data.getStringExtra("email");
            String regPassword = data.getStringExtra("password");
            if (!regEmail.isEmpty() || !regPassword.isEmpty()) {
                mUserName.setText(regEmail);
                mPassWord.setText(regPassword);
            }
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


    public void connectServer(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Log.e("connect", "onTokenIncorrect");
            }

            @Override
            public void onSuccess(String s) {
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().setCurrentUserInfo(new UserInfo(s, sp.getString("username", ""), Uri.parse(sp.getString("portrait", ""))));
                    RongIM.getInstance().setMessageAttachedUserInfo(true);
                }
                request(SYNCFRIEND);
                Log.e("userid", "userid" + s);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.e("connect", "ErrorCode：" + errorCode.getValue());
            }
        });
    }

}
