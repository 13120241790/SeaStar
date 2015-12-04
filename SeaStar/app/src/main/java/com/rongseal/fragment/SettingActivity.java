package com.rongseal.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;

import com.rongseal.R;
import com.rongseal.activity.BaseActivity;
import com.rongseal.widget.SwitchButton;
import com.sd.core.common.broadcast.BroadcastManager;
import com.sd.core.utils.NToast;

import io.rong.imkit.RongContext;

/**
 * Created by AMing on 15/12/3.
 * Company RongCloud
 */
public class SettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    public static final java.lang.String START = "START";
    public static final java.lang.String PUSHSTART = "PUSHSTART";
    public static final java.lang.String CLOSE = "CLOSE";
    public static final java.lang.String PUSHCLOSE = "PUSHCLOSE";



    public static final String DESKTOP = "desktopkf";
    private static final String VOICE = "voice";
    public static final String PUSH = "settingpush";

    private SwitchButton showKF , voice ,mPush;
    private SharedPreferences sp;
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
        mPush = (SwitchButton) findViewById(R.id.setting_push);
        showKF.setOnCheckedChangeListener(this);
        voice.setOnCheckedChangeListener(this);
        mPush.setOnCheckedChangeListener(this);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        showKF.setChecked(sp.getBoolean(DESKTOP, true));
        voice.setChecked(sp.getBoolean(VOICE, true));
        mPush.setChecked(sp.getBoolean(PUSH,true));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences.Editor edit = sp.edit();
        switch (buttonView.getId()) {
            case R.id.show_desktopkf:
                if (isChecked) {
                    BroadcastManager.getInstance(mContext).sendBroadcast(START);
                    edit.putBoolean(DESKTOP, true);
                    edit.commit();
                } else {
                    BroadcastManager.getInstance(mContext).sendBroadcast(CLOSE);
                    edit.putBoolean(DESKTOP,false);
                    edit.commit();
                }
                break;
            case R.id.seting_voice:
                if (isChecked) {
                    edit.putBoolean(VOICE,true);
                    edit.commit();
                }else {
                    edit.putBoolean(VOICE,false);
                    edit.commit();
                }
                break;
            case R.id.setting_push:
                if (isChecked) {
                    BroadcastManager.getInstance(mContext).sendBroadcast(PUSHSTART);
                    edit.putBoolean(PUSH, true);
                    edit.commit();
                } else {
                    BroadcastManager.getInstance(mContext).sendBroadcast(PUSHCLOSE);
                    edit.putBoolean(PUSH,false);
                    edit.commit();
                }
                break;
        }
    }
}
