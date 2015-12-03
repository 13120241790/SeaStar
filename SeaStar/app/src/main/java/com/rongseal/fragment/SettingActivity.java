package com.rongseal.fragment;

import android.os.Bundle;
import android.widget.CompoundButton;

import com.rongseal.R;
import com.rongseal.activity.BaseActivity;
import com.rongseal.widget.SwitchButton;
import com.sd.core.utils.NToast;

/**
 * Created by AMing on 15/12/3.
 * Company RongCloud
 */
public class SettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private SwitchButton showKF , voice ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("设置");
        setContentView(R.layout.ss_setting_activity);
        initView();
    }

    private void initView() {
        showKF = (SwitchButton) findViewById(R.id.show_desktopkf);
        voice = (SwitchButton) findViewById(R.id.seting_voice);
        showKF.setOnCheckedChangeListener(this);
        voice.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.show_desktopkf:
                if (isChecked) {
                    NToast.shortToast(mContext,"已开启桌面客服");
                }else {
                    NToast.shortToast(mContext,"已关闭桌面客服");
                }
                break;
            case R.id.seting_voice:
                if (isChecked) {
                    NToast.shortToast(mContext,"已开启声音");
                }else {
                    NToast.shortToast(mContext,"已关闭声音");
                }
                break;
        }
    }
}
