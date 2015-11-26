package com.rongseal.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rongseal.App;
import com.rongseal.R;
import com.rongseal.adapter.BaseAdapter;
import com.rongseal.bean.response.GetAllGroupListResponse;
import com.rongseal.bean.response.GetMyGroupResponse;
import com.rongseal.bean.response.JoinGroupResponse;
import com.rongseal.db.com.rongseal.database.DBManager;
import com.rongseal.db.com.rongseal.database.Group;
import com.rongseal.widget.dialog.LoadDialog;
import com.rongseal.widget.pulltorefresh.PullToRefreshBase;
import com.rongseal.widget.pulltorefresh.PullToRefreshListView;
import com.sd.core.network.http.HttpException;
import com.sd.core.utils.NToast;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by AMing on 15/11/23.
 * Company RongCloud
 */
public class AllGroupActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {

    private static final int SEARCHALLGROUP = 1567;
    private static final int ADDGROUP = 1568;

    private PullToRefreshListView mListView;

    private SearchGroupAdapter mAdapter;

    private TextView mTextView;

    private ViewHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("云上的群组");
        setContentView(R.layout.sr_allgroup_activity);
        LoadDialog.show(mContext, "正在云端查询...");
        request(SEARCHALLGROUP);
        initViews();
    }

    private void initViews() {
        mListView = (PullToRefreshListView) findViewById(R.id.allgrouplistview);
        mTextView = (TextView) findViewById(R.id.nogroup);
        mAdapter = new SearchGroupAdapter(mContext, this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public Object doInBackground(int requestCode) throws HttpException {
        switch (requestCode) {
            case SEARCHALLGROUP:
                return action.getAllGroupList();
            case ADDGROUP:
                return action.JoinGroup(groupBean.getId());
        }
        return super.doInBackground(requestCode);
    }


    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode) {
            case SEARCHALLGROUP:
                if (result != null) {
                    GetAllGroupListResponse res = (GetAllGroupListResponse) result;
                    if (res.getCode() == 200) {
                        mAdapter.removeAll();
                        mListView.setVisibility(View.VISIBLE);
                        mTextView.setVisibility(View.GONE);
                        mAdapter.addData(res.getResult());
                        mAdapter.notifyDataSetChanged();
                        LoadDialog.dismiss(mContext);
                        mListView.onRefreshComplete();
                    }
                }
                break;
            case ADDGROUP:
                if (result != null) {
                    JoinGroupResponse res = (JoinGroupResponse) result;
                    if (res.getCode() == 200) {
                        DBManager.getInstance(mContext).getDaoSession().getGroupDao().insertOrReplace(new Group(groupBean.getId(), groupBean.getName(),
                                null, null, null, null, null
                        ));
                        request(SEARCHALLGROUP);
                        NToast.shortToast(mContext, "加入成功");
                        LoadDialog.dismiss(mContext);
                    } else if (res.getCode() == 202) {
                        LoadDialog.dismiss(mContext);
                        mListView.onRefreshComplete();
                        NToast.shortToast(mContext, "该群人数已经达到上限");
                    }
                }
                break;
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case SEARCHALLGROUP:
                LoadDialog.dismiss(mContext);
                mListView.onRefreshComplete();
                break;
            case ADDGROUP:

                LoadDialog.dismiss(mContext);
                mListView.onRefreshComplete();
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {

    }

    class SearchGroupAdapter extends BaseAdapter {

        private AllGroupActivity mActivity;


        public SearchGroupAdapter(Context context, AllGroupActivity activity) {
            super(context);
            mActivity = activity;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.sr_allgroup_item, null);
                holder.mGroupHead = (ImageView) convertView.findViewById(R.id.search_group_head);
                holder.mGroupName = (TextView) convertView.findViewById(R.id.group_name);
                holder.mGroupId = (TextView) convertView.findViewById(R.id.group_id);
                holder.mGroupNumber = (TextView) convertView.findViewById(R.id.group_number);
                holder.mGroupTime = (TextView) convertView.findViewById(R.id.group_create_time);
                holder.mGroupDescribe = (TextView) convertView.findViewById(R.id.group_describe);
                holder.mShowAdded = (TextView) convertView.findViewById(R.id.added_group);
                holder.mAddGroup = (Button) convertView.findViewById(R.id.add_group);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            GetAllGroupListResponse.ResultEntity bean = (GetAllGroupListResponse.ResultEntity) dataSet.get(position);
            if (TextUtils.isEmpty(bean.getPortrait())) {
                ImageLoader.getInstance().displayImage(bean.getPortrait(), holder.mGroupHead, App.getOptions());
            }
            holder.mGroupName.setText(bean.getName());
            holder.mGroupId.setText(bean.getId());
            holder.mGroupNumber.setText(bean.getNumber() + "/500");
            holder.mGroupTime.setText("创建时间:" + bean.getCreat_datetime());
            holder.mGroupDescribe.setText(bean.getIntroduce());


            QueryBuilder qb = DBManager.getInstance(mContext).getDaoSession().getGroupDao().queryBuilder();
            List<Group> list = qb.list();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getGroupId().contains(bean.getId())) {
                        holder.mAddGroup.setVisibility(View.GONE);
                        holder.mShowAdded.setVisibility(View.VISIBLE);
                        holder.mShowAdded.setText("已在该群");
                    } else {
                        if (Integer.parseInt(bean.getNumber()) < 500) {
                            holder.mAddGroup.setTag(bean);
                            holder.mAddGroup.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    GetAllGroupListResponse.ResultEntity bean = (GetAllGroupListResponse.ResultEntity) v.getTag();
                                    mActivity.addGroup(bean);

                                }
                            });
                        } else {
                            holder.mAddGroup.setVisibility(View.GONE);
                            holder.mShowAdded.setVisibility(View.VISIBLE);
                            holder.mShowAdded.setText("该群已满");
                        }
                    }
                }
            } else {
                if (Integer.parseInt(bean.getNumber()) < 500) {
                    holder.mAddGroup.setTag(bean);
                    holder.mAddGroup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GetAllGroupListResponse.ResultEntity bean = (GetAllGroupListResponse.ResultEntity) v.getTag();
                            mActivity.addGroup(bean);

                        }
                    });
                } else {
                    holder.mAddGroup.setVisibility(View.GONE);
                    holder.mShowAdded.setVisibility(View.VISIBLE);
                    holder.mShowAdded.setText("该群已满");
                }
            }
            return convertView;
        }
    }

    class ViewHolder {
        TextView mGroupName, mGroupId, mGroupNumber, mGroupTime, mGroupDescribe, mShowAdded;
        ImageView mGroupHead;
        Button mAddGroup;
    }

    GetAllGroupListResponse.ResultEntity groupBean;

    public void addGroup(GetAllGroupListResponse.ResultEntity groupBean) {
        this.groupBean = groupBean;
        LoadDialog.show(mContext, "正在请求...");
        request(ADDGROUP);
    }

}
