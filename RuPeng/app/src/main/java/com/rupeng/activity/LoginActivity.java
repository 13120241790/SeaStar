package com.rupeng.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.rupeng.MainActivity;
import com.rupeng.R;
import com.rupeng.RongCloudEvent;
import com.rupeng.bean.response.LoginResponse;
import com.rupeng.widget.ClearWriteEditText;
import com.rupeng.widget.dialog.LoadDialog;
import com.sd.core.network.http.HttpException;
import com.sd.core.utils.NLog;
import com.sd.core.utils.NToast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by AMing on 15/11/2.
 * Company RongCloud
 * 登陆
 */
public class LoginActivity  extends BaseActivity implements View.OnClickListener {
    private static final int REGISTCODE = 101;
    private static final int LOGIN_CODE = 2016;
    // 用户名 密码
    private ClearWriteEditText mUserName , mPassWord;
    // 登录
    private Button  mButtonLogin;
    // 忘记密码 注册 左侧title  又侧title
    private TextView mFogotPassWord , mRegister,mButtonRegist;
    // 大 logo 图片  背景图片
    private ImageView mLoginImg , mImgBackgroud;

    private String sUserName,sPassword;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setHeadVisibility(View.GONE);
        setContentView(R.layout.rp_login_activity);
        initView();
    }

    private void initView() {
        mLoginImg = (ImageView) findViewById(R.id.de_login_logo);
        mUserName = (ClearWriteEditText) findViewById(R.id.app_username_et);
        mPassWord = (ClearWriteEditText) findViewById(R.id.app_password_et);
        mButtonLogin = (Button) findViewById(R.id.app_sign_in_bt);
        mRegister = (TextView) findViewById(R.id.de_login_register);
        mFogotPassWord = (TextView) findViewById(R.id.de_login_forgot);
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

        sp = getSharedPreferences("config",MODE_PRIVATE);
        String old_username = sp.getString("loginemail", "");
        String old_password = sp.getString("loginpassword", "");
        mUserName.setText(old_username);
        mPassWord.setText(old_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_sign_in_bt:
               sUserName =  mUserName.getText().toString().trim();
               sPassword =  mPassWord.getText().toString().trim();
                if (TextUtils.isEmpty(sUserName)) {
                    NToast.shortToast(mContext, "用户名不能为空");
                    mUserName.setShakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(sPassword)) {
                    NToast.shortToast(mContext, "密码不能为空");
                    mUserName.setShakeAnimation();
                    return;
                }
                if (!isEmail(sUserName)) {
                    NToast.shortToast(mContext,"邮箱地址不合法");
                    return;
                }
                LoadDialog.show(mContext, "正在登陆...");
                String sToken = sp.getString("token",null);
                if (!TextUtils.isEmpty(sToken) && sUserName.equals(sp.getString("loginemail",""))) {
                    connectServer(sToken);
                }else{
                    request(LOGIN_CODE);
                }


                SharedPreferences.Editor edit = sp.edit();
                edit.putString("loginemail", sUserName);
                edit.putString("loginpassword", sPassword);
                edit.commit();

                break;
            case R.id.de_login_register:
                Intent regIntent = new Intent(this,RegistActivity.class);
                startActivityForResult(regIntent,REGISTCODE);
                break;
        }
    }


    @Override
    public Object doInBackground(int requestCode) throws HttpException {
        switch (requestCode){
            case LOGIN_CODE:
              return action.login(sUserName,sPassword);
        }
        return super.doInBackground(requestCode);
    }


    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode) {
            case LOGIN_CODE:
                LoginResponse res = (LoginResponse)result;
                if (res.getCode() == 200) {
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("token", res.getResult().getToken());
                    edit.commit();
                    connectServer(res.getResult().getToken());
                }
                break;
        }
    }



    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case LOGIN_CODE:
                NToast.shortToast(mContext, "登录失败");
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


    public void connectServer(String token){
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Log.e("connect", "onTokenIncorrect");
            }

            @Override
            public void onSuccess(String s) {
                NToast.shortToast(mContext, "登录成功");
                LoadDialog.dismiss(mContext);
                Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mIntent);
                LoginActivity.this.finish();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.e("connect", "ErrorCode：" + errorCode.getValue());
            }
        });
    }

}
