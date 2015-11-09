package com.rupeng.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.rupeng.R;
import com.rupeng.bean.Friend;
import com.rupeng.pinyin.CharacterParser;
import com.rupeng.pinyin.PinyinComparator;
import com.rupeng.pinyin.SideBar;
import com.rupeng.widget.ClearWriteEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by AMing on 15/11/4.
 * Company RongCloud
 */
public class StartDiscussionActivity extends BaseActivity implements View.OnClickListener {

    /** 确定开启讨论组聊天 */
    private Button mRightBtn, mLeftBtn;
    /** 自动搜索的 EditText */
    private ClearWriteEditText mAboutSerrch;
    /**好友列表的 ListView*/
    private ListView mListView;
    /**发起讨论组的 adapter*/
    private StartDiscussionAdapter adapter;
    /** 右侧好友指示 Bar */
    private SideBar mSidBar;
    /** 中部展示的字母提示 */
    public TextView dialog;

    private TextView show_no_friends;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /** 数据源 由好友的 Intent 传来 */
    private List<Friend> SourceDateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SourceDateList = (List<Friend>) getIntent().getSerializableExtra("FRIENDDATA");
        setContentView(R.layout.rp_start_discussion_activity);
        initViews();
    }

    private void initViews() {
        mLeftBtn = getBtn_left();
        mLeftBtn.setText("发起讨论组");

        mRightBtn = getBtn_right();
        mRightBtn.setVisibility(View.VISIBLE);
        mRightBtn.setText("确定");
        mRightBtn.setOnClickListener(this);

        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = PinyinComparator.getInstance();


        //自动搜索
        mAboutSerrch = (ClearWriteEditText) this.findViewById(R.id.dis_filter_edit);
        //好友列表
        mListView = (ListView) this.findViewById(R.id.dis_friendlistview);
        //右侧好友指示 bar
        mSidBar = (SideBar) this.findViewById(R.id.dis_sidrbar);
        //中间提示的字母
        dialog = (TextView) this.findViewById(R.id.dis_dialog);
        show_no_friends = (TextView) this.findViewById(R.id.dis_show_no_friends);
        mSidBar.setTextView(dialog);

        //设置右侧触摸监听
        mSidBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    mListView.setSelection(position);
                }

            }
        });

