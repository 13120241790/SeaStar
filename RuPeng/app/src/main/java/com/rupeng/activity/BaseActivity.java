package com.rupeng.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bugtags.library.Bugtags;
import com.rupeng.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by AMing on 15/11/2.
 * Company RongCloud
 */
public class BaseActivity extends FragmentActivity {

    protected Context context;

    private ViewFlipper mContentView;
    protected RelativeLayout layout_head;
    protected Button btn_left;
    protected Button btn_right;
    protected TextView tv_title;
    private Drawable btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.rp_base_activity);

        // 初始化公共头部
        mContentView = (ViewFlipper) super.findViewById(R.id.layout_container);
        layout_head = (RelativeLayout) super.findViewById(R.id.layout_head);
        btn_left = (Button) super.findViewById(R.id.btn_left);
        btn_right = (Button) super.findViewById(R.id.btn_right);
        tv_title = (TextView) super.findViewById(R.id.tv_title);
        btn_back = getResources().getDrawable(R.drawable.rp_btn_back);
        btn_back.setBounds(0, 0, btn_back.getMinimumWidth(), btn_back.getMinimumHeight());

    }

    @Override
    public void setContentView(View view) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        mContentView.addView(view, lp);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(view);

    }

        @Override
    protected void onResume() {
        super.onResume();
        //注：回调 1
        Bugtags.onResume(this); // bugtags
        MobclickAgent.onResume(this); // umeng

    }

    @Override
    protected void onPause() {
        super.onPause();
        //注：回调 2
        Bugtags.onPause(this);  // bugtags
        MobclickAgent.onPause(this); // uemng
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //注：回调 3
        Bugtags.onDispatchTouchEvent(this, event);
        return super.dispatchTouchEvent(event);
    }

    /**
     * 设置头部是否可见
     *
     * @param visibility
     */
    public void setHeadVisibility(int visibility) {
        layout_head.setVisibility(visibility);
    }

    /**
     * 设置左边是否可见
     *
     * @param visibility
     */
    public void setLeftVisibility(int visibility) {
        btn_left.setVisibility(visibility);
    }

    /**
     * 设置右边是否可见
     *
     * @param visibility
     */
    public void setRightVisibility(int visibility) {
        btn_right.setVisibility(visibility);
    }

    /**
     * 设置标题
     */
    public void setTitle(int titleId) {
        setTitle(getString(titleId), false);
    }

    /**
     * 设置标题
     */
    public void setTitle(int titleId, boolean flag) {
        setTitle(getString(titleId), flag);
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        setTitle(title, false);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title, boolean flag) {
        btn_left.setText(title);
        if (flag) {
            btn_left.setCompoundDrawables(null, null, null, null);
        } else {
            btn_left.setCompoundDrawables(btn_back, null, null, null);
        }
    }

    /**
     * 点击左按钮
     *
     * @param
     */
    public void onLeftClick(View v) {
        finish();
    }

    /**
     * 点击右按钮
     *
     * @param
     */
    public void onRightClick(View v) {

    }

    public Button getBtn_left() {
        return btn_left;
    }

    public void setBtn_left(Button btn_left) {
        this.btn_left = btn_left;
    }

    public Button getBtn_right() {
        return btn_right;
    }

    public void setBtn_right(Button btn_right) {
        this.btn_right = btn_right;
    }

    public Drawable getBtn_back() {
        return btn_back;
    }

    public void setBtn_back(Drawable btn_back) {
        this.btn_back = btn_back;
    }
}
