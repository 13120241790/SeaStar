package com.rongseal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rongseal.activity.BaseActivity;
import com.rongseal.bean.response.GetMyGroupResponse;
import com.rongseal.db.com.rongseal.database.DBManager;
import com.rongseal.db.com.rongseal.database.Group;
import com.rongseal.fragment.FriendsFragment;
import com.rongseal.fragment.MineFragment;
import com.rongseal.fragment.SealFragment;
import com.rongseal.fragment.SettingActivity;
import com.rongseal.utlis.DialogWithYesOrNoUtils;
import com.rongseal.widget.DragPointView;
import com.sd.core.common.broadcast.BroadcastManager;
import com.sd.core.network.http.HttpException;
import com.sd.core.utils.NToast;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, DragPointView.OnDragListencer, View.OnLongClickListener {

    private static final int SYNCGROUP = 209;
    private ViewPager mViewPager;

    private FragmentPagerAdapter mFragmentPagerAdapter; //将 tab  页面持久在内存中

    private Fragment mConversationList;

    public FriendsFragment mFriendFragment;

    private Fragment mConversationFragment = null;

    private List<Fragment> mFragment = new ArrayList<>();

    private TextView mRuPengView, mConversationListView, mContactView, mSettingView;

    private DragPointView mUnreadCount;

    private LinearLayout LRuPeng, LContact, LSetting;

    private RelativeLayout LConversationList;

    private ImageView mImageView, mSealIcon, mMessageIcon, mFriends, mMy , onlineService;
    //屏幕的1/4 , 记录当前页码数
    private int mScreen1_4, mCurrentPageIndex;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setHeadVisibility(View.GONE);
        setContentView(R.layout.rp_main_activity);
        initGroupDB();
        mConversationList = initConversationList();
        initTabLine();
        initUnreadCountListener();
        initView(mConversationList);
        initConfig();
        initBroadListener();
    }



    private void initGroupDB() {
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
                        List<GetMyGroupResponse.ResultEntity> lsit = res.getResult();
                        for (GetMyGroupResponse.ResultEntity re : lsit) {
                            DBManager.getInstance(mContext).getDaoSession().getGroupDao().insertOrReplace(new Group(
                                    re.getId(),
                                    re.getName(),
                                    null, null, null, null, null
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
        LConversationList = (RelativeLayout) findViewById(R.id.ll_chat);
        mUnreadCount = (DragPointView) findViewById(R.id.ss_unreadcount);
        LContact = (LinearLayout) findViewById(R.id.ll_friend);
        LSetting = (LinearLayout) findViewById(R.id.ll_setting);
        mSealIcon = (ImageView) findViewById(R.id.main_seal);
        mSealIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_rong_abnormal));
        mMessageIcon = (ImageView) findViewById(R.id.main_message);
        mFriends = (ImageView) findViewById(R.id.main_friends);
        onlineService = (ImageView) findViewById(R.id.online_service);
        onlineService.setOnClickListener(this);
        mMy = (ImageView) findViewById(R.id.main_my);
        LRuPeng.setOnClickListener(this);
        LConversationList.setOnClickListener(this);
        LConversationList.setOnLongClickListener(this);
        LContact.setOnClickListener(this);
        LSetting.setOnClickListener(this);
        mUnreadCount.setOnClickListener(this);
        mUnreadCount.setDragListencer(this);

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
            case R.id.online_service:
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startConversation(this, Conversation.ConversationType.APP_PUBLIC_SERVICE, "KEFU144542424649464", "在线客服");
                }
                break;

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            if (isReceiverPush) {
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "确定退出应用?", new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void exectEvent() {
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
            }else {
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "确定退出应用(not receiver push)?", new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void exectEvent() {
                        if (RongIM.getInstance() != null)
                            RongIM.getInstance().logout();
                        try {
                            Thread.sleep(500);
                            android.os.Process.killProcess(Process.myPid());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


        }

        return false;
    }


    private void initUnreadCountListener() {
        final Conversation.ConversationType[] conversationTypes = {Conversation.ConversationType.PRIVATE, Conversation.ConversationType.DISCUSSION,
                Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.PUBLIC_SERVICE};

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RongIM.getInstance().setOnReceiveUnreadCountChangedListener(mCountListener, conversationTypes);
            }
        }, 500);
    }

    public RongIM.OnReceiveUnreadCountChangedListener mCountListener = new RongIM.OnReceiveUnreadCountChangedListener() {
        @Override
        public void onMessageIncreased(int count) {
            if (count == 0) {
                mUnreadCount.setVisibility(View.GONE);
            } else if (count > 0 && count < 100) {
                mUnreadCount.setVisibility(View.VISIBLE);
                mUnreadCount.setText(count + "");
            } else {
                mUnreadCount.setVisibility(View.VISIBLE);
                mUnreadCount.setText("···");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        BroadcastManager.getInstance(mContext).destroy(SettingActivity.START);
        BroadcastManager.getInstance(mContext).destroy(SettingActivity.CLOSE);
    }

    @Override
    public void onDragOut() {
        if (RongIM.getInstance() != null) {
            List<Conversation> conversationList = RongIM.getInstance().getRongIMClient().getConversationList();
            if (conversationList != null && conversationList.size() > 0) {
                for (Conversation c : conversationList) {
                    RongIM.getInstance().getRongIMClient().clearMessagesUnreadStatus(c.getConversationType(), c.getTargetId());
                }
                mUnreadCount.setVisibility(View.GONE);
                NToast.shortToast(mContext, "清除成功");
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "是否清空会话列表?", new DialogWithYesOrNoUtils.DialogCallBack() {
            @Override
            public void exectEvent() {
                if (RongIM.getInstance() != null) {
                    List<Conversation> conversationList = RongIM.getInstance().getRongIMClient().getConversationList();
                    if (conversationList != null && conversationList.size() > 0) {
                        for (Conversation c : conversationList) {
                            RongIM.getInstance().getRongIMClient().removeConversation(c.getConversationType(), c.getTargetId());
                        }
                        mUnreadCount.setVisibility(View.GONE);
                        NToast.shortToast(mContext, "清除成功");
                    }
                }
            }
        });
        return true;
    }


    private void initConfig() {
        sp =getSharedPreferences("config", MODE_PRIVATE);
        boolean temp = sp.getBoolean(SettingActivity.DESKTOP, true);
        boolean pushTemp = sp.getBoolean(SettingActivity.PUSH, true);
        if (temp) {
            onlineService.setVisibility(View.VISIBLE);
        }else {
            onlineService.setVisibility(View.GONE);
        }
        if (pushTemp) {
            isReceiverPush = true;
        }else {
            isReceiverPush = false;
        }
    }

    private boolean isReceiverPush = true;

    private void initBroadListener() {
        BroadcastManager.getInstance(mContext).addAction(SettingActivity.START, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getAction();
                if (!TextUtils.isEmpty(command)) {
                    onlineService.setVisibility(View.VISIBLE);
                }
            }
        });
        BroadcastManager.getInstance(mContext).addAction(SettingActivity.CLOSE, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getAction();
                if (!TextUtils.isEmpty(command)) {
                    onlineService.setVisibility(View.GONE);
                }
            }
        });
        BroadcastManager.getInstance(mContext).addAction(SettingActivity.PUSHSTART, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getAction();
                if (!TextUtils.isEmpty(command)) {
                    isReceiverPush = true;
                }
            }
        });
        BroadcastManager.getInstance(mContext).addAction(SettingActivity.PUSHCLOSE, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getAction();
                if (!TextUtils.isEmpty(command)) {
                    isReceiverPush = false;
                }
            }
        });
    }

}
