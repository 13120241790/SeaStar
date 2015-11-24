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
import com.rongseal.widget.dialog.LoadDialog;
import com.rongseal.widget.pulltorefresh.PullToRefreshBase;
import com.rongseal.widget.pulltorefresh.PullToRefreshListView;
import com.sd.core.network.http.HttpException;

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
        setTitle("云上所有的群组");
        setContentView(R.layout.sr_allgroup_activity);
        request(SEARCHALLGROUP);
        initViews();
    }

    private void initViews() {
        mListView = (PullToRefreshListView) findViewById(R.id.allgrouplistview);
    }

    @Override
    public Object doInBackground(int requestCode) throws HttpException {
        switch (requestCode) {
            case SEARCHALLGROUP:
                LoadDialog.show(mContext, "正在云端查询...");
                return action.getAllGroupList();
            case ADDGROUP:
//                return action.JoinGroup();
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

                    }
                }

                break;
            case ADDGROUP:
                break;
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case SEARCHALLGROUP:
                break;
            case ADDGROUP:
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {

    }

    class SearchGroupAdapter extends BaseAdapter {

        private AllGroupActivity mActivity;

        public SearchGroupAdapter(Context context,AllGroupActivity activity) {
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
            holder.mGroupNumber.setText(bean.getNumber()+"/500");
            holder.mGroupTime.setText("群组创建时间:"+bean.getCreat_datetime());
            holder.mGroupDescribe.setText(bean.getIntroduce());
            holder.mAddGroup.setTag(bean);
            holder.mAddGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetAllGroupListResponse.ResultEntity bean = (GetAllGroupListResponse.ResultEntity) v.getTag();

//                    LoadDialog.show(mContext,"正在加入...");
//                    request(ADDGROUP);
                }
            });
            return convertView;
        }
    }

    class ViewHolder {
        TextView mGroupName, mGroupId, mGroupNumber, mGroupTime, mGroupDescribe, mShowAdded;
        ImageView mGroupHead;
        Button mAddGroup;
    }

    public void addGroup(GetAllGroupListResponse.ResultEntity bean){

    }
}
