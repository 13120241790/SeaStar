package com.rongseal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rongseal.bean.response.GetMyGroupResponse;
import com.rongseal.db.com.rongseal.database.DBManager;
import com.rongseal.db.com.rongseal.database.Group;
import com.rongseal.fragment.FriendsFragment;
import com.rongseal.fragment.SealFragment;
import com.rongseal.fragment.MineFragment;
import com.rongseal.activity.BaseActivity;
import com.sd.core.network.http.HttpException;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private static final int SYNCGROUP = 209;
    private ViewPager mViewPager;

    private FragmentPagerAdapter mFragmentPagerAdapter; //将 tab  页面持久在内存中

    private Fragment mConversationList;

    public FriendsFragment mFriendFragment;

    private Fragment mConversationFragment = null;

    private List<Fragment> mFragment = new ArrayList<>();

    private TextView mRuPengView, mConversationListView, mContactView, mSettingView , mUnreadText;

    private LinearLayout LRuPeng, LContact, LSetting,LConversationList;

    private ImageView mImageView , mUnreadImg , mSealIcon , mMessageIcon ,mFriends ,mMy;
    //屏幕的1/4 , 记录当前页码数
    private int mScreen1_4, mCurrentPageIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setHeadVisibility(View.GONE);
        setContentView(R.layout.rp_main_activity);
