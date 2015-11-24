package com.rongseal.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rongseal.R;
import com.rongseal.adapter.BaseAdapter;
import com.rongseal.db.com.rongseal.database.DBManager;
import com.rongseal.db.com.rongseal.database.Group;
import com.rongseal.widget.pulltorefresh.PullToRefreshBase;
import com.rongseal.widget.pulltorefresh.PullToRefreshListView;

import java.util.List;

/**
 * Created by AMing on 15/11/23.
 * Company RongCloud
 */
public class MyGroupActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {

    private PullToRefreshListView mListView;

    private MyGroupAdapter mAdapter;

    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("我的群组");
        setContentView(R.layout.mygroup_activity);
        initView();
    }

    private void initView() {
        mListView = (PullToRefreshListView) findViewById(R.id.mygrouplistview);
        mTextView = (TextView) findViewById(R.id.nomygroup);
        List<Group> groupList = DBManager.getInstance(mContext).getDaoSession().getGroupDao().queryBuilder().list();
        if (groupList != null && groupList.size() > 0) {
            mAdapter = new MyGroupAdapter(mContext, groupList);
            mListView.setAdapter(mAdapter);
            mTextView.setVisibility(View.GONE);
        } else {
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {

    }


    class MyGroupAdapter extends BaseAdapter {

        private ViewHolder holder;

        List<Group> list;

        public MyGroupAdapter(Context context, List<Group> data) {
            super(context, data);
            list = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.mygroup_item, null);
                holder.mGroupHead = (ImageView) convertView.findViewById(R.id.my_group_head);
                holder.mGroupName = (TextView) convertView.findViewById(R.id.my_group_name);
                holder.mGroupStart = (Button) convertView.findViewById(R.id.start_group);
                holder.mGroupId = (TextView) convertView.findViewById(R.id.my_group_id);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Group bean = list.get(position);
            holder.mGroupName.setText(bean.getName());
            holder.mGroupId.setText(bean.getGroupId());
            holder.mGroupStart.setTag(bean.getGroupId());
            holder.mGroupStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String groupId = (String) v.getTag();
                    Intent intent = new Intent(MyGroupActivity.this, GroupAetailsActivity.class);
                    intent.putExtra("groupId", groupId);
                    startActivity(intent);
                }
            });
            return convertView;
        }

        class ViewHolder {
            ImageView mGroupHead;
            TextView mGroupName;
            TextView mGroupId;
            Button mGroupStart;
        }
    }

}
