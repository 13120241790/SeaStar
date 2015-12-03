package com.rongseal.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.rongseal.R;
import com.rongseal.RongCloudEvent;
import com.rongseal.activity.StartDiscussionActivity;
import com.rongseal.activity.UserDetailActivity;
import com.rongseal.adapter.FriendAdapter;
import com.rongseal.bean.Friend;
import com.rongseal.db.com.rongseal.database.DBManager;
import com.rongseal.db.com.rongseal.database.FriendDao;
import com.rongseal.pinyin.CharacterParser;
import com.rongseal.pinyin.PinyinComparator;
import com.rongseal.pinyin.SideBar;
import com.rongseal.widget.ClearWriteEditText;
import com.sd.core.common.broadcast.BroadcastManager;
import com.sd.core.utils.NLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;
import io.rong.imkit.RongIM;

/**
 * Created by AMing on 15/11/2.
 * Company RongCloud
 */
public class FriendsFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    //不带字母的数据集合
    private List<Friend> mSourceDateList = new ArrayList<>();
    //带字母的集合
    private List<Friend> SourceDateList;

    public static FriendsFragment instance = null;
    private FriendDao friendDao;

    public static FriendsFragment getInstance() {
        if (instance == null) {
            instance = new FriendsFragment();
        }
        return instance;
    }

    private View mView;
    /**
     * 自动搜索的 EditText
     */
    private ClearWriteEditText mAboutSerrch;
    /**
     * 好友列表的 ListView
     */
    private ListView mListView;
    /**
     * 好友列表的 adapter
     */
    private FriendAdapter adapter;
    /**
     * 右侧好友指示 Bar
     */
    private SideBar mSidBar;
    /**
     * 中部展示的字母提示
     */
    public TextView dialog;

    private TextView show_no_friends;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.rp_friend_fragment, null);
        refreshUIListener();
        initView();
        return mView;
    }

    private void refreshUIListener() {
        BroadcastManager.getInstance(getActivity()).addAction(RongCloudEvent.REFRESHUI, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getAction();
                if (!TextUtils.isEmpty(command)) {
                    friendDao = DBManager.getInstance(getActivity()).getDaoSession().getFriendDao();
                    QueryBuilder<com.rongseal.db.com.rongseal.database.Friend> qb = friendDao.queryBuilder();
                    if (qb.list().size() > 0 && qb.list() != null) {
                        mSourceDateList.clear();
                        SourceDateList.clear();
                        for (int i = 0; i < qb.list().size(); i++) {
                            com.rongseal.db.com.rongseal.database.Friend bean = qb.list().get(i);
                            mSourceDateList.add(new Friend(bean.getUserId(), bean.getName(), bean.getPortraitUri()));
                        }
                        SourceDateList = filledData(mSourceDateList);
                        Collections.sort(SourceDateList, pinyinComparator);
                        adapter.updateListView(SourceDateList);
                        if (SourceDateList.size() > 0) {
                            show_no_friends.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }


    private void initView() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = PinyinComparator.getInstance();
        mAboutSerrch = (ClearWriteEditText) mView.findViewById(R.id.filter_edit);
        mListView = (ListView) mView.findViewById(R.id.friendlistview);
        mSidBar = (SideBar) mView.findViewById(R.id.sidrbar);
        dialog = (TextView) mView.findViewById(R.id.dialog);
        show_no_friends = (TextView) mView.findViewById(R.id.show_no_friends);
        mSidBar.setTextView(dialog);
        //设置右侧触摸监听
        mSidBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }

            }
        });

        //-------------- 获取好友列表数据 ---------------
        friendDao = DBManager.getInstance(getActivity()).getDaoSession().getFriendDao();
        QueryBuilder<com.rongseal.db.com.rongseal.database.Friend> qb = friendDao.queryBuilder();
        if (qb.list().size() > 0 && qb.list() != null) {
            for (int i = 0; i < qb.list().size(); i++) {
                com.rongseal.db.com.rongseal.database.Friend bean = qb.list().get(i);
                mSourceDateList.add(new Friend(bean.getUserId(), bean.getName(), bean.getPortraitUri()));
            }
        } else {
            //TODO 是否需要网络再次拉取？ login 已经拉取存数据库
            NLog.e("database", "friend database is null");
        }

        SourceDateList = filledData(mSourceDateList); //过滤数据对象为友字母字段
        if (SourceDateList.size() > 0) {
            show_no_friends.setVisibility(View.GONE);
        }
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);

        for (int i = 0 ; i < mSourceDateList.size() ; i++) {
            SourceDateList.get(i).setUserId(mSourceDateList.get(i).getUserId());
            SourceDateList.get(i).setName(mSourceDateList.get(i).getName());
        }

        adapter = new FriendAdapter(getActivity(), SourceDateList);
        mListView.setAdapter(adapter);
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemClickListener(this);
        //根据输入框输入值的改变来过滤搜索  顶部实时搜索
        mAboutSerrch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    /**
     * 为ListView填充数据
     *
     * @param
     * @return
     */
    private List<Friend> filledData(List<Friend> lsit) {
        List<Friend> mFriendList = new ArrayList<Friend>();

        for (int i = 0; i < lsit.size(); i++) {
            Friend friendModel = new Friend();
            friendModel.setName(lsit.get(i).getName());
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(lsit.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                friendModel.setLetters(sortString.toUpperCase());
            } else {
                friendModel.setLetters("#");
            }

            mFriendList.add(friendModel);
        }
        return mFriendList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<Friend> filterDateList = new ArrayList<Friend>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (Friend friendModel : SourceDateList) {
                String name = friendModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(friendModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    public TextView getDialog() {
        return dialog;
    }

    public void setDialog(TextView dialog) {
        this.dialog = dialog;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Intent mIntent = new Intent(getActivity(), StartDiscussionActivity.class);
        mIntent.putExtra("FRIENDDATA", (Serializable) mSourceDateList);
        getActivity().startActivity(mIntent);
        return true;
    }

    /**
     * 开启多人好友聊天
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().startPrivateChat(getActivity(), SourceDateList.get(position).getUserId(), SourceDateList.get(position).getName());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BroadcastManager.getInstance(getActivity()).destroy(RongCloudEvent.REFRESHUI);
    }
}