//        EventBus.getDefault().register(this);
        initGroupDB();
        mConversationList = initConversationList();
        initTabLine();
        initView(mConversationList);
    }

    private void initGroupDB() {
//        QueryBuilder qb = DBManager.getInstance(mContext).getDaoSession().getGroupDao().queryBuilder();
//        if (qb.list().size() != 0)) {
//        }
        request(SYNCGROUP);
    }

    @Override
    public Object doInBackground(int requestCode) throws HttpException {
        switch (requestCode) {
            case SYNCGROUP:
                return action.getMyGruop();
        }
        return super.doInBackground(requestCode);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode) {
            case SYNCGROUP:
                if (result != null) {
                    GetMyGroupResponse res = (GetMyGroupResponse) result;
                    if (res.getCode() == 200) {
                        DBManager.getInstance(mContext).getDaoSession().getGroupDao().deleteAll();
                       List<GetMyGroupResponse.ResultEntity> lsit =  res.getResult();
                        for (GetMyGroupResponse.ResultEntity re :  lsit) {
                            DBManager.getInstance(mContext).getDaoSession().getGroupDao().insertOrReplace(new Group(
                                    re.getId(),
                                    re.getName(),
                                    null,null,null,null,null
                            ));
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }

    private void initView(Fragment mConversationList) {
        //颜色变化
        mRuPengView = (TextView) this.findViewById(R.id.tv_rupeng);
        mConversationListView = (TextView) this.findViewById(R.id.tv_message);
        mContactView = (TextView) this.findViewById(R.id.tv_friend);
        mSettingView = (TextView) this.findViewById(R.id.tv_setting);
        LRuPeng = (LinearLayout) findViewById(R.id.ll_rupeng);
        LConversationList = (LinearLayout) findViewById(R.id.ll_chat);
        LContact = (LinearLayout) findViewById(R.id.ll_friend);
        LSetting = (LinearLayout) findViewById(R.id.ll_setting);
//        mUnreadText = (TextView) findViewById(R.id.main_unread_count);
//        mUnreadImg = (ImageView) findViewById(R.id.main_unread_cion);
        mSealIcon = (ImageView) findViewById(R.id.main_seal);
        mSealIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_rong_abnormal));
        mMessageIcon = (ImageView) findViewById(R.id.main_message);
        mFriends = (ImageView) findViewById(R.id.main_friends);
        mMy = (ImageView) findViewById(R.id.main_my);
        LRuPeng.setOnClickListener(this);
        LConversationList.setOnClickListener(this);
        LContact.setOnClickListener(this);
        LSetting.setOnClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.rc_viewpager);
        mFriendFragment = FriendsFragment.getInstance();
        mFragment.add(SealFragment.getInstance());
        mFragment.add(mConversationList);
        if (mFriendFragment != null) {
            mFragment.add(mFriendFragment);
        }
        mFragment.add(MineFragment.getInstance());
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }
        };
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setOnPageChangeListener(this);
    }


    /**
     * 初始化指示器
     */
    private void initTabLine() {
        mImageView = (ImageView) findViewById(R.id.iv_tabline);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mScreen1_4 = metrics.widthPixels / 4; // 获取屏幕宽度像素的1/4;
        ViewGroup.LayoutParams layoutParams = mImageView.getLayoutParams();
        layoutParams.width = mScreen1_4;
        mImageView.setLayoutParams(layoutParams);
    }


    private Fragment initConversationList() {
        if (mConversationFragment == null) {
            ConversationListFragment listFragment = ConversationListFragment.getInstance();
            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationlist")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false") //设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false") //设置私聊会话是否聚合显示
                    .build();
            listFragment.setUri(uri);
            return listFragment;
        } else {
            return mConversationFragment;
        }
    }


    @Override
    public void onPageSelected(int position) {
        changeTextViewColor();
        switch (position) {
            case 0:
                mRuPengView.setTextColor(Color.parseColor("#3498DB"));
                mSealIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_rong_abnormal));
                break;
            case 1:
                mConversationListView.setTextColor(Color
                        .parseColor("#3498DB"));
                mMessageIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_message_abnormal));
                break;
            case 2:
                mContactView.setTextColor(Color
                        .parseColor("#3498DB"));
                mFriends.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_friends_abnormal));
                break;
            case 3:
                mSettingView.setTextColor(Color
                        .parseColor("#3498DB"));
                mMy.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_my_abnormal));
                break;
        }
        mCurrentPageIndex = position;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // 父控件为 LinearLayout 才有leftMargin 属性 这里需要强转的原因
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mImageView
                .getLayoutParams();
        if (mCurrentPageIndex == 0 && position == 0)// 0->1
        {
            lp.leftMargin = (int) (positionOffset * mScreen1_4 + mCurrentPageIndex
                    * mScreen1_4);
        } else if (mCurrentPageIndex == 1 && position == 0)// 1->0
        {
            lp.leftMargin = (int) (mCurrentPageIndex
                    * mScreen1_4 + (positionOffset - 1)
                    * mScreen1_4);
        } else if (mCurrentPageIndex == 1 && position == 1) // 1->2
        {
            lp.leftMargin = (int) (mCurrentPageIndex
                    * mScreen1_4 + positionOffset * mScreen1_4);
        } else if (mCurrentPageIndex == 2 && position == 1) // 2->1
        {
            lp.leftMargin = (int) (mCurrentPageIndex
                    * mScreen1_4 + (positionOffset - 1)
                    * mScreen1_4);
        } else if (mCurrentPageIndex == 2 && position == 2) // 2->3
        {
            lp.leftMargin = (int) (mCurrentPageIndex
                    * mScreen1_4 + positionOffset * mScreen1_4);
        } else if (mCurrentPageIndex == 3 && position == 2) // 3->2
        {
            lp.leftMargin = (int) (mCurrentPageIndex
                    * mScreen1_4 + (positionOffset - 1)
                    * mScreen1_4);
        }

        mImageView.setLayoutParams(lp);
    }


    private void changeTextViewColor() {
        mSealIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_rong_normal));
        mMessageIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_message_normal));
        mFriends.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_friends_normal));
        mMy.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_my_normal));
        mRuPengView.setTextColor(Color.GRAY);
        mConversationListView.setTextColor(Color.GRAY);
        mContactView.setTextColor(Color.GRAY);
        mSettingView.setTextColor(Color.GRAY);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mFriendFragment.getDialog().setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_rupeng:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.ll_chat:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.ll_friend:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.ll_setting:
                mViewPager.setCurrentItem(3);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {


            final AlertDialog.Builder alterDialog = new AlertDialog.Builder(this);
            alterDialog.setMessage("确定退出应用？");
            alterDialog.setCancelable(true);

            alterDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (RongIM.getInstance() != null)
                        RongIM.getInstance().disconnect(true);

                    try {
                        Thread.sleep(500);
                        android.os.Process.killProcess(Process.myPid());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            alterDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alterDialog.show();
        }

        return false;
    }

//    public void onEventMainThread(Integer i) {
//        if (i == 0) {
//            mUnreadImg.setVisibility(View.GONE);
//            mUnreadText.setVisibility(View.GONE);
//        }else {
//            mUnreadImg.setVisibility(View.VISIBLE);
//            mUnreadText.setText(i);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
