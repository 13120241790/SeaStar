package com.rongseal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.rongseal.R;
import com.rongseal.adapter.ViewPagerAdapter;

import java.util.ArrayList;

/**
 * Created by AMing on 15/11/12.
 * Company RongCloud
 * 指引页面
 */
public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private ViewPager guidePages;
    private ArrayList<View> pageViews;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeadVisibility(View.GONE);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        boolean fristlogin = sp.getBoolean("fristlogin", true);
        boolean isExeced = sp.getBoolean("isExeced", true);

        if (fristlogin && isExeced) {
            setContentView(R.layout.layout_guide);
            LayoutInflater inflater = getLayoutInflater();
            pageViews = new ArrayList<View>();
            pageViews.add(inflater.inflate(R.layout.layout_guide_item1, null));
            pageViews.add(inflater.inflate(R.layout.layout_guide_item2, null));
            pageViews.add(inflater.inflate(R.layout.layout_guide_item3, null));
            pageViews.add(inflater.inflate(R.layout.layout_guide_item4, null));

            guidePages = (ViewPager) findViewById(R.id.guidePages);
            guidePages.setAdapter(new ViewPagerAdapter(pageViews));
            guidePages.setOnPageChangeListener(this);
        } else {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == (pageViews.size() - 1)) {
            View itemView = pageViews.get(position);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    finish();

                    SharedPreferences.Editor edit = sp.edit();
                    edit.putBoolean("isExeced", false);
                    edit.commit();
                }
            });
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
