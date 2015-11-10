package com.rupeng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.rupeng.widget.ClearWriteEditText;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by AMing on 15/11/2.
 * Company RongCloud
 */
public class LoginActivity  extends BaseActivity implements View.OnClickListener {
    private static final int REGISTCODE = 101;
    // 用户名 密码
    private ClearWriteEditText mUserName , mPassWord;
    // 登录
    private Button  mButtonLogin;
    // 忘记密码 注册 左侧title  又侧title
    private TextView mFogotPassWord , mRegister,mButtonRegist;
    // 大 logo 图片  背景图片
    private ImageView mLoginImg , mImgBackgroud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setHeadVisibility(View.GONE);
        setContentView(R.layout.rp_login_activity);

        RongIM.connect("bugmIZWR5JGzPHNoNp47EEGFC6hW/OOiwJwgWU0oTvPK1cxi0MjsRwRj4jyW+UFZDt0alvCqoDsBSVHlDVJA1g==", new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Log.e("App", "onTokenIncorrect");
            }

            @Override
            public void onSuccess(String s) {
                Log.e("App", "userid is ：" + s);
                RongCloudEvent.getInstance().setConnectedListener();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCod) {
                Log.e("App", "ErrorCode =" + errorCod.getValue());
            }
        });

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_sign_in_bt:
//                mUserName.setShakeAnimation();
                Intent mIntent = new Intent(this, MainActivity.class);
                startActivity(mIntent);
                this.finish();
                break;
            case R.id.de_login_register:
                Intent regIntent = new Intent(this,RegistActivity.class);
                startActivityForResult(regIntent,REGISTCODE);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImgBackgroud = null;
    }
}
