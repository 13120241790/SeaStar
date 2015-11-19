package com.rongseal.fragment;

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
import com.rongseal.activity.StartDiscussionActivity;
import com.rongseal.adapter.FriendAdapter;
import com.rongseal.bean.Friend;
import com.rongseal.db.com.rongseal.database.DBManager;
import com.rongseal.db.com.rongseal.database.FriendDao;
import com.rongseal.pinyin.CharacterParser;
import com.rongseal.pinyin.PinyinComparator;
import com.rongseal.pinyin.SideBar;
import com.rongseal.widget.ClearWriteEditText;
import com.sd.core.utils.NLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by AMing on 15/11/2.
 * Company RongCloud
 */
public class FriendsFragment extends Fragment implements AdapterView.OnItemLongClickListener {

    /**
     * ----------------------------------模拟好友数据-------------------------------------
     */
    static List<Friend> mSourceDateList = new ArrayList<>();
//
//    static{
//        mSourceDateList.add(new Friend("刘德华","0"));
//        mSourceDateList.add(new Friend("周杰伦","1"));
//        mSourceDateList.add(new Friend("王力宏","2"));
//        mSourceDateList.add(new Friend("赵云","3"));
//        mSourceDateList.add(new Friend("我是你想不到的无关痛痒","4"));
//        mSourceDateList.add(new Friend("Coder","5"));
//        mSourceDateList.add(new Friend("虚竹","6"));
//        mSourceDateList.add(new Friend("江南","7"));
//        mSourceDateList.add(new Friend("诸葛亮","8"));
//        mSourceDateList.add(new Friend("刘备-玄德","9"));
//        mSourceDateList.add(new Friend("关羽-云长","10"));
//        mSourceDateList.add(new Friend("司马懿-仲达","11"));
//        mSourceDateList.add(new Friend("周瑜-公瑾","12"));
//        mSourceDateList.add(new Friend("asdgsdhf","13"));
//        mSourceDateList.add(new Friend("!%%^$^&","14"));
//        mSourceDateList.add(new Friend("曹操-孟德","15"));
//        mSourceDateList.add(new Friend("对酒当歌","16"));
//        mSourceDateList.add(new Friend("人生几何","17"));
//        mSourceDateList.add(new Friend("十步杀一人","18"));
//        mSourceDateList.add(new Friend("千里不留行","19"));
//        mSourceDateList.add(new Friend("事了拂衣去","20"));
//        mSourceDateList.add(new Friend("深藏功与名","21"));
//        mSourceDateList.add(new Friend("先天下之忧而忧","22"));
//        mSourceDateList.add(new Friend("后天下之乐而乐","23"));
//        mSourceDateList.add(new Friend("HELLO ANDROID","24"));
//        mSourceDateList.add(new Friend("Cordova","25"));
//
//
//
//    }

    /**
     * ----------------------------------模拟好友数据-------------------------------------
     */

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
    private List<Friend> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.rp_friend_fragment, null);
        initView();
        return mView;
    }

    private void initView() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = PinyinComparator.getInstance();

        //自动搜索
        mAboutSerrch = (ClearWriteEditText) mView.findViewById(R.id.filter_edit);
        //好友列表
        mListView = (ListView) mView.findViewById(R.id.friendlistview);
        //右侧好友指示 bar
        mSidBar = (SideBar) mView.findViewById(R.id.sidrbar);
        //中间提示的字母
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
        QueryBuilder<com.rongseal.db.Friend> qb = friendDao.queryBuilder();
        if (qb.list().size() > 0 && qb.list() != null) {
            for (int i = 0; i < qb.list().size(); i++) {
                com.rongseal.db.Friend bean = qb.list().get(i);
                mSourceDateList.add(new Friend(bean.getUserId(), bean.getName(), bean.getPortraitUri()));
            }
        } else {
            //TODO 是否需要网络再次拉取？ login 已经拉取存数据库
            NLog.e("database", "friend database is null");
        }

        SourceDateList = filledData(mSourceDateList); //过滤数据对象为友字母字段
        if (SourceDateList.size() == 0) {
            //背景提示没有好友
            show_no_friends.setVisibility(View.VISIBLE);
        }
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new FriendAdapter(getActivity(), SourceDateList);
        mListView.setAdapter(adapter);
        mListView.setOnItemLongClickListener(this);


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
        //TODO 发起多人聊天选择页面  此处传递模拟数据
        Intent mIntent = new Intent(getActivity(), StartDiscussionActivity.class);
        mIntent.putExtra("FRIENDDATA", (Serializable) SourceDateList);
        getActivity().startActivity(mIntent);
        return false;
    }
}
