package com.rongseal.activity;

import android.os.*;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.rongseal.R;
import com.rongseal.adapter.GroupMemberAdapter;
import com.rongseal.bean.response.GetGroupInfoResponse;
import com.rongseal.bean.response.QuitGroupResponse;
import com.rongseal.db.com.rongseal.database.DBManager;
import com.rongseal.db.com.rongseal.database.Group;
import com.rongseal.utlis.DialogWithYesOrNoUtils;
import com.rongseal.widget.dialog.LoadDialog;
import com.rongseal.widget.pulltorefresh.PullToRefreshBase;
import com.rongseal.widget.pulltorefresh.PullToRefreshGridView;
import com.sd.core.network.http.HttpException;
import com.sd.core.utils.NToast;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * Created by AMing on 15/11/24.
 * Company RongCloud
 */
public class GroupAetailsActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private static final int GETGROUPINFO = 3987;
    private static final int DELETEGROUP = 3965;
    private String groupId;

    private PullToRefreshGridView pullGridView;
    private GroupMemberAdapter adapter;
    private Button mStartChat, mDeleteGroup;
    private TextView mGroupName, mGroupId, mGroupTime, mGroupCreator, mGroupNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.group_aetails);
        setContentView(R.layout.group_aetails_activity);
        groupId = getIntent().getStringExtra("groupId");
        LoadDialog.show(mContext);
        request(GETGROUPINFO);
        initView();
    }

    private void initView() {
        adapter = new GroupMemberAdapter(mContext);
        mStartChat = (Button) findViewById(R.id.start_group_chat);
        mDeleteGroup = (Button) findViewById(R.id.delete_group);
        mStartChat.setOnClickListener(this);
        mDeleteGroup.setOnClickListener(this);
        mGroupNumber = (TextView) findViewById(R.id.group_number);
        mGroupCreator = (TextView) findViewById(R.id.details_creator);
        mGroupTime = (TextView) findViewById(R.id.details_time);
        mGroupId = (TextView) findViewById(R.id.details_id);
        mGroupName = (TextView) findViewById(R.id.details_name);
        pullGridView = (PullToRefreshGridView) findViewById(R.id.group_member);
        pullGridView.setEmptyView(LayoutInflater.from(mContext).inflate(R.layout.layout_empty, null));
        pullGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        pullGridView.setOnRefreshListener(this);
        pullGridView.setOnItemClickListener(this);
        pullGridView.setAdapter(adapter);
    }

    @Override
    public Object doInBackground(int requestCode) throws HttpException {
        switch (requestCode) {
            case GETGROUPINFO:
                return action.getGroupInfo(groupId);
            case DELETEGROUP:
                return action.quitGroup(groupId);
        }
        return super.doInBackground(requestCode);
    }

    GetGroupInfoResponse.ResultEntity re;

    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode) {
            case GETGROUPINFO:
                if (result != null) {
                    GetGroupInfoResponse res = (GetGroupInfoResponse) result;
                    if (res.getCode() == 200) {
                        re = res.getResult();
                        mGroupName.setText(getResources().getString(R.string.group_name) + re.getName());
                        mGroupNumber.setText(getResources().getString(R.string.group_number)+"(" + re.getNumber() + "):");
                        mGroupCreator.setText(getResources().getString(R.string.creator)+"(id):" + re.getCreate_user_id());
                        mGroupTime.setText(getResources().getString(R.string.create_time) + re.getCreat_datetime());
                        mGroupId.setText(getResources().getString(R.string.group)+"id:" + re.getId());
                        List<GetGroupInfoResponse.ResultEntity.UsersEntity> groupMemberList = res.getResult().getUsers();
                        adapter.setDataSet(groupMemberList);
                        adapter.notifyDataSetChanged();
                        pullGridView.onRefreshComplete();
                        LoadDialog.dismiss(mContext);
                    }
                }
                break;
            case DELETEGROUP:
                if (result != null) {
                    QuitGroupResponse res = (QuitGroupResponse) result;
                    if (res.getCode() == 200) {
                        DBManager.getInstance(mContext).getDaoSession().getGroupDao().delete(new Group(groupId));
                        if (RongIM.getInstance() != null) {
                            RongIM.getInstance().getRongIMClient().clearMessages(Conversation.ConversationType.GROUP, groupId);
                            RongIM.getInstance().getRongIMClient().removeConversation(Conversation.ConversationType.GROUP, groupId);
                        }
                        EventBus.getDefault().post(new FinishActivity());
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, R.string.quit_success);
                        finish();
                    }
                }
                break;
        }

    }

    class FinishActivity {
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case GETGROUPINFO:
                pullGridView.onRefreshComplete();
                LoadDialog.dismiss(mContext);
                break;
        }

    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        request(GETGROUPINFO);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_group_chat:
                EventBus.getDefault().post(new FinishActivity());
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startGroupChat(mContext, groupId, re.getName());
                    finish();
                }
                break;
            case R.id.delete_group:
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, getResources().getString(R.string.quit_and_delete_group), new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void exectEvent() {
                        LoadDialog.show(mContext);
                        request(DELETEGROUP);
                    }
                });
                break;
        }
    }
}
