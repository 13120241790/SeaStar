package com.rupeng.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rupeng.R;
import com.rupeng.bean.response.RegistResponse;
import com.rupeng.common.ApiAction;
import com.rupeng.widget.dialog.LoadDialog;
import com.sd.core.network.async.AsyncTaskManager;
import com.sd.core.network.http.HttpException;
import com.sd.core.network.http.SyncHttpClient;
import com.sd.core.utils.NToast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.rong.imkit.RongIM;

/**
 * Created by AMing on 15/11/10.
 * Company RongCloud
 */
public class RegistActivity extends BaseActivity implements View.OnClickListener {

    private static final int REGIST_CODE = 2015;

    private int REGIST_BACK = 1001;
    private EditText mEmail , mPassword ,mUserName;

    private Button mButton;

    protected Context mContext;

    private String sEmail;
    private String sPassword;
    private String sUserName;

    private SyncHttpClient mSyncHttpClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rp_regist_activity);
        mContext = this;
        mSyncHttpClient = SyncHttpClient.getInstance(mContext);
        setTitle("注册");
        initView();
    }

    private void initView() {
        mEmail = (EditText) findViewById(R.id.reg_email);
        mPassword = (EditText) findViewById(R.id.reg_password);
        mUserName = (EditText) findViewById(R.id.reg_username);
        mButton = (Button) findViewById(R.id.reg_button);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reg_button:
                sEmail = mEmail.getText().toString().trim();
                sPassword = mPassword.getText().toString().trim();
                sUserName = mUserName.getText().toString().trim();


                if (TextUtils.isEmpty(sEmail)|TextUtils.isEmpty(sPassword)|TextUtils.isEmpty(sUserName)) {
                    Toast.makeText(this, "注册信息(邮箱,密码,昵称)不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isEmail(sEmail)) {
                    NToast.shortToast(mContext, "邮箱地址不合法");
                    return;
                }
                    LoadDialog.show(mContext);
                    request(REGIST_CODE);
                break;
        }
    }

    @Override
    public Object doInBackground(int requestCode) throws HttpException {
        switch (requestCode) {
            case REGIST_CODE:
                return action.regist(sEmail,sPassword,sUserName,"13120241790");
        }
        return super.doInBackground(requestCode);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode){
            case REGIST_CODE:
                LoadDialog.dismiss(mContext);
                if (result != null) {
                    RegistResponse res = (RegistResponse)result;
                    switch (res.getCode()) {
                        case 200:
                            NToast.shortToast(mContext, "注册成功");
                            Intent data = new Intent();
                            data.putExtra("email", sEmail);
                            data.putExtra("password", sPassword);
                            setResult(REGIST_BACK, data);
                            RegistActivity.this.finish();
                            break;
                        case 101:
                            NToast.shortToast(mContext,"邮箱已存在");
                            break;
                    }
                }
                break;
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode){
            case REGIST_CODE:
                NToast.shortToast(mContext, "注册失败");
                LoadDialog.dismiss(mContext);
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
}