//        SourceDateList = new ArrayList<>(); //测试无好友
        if (SourceDateList.size() == 0) {
            //背景提示没有好友
            show_no_friends.setVisibility(View.VISIBLE);
        }
        if(SourceDateList != null)
        SourceDateList = filledData(SourceDateList);

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new StartDiscussionAdapter(StartDiscussionActivity.this, SourceDateList);
        mListView.setAdapter(adapter);

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
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr){
        List<Friend> filterDateList = new ArrayList<Friend>();

        if(TextUtils.isEmpty(filterStr)){
            filterDateList = SourceDateList;
        }else{
            filterDateList.clear();
            for(Friend friendModel : SourceDateList){
                String name = friendModel.getName();
                if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
                    filterDateList.add(friendModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }


    /**
     * 为ListView填充数据
     * @param
     * @return
     */
    private List<Friend> filledData(List<Friend> lsit){
        List<Friend> mFriendList = new ArrayList<Friend>();

        for(int i=0; i<lsit.size(); i++){
            Friend friendModel = new Friend();
            friendModel.setName(lsit.get(i).getName());
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(lsit.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                friendModel.setLetters(sortString.toUpperCase());
            }else{
                friendModel.setLetters("#");
            }

            mFriendList.add(friendModel);
        }
        return mFriendList;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_right:
//                Toast.makeText(StartDiscussionActivity.this,"发起讨论组聊天",Toast.LENGTH_SHORT).show();
                if (mCBFlag != null && adapterList != null && adapterList.size() >0 ) {
//                    List<Friend> startDisList = new ArrayList<>();
//                    for (int i = 0 ; i < adapterList.size() ; i++ ) {
//                        if (mCBFlag.get(i)) {
//                            startDisList.add(adapterList.get(i));
//                        }
//                    }
                    List<String> idList = new ArrayList<>();
//                    List<String> nameList = new ArrayList<>();
                    //TODO 此处数据没有 userid 应该从 好友Fragment 开启讨论组选择页面 查询源头数据的 userid 什么时候丢失
//                    for (Friend fd : startDisList) {
//                        Log.e("讨论组数据",startDisList.size()+fd.getUserId());
//                        idList.add(fd.getUserId());
//                    }
                    // 模拟数据有 userid
                    idList.add("10010");
                    idList.add("1");
                    idList.add("2");
                    if (RongIM.getInstance() != null && idList.size() > 0) {
                        RongIM.getInstance().getRongIMClient().createDiscussion("讨论组聊天", idList, new RongIMClient.CreateDiscussionCallback() {
                            @Override
                            public void onSuccess(String s) {
                                RongIM.getInstance().startDiscussionChat(StartDiscussionActivity.this,s,"我的讨论组");
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                            }
                        });
                    }
                }else {
                    Toast.makeText(StartDiscussionActivity.this,"无数据",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //用于存储CheckBox选中状态
    public Map<Integer,Boolean> mCBFlag = null;

    public List<Friend> adapterList;

    class StartDiscussionAdapter extends BaseAdapter implements SectionIndexer{

        private Context context;

        public StartDiscussionAdapter(Context context , List<Friend> list) {
            this.context = context;
            adapterList = list;
            mCBFlag = new HashMap<Integer, Boolean>();
            init();

        }

        void init(){
            for(int i = 0 ; i < adapterList.size() ; i++){
                mCBFlag.put(i,false);
            }
        }

        /** 传入新的数据 刷新UI的方法 */
        public void updateListView(List<Friend> list){
            adapterList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return adapterList.size();
        }

        @Override
        public Object getItem(int position) {
            return adapterList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            final Friend mContent = adapterList.get(position);
            if (convertView== null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.start_discussion_item, null);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.dis_friendname);
                viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.dis_catalog);
                viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.dis_frienduri);
                viewHolder.isSelect = (CheckBox) convertView.findViewById(R.id.dis_select);

                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);
            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                viewHolder.tvLetter.setVisibility(View.VISIBLE);
                viewHolder.tvLetter.setText(mContent.getLetters());
            }else {
                viewHolder.tvLetter.setVisibility(View.GONE);
            }

            viewHolder.isSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mCBFlag.put(position,true);
                    }else{
                        mCBFlag.put(position,false);
                    }
                }
            });

            viewHolder.tvTitle.setText(adapterList.get(position).getName());
            //TODO 此处头像代码未设置
            Picasso.with(context)
                    .load(adapterList.get(position).getUri())
                    .placeholder(R.drawable.rc_default_portrait)
                    .centerCrop()
                    .into(viewHolder.mImageView);

            viewHolder.isSelect.setChecked(mCBFlag.get(position));
            return convertView;
        }




        @Override
        public Object[] getSections() {
            return new Object[0];
        }

        /**
         * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
         */
        @Override
        public int getPositionForSection(int sectionIndex) {
            for (int i = 0; i < getCount(); i++) {
                String sortStr = adapterList.get(i).getLetters();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == sectionIndex) {
                    return i;
                }
            }

            return -1;
        }

        /**
         * 根据ListView的当前位置获取分类的首字母的Char ascii值
         */
        @Override
        public int getSectionForPosition(int position) {
            return adapterList.get(position).getLetters().charAt(0);
        }


        final class ViewHolder{
            /** 首字母 */
            TextView tvLetter;
            /** 昵称 */
            TextView tvTitle;
            /** 头像 */
            ImageView mImageView;
            /** userid */
            TextView tvUserId;
            /** 是否被选中的checkbox */
            CheckBox isSelect;
        }

        /**
         * 提取英文的首字母，非英文字母用#代替。unused
         *
         * @param str
         * @return
         */
        private String getAlpha(String str) {
            String  sortStr = str.trim().substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortStr.matches("[A-Z]")) {
                return sortStr;
            } else {
                return "#";
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListView = null;
        adapter = null;
    }
}
